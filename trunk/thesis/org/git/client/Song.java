/*
 * Created on May 7, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
Copyright 2003 Joseph Barnett

This File is part of "one 2 oh my god"

"one 2 oh my god" is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
Free Software Foundation; either version 2 of the License, or
your option) any later version.

"one 2 oh my god" is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with "one 2 oh my god"; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 */
package org.git.client;
import java.util.List;

import org.git.client.local.LocalSong;

import ca.odell.glazedlists.TextFilterable;


/**
 * @author jbarnett
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Song implements Cloneable, TextFilterable {
	public String name;
	public int id;
	public int time;
	public String album;
	public String artist;
	public int track;
	public String genre;
	public String format;
    public int size;
	public Host host;
	public boolean compilation;
	public int bitrate;
    
	// public boolean is_available;
	public int status;
	
	public static int STATUS_OK = 0;
	public static int STATUS_NOT_FOUND = 2;
	public static int STATUS_ERROR = 3;
	
	public Song() {
		name="";
		id=0;
		album = "";
		artist = "";
		track = 0;
		genre = "";
		format = "";
		compilation = false;
        host = null;
        status = Song.STATUS_OK;
	}
	
    public Song duplicate() throws CloneNotSupportedException {
        return (Song)clone();
    }
        
    public boolean contains(String s) {
            if (name.toLowerCase().indexOf(s) != -1)
                return true;
            else if (artist.toLowerCase().indexOf(s) != -1)
                return true;
            else if (album.toLowerCase().indexOf(s) != -1)
                return true;
            else if (genre.toLowerCase().indexOf(s) != -1)
                return true;
            else if (format.toLowerCase().indexOf(s) != -1)
                return true;
            else if (host.getName().toLowerCase().indexOf(s) != -1)
                return true;
            else if (s.startsWith("length:") && ("length:"+Integer.toString(time)).equals(s))
                return true;
            else if (s.startsWith("size:") && ("size:"+Integer.toString(size)).equals(s))
                return true;
            else if (s.startsWith("track:") && ("track:"+Integer.toString(track)).equals(s))
                return true;
            return false;
        }
        
    public boolean equals(Object o) {
        if (o == null)
            return false;
        Song s = (Song)o;
        if (s.artist.equalsIgnoreCase(this.artist) && s.name.equalsIgnoreCase(this.name)
                && s.size == this.size)
            return true;
        else
            return false;
    }
    
	public int compareTo(Object o) {
		if (o instanceof Song) {
		    
			if (this.artist.compareTo(((Song)o).artist) != 0) {
				if (this.artist.matches(" *"))
					return 1;
				else if (((Song)o).artist.matches(" *"))
					return -1;
				if (this.artist.toLowerCase().startsWith("the "))
				    return this.artist.substring(4).compareToIgnoreCase(((Song)o).artist);
				else if (((Song)o).artist.toLowerCase().startsWith("the "))
				    return this.artist.compareToIgnoreCase(((Song)o).artist.substring(4));
				else
				    return this.artist.compareToIgnoreCase(((Song)o).artist);
			}else if (this.album.compareTo(((Song)o).album) != 0) {
				if (this.album.matches(" *"))
					return 1;
				else if (((Song)o).album.matches(" *"))
					return -1;
				return this.album.compareToIgnoreCase(((Song)o).album);
			}
			else if (new Integer(this.track).compareTo(new Integer(((Song)o).track)) != 0)
				return (new Integer(this.track).compareTo(new Integer(((Song)o).track)));
			else
				return this.name.compareToIgnoreCase(((Song)o).name);
		} else throw new ClassCastException();
	}
	
	
    public String toString() {
		//String ret=artist + (artist.length()>0?" - ":"") + album+(album.length()>0?" ":"")+(track>0?" (Track":"")+(track>0?Integer.toString(track):"")+(track>0?") - ":"")+name +(genre.length()>0?" [":"")+genre+(genre.length()>0?"]":"");
		String ret=artist + (artist.length()>0?" - ":"") + name;
		return ret;
	}
        
    public String shortString()
        {
            return "\"" + name + "\" by "+artist;
        }
        
    public Host getHost() {
        return host;
    }
    
	/**
	 * @return
	 */
	public String getAlbum() {
		return album;
	}

	/**
	 * @return
	 */
	public String getArtist() {
		return artist;
	}

	/**
	 * @return
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @return
	 */
	public String getGenre() {
		return genre;
	}

	/**
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public int getTrack() {
		return track;
	}

	/**
	 * @return
	 */
	public int getTime() {
		return time;
	}

	/**
	 * @param i
	 */
	public void setTime(int i) {
		time = i;
	}
        
    public int getSize()
    {
        return size;
    }

    /* (non-Javadoc)
     * @see ca.odell.glazedlists.TextFilterable#getFilterStrings(java.util.List)
     */
    public void getFilterStrings(List strings) {
        strings.add(name);
        strings.add(artist);
        strings.add(album);
        strings.add(genre);
//        strings.add(host);
//        strings.add(format);
        
    }
    
    public String getSongInfo() {
        String s = "<HTML><b>" +
        		"" + name + "<p>" + artist + "<p>" + album + "<p>" +
        		"</b>";
        return s;
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
}

