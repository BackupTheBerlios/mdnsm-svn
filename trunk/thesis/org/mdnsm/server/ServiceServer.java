package org.mdnsm.server;

import org.mdnsm.mdns.*;
import org.mdnsm.client.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;

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
	private int infoTtl = 360000;
	
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
			int ttl = 360000;
			client.getServerCache().add(new DNSRecord.Pointer(serviceInfo.getType(), DNSConstants.TYPE_PTR, DNSConstants.CLASS_IN, infoTtl, serviceInfo.getQualifiedName()));
			client.getServerCache().add(new DNSRecord.Service(serviceInfo.getQualifiedName(), DNSConstants.TYPE_SRV, DNSConstants.CLASS_IN, infoTtl, serviceInfo.getPriority(), serviceInfo.getWeight(), serviceInfo.getPort(), jmdns.getLocalHost().getName()));
			client.getServerCache().add(new DNSRecord.Text(serviceInfo.getQualifiedName(), DNSConstants.TYPE_TXT, DNSConstants.CLASS_IN, infoTtl, serviceInfo.getTextBytes()));
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
		
		// TODO: start listening for queries from other servers
		// TODO: start listening for queries from the local subnet
		status = SERVER_RUNNING;
	}
	
	public void run() {
		while(status == SERVER_RUNNING) {
			try {
				Thread.sleep(100);
			}
			catch(Exception exc) {
				
			}
		}
		jmdns.unregisterService(serviceInfo);
		// TODO: boodschap naar alle andere service servers
		
		boolean left = true;
		// Remove server PTR records from the server cache
		DNSEntry entry = client.getServerCache().get(new DNSRecord.Pointer(serviceInfo.getType(), DNSConstants.TYPE_PTR, DNSConstants.CLASS_IN, infoTtl, serviceInfo.getQualifiedName()));
		while(left) {
			left = client.getServerCache().remove(entry);
		}
		left = true;
		// Remove server SRV records from the server cache
		entry = client.getServerCache().get(new DNSRecord.Service(serviceInfo.getQualifiedName(), DNSConstants.TYPE_SRV, DNSConstants.CLASS_IN, infoTtl, serviceInfo.getPriority(), serviceInfo.getWeight(), serviceInfo.getPort(), jmdns.getLocalHost().getName()));
		while(left) {
			left = client.getServerCache().remove(entry);
		}
		left = true;
		// Remove server TXT records from the server cache
		entry = client.getServerCache().get(new DNSRecord.Text(serviceInfo.getQualifiedName(), DNSConstants.TYPE_TXT, DNSConstants.CLASS_IN, infoTtl, serviceInfo.getTextBytes()));
		while(left) {
			left = client.getServerCache().remove(entry);
		}
		
		System.out.println("Service server stopped for "+hostAddress+".");
		// TODO: service info, jmdns, client en hostadres nog op null zetten
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
			client.getServerCache().add(new DNSRecord.Pointer(event.getType(), DNSConstants.TYPE_PTR, DNSConstants.CLASS_IN, infoTtl, event.getName()+event.getType()));
			System.out.println("ServiceServer.serviceAdded (@ "+hostAddress+"): " + event.getName() + "." + event.getType());
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
			boolean left = true;
			// Remove PTR records from the server cache
			DNSEntry entry = client.getServerCache().get(new DNSRecord.Pointer(event.getType(), DNSConstants.TYPE_PTR, DNSConstants.CLASS_IN, infoTtl, event.getName()+event.getType()));
			String alias = ((DNSRecord.Pointer)entry).getAlias();
			while(left && entry != null) {
				left = client.getServerCache().remove(entry);
			}
			left = true;
			// Remove SRV records from the server cache
			entry = client.getServerCache().get(new DNSRecord.Service(alias, DNSConstants.TYPE_SRV, DNSConstants.CLASS_IN, infoTtl, 0, 0, 0, jmdns.getLocalHost().getName()));
			while(left && entry != null) {
				left = client.getServerCache().remove(entry);
			}
			left = true;
			// Remove TXT records from the server cache
			entry = client.getServerCache().get(new DNSRecord.Text(alias, DNSConstants.TYPE_TXT, DNSConstants.CLASS_IN, infoTtl, null));
			while(left && entry != null) {
				left = client.getServerCache().remove(entry);
			}
			System.out.println("ServiceServer.serviceRemoved (@ "+hostAddress+"): " + event.getType());
		}
		
		/**
		 * A service has been resolved and updated in the server's cache.
		 */
		public void serviceResolved(ServiceEvent event) {
			client.getServerCache().add(new DNSRecord.Service(event.getInfo().getQualifiedName(), DNSConstants.TYPE_SRV, DNSConstants.CLASS_IN, infoTtl, event.getInfo().getPriority(), event.getInfo().getWeight(), event.getInfo().getPort(), jmdns.getLocalHost().getName()));
			client.getServerCache().add(new DNSRecord.Text(event.getInfo().getQualifiedName(), DNSConstants.TYPE_TXT, DNSConstants.CLASS_IN, infoTtl, event.getInfo().getTextBytes()));
			System.out.println("ServiceServer.serviceResolved (@ "+hostAddress+"): " + event.getType() + " at " + event.getInfo().getHostAddress() + ":" + event.getInfo().getPort() + " offering \"" + event.getInfo().getTextString() + "\"");
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
	
	public void requestInfo() {
		
	}
	
	/**
	 * Shutdown this DNS server by multicasting a message to all local clients,
	 * shutting down the UDP daemon and removing any service listeners from the
	 * mDNS daemon.
	 */
	public void shutdown() {
		status = SERVER_STOPPED;
	}

}
