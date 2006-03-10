/* 
 Download Table Model
 *
 *Greg Jordan
 */
package org.git.client.swing;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import org.git.client.Song;

/**
 * @author jbarnett
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DownloadTableModel extends AbstractTableModel{
	public final String[] columnNames = {"#","Filename"};
        public ArrayList ds;    // download*ed* songs
        public int num;
        
	public DownloadTableModel() {
		super();
                ds = new ArrayList();
                num = 0;
	}
	
	public String getColumnName(int col) {
		return columnNames[col];
	}
	
	public void clear() {
		ds = new ArrayList();
	}
	
	public void addSong(Song s) {
                ds.add(s);
                fireTableDataChanged();
	}
	
	public void removeRow(int s) {
	    if (ds.size() > s) {
	        ds.remove(s);
	    // if s < num decrease num
	    if (s < num)
	        num --;
	    
	    // if s > num decrease num?
	    fireTableDataChanged();
	    }
	}
	
        public Song getSongAt(int row) {
            return (Song)ds.get(row);
        }
        
	public int getRowCount() {
		return ds.size();
	}

        public Class getColumnClass(int row, int col) {
            return getValueAt(row, col).getClass();
        }
        
	public int getColumnCount() {
		return columnNames.length;
	}

	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		try
                {
                Song temp = (Song)ds.get(rowIndex);
                switch (columnIndex)
                {
                    case 0:
                        return new Integer(rowIndex+1);
                    case 1:
                            return temp;
                    case 2:
                        return temp.getFormat();
                }
                }
                catch (Exception e){
                    return "oops";
                }
                return "";
	}
}