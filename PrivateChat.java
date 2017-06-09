//Daniel Rodriguez
//CS300

//Daniel's Chat Application Program Project
//This program is a chat application. It allows multiple users/clients to connect to a server 
//and communicate with each other--either to the group as a whole or to an individual--by sending 
//messages back and forth. 

//PrivateChat.java - this file contains the code that manages the appearance and behaviors of a 
//private chat window.


import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


//PRIVATECHAT CLASS - This class manages the appearance and behavior of the private chat 
//window between two users.
public class PrivateChat {

	public JFrame frame;						//the physical chat window
	private JTextArea pmTextMessage;			//the text field for the message being sent
	public String pmUser;						//label for the username of user
	private JLabel pmLabel;						//label showing who user is chatting with
	static ChatClientNew chatClientNewObj; 		//chatClientNew object
	private JTextArea pmChatLog;				//text field showing the chat log between users
	private JButton chatHistoryBtn;				//button to access chat history between users

	
	//SETCLIENT - this method creates new window with respective users
	public void setClient(ChatClientNew chatClientNewObjInput) {
		chatClientNewObj = chatClientNewObjInput;
	}
	
	
	//DISPLAY_MESSAGE - this method displays the latest message sent between users
	public void display_message(String msgText) {
		pmChatLog.append(msgText);
	}
	/**
	 * Launch the application.
	 */
	/*public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PrivateChat window = new PrivateChat();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/

	
	//PRIVATECHAT CLASS CONSTRUCTOR - initializes class object
	public PrivateChat() {
		initialize();
	}
	
	
	//SET_USERS - this method sets the user client is chatting with and sets label on window
	public void set_user(String pmUserInput) {
		pmUser = pmUserInput;
		pmLabel.setText("chatting with user: "+pmUserInput);
	}

	//INITIALIZE - this method sets up the appearance and actions of the chat window and its
	//buttons
	private void initialize() {
		//the chat window
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		//text field displaying the chat log
		pmChatLog = new JTextArea();
		pmChatLog.setBounds(6, 35, 438, 175);
		frame.getContentPane().add(pmChatLog);
		
		//"Send" button
		JButton pmSendBtn = new JButton("Send");
		pmSendBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pmChatLog.append(chatClientNewObj.get_userName()+" : "+pmTextMessage.getText()+"\n");
				chatClientNewObj.sendMessage(new ChatMessage(ChatMessage.MESSAGEPM, pmTextMessage.getText(), pmUser, null));
				pmTextMessage.setText("");
			}
		});
		pmSendBtn.setBounds(366, 232, 84, 29);
		frame.getContentPane().add(pmSendBtn);
		
		//text field where message being send will be typed
		pmTextMessage = new JTextArea();
		pmTextMessage.setBounds(6, 222, 358, 39);
		frame.getContentPane().add(pmTextMessage);
		
		//label indicating who current user is chatting with; initially blank
		pmLabel = new JLabel("");
		pmLabel.setBounds(6, 6, 326, 22);
		frame.getContentPane().add(pmLabel);
		
		//"History" button
		chatHistoryBtn = new JButton("History");
		chatHistoryBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pmChatLog.setText("");
				chatClientNewObj.sendMessage(new ChatMessage(ChatMessage.HISTORY, null, pmUser, null));
			}
		});
		chatHistoryBtn.setBounds(360, -1, 84, 29);
		frame.getContentPane().add(chatHistoryBtn);
	}
}
