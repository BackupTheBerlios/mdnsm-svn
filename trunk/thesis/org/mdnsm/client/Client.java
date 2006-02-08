package org.mdnsm.client;

import org.mdnsm.mdns.*;
import org.mdnsm.mdns.JmDNS.Prober;
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
	
	// TODO: correcte waarden
	private final int SERVER_RR_TTL = 360000;
	private final int SERVER_ANN_INTERVAL = 300000;
	private final int SERVER_CLEAN_INTERVAL = 300000;
	
	// Port on which the server daemons communicate
	private final int DAEMON_PORT = 1337;
	// Port on which servers and clients communicate
	private final int SERVER_CLIENT_COMM = 1338;
	
	// JmDNS instances associated with this client
	private Hashtable jmdnss = new Hashtable();
	// Server instances associated with this client
	private Hashtable servers = new Hashtable();
	// Server cache used by server instances to share accumulated data
	private DNSCache serverCache;
	
	private Timer timer;
	
	// Server daemons associated with this clients server instances
	private Hashtable serverDaemons;
	// List of other servers on the network
	private SSCache ssCache;
	
	// Listener for usable server information
	private ServerListener serverListener;
	// List of servers this client can contact to get information
	// (should not be used when this client has server instances running)
	private Vector reachableServers;
	
	// Listeners for information
	private Hashtable infoListeners;
	
	// The socket for client server communication
	private DatagramSocket socket;
	
	private String os;
	
	public Client() throws IOException {
		initData();
		new NICMonitor().start();
	}
	
	/**
	 * Initialize this client's data structures.
	 */
	private void initData() {
		serverCache = new DNSCache(100);
		timer = new Timer();
		ssCache = new SSCache();
		serverDaemons = new Hashtable();
		reachableServers = new Vector();
		serverListener = new ServerListener();
		infoListeners = new Hashtable();
		try {
			socket = new DatagramSocket(SERVER_CLIENT_COMM);
		}
		catch(SocketException exc) {
			exc.printStackTrace();
		}
	}
	
	public DNSCache getServerCache() {
		return serverCache;
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
		
		public NICMonitor() {
			os = System.getProperty("os.name");
		}
		
		public void start() {
			timer.schedule(this, 0, MONITOR_INTERVAL);
		}
		
		public void run() {
			try {
				Vector ips = getIPs();
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
		}
		// One IP detected, no active JmDNS instances (typically at startup with a single NIC).
		if(ips.size() == 1 && jmdnss.size() == 0) {
			try {
				jmdnss.put((String)ips.get(0), new JmDNS((String)ips.get(0)));
				// Activate server listener
				((JmDNS)jmdnss.get((String)ips.get(0))).addServiceListener("_sserver._udp.*.local.", serverListener);
			}
			catch(IOException exc) {
				System.out.println("Client.NICMonitor.checkServerNeeded: I/O exception occurred when trying to initialize JmDNS instance: " + exc.getMessage());
			}
		}
		// Multiple IPs detected
		else if(ips.size() > 1) {
			// Coming from 1 IP, thus meaning activated server listener, implies deactivating the server listener
			if(jmdnss.size() == 1) {
				((JmDNS)jmdnss.get((String)jmdnss.keys().nextElement())).removeServiceListener("_sserver._udp.*.local.", serverListener);
			}
			Iterator iterator = ips.iterator();
			while(iterator.hasNext()) {
				String ip = (String)iterator.next();
				// IP already existed before without server, start server and server daemon for it
				if(jmdnss.containsKey(ip) && !servers.containsKey(ip)) {
					servers.put(ip, new ServiceServer(this, (JmDNS)jmdnss.get(ip), ip));
					serverDaemons.put(ip, new ServerDaemon(ip));
					new Thread((ServiceServer)servers.get(ip)).start();
					new Thread((ServerDaemon)serverDaemons.get(ip)).start();
				}
				// IP is new, start JmDNS instance, server and server daemon for it
				else if(!jmdnss.containsKey(ip)) {
					try {
						jmdnss.put(ip, new JmDNS(ip));
					}
					catch(IOException exc) {
						System.out.println("Client.NICMonitor.checkServerNeeded: I/O exception occurred when trying to initialize JmDNS instance: " + exc.getMessage());
					}
					servers.put(ip, new ServiceServer(this, (JmDNS)jmdnss.get(ip), ip));
					serverDaemons.put(ip, new ServerDaemon(ip));
					new Thread((ServiceServer)servers.get(ip)).start();
					new Thread((ServerDaemon)serverDaemons.get(ip)).start();
				}
			}
		}
	}
	
	/**
	 * Remove obsolete servers and JmDNS instances.
	 */
	private void removeServers(Vector ips) {
		Enumeration existingServers = servers.keys();
		while(existingServers.hasMoreElements()) {
			String key = (String)existingServers.nextElement();
			if(!ips.contains(key)) {
				((ServiceServer)servers.get(key)).shutdown();
				((ServerDaemon)serverDaemons.get(key)).stop();
				((JmDNS)jmdnss.get(key)).close();
				servers.remove(key);
				serverDaemons.remove(key);
				jmdnss.remove(key);
			}
		}
	}
	
	/**
	 * Get the available IPs of this computer.
	 */
	private Vector getIPs() throws IOException {
		if(os.equals("Windows XP")) {
			return getWindowsIPConfiguration();
		}
		else if(os.equals("Linux")){
			return getLinuxIPConfiguration();
		}
		else throw new IllegalArgumentException("Sorry, your OS is not supported by mDNSm.");
	}
	
	/**
	 * Get the available IPs if this computer runs Windows XP.
	 */
	private Vector getWindowsIPConfiguration() throws IOException {
		Vector result = new Vector();
		Process process = Runtime.getRuntime().exec("ipconfig");
		BufferedInputStream is = new BufferedInputStream(process.getInputStream());
		String output = "";
		int c = is.read();
		while(c != -1) {
			if(output.endsWith("IP Address. . . . . . . . . . . . : ")) {
				String ip = "";
				while(Character.isDigit((char)c) || (char)c == '.') {
					ip += (char)c;
					c = is.read();
				}
				if(!ip.equals("127.0.0.1") && !ip.equals("0.0.0.0") && !ip.startsWith("169.")) {
					result.add(ip);
				}
			}
			output += (char) c;
			c = is.read();
		}
		return result;
	}
	
	/**
	 * Get the available IPs if this computer runs a Linux OS.
	 */
	private Vector getLinuxIPConfiguration() throws IOException {
		Vector result = new Vector();
		Process process = Runtime.getRuntime().exec("/sbin/ifconfig");
		BufferedInputStream is = new BufferedInputStream(process.getInputStream());
		String output = "";
		int c = is.read();
		while(c != -1) {
			if(output.endsWith("inet addr:")) {
				String ip = "";
				while(Character.isDigit((char)c) || (char)c == '.') {
					ip += (char)c;
					c = is.read();
				}
				if(!ip.equals("127.0.0.1") && !ip.equals("0.0.0.0") && !ip.startsWith("169.")) {
					result.add(ip);
				}
			}
			output += (char) c;
			c = is.read();
		}
		return result;
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
	private class ServerListener implements ServiceListener {
		
		/**
		 * New server found, add to reachable server list.
		 */
		public void serviceAdded(ServiceEvent event) {
			ServiceInfo info = new ServiceInfo(event.getType(), event.getName());
			removeInfo(info);
			reachableServers.add(info);
		}
		
		/**
		 * Server has been stopped, remove from reachable server list.
		 */
		public void serviceRemoved(ServiceEvent event) {
			ServiceInfo info = new ServiceInfo(event.getType(), event.getName());
			removeInfo(info);
		}
		
		/**
		 * Server info has been updated, update reachable server list.
		 */
		public void serviceResolved(ServiceEvent event) {
			ServiceInfo info = event.getInfo();
			removeInfo(info);
			reachableServers.add(info);
		}
		
		/**
		 * Remove the given server information from the list of reachable servers.
		 */
		private void removeInfo(ServiceInfo info) {
			ServiceInfo TBR = null;
			for(Iterator i = reachableServers.iterator(); i.hasNext();) {
				ServiceInfo server = (ServiceInfo)i.next();
				if(server.getType().equals(info.getType()) && server.getName().equals(info.getName())) {
					TBR = server;
				}
			}
			reachableServers.remove(TBR);
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
		private MulticastSocket sendSocket;
		private DatagramSocket receiveSocket;
		private boolean RUNNING;
		
		/**
		 * Initialize a new server daemon.
		 */
		public ServerDaemon(String ip) {
			this.ip = ip;
			try {
				sendSocket = new MulticastSocket(DAEMON_PORT);
				sendSocket.joinGroup(InetAddress.getByName(DNSConstants.MDNS_GROUP));
				sendSocket.setNetworkInterface(NetworkInterface.getByInetAddress(InetAddress.getByAddress(ip.getBytes())));
				receiveSocket = new DatagramSocket(DAEMON_PORT, InetAddress.getByAddress(ip.getBytes()));
			}
			catch(IOException exc) {
				System.out.println("Client.ServerDaemon.ServerDaemon: I/O exception occured when trying to initialize multicast sockets.");
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
			timer.schedule(announcer, 0, SERVER_ANN_INTERVAL);
			SSCacheCleaner cleaner = new SSCacheCleaner();
			timer.schedule(cleaner, 0, SERVER_CLEAN_INTERVAL);
			while(RUNNING) {
				try {
					DatagramPacket packet = new DatagramPacket(new byte[1000], 1000);
					receiveSocket.receive(packet);
					ssCache.addServer(getRRFromPacket(packet));
					route(packet);
				}
				catch(SocketException exc) {
					exc.printStackTrace();
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
					sendSocket.send(packet);
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
			String subnet = getSubnet(getIP());
			Vector subnets = getVisitedSubnets(getVisitedFromPacket(packet));
			if(!subnets.contains(subnet)) {
				String newSubnets = getVisitedFromPacket(packet) + "," + subnet;
				try {
					sendSocket.send(constructPacket(getRRFromPacket(packet).getDomain(), newSubnets));
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
			byte[] rr = (new ResourceRecord(ip, Utils.NS, 1, SERVER_RR_TTL, new byte[0])).getRR();
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
			return new ResourceRecord(newIP, Utils.NS, 1, SERVER_RR_TTL, null);
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
	 * Request information about the given type and feed the information to the
	 * given listener.
	 */
	public void requestInfo(String type, ServiceListener listener) {
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
		resolveInfo(type);
	}
	
	private void resolveInfo(String type) {
		if(hasServers()) {
			// TODO: lokale servers direct aanspreken
		}
		else {
			(new Thread(new ServiceResolver(type))).start();
		}
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
    	
        int count = 0;
        private String type;

        public ServiceResolver(String type) {
            this.type = type;
        }

        public void start() {
            timer.schedule(this, DNSConstants.QUERY_WAIT_INTERVAL, DNSConstants.QUERY_WAIT_INTERVAL);
        }

        public void run() {
            try {
                    if (count++ < 3) {
                        long now = System.currentTimeMillis();
                        DNSOutgoing out = new DNSOutgoing(DNSConstants.FLAGS_QR_QUERY);
                        out.addQuestion(new DNSQuestion(type, DNSConstants.TYPE_PTR, DNSConstants.CLASS_IN));
                        // This should only be executed when there is only one JmDNS instance running locally
                        // so we should be able to safely add the registered services of that instance as known
                        // answers
                        Map services = (Map)((JmDNS)jmdnss.get((String)jmdnss.keys().nextElement())).getServices();
                        for (Iterator s = services.values().iterator(); s.hasNext();)
                        {
                            final ServiceInfo info = (ServiceInfo) s.next();
                            try
                            {
                                out.addAnswer(new DNSRecord.Pointer(info.getType(), DNSConstants.TYPE_PTR, DNSConstants.CLASS_IN, DNSConstants.DNS_TTL, info.getQualifiedName()), now);
                            }
                            catch (IOException ee)
                            {
                                break;
                            }
                        }
                        send(out);
                    }
                    else {
                        // After three queries, we can quit.
                        cancel();
                    }
            }
            catch (IOException exc) {
            	exc.printStackTrace();
            }
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
        
        int count = 0;
        private ServiceInfo info;

        public ServiceInfoResolver(ServiceInfo info) {
            this.info = info;
        }

        public void start() {
            timer.schedule(this, DNSConstants.QUERY_WAIT_INTERVAL, DNSConstants.QUERY_WAIT_INTERVAL);
        }

        public void run() {
            try {
            	if (count++ < 3) {
            		long now = System.currentTimeMillis();
            		DNSOutgoing out = new DNSOutgoing(DNSConstants.FLAGS_QR_QUERY);
            		out.addQuestion(new DNSQuestion(info.getQualifiedName(), DNSConstants.TYPE_SRV, DNSConstants.CLASS_IN));
            		out.addQuestion(new DNSQuestion(info.getQualifiedName(), DNSConstants.TYPE_TXT, DNSConstants.CLASS_IN));
            		send(out);
            	}
            	else {
            		// After three queries, we can quit.
            		cancel();
            	}
            }
            catch(IOException exc) {
            	exc.printStackTrace();
            }
        }
    }
    
    /**
     * Send an outgoing unicast DNS message to all reachable servers.
     */
    private void send(DNSOutgoing out) throws IOException {
        out.finish();
        if (!out.isEmpty()) {
        	for(Iterator i = reachableServers.iterator(); i.hasNext();) {
        		String ip = (String)i.next();
        		DatagramPacket packet = new DatagramPacket(out.getData(), out.getOff(), InetAddress.getByName(ip), SERVER_CLIENT_COMM);
        		try {
        			DNSIncoming msg = new DNSIncoming(packet);
        		}
        		catch (IOException exc) {
        			exc.printStackTrace();
        		}
        		socket.send(packet);
        	}
        }
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
        
    	private boolean needed = true;
    	
    	public void run() {
            try {
                byte buf[] = new byte[DNSConstants.MAX_MSG_ABSOLUTE];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                while (needed) {
                    packet.setLength(buf.length);
                    socket.receive(packet);
                    try {
                    	DNSIncoming msg = new DNSIncoming(packet);
                    	// We don't really expect queries here
                    	if(msg.isResponse()) {
                    		handleResponse(msg);
                    	}
                    }
                    catch (Exception exc) {
                        exc.printStackTrace();
                    }
                }
            }
            catch (IOException exc) {
            	exc.printStackTrace();
            }
        }
    	
    }
    	
    /**
     * Handle an incoming response.
     * For pointers to specific types, start requesting service information.
     * For specific service information, pass it on to the associated listeners.
     * Based on JmDNS code.
     */
    private void handleResponse(DNSIncoming msg) throws IOException
    {
    	long now = System.currentTimeMillis();
    	
    	boolean hostConflictDetected = false;
    	boolean serviceConflictDetected = false;
    	
    	for (Iterator i = msg.answers.iterator(); i.hasNext();)
    	{
    		boolean isInformative = false;
    		DNSRecord rec = (DNSRecord) i.next();
    		boolean expired = rec.isExpired(now);
    		
    		// update the cache
    		DNSRecord c = (DNSRecord) cache.get(rec);
    		if (c != null)
    		{
    			if (expired)
    			{
    				isInformative = true;
    				cache.remove(c);
    			}
    			else
    			{
    				c.resetTTL(rec);
    				rec = c;
    			}
    		}
    		else
    		{
    			if (!expired)
    			{
    				
    				isInformative = true;
    				cache.add(rec);
    			}
    		}
    		switch (rec.type)
    		{
    		case DNSConstants.TYPE_PTR:
    			// handle _mdns._udp records
    			if (rec.getName().indexOf("._mdns._udp.") >= 0)
    			{
    				if (!expired && rec.name.startsWith("_services._mdns._udp."))
    				{
    					isInformative = true;
    					registerServiceType(((DNSRecord.Pointer) rec).alias);
    				}
    				continue;
    			}
    			registerServiceType(rec.name);
    			break;
    		}
    		
    		if ((rec.getType() == DNSConstants.TYPE_A) || (rec.getType() == DNSConstants.TYPE_AAAA))
    		{
    			hostConflictDetected |= rec.handleResponse(this);
    		}
    		else
    		{
    			serviceConflictDetected |= rec.handleResponse(this);
    		}
    		
    		// notify the listeners
    		if (isInformative)
    		{
    			recordUpdated(now, rec);
    		}
    	}
    	
    	if (hostConflictDetected || serviceConflictDetected)
    	{
    		new Prober().start();
    	}
    }
		
}
