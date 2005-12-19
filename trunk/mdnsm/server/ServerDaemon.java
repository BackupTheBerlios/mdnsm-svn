package org.mdnsm.server;

/**
 * Daemon class taking care of propagation of the associated server on
 * other subnets and accumulating information on servers on other subnets.
 * 
 * @author	Frederic Cremer
 *
 */
public class ServerDaemon {
	
	private ServiceServer server;
	
	public ServerDaemon(ServiceServer server) {
		if(server == null) {
			throw new IllegalArgumentException("ServerDaemon.ServerDaemon: invalid server specified.");
		}
		this.server = server;
	}
	
	// TODO: methode om server bekend te maken
	
	// TODO: methode om bekendmakingen van andere servers te verwerken
	
}
