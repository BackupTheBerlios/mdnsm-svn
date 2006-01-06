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
	private ServiceCache serverCache;
	
	private Timer timer;
	
	private String os;
	
	public Client() throws IOException {
		timer = new Timer();
		// TODO: beginnen luisteren naar servers
		new NICMonitor().start();
	}
	
	public ServiceCache getServerCache() {
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
				//System.out.println("Client: checking NICs ...");
				// TODO: server is enige server op lokaal subnet
				Vector ips = getIPs();
				removeServers(ips);
				checkServerNeed(ips);
			}
			catch(IOException exc) {
				System.out.println("Client.NICMonitor.run: I/O exception occurred when determining IPs: " + exc.getMessage());
			}
		}
		
	}
	

	/**
	 * Check whether servers should be halted (that is, only 1 configured NIC remains).
	 */
	private void checkServerNeed(Vector ips) {
		// One IP left, which has a server bound to it
		if(ips.size() <= 1 && servers.keys().hasMoreElements()) {
			((ServiceServer)servers.get(servers.keys().nextElement())).shutdown();
			// TODO: serverCache.empty();
		}
		// Multiple IPs detected
		else if(ips.size() > 1) {
			if(serverCache == null) {
				serverCache = new ServiceCache();
			}
			Iterator iterator = ips.iterator();
			while(iterator.hasNext()) {
				String ip = (String)iterator.next();
				if(jmdnss.containsKey(ip) && !servers.containsKey(ip)) {
					servers.put(ip, new ServiceServer(this, (JmDNS)jmdnss.get(ip), ip));
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
				//((JmDNS)jmdnss.get(key)).close();
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
					System.out.println("ip found: "+ip);
					result.add(ip);
				}
			}
			output += (char) c;
			c = is.read();
		}
		System.out.println();
		return result;
	}
	
}
