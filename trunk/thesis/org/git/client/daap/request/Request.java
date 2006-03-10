/*
 * Created on May 6, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
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

 *
 *update: Greg Jordan, 2004
 */
package org.git.client.daap.request;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.git.client.daap.DaapHost;
import org.git.client.daap.Hasher;

/**
 * @author jbarnett
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class Request {
    public static boolean debug = false;
    protected DaapHost host;
    protected int response_code;
    protected String response_message;
    public byte[] data;
    protected int offset;
    protected int expectedLength;
    protected String dataType;
    protected ArrayList fieldPairs;
    protected ArrayList mlitIndexes;
    protected ArrayList mlclIndexes;
    protected ArrayList mdclIndexes;
    protected HttpURLConnection httpc;
    protected int access_index;

    // start of song request.
    public Request(DaapHost h) throws BadResponseCodeException,
            PasswordFailedException, IOException {
        host = h;
        response_code = -1;
        dataType = "";
        offset = 0;
        access_index = 2;
        fieldPairs = new ArrayList();
        mlitIndexes = new ArrayList();
        mlclIndexes = new ArrayList();
        mdclIndexes = new ArrayList();
        //		query();
        //		readResponse();
        //		process();
    }

    protected String getRequestString() {
        return "";
    }

    protected void query() throws BadResponseCodeException,
            PasswordFailedException{
        URL url = null;
        try {
            url = new URL("http://" + host.getAddress() + ":" + host.getPort()
                    + "/" + getRequestString());
            httpc = (HttpURLConnection) url.openConnection();
            addRequestProperties();
            try {
                httpc.connect();
            } catch (Exception e) {
                System.out.println("bad connection!");
            }
            response_code = httpc.getResponseCode();
            if (!(response_code == HttpURLConnection.HTTP_OK)
                    && !(response_code == HttpURLConnection.HTTP_PARTIAL)) {
                response_message = httpc.getResponseMessage();
                if (response_code == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    System.out.println(host.getAddress()
                            + " requires password!");
                    throw new PasswordFailedException("" + response_code + ": "
                            + response_message);
                }
                //httpc.disconnect();
                throw new BadResponseCodeException(response_code,
                        response_message + " by "+host.getName());
            }
        } catch (MalformedURLException e) {
            System.out.println("Malformed URL");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    protected void readResponse() throws IOException {
        DataInputStream in = new DataInputStream(httpc.getInputStream());
        int len = httpc.getContentLength();
        if (len == -1) {
            return;
        }
        data = new byte[len];
        in.readFully(data);
    }

    protected void addRequestProperties() {
        httpc.setRequestProperty("User-Agent", "GIT/0.2 (Windows; N)");
        httpc.addRequestProperty("Client-DAAP-Version", "3.0");
        httpc.addRequestProperty("Client-DAAP-Access-Index", String.valueOf(access_index));
        httpc.addRequestProperty("Client-DAAP-Validation", getHashCode(this));
//        httpc.addRequestProperty("Accept-Encoding", "");
        if (host.isPasswordProtected()) {
            //		    System.out.println("password = " + host.getPassword());
            Base64 base64 = new Base64();
            String encodedPassword = base64.encode("Get_It_Together:"
                    + host.getPassword());
            httpc.addRequestProperty("Authorization", "Basic "+encodedPassword);
        }
//        httpc.addRequestProperty("Connection", "Close");
    }

    //	Returns -1 if the server hasn't been contacted yet, or if no response
    //	has been received.
    public int getResponseCode() {
        return response_code;
    }

    //     static methods:
    protected static int readSize(String data) {
        return readSize(data, 4);
    }

    protected static int readSize(String data, int j) {
        String elength = "";
        for (int i = 0; i < j; i++) {
            elength += ((int) data.charAt(i) > 15 ? "" : "0")
                    + Integer.toHexString((int) data.charAt(i));
        }
        return Integer.valueOf(elength, 16).intValue();
    }

    protected String getHashCode(Request r) {
        return Hasher.GenerateHash("/" + r.getRequestString(), this, false);
    }

    protected String dataString(int i) {
        return readString(data, offset, i);
    }

    public static String readString(byte[] data, int offset, int i) {
        String a = "";
        try {
            a = new String(data, offset, i, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return a;
    }

    protected int dataInt() {
        return readInt(data, offset, 4);
    }

    protected static int readInt(byte[] data, int offset) {
        int i = 0;
        try {
            ByteArrayInputStream b = new ByteArrayInputStream(data, offset, 4);
            DataInputStream d = new DataInputStream(b);
            i = d.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return i;
    }

    public DaapHost getHost() {
        return host;
    }
    
    public int getAccessIndex() {
        return access_index;
    }
    
    public static int readInt(byte[] data, int offset, int size) {
        int i = 0;
        try {
            ByteArrayInputStream b = new ByteArrayInputStream(data, offset,
                    size);
            DataInputStream d = new DataInputStream(b);
            int pow = size * 2 - 1;
            for (int j = 0; j < size; j++) {
                int num = (0xFF & d.readByte());
                int up = ((int) Math.pow(16, pow)) * (num / 16);
                pow--;
                int down = ((int) Math.pow(16, pow)) * (num % 16);
                i += up + down;
                pow--;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return i;
    }

    protected void process() {
        if (data.length == 0) {
            System.err.println("zero length data!");
            return;
        }
        dataType = dataString(4);
        offset += 4;
        int size = dataInt();
        offset += 4;
        fieldPairs = processDataFields();
    }

    protected ArrayList processDataFields(byte[] data, int offset) {
        ArrayList fieldPairs = new ArrayList();
        while (offset < data.length) {
            String name = "";
            name = readString(data, offset, 4);
            offset += 4;
            int size = readInt(data, offset);
            offset += 4;
            if (size > 10000000)
                System.out.println("This host probably uses gzip encoding");
            FieldPair fp = new FieldPair(name, data, offset, size);
            offset += size;
            fieldPairs.add(fp);
            if (name.equals("mlcl")) {
                mlclIndexes.add(new Integer(fieldPairs.size() - 1));
            } else if (name.equals("mlit")) {
                mlitIndexes.add(new Integer(fieldPairs.size() - 1));
            } else if (name.equals("mdcl")) {
                mdclIndexes.add(new Integer(fieldPairs.size() - 1));
            }
        }
        return fieldPairs;
    }

    protected ArrayList processDataFields() {
        return processDataFields(data, offset);
        //		ArrayList fieldPairs = new ArrayList();
        //		while (offset < data.length) {
        //			String name="";
        //			name = dataString(4);
        //			offset +=4;
        //			int size=dataInt();
        //			offset+=4;
        //			FieldPair fp = new FieldPair(name, data, offset, size);
        //			offset += size;
        //			fieldPairs.add(fp);
        //			if (name.equals("mlcl")) {
        //				mlclIndexes.add(new Integer(fieldPairs.size()-1));
        //			} else if (name.equals("mlit")) {
        //				mlitIndexes.add(new Integer(fieldPairs.size()-1));
        //			} else if (name.equals("mdcl")) {
        //				mdclIndexes.add(new Integer(fieldPairs.size()-1));
        //			}
        //		}
        //		return fieldPairs;
    }

    public String toString() {
        String ret = "";
        for (int i = 0; i < fieldPairs.size(); i++) {
            FieldPair fp = (FieldPair) fieldPairs.get(i);
            ret += fp.toString() + "\n";
        }
        return ret;
    }
}