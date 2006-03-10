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
import org.git.client.daap.DaapPlaylist;

/**
 * @author     jbarnett To change the template for this generated type comment
 *      go to Window>Preferences>Java>Code Generation>Code and Comments
 * @created    August 6, 2004
 */
public class PlaylistsRequest extends Request {

	protected ArrayList mlclDataFields;
	protected ArrayList mlitDataFields;
	protected boolean println           = true;


	/**
	 *  Constructor for the PlaylistRequest object
	 *
	 * @param  server                           Description of the Parameter
	 * @param  port                             Description of the Parameter
	 * @param  sessionId                        Description of the Parameter
	 * @param  revNum                           Description of the Parameter
	 * @param  status                           Description of the Parameter
	 * @exception  NoServerPermissionException  Description of the Exception
	 * @throws PasswordFailedException
	 * @throws IOException
	 * @throws BadResponseCodeException
	 */
	public PlaylistsRequest(DaapHost h) throws NoServerPermissionException, BadResponseCodeException, PasswordFailedException, IOException {
		super(h);
		System.err.println(1);
		query();
		System.err.println(2);
		readResponse();
		System.err.println(3);
		process();
		System.err.println(4);
	}
	

    protected String getRequestString() {
        String ret = "databases/";
        ret += host.getDatabaseID() +"/";
        ret += "containers?";
        ret += "meta=dmap.itemid,dmap.itemname,dmap.persistentid,com.apple.itunes.smart-playlist";
        ret += "&session-id=" + host.getSessionID();
        ret += "&revision-number=" + host.getRevisionNumber();
        return ret;
    }

    protected void addRequestProperties() {
        super.addRequestProperties();
    }
    
	protected void process() {
		super.process();
		parseMLCL();
	}

	protected void parseMLCL() {
		for (int i = 0; i < mlclIndexes.size(); i++) {
			byte[] mlclData  = ((FieldPair) fieldPairs.get(((Integer) mlclIndexes.get(i)).intValue())).value;
			mlclDataFields = this.processDataFields(mlclData, 0);
		}
		parseMLIT();
	}

	protected void parseMLIT() {
		mlitDataFields = new ArrayList();
		for (int i = 0; i < mlitIndexes.size(); i++) {
			byte[] mlitData  = ((FieldPair) mlclDataFields.get(((Integer) mlitIndexes.get(i)).intValue())).value;
			mlitDataFields.add(processDataFields(mlitData, 0));
		}
	}

	/**
	 *  Gets the libraryCount attribute of the DatabasesRequest object
	 *
	 * @return    The libraryCount value
	 */
	public int getPlaylistCount() {
		return mlclDataFields.size();
	}

	public ArrayList getPlaylists() {
	    ArrayList playlists = new ArrayList();
	    for (int i=0; i < mlitDataFields.size(); i++)	// for every playlist item
	    {
	        ArrayList currMlitDataFields  = ((ArrayList) mlitDataFields.get(i));
            DaapPlaylist p = new DaapPlaylist(host);
	        for (int j=0; j < currMlitDataFields.size(); j++) {
	            FieldPair fp = (FieldPair) currMlitDataFields.get(j);
	            if (fp.name.equals("minm"))
	                p.name = Request.readString(fp.value, 0, fp.value.length);
                else if (fp.name.equals("miid"))
                    p.id = Request.readInt(fp.value, 0);
                else if (fp.name.equals("mper"))
                    p.persistent_id = Request.readString(fp.value, 0, fp.value.length);
                else if (fp.name.equals("mimc"))
                    p.song_count = Request.readInt(fp.value, 0);
                else if (fp.name.equals("aeSP"))
                {
                    p.smart_playlist = true;
                }
	        }
	        if (!p.name.equals(host.getName()))
	            playlists.add(p);
	    }
	    return playlists;
	}	
}