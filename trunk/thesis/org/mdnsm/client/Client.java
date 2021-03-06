package org.mdnsm.client;

import org.mdnsm.mdns.*;
import org.mdnsm.server.*;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.net.*;

/**
 * TODO: beschrijving
 * TODO: netjes groeperen, benoemen van en waarde geven aan final variabelen
 * 
 * @author	Frederic Cremer
 */
public class Client {
	
	private Vector ips;
	
	// JmDNS instances associated with this client
	private Hashtable jmdnss;
	// Server instances associated with this client
	private Hashtable servers;
	// Server cache used by server instances to share accumulated data
	private DNSCache serverCache;
	
	private Timer timer;
	private NICMonitor nicMonitor;
	
	// Listener for usable server information
	private ServerListener serverListener;
	// Server checker for one-IP machines
	private ServerChecker serverChecker;
	// Server cleaner for one-IP machines
	private ServerCleaner serverCleaner;
	
	// Server daemons associated with this clients server instances
	private Hashtable serverDaemons;
	// List of other servers on the network
	private SSCache ssCache;
	
	// List of servers this client can contact to get information
	// (should not be used when this client has server instances running)
	private DNSCache reachableServers;
	
	// Listeners for information
	private Hashtable infoListeners;
	
	// The sockets for client communication
	private Hashtable sockets;
	// The socket listeners for client communication
	private Hashtable socketListeners;
	
	public Client() throws IOException {
		initData();
		nicMonitor = new NICMonitor();
		nicMonitor.start();
	}
	
	/**
	 * Initialize this client's data structures.
	 */
	private void initData() {
		ips = new Vector();
		jmdnss = new Hashtable();
		servers = new Hashtable();
		serverCache = new DNSCache(100);
		timer = new Timer();
		ssCache = new SSCache();
		serverDaemons = new Hashtable();
		reachableServers = new DNSCache(10);
		serverListener = new ServerListener();
		serverChecker = new ServerChecker();
		serverCleaner = new ServerCleaner();
		infoListeners = new Hashtable();
		sockets = new Hashtable();
		socketListeners = new Hashtable();
	}
	
	/**
	 * Register a service running on this computer.
	 */
	public void registerService(ServiceInfo info) {
		try {
			for(Iterator i = jmdnss.values().iterator(); i.hasNext();) {
				((JmDNS)i.next()).registerService(info, true);
			}
		}
		catch(IOException exc) {
			exc.printStackTrace();
		}
	}
	
	/**
	 * Unregister a service running on this computer.
	 * If the current primary IP has changed from the IP with which the service
	 * is registered, this has no result.  This does no harm, as the JmDNS instance
	 * registering the service has been stopped, unregistering all of its services.
	 */
	public void unregisterService(ServiceInfo info) {
		try {
			for(Iterator i = jmdnss.values().iterator(); i.hasNext();) {
				if(info.getType().indexOf("*") >= 0) {
					((JmDNS)i.next()).unregisterService(info, true);
				}
				else {
					((JmDNS)i.next()).unregisterService(info);
				}
			}
		}
		catch(IOException exc) {
			exc.printStackTrace();
		}
	}
	
	public DNSCache getServerCache() {
		return serverCache;
	}
	
	public SSCache getSSCache() {
		return ssCache;
	}
	
	/**
	 * Check whether this client has server instances running.
	 */
	public boolean hasServers() {
		return servers.size() > 0;
	}
	
	/*
	 * IP monitoring
	 */
	
	/**
	 * Inner class monitoring the status of the network interface cards of this
	 * client.  A new NIC or NIC configuration should result in the creation of
	 * new JmDNS and ServiceServer instances for the associated network address.
	 * On the contrary, when a NIC configuration disappears or changes, the cor-
	 * responding JmDNS and ServiceServer instances should be changed or removed
	 * accordingly.
	 * 
	 * @author	Frederic Cremer
	 */
	private class NICMonitor extends TimerTask {
		
		private final long MONITOR_INTERVAL = 1000;  // TODO: interval correct instellen
		
		public void start() {
			timer.schedule(this, 0, MONITOR_INTERVAL);
		}
		
		public void run() {
			try {
				Vector ips = Utils.getIPs();
				removeServers(ips);
				checkServerNeed(ips);
			}
			catch(IOException exc) {
				System.out.println("Client.NICMonitor.run: I/O exception occurred when determining IPs: " + exc.getMessage());
				exc.printStackTrace();
			}
		}
		
	}
	

