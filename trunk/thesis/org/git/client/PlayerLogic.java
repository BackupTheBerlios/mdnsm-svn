/*
 * Created on Feb 18, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.git.client;

import java.util.Stack;

import org.git.client.swing.GetItTogether;
import org.git.client.swing.QueuePlaylist;

/**
 * @author Greg
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PlayerLogic {
    
    public Stack history;
    private GetItTogether git;
    private QueuePlaylist queue;
    
    
    
    public PlayerLogic() {
        git = GetItTogether.instance;
        queue = git.queue;
    }
    

    
}
