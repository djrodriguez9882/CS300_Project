//Daniel Rodriguez
//CS300

//Daniel's Chat Application Program Project
//This program is a chat application. It allows multiple users/clients to connect to a server 
//and communicate with each other--either to the group as a whole or to an individual--by sending 
//messages back and forth. 

//ChatClientNew.java - This file contains the code for the initiation of a new chat client.
//Each time a new client logs in to the program and establishes a connection with the
//server, a new ChatClientNew object is created to facilitate the communication between
//server and client. All client-side method implementations are found here including the
//ability to send and receive a message, register an account, sign in, sign out, and connect
//to server.


import java.awt.EventQueue;
import java.net.*;
import java.io.*;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTextField;

import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ListSelectionModel;


//CHATCLIENTNEW CLASS - This class sets up the main public chat window that allows user to
//send a chat message to all online users. It is from this window that a user is able to login
//to the application, if they are not yet signed in. This button also serves as the logout
//button once user is logged in. This class manages all communications with the server.
public class ChatClientNew {

	private JFrame frame;					//used for the chat window
	private JTextArea messageDisplay;		//text field that displays chat log
	private JButton loginLogoutBtn;			//login button in window
	private JList<String> userList;			//list of all online users
	private JTextField txtMessage;			//messaged being sent by sender
	private JLabel userLabel;				//label in window identifying user
	private Socket socket;					//socket connection to server
	private String server, userName, password;	//self-evident
	private int port;						//port number being used to communicate over
	private ObjectInputStream sInput;		//to read from socket
	private ObjectOutputStream sOutput;		//to write on socket
	static ChatClientNew currentObject;		//self-evident
	Boolean connected;						//shows if user is connected or not
	public LoginWindow loginWindowObj;		//separate window to input login information
	private DefaultListModel<String> userListModel = new DefaultListModel();
	private ArrayList<PrivateChat> privateChatObjList;	//userlist for private, 1 on 1 chat

	
	//SET_USERINFO - this method is called to save user's username and password input. This
	//information will be sent to the server to check for verification or to add--depending
	//on if user is just signing in or registering.
	public void set_userInfo(String userNameInput, String passwordInput) {
		userName = userNameInput;
		password = passwordInput;
	}
	
	
	//DISPLAY - This method prints a message (not chat message) to the console--programmer
	//used it to make sure certain program elements were behaving as expected.
	private void display(String msg) {
		System.out.println(msg);      // println in console mode
	}

	
	//GET_USERNAME - This method is a getter; returns a user's username.
	String get_userName() {
		return userName;
	}

	
	//SENDMESSAGE - Main form of communication between client and server. Client sends this
	//message which the server deciphers and determines what to do with it, depending
	//on the information included.
	void sendMessage(ChatMessage msg) {
		try {
			sOutput.writeObject(msg);
		}
		catch(IOException e) {
			display("Exception writing to server: " + e);
		}
	}
	
	
	//REGISTERUSER - The user provided username and password is sent to the server along with
	//an int indicating that this information is to be used for registering a new user.
	void registerUser() {
		sendMessage(new ChatMessage(ChatMessage.REGISTER, null, userName, password));
	}
	
	
	//LOGINUSER - The user provided username and password is sent to the server along with
	//an int indicating that this information is to be used for signing in a user.
	void loginUser() {
		sendMessage(new ChatMessage(ChatMessage.LOGIN, null, userName, password));
	}
	
	
	//CONNECT_TO_SERVER - This method establishes the connection with the server, creating a 
	//new socket and input/output streams.
	public boolean connect_to_server() {
		display("trying to connect to server");
		//trying to connect to the server
		try {
			socket = new Socket(server, port);
		} 
		//failure
		catch(Exception ec) {
			display("Error connectiong to server:" + ec);
			return false;
		}		
		String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
		display(msg);	
		//Input/Output streams
		try
		{
			sInput  = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException eIO) {
			display("Exception creating new Input/output Streams: " + eIO);
			return false;
		}
		//Thread to listen for messages from the server 
		new ListenFromServer().start();
		return true;
	}


