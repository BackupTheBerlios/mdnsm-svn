/*
 * Created on Feb 6, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.git;

import java.util.ArrayList;

/**
 * @author Greg
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StringPairList {
    
    protected ArrayList pairs;
    
    public StringPairList() {
        pairs = new ArrayList();
    }
    
    public void addPair(String a, String b) {
        pairs.add(new StringPair(a,b));
    }
    
    public StringPair getPair(int i) {
        return (StringPair)pairs.get(i);
    }
    
    public int size() {
        return pairs.size();
    }
    
}
