/* 
 * Digital Audio Access Protocol (DAAP)
 * Copyright (C) 2004 Roger Kapsi, info at kapsi dot de
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package de.kapsi.net.daap.chunks;

import java.io.OutputStream;
import java.io.IOException;

/**
 * An implementation of a boolean chunk.
 *
 * @author  Roger Kapsi
 */
public class BooleanChunk extends AbstractChunk {
    
    private boolean value;
    
    protected BooleanChunk(String type, String name, boolean value) {
        super(type, name);
        this.value = value;
    }
    
    public boolean getValue() {
        return value;
    }
    
    public void setValue(boolean value) {
        this.value = value;
    }
    
    /**
     * Length is 1 byte
     */
    public int getLength() {
        return 1;
    }
    
    /**
     * Returns <tt>Chunk.BOOLEAN_TYPE</tt>
     */
    public int getType() {
        return Chunk.BOOLEAN_TYPE;
    }
    
    public void serialize(OutputStream out) throws IOException {
        super.serialize(out);
        out.write((getValue()) ? (byte)1 : (byte)0);
    }
    
    public String toString() {
        return super.toString() + "=" + value;
    }
}
