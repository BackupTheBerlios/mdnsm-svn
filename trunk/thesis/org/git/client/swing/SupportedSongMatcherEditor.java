/*
 * Created on Oct 17, 2005
 *
 */
package org.git.client.swing;

import org.git.GITProperties;
import org.git.client.Song;

import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.matchers.AbstractMatcherEditor;


/**
 * @author Greg
 *
 */
public class SupportedSongMatcherEditor extends AbstractMatcherEditor {

    public SupportedSongMatcherEditor() {
        super();
        currentMatcher = new SupportedSongMatcher();
    }

    public void playerChanged() {
        fireChanged(currentMatcher);
    }
    
}

class SupportedSongMatcher implements Matcher {
    public boolean matches(Object arg0) {
        Song s = (Song)arg0;
        if (!GITProperties.hideUnsupportedSongs || GetItTogether.player.supportsSong(s))
            return true;
        else
            return false;
    }    
}