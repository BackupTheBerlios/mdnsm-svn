package org.mdnsm.client;

import org.mdnsm.mdns.*;
import org.mdnsm.server.*;
import java.util.*;
import java.net.*;
import java.io.*;

public class Test {
	
	public static void main(String args[]) throws IOException {
		JmDNS jmdns = new JmDNS();
		Client client = new Client(jmdns);
		jmdns.registerService(new ServiceInfo("_http._udp."+jmdns.getInterface().getHostAddress()+".local.", "testhttp", 1030, "test service"));
	}
	
}
