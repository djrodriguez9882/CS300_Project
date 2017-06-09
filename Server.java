//Daniel Rodriguez
//CS300

//Daniel's Chat Application Program Project
//This program is a chat application. It allows multiple users/clients to connect to a server 
//and communicate with each other--either to the group as a whole or to an individual--by sending 
//messages back and forth. 

//Server.java - This file contains the coding for the server-side programming. It sets up a port to
//monitor for communications from the client-side program and keeps a list of all users--including
//information for if they are currently online or not and the ability to add new users. Userlist
//is stored in a data structure while program is running and writes the information to a file for 
//future use after program closes.


import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;


//SERVER CLASS - This class is the main component of the Server that will be used to facilitate
//communication between clients. It listens for communications from the client and decides what 
//to do with the information. Fields include a unique ID for each connection, an ArrayList
//that keeps the list of the current, active clients, a date format, a int to keep track of the 
//port number in use, a boolean to determine whether to keep the connection open or not, and an
//ArrayList that keeps track of all Users registered in the chat application.
public class Server {
	// a unique ID number for each connection
	private static int uniqueId;
	// an ArrayList of clients
	private ArrayList<ClientThread> clientList;
	// datestamp
	private SimpleDateFormat sdf;
	// the port number for communication with clients
	private int port;
	// boolean which keeps server connection open or closed
	private boolean keepGoing;
	// list of all existing users registered to program
	private ArrayList<UserInfo> allUserList;


	
	//SERVER CLASS CONSTRUCTOR - passes in a port number which will be used as main line of
	//communication between server and clients.
	public Server(int port) {
		// port number
		this.port = port;
		// timestamp format hh:mm:ss
		sdf = new SimpleDateFormat("HH:mm:ss");
		// ArrayList for the list of clients
		clientList = new ArrayList<ClientThread>();
		// ArrayList for the list of all registered users
		allUserList = new ArrayList<UserInfo>();
		// Calls load_user_info method which reads in list of usernames and passwords from 
		// external file and populates the allUserList ArrayList each time the server starts.
		load_user_info();
	}
	
	
	//START - This method 
	public void start() {
		keepGoing = true;
		/* create socket server and wait for connection requests */
		int numberOfTries = 0;
		while (numberOfTries < 10) {
			numberOfTries++;
			try 
			{
				// the socket used by the server
				ServerSocket serverSocket = new ServerSocket(port);
				numberOfTries = 1000000;
	
				// infinite loop to wait for connections
				while(keepGoing) 
				{
					// format message saying we are waiting
					display("Server waiting for Clients on port " + port + ".");
					
					Socket socket = serverSocket.accept();  	// accept connection
					// if I was asked to stop
					if(!keepGoing)
						break;
					ClientThread clientThreadObj = new ClientThread(socket);  // make a thread of it
					clientList.add(clientThreadObj);									// save it in the ArrayList
					clientThreadObj.start();
				}
	
			}
			// something went bad
			catch (IOException e) {
	            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
				display(msg);
				port++;
			}
		}
	}		

	// DISPLAY - this method prints a message (not chat message) to console--used to make sure
	// program behaves as expected or to communicate an action to programmer/person monitoring server
	private void display(String msg) {
		String time = sdf.format(new Date()) + " " + msg;
		System.out.println(time);

	}
	
	
	//LOAD_USER_INFO - This method reads in username and password information from an external file
	//and saves them in an ArrayList for the server to access (i.e. when client is logging in, server
	//checks for matching username and password as user provided to access connection.
	private void load_user_info() {
		Scanner filein;
		try {
			filein = new Scanner(new FileInputStream("src/userInfo.txt"));
			while (filein.hasNext()) 
			{
				String userName = filein.next();
				String password = filein.next();
				UserInfo tempUserInfo = new UserInfo(userName, password);
				allUserList.add(tempUserInfo);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	//ADD_USER - This method adds a new user and their login information to the userlist
	private void add_user(String userNameInput, String passwordInput) {
		String userName = userNameInput;
		String password = passwordInput;
		UserInfo tempUserInfo = new UserInfo(userName, password);
		allUserList.add(tempUserInfo);
		try {
		    Files.write(Paths.get("src/userInfo.txt"), ("\n"+userName+" "+password).getBytes(), StandardOpenOption.APPEND);
		}catch (IOException e) {
		    //exception handling left as an exercise for the reader
		}
	}
	
	
	//SEND_PM - This method contains the information to send a message from one user to another
	private void send_pm(ChatMessage msg, String currentUser) {
		String msgText = currentUser+" : "+msg.getMessage()+"\n";
		String pmUser = msg.getUserName();
		for(int i = 0; i < clientList.size(); ++i) {
			ClientThread ct = clientList.get(i);
			if(ct.userName.equals(pmUser)) {
				System.out.println("found user "+pmUser);
				System.out.println("msg "+msgText);
				ct.writeMsg(ChatMessage.MESSAGEPM, msgText, currentUser, null);
			}
		}
		//update the chat history file for both "a to b" and "b to a"
		update_chat_history(currentUser, pmUser, msgText);
		update_chat_history(pmUser, currentUser, msgText);
	}
	
	
	//GET_FILENAME - This method returns the file name of the chat log between two users
	String get_fileName(String user1, String user2) {
		return "src/"+user1+"_"+user2+".history";
	}
	
	
	//UPDATE_CHAT_HISTORY - This method appends the respective chat log/history file between
	//two users
	private void update_chat_history(String user1, String user2, String message) {
		String fileName = get_fileName(user1, user2);
		try {
			File yourFile = new File(fileName);
			yourFile.createNewFile(); // if file already exists will do nothing 
		    Files.write(Paths.get(fileName), message.getBytes(), StandardOpenOption.APPEND);
		}catch (IOException e) {
		    //exception handling left as an exercise for the reader
			System.out.println("some error in writing ");
		}
	}

	

	//when the Server.java file is run, this will set up the server side programming--
	//port number and thread
	public static void main(String[] args) {
		// start server on port 1500 unless a PortNumber is specified 
		int portNumber = 1500;
		Server server = new Server(portNumber);
		server.start();
	}

	//One thread will run per client
	class ClientThread extends Thread {
		// the socket where to listen/talk
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		//unique id (to make disconnection easier)
		int id;
		//client's username
		String userName;
		ChatMessage clientMessage;
		String date;
		Boolean loggedIn;

		//CLIENTTHREAD CLASS CONSTRUCTOR
		ClientThread(Socket socket) {
			//unique id
			id = ++uniqueId;
			this.socket = socket;
			loggedIn = false;
			
			System.out.println("Thread trying to create Object Input/Output Streams");
			try
			{
				//creates output 
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput  = new ObjectInputStream(socket.getInputStream());
				//reads username
				//userName = (String) sInput.readObject();
				//display(userName + " just connected.");
			}
			catch (IOException e) {
				display("Exception creating new Input/output Streams: " + e);
				return;
			}
            date = new Date().toString() + "\n";
		}

		//continuously runs
		public void run() {
			//loop until LOGOUT
			boolean keepGoing = true;
			while(keepGoing) {
				try {
					clientMessage = (ChatMessage) sInput.readObject();
				}
				catch (IOException e) {
					display(userName + " Exception reading Streams: " + e);
					break;				
				}
				catch(ClassNotFoundException e2) {
					break;
				}
				//the message
				String message = clientMessage.getMessage();

				//different types of messages server will receive and the actions it should
				//take accordingly
				switch(clientMessage.getType()) {
				case ChatMessage.HISTORY:
					send_history(clientMessage, userName);
					break;
				case ChatMessage.MESSAGEPM:
					send_pm(clientMessage, userName);
					break;
				case ChatMessage.MESSAGEALL:
					broadcast(ChatMessage.MESSAGE, userName+"("+sdf.format(new Date())+"):"+message+"\n");
					break;
				case ChatMessage.LOGIN:
					//here we need to try to login the user
					loginUser(clientMessage);
					break;
				case ChatMessage.REGISTER:
					registerUser(clientMessage);
					break;
					//here we will try to register a new user
				case ChatMessage.MESSAGE:
					//broadcast(userName + ": " + message);
					break;
				case ChatMessage.LOGOUT:
					display(userName + " disconnected with a LOGOUT message.");
					//keepGoing = false;
					if (loggedIn) {
						loggedIn = false;
						broadcast(ChatMessage.REMOVEUSER, userName);
						//notifies group chat when a user logs out of application
						broadcast(ChatMessage.MESSAGE, userName+" has logged out\n");
					}
					break;
				case ChatMessage.WHOISIN:
					writeMsg(0, "List of the users connected at " + sdf.format(new Date()) + "\n", null, null);
					//scan List of connected users 
					for(int i = 0; i < clientList.size(); ++i) {
						ClientThread ct = clientList.get(i);
						if (ct.loggedIn) {
							writeMsg(ChatMessage.ADDUSER, ct.userName, null, null);
						}
					}
					break;
				}
			}
			//close window
			close();
		}
		
		//try to close 
		private void close() {
			//try closing the connection
			try {
				if(sOutput != null) sOutput.close();
			}
			catch(Exception e) {}
			try {
				if(sInput != null) sInput.close();
			}
			catch(Exception e) {};
			try {
				if(socket != null) socket.close();
			}
			catch (Exception e) {}
		}

		//message that will be sent from server to client, formatted in specified format
		private boolean writeMsg(int msgType, String msg, String userName, String password) {
			//send the message, if client is still connected 
			if(!socket.isConnected()) {
				close();
				return false;
			}
			//write message to the stream
			try {
				sOutput.writeObject(new ChatMessage(msgType, msg, userName, password));
			}
			// Inform user if error occurs
			catch(IOException e) {
				display("Error sending message to " + userName);
				display(e.toString());
			}
			return true;
		}
		
		
		//BROADCAST - this method send message over stream
		private void broadcast(int msgType, String msg) {
			for(int i = 0; i < clientList.size(); ++i) {
				ClientThread ct = clientList.get(i);
				if (ct.loggedIn) {
					ct.writeMsg(msgType, msg, null, null);
				}
			}
		}
		
		
		//LOGINUSER - this method traverses the list of online users to see if user trying to 
		//log in has already been logged in; sends error message, if so. Connects, otherwise.
		private void loginUser(ChatMessage msgInput) {
			String userNameInput = msgInput.getUserName();
			String passwordInput = msgInput.getPassword();
			Boolean foundUser = false;
			for(int i = 0; i < clientList.size(); ++i) {
				ClientThread ct = clientList.get(i);
				if (userNameInput.equals(ct.userName)) {
					writeMsg(ChatMessage.LOGINFAIL, "You are already logged in some place else", null, null);
					return;
				}
			}
			for(int i = 0; i < allUserList.size(); ++i) {
				UserInfo currentUser = allUserList.get(i);
				if (currentUser.userName.equals(userNameInput) ) {
					foundUser = true;
					if (currentUser.password.equals(passwordInput)) {
						userName = userNameInput;
						broadcast(ChatMessage.MESSAGE, userName+" has logged in\n");
						writeMsg(ChatMessage.LOGINSUCCESS, "successfully connected", null, null);	
						loggedIn = true;
						broadcast(ChatMessage.ADDUSER, userName);
					} else {
						writeMsg(ChatMessage.LOGINFAIL, "incorrect password", null, null);						
					}
					break;
				}
			}
			if (!foundUser) {
				writeMsg(ChatMessage.LOGINFAIL, "userName not found", null, null);						
			}
		}
	
		
		//REGISTERUSER - This method traverses list of registered users and returns a failure
		//to register message if username already exists. Otherwise, it adds user to userlist
		private void registerUser(ChatMessage msgInput) {
			String userNameInput = msgInput.getUserName();
			String passwordInput = msgInput.getPassword();
			Boolean foundUser = false;
			for(int i = 0; i < allUserList.size(); ++i) {
				UserInfo currentUser = allUserList.get(i);
				if (currentUser.userName.equals(userNameInput)) {
					writeMsg(ChatMessage.LOGINFAIL, "userName already exists", null, null);
					foundUser = true;
					break;
				}
			}
			if (!foundUser) {
				writeMsg(ChatMessage.LOGINSUCCESS, "successfully registered user", null, null);		
				add_user(userNameInput, passwordInput);
			}
		}
		
		
		//SEND_HISTORY - This method reads in the chat history between two users after finding
		//the respective file
		void send_history(ChatMessage msg, String currentUser) {
			String pmUser = msg.getUserName();
			String fileName = get_fileName(currentUser, pmUser);
			Scanner filein;
			try {
				filein = new Scanner(new FileInputStream(fileName));
				while (filein.hasNext()) 
				{
					String msgToSend = filein.nextLine();
					writeMsg(ChatMessage.HISTORY, msgToSend+"\n", pmUser, null);
				}
			} catch (IOException e) {
			
			}
		}
		
	}
	
	
	//USERINFO CLASS - this class stores the username and password for each registered user;
	//stored in array of users
	class UserInfo {
		public String userName;
		public String password;
		UserInfo(String userNameInput, String passwordInput) {
			userName = userNameInput;
			password = passwordInput;
		}
	}
}


