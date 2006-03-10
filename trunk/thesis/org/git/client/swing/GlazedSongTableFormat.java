/*
 * Created on Jan 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.git.client.swing;

import java.util.ArrayList;
import java.util.Comparator;

import org.git.GITUtils;
import org.git.client.Song;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.gui.WritableTableFormat;

/**
 * @author Greg
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GlazedSongTableFormat implements WritableTableFormat, AdvancedTableFormat {
    
    public static GlazedSongTableFormat INSTANCE = new GlazedSongTableFormat();
    
    public SongTrackComparator track = new SongTrackComparator();
    public SongArtistComparator artist = new SongArtistComparator();
    public SongAlbumComparator album = new SongAlbumComparator();
    public SongNameComparator name = new SongNameComparator();
    public SongTimeComparator time = new SongTimeComparator();
    public SongSizeComparator size = new SongSizeComparator();    
    
    private ArrayList list = new ArrayList();
    {
        list.add(artist);
        list.add(album);
        list.add(track);
        list.add(name);
    }
    
    public Comparator song = GlazedLists.chainComparators(list);
    
    public int getColumnCount() {
        return 6;
    }
    
    public String getColumnName(int col) {
        switch (col) {
        case 0:
            return "Tr.";
        case 1:
            return "Artist";
        case 2:
            return "Album";
        case 3:
            return "Title";
        case 4:
            return "Length";
        case 5:
            return "Size";
        }
        return "";
    }
    
    public Object getColumnValue(Object o, int col) {
        if (o == null) return null;
        Song s = (Song)o;
        return s;
    }
    
    public boolean isEditable(Object o, int col) {
        if (getColumnName(col).equals("Title"))
            return true;
        else
            return false;
    }
    
    public Object setColumnValue(Object base, Object edited, int col) {
        return base;
    }
    
    public Comparator getColumnComparator(int col) {
        switch(col) {
        case 0:
            return track;
        case 1:
            return artist;
        case 2:
            return album;
        case 3:
            return name;
        case 4:
            return time;
        case 5:
            return size;
        default:
            return song;
        }
    }
    
    //      DEFINE COLUMN COMPARATORS:
    
    private class SongTrackComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            Song s1=(Song)o1;Song s2=(Song)o2;
            return s1.track - s2.track;
        }
    }
    private class SongArtistComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            Song s1=(Song)o1;Song s2=(Song)o2;
            return GITUtils.compareStrings(s1.artist,s2.artist);
        }
    }
    private class SongAlbumComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            Song s1=(Song)o1;Song s2=(Song)o2;
            return GITUtils.compareStrings(s1.album,s2.album);
        }
    }
    private class SongNameComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            Song s1=(Song)o1;Song s2=(Song)o2;
            return GITUtils.compareStrings(s1.name,s2.name);
        }
    }
    private class SongTimeComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            Song s1=(Song)o1;Song s2=(Song)o2;
            return s1.time - s2.time;
        }
    }
    private class SongSizeComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            Song s1=(Song)o1;Song s2=(Song)o2;
            return s1.size - s2.size;
        }
    }
    /* (non-Javadoc)
     * @see ca.odell.glazedlists.gui.AdvancedTableFormat#getColumnClass(int)
     */
    public Class getColumnClass(int column) {
        // TODO Auto-generated method stub
        return null;
    }
    
}