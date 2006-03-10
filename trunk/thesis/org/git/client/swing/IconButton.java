/*
 * IconButton.java
 *
 * Created on June 30, 2004, 7:59 PM
 */

package org.git.client.swing;

import javax.swing.Icon;
import javax.swing.JLabel;
/**
 *
 * @author  Greg
 */
public class IconButton extends JLabel{
    public Icon mouseUp, mouseOver, mouseDown;
    
    public IconButton(Icon norm, Icon mo, Icon md) {
        super();
        mouseUp = norm;
        mouseOver = mo;
        mouseDown = md;
        setIcon(mouseUp);
    }
    
}
