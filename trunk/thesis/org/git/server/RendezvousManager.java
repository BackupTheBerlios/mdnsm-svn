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

import org.git.client.swing.GetItTogether;

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
    
    private ServiceInfo music_server_info;
    private ServiceInfo lyrics_server_info;
    private ServiceInfo settings_server_info;
    
    public static RendezvousManager instance() {
        return INSTANCE;
    }
    
    // returns a ServiceInfo object based on the current GITProperties server settings
    private ServiceInfo createMusicServerInfo() {
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
        
        //String qualified_name = GITUtils.getQualifiedServiceName(name);
        
        info = new ServiceInfo(GetItTogether.iTunesService, name, port,
            				0, 0, props);
        
        return info;
    }
    
    private ServiceInfo createLyricsServerInfo() {
        ServiceInfo info;
        
        // load stuff from GITProperties.
        String name = GITProperties.lyricsShareName;
        int port = GITProperties.lyricsSharePort;
        
        // add the Rendezvous properties.
        Hashtable props = new Hashtable();
        props.put(MACHINE_NAME, GITProperties.lyricsShareName);
        props.put(PASSWORD, Boolean.toString(GITProperties.lyricsSharePasswordRequired));
        props.put(VERSION, String.valueOf(DaapUtil.VERSION_3));
        props.put(SERVER_PROGRAM, GIT_SERVER);
        
        //String qualified_name = GITUtils.getQualifiedServiceName(name);
        
        info = new ServiceInfo(GetItTogether.lyricsService, name, port,
            				0, 0, props);
        
        return info;
    }
    
    private ServiceInfo createSettingsServerInfo() {
        ServiceInfo info;
        
        // load stuff from GITProperties.
        String name = GITProperties.settingsShareName;
        int port = GITProperties.settingsSharePort;
        
        // add the Rendezvous properties.
        Hashtable props = new Hashtable();
        props.put(MACHINE_NAME, GITProperties.settingsShareName);
        props.put(PASSWORD, Boolean.toString(GITProperties.settingsSharePasswordRequired));
        props.put(VERSION, String.valueOf(DaapUtil.VERSION_3));
        props.put(SERVER_PROGRAM, GIT_SERVER);
        
        //String qualified_name = GITUtils.getQualifiedServiceName(name);
        
        info = new ServiceInfo(GetItTogether.settingsService, name, port,
            				0, 0, props);
        
        return info;
    }
    
    public boolean isMusicRegistered() {
        return (music_server_info != null);
    }
    
    public boolean isLyricsRegistered() {
        return (lyrics_server_info != null);
    }
    
    public boolean isSettingsRegistered() {
        return (settings_server_info != null);
    }
    
    /**
     * Register the server of the given type with the client.
     */
    public synchronized void registerServer(String type) throws IOException {
        if(type.equals(GetItTogether.iTunesService)) {
	    	if (isMusicRegistered())
	            return;
	        
	        ServiceInfo info = createMusicServerInfo();
	        client.registerService(info);
	        
	        this.music_server_info = info;
        }
        else if(type.equals(GetItTogether.lyricsService)) {
	    	if (isLyricsRegistered())
	            return;
	        
	        ServiceInfo info = createLyricsServerInfo();
	        client.registerService(info);
	        
	        this.lyrics_server_info = info;
        }
        if(type.equals(GetItTogether.settingsService)) {
	    	if (isSettingsRegistered())
	            return;
	        
	        ServiceInfo info = createSettingsServerInfo();
	        client.registerService(info);
	        
	        this.settings_server_info = info;
        }
        if(isMusicRegistered() || isLyricsRegistered() || isSettingsRegistered()) {
        	setStatus(STATUS_REGISTERED);
        }
    }
    
    // unregisters the daap server with mDNS
    public synchronized void unregisterServer(String type) {
        if(type.equals(GetItTogether.iTunesService)) {
			if (!isMusicRegistered())
		        return;
		    client.unregisterService(music_server_info);
		    this.music_server_info = null;
        }
        else if(type.equals(GetItTogether.lyricsService)) {
			if (!isLyricsRegistered())
		        return;
		    client.unregisterService(lyrics_server_info);
		    this.lyrics_server_info = null;
        }
        else if(type.equals(GetItTogether.settingsService)) {
			if (!isSettingsRegistered())
		        return;
		    client.unregisterService(settings_server_info);
		    this.settings_server_info = null;
        }
        if(!isMusicRegistered() && !isLyricsRegistered() && !isSettingsRegistered()) {
        	setStatus(STATUS_UNREGISTERED);
        }
    }
    
    // updates the broadcast music server info
    public synchronized void update() {
        if (!isMusicRegistered())
            return;
        
        //unregisterServer(GetItTogether.iTunesService);
        
        ServiceInfo info = createMusicServerInfo();
        //client.registerService(info);
        music_server_info = info;
        setStatus(STATUS_REGISTERED);
    }
    
    // closes jmdns, unregisters all services.
    public synchronized void close() {
        setStatus(STATUS_UNREGISTERED);
        music_server_info = null;
        lyrics_server_info = null;
        settings_server_info = null;
        client.shutdown();
    }
    
    public void setClient(Client client) {
        this.client = client;
    }
    
}
