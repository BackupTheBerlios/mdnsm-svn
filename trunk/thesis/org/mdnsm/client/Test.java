package org.mdnsm.client;

import org.mdnsm.mdns.*;
import org.mdnsm.server.*;

import java.util.*;
import java.net.*;
import java.io.*;
import java.util.regex.*;

public class Test {
	
	public static void main(String args[]) throws IOException {
		Client client = new Client();
		try {
			Thread.sleep(15000);
		}
		catch(Exception exc) {
			
		}
		ServiceInfo info = new ServiceInfo("_blop._udp.*.local.", "mdnsm3", 80, 0, 0, new String("testblop").getBytes());
		client.registerService(info);
		//client.requestInfo("_test._udp.*.local.", new L());
		try {
			Thread.sleep(15000);
		}
		catch(Exception exc) {
			
		}
		client.unregisterService(info);
	}
	
	private static class L implements ServiceListener {
		public void serviceAdded(ServiceEvent event) {
			
		}
		public void serviceResolved(ServiceEvent event) {
			
		}
		public void serviceRemoved(ServiceEvent event) {
			
		}
	}
	
}
