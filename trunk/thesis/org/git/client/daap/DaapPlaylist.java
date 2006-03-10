/*
 * Created on Aug 18, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.git.client.daap;

import java.util.ArrayList;
import java.util.Collection;

import org.git.client.Playlist;
import org.git.client.daap.request.BadResponseCodeException;
import org.git.client.daap.request.SinglePlaylistRequest;

/**
 * @author Greg
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DaapPlaylist extends Playlist {
    
    public int id;
    public String persistent_id;
    public boolean smart_playlist;
    public int song_count = 0;
    
    protected ArrayList songs;
    protected DaapHost host;
    
    public DaapPlaylist(DaapHost h) {
        host = h;
        setStatus(Playlist.STATUS_NOT_INITIALIZED);
    }
    
    public void initialize() {
        setStatus(Playlist.STATUS_INITIALIZING);
        try {
            SinglePlaylistRequest p = new SinglePlaylistRequest(this);
            songs = p.getSongs();
            setStatus(Playlist.STATUS_INITIALIZED);
        } catch (BadResponseCodeException e) {
            if (host.login(false))
            {
                initialize();
            	return;
            }
            setStatus(Playlist.STATUS_NOT_INITIALIZED);
            System.out.println("Error code "+e.response_code+" on playlist");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DaapHost getHost() {
        return host;
    }
    
    public String getPersistentId() {
        return persistent_id;
    }
    
    public int getId() {
        return id;
    }
    
    public Collection getSongs() {
        if (songs == null)
            return new ArrayList();
        return songs;
    }
    
    public int getSongCount() {
        return song_count;
    }
    
    public String getToolTipText() {
        String str = "<HTML>";
        str += "Name: "+name+"<BR>";
        str +="Smart Playlist: "+(smart_playlist ? "Yes" : "No");
        str +="<BR>";
        if (status == Playlist.STATUS_INITIALIZED)
            str +="Song Count: "+songs.size()+"<BR>";
        else
            str +="Song Count: "+song_count+"<BR>";
        return str;
    }
    
}
