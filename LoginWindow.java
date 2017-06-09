//Daniel Rodriguez
//CS300

//Daniel's Chat Application Program Project
//This program is a chat application. It allows multiple users/clients to connect to a server 
//and communicate with each other--either to the group as a whole or to an individual--by sending 
//messages back and forth. 

//LoginWindow.java - This file contains the code that manages the appearance and behavior
//of the LoginWindow. Once the user clicks the login button on the opening, main public
//chat window, this login screen pops up with text fields for username and password. There are
//also two buttons: "Sign In" and "Register." User will click on one depending on what action
//they would like to take.


import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


//LOGINWINDOW CLASS - This class manages the appearance and behavior of the login window. 
public class LoginWindow {

	public JFrame frame;						//used for the physical window build
	private JTextField userNameField;			//text field where user types in username
	private JTextField passwordField;			//text field where user types in password
	static ChatClientNew chatClientNewObj; 		//ChatClientNew object
	private JLabel loginStatus;					//shows if user is logged in to system or not

	
	//CLOSE_WINDOW - This method simply closes the window--called when user clicks the 'x'
	//on the window.
	public void close_window() {
		frame.dispose();
	}

	
	//SETCLIENT - This method gets the ball rolling on connecting to server and starting chat
	//capabilities.
	public void setClient(ChatClientNew chatClientNewObjInput) {
		chatClientNewObj = chatClientNewObjInput;
	}
	/**
	 * Launch the application.
	 */
	/*public static void launchWindow(ChatClientNew chatClientNewObjInput) {
		chatClientNewObj = chatClientNewObjInput;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/

	//LOGINWINDOW CLASS CONSTRUCTOR - initializes instance of class object
	public LoginWindow() {
		initialize();
	}
	
	
	//SET_STATUS - sets the status of the client as online or not.
	public void set_status(String statusInput) {
		loginStatus.setText(statusInput);
	}

	
	//INITITALIZE - Sets up the appearance and actions of the UI and its buttons
	private void initialize() {
		//window
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		//text field where user can type in their username
		userNameField = new JTextField();
		userNameField.setBounds(153, 81, 130, 26);
		frame.getContentPane().add(userNameField);
		userNameField.setColumns(10);
		
		//label for the username text field
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setBounds(85, 86, 76, 16);
		frame.getContentPane().add(lblUsername);
		
		//text field where user can type in their username
		passwordField = new JTextField();
		passwordField.setBounds(153, 110, 130, 26);
		frame.getContentPane().add(passwordField);
		passwordField.setColumns(10);
		
		//label for the password text field
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(85, 114, 71, 16);
		frame.getContentPane().add(lblPassword);
		
		//"Sign In" button
		JButton btnSignIn = new JButton("Sign In");
		btnSignIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chatClientNewObj.set_userInfo(userNameField.getText(), passwordField.getText());
				chatClientNewObj.loginUser();
			}
		});
		btnSignIn.setBounds(79, 148, 117, 29);
		frame.getContentPane().add(btnSignIn);
		
		//"Register" button
		JButton btnRegister = new JButton("Register");
		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chatClientNewObj.set_userInfo(userNameField.getText(), passwordField.getText());
				chatClientNewObj.registerUser();
			}
		});
		btnRegister.setBounds(208, 148, 117, 29);
		frame.getContentPane().add(btnRegister);

		//Label for a status field showing the status connection. 
		JLabel lblStatus = new JLabel("Status:");
		lblStatus.setBounds(89, 189, 43, 16);
		frame.getContentPane().add(lblStatus);
		
		//Login status: user will only see something here if there is a problem with login:
		//incorrect username or password or if a username they are trying to register already
		//exists
		loginStatus = new JLabel("                                                                                           ");
		loginStatus.setBounds(144, 177, 270, 41);
		frame.getContentPane().add(loginStatus);
	}
}