	/**
	 * Check whether servers should be halted (that is, only 1 configured NIC remains).
	 */
	private void checkServerNeed(Vector ips) {
		// One IP left, which has a server bound to it
		if(ips.size() == 1 && servers.keys().hasMoreElements()) {
			String key = (String)servers.keys().nextElement();
			// Stop the associated server
			((ServiceServer)servers.get(key)).shutdown();
			// Stop the associated server daemon
			((ServerDaemon)serverDaemons.get(key)).stop();
			// Remove hashtable entries
			servers.remove(key);
			serverDaemons.remove(key);
			// Clear the server cache (redundant, cache should already be empty)
			serverCache.clear();
			// Reactivate the server listener (one server left implies that before multiple server instances
			// were running, and thus the server listener was deactivated)
			((JmDNS)jmdnss.get(key)).addServiceListener("_sserver._udp.*.local.", serverListener);
			serverChecker.start();
			serverCleaner.start();
		}
		// One IP detected, no active JmDNS instances (typically at startup with a single NIC).
		if(ips.size() == 1 && jmdnss.size() == 0) {
			try {
				String ip = (String)ips.get(0);
				jmdnss.put(ip, new JmDNS(ip));
				// Activate server listener and checker
				((JmDNS)jmdnss.get(ip)).addServiceListener("_sserver._udp.*.local.", serverListener);
				serverChecker.start();
				serverCleaner.start();
				DatagramSocket socket = new DatagramSocket(Utils.CLIENT_COM, InetAddress.getByName(ip));
				sockets.put(ip, socket);
				SocketListener listener = new SocketListener(socket, ip);
				socketListeners.put(ip, listener);
				(new Thread(listener)).start();
			}
			catch(IOException exc) {
				System.out.println("Client.NICMonitor.checkServerNeeded: I/O exception occurred when trying to initialize JmDNS instance: " + exc.getMessage());
			}
		}
		
		// Multiple IPs detected
		else if(ips.size() > 1) {
			// Coming from 1 IP, thus meaning activated server listener, implies deactivating the server listener and socket listener
			if(jmdnss.size() == 1) {
				((JmDNS)jmdnss.get((String)jmdnss.keys().nextElement())).removeServiceListener("_sserver._udp.*.local.", serverListener);
				serverChecker.cancel();
				serverCleaner.stop();
				reachableServers.clear();
			}
			Iterator iterator = ips.iterator();
			while(iterator.hasNext()) {
				String ip = (String)iterator.next();
				// IP already existed before without server, start server and server daemon for it
				if(jmdnss.containsKey(ip) && !servers.containsKey(ip)) {
					servers.put(ip, new ServiceServer(this, (JmDNS)jmdnss.get(ip), ip));
					ServiceInfo info = ((ServiceServer)servers.get(ip)).getInfo();
					reachableServers.add(new DNSRecord.Pointer(info.getType(), DNSConstants.TYPE_PTR, DNSConstants.CLASS_IN, DNSConstants.DNS_TTL, info.getQualifiedName()));
					reachableServers.add(new DNSRecord.Service(info.getQualifiedName(), DNSConstants.TYPE_PTR, DNSConstants.CLASS_IN, DNSConstants.DNS_TTL, info.getPriority(), info.getWeight(), info.getPort(), info.getServer()));
					reachableServers.add(new DNSRecord.Text(info.getQualifiedName(), DNSConstants.TYPE_PTR, DNSConstants.CLASS_IN, DNSConstants.DNS_TTL, info.getTextBytes()));
					serverDaemons.put(ip, new ServerDaemon(ip));
					new Thread((ServerDaemon)serverDaemons.get(ip)).start();
				}
				// IP is new, start JmDNS instance, server, server daemon and socket listener for it
				else if(!jmdnss.containsKey(ip)) {
					try {
						jmdnss.put(ip, new JmDNS(ip));
					}
					catch(IOException exc) {
						System.out.println("Client.NICMonitor.checkServerNeeded: I/O exception occurred when trying to initialize JmDNS instance: " + exc.getMessage());
					}
					servers.put(ip, new ServiceServer(this, (JmDNS)jmdnss.get(ip), ip));
					ServiceInfo info = ((ServiceServer)servers.get(ip)).getInfo();
					reachableServers.add(new DNSRecord.Pointer(info.getType(), DNSConstants.TYPE_PTR, DNSConstants.CLASS_IN, DNSConstants.DNS_TTL, info.getQualifiedName()));
					reachableServers.add(new DNSRecord.Service(info.getQualifiedName(), DNSConstants.TYPE_SRV, DNSConstants.CLASS_IN, DNSConstants.DNS_TTL, info.getPriority(), info.getWeight(), info.getPort(), info.getServer()));
					reachableServers.add(new DNSRecord.Text(info.getQualifiedName(), DNSConstants.TYPE_TXT, DNSConstants.CLASS_IN, DNSConstants.DNS_TTL, info.getTextBytes()));
					serverDaemons.put(ip, new ServerDaemon(ip));
					new Thread((ServerDaemon)serverDaemons.get(ip)).start();
					try {
						DatagramSocket socket = new DatagramSocket(Utils.CLIENT_COM, InetAddress.getByName(ip));
						sockets.put(ip, socket);
						SocketListener listener = new SocketListener(socket, ip);
						socketListeners.put(ip, listener);
						(new Thread(listener)).start();
					}
					catch(SocketException exc) {
						exc.printStackTrace();
					}
					catch(UnknownHostException exc) {
						exc.printStackTrace();
					}
				}
			}
		}
	}
	
