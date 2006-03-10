/*
 * Created on Oct 17, 2005
 *
 */
package org.git.client.swing;

import org.git.client.Song;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.matchers.AbstractMatcherEditor;

/**
 * @author Greg
 *
 */
public class VisibleHostMatcherEditor extends AbstractMatcherEditor {

    public VisibleHostMatcherEditor() {
        super();
        currentMatcher = new VisibleHostMatcher();
    }

    public void hostShown() {
        fireRelaxed(currentMatcher);
    }
    
    public void hostHidden() {
        fireConstrained(currentMatcher);
    }
}

    class VisibleHostMatcher implements Matcher {
        
        public boolean matches(Object o) {
            Song song = (Song)o;
      	   if (song.getHost() != null && song.getHost().isVisible())
      	       return true;
      	   else
      	       return false;
        }
    }
