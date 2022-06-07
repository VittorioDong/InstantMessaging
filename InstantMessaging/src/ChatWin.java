import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class ChatWin extends Thread implements ActionListener {

	static JFrame chatFrame;
	JPanel chatPanel;
	JButton sendButton;
	JLabel userListLabel, userMessageLabel, chatUserLabel, backLabel;
	JTextField messageTextField;
	java.awt.List UserList;
	TextArea messageTextArea;
	JComboBox userComboBox;
	JCheckBox privateChatCheckBox;
	String serverIp, loginName;
	Thread thread;
	Message messageObj = null;
	String serverMessage = "";

	public ChatWin(String name, String IP) {
		// TODO Auto-generated constructor stub
		serverIp = IP;
		loginName = name;
		chatFrame = new JFrame("欢迎"+ name +"使用本即时通讯软件" );
		chatPanel = new JPanel();
		chatFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		chatFrame.getContentPane().add(chatPanel);

		Font fntDisp1 = new Font("宋体", Font.PLAIN, 12);

		String list[] = { "所有人" };
		//logoutButton = new JButton("退出");
		sendButton = new JButton("发送");
		userListLabel = new JLabel("在线用户");
		userMessageLabel = new JLabel("聊天信息");
		chatUserLabel = new JLabel("发送对象:");
		UserList = new java.awt.List();
		messageTextField = new JTextField(170);
		userComboBox = new JComboBox(list);
		privateChatCheckBox = new JCheckBox("私发");
		messageTextArea = new TextArea("", 300, 200, TextArea.SCROLLBARS_VERTICAL_ONLY);// 只能向下滚动
		messageTextArea.setForeground(new Color(0, 0, 0));
		messageTextArea.setEditable(false); // 不可写入

		chatPanel.setLayout(null);
		//logoutButton.setBounds(520, 500, 160, 40);
		sendButton.setBounds(120, 500, 160, 40);

		userListLabel.setBounds(620, 0, 120, 40);
		userMessageLabel.setBounds(10, 0, 120, 40);
		chatUserLabel.setBounds(470, 450, 80, 40);

		UserList.setBounds(620, 40, 150, 400);
		messageTextArea.setBounds(10, 40, 600, 400);
		messageTextField.setBounds(10, 450, 450, 40);
		userComboBox.setBounds(540, 455, 70, 30);
		privateChatCheckBox.setBounds(620, 455, 60, 20);
		//logoutButton.setFont(fntDisp1);
		sendButton.setFont(fntDisp1);
		userListLabel.setFont(fntDisp1);
		userMessageLabel.setFont(fntDisp1);
		chatUserLabel.setFont(fntDisp1);
		userComboBox.setFont(fntDisp1);
		privateChatCheckBox.setFont(fntDisp1);

		userListLabel.setForeground(Color.black);
		userMessageLabel.setForeground(Color.black);
		chatUserLabel.setForeground(Color.black);
		userComboBox.setForeground(Color.black);
		privateChatCheckBox.setForeground(Color.black);
		UserList.setBackground(Color.white);
		messageTextArea.setBackground(Color.white);
		//logoutButton.setBackground(Color.WHITE);
		sendButton.setBackground(Color.WHITE);
		//chatPanel.add(logoutButton);
		chatPanel.add(sendButton);
		chatPanel.add(userListLabel);
		chatPanel.add(userMessageLabel);
		chatPanel.add(chatUserLabel);
		chatPanel.add(UserList);
		chatPanel.add(messageTextArea);
		chatPanel.add(messageTextField);
		chatPanel.add(userComboBox);
		chatPanel.add(privateChatCheckBox);

		chatFrame.addWindowListener(new Windowclose());
		//logoutButton.addActionListener(this);
		sendButton.addActionListener(this);
		UserList.addActionListener(this);
		messageTextField.addActionListener(this);

		// 启动聊天页面信息刷新线程
		Thread thread = new Thread(this);
		thread.start();

		chatFrame.setSize(800, 590);
		chatFrame.setVisible(true);
		chatFrame.setResizable(false);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		Object source = (Object) e.getSource();
//		if (source.equals(logoutButton)) {
//			logout();
//		}
		if (source.equals(sendButton)) {
			sendMessage();
		}
		if (source.equals(UserList)) // 双击列表框
		{
			changeUser();
		}
	}



	public void run() {
		int intMessageCounter = 0;//需要显示的消息的起始位置
		int intUserTotal = 0;
		boolean isFirstLogin = true;	// 判断是否刚登陆
		boolean isFound; 				// 判断是否找到用户
		Vector user_logout = new Vector();

		try {
			while(true) {
				
				Socket toServer;
				//与服务器建立连接
				toServer = new Socket(serverIp, 6668);
				
				// 将Message信息发往服务器
				messageObj = new Message();
				ObjectOutputStream streamtoserver = new ObjectOutputStream(toServer.getOutputStream());
				streamtoserver.writeObject((Message) messageObj);
				
				// 接收来自服务器的Message信息
				ObjectInputStream streamfromserver = new ObjectInputStream(toServer.getInputStream());
				messageObj = (Message) streamfromserver.readObject();
				
				// 刷新聊天信息列表
				if (isFirstLogin) 										// 如果用户刚刚登陆
				{
					intMessageCounter = messageObj.chatVector.size();	// 不显示该用户登陆前的聊天内容
					isFirstLogin = false;
				}
				
				if (!serverMessage.equals(messageObj.notice)) {
					serverMessage = messageObj.notice;
					messageTextArea.append("[系统消息]：" + serverMessage+"\n");
				}
				
				//遍历每一条消息
				for (int i = intMessageCounter; i < messageObj.chatVector.size(); i++) {
					Communication temp = (Communication) messageObj.chatVector.elementAt(i);

					String temp_message;
					if (temp.chatUser.equals(loginName)) {			//如果发言者是自己
						if (temp.chatToUser.equals(loginName)) {	//如果聊天对象是自己
							temp_message = "用户您好，不能自己对自己说话哦~" + "\n";
						}else{										//如果聊天对象是其他用户
							if (!temp.privateChat){ 				// 如果不是私聊	
								temp_message = " 你 对 " + temp.chatToUser + " " + "说：" + temp.chatMessage + "\n";
							}else{ 									//如果是私聊
								temp_message = " 你 私发给 " + temp.chatToUser + " " + "说：" + temp.chatMessage + "\n";
							}
						}
					} else {										//如果发言者是其他用户
						if (temp.chatToUser.equals(loginName)) {	//如果聊天对象是你
							if (!temp.privateChat){ 				// 如果不是私聊
								temp_message = " " + temp.chatUser + " 对 你 " + "说：" + temp.chatMessage + "\n";
							} else {								//如果是私聊
								temp_message = " " + temp.chatUser + " 私发给 你 " + "说：" + temp.chatMessage + "\n";
							}										
						} else {									//如果聊天对象不是你
							if (!temp.chatUser.equals(temp.chatToUser)){ // 如果对方没有自言自语
								if (!temp.privateChat){				// 如果不是私聊						
									temp_message = " " + temp.chatUser + " 对 " + temp.chatToUser + " " + "说：" + temp.chatMessage + "\n";
								} else {							// 如果是私聊，不显示消息
									temp_message = "";
								}
							} else {								// 如果对方自言自语，不显示消息
								temp_message = "";
							}
						}
					}
					messageTextArea.append(temp_message);
					intMessageCounter++;
				}

				//刷新在线用户
				UserList.clear();
				for (int i = 0; i < messageObj.userOnlineVector.size(); i++) {
					String user = ((User) messageObj.userOnlineVector.elementAt(i)).name;
					UserList.addItem(user);
				}
				
				//显示用户进入聊天室的信息
				if (messageObj.userOnlineVector.size() > intUserTotal) {
					String tempstr = ((User) messageObj.userOnlineVector.elementAt(messageObj.userOnlineVector.size() - 1)).name;
					if (!tempstr.equals(loginName)) {				//如果刚进入聊天室的不是自己，就显示用户进入聊天室的信息
						messageTextArea.append("欢迎 " + tempstr + " 进入了聊天室" + "\n");
					}
				}
				
				//显示用户离开聊天室的信息
				if (messageObj.userOnlineVector.size() < intUserTotal) {
					for (int i = 0; i < user_logout.size(); i++) {
						isFound = false;
						for (int j = 0; j < messageObj.userOnlineVector.size(); j++) {
							String tempstr = ((User) user_logout.elementAt(i)).name;
							if (tempstr.equals(((User) messageObj.userOnlineVector.elementAt(j)).name)) {
								isFound = true;
								break;
							}
						}
						if (!isFound) 								//没有发现该用户
						{
							String tempstr = ((User) user_logout.elementAt(i)).name;
							if (!tempstr.equals(loginName)) {		//如果刚离开聊天室的不是自己，就显示用户离开聊天室的信息
								messageTextArea.append("用户 " + tempstr + " 离开了聊天室" + "\n");
							}
						}
					}
				}
				user_logout = messageObj.userOnlineVector;
				intUserTotal = messageObj.userOnlineVector.size();
				streamtoserver.close();
				streamfromserver.close();
				toServer.close();
				Thread.sleep(3000);
			}

		} catch (Exception e) {
			@SuppressWarnings("unused")
			JOptionPane jop = new JOptionPane();
			JOptionPane.showMessageDialog(null, "不能连接服务器！");
			e.printStackTrace();
			chatFrame.dispose();
		}

	}
	
	// "退出"按钮
	public void logout() {
		Logout logout = new Logout();
		logout.logoutName = loginName;
		// 发送退出信息
		try {
			Socket toServer = new Socket(serverIp, 6668);
			// 向服务器发送信息
			ObjectOutputStream outObj = new ObjectOutputStream(toServer.getOutputStream());
			outObj.writeObject(logout);
			System.out.println("退出");
			outObj.close();
			toServer.close();
			
			chatFrame.dispose();
		} catch (Exception e) {
		}

	}

	//监听窗口关闭响应
	class Windowclose extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			logout();
		}
	}
	
	private void sendMessage() {
		// TODO Auto-generated method stub
		Communication communication = new Communication();
		communication.chatUser = loginName;
		communication.chatMessage = messageTextField.getText();
		communication.chatToUser = String.valueOf(userComboBox.getSelectedItem());
		communication.privateChat = privateChatCheckBox.isSelected() ? true : false;
		try {
			Socket toServer = new Socket(serverIp, 6668);
			ObjectOutputStream outObj = new ObjectOutputStream(toServer.getOutputStream());
			outObj.writeObject(communication);
			messageTextField.setText(""); // 清空文本框
			outObj.close();
			toServer.close();
		} catch (Exception e) {
		}
	}

	// 将所选用户添加到cmbUser中
		public void changeUser() {

			boolean inComboBox = false;
			String selected = UserList.getSelectedItem();
			for (int i = 0; i < userComboBox.getItemCount(); i++) {
				if (selected.equals(userComboBox.getItemAt(i))) {
					inComboBox = true;
					break;
				}
			}
			if (!inComboBox) {
				userComboBox.insertItemAt(selected, 0);
			}
			userComboBox.setSelectedItem(selected);
		}
}