	/**
	 * Remove obsolete servers and JmDNS instances.
	 * Non-existing IP means:
	 * 		- removing the server from the list of reachable servers
	 * 		- shutting down the server instance
	 * 		- shutting down the associated server daemon
	 * 		- removing associated socket and socket listener
	 * 		- shutting down the JmDNS instance
	 * 		- clearing the associated data structures
	 */
	private void removeServers(Vector ips) {
		Enumeration existingServers = servers.keys();
		while(existingServers.hasMoreElements()) {
			String key = (String)existingServers.nextElement();
			if(!ips.contains(key)) {
				((JmDNS)jmdnss.get(key)).rebind((String)ips.get(0));
				ServiceInfo info = ((ServiceServer)servers.get(key)).getInfo();
				DNSEntry entry = reachableServers.get(new DNSRecord.Pointer(info.getType(), DNSConstants.TYPE_PTR, DNSConstants.CLASS_IN, 0, info.getQualifiedName()));
				reachableServers.remove(entry);
				entry = reachableServers.get(new DNSRecord.Service(info.getQualifiedName(), DNSConstants.TYPE_SRV, DNSConstants.CLASS_IN, 0, info.getPriority(), info.getWeight(), info.getPort(), info.getServer()));
				reachableServers.remove(entry);
				entry = reachableServers.get(new DNSRecord.Text(info.getQualifiedName(), DNSConstants.TYPE_TXT, DNSConstants.CLASS_IN, 0, info.getTextBytes()));
				reachableServers.remove(entry);
				((ServiceServer)servers.get(key)).shutdown();
				((ServerDaemon)serverDaemons.get(key)).stop();
				((SocketListener)socketListeners.get(key)).stop();
				((DatagramSocket)sockets.get(key)).close();
				((JmDNS)jmdnss.get(key)).close();
				servers.remove(key);
				serverDaemons.remove(key);
				socketListeners.remove(key);
				sockets.remove(key);
				jmdnss.remove(key);
			}
		}
	}

	/*
	 * Listening to usable servers
	 */
	
	/**
	 * Inner class implementing the ServiceListener interface to handle
	 * the listening to server types.
	 * 
	 * @author	Frederic Cremer
	 */
	public class ServerListener implements ServiceListener {
		
		/**
		 * New server found, add to reachable server list.
		 */
		public void serviceAdded(ServiceEvent event) {
			ServiceInfo info = new ServiceInfo(event.getType(), event.getName());
			removeServer(info);
			System.out.println("server added: " + info.getQualifiedName());
			reachableServers.add(new DNSRecord.Pointer(info.getType(), DNSConstants.TYPE_PTR, DNSConstants.CLASS_IN, Utils.SERVER_TTL, info.getQualifiedName()));
			((JmDNS)jmdnss.values().iterator().next()).requestServiceInfo(event.getType(), event.getName());
		}
		
		/**
		 * Server has been stopped, remove from reachable server list.
		 */
		public void serviceRemoved(ServiceEvent event) {
			System.out.println("server removed");
			ServiceInfo info = new ServiceInfo(event.getType(), event.getName());
			removeServer(info);
		}
		
		/**
		 * Server info has been updated, update reachable server list.
		 */
		public void serviceResolved(ServiceEvent event) {
			ServiceInfo info = event.getInfo();
			removeServer(info);
			System.out.println("server modified: " + info.getQualifiedName());
			reachableServers.add(new DNSRecord.Pointer(info.getType(), DNSConstants.TYPE_PTR, DNSConstants.CLASS_IN, Utils.SERVER_TTL, info.getQualifiedName()));
			reachableServers.add(new DNSRecord.Service(info.getQualifiedName(), DNSConstants.TYPE_SRV, DNSConstants.CLASS_IN, Utils.SERVER_TTL, info.getPriority(), info.getWeight(), info.getPort(), info.getServer()));
			reachableServers.add(new DNSRecord.Text(info.getQualifiedName(), DNSConstants.TYPE_TXT, DNSConstants.CLASS_IN, Utils.SERVER_TTL, info.getTextBytes()));
		}
		
		/**
		 * Remove the given server information from the list of reachable servers.
		 */
		private void removeServer(ServiceInfo info) {
			DNSEntry entry = new DNSRecord.Pointer(info.getType(), DNSConstants.TYPE_PTR, DNSConstants.CLASS_IN, Utils.SERVER_TTL, info.getQualifiedName());
			reachableServers.remove(entry);
			if(info.hasData()) {
				entry = new DNSRecord.Service(info.getQualifiedName(), DNSConstants.TYPE_SRV, DNSConstants.CLASS_IN, Utils.SERVER_TTL, info.getPriority(), info.getWeight(), info.getPort(), info.getServer());
				reachableServers.remove(entry);
				entry = new DNSRecord.Text(info.getQualifiedName(), DNSConstants.TYPE_TXT, DNSConstants.CLASS_IN, Utils.SERVER_TTL, info.getTextBytes());
				reachableServers.remove(entry);
			}
		}
		
