/*
 * Created on Aug 18, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.git.client.daap.request;

import java.io.IOException;
import java.util.ArrayList;

import org.git.client.daap.DaapPlaylist;

/**
 * @author Greg
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SinglePlaylistRequest extends Request {
	protected ArrayList mlclDataFields;
	protected ArrayList mlitDataFields;
	protected DaapPlaylist playlist;

    /**
     * @param h
     * @throws BadResponseCodeException
     * @throws PasswordFailedException
     * @throws IOException
     */
    public SinglePlaylistRequest(DaapPlaylist p) throws BadResponseCodeException, PasswordFailedException, IOException {
        super(p.getHost());
        playlist = p;
        query();
		readResponse();
		process();
        // TODO Auto-generated constructor stub
    }
    
    protected String getRequestString() {
        String ret = "databases/"+host.getDatabaseID() +"/";
        ret += "containers/"+playlist.getId()+"/";
        ret += "items?";
        ret += "meta=dmap.itemid,dmap.containeritemid";
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


	/**  Description of the Method */
	protected void parseMLCL() {
		for (int i = 0; i < mlclIndexes.size(); i++) {
			byte[] mlclData  = ((FieldPair) fieldPairs.get(((Integer) mlclIndexes.get(i)).intValue())).value;
			mlclDataFields = this.processDataFields(mlclData, 0);
		}
		parseMLIT();
	}


	/**  Description of the Method */
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
	public int getSongCount() {
		return mlclDataFields.size();
	}

	public ArrayList getSongs() {
	    ArrayList songs = new ArrayList();
	    for (int i=0; i < mlitDataFields.size(); i++)	// for every playlist item
	    {
	        ArrayList currMlitDataFields  = ((ArrayList) mlitDataFields.get(i));
	        int song_id = 0;
	        for (int j=0; j < currMlitDataFields.size(); j++) {
	            FieldPair fp = (FieldPair) currMlitDataFields.get(j);
	            if (fp.name.equals("miid"))
                    song_id = Request.readInt(fp.value, 0);
	            
	        }
	        songs.add(host.getSongById(song_id));
	    }
	    return songs;
	}	
}
