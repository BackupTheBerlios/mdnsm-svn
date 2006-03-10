/*
 * Created on Oct 17, 2005
 *
 */
package org.git.client.swing;

import java.util.List;

import org.git.client.Song;

import ca.odell.glazedlists.TextFilterator;

/**
 * @author Greg
 *
 */
public class SongTextFilterator implements TextFilterator {

    /* (non-Javadoc)
     * @see ca.odell.glazedlists.TextFilterator#getFilterStrings(java.util.List, java.lang.Object)
     */
    public void getFilterStrings(List baseList, Object element) {
        Song s = (Song)element;

        baseList.add(s.getName());
        baseList.add(s.getGenre());
        baseList.add(s.getArtist());
        baseList.add(s.getAlbum());
        baseList.add(s.getFormat());
        baseList.add(s.getHost().getName());
        baseList.add(s.getSongInfo());
    }

}