	//DISPLAYWINDOW - Upon launch of application, this function opens the first window seen
	//where user can choose to login.
	public static void displayWindow() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChatClientNew window = new ChatClientNew();
					window.connect_to_server();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	//CHATCLIENTNEW CLASS CONSTRUCTOR
	public ChatClientNew() {
		currentObject = this;
		initialize();
	}

	
	//CREATE_NEW_PM_WINDOW - When a user selects a single user to chat with, this method 
	//establishes the private message chat window. It first checks to see if a window
	//currently exists between the sender and the receiver. If not, it opens a window. 
	private void create_new_pm_window(String selectedUser) {
		boolean foundUser = false;
		for (int i=0; i<privateChatObjList.size(); ++i) {
			PrivateChat curObj = privateChatObjList.get(i);
			if (selectedUser.equals(curObj.pmUser)) {
				System.out.println("Already chatting with this user\n");
				foundUser = true;
			}
		}
		if (!foundUser) {
			PrivateChat privateChatObj = new PrivateChat();
			privateChatObj.set_user(selectedUser);
			privateChatObj.setClient(currentObject);
			privateChatObj.frame.setVisible(true);
			privateChatObjList.add(privateChatObj);
		}
	}
	

	//INITIALIZE - this method sets up all information for new private chat window
	private void initialize() {
		privateChatObjList = new ArrayList<PrivateChat>();
		server = "localhost";
		port = 1500;
		connected = true;
		frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				log_out_user();
			}
		});
		frame.setBounds(100, 100, 692, 426);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				send_current_message();
			}
		});
		btnSend.setBounds(550, 353, 105, 29);
		frame.getContentPane().add(btnSend);
		
		userList = new JList<>(userListModel);
		userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		//if user double-clicks on a username in userlist, a new pm chat window opens
		userList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
	            System.out.println("mouse click detected");
		        if (evt.getClickCount() == 2) {
		            // Double-click detected
		            System.out.println("doubled clicked on user");
		    		String selectedUser = userList.getSelectedValue();
		            create_new_pm_window(selectedUser);
		        }
			}
		});
		userList.setBounds(550, 41, 103, 273);
		frame.getContentPane().add(userList);
		
		txtMessage = new JTextField();
		txtMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				send_current_message();
			}
		});
		txtMessage.setBounds(34, 329, 500, 53);
		frame.getContentPane().add(txtMessage);
		txtMessage.setColumns(10);
		
		messageDisplay = new JTextArea();
		messageDisplay.setBounds(38, 41, 496, 273);
		frame.getContentPane().add(messageDisplay);
		
		loginLogoutBtn = new JButton("Login");
		loginLogoutBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (loginLogoutBtn.getText().equals("Login")) {
					System.out.println("pressed login");
					loginWindowObj = new LoginWindow();
					//loginWindowObj.launchWindow(currentObject);
					loginWindowObj.setClient(currentObject);
					loginWindowObj.frame.setVisible(true);	
				} else {
					log_out_user();
				}
			}
		});
		loginLogoutBtn.setBounds(550, 6, 105, 29);
		frame.getContentPane().add(loginLogoutBtn);
		
		userLabel = new JLabel("No User");
		userLabel.setBounds(477, 11, 61, 16);
		frame.getContentPane().add(userLabel);
	}
	
	
	//SEND_CURRENT_MESSAGE - Client sends message String to be sent along with an in 
	//indicating that this message should be sent to all online users. Username and password
	//fields are empty, as they are not needed for this message.
	void send_current_message() {
		sendMessage(new ChatMessage(ChatMessage.MESSAGEALL, txtMessage.getText(), null, null));
		txtMessage.setText("");
	}

	
	//LOG_OUT_USER - An int is sent to server to indicate that this particular user is signing
	//out of the program. No message, username, or password is sent as they are not needed
	//for this particular action.
	void log_out_user() {
		sendMessage(new ChatMessage(ChatMessage.LOGOUT, null, null, null));
		loginLogoutBtn.setText("Login");
		userListModel.removeAllElements();
		userLabel.setText("No User");
	}

	
	//POPULATE_USER_LIST - This method is used to update the active userlist in the public
	//chat window for each user. First the current list is cleared and updated with a
	//list of current users logged in
	void populate_user_list() {
		//clear out the current user userList
		//userList.removeAll();
		userListModel.removeAllElements();
		//update with currently active users
		sendMessage(new ChatMessage(ChatMessage.WHOISIN, null, null, null));
	}

	
	//ADD_USER_TO_LIST - Passes in a username and adds it to the list of online users
	void add_user_to_list(String userToAdd) {
		if (!userToAdd.equals(userName)) {
			userListModel.addElement(userToAdd);
		}
	}
	
	
	//DISPLAY_PUBLIC_MESSAGE - Passes in a String message and displays it in the chat log
	//section of a user's chat window.
	void display_public_message(String message) {
		messageDisplay.append(message);
	}
	
	
	//REMOVE_USER_FROM_LIST - When a user logs out of the program, this method is used to 
	//remove their username from the list of current, active users.
	void remove_user_from_list(String userToRemove) {
		userListModel.removeElement(userToRemove);
	}
	
	
	//FIND_AND_DISPLAY - This method is used when a user selects a user to private chat with.
	//This method searches to see if a window already exists between the two users. If not, it
	//opens a new PM window.
	void find_and_display_pm(ChatMessage msg) {
		String sender = msg.getUserName();
		int userId;
		boolean foundUser = false;
		int i;
		for(i = 0; i < privateChatObjList.size(); ++i) {
			PrivateChat curObj = privateChatObjList.get(i);
			if (curObj.pmUser.equals(sender)) {
				foundUser = true;
				userId = i;
				break;
			}
		}
		if (!foundUser) {
			//No PM window exists between users; open a new one.
			userId = i;
			create_new_pm_window(sender);
		}

		PrivateChat selectedUserWindow = privateChatObjList.get(i);
		selectedUserWindow.display_message(msg.getMessage());
	}
	//LISTENFROMSERVER CLASS - This class is responsible for listening for and routing messages
	//from server. Messages are routed according to including int indicating what action should
	//be taken. The respective methods are then called, accordingly.
	class ListenFromServer extends Thread {
		public void run() {
			while(true) {
				try {
					ChatMessage msg = (ChatMessage)sInput.readObject();
					// if console mode print the message and add back the prompt
					System.out.println(msg.getMessage());
					System.out.println(msg.getType());
					System.out.print("> ");
					if (msg.getType() == ChatMessage.LOGINSUCCESS) {
						loginWindowObj.close_window();
						loginLogoutBtn.setText("Logout");
						userLabel.setText(userName);
						populate_user_list();
					}
					//if there was an error logging in
					if (msg.getType() == ChatMessage.LOGINFAIL) {
						loginWindowObj.set_status(msg.getMessage());
					}
					//if user was added
					if (msg.getType() == ChatMessage.ADDUSER) {
						add_user_to_list(msg.getMessage());
					}
					//if user was removed
					if (msg.getType() == ChatMessage.REMOVEUSER) {
						remove_user_from_list(msg.getMessage());
					}
					//to send/receive message
					if (msg.getType() == ChatMessage.MESSAGE) {
						display_public_message(msg.getMessage());
					}
					//to send/receive a private message
					if (msg.getType() == ChatMessage.MESSAGEPM) {
						find_and_display_pm(msg);
					}
					//to access chat history between two users
					if (msg.getType() == ChatMessage.HISTORY) {
						find_and_display_pm(msg);
					}
				}
				catch(IOException e) {
					display("Server has close the connection: " + e);
					break;
				}
				//required catch
				catch(ClassNotFoundException e2) {
				}
			}
		}
	}
}
