/*
 * Created on Mar 15, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.git.client.local;

import java.util.ArrayList;
import java.util.Collection;

import org.git.client.Host;
import org.git.client.Playlist;



/**
 * @author Greg
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LocalPlaylist extends Playlist {
    public LocalHost host;
    public ArrayList songs = new ArrayList();
    public ArrayList songIDs; // Song IDs stored as strings.
    
    int id;
    String persistent_id;
    boolean master;
    boolean all_items;
    
    public LocalPlaylist(LocalHost h) {
        host = h;
        setStatus(Playlist.STATUS_NOT_INITIALIZED);
    }
    
    public Collection getSongs() {
        if (songs == null)
            songs = new ArrayList();
        return songs;
    }

    public void initialize() {
        setStatus(Playlist.STATUS_INITIALIZING);
        for (int i=0; i < songIDs.size(); i++) {
            String s = (String)songIDs.get(i);
            int id = Integer.parseInt(s);
            try {
            songs.add(host.getSongById(id));
            } catch (Exception e){e.printStackTrace();}
        }
        setStatus(Playlist.STATUS_INITIALIZED);
    }
    
}
