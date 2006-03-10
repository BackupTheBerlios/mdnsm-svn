/*
 * Created on Aug 17, 2004
 * Created by wooo as part of git
 */
package org.git.player;

import java.awt.event.ActionEvent;

/**
 * @author wooo
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PlayerAdapter implements PlayerListener {

    /* (non-Javadoc)
     * @see itunes.player.PlayerListener#songFinished(java.awt.event.ActionEvent)
     */
    public void songFinished(ActionEvent e) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see itunes.player.PlayerListener#songLoaded(java.awt.event.ActionEvent)
     */
    public void songLoaded(ActionEvent e) {
        // TODO Auto-generated method stub

    }

    public void statusUpdated(Object o, int status){}
    
}
