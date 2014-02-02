package tweetdependenciesbot;

import javax.swing.*;
import java.awt.event.*;

class InputHandler extends JDialog implements ActionListener
{
	JLabel labelUser;
	JLabel labelPassword;
	JTextField textUser;
	JPasswordField passPassword;

      	JButton okButton;
      	JButton cancelButton;

	JDialog dialog;
  
	public InputHandler()
	{
		/** Database */

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

		dialog.setTitle("Login in to MySQL database");
		dialog.getContentPane().setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.PAGE_AXIS));

		dialog.getContentPane().add(panelDBUser);
		dialog.getContentPane().add(panelDBPassword);
		dialog.getContentPane().add(panelButtons);

		dialog.setSize(350, 150);
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
}