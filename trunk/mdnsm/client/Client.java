package org.mdnsm.client;

import org.mdnsm.mdns.*;
import org.mdnsm.monitor.*;
import org.mdnsm.server.*;
import java.io.*;

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
	private ServerMonitor monitor;
	
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
	
	public ServerMonitor getMonitor() {
		return monitor;
	}
	
}
