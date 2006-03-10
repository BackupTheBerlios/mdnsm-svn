/*
 * Created on Aug 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.git.client.swing;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import org.dotuseful.ui.tree.AutomatedTreeModel;
import org.git.client.Host;
import org.git.client.local.LocalHost;


/**
 * @author Greg
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GITTreeModel extends AutomatedTreeModel {
    
    public GITTreeModel(GITNode node) {
        super(node);
    }
    
    public GITNode getChildByObject(DefaultMutableTreeNode node, Object h) {
        for (int i=0; i < node.getChildCount(); i++)
        {
            GITNode g = (GITNode)node.getChildAt(i);
            if (g.getUserObject().equals(h))
                return g;
        }
        return null;
    }
    
    public GITNode getChildByName(DefaultMutableTreeNode node, String name) {
        for (int i=0; i < node.getChildCount(); i++)
        {
            GITNode g = (GITNode)node.getChildAt(i);
            if (g.toString().equals(name))
                return g;
        }
        return null;
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
    	MutableTreeNode   aNode = (MutableTreeNode)path.getLastPathComponent();
    	if (aNode instanceof HostNode)
    	{
    	    HostNode hnode = (HostNode)aNode;
    	    Host h = (Host)hnode.getUserObject();
    	    if (hnode.getType() == GITNode.LOCAL)
    	    {
    	        LocalHost lh = (LocalHost)h;
    	        lh.setName(newValue.toString());
    	    }
    	    
    	}
        nodeChanged(aNode);
        }
    
}