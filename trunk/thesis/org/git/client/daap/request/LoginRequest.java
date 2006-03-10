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

import org.git.client.daap.DaapHost;
/**
 * @author     jbarnett To change the template for this generated type comment
 *      go to Window>Preferences>Java>Code Generation>Code and Comments
 * @created    July 15, 2004
 */
public class LoginRequest extends Request {

	/**
	 *  Constructor for the LoginRequest object
	 *
	 * @param  server                           Description of the Parameter
	 * @param  port                             Description of the Parameter
	 * @param  status                           Description of the Parameter
	 * @param  password                         Description of the Parameter
	 * @throws BadResponseCodeException
	 * @throws PasswordFailedException
	 * @throws IOException
	 * @exception  NoServerPermissionException  Description of the Exception
	 */
	public LoginRequest(DaapHost h) throws BadResponseCodeException, PasswordFailedException, IOException {
        super(h);
        query();
		readResponse();
		process();
	}
	
	protected String getRequestString() {
	    return "login";
	}
	
	protected void addRequestProperties() {
	    super.addRequestProperties();
	}
	
	/**
	 *  Gets the sessionId attribute of the LoginRequest object
	 *
	 * @return    The sessionId value
	 */
	public int getSessionId() {
		int index     = fieldPairs.indexOf(new FieldPair("mlid", new byte[0], 0, 0));
		if (index == -1) {
			return index;
		}
		FieldPair fp  = (FieldPair) fieldPairs.get(index);
		return readInt(fp.value, 0);
	}
}