		/**
		 * Update the time-to-live of the corresponding record in the list of reachable
		 * servers.
		 */
		public void updateTTLs(DNSRecord rec) {
			for (DNSCache.CacheNode n = (DNSCache.CacheNode) reachableServers.find(rec.getName()); n != null; n = n.next()) {
				 DNSEntry entry = n.getValue();
				 ((DNSRecord)entry).resetTTL(rec);
			}
		}
		
	}
	
	/**
	 * Timertask polling the network for available servers.
	 * Only used in the case of one IP on the local machine.
	 * 
	 * @author	Frederic Cremer
	 */
	private class ServerChecker extends TimerTask {
		
		public void start() {
			timer.schedule(this, Utils.SERVER_CHECK_INTERVAL, Utils.SERVER_CHECK_INTERVAL);
		}
		
		public void run() {
			JmDNS jmdns = (JmDNS)jmdnss.values().iterator().next();
			jmdns.requestServices("_sserver._udp.*.local.");
		}
		
	}
	
	/**
	 * Timertask cleaning up the list of available servers.
	 * Only used in the case of one IP on the local machine.
	 * 
	 * @author	Frederic Cremer
	 *
	 */
	private class ServerCleaner extends TimerTask {
		
		private boolean needed = true;
		
		public void start() {
			timer.schedule(this, Utils.SERVER_CLEAN_INTERVAL, Utils.SERVER_CLEAN_INTERVAL);
		}
		
		public void run() {
			JmDNS jmdns = (JmDNS)jmdnss.values().iterator().next();
			
			// Copy existing entries into copy list
			// (for concurrency support)
			// (code based on JmDNS.RecordReaper code)
			List list = new ArrayList();
            synchronized (reachableServers) {
                for (Iterator i = reachableServers.iterator(); i.hasNext();) {
                    for (DNSCache.CacheNode n = (DNSCache.CacheNode) i.next(); n != null; n = n.next()) {
                        list.add(n.getValue());
                    }
                }
            }
            // Now removing expired records
            long now = System.currentTimeMillis();
            for (Iterator i = list.iterator(); i.hasNext();) {
                DNSRecord c = (DNSRecord) i.next();
                if (c.isExpired(now)) {
                    reachableServers.remove(c);
                }
            }
			if(!needed) {
				cancel();
			}
		}
		
		public void stop() {
			needed = false;
		}
		
	}
	
	/*
	 * Spreading and storing (local) server information
	 */
	
	/**
	 * Daemon class taking care of propagation of the associated server on
	 * other subnets and accumulating information on servers on other subnets.
	 * 
	 * @author	Frederic Cremer
	 *
	 */
	public class ServerDaemon implements Runnable {
		
		private String ip;
		private MulticastSocket sdSocket;
		private boolean RUNNING;
		
		/**
		 * Initialize a new server daemon.
		 */
		public ServerDaemon(String ip) {
			this.ip = ip;
			try {
				sdSocket = new MulticastSocket(Utils.DAEMON_PORT);
				sdSocket.joinGroup(InetAddress.getByName(Utils.SERVER_MULTICAST_GROUP));
				sdSocket.setNetworkInterface(NetworkInterface.getByInetAddress(InetAddress.getByName(ip)));
			}
			catch(IOException exc) {
				System.out.println("Client.ServerDaemon.ServerDaemon: I/O exception occured when trying to initialize multicast sockets.");
				exc.printStackTrace();
			}
		}
		
		/**
		 * Get the IP of the server this daemon runs for.
		 */
		public String getIP() {
			return ip;
		}
		
		/**
		 * Run this server daemon.
		 */
		public void run() {
			RUNNING = true;
			ServerAnnouncer announcer = new ServerAnnouncer();
			timer.schedule(announcer, 0, Utils.SERVER_ANN_INTERVAL);
			SSCacheCleaner cleaner = new SSCacheCleaner();
			timer.schedule(cleaner, 0, Utils.SERVER_CLEAN_INTERVAL);
			while(RUNNING) {
				try {
					DatagramPacket packet = new DatagramPacket(new byte[1000], 1000);
					sdSocket.receive(packet);
					ssCache.addServer(getRRFromPacket(packet));
					route(packet);
				}
				catch(SocketException exc) {
					// Sockets are closed, catch the failing of the receive and send methods
				}
				catch(UnknownHostException exc) {
					exc.printStackTrace();
				}
				catch(IOException exc) {
					exc.printStackTrace();
				}
			}
			announcer.cancel();
			cleaner.cancel();
		}
		
