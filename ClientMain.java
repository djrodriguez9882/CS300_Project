//Daniel Rodriguez
//CS300

//Daniel's Chat Application Program Project
//This program is a chat application. It allows multiple users/clients to connect to a server 
//and communicate with each other--either to the group as a whole or to an individual--by sending 
//messages back and forth. 

//ClientMain.java - This file contains the main function that starts the chat application program.


public class ClientMain {
	//MAIN - This main method is what starts the chat program. It creates a Chat client and opens a 
	//new chat window--where the client can login and begin chatting.
	public static void main(String[] args) {
		ChatClientNew chatClientNewObj = new ChatClientNew();
		chatClientNewObj.displayWindow();
		/*ChatClientNew chatClientNewObj1 = new ChatClientNew();
		chatClientNewObj1.displayWindow();
		ChatClientNew chatClientNewObj2 = new ChatClientNew();
		chatClientNewObj2.displayWindow();
		ChatClientNew chatClientNewObj3 = new ChatClientNew();
		chatClientNewObj3.displayWindow();*/
	}
}
