package org.mdnsm.client;

import org.mdnsm.mdns.*;
import org.mdnsm.server.*;
import java.util.*;
import java.net.*;
import java.io.*;
import java.util.regex.*;

public class Test {
	
	public static void main(String args[]) throws IOException {
//		JmDNS jmdns = new JmDNS();
//		jmdns.registerService(new ServiceInfo("_ftp._udp."+jmdns.getInterface().getHostAddress()+".local.", "testftp", 1125, "test ftp"));
//		Client client = new Client(jmdns);
//		jmdns.registerService(new ServiceInfo("_http._udp."+jmdns.getInterface().getHostAddress()+".local.", "testhttp", 1112, "test http"));
		testIpconfig();
		testOS();
	}
	
	private static void testOS() {
		System.out.println(System.getProperty("os.name"));
	}
	
	private static void testIpconfig() throws IOException {
		String command = "ipconfig";
		Process process = Runtime.getRuntime().exec(command);
		BufferedInputStream is = new BufferedInputStream(process.getInputStream());
		String output = "";
		int c = is.read();
		while(c != -1) {
			if(output.endsWith("IP Address. . . . . . . . . . . . : ")) {
				String ip = "";
				while(Character.isDigit((char)c) || (char)c == '.') {
					ip += (char)c;
					c = is.read();
				}
				System.out.println("IP address: " + ip + " (lengte: " + ip.length() + ")");
			}
			output += (char) c;
			c = is.read();
		}
	}
	
}
