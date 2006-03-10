/*
 * Created on Jun 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.git.client.rss;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.git.client.Host;
import org.git.client.Song;

/**
 * @author Greg
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PodcastSong extends Song{

    public URL url;
    public Date pubDate;
    
    public PodcastSong(Host h) {
        super();
        host = h;
    }
    
}
