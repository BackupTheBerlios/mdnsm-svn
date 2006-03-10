/*
 * Created on Jan 21, 2005
 * Created by wooo as part of git
 */

package org.git.client.daap;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.git.GITProperties;
import org.git.client.swing.ParagraphLayout;



/**
 * 
 * @author wooo
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GetNewHost extends JDialog implements ActionListener {
    	private InetAddress ip;
    	private String name;
    	private int port;
    	private JTextField mAddress;
    	private JTextField mPort;
    	private JTextField mName;
    	private JLabel message;
    	private int status;
    	public static final int STATUS_PRESSED_CANCEL = 0;
    	public static final int STATUS_PRESSED_OK = 1;

    	/**
    	 *  Constructor for the GetNewHost object
    	 *
    	 * @param  frame  Description of the Parameter
    	 * @param  title  Description of the Parameter
    	 * @param  modal  Description of the Parameter
    	 */
    	public GetNewHost(Frame frame, boolean modal) {
    		super(frame, "Manually add a new shared host", modal);
    		
    		message = new JLabel("Enter the IP address and port of the server:");

    		// ADDRESS AND PORT:
    		JPanel hostInfo = new JPanel();
    		hostInfo.setLayout(new ParagraphLayout());
    		
    		mAddress = new JTextField("127.0.0.1");
    		mAddress.setPreferredSize(new Dimension(150,25));
    		mPort = new JTextField(GITProperties.sharePort + "");
    		mPort.setPreferredSize(new Dimension(75,25));
    		hostInfo.add(new JLabel("IP Address:"));
    		hostInfo.add(mAddress);
    		hostInfo.add(new JLabel("Port:"), ParagraphLayout.NEW_PARAGRAPH);
    		hostInfo.add(mPort);
    		
    		// HOST NAME:
    		mName = new JTextField("New Host");
    		mName.addActionListener(this);
    		mName.setPreferredSize(new Dimension(150, 25));
    		hostInfo.add(new JLabel("Choose a name:"), ParagraphLayout.NEW_PARAGRAPH);
    		hostInfo.add(mName);

    		// BUTTONS:
    		JPanel OkCancelPanel           = new JPanel();
    		final JButton OkButton         = new JButton("OK");
    		OkCancelPanel.add(OkButton);
    		final JButton CancelButton     = new JButton("Cancel");
    		OkCancelPanel.add(CancelButton);

    		//listen for ok and cancel button clicks
    		ActionListener ButtonListener  =
    			new ActionListener() {
    				public void actionPerformed(ActionEvent e) {
    					if (e.getSource().equals(OkButton)) {
    						//System.out.println("action! ok");
    						onOk();
    					} else if (e.getSource().equals(CancelButton)) {
    						//System.out.println("action! cancel");
    						onCancel();
    					}
    				}
    			};
    		OkButton.addActionListener(ButtonListener);
    		CancelButton.addActionListener(ButtonListener);
//    		getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
    		getContentPane().add(hostInfo);
    		getContentPane().add(OkCancelPanel, BorderLayout.SOUTH);


    		//when the window is closed, kill the application
    		addWindowListener(new WindowAdapter() {
    		    public void windowClosed(WindowEvent e) {
    		        onCancel();
    		    }
    		});

    		validate();
    		
    		// SIZE ME:
    		setSize(new Dimension(400,250));
    		
    		// MAKE SO USER CANT RESIZE ME:
    		setResizable(false);
    		
    		//center the dialog on the screen
    		setLocationRelativeTo(frame);

    		//dispose of the dialog when the dialog is closed
    		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    	}


    	/**  Handle cancel button clicks * */
    	private void onCancel() {
    	    status = 0;
    	    hide();
    	}


    	/**  Handle ok button clicks. */
    	private void onOk() {

    	    // TEST THE INPUTS FOR VALIDITY:
    	    try {
    	        if (mAddress.getText().length() == 0) {
    	            JOptionPane.showMessageDialog(this, "Please enter a valid IP address.","Error!", JOptionPane.ERROR_MESSAGE);
    	            return;
    	        }
    	        port = Integer.parseInt(mPort.getText());
    	        ip = InetAddress.getByName(mAddress.getText());
    	        name = mName.getText();
    	    } catch (NumberFormatException e) {
    	        JOptionPane.showMessageDialog(this, "Please enter a valid port number.","Error", JOptionPane.ERROR_MESSAGE);
    	        return;
    	    } catch (UnknownHostException e) {
    	        JOptionPane.showMessageDialog(this, "Please enter a valid IP address.","Error!", JOptionPane.ERROR_MESSAGE);
    	        return;
    	    }
    	    status = 1;
    	    hide();
    	}

    	public int getStatus() {
    	    return status;
    	}
    	
    	public InetAddress getHostAddress() {
    		return ip;
    	}
    	
    	public String getHostName() {
    	    return name;
    	}
    	
    	public int getPort() {
    	    return port;
    	}


        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            onOk();
        }
    	
    }
