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

package de.kapsi.net.daap;

import java.net.InetSocketAddress;

/**
 * Interface for DaapServer Settings
 *
 * @author  Roger Kapsi
 */
public interface DaapConfig {
    
    /**
     * HTTP style name for the Server
     * 
     * @return the HTTP style name of the Server (e.g. DaapServer/0.1)
     */    
    public String getServerName();
    
    /**
     * An InetSocketAddress (IP:Port) to which the DAAP server will be
     * bound
     * 
     * @return an InetSocketAddress
     */    
    public InetSocketAddress getInetSocketAddress();
    
    /**
     * The ServerSocket backlog
     * 
     * @return the Backlog for the ServerSocket
     */    
    public int getBacklog();
    
    /**
     * The maximum number of simultaneous incoming connections. Keep
     * in mind that there will be (in worst case) twice as many connections
     * because each DAAP connection has a separate audio stream
     * 
     * @return the maximum number of connections
     */    
    public int getMaxConnections();
}
