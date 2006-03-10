/*
    Created on May 6, 2003
    To change the template for this generated file go to
    Window>Preferences>Java>Code Generation>Code and Comments
    Copyright 2003 Joseph Barnett
    This File is part of "one 2 oh my god"
    "one 2 oh my god" is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    Free Software Foundation; either version 2 of the License, or
    your option) any later version.
    "one 2 oh my god" is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    You should have received a copy of the GNU General Public License
    along with "one 2 oh my god"; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
  */
package org.git.client.daap.request;

import java.io.IOException;
import java.util.ArrayList;

import org.git.client.daap.DaapHost;
import org.git.client.daap.DaapSong;

/**
 * @author     jbarnett To change the template for this generated type comment
 *      go to Window>Preferences>Java>Code Generation>Code and Comments
 * @created
 */
public class SingleDatabaseRequest extends DatabasesRequest {

	// protected boolean println  = false;

	public SingleDatabaseRequest(DaapHost h) throws BadResponseCodeException, PasswordFailedException, IOException {
		super(h);
	}


	protected String getRequestString() {
	    String ret = "databases/";
	    ret += host.getDatabaseID() + "/";
	    ret += "items?";
	    ret += "type=music&";
	    ret += "meta=dmap.itemid,dmap.itemname,daap.songalbum,daap.songartist,daap.songtracknumber,daap.songgenre,daap.songformat,daap.songtime,daap.songsize,daap.songbitrate,daap.songcompilation";
        ret += "&session-id=" + host.getSessionID();
        ret += "&revision-number=" + host.getRevisionNumber();
        return ret;
	}
	
	/**
	 *  Gets the songs attribute of the SingleDatabaseRequest object
	 *
	 * @param  h  Description of the Parameter
	 * @return    The songs value
	 */
	public ArrayList getSongs() {
		ArrayList songs  = new ArrayList(mlitDataFields.size());
		for (int i = 0; i < mlitDataFields.size(); i++) {
			ArrayList fps  = ((ArrayList) mlitDataFields.get(i));
			String name    = null;
			int id         = 0;
			DaapSong s         = new DaapSong();
				s.host = host;
			for (int j = 0; j < fps.size(); j++) {
				FieldPair fp  = ((FieldPair) fps.get(j));
				if (fp.name.equals("miid")) {
					s.id = Request.readInt(fp.value, 0, 4);
				} else if (fp.name.equals("minm")) {
					s.name = Request.readString(fp.value, 0, fp.value.length).trim();
				} else if (fp.name.equals("mper")) {
				    s.persistent_id = Request.readString(fp.value, 0, fp.value.length).trim();
				} else if (fp.name.equals("asal")) {
					s.album = Request.readString(fp.value, 0, fp.value.length).trim();
				} else if (fp.name.equals("asar")) {
					s.artist = Request.readString(fp.value, 0, fp.value.length).trim();
				} else if (fp.name.equals("astn")) {
					s.track = Request.readInt(fp.value, 0, 2);
				} else if (fp.name.equals("asgn")) {
					s.genre = Request.readString(fp.value, 0, fp.value.length);
				} else if (fp.name.equals("asfm")) {
					s.format = Request.readString(fp.value, 0, fp.value.length);
				} else if (fp.name.equals("astm")) {
					s.time = Request.readInt(fp.value, 0, 4);
				} else if (fp.name.equals("assz")) {
					s.size = Request.readInt(fp.value, 0, 4);
				} else if (fp.name.equals("asco")) {
				    s.compilation = (Request.readInt(fp.value, 0,1) == 1);
				} else if (fp.name.equals("asbr")) {
				    s.bitrate = Request.readInt(fp.value,0,2);
				}
				
			}
			songs.add(s);
		}
		return songs;
	}
}

