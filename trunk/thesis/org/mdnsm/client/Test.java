package org.mdnsm.client;

import org.mdnsm.mdns.*;
import org.mdnsm.server.*;
import java.util.*;
import java.net.*;
import java.io.*;
import java.util.regex.*;

public class Test {
	
	public static void main(String args[]) throws IOException {
//		jmdns.registerService(new ServiceInfo("_ftp._udp."+jmdns.getInterface().getHostAddress()+".local.", "testftp", 1125, "test ftp"));
		Client client = new Client();
//		jmdns.registerService(new ServiceInfo("_http._udp."+jmdns.getInterface().getHostAddress()+".local.", "testhttp", 1112, "test http"));
	}
	
}
