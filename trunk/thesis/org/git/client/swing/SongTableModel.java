/*
    Created on May 8, 2003
    To change the template for this generated file go to
    Window>Preferences>Java>Code Generation>Code and Comments
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
package org.git.client.swing;

import java.util.Collection;
import java.util.Vector;

import org.git.client.Song;

/**
 * @author     jbarnett To change the template for this generated type comment
 *      go to Window>Preferences>Java>Code Generation>Code and Comments
 * @created    July 15, 2004
 */
public class SongTableModel extends MyTableModel {
	public final String[] columnNames  = {"Tr.", "Artist", "Album", "Title", "Length", "Size"};
	//private ArrayList data;
	public Vector songs;
	public Vector searched;
	private boolean searching;


	/**
	 *  Constructor for the SongTableModel object
	 *
	 * @param  p  Description of the Parameter
	 */
	public SongTableModel() {
		super();
		//data = i.getSongs();
		songs = new Vector();
		searched = null;
	}


	/**
	 *  Gets the columnName attribute of the SongTableModel object
	 *
	 * @param  col  Description of the Parameter
	 * @return      The columnName value
	 */
	public String getColumnName(int col) {
		return columnNames[col];
	}


	/**  Description of the Method */
	public void clear() {
		songs = null;
		songs = new Vector();
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	public void add(Song s) {
		songs.add(s);
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	public void remove(Song s) {
		songs.remove(s);
	}
	
	/**
	 *  Description of the Method
	 *
	 * @return    Description of the Return Value
	 */
	public boolean searching() {
		return searching;
	}


	/**  Description of the Method */
	public void search() {
		searched = null;
		searching = false;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  name  Description of the Parameter
	 * @param  time  Description of the Parameter
	 * @return       The duplicate value
	 */

	public boolean isDuplicate(String name, int time) {
		searching = true;
		searched = new Vector();
		for (int row = 0; row < songs.size(); row++) {

			if (((Song) songs.get(row)).getName().trim().indexOf(name.trim()) != -1) {
				System.out.println("name matched: " + name);
				System.out.println("time = " + time + " vs. " + ((Song) songs.get(row)).getTime());
				// since when a song is converted, it loses the exact song length, lets go to be within 100 milliseconds
				// need to be able to mangle the songname here too (get rid of invalid filename characters so that once imported it still matches)
				if (Math.abs(((Song) songs.get(row)).getTime() - time) < 100) {
					System.out.println("time matched (within 100 miliseconds): " + time);
					searched.add((Song) songs.get(row));
					searching = false;
					return true;
				}
			}
		}
		searching = false;
		return false;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  name    Description of the Parameter
	 * @param  album   Description of the Parameter
	 * @param  track   Description of the Parameter
	 * @param  artist  Description of the Parameter
	 * @return         The duplicate value
	 */

	public boolean isDuplicate(String name, String album, int track, String artist) {
		searching = true;
		// searched = null;
		searched = new Vector();
		for (int row = 0; row < songs.size(); row++) {
			name = name.toLowerCase();
			if (((Song) songs.get(row)).contains(name.trim())) {
				artist = artist.toLowerCase();
				if (((Song) songs.get(row)).contains(artist.trim())) {
					album = album.toLowerCase();
					if (((Song) songs.get(row)).contains(album.trim())) {
						if (((Song) songs.get(row)).getTrack() == track) {
							searched.add((Song) songs.get(row));
							searching = false;
							return true;
						}
					}
				}
			}
		}
		searching = false;
		return false;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	public void search(String s) {
		searching = true;
		searched = new Vector();
		s = s.toLowerCase();
		for (int row = 0; row < songs.size(); row++) {
			if (((Song) songs.get(row)).contains(s.trim()) && ((Song) songs.get(row)).host.isVisible()) {
				searched.add((Song) songs.get(row));
			}
		}
		searching = false;
	}


	/**
	 *  Gets the cellEditable attribute of the SongTableModel object
	 *
	 * @param  row  Description of the Parameter
	 * @param  col  Description of the Parameter
	 * @return      The cellEditable value
	 */
	public boolean isCellEditable(int row, int col) {
	    if (col == 3)
	        return true;
	    else
	        return false;
	}


	/**
	 *  Gets the rowCount attribute of the SongTableModel object
	 *
	 * @return    The rowCount value
	 */
	public int getRowCount() {
		if (searched == null) {
			return songs.size();
		} else {
			return searched.size();
		}
	}


	/**
	 *  Gets the columnCount attribute of the SongTableModel object
	 *
	 * @return    The columnCount value
	 */
	public int getColumnCount() {
		return columnNames.length;
	}


	/**
	 *  Gets the columnClass attribute of the SongTableModel object
	 *
	 * @param  c  Description of the Parameter
	 * @return    The columnClass value
	 */
	public Class getColumnClass(int c) {
	    switch (c) {
	    case 0:
	        return new Integer(0).getClass();
	    case 4:
	        return new Integer(0).getClass();
	    case 5:
	        return new Integer(0).getClass();
	    default:
	        return new String().getClass();
                
		}
	}


	/**
	 *  Gets the valueAt attribute of the SongTableModel object
	 *
	 * @param  rowIndex     Description of the Parameter
	 * @param  columnIndex  Description of the Parameter
	 * @return              The valueAt value
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
	    if (songs == null) {
	        System.out.println("songs == null");
	        return "";
	    }
	    if (searched == null) {
			return (getColumnObject((Song) songs.get(rowIndex), columnIndex));
		} else {
			return (getColumnObject((Song) searched.get(rowIndex), columnIndex));
		}
	}


	/**
	 *  Gets the totals attribute of the SongTableModel object
	 *
	 * @return    The totals value
	 */
	public String getTotals() {
		int songs        = 0;
		long tme         = 0;
		double sze       = 0;
		for (int i = 0; i < getRowCount(); i++) {
			Song s  = getSongAt(i);
			songs++;
			tme = tme + s.time;
			sze = sze + s.size;
		}
		String totals    = new String();
		totals = totals.concat(songs + " songs, ");

		double secs      = tme / 1000;
		double yrs       = secs / 31556926;
		String years     = String.valueOf(yrs);
		double round     = (double) Math.round(yrs * 1000) / (double) 1000;
		totals = totals.concat(round + " years, ");
		double gigs      = sze / 1073741824;
		double giground  = (double) Math.round(gigs * 100) / (double) 100;
		totals = totals.concat(giground + " GB");
		return totals;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 * @return    Description of the Return Value
	 */
	public int indexOf(Song s) {
		if (searching() == true) {
			return -1;
		}
		if (searched == null) {
			return songs.indexOf(s);
		} else {
			return searched.indexOf(s);
		}
	}


	public Collection getSongs() {
	    if (searched == null)
	        return songs;
	    else
	        return searched;
	}
	
	/**
	 *  Gets the songAt attribute of the SongTableModel object
	 *
	 * @param  row  Description of the Parameter
	 * @return      The songAt value
	 */
	public Song getSongAt(int row) {
		if (searched == null) {
			return (Song) songs.get(row);
		} else {
			return (Song) searched.get(row);
		}
	}

	/**
	 *  Gets the columnObject attribute of the SongTableModel object
	 *
	 * @param  s            Description of the Parameter
	 * @param  columnIndex  Description of the Parameter
	 * @return              The columnObject value
	 */
	public Object getColumnObject(Song s, int columnIndex) {
	    switch (columnIndex) {
						case 0:
							if (s.track == 0 || s.track == -1)
							    return null;
						    return new Integer(s.track);
						case 1:
							if (s.artist.length() == 0) {
								return null;
							}
							return s.artist;
						case 2:
							if (s.album.length() == 0) {
								return null;
							}
							return s.album;
						case 3:
							if (s.name.length() == 0) {
								return null;
							}
							return s.name;
						case 4:
							return new Integer(s.time);
						case 5:
							return new Integer(s.size);
						case 6:
							return Integer.toString(s.id);
						case 7:
							return s.format;
		}
		return new String("Unidentified object");
	}
}