		/**
		 * Stop this server daemon.
		 */
		public void stop() {
			RUNNING = false;
			sdSocket.close();
		}
		
		/**
		 * Timertask handling announcing the associated server.
		 * 
		 * @author	Frederic Cremer
		 */
		private class ServerAnnouncer extends TimerTask {
			
			public void run() {
				try {
					DatagramPacket packet = constructPacket(getIP(), getSubnet(getIP()));
					packet.setAddress(InetAddress.getByName(Utils.SERVER_MULTICAST_GROUP));
					packet.setPort(Utils.DAEMON_PORT);
					sdSocket.send(packet);
					route(packet);
				}
				catch(IOException exc) {
					exc.printStackTrace();
				}
			}
			
		}
		
		/**
		 * Timertask handling the periodic cleaning of the service server cache.
		 * 
		 * @author	Frederic Cremer
		 */
		private class SSCacheCleaner extends TimerTask {
			
			public void run() {
				ssCache.clean();
			}
			
		}
		
		/**
		 * Route the given packet to other local servers.
		 */
		private void route(DatagramPacket packet) {
			Enumeration e = serverDaemons.elements();
			while(e.hasMoreElements()) {
				ServerDaemon s = (ServerDaemon)e.nextElement();
				if(!s.getIP().equals(getIP())) {
					s.routeExternal(packet);
				}
			}
		}
		
		/**
		 * Route the given packet over the network device associated with this server daemon,
		 * if appropriate.
		 */
		public void routeExternal(DatagramPacket packet) {
			ssCache.addServer(getRRFromPacket(packet));
			String subnet = getSubnet(getIP());
			Vector subnets = getVisitedSubnets(getVisitedFromPacket(packet));
			if(!subnets.contains(subnet)) {
				String newSubnets = getVisitedFromPacket(packet) + "," + subnet;
				try {
					DatagramPacket sdPacket = constructPacket(getRRFromPacket(packet).getDomain(), newSubnets);
					sdPacket.setAddress(InetAddress.getByName(Utils.SERVER_MULTICAST_GROUP));
					sdPacket.setPort(Utils.DAEMON_PORT);
					sdSocket.send(sdPacket);
				}
				catch(IOException exc) {
					exc.printStackTrace();
				}
			}
		}
		
		/**
		 * Get a vector of subnet strings from one comma-separated list of IPs.
		 */
		private Vector getVisitedSubnets(String subnets) {
			Vector result = new Vector();
			StringTokenizer tok = new StringTokenizer(subnets, ",");
			while (tok.hasMoreTokens()) {
				String token = tok.nextToken();
				result.add(token);
			}
			return result;
		}
		
		/**
		 * Return the subnet from the given IP (first three parts).
		 */
		private String getSubnet(String ip) {
			StringTokenizer tok = new StringTokenizer(ip, ".");
			return tok.nextToken() + "." + tok.nextToken() + "." + tok.nextToken();
		}
		
		/**
		 * Construct a datagram packet with a resource record of the given server,
		 * along with a list of already visited servers (comma-separated list).
		 */
		private DatagramPacket constructPacket(String ip, String visited) {
			// Get the numbers required to calculate the byte array length
			int ipLength = ip.length();
			int visitedLength = visited.length();
			byte[] rr = (new ResourceRecord(ip, Utils.NS, 1, Utils.SERVER_RR_TTL, new byte[0])).getRR();
			byte[] vb = visited.getBytes();
			// Construct the byte array
			byte[] bytes = new byte[ipLength+visitedLength+rr.length+vb.length];
			// Fill the byte array
			bytes[0] = Utils.getByte(ipLength, 2);
			bytes[1] = Utils.getByte(ipLength, 1);
			bytes[2] = Utils.getByte(visitedLength, 2);
			bytes[3] = Utils.getByte(visitedLength, 1);
			System.arraycopy(rr, 0, bytes, 4, rr.length);
			System.arraycopy(vb, 0, bytes, 4+rr.length, vb.length);
			// Create datagram packet with the byte array
			DatagramPacket result = new DatagramPacket(bytes, bytes.length);
			return result;
		}
		
		/**
		 * Get the resource record from an incoming datagram packet.
		 */
		private ResourceRecord getRRFromPacket(DatagramPacket packet) {
			// Get the data from the packet
			byte[] bytes = packet.getData();
			// Get the IP from the packet
			int ipLength = Utils.addThem(bytes[0], bytes[1]);
			byte[] ipBytes = new byte[ipLength];
			System.arraycopy(bytes, 4, ipBytes, 0, ipLength);
			String newIP = new String(ipBytes);
			// Construct resource record and return it
			return new ResourceRecord(newIP, Utils.NS, 1, Utils.SERVER_RR_TTL, null);
		}
		
