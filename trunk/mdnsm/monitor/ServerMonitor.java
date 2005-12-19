package org.mdnsm.monitor;

import org.mdnsm.client.*;

/**
 * Auxiliary class monitoring whether the associated client should become
 * or cease being a server.
 * 
 * @author	Frederic Cremer
 */
public class ServerMonitor {
	
	private Client client;
	
	public ServerMonitor(Client client) {
		if(client == null) {
			throw new IllegalArgumentException("ServerMonitor.ServerMonitor: invalid client specified.");
		}
		this.client = client;
	}
	
	// TODO: methode die periodisch twee zaken checkt:
	//			a) komt client in aanmerking voor server?
	//				zo ja, start server
	//			b) is client niet meer in staat om server te zijn?
	//				zo ja, stop server
	
}
