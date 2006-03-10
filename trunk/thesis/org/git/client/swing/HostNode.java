/*
 * Created on Aug 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.git.client.swing;

import org.git.client.Host;
import org.git.client.daap.DaapHost;
import org.git.client.local.LocalHost;
import org.git.client.rss.PodcastHost;

/**
 * @author Greg
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class HostNode extends GITNode {
    
    public HostNode(DaapHost h) {
        super(h);
    }
    
    public HostNode(LocalHost h) {
        super(h);
    }

    /**
     * @param host
     */
    public HostNode(PodcastHost host) {
        super(host);
    }

    public void addPlaylists() {
        Host h = (Host)userObject;
        if (h.getStatus() != Host.STATUS_CONNECTED)
        {
            
        }
    }
    
    public String getName() {
        return ((Host)userObject).getName();
    }
    
    public Host getHost() {
        return (Host)userObject;
    }
    
    public boolean isEditable() {
        return true;
    }
    
    public String getToolTipText() {
        return ((Host)userObject).getToolTipText();
    }
    
}
