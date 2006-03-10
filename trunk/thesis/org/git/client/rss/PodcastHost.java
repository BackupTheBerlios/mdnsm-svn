/*
 * Created on Jun 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.git.client.rss;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.parsers.SAXParserFactory;

import org.git.client.Host;
import org.git.client.Song;
import org.git.client.local.ITunesHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

/**
 * @author Greg
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PodcastHost extends Host {

    ArrayList podcastUrls = new ArrayList();
    EventList songs = new BasicEventList();
    EventList playlists;
    
    /**
     * @param nayme
     */
    public PodcastHost(String nayme) {
        super(nayme);
        for (int i=0; i < podcastUrls.size(); i++) {
            addPodcast((String)podcastUrls.get(i));
        }
    }
    
    public void addPodcast(String s) {
        try {
            URL url = new URL(s);
            
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(false);
            
            XMLReader xmlReader = factory.newSAXParser().getXMLReader();
            PodcastHandler handler = new PodcastHandler(this);
            xmlReader.setContentHandler(handler);
            InputSource is = new InputSource(url.openStream());
            xmlReader.parse(is);
            
            songs.getReadWriteLock().writeLock().lock();
            songs.addAll(handler.getSongs());
            songs.getReadWriteLock().writeLock().unlock();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }
    
    public EventList getSongs() {
        if (songs == null)
            songs = new BasicEventList();
        return songs;
    }

    public Collection getPlaylists() {
        if (playlists == null)
            playlists = new BasicEventList();
        return playlists;
    }

    public InputStream getSongStream(Song s) {
        PodcastSong ps = (PodcastSong)s;
        try {
            return ps.url.openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTypeString() {
        return "Podcast host";
    }

}
