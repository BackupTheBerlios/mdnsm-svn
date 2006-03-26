/*
 * Created on Jan 21, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.git.server;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Hashtable;

import org.mdnsm.mdns.ServiceInfo;
import org.mdnsm.client.*;

import org.git.GITProperties;
import org.git.GITUtils;
import org.git.client.BasicStatusObject;

import de.kapsi.net.daap.DaapUtil;

/**
 * @author Greg
 *
 * Configures and handles the mDNS broadcasting for GIT.
 * Ideas blatantly borrowed from LimeWire's DaapManager class.
 */
public final class RendezvousManager extends BasicStatusObject{
    
    private static RendezvousManager INSTANCE = new RendezvousManager();
    
    public static final String DAAP_SERVICE_TYPE = "_daap._tcp.local.";
    public static final String MACHINE_NAME = "Machine Name";
    public static final String PASSWORD = "Password";
    public static final String VERSION = "Version";
    public static final String SERVER_PROGRAM = "Daap Server";
    
    public static final String GIT_VERSION = "0.7.0";
    public static final String GIT_SERVER = "Get It Together" + GIT_VERSION;
    
    public static final int STATUS_UNREGISTERED = 0;
    public static final int STATUS_REGISTERED = 1;
    
    private InetAddress address;
    private Client client;
    private ServiceInfo server_info;
    
    public static RendezvousManager instance() {
        return INSTANCE;
    }
    
    // returns a ServiceInfo object based on the current GITProperties server settings
    private ServiceInfo createServerInfo() {
        ServiceInfo info;
        
        // load stuff from GITProperties.
        String name = GITProperties.shareName;
        int port = GITProperties.sharePort;
        boolean password_req = GITProperties.sharePasswordRequired;
        String password = GITProperties.sharePassword;
        
        // add the Rendezvous properties.
        Hashtable props = new Hashtable();
        props.put(MACHINE_NAME, GITProperties.shareName);
        props.put(PASSWORD, Boolean.toString(GITProperties.sharePasswordRequired));
        props.put(VERSION, String.valueOf(DaapUtil.VERSION_3));
        props.put(SERVER_PROGRAM, GIT_SERVER);
        
        String qualified_name = GITUtils.getQualifiedServiceName(name);
        
        info = new ServiceInfo(DAAP_SERVICE_TYPE, qualified_name, port,
            				0, 0, props);
        
        return info;
    }
    
    public boolean isRegistered() {
        return (server_info != null);
    }
    
    // registers the daap server with mDNS
    public synchronized void registerServer() throws IOException {
        if (isRegistered())
            return;
        
        ServiceInfo info = createServerInfo();
        jmdns.registerService(info);
        
        this.server_info = info;
        setStatus(STATUS_REGISTERED);
    }
    
    // unregisters the daap server with mDNS
    public synchronized void unregisterServer() {
        if (!isRegistered())
            return;

        jmdns.unregisterService(server_info);
        this.server_info = null;
        setStatus(STATUS_UNREGISTERED);
    }
    
    // updates the broadcast server info
    public synchronized void update() {
        if (!isRegistered())
            return;
        
        unregisterServer();
        
        try {
        ServiceInfo info = createServerInfo();
        jmdns.registerService(info);
        server_info = info;
        setStatus(STATUS_REGISTERED);
        } catch (IOException io) {io.printStackTrace();}
    }
    
    // closes jmdns, unregisters all services.
    public synchronized void close() {
        setStatus(STATUS_UNREGISTERED);
        server_info = null;
        jmdns.unregisterAllServices();
        jmdns.close();
    }
    
    public void setClient(Client client) {
        this.client = client;
    }
    
}
