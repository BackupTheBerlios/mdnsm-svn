package org.mdnsm.client;

import org.mdnsm.mdns.*;
import org.mdnsm.monitor.*;
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
	
	public Client(JmDNS jmdns) throws IOException {
		this.jmdns = jmdns;
		serverCache = new ServiceCache();
		server = new ServiceServer(this, jmdns.getInterface().getHostAddress());
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
		
		public void start() {
			// TODO: instellen dat om de zoveel tijd run() uitgevoerd wordt
		}
		
		public void run() {
			// TODO:
			// a) checken van beschikbaarheid van NIC's
			// b) servers/JmDNS's opstarten en afsluiten
		}
		
	}
	
}
