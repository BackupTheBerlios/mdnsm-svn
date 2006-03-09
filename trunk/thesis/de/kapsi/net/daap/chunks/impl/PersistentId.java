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

package de.kapsi.net.daap.chunks.impl;

import de.kapsi.net.daap.chunks.LongChunk;
import java.math.BigInteger;

/**
 * In theory used for global unique IDs and afaik only required
 * for the <tt>/resolve</tt> request.
 *
 * @author  Roger Kapsi
 */
public class PersistentId extends LongChunk {
    
    public PersistentId() {
        this(0L);
    }
    
    public PersistentId(long id) {
        super("mper", "dmap.persistentid", id);
    }
    
    public PersistentId(String id) {
        super("mper", "dmap.persistentid", id);
    }
    
    public PersistentId(BigInteger id) {
        super("mper", "dmap.persistentid", id);
    }
}
