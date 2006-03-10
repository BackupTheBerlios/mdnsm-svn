/*
 * Host.java
 *
 * Created on August 9, 2004, 8:35 PM
 */

package org.git.client;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import ca.odell.glazedlists.EventList;
/**
 *
 * @author  Greg
 */
public abstract class Host {
    protected ArrayList status_listeners;
    protected boolean auto_connect;
    protected String name;
    protected int status;
    protected boolean visible;
    protected ArrayList playlists = new ArrayList();
//    public GetItTogether git;
    
    public static final String[] status_strings = {"Unavailable / Error","Not Connected", "Connecting", "Connected", "Connected"};
    public static final int STATUS_NOT_AVAILABLE = 0;
    public static final int STATUS_NOT_CONNECTED = 1;
    public static final int STATUS_CONNECTING = 2;
    public static final int STATUS_CONNECTED = 3;
    public static final int STATUS_PLAYLISTS_LOADED = 4;
    
    /** Creates a new instance of Host */
    public Host(String nayme) {
        name = nayme;
        status_listeners = new ArrayList();
        visible = false;
        setStatus(STATUS_NOT_CONNECTED);
    }
    
    /**
     * Causes this Host to connect to the song source and load the songs into memory.
     */    
    public void connect() {}
    
    public void loadPlaylists() {}
    
    /**
     * Causes this Host to disconnect from the song source and remove the songs from memory.
     */    
    public void disconnect() {}
    
    /**
     * Retrieves the Songs contained within this Host.
     * @return an ArrayList containing all of the songs contained within the Host.
     *    If there are no songs, an empty ArrayList is returned.
     */    
    public abstract EventList getSongs();

    public Song getSongById(int id) {
        Song song = null;
        Iterator i = getSongs().iterator();
        while (i.hasNext()) {
            song = (Song)i.next();
            if (song != null && song.id == id)
                return song;
        }
        return null;
//        throw new IllegalStateException("Song ID: "+id+" not found in host:"+name);
    }
    
    public abstract Collection getPlaylists();
    
    /**
     * Convenience method.
     * @see getSongStream(Song s, int bytes)
     * @param s the requested Song
     * @return Input stream, starting from byte 0.
     */    
    public abstract InputStream getSongStream(Song s);
    
    // field-return methods:
    /**
     *
     * @return the Host's name.
     */    
    public String getName() {
        return name;
    }
    
    protected void setStatus(int stat) {
        status = stat;
//        System.out.println("my Change");
        fireStatusChanged();
    }
    
    /**
     *
     * @return the Host's status.
     */    
    public int getStatus() {
        return status;
    }
    
    public abstract String getTypeString();
    
    public boolean isVisible() {
        return visible;
    }
    
    public void setVisible(boolean b) {
        visible = b;
        fireStatusChanged();
    }
    
    public boolean equals(Object o) {
        return name.equals(((Host)o).getName());
    }
    
    public String getToolTipText() {
        return name;
    }
    
    public String toString() {
        return name;
    }
    
    public void fireStatusChanged() {
        for (int i=0; i < status_listeners.size(); i++) {
            StatusListener h = (StatusListener)status_listeners.get(i);
            h.stateUpdated(this);
        }
    }
    
    public void addStatusListener(StatusListener sl) {
        status_listeners.add(sl);
    }
    
    public boolean removeStatusListener(StatusListener sl) {
        return status_listeners.remove(sl);
    }

    public boolean isAutoConnect() {
        return auto_connect;
    }

    public void setAutoConnect(boolean aut) {
        auto_connect = aut;
    }
    
}
