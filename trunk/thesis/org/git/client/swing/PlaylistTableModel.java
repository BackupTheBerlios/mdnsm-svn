/*
 * Created on May 8, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 * 
 * Copyright 2003 Joseph Barnett

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


import java.util.ArrayList;
import java.util.Collection;

import org.git.client.Song;

/**
 * @author jbarnett
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PlaylistTableModel extends MyTableModel {
	public final String[] columnNames = {"Tr.", "Artist", "Title","Length"};
	//private ArrayList data;
        private GetItTogether prog;
        protected ArrayList playlist;
	
	public PlaylistTableModel(GetItTogether p) {
		super();
		prog = p;
                playlist = new ArrayList();
	}
	
	public String getColumnName(int col) {
		return columnNames[col];
	}
	
        public boolean isCellEditable(int row, int col) {
            if (prog.playingSong == getSongAt(row) && getColumnName(col) == "Title")
            {
                return true;
            }
            else
                return false;
        }
        
	public void clear() {
		playlist.clear();
	}
	
        
	public int getRowCount() {
		return playlist.size();
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

        public int indexOf(Song s) {
            return playlist.indexOf(s);
        }
        
	public Object getValueAt(int rowIndex, int columnIndex) {
		return (getColumnObject((Song)playlist.get(rowIndex),columnIndex));
	}

	public Collection getSongs() {
	    return playlist;
	}
	
    public Song getSongAt(int row)
    {
        return (Song)playlist.get(row);        
    }
    
    public Object getColumnObject(Song s, int columnIndex){
        switch (columnIndex)
            {
                case 0:
                    return new Integer(s.track);
                case 1:
                    return s.artist;
                case 2:
                    return s.name;
                case 3:
                    return new Integer(s.time);
        }
        return new String("Unidentified object");
}
}
