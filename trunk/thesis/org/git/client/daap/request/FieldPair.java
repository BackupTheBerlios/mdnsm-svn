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

 */
package org.git.client.daap.request;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.git.client.Song;
/**
 * @author jbarnett
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class FieldPair {
	public String name;
	public byte[] value;
	public byte[] transmission;
	
	public FieldPair(String name, int value) {
		byte[] bytes = new byte[4];
		enterInt(bytes, value, 0);
		init(name,bytes,0,bytes.length);
	}
	
	public FieldPair(String name, Song s, String meta) {
		String[] extraTags = meta.split(",");
		FieldPair miid = new FieldPair("miid", s.id);
		FieldPair minm = new FieldPair("minm", s.getName());
		ArrayList songPairs = new ArrayList();
		songPairs.add(miid); songPairs.add(minm);
		for (int i = 0; i < extraTags.length; i++) {
			// add FieldPairs for each meta tag
			if (!extraTags[i].equals("dmap.itemid") && !extraTags[i].equals("dmap.itemname")) { //already do these	
				FieldPair newPair=new FieldPair(lookup(extraTags[i]), lookup(extraTags[i], s));
				if (!newPair.name.equals("null"))
					songPairs.add(newPair);
			}
		}
		FieldPair[] array = new FieldPair[songPairs.size()];
		System.arraycopy(songPairs.toArray(), 0, array, 0, array.length);
		byte[] bytes = embed(array);
		init(name,bytes,0,bytes.length);
	}
	
	public FieldPair(String name, FieldPair[] fields) {
		byte[] bytes = embed(fields);	
		init(name,bytes,0,bytes.length);
	}
	
	public FieldPair(String name, byte[] value) {
		init(name,value,0,value.length);	
	}
	
	public FieldPair(String name, byte value) {
		byte[] bytes = {value};
		init(name,bytes,0,bytes.length);
	}
	public FieldPair(String name, byte[] value,int offset, int size) {
		init(name,value,offset,size);
	}
	public FieldPair (String name, String value) {
		init(name, value.getBytes(), 0, value.getBytes().length);	
	}
	
	public void init(String name, byte[] value, int offset ,int size) {
		this.name = name;
		this.value = new byte[size];
		try {
			ByteArrayInputStream b = new ByteArrayInputStream(value,offset,size);
			DataInputStream d = new DataInputStream(b);
			d.readFully(this.value);
		} catch (IOException e) {
			e.printStackTrace();
		}
		transmission = getDaapBytes();
	}

	public String toString() {
		String ret = name + " ";
		for (int i = 0; i < value.length;i++) {
			ret+= Integer.toHexString(0xFF&value[i]) + " ";
		}
		ret += "("+ Request.readString(value,0,value.length)+")";

		return ret;
	}
	
	private byte[] getDaapBytes() {
		byte[] bytes = new byte[4+value.length+name.length()];
		System.arraycopy(name.getBytes(), 0, bytes, 0, name.length());
		int length = value.length;
		byte[] lengthBytes = new byte[4];
		lengthBytes[3] = (byte)(length % 256);
		length = length / 256;
		lengthBytes[2] = (byte)(length % 256);
		length = length / 256;
		lengthBytes[1] = (byte)(length % 256);
		length = length / 256;
		lengthBytes[0] = (byte)(length % 256);
		System.arraycopy(lengthBytes,0,bytes, name.length(), 4);
		System.arraycopy(value, 0, bytes, name.length() + 4, value.length);
		return bytes;	
	}
	
	public boolean equals(Object o) {
		if (FieldPair.class.isInstance(o)) {
			return (((FieldPair)o).name.equals(this.name));
		} else if (String.class.isInstance(o)) {
			return ((String)o).equals(this.name);
		} else return false;
	}
	
	public static void enterInt(byte[] data, int value, int offset) {
		int temp = value;
		for (int i = offset; i < offset + 4; i++) {
			data[offset + 3 - i] = (byte)( temp % 256);
			temp = temp / 256;
		}
	}
	
	private static byte[] embed(FieldPair[] fields) {
		int offset = 0;
		int length = 0;
		for (int i = 0; i < fields.length; i++) {
			length += fields[i].transmission.length;	
		}
		byte[] data = new byte[length];
		for (int i = 0; i < fields.length; i++) {
			System.arraycopy(fields[i].transmission, 0, data, offset, fields[i].transmission.length);
			offset += fields[i].transmission.length;
		}
		return data;
	}
	
	private static String lookup(String meta) {
		if (meta.equals("daap.songalbum")) {
			return "asal";	
		} else if (meta.equals("daap.songartist")) {
			return "asar";	
		} else if (meta.equals("daap.songtracknumber")) {
			return "astn";	
		} else if (meta.equals("daap.songuserrating")) {
			return "asur";	
		} else if (meta.equals("daap.songgenre")) {
			return "asgn";	
		} else if (meta.equals("daap.songformat")) {
			return "asfm";	
		} else if (meta.equals("daap.songtime")) {
			return "astm";
                } else if (meta.equals("daap.songsize")) {
                        return "assz";
		}
		return "null";
	}
	
	private static byte[] lookup(String meta, Song s) {
		if (meta.equals("daap.songalbum")) {
			return s.getAlbum().getBytes();	
		} else if (meta.equals("daap.songartist")) {
			return s.getArtist().getBytes();	
		} else if (meta.equals("daap.songtracknumber")) {
			byte[] intBytes = new byte[2];
			intBytes[0] = (byte)(s.getTrack() / 256);
			intBytes[1] = (byte)(s.getTrack() % 256);
			return intBytes;	
		} else if (meta.equals("daap.songuserrating")) {
			byte[] bytes = {(byte)0};
			return bytes;	
		}else if (meta.equals("daap.songgenre")) {
			return s.getGenre().getBytes();	
		} else if (meta.equals("daap.songformat")) {
			return "mp3".getBytes();	
		} else if (meta.equals("daap.songtime")) {
			byte[] intBytes = new byte[4];
			enterInt(intBytes, s.getTime(), 0);
			return intBytes;
                } else if (meta.equals("daap.songsize")) {
                        byte[] intBytes = new byte[4];
                        enterInt(intBytes, s.getSize(), 0);
                        return intBytes;
		}
		return new byte[0];	
	}
}

