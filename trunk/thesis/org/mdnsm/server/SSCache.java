package org.mdnsm.server;

import java.util.*;

/**
 * A cache containing resource records pointing to other known service
 * servers in the network.
 * 
 * @author Frederic Cremer
 */
public class SSCache {
	
	private Vector elements;
	
	/**
	 * Initialize a new service server cache.
	 */
	public SSCache() {
		elements = new Vector();
	}
	
	/**
	 * Get an iterator over all elements in this cache.
	 */
	public Iterator iterator() {
		return elements.iterator();
	}
	
	/**
	 * Add a new resource record to the cache, or update an existing
	 * resource record if a match occurs.
	 */
	public void addServer(ResourceRecord rr) {
		Iterator iterator = elements.iterator();
		SSElement exists = null;
		while(iterator.hasNext() && exists == null) {
			SSElement next = (SSElement)iterator.next();
			if(next.getRR().getDomain().equals(rr.getDomain())) {
				exists = next;
			}
		}
		if(exists == null) {
			elements.add(new SSElement(rr));
		}
		else {
			exists.setExpirationTime(exists.getRR().getTtl());
		}
	}
	
	/**
	 * Iterate over all resource records in the cache and delete the ones
	 * with an expiration time equal to or smaller than the current time.
	 */
	public void clean() {
		long now = System.currentTimeMillis();
		Iterator iterator = elements.iterator();
		while(iterator.hasNext()) {
			SSElement next = (SSElement)iterator.next();
			if(next.getExpirationTime() <= now) {
				elements.remove(next);
				next.terminate();
			}
		}
	}
	
	/**
	 * Representation of an element in the Service Server Cache.
	 * Such an element consists of a resource record pointing to a service server,
	 * along with an expiration time for the record.
	 */
	public class SSElement {
		
		private ResourceRecord rr;
		private long expTime;
		
		/**
		 * Initialize a new element with the given resource record.
		 */
		public SSElement(ResourceRecord rr) {
			this.rr = rr;
			setExpirationTime(rr.getTtl());
		}
		
		/**
		 * Get the resource record of this cache element.
		 */
		public ResourceRecord getRR() {
			return rr;
		}
		
		/**
		 * Get the expiration time of this cache element.
		 */
		public long getExpirationTime() {
			return expTime;
		}
		
		/**
		 * Set the expiration time of this cache element to ttl milliseconds
		 * after the current time.
		 */
		protected void setExpirationTime(int ttl) {
			expTime = System.currentTimeMillis() + ttl;
		}
		
		public void terminate() {
			rr = null;
			expTime = 0;
		}
		
	}
	
}
