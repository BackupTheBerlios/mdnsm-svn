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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.httpclient.Header;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.kapsi.net.daap.chunks.Chunk;

/**
 * Misc methods and constants
 *
 * @author  Roger Kapsi
 */
public final class DaapUtil {
    
    /**
     * NULL value (Zero) is a forbidden value (in some cases) in 
     * DAAP and means that a value is not initialized (basically 
     * <code>null</code> for primitive types).
     */
    public static final int NULL = 0;
    
    /**
     * Global flag to turn gzip compression on and off
     */
    public static final boolean COMPRESS = false;
    
    private static final byte[] CRLF = { (byte)'\r', (byte)'\n' };
    private static final String ISO_8859_1 = "ISO-8859-1";
    
    private static final Log LOG = LogFactory.getLog(DaapUtil.class);
    
    private final static SimpleDateFormat formatter = 
        new SimpleDateFormat("EEE, d MMM yyyy hh:mm:ss z", Locale.US);
    
    private final static Random generator = new Random();
    
    static {
        // warm up...
        for(int i = 0; i < 100; i++) {
            generator.nextInt();
        }
    }
    
    /**
     * A hard coded list of Song attributes.
     */
    public static final String[] DATABASE_SONGS_META = {
        "dmap.itemkind",
        "daap.songalbum",
        "daap.songgrouping",
        "daap.songartist",
        "daap.songbeatsperminute",
        "daap.songbitrate",
        "daap.songcomment",
        "daap.songcompilation",
        "daap.songcomposer",
        "daap.songdateadded",
        "daap.songdatemodified",
        "daap.songdisccount",
        "daap.songdiscnumber",
        "daap.songdatakind",
        "daap.songformat",
        "daap.songeqpreset",
        "daap.songgenre",
        "dmap.itemid",
        "daap.songdescription",
        "dmap.itemname",
        "com.apple.itunes.norm-volume",
        "dmap.persistentid",
        "daap.songdisabled",
        "daap.songrelativevolume",
        "daap.songsamplerate",
        "daap.songsize",
        "daap.songstarttime",
        "daap.songstoptime",
        "daap.songtime",
        "daap.songtrackcount",
        "daap.songtracknumber",
        "daap.songuserrating",
        "daap.songyear",
        "dmap.containeritemid",
        "daap.songdataurl"
    };
    
    /**
     * A hard coded list of Playlist attributes.
     */
    public static final String[] DATABASE_PLAYLISTS_META = {
        "dmap.itemid",
        "dmap.persistentid",
        "dmap.itemname",
        "com.apple.itunes.smart-playlist",
        "dmap.itemcount"
    };
    
    /**
     * A hard coded list of Playlist/Song attributes.
     */
    public static final String[] PLAYLIST_SONGS_META = {
        "dmap.itemkind",
        "dmap.itemid",
        "dmap.containeritemid"
    };
    
    /** 1.0.0 (iTunes 4.0) */
    public static final int VERSION_1 = 0x00010000; // 1.0.0
    
    /** 2.0.0 (iTunes 4.1, 4.2) */
    public static final int VERSION_2 = 0x00020000; // 2.0.0
    
    /** Version 3.0.0 (iTunes 4.5, 4.6) */
    public static final int VERSION_3 = 0x00030000; // 3.0.0
    
    private static final String CLIENT_DAAP_VERSION = "Client-DAAP-Version";
    private static final String USER_AGENT = "User-Agent";
    
    private DaapUtil() {
    }
    
