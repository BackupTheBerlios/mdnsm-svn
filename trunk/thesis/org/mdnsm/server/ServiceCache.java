package org.mdnsm.server;

import org.mdnsm.mdns.*;
import java.util.*;

/**
 * A cache containing available services per subnet.
 * 
 * @author	Frederic Cremer
 */
public class ServiceCache {
	
	private Hashtable cache;
	
	public ServiceCache() {
		cache = new Hashtable();
	}
	
	/**
	 * Add a new service to this cache.
	 */
	public void addService(ServiceInfo service) {
		String subnet;
		if(!service.getHostAddress().equals("")) {
			subnet = getSubnetFromAddress(service.getHostAddress());
		}
		else {
			subnet = getSubnetFromType(service.getType());
		}
		if(cache.containsKey(subnet)) {
			Vector vector = (Vector)cache.get(subnet);
			Iterator iterator = vector.iterator();
			while(iterator.hasNext()) {
				ServiceInfo info = (ServiceInfo)iterator.next();
				if(info.getType().equalsIgnoreCase(service.getType()) && info.getName().equalsIgnoreCase(service.getName())) {
					synchronized(vector) {
						vector.remove(info);
					}
				}
			}
			synchronized(vector) {
				vector.add(service);
			}
		}
		else {
			synchronized(cache) {
				cache.put(subnet, new Vector());
				((Vector)cache.get(subnet)).add(service);
			}
		}
	}
	
	/**
	 * Remove a service from this cache.
	 */
	public void removeService(String type, String name) {
		ServiceInfo infoTBR = null;
		Enumeration iterator = cache.keys();
		while(iterator.hasMoreElements()) {
			String subnet = (String)iterator.nextElement();
			Vector services = (Vector)cache.get(subnet);
			Iterator iterator2 = services.iterator();
			while(iterator2.hasNext()) {
				ServiceInfo info = (ServiceInfo)iterator2.next();
				if(info.getType().equalsIgnoreCase(type) && info.getName().equalsIgnoreCase(name)) {
					infoTBR = info;
					break;
				}
			}
			if(infoTBR != null) {
				synchronized(services) {
					services.remove(infoTBR);
				}
			}
		}
	}
	
	/**
	 * Get the subnet out of the given address.
	 * Convention used in this program: the first three bytes of each address
	 * forms the subnet.
	 */
	private String getSubnetFromAddress(String address) {
		StringTokenizer tok = new StringTokenizer(address, ".");
		return (tok.nextToken() + "." + tok.nextToken() + "." + tok.nextToken());
	}
	
	/**
	 * Get the subnet out of the given full type.
	 * Type is of form "_protocol._protocol.<IP>.local.".
	 */
	private String getSubnetFromType(String type) {
		StringTokenizer tok = new StringTokenizer(type, ".");
		String token = tok.nextToken();
		while(!token.equals("_udp") && !token.equals("_tcp")) {
			token = tok.nextToken();
		}
		return (tok.nextToken() + "." + tok.nextToken() + "." + tok.nextToken());
	}
	
}
