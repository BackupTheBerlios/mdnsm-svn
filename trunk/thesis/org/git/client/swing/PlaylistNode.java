/*
 * Created on Aug 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.git.client.swing;

import org.git.client.Playlist;

/**
 * @author Greg
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PlaylistNode extends GITNode {
    
    public PlaylistNode(Playlist p) {
        super(p);
        setAllowsChildren(false);
    }

    /* (non-Javadoc)
     * @see org.git.client.swing.GITNode#getName()
     */
    public String getName() {
        // TODO Auto-generated method stub
        return ((Playlist)userObject).getName();
    }
    
    public String getToolTipText() {
        return ((Playlist)userObject).getToolTipText();
    }
    
}
