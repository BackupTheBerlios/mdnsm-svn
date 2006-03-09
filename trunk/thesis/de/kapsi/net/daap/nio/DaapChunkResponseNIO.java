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

package de.kapsi.net.daap.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import de.kapsi.net.daap.DaapChunkResponse;
import de.kapsi.net.daap.DaapRequest;

/**
 * NIO (New or Non-Blocking I/O) based DaapChunkResponse.
 *
 * @author  Roger Kapsi
 */
public class DaapChunkResponseNIO extends DaapChunkResponse {
    
    private SocketChannel channel;
    
    private ByteBuffer headerBuffer;
    private ByteBuffer dataBuffer;
    
    /** Creates a new instance of DaapChunkResponse */
    public DaapChunkResponseNIO(DaapRequest request, byte[] data) {
        super(request, data);
        
        DaapConnectionNIO connection = (DaapConnectionNIO)request.getConnection();
        channel = connection.getChannel();
        
        headerBuffer = ByteBuffer.wrap(header);
        dataBuffer = ByteBuffer.wrap(data);
    }
    
    public boolean hasRemainig() {
        return headerBuffer.hasRemaining() || dataBuffer.hasRemaining();
    }
    
    /**
     *
     * @throws IOException
     * @return
     */    
    public boolean write() throws IOException {
        
        if (headerBuffer.hasRemaining()) {
            channel.write(headerBuffer);
            
            if (headerBuffer.hasRemaining())
                return false;
        
        }
        
        if (dataBuffer.hasRemaining()) {
            channel.write(dataBuffer);
            return !dataBuffer.hasRemaining();
        }
        
        return true;
    }
}