    /** 
     * Returns <code>true</code> if version is a supported protocol
     * version. At the moment only {@see #VERSION_3} and later are
     * supported.
     * 
     * @param version a protocol version
     * @return <code>true</code> if version is a supported
     */
    public static boolean isSupportedProtocolVersion(int version) {
        if (version >= VERSION_3) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Converts a four character content code to an int and returns it.
     * 
     * @param contentCode a four character content code
     * @return content code
     */
    public static int toContentCodeNumber(String contentCode) {
        if (contentCode.length() != 4) {
            throw new IllegalArgumentException("content code must have 4 characters!");
        }
        
        try {
            byte[] chars = contentCode.getBytes("UTF-8");
            return ByteUtil.toIntBE(chars, 0);
        } catch (UnsupportedEncodingException err) {
            LOG.error(err);
            return 0;
        }
    }
    
    /**
     * Returns the current Date/Time in "iTunes time format"
     */
    public static final String now() {
        return formatter.format(new Date());
    }
    
    /**
     * Serializes the <code>chunk</code> and compresses it optionally.
     * The serialized data is returned as a byte-Array.
     */
    public static final byte[] serialize(Chunk chunk, boolean compress) throws IOException {
        
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(chunk.getSize());
        
        if (compress) {
            GZIPOutputStream gzip = new GZIPOutputStream(buffer);
            chunk.serialize(gzip);
            gzip.finish();
            gzip.close();
        } else {
            chunk.serialize(buffer);
            buffer.flush();
            buffer.close();
        }
        
        return buffer.toByteArray();
    }
    
    /**
     * Splits a query String ("key1=value1&key2=value2...") and
     * stores the data in a Map
     * 
     * @param queryString a query String
     * @return the splitten query String as Map
     */
    public static final Map parseQuery(String queryString) {
        
        Map map = new HashMap();
        
        if (queryString != null && queryString.length() != 0) {
            StringTokenizer tok = new StringTokenizer(queryString, "&");
            while(tok.hasMoreTokens()) {
                String token = tok.nextToken();
                
                int q = token.indexOf('=');
                if (q != -1 && q != token.length()) {
                    String key = token.substring(0, q);
                    String value = token.substring(++q);
                    map.put(key, value);
                }
            }
        }
        
        return map;
    }
    
    /**
     * Splits a meta String ("foo,bar,alice,bob") and stores the data
     * in an ArrayList
     * 
     * @param meta a meta String
     * @return the splitten meta String as ArrayList
     */
    public static final ArrayList parseMeta(String meta) {
        StringTokenizer tok = new StringTokenizer(meta, ",");
        ArrayList list = new ArrayList(tok.countTokens());
        boolean flag = false;
        
        while(tok.hasMoreTokens()) {
            String token = tok.nextToken();
            
            // Must be te fist! See DAAP documentation 
            // for more info!
            if (!flag && token.equals("dmap.itemkind")) {
                list.add(0, token);
                flag = true;
            } else {
                list.add(token);
            }
        }
        return list;
    }
    
    /**
     * Creates and returns an unique session ID
     * 
     * @param knownIDs all known session IDs
     * @return a uniquie session id
     */
    public static Integer createSessionId(Set knownIDs) {
        Integer sessionId = null;
        
        while(sessionId == null || knownIDs.contains(sessionId)) {
            int tmp = generator.nextInt();
            
            if (tmp == 0) {
                continue;
            } else if (tmp < 0) {
                tmp = -tmp;
            }
            
            sessionId = new Integer(tmp);
        }
        
        return sessionId;
    }
    
    /**
     * Converts major, minor to a DAAP version.
     * Version 2 is for example 0x00020000
     * 
     * @param major the major version (x)
     * @return x.0.0
     */
    public static int toVersion(int major) {
        return toVersion(major, 0, 0);
    }
    
    /**
     * Converts major, minor to a DAAP version.
     * Version 2.1 is for example 0x00020100
     * 
     * @param major the major version (x)
     * @param minor the minor version (y)
     * @return x.y.0
     */
    public static int toVersion(int major, int minor) {
        return toVersion(major, minor, 0);
    }
    
    /**
     * Converts major, minor and patch to a DAAP version.
     * Version 2.1.3 is for example 0x00020103
     * 
     * @param major the major version (x)
     * @param minor the minor version (y)
     * @param patch the patch version (z)
     * @return x.y.z
     */
    public static int toVersion(int major, int minor, int patch) {
        byte[] dst = new byte[4];
        ByteUtil.toByte16BE((major & 0xFFFF), dst, 0);
        dst[2] = (byte)(minor & 0xFF);
        dst[3] = (byte)(patch & 0xFF);
        return ByteUtil.toIntBE(dst, 0);
    }
    
    /**
     * This method tries the determinate the protocol version
     * and returns it or {@see #NULL} if version could not be
     * estimated...
     */
    public static int getProtocolVersion(DaapRequest request) {
        
        if (request.isUnknownRequest())
            return DaapUtil.NULL;
        
        Header header = request.getHeader(CLIENT_DAAP_VERSION);
        
        if (header == null && request.isSongRequest()) {
            header = request.getHeader(USER_AGENT);
        }
        
        if (header == null)
            return DaapUtil.NULL;
        
        String name = header.getName();
        String value = header.getValue();

        // Unfortunately song requests do not have a Client-DAAP-Version
        // header. As a workaround we can estimate the protocol version
        // by User-Agent but that is weak an may break with non iTunes
        // hosts...
        if ( request.isSongRequest() && name.equals(USER_AGENT)) {
            
            // Note: the protocol version of a Song request is estimated
            // by the server with the aid of the sessionId, i.e. this block
            // is actually never touched...
            if (value.startsWith("iTunes/4.5") || value.startsWith("iTunes/4.6"))
                return DaapUtil.VERSION_3;
            else if (value.startsWith("iTunes/4.2") || value.startsWith("iTunes/4.1"))
                return DaapUtil.VERSION_2;
            else if (value.startsWith("iTunes/4.0"))
                return DaapUtil.VERSION_1;
            else
                return DaapUtil.NULL;
            
        } else {
            
            StringTokenizer tokenizer = new StringTokenizer(value, ".");
            int count = tokenizer.countTokens();
            
            if (count >= 2 && count <= 3) {
                try {

                    int major = DaapUtil.NULL;
                    int minor = DaapUtil.NULL;
                    int patch = DaapUtil.NULL;

                    major = Integer.parseInt(tokenizer.nextToken());
                    minor = Integer.parseInt(tokenizer.nextToken());

                    if (count == 3)
                        patch = Integer.parseInt(tokenizer.nextToken());

                    return DaapUtil.toVersion(major, minor, patch);

                } catch (NumberFormatException err) {
                }
            }
        }
        
        return DaapUtil.NULL;
    }
}
