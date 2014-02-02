package tweetdependenciesbot;

import javax.swing.*;
import java.awt.event.*;

class InputHandler extends JDialog implements ActionListener
{
	JLabel labelDatabase;

	JLabel labelUser;
	JLabel labelPassword;
	JTextField textUser;
	JPasswordField passPassword;

	JLabel labelTwitterAccount;

	JLabel labelTwitterLogin;
      	JLabel labelTwitterPassword;
      	JTextField textTwitterLogin;
      	JPasswordField passTwitterPassword;

      	JButton okButton;
      	JButton cancelButton;

	JDialog dialog;
  
	public InputHandler()
	{
		/** Database */
		JPanel panelDatabase = new JPanel();
		labelDatabase = new JLabel("Database");
		panelDatabase.add(labelDatabase);

		JPanel panelDBUser = new JPanel();
		labelUser = new JLabel("User");
		textUser  = new JTextField(15);
		panelDBUser.add(labelUser);
		panelDBUser.add(textUser);

		JPanel panelDBPassword = new JPanel();
		labelPassword = new JLabel("Password");
		passPassword  = new JPasswordField(15);
		panelDBPassword.add(labelPassword);
		panelDBPassword.add(passPassword);

		/** Twitter account */

		JPanel panelTwitterAccount = new JPanel();
		labelTwitterAccount = new JLabel("Twitter Account");
		panelTwitterAccount.add(labelTwitterAccount);

		JPanel panelTALogin = new JPanel();
		labelTwitterLogin = new JLabel("Login   ");
		textTwitterLogin  = new JTextField(15);
		panelTALogin.add(labelTwitterLogin);
		panelTALogin.add(textTwitterLogin);

		JPanel panelTAPassword = new JPanel();
		labelTwitterPassword = new JLabel("Password");
		passTwitterPassword  = new JPasswordField(15);
		panelTAPassword.add(labelTwitterPassword);
		panelTAPassword.add(passTwitterPassword);

		/** Buttons */

		JPanel panelButtons = new JPanel();
		okButton = new JButton("OK");
		cancelButton = new JButton("Cancel");
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		panelButtons.add(okButton);
		panelButtons.add(cancelButton);

		/** Dialog */

		dialog = new JDialog();
		dialog.setResizable(false);

		dialog.setTitle("Login in to Twitter");
		dialog.getContentPane().setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.PAGE_AXIS));

		dialog.getContentPane().add(panelDatabase);
		dialog.getContentPane().add(panelDBUser);
		dialog.getContentPane().add(panelDBPassword);
		dialog.getContentPane().add(panelTwitterAccount);
		dialog.getContentPane().add(panelTALogin);
		dialog.getContentPane().add(panelTAPassword);
		dialog.getContentPane().add(panelButtons);

		dialog.setSize(350, 300);
		dialog.setLocationRelativeTo(null); // place in center of screen
		dialog.setModal(true);
		dialog.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == okButton)
			dialog.dispose();
		else if (e.getSource() == cancelButton)
			System.exit(0);
	}

	public String getUser()
	{
		return textUser.getText();
	}

	public String getPassword()
	{
		return String.valueOf(passPassword.getPassword());
	}

	public String getTwitterLogin() {
		return textTwitterLogin.getText();
	}
  
	public String getTwitterPassword() {
		return String.valueOf(passTwitterPassword.getPassword());
	}
}