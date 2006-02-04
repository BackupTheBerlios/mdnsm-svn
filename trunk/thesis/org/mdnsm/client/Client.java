package org.mdnsm.client;

import org.mdnsm.mdns.*;
import org.mdnsm.server.*;

import java.io.*;
import java.util.*;

/**
 * Temporary (?) class, which contains the main method starting all
 * of it: creating an mDNS thread, possibly starting a DNS server,
 * passing the mDNS thread on as a parameter to the DNS server.
 * 
 * @author	Frederic Cremer
 */
public class Client {
	
	private Hashtable jmdnss = new Hashtable();
	private Hashtable servers = new Hashtable();
	private DNSCache serverCache;
	
	private Timer timer;
	
	private ServerDaemon daemon;
	
	private String os;
	
	public Client() throws IOException {
		serverCache = new DNSCache(100);
		timer = new Timer();
		daemon = new ServerDaemon();
		// TODO: beginnen luisteren naar servers
		new NICMonitor().start();
		(new Thread(daemon)).start();
	}
	
	public DNSCache getServerCache() {
		return serverCache;
	}
	
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
			((ServiceServer)servers.get(servers.keys().nextElement())).shutdown();
			daemon.removeIP(key);
			servers.remove(key);
			serverCache.clear();
		}
		// One IP detected, no active JmDNS instances (typically at startup with a single NIC).
		if(ips.size() == 1 && jmdnss.size() == 0) {
			try {
				jmdnss.put((String)ips.get(0), new JmDNS((String)ips.get(0)));
			}
			catch(IOException exc) {
				System.out.println("Client.NICMonitor.checkServerNeeded: I/O exception occurred when trying to initialize JmDNS instance: " + exc.getMessage());
			}
		}
		// Multiple IPs detected
		else if(ips.size() > 1) {
			Iterator iterator = ips.iterator();
			while(iterator.hasNext()) {
				String ip = (String)iterator.next();
				if(jmdnss.containsKey(ip) && !servers.containsKey(ip)) {
					servers.put(ip, new ServiceServer(this, (JmDNS)jmdnss.get(ip), ip));
					daemon.addIP(ip);
					new Thread((ServiceServer)servers.get(ip)).start();
				}
				else if(!jmdnss.containsKey(ip)) {
					try {
						jmdnss.put(ip, new JmDNS(ip));
					}
					catch(IOException exc) {
						System.out.println("Client.NICMonitor.checkServerNeeded: I/O exception occurred when trying to initialize JmDNS instance: " + exc.getMessage());
					}
					servers.put(ip, new ServiceServer(this, (JmDNS)jmdnss.get(ip), ip));
					daemon.addIP(ip);
					new Thread((ServiceServer)servers.get(ip)).start();
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
				daemon.removeIP(key);
				((JmDNS)jmdnss.get(key)).close();
				servers.remove(key);
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
		// TODO: exception gooien als besturingssysteem niet herkend wordt
		else return null;
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
	
	/**
	 * Daemon class taking care of propagation of the associated server on
	 * other subnets and accumulating information on servers on other subnets.
	 * 
	 * @author	Frederic Cremer
	 *
	 */
	public class ServerDaemon implements Runnable {
		
		private Vector serverIPs;
		
		/**
		 * Initialize a new server daemon.
		 */
		public ServerDaemon() {
			serverIPs = new Vector();
			// start timertasks
		}
		
		/**
		 * Add a new server IP to the list of server IPs.
		 */
		public void addIP(String ip) {
			serverIPs.add(ip);
		}
		
		/**
		 * Remove a server IP from the list of server IPs.
		 */
		public void removeIP(String ip) {
			serverIPs.remove(ip);
		}
		
		/**
		 * Run this server daemon.
		 */
		public void run() {
			// controleer binnenkomende datagrampakketten op elk beschikbaar IP
		}
		
		// announcements: timertask
		
		// lijst updaten: timertask
		
	}
	
}
