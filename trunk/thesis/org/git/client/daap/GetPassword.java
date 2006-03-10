package org.git.client.daap;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

/**
 *  Description of the Class
 *
 * @author     wooo
 * @created    July 31, 2004
 */
public class GetPassword extends JDialog {
	private String password               = "";
	private JPasswordField mPasswordEdit  = new JPasswordField();
	private JLabel message                = new JLabel();
	private int status = -1;		
	public static final int STATUS_PRESSED_CANCEL = 0;
	public static final int STATUS_PRESSED_OK = 1;

	/**
	 *  Constructor for the GetPassword object
	 *
	 * @param  frame  Description of the Parameter
	 * @param  title  Description of the Parameter
	 * @param  modal  Description of the Parameter
	 */
	public GetPassword(Frame frame, String title, boolean modal) {
		super(frame, "Enter Password", modal);
		message.setText("The music library \"" + title + "\" requires a password:");
		//set the size of the window
		setBounds(0, 0, 320, 130);

		//don't let the user resize the dialog
		setResizable(false);

		//use a grid layout, to layout all three edit controls
		JPanel EditPanel               = new JPanel(new GridLayout(2, 1));

		// private void AddEditField(String label, JComponent component, JPanel panel) {
		JPanel lp                      = new JPanel(new FlowLayout(FlowLayout.LEFT));
		// JLabel EditLabel  = new JLabel(label);
		// message.setPreferredSize(new Dimension(80, 25));
		// component.setPreferredSize(new Dimension(200, 25));
		lp.add(message);
		// LinePanel.add(component);
		EditPanel.add(lp);

		// AddLabel(
		// AddEditField("Password:", mPasswordEdit, EditPanel);
		JPanel LinePanel               = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel EditLabel               = new JLabel("Password:");
		EditLabel.setPreferredSize(new Dimension(80, 25));
		mPasswordEdit.setPreferredSize(new Dimension(200, 25));
			mPasswordEdit.setAction(new AbstractAction() {
			    public void actionPerformed(ActionEvent e) {
			        OnOk();
			    }
			});
		LinePanel.add(EditLabel);
		LinePanel.add(mPasswordEdit);
		EditPanel.add(LinePanel);

		//make a panel containing the ok and cancel buttons
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
						OnOk();
					} else if (e.getSource().equals(CancelButton)) {
						//System.out.println("action! cancel");
						OnCancel();
					}
				}
			};
		OkButton.addActionListener(ButtonListener);
		CancelButton.addActionListener(ButtonListener);
		getContentPane().add(EditPanel);
		getContentPane().add(OkCancelPanel, "South");

		//when the window is closed, kill the application
		/*
		    addWindowListener(
		    new WindowAdapter() {
		    public void windowClosed(WindowEvent e) {
		    // OnCancel();
		    }
		    });
		  */
		//center the dialog on the screen
		setLocationRelativeTo(null);

		//dispose of the dialog when the dialog is closed
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}


	/**  Handle cancel button clicks * */
	private void OnCancel() {
	    status = 0;
		//System.out.println("oncancel");
		dispose();
	}


	/**  Handle ok button clicks. */
	private void OnOk() {
	    status = 1;
		//get the password
		//System.out.println("ok");
		char[] Password  = mPasswordEdit.getPassword();
		password = new String(Password);
//		System.out.println("Password = " + password);

		dispose();
	}

	public int getStatus() {
	    return status;
	}
	
	
	/**
	 *  Gets the password attribute of the GetPassword object
	 *
	 * @return    The password value
	 */
	public String getPassword() {
		return password;
	}
	
}