		/**
		 * Get the string of already visited subnets from an incoming datagram packet.
		 */
		private String getVisitedFromPacket(DatagramPacket packet) {
			//Get the data from the packet
			byte[] bytes = packet.getData();
			// Get the string of visited IPs from the packet
			int ipLength = Utils.addThem(bytes[0], bytes[1]);
			int visitedLength = Utils.addThem(bytes[2], bytes[3]);
			byte[] visitedBytes = new byte[visitedLength];
			System.arraycopy(bytes, 4+ipLength+10, visitedBytes, 0, visitedLength);
			return new String(visitedBytes);
		}
	
	}
	
	/*
	 * Communication with servers
	 */
	
	/**
	 * Add a service listener listening for services of the given
	 * type.
	 * 
	 * @param	General type, e.g. "_daap._tcp.*.local."
	 * @param	Actual service listener, e.g. GetItTogether instance
	 */
	public void addServiceListener(String type, ServiceListener listener) {
		type = type.toLowerCase();
		if(infoListeners.containsKey(type)) {
			Vector listeners = (Vector)infoListeners.get(type);
			listeners.add(listener);
		}
		else {
			Vector vector = new Vector();
			vector.add(listener);
			infoListeners.put(type, vector);
		}
	}
	
	/**
	 * Request information about the given type and feed the information to the
	 * given listener.
	 */
	public void requestServices(String type) {
		type = type.toLowerCase();
		(new ServiceResolver(type)).start();
	}
	
	/**
	 * Request information about the given service.
	 */
	public void requestServiceInfo(ServiceInfo info) {
		(new ServiceInfoResolver(info)).start();
	}
	
	/**
     * The ServiceResolver queries three times consecutively for services of
     * a given type, and then removes itself from the timer.
     * Based on JmDNS code.
     * 
     * @author	Arthur van Hoff, Rick Blair, Jeff Sonstein, Werner Randelshofer,
     * 			Pierre Frisch, Scott Lewis
     * @author	Frederic Cremer
     */
    private class ServiceResolver extends TimerTask {
    	
        private String type;

        public ServiceResolver(String type) {
            this.type = type;
        }
        
        public void start() {
            timer.schedule(this, DNSConstants.QUERY_WAIT_INTERVAL, DNSConstants.QUERY_WAIT_INTERVAL);
        }

        public void run() {
            try {
            	long now = System.currentTimeMillis();
            	DNSOutgoing out = new DNSOutgoing(DNSConstants.FLAGS_QR_QUERY);
            	out.addQuestion(new DNSQuestion(type, DNSConstants.TYPE_PTR, DNSConstants.CLASS_IN));
            	// There is only one NIC locally, add known answers
            	// (for we really have to send this message to an external
            	//  server).
            	if(jmdnss.values().size() == 1) {
            		Map services = ((JmDNS)jmdnss.values().iterator().next()).getServices();
            		for (Iterator s = services.values().iterator(); s.hasNext();) {
            			ServiceInfo info = (ServiceInfo) s.next();
            			try {
            				out.addAnswer(new DNSRecord.Pointer(info.getType(), DNSConstants.TYPE_PTR, DNSConstants.CLASS_IN, DNSConstants.DNS_TTL, info.getQualifiedName()), now);
            			}
            			catch (IOException ee) {
            				break;
            			}
            		}
            	}
            	sendToServers(out);
            }
            catch (IOException exc) {
            	exc.printStackTrace();
            }
            cancel();
        }
    }
	
	/**
     * The ServiceInfoResolver queries up to three times consecutively for
     * a service info, and then removes itself from the timer.
     * This code is based on the JmDNS code by Arthur van Hoff, Rick Blair,
     * Jeff Sonstein, Werner Randelshofer, Pierre Frisch and Scott Lewis,
     * adapted (and simplified) for this unicast case.
     * 
     * @author	Arthur van Hoff, Rick Blair, Jeff Sonstein, Werner Randelshofer,
     * 			Pierre Frisch, Scott Lewis
     * @author	Frederic Cremer
     */
    private class ServiceInfoResolver extends TimerTask {
        
        private ServiceInfo info;

        public ServiceInfoResolver(ServiceInfo info) {
            this.info = info;
        }

        public void start() {
            timer.schedule(this, DNSConstants.QUERY_WAIT_INTERVAL, DNSConstants.QUERY_WAIT_INTERVAL);
        }

        public void run() {
            try {
            	long now = System.currentTimeMillis();
            	DNSOutgoing out = new DNSOutgoing(DNSConstants.FLAGS_QR_QUERY);
            	out.addQuestion(new DNSQuestion(info.getQualifiedName(), DNSConstants.TYPE_SRV, DNSConstants.CLASS_IN));
            	out.addQuestion(new DNSQuestion(info.getQualifiedName(), DNSConstants.TYPE_TXT, DNSConstants.CLASS_IN));
            	send(out, Utils.getIPFromType(info.getQualifiedName()));
            }
            catch(IOException exc) {
            	exc.printStackTrace();
            }
            cancel();
        }
    }
    
    /**
     * Send an outgoing DNS message to the given IP address.
     */
    private void send(DNSOutgoing out, String ip) {
    	try {
    		out.finish();
    		if(!out.isEmpty()) {
    			DatagramPacket packet = new DatagramPacket(out.getData(), out.getOff(), InetAddress.getByName(ip), Utils.CLIENT_COM);
    			DatagramSocket socket = (DatagramSocket)sockets.values().iterator().next();
    			socket.send(packet);
    		}
    	}
    	catch(IOException exc) {
    		exc.printStackTrace();
    	}
    }
    
    /**
     * Send an outgoing unicast DNS message to all reachable servers.
     */
    private void sendToServers(DNSOutgoing out) throws IOException {
        out.finish();
        if (!out.isEmpty()) {
        	Vector serverIPs = getServerIPs();
        	// Any of the available sockets can be used to send a packet.
        	// The safest thing here is to use the first one (may be the only one available).
        	DatagramSocket socket = (DatagramSocket)sockets.values().iterator().next();
        	for(Iterator i = serverIPs.iterator(); i.hasNext();) {
        		String ip = (String)i.next();
        		DatagramPacket packet = new DatagramPacket(out.getData(), out.getOff(), InetAddress.getByName(ip), Utils.SERVER_COM);
        		socket.send(packet);
        	}
        }
    }
    
    /**
     * Get a list of all reachable server IPs.
     */
    private Vector getServerIPs() {
    	Vector result = new Vector();
    	for (Iterator i = reachableServers.iterator(); i.hasNext(); ) {
    		for (DNSCache.CacheNode n = (DNSCache.CacheNode) i.next(); n != null; n = n.next()) {
    			DNSEntry entry = n.getValue();
    			String ip = "";
    			if(entry.getType() == DNSConstants.TYPE_PTR) {
    				ip = Utils.getIPFromType(((DNSRecord.Pointer)entry).getAlias());
    				if(!result.contains(ip)) {
    					result.add(ip);
    				}
    			}
    			if(entry.getType() == DNSConstants.TYPE_SRV || entry.getType() == DNSConstants.TYPE_TXT) {
    				ip = Utils.getIPFromType(entry.getName());
    				if(!result.contains(ip)) {
    					result.add(ip);
    				}
    			}
    		}
    	}
    	return result;
    }
    
    /**
     * Listen for unicast packets.
     * Code based on JmDNS code.
     * 
     * @author	Arthur van Hoff, Rick Blair, Jeff Sonstein, Werner Randelshofer,
     * 			Pierre Frisch, Scott Lewis
     * @author	Frederic Cremer
     */
    class SocketListener implements Runnable {
        
    	private DatagramSocket socket;
    	private String ip;
    	private boolean needed = true;
    	
    	public SocketListener(DatagramSocket socket, String ip) {
    		this.socket = socket;
    		this.ip = ip;
    	}
    	
    	public void run() {
            try {
            	byte buf[] = new byte[DNSConstants.MAX_MSG_ABSOLUTE];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                while (needed) {
                    packet.setLength(buf.length);
                    socket.receive(packet);
                    try {
                    	DNSIncoming msg = new DNSIncoming(packet);
                    	if(msg.isResponse()) {
                    		handleResponse(msg);
                    	}
                    	if(msg.isQuery()) {
                    		String sender = packet.getAddress().getHostAddress();
                    		DNSOutgoing out = new DNSOutgoing(DNSConstants.FLAGS_QR_RESPONSE);
                    		for(Iterator i = msg.getQuestions().iterator(); i.hasNext();) {
                    			DNSQuestion q = (DNSQuestion)i.next();
                    			if(q.getType() == DNSConstants.TYPE_SRV) {
                    				ServiceInfo info = ((JmDNS)jmdnss.get(ip)).getServiceInfo(q.getName().toLowerCase());
                    				out.addAnswer(new DNSRecord.Service(info.getQualifiedName(), DNSConstants.TYPE_SRV, DNSConstants.CLASS_IN, DNSConstants.DNS_TTL, info.getPriority(), info.getWeight(), info.getPort(), info.getServer()), System.currentTimeMillis());
                    			}
                    			else if(q.getType() == DNSConstants.TYPE_TXT) {
                    				ServiceInfo info = ((JmDNS)jmdnss.get(ip)).getServiceInfo(q.getName().toLowerCase());
                    				out.addAnswer(new DNSRecord.Text(info.getQualifiedName(), DNSConstants.TYPE_TXT, DNSConstants.CLASS_IN, DNSConstants.DNS_TTL, info.getTextBytes()), System.currentTimeMillis());
                    			}
                    		}
                    		send(out, sender);
                    	}
                    }
                    catch (IOException exc) {
                        exc.printStackTrace();
                    }
                }
            }
            catch(SocketException exc) {
            	// Associated socket is closed, catch the failing of the receive method
            }
            catch (IOException exc) {
            	exc.printStackTrace();
            }
        }
    	
