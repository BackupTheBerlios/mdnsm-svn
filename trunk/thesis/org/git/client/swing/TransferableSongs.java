/*
 * TransferableSongs.java
 *
 * Created on June 5, 2004, 1:37 PM
 */

package org.git.client.swing;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import org.git.client.Song;
/**
 *
 * @author  greg
 */
public class TransferableSongs implements Serializable, Transferable {
    
    final public static DataFlavor SONGS_FLAVOR = new DataFlavor(Song.class, "Song");
    static DataFlavor flavors[] = {SONGS_FLAVOR };
    public ArrayList songs;
    public int[] rows;
    
    public TransferableSongs(ArrayList s, int[] r)
    {
        songs = s;
        rows = r;
    }
    
    public boolean isDataFlavorSupported(DataFlavor df) {
        return df.equals(SONGS_FLAVOR);
    }

    public Object getTransferData(DataFlavor df) throws UnsupportedFlavorException, IOException {
        if (df.equals(SONGS_FLAVOR)) {
            return rows;
        }
        else throw new UnsupportedFlavorException(df);
    }

    public int[] getRows() {
        return rows;
    }
    
    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

   private void writeObject(java.io.ObjectOutputStream out) throws IOException {
     out.defaultWriteObject();
   }

   private void readObject(java.io.ObjectInputStream in)
     throws IOException, ClassNotFoundException {
     in.defaultReadObject();
   }

}