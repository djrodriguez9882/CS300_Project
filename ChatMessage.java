//Daniel Rodriguez
//CS300

//Daniel's Chat Application Program Project
//This program is a chat application. It allows multiple users/clients to connect to a server 
//and communicate with each other--either to the group as a whole or to an individual--by sending 
//messages back and forth. 

//ChatMessage.java -  This file contains code for the ChatMessage class which are the messages
//sent between the server and client. They are formatted in a way that are easy and efficient.


import java.io.*;


//CHATMESSAGE CLASS - This class sets up the format for the messages that will be sent between
//the client and server.
public class ChatMessage implements Serializable {

	protected static final long serialVersionUID = 1112122200L;

	//The different types of message sent by the Client
	//WHOISIN is the list of current, active users
	//MESSAGE the message being sent
	//LOGOUT to disconnect from the server
	//LOGIN to connect to the server
	//REGISTER to add a new user to userlist
	//LOGINSUCCESS indicated login was successful
	//LOGINFAIL indicates login was unsuccessful
	//ADDUSER to add a user to the list of current, active users
	//REMOVEUSER to remove a user from the list of current, active users
	//MESSAGEALL to send a message to all online users
	//MESSAGEPM to send a private message to one other user
	//HISTORY to access chat history between current user and one other
	static final int WHOISIN = 0, MESSAGE = 1, LOGOUT = 2, LOGIN = 3, REGISTER = 4, 
			LOGINSUCCESS = 5, LOGINFAIL = 6, ADDUSER = 7, REMOVEUSER = 8, MESSAGEALL = 9, 
			MESSAGEPM = 10, HISTORY = 11;
	private int type;
	private String message, userName, password;
	
	
	//CHATMESSAGE CONSTRUCTOR - initializes ChatMessage class object
	ChatMessage(int type, String message, String userName, String password) {
		this.type = type;
		this.message = message;
		this.userName = userName;
		this.password = password;
	}
	
	//GETTYPE - this method is a getter; returns the message type field
	int getType() {
		return type;
	}
	
	//GETMESSAGE - this method is a getter; returns the message field
	String getMessage() {
		return message;
	}
	
	//GETUSERNAME- this method is a getter; returns the username field
	String getUserName() {
		return userName;
	}
	
	//GETPASSWORD - this method is a getter; returns the password field
	String getPassword() {
		return password;
	}
}