    	/**
    	 * Stop this socket listener.
    	 */
    	public void stop() {
    		needed = false;
    		
    	}
    	
    }
    	
    /**
     * Handle an incoming response.
     * For pointers to specific types, start requesting service information.
     * For specific service information, pass it on to the associated listeners.
     * Based on JmDNS code.
     */
    private void handleResponse(DNSIncoming msg) throws IOException {
    	long now = System.currentTimeMillis();
    	for (Iterator i = msg.getAnswers().iterator(); i.hasNext();){
    		DNSRecord rec = (DNSRecord) i.next();
    		ServiceInfo info = null;
    		String type = null;
    		switch (rec.getType()) {
    			case DNSConstants.TYPE_PTR:
    				//System.out.println("pointer to " + rec.getName());
    				type = JmDNS.convertToType(rec.getName());
    				info = new ServiceInfo(rec.getName(), JmDNS.toUnqualifiedName(rec.getName(), ((DNSRecord.Pointer)rec).getAlias()));
    				if(rec.ttl == 0) {
    					for(Iterator j = ((Vector)infoListeners.get(type)).iterator(); j.hasNext();) {
	    					ServiceListener l = (ServiceListener)j.next();
	    					l.serviceRemoved(new ServiceEvent((JmDNS)jmdnss.values().iterator().next(), rec.getName(), JmDNS.toUnqualifiedName(rec.getName(), ((DNSRecord.Pointer)rec).getAlias()), null));
	    				}
    				}
    				else {
	    				for(Iterator j = ((Vector)infoListeners.get(type)).iterator(); j.hasNext();) {
	    					ServiceListener l = (ServiceListener)j.next();
	    					l.serviceAdded(new ServiceEvent((JmDNS)jmdnss.values().iterator().next(), rec.getName(), JmDNS.toUnqualifiedName(rec.getName(), ((DNSRecord.Pointer)rec).getAlias()), null));
	    				}
    				}
    				
    				//(new ServiceInfoResolver(info)).start();
    				break;
    			case DNSConstants.TYPE_SRV:
    				type = JmDNS.convertToType(JmDNS.toFullType(rec.getName()));
    				DNSRecord trec = (DNSRecord)i.next();
    				byte[] text = ((DNSRecord.Text)trec).text;
    				info = new ServiceInfo(JmDNS.toFullType(rec.getName()), JmDNS.toUnqualifiedName(JmDNS.toFullType(rec.getName()), rec.getName()), ((DNSRecord.Service)rec).port, ((DNSRecord.Service)rec).weight, ((DNSRecord.Service)rec).priority, new String(text));
    				info.addr = InetAddress.getByName(Utils.getIPFromType(rec.getName()));
    				info.server = ((DNSRecord.Service)rec).server;
    				for(Iterator j = ((Vector)infoListeners.get(type)).iterator(); j.hasNext();) {
    					ServiceListener l = (ServiceListener)j.next();
    					l.serviceResolved(new ServiceEvent((JmDNS)jmdnss.values().iterator().next(), JmDNS.toFullType(rec.getName()), JmDNS.toUnqualifiedName(JmDNS.toFullType(rec.getName()), rec.getName()), info));
    				}
    				break;
    		}
    	}
    }
    
    /**
     * Shut down this client.
     */
    public void shutdown() {
    	nicMonitor.cancel();
    	for(Iterator a = servers.values().iterator(); a.hasNext();) {
    		((ServiceServer)a.next()).shutdown();
    	}
    	servers.clear();
    	if(jmdnss.size() == 1) {
    		JmDNS jmdns = ((JmDNS)jmdnss.get((String)jmdnss.keys().nextElement()));
    		jmdns.removeServiceListener("_sserver._udp.*.local.", serverListener);
    		serverChecker.cancel();
    		serverCleaner.cancel();
    	}
    	for(Iterator b = serverDaemons.values().iterator(); b.hasNext();) {
    		((ServerDaemon)b.next()).stop();
    	}
    	serverDaemons.clear();
    	for(Iterator c = socketListeners.values().iterator(); c.hasNext();) {
    		((SocketListener)c.next()).stop();
    	}
    	socketListeners.clear();
    	for(Iterator d = sockets.values().iterator(); d.hasNext();) {
    		((DatagramSocket)d.next()).close();
    	}
    	sockets.clear();
    	for(Iterator e = jmdnss.values().iterator(); e.hasNext();) {
    		((JmDNS)e.next()).close();
    	}
    	jmdnss.clear();
    	for(Iterator f = infoListeners.values().iterator(); f.hasNext();) {
    		((Vector)f.next()).clear();
    	}
    	infoListeners.clear();
    	
    	serverCache.clear();
    	reachableServers.clear();
    	ssCache.clean();
    	
    	timer.cancel();
    }
		
}
