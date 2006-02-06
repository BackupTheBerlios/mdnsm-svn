package org.mdnsm.client;

import org.mdnsm.mdns.*;
import org.mdnsm.server.*;

import java.util.*;
import java.net.*;
import java.io.*;
import java.util.regex.*;

public class Test {
	
	public static void main(String args[]) throws IOException {
		DatagramPacket packet = constructPacket("www.test.com", "www.google.be,192.168.0.1,192.168.1.1,zever,gezever!");
		ResourceRecord record = getRRFromPacket(packet);
		System.out.println(record.getDomain());
		System.out.println(getVisitedFromPacket(packet));
		//Client client = new Client();
//		jmdns.registerService(new ServiceInfo("_http._udp."+jmdns.getInterface().getHostAddress()+".local.", "testhttp", 1112, "test http"));
	}
	
	private static final int SERVER_RR_TTL = 360000;
	
	/**
	 * Construct a datagram packet with a resource record of the given server,
	 * along with a list of already visited servers (comma-separated list).
	 */
	private static DatagramPacket constructPacket(String ip, String visited) {
		// Get the numbers required to calculate the byte array length
		int ipLength = ip.length();
		int visitedLength = visited.length();
		byte[] rr = (new ResourceRecord(ip, Utils.NS, 1, SERVER_RR_TTL, new byte[0])).getRR();
		byte[] vb = visited.getBytes();
		// Construct the byte array
		byte[] bytes = new byte[ipLength+visitedLength+rr.length+vb.length];
		// Fill the byte array
		bytes[0] = Utils.getByte(ipLength, 2);
		bytes[1] = Utils.getByte(ipLength, 1);
		bytes[2] = Utils.getByte(visitedLength, 2);
		bytes[3] = Utils.getByte(visitedLength, 1);
		System.arraycopy(rr, 0, bytes, 4, rr.length);
		System.arraycopy(vb, 0, bytes, 4+rr.length, vb.length);
		// Create datagram packet with the byte array
		DatagramPacket result = new DatagramPacket(bytes, bytes.length);
		return result;
	}
	
	/**
	 * Get the resource record from an incoming datagram packet.
	 */
	private static ResourceRecord getRRFromPacket(DatagramPacket packet) {
		// Get the data from the packet
		byte[] bytes = packet.getData();
		// Get the IP from the packet
		int ipLength = Utils.addThem(bytes[0], bytes[1]);
		byte[] ipBytes = new byte[ipLength];
		System.arraycopy(bytes, 4, ipBytes, 0, ipLength);
		String ip = new String(ipBytes);
		// Construct resource record and return it
		return new ResourceRecord(ip, Utils.NS, 1, SERVER_RR_TTL, null);
	}
	
	/**
	 * Get the string of already visited IPs from an incoming datagram packet.
	 */
	private static String getVisitedFromPacket(DatagramPacket packet) {
		//Get the data from the packet
		byte[] bytes = packet.getData();
		// Get the string of visited IPs from the packet
		int ipLength = Utils.addThem(bytes[0], bytes[1]);
		int visitedLength = Utils.addThem(bytes[2], bytes[3]);
		byte[] visitedBytes = new byte[visitedLength];
		System.arraycopy(bytes, 4+ipLength+10, visitedBytes, 0, visitedLength);
		return new String(visitedBytes);
	}
	
}
