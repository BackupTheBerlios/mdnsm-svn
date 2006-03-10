/*
 * DisplayTableModel.java
 *
 * Created on June 26, 2004, 2:55 PM
 */
package org.git.client.swing;

import java.util.Collection;
import java.util.Vector;

import org.git.client.Song;


/**
 *
 * @author  Greg
 */
public class DisplayTableModel extends MyTableModel{
    GetItTogether prog;
    /** Creates a new instance of DisplayTableModel */
    
    public DisplayTableModel(GetItTogether p) {
        super();
        prog = p;
    }
    
    public Song getSongAt(int i) {
        if (prog.playingSong != null)
            return prog.playingSong;
        else
            return new Song();
    }
    
    public int getColumnCount() {
        return 1;
    }
    
    public int getRowCount() {
            return 1;
    }
    
    public boolean isCellEditable(int row, int col) {
            if (prog.playingSong != null)
                return true;
            else
                return false;
        }
    
    public String getColumnName(int col) {
        return "Song";
    }
    
    public Object getValueAt(int rowIndex, int columnIndex) {
        Song s = null;
        if (prog.playingSong != null)
            s = prog.playingSong;
        else
            s = new Song();
        return s;
    }
    
    public Collection getSongs() {
        return new Vector();
    }
    
    public int indexOf(Song s) {
        if (s.equals(prog.playingSong))
            return 0;
        else
            return -1;
    }
    
}
