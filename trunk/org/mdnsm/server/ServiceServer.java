package org.mdnsm.server;

import org.mdnsm.mdns.*;
import org.mdnsm.client.*;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Server recording NS DNS records for other service servers on the
 * network and recording Multicast DNS records for the services available on the
 * subnet this server is responsible for.
 * 
 * Code is based on JDNSS code (http://jdnss.sourceforge.net).
 * 
 * @author Steve Beaty (JDNSS creator)
 * @author Frederic Cremer
 */

public class ServiceServer {
	
	private String hostAddress;
	private static int threads = 10;
	private static int port = 53;
	private Client client;
	private ServiceCache serviceCache = new ServiceCache();
	
	private final int SERVER_STOPPED = 0;
	private final int SERVER_RUNNING = 1;
	private int status;
	private Thread statusMonitor;

	/**
	 * Initialize a new server as a part of the given client.
	 * 
	 * @param	client
	 *          The client of which this server is a part.
	 */
	public ServiceServer(Client client, String hostAddress) {
		if(client == null) {
			throw new IllegalArgumentException("DNSServer.DNSServer: invalid client specified.");
		}
		this.client = client;
		
		if(hostAddress == null) {
			throw new IllegalArgumentException("DNSServer.DNSServer: invalid host name specified.");
		}
		this.hostAddress = hostAddress;
		
		// Start the server status monitor
		statusMonitor = new Thread(new SServerMonitor(), "ServiceServer.SServerMonitor");
		
		// Register this server as a service
		try {
			getClient().getJmdns().registerService(new ServiceInfo("_sserver._udp." + getHostAddress() + ".local.", "serviceserver", 53, ""));
		}
		catch(IOException exc) {
			System.out.println("DNSServer.DNSServer: some I/O exception occured while registering service server with JmDNS instance:");
			exc.printStackTrace();
		}
		
		// Start listening for new service types on the local subnet
		try {
			getClient().getJmdns().addServiceTypeListener(new STypeListener());
		}
		catch(IOException exc) {
			System.out.println("DNSServer.DNSServer: some I/O exception occured while adding TypeListener:");
			exc.printStackTrace();
		}
		// TODO: start UDP daemon and multicast an announcement to all other
		// servers on the network
		// TODO: start listening for queries from other servers
		// TODO: start listening for queries from the local subnet
		status = SERVER_RUNNING;
		statusMonitor.start();
	}
	
	/**
	 * Listener which detects new service types.
	 * 
	 * @author	Frederic Cremer
	 */
	private class STypeListener implements ServiceTypeListener {
		
		private Vector typesDiscovered = new Vector();
		
		/**
		 * Search available services on the local subnet when detecting
		 * a new service type.
		 */
		public void serviceTypeAdded(ServiceEvent event) {
			if(!typesDiscovered.contains(event.getType())) {
				getClient().getJmdns().addServiceListener(event.getType(), new SListener());
				synchronized(typesDiscovered) {
					typesDiscovered.add(event.getType());
				}
			}
			else {
				System.out.println("ServiceServer.serviceTypeAdded: service type exists: " + event.getType());
			}
		}
		
	}
	
	/**
	 * Listener which detects new services.
	 * 
	 * @author	Frederic Cremer
	 */
	private class SListener implements ServiceListener {
		
		/**
		 * A new service is discovered and added to the server's cache.
		 */
		public void serviceAdded(ServiceEvent event) {
			System.out.println("ServiceServer.serviceAdded: service added: " + event.getType());
			getClient().getServerCache().addService(new ServiceInfo(event.getType(), event.getName()));
		}
		
		/**
		 * A service has been removed and thus removed from the server's cache.
		 */
		public void serviceRemoved(ServiceEvent event) {
			getClient().getServerCache().removeService(event.getType(), event.getName());
		}
		
		/**
		 * A service has been resolved and updated in the server's cache.
		 */
		public void serviceResolved(ServiceEvent event) {
			System.out.println("ServiceServer.serviceResolved: service added: " + event.getType());
			getClient().getServerCache().addService(event.getInfo());
		}
		
	}
	
	/**
	 * Auxiliary class monitoring the state of this service server.
	 * 
	 * @author	Frederic Cremer
	 */
	class SServerMonitor implements Runnable {
		
		public void run() {
			System.out.println("Service server started.");
			while(status == SERVER_RUNNING) {
				// run
			}
			System.out.println("Service server stopped.");
		}
		
	}
	
	public String getHostAddress() {
		return hostAddress;
	}
	
	public static int getThreads() {
		return threads;
	}

	public static int getPort() {
		return port;
	}
	
	/**
	 * Get the parent client of this server.
	 */
	public Client getClient() {
		return client;
	}
	
	/**
	 * Shutdown this DNS server by multicasting a message to all local clients,
	 * shutting down the UDP daemon and removing any service listeners from the
	 * mDNS daemon.
	 */
	public void shutdown() {

	}
	
	// obsolete, tenzij gebruiken voor name records (to be decided)
	public static Zone getZone(String zone) {
		return null;
	}

}
