/*
 * MyTableModel.java
 *
 * Created on June 20, 2004, 5:18 PM
 */

package org.git.client.swing;

import java.util.Collection;

import javax.swing.table.AbstractTableModel;

import org.git.client.Song;
/**
 *
 * @author  Greg
 */
public abstract class MyTableModel extends AbstractTableModel {
    public abstract int indexOf(Song s);
    public abstract Song getSongAt(int row);
    public abstract Collection getSongs();
}
