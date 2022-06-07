import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Date;
import java.util.Vector;

public class Connection extends Thread {

	private Socket clientSocket;//与客户端通讯
	private Vector<User> userOnline;//在线的用户
	private Vector<Communication> userChat;//聊天的集合
	private ObjectInputStream fromClient;//从客户到服务器的输入流
	private PrintStream toClient;//传到客户端的打印流
	private static Vector registerList = new Vector();//注册的用户
	private ServerFrame serverFrame;//服务器窗口
	private Object tempObj;//临时变量
	//构造函数
	public Connection(ServerFrame sFrame, Socket client, Vector useronline, Vector userchat) {
		// TODO Auto-generated constructor stub
		serverFrame = sFrame;
		clientSocket = client;
		userOnline = useronline;
		userChat = userchat;

		try {
			fromClient = new ObjectInputStream(clientSocket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			try {
				clientSocket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
				return;
			}
		}
		
		// 服务器写到客户端
		try {
			toClient = new PrintStream(clientSocket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			try {
				clientSocket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
				return;
			}
		}	
		this.start();	
	}

	public void run() {	//处理与客户端的通讯线程
		try {
			tempObj = (Object) fromClient.readObject();
			if (tempObj.getClass().getName().equals("User")) {	//从客户端传到服务器的对象所属的类为User，意味着执行的是登录
				loginService();
			}
			if (tempObj.getClass().getName().equals("RegisterInfo")) {	//从客户端传到服务器的对象所属的类为RegisterInfo，意味着执行的是注册
				registerService();
			}
			if (tempObj.getClass().getName().equals("Message")) {	//从客户端传到服务器的对象所属的类为Message，意味着执行的是发布公告
				messageService();
			}
			if (tempObj.getClass().getName().equals("Communication")) {	//从客户端传到服务器的对象所属的类为Communication，意味着执行的是?
				communicationService();
			}
			if (tempObj.getClass().getName().equals("Logout")) {	//从客户端传到服务器的对象所属的类为Logout，意味着执行的是退出登录
				logoutService();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e1) {
			System.out.println("读入对象失败...");
			e1.printStackTrace();
		} finally {
			try {
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

	//登录处理
	public void loginService() {

		try {
			User clientMessage2 = (User) tempObj;

			// 读文件
			FileInputStream file3 = new FileInputStream("RegisterInformation.txt");	//创建文件输入流实例
			ObjectInputStream objInput1 = new ObjectInputStream(file3);	//创建对象输入流实例
			registerList = (Vector) objInput1.readObject();				//从文件中读入对象

			int find = 0; // 1表示找到该用户，即该用户已经注册了；0表示该用户未注册
			for (int i = 0; i < registerList.size(); i++) {				//遍历注册表中的每一个用户
				RegisterInfo registerInfo = (RegisterInfo) registerList.elementAt(i);

				if (registerInfo.name.equals(clientMessage2.name)) {	//找到该用户
					find = 1;
					if (!registerInfo.password.equals(clientMessage2.password)) {//密码错误
						toClient.println("密码不正确");
						break;
					} else {											// 判断是否已经登录		
						int login_flag = 0;								//0表示未登录，1表示已经登录
						for (int j = 0; j < userOnline.size(); j++) {	//遍历在线用户
							String _custName = ((User) userOnline.elementAt(j)).name;
							if (clientMessage2.name.equals(_custName)) {//在在线用户中找到该用户，该用户已经登录，无法再次登录
								login_flag = 1;
								break;
							}
						}

						if (userOnline.size() >= 50) {
							toClient.println("登录人数过多，请稍候再试");		//向客户端返回登录人数过多的信息
							break;
						}

						if (login_flag == 0) {					//该用户未登录
							userOnline.addElement(clientMessage2);// 将该用户添加到在线用户列表中
							toClient.println("登录成功");			//向客户端返回登录成功的信息
							Date time = new Date();
							logWrite("用户 " + clientMessage2.name + "于"+ time.toLocaleString() + "登录" + "\n");//把登录信息记录在服务器日志中
							freshServerUserList();				//刷新在线用户信息
							break;
						} else {
							toClient.println("该用户已登录");		//向客户端返回该用户已登录的信息
						}
					}
				} else {
					continue;
				}
			}
			if (find == 0) {									//未找到该用户
				toClient.println("没有这个用户，请先注册");			//向客户端返回该用户未注册的信息
			}

			file3.close();										//关闭文件输入流
			objInput1.close();									//关闭对象输入流
			fromClient.close();									//关闭从客户到服务器的输入流
		} catch (ClassNotFoundException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	public void logWrite(String logInfo) {
		String newlogInfo = serverFrame.logTextArea.getText() + "\n" + logInfo;
		serverFrame.logTextArea.setText(newlogInfo);
	}
	
	private void freshServerUserList() {
		String[] userList = new String[50];
		User user = null;
		for (int j = 0; j < userOnline.size(); j++) {
			user = (User) userOnline.get(j);
			userList[j] = user.name;
		}
		serverFrame.list.setListData(userList);						//输出在线用户的用户名
		serverFrame.numberTextField.setText("" + userOnline.size());//输出在线用户的数量
		//serverFrame.lblUserCount.setText("当前在线人数:" + userOnline.size());
	}
	
	//注册
	public void registerService() {								
		try {
			int flag = 0; 										//0表示不重名，1表示重名
			RegisterInfo clientInfo = (RegisterInfo) tempObj;
			File userList = new File("RegisterInformation.txt");
			if (userList.length() != 0)							// 判断是否是第一个注册用户，如果是第一个注册用户，就不需要判断是否重名，直接注册即可
			{
				ObjectInputStream objInput = new ObjectInputStream(new FileInputStream(userList));
				registerList = (Vector) objInput.readObject();
				// 判断是否有重名
				for (int i = 0; i < registerList.size(); i++) {
					RegisterInfo registerInfo = (RegisterInfo) registerList.elementAt(i);
					if (registerInfo.name.equals(clientInfo.name)) {
						toClient.println("注册名重复,请重新输入");
						flag = 1;
						break;
					} else if (registerInfo.name.equals("所有人")) {	//用户名“所有人”作为群发的标志，客户不能占用该名称
						toClient.println("禁止使用此注册名,请重新输入");
						flag = 1;
						break;
					}
				}
			}
			if (flag == 0) {										// 添加新注册用户
				registerList.addElement(clientInfo);				//向注册表末尾加新注册的用户信息
				FileOutputStream file = new FileOutputStream(userList);
				ObjectOutputStream objout = new ObjectOutputStream(file);
				objout.writeObject(registerList);					// 将向量中的类写回文件
				
				toClient.println(clientInfo.name + "注册成功");		
				Date t = new Date();
				logWrite("用户" + clientInfo.name + "注册成功, " + "注册时间:" + t.toLocaleString() + "\n");// 将注册成功信息写入服务器日志
				file.close();										//关闭文件输出流
				objout.close();										//关闭对象输出流
				fromClient.close();									//关闭从客户到服务器的输入流
			}
		} catch (ClassNotFoundException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	public void messageService() {
		try {
			Message message = new Message();
			message.userOnlineVector = userOnline;
			message.chatVector = userChat;
			message.notice = "" + serverFrame.serverMessage;

			ObjectOutputStream outputstream = new ObjectOutputStream(clientSocket.getOutputStream());
			outputstream.writeObject((Message)message);

			clientSocket.close();
			outputstream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	//增加信息处理
	public void communicationService() {
		// 将接收到的对象值赋给聊天信息的序列化对象
		Communication communication = new Communication();
		communication = (Communication) tempObj;

		communicationLog(communication);

		// 将聊天信息的序列化对象填加到保存聊天信息的矢量中
		userChat.addElement((Communication) communication);
		return;
	}
	
	public void communicationLog(Communication communication) {
		String newlog = serverFrame.messageTextArea.getText();
		Date date = new Date();
		if (!communication.privateChat) {
			newlog += "\n";
			newlog += ("[" + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds() + "]");
			newlog += communication.chatUser;
			newlog += "->";
			newlog += communication.chatToUser;
			newlog += ":";
			newlog += communication.chatMessage;
		}
		serverFrame.messageTextArea.setText(newlog);
	}
	
	public void logoutService() {
		Logout tempLogout = new Logout();
		tempLogout = (Logout) tempObj;

		removeUser(tempLogout);
		Date t = new Date();

		logWrite("用户 " + tempLogout.logoutName + " 于" + t.toLocaleString() + "退出" + '\n');

		freshServerUserList();
	}
	
	private void removeUser(Logout tempLogout) {
		Vector<User> tempVec = new Vector<User>();
		User tempUser = null;
		for (int j = 0; j < userOnline.size(); j++) {
			tempUser = (User) userOnline.get(j);
			if (!tempLogout.logoutName.equals(tempUser.name)) {
				tempVec.add(tempUser);
			}
		}
		userOnline.removeAllElements();
		for (int j = 0; j < tempVec.size(); j++) {
			tempUser = (User) tempVec.get(j);
			userOnline.add(tempUser);
		}

	}
	
	
}
