/* OMG h4x HAHAHAHAH OWEND GREG DONT MAKE ME MOVE UR CRAP D00d -BRIAN
 * Created on Aug 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.git.client.swing;

import org.dotuseful.ui.tree.AutomatedTreeNode;
import org.git.GITUtils;
import org.git.StringPairList;
import org.git.client.Host;
import org.git.client.Playlist;
import org.git.client.daap.DaapHost;
import org.git.client.local.LocalHost;
import org.git.client.rss.PodcastHost;

/**
 * @author Greg
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GITNode extends AutomatedTreeNode {
    protected int type;
    
    public final static int DAAP = 0;
    public final static int LOCAL = 1;
    public final static int PLAYLIST = 2;
    public final static int ROOT = 3;
    public final static int SPACE = 4;
    public final static int DAAP_ROOT = 5;
    public final static int LOCAL_ROOT = 6;
    public final static int PLAYLIST_ROOT = 7;
    public final static int SETTINGS = 8;
    public final static int DOWNLOADER = 9;
    public static final int PODCAST = 10;

    public static final int PODCAST_ROOT = 11;
    
    public GITNode(DaapHost host) {
        super(host);
        type = DAAP;
    }
    
    public GITNode(LocalHost host) {
        super(host);
        type = LOCAL;
    }
    
    public GITNode(PodcastHost host) {
        super(host);
        type = PODCAST_ROOT;
    }
    public GITNode(Playlist pl) {
        super(pl);
        type = PLAYLIST;
    }
    
    public GITNode(int i) {
        super("");
        type = i;
    }
    
    public GITNode (String s, int i) {
        super(s);
        type = i;
    }
    
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        nodeChanged();
        // make it throw an exception if the userObject isn't a Host.
        Host h = (Host)userObject;
    }
    
    public int getType() {
        return type;
    }
    
    public GITNode getChildByObject(Object o) {
        // search through children
        for (int i=0; i < getChildCount(); i++) {
            GITNode node = (GITNode) getChildAt(i);
            if (node.getUserObject() == o)
                return node;
        }
        
        // search through children's children
        for (int i=0; i < getChildCount(); i++) {
            GITNode node = (GITNode) getChildAt(i);
            GITNode c = node.getChildByObject(o);
            if (c != null)
                return c;
        }
        return null;
    }
    
    public GITNode getChildByName(String s) {
        for (int i=0; i < getChildCount(); i++) {
            GITNode node = (GITNode) getChildAt(i);
            if (node.toString().equals(s))
                return node;
        }
        return null;
    }
    
    public void insertAlphabetically(GITNode insert) {
        insert(insert, getIndexForInsertion(insert));
    }
    
    protected int getIndexForInsertion(GITNode new_node) {
        int cur = -1;
        boolean found = false;
        for (int i = 0; i < getChildCount(); i++) {
            GITNode node = (GITNode) getChildAt(i);
            if (node == null)
                return -1;
            if (node.getUserObject().toString().toLowerCase().compareTo(
                    new_node.getUserObject().toString().toLowerCase()) > 0
                    && node.getType() == new_node.getType()) {
                found = true;
                return i;
            }
        }
        return getChildCount();
    }
    
    public String getToolTipText() {
        switch (getType()) {
	    	case LOCAL_ROOT:
        	case DAAP_ROOT:
	    	    StringPairList pairs = new StringPairList();
	    	    if (getType() == GITNode.DAAP_ROOT)
	    	        pairs.addPair("Number of Hosts", String.valueOf(getChildCount()));
	    	    else if (getType() == GITNode.LOCAL_ROOT)
	    	        pairs.addPair("Number of Libraries", String.valueOf(getChildCount()));
	    	    return GITUtils.createPropertyLabel(pairs);
		    default:
		        return toString();
        }
    }
    
    public String toString() {
        return userObject.toString();
    }
    
}
