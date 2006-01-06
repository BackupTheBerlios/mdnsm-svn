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

public class ServiceServer implements Runnable {
	
	private Client client;
	private JmDNS jmdns;
	
	private String hostAddress;
	private static int threads = 10;
	private static int port = 53;
	
	private ServiceInfo serviceInfo;
	
	private Timer timer;
	private final int SERVER_STOPPED = 0;
	private final int SERVER_RUNNING = 1;
	private int status;
	private Thread statusMonitor;
	
	/**
	 * Initialize a new server as a part of the given client.
	 * 
	 * @param	client
	 *          The client of which this server is a part.
	 * @param	jdmsn
	 * 			The JmDNS instance to which this server is bound.
	 * @param	hostAddress
	 * 			The IP address of the network interface card on which this server is listening.
	 */
	public ServiceServer(Client client, JmDNS jmdns, String hostAddress) {
		if(client == null) {
			throw new IllegalArgumentException("ServiceServer.ServiceServer: invalid client specified.");
		}
		this.client = client;
		
		if(jmdns == null) {
			throw new IllegalArgumentException("ServiceServer.ServiceServer: invalid JmDNS instance specified.");
		}
		this.jmdns = jmdns;
		
		if(hostAddress == null) {
			throw new IllegalArgumentException("ServiceServer.ServiceServer: invalid host name specified.");
		}
		this.hostAddress = hostAddress;
		timer = new Timer();
		// Register this server as a service
		try {
			// TODO: deftige benaming voor service servers en deftige beschrijving
			serviceInfo = new ServiceInfo("_sserver._udp." + getHostAddress() + ".local.", "serviceserver", 53, "service server on "+hostAddress+" registering services");
			jmdns.registerService(serviceInfo);
		}
		catch(IOException exc) {
			System.out.println("DNSServer.DNSServer: some I/O exception occured while registering service server with JmDNS instance:");
			exc.printStackTrace();
		}
		
		System.out.println("Service server started for "+hostAddress+".");
		
		// Start listening for new service types on the local subnet
		try {
			jmdns.addServiceTypeListener(new STypeListener());
		}
		catch(IOException exc) {
			System.out.println("DNSServer.DNSServer: some I/O exception occured while adding TypeListener:");
			exc.printStackTrace();
		}
		status = SERVER_RUNNING;
	}
	
	public void run() {
		while(status == SERVER_RUNNING) {
			
			// TODO: deze code hier in plaats van in constructor doet programma vlotter werken.  Waarom?
			// Register this server as a service
			try {
				// TODO: deftige benaming voor service servers en deftige beschrijving
				serviceInfo = new ServiceInfo("_sserver._udp." + getHostAddress() + ".local.", "serviceserver", 53, "service server on "+hostAddress+" registering services");
				jmdns.registerService(serviceInfo);
			}
			catch(IOException exc) {
				System.out.println("DNSServer.DNSServer: some I/O exception occured while registering service server with JmDNS instance:");
				exc.printStackTrace();
			}
			
			System.out.println("Service server started for "+hostAddress+".");
			
			// Start listening for new service types on the local subnet
			try {
				jmdns.addServiceTypeListener(new STypeListener());
			}
			catch(IOException exc) {
				System.out.println("DNSServer.DNSServer: some I/O exception occured while adding TypeListener:");
				exc.printStackTrace();
			}
			
			// TODO: start UDP daemon and multicast an announcement to all other
			// servers on the network
			// TODO: start listening for queries from other servers
			// TODO: start listening for queries from the local subnet
		}
		jmdns.unregisterService(serviceInfo);
		// TODO: boodschap naar alle andere service servers
		getClient().getServerCache().removeSubnet(hostAddress);
		serviceInfo = null;
		jmdns = null;
		client = null;
		System.out.println("Service server stopped for "+hostAddress+".");
		hostAddress = null;
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
				//synchronized(typesDiscovered) {
					typesDiscovered.add(event.getType());
				//}
				System.out.println("ServiceServer.serviceTypeAdded ("+hostAddress+"): " + event.getType());
				jmdns.addServiceListener(event.getType(), new SListener());
			}
			else {
				System.out.println("ServiceServer.serviceTypeAdded ("+hostAddress+"): service type exists: " + event.getType());
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
			getClient().getServerCache().addService(new ServiceInfo(event.getType(), event.getName()));
			System.out.println("ServiceServer.serviceAdded ("+hostAddress+"): " + event.getType());
			new ServiceResolver(event).start();
		}
		
		/**
		 * Inner class to delay the service information resolving a bit to allow
		 * service information to be available.
		 *
		 * @author	Frederic Cremer
		 */
		private class ServiceResolver extends TimerTask {
			
			private ServiceEvent event;
			
			public ServiceResolver(ServiceEvent event) {
				this.event = event;
			}
			
			public void start() {
				timer.schedule(this, 200);
			}
			
			public void run() {
				jmdns.requestServiceInfo(event.getType(), event.getName());
			}
			
		}
		
		/**
		 * A service has been removed and thus removed from the server's cache.
		 */
		public void serviceRemoved(ServiceEvent event) {
			System.out.println("ServiceServer.serviceRemoved ("+hostAddress+"): " + event.getType());
			getClient().getServerCache().removeService(event.getType(), event.getName());
		}
		
		/**
		 * A service has been resolved and updated in the server's cache.
		 */
		public void serviceResolved(ServiceEvent event) {
			getClient().getServerCache().addService(event.getInfo());
			System.out.println("ServiceServer.serviceResolved ("+hostAddress+"): " + event.getType() + " at " + event.getInfo().getHostAddress() + ":" + event.getInfo().getPort() + " offering \"" + event.getInfo().getTextString() + "\"");
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
		status = SERVER_STOPPED;
	}
	
	// obsolete, tenzij gebruiken voor name records (to be decided)
	public static Zone getZone(String zone) {
		return null;
	}

}
