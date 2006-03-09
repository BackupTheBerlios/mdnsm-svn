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

import de.kapsi.net.daap.chunks.IntChunk;

/**
 * The number of items (e.g. the number of Songs in a Playlist)
 * a DAAP response has. This chunk usually appears together with
 * SpecifiedTotalCount.
 * 
 * @see SpecifiedTotalCount
 * @author  Roger Kapsi
 */
public class ReturnedCount extends IntChunk {
    
    public ReturnedCount() {
        this(0);
    }
    
    public ReturnedCount(int count) {
        super("mrco", "dmap.returnedcount", count);
    }
}
