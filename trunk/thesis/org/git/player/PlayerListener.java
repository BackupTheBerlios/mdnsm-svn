/*
 * Created on Aug 17, 2004
 * Created by wooo as part of git
 */
package org.git.player;

import java.awt.event.ActionEvent;
import java.util.EventListener;

/**
 * @author wooo
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public interface PlayerListener extends EventListener {
    public abstract void songFinished(ActionEvent e);

    public abstract void songLoaded(ActionEvent e);
    
    public abstract void statusUpdated(Object o, int status);
}