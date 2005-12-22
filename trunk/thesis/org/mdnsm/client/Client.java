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
	
	private ServiceServer server;
	private ServiceCache serverCache;
	private JmDNS jmdns;
	
	private Timer timer;
	
	public Client(JmDNS jmdns) throws IOException {
		timer = new Timer();
		new NICMonitor().start();
//		this.jmdns = jmdns;
//		serverCache = new ServiceCache();
//		server = new ServiceServer(this, jmdns.getInterface().getHostAddress());
	}
	
	public ServiceServer getServer() {
		return server;
	}
	
	public ServiceCache getServerCache() {
		return serverCache;
	}
	
	public JmDNS getJmdns() {
		return jmdns;
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
		
		private String os;
		private final long MONITOR_INTERVAL = 10000;  // TODO: interval correct instellen
		
		public NICMonitor() {
			os = System.getProperty("os.name");
		}
		
		public void start() {
			timer.schedule(this, 0, MONITOR_INTERVAL);
		}
		
		public void run() {
			try {
				String[] ips = getIPs();
				for(int i = 0; i < ips.length; i++) {
					System.out.println(ips[i]);
				}
			}
			catch(Exception exc) {
				
			}
			// TODO:
			// a) checken van beschikbaarheid van NIC's
			// b) servers/JmDNS's opstarten en afsluiten
		}
		
		/**
		 * Get the available IPs of this computer.
		 */
		private String[] getIPs() throws IOException {
			if(os.equals("Windows XP")) {
				Vector ips = getWindowsIPConfiguration();
				String[] result = new String[ips.size()];
				for(int i = 0; i < ips.size(); i++) {
					result[i] = (String)ips.get(i);
				}
				return result;
			}
			else if(os.equals("Linux")){
				Vector ips = getLinuxIPConfiguration();
				String[] result = new String[ips.size()];
				for(int i = 0; i < ips.size(); i++) {
					result[i] = (String)ips.get(i);
				}
				return result;
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
		
	}
	
}
