/*
 * Created on Mar 9, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.git.client.local;

import java.io.Writer;

import org.xml.sax.SAXException;

import com.megginson.sax.DataWriter;

/**
 * @author Greg
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ITunesXMLWriter extends DataWriter {

    public ITunesXMLWriter(Writer w) {
        super(w);
    }
    
    public void key(String s) throws SAXException {
        this.dataElement("key",s);
    }
    
    public void string(String name, String data) throws SAXException {
        this.key(name);
        this.dataElement("string",data);
    }
    
    public void integer(String name,int i) throws SAXException {
        this.key(name);
        this.dataElement("integer",String.valueOf(i));
    }
    
    public void bool(String name, boolean val) throws SAXException {
        this.key(name);
        this.emptyElement(String.valueOf(val));
    }
    
}
