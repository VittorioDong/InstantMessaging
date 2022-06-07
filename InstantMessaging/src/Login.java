import java.awt.Color;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Login extends JFrame implements ActionListener {
	private static final long serialVersionUID = 7663206300308166519L;
	
	private JPanel loginPanel;
	private JButton loginButton, registerButton, exitButton;
	private JLabel serverLabel, userNameLabel, passwordLabel;
	private JTextField userNameTextField, serverTextField;
	private JPasswordField passwordPasswordField;
	private String serverIp;

	//构造函数，初始化登录界面
	public Login() {
		super("登录");
		loginPanel = new JPanel();
		this.getContentPane().add(loginPanel);

		serverLabel = new JLabel("服务器:");
		userNameLabel = new JLabel("用户名:");
		passwordLabel = new JLabel("密码:");
		serverTextField = new JTextField(20);
		serverTextField.setText("127.0.0.1");
		userNameTextField = new JTextField(20);
		passwordPasswordField = new JPasswordField(20);
		loginButton = new JButton("登录");
		registerButton = new JButton("注册");
		exitButton = new JButton("退出");

		loginPanel.setLayout(null);

		serverLabel.setBounds(130, 50, 150, 30);
		serverTextField.setBounds(200, 50, 150, 25);
		userNameLabel.setBounds(130, 100, 150, 30);
		userNameTextField.setBounds(200, 100, 150, 25);
		passwordLabel.setBounds(130, 150, 150, 30);
		passwordPasswordField.setBounds(200, 150, 150, 25);
		loginButton.setBounds(50, 200, 80, 25);
		registerButton.setBounds(200, 200, 80, 25);
		exitButton.setBounds(350, 200, 80, 25);

		Font fontstr = new Font("宋体", Font.PLAIN, 12);
		serverLabel.setFont(fontstr);
		serverTextField.setFont(fontstr);
		userNameLabel.setFont(fontstr);
		userNameTextField.setFont(fontstr);
		passwordLabel.setFont(fontstr);
		passwordPasswordField.setFont(fontstr);
		loginButton.setFont(fontstr);
		registerButton.setFont(fontstr);
		exitButton.setFont(fontstr);

		userNameLabel.setForeground(Color.BLACK);
		passwordLabel.setForeground(Color.BLACK);
		loginButton.setBackground(Color.WHITE);
		registerButton.setBackground(Color.WHITE);
		exitButton.setBackground(Color.WHITE);

		loginPanel.add(serverLabel);
		loginPanel.add(serverTextField);
		loginPanel.add(userNameLabel);
		loginPanel.add(userNameTextField);
		loginPanel.add(passwordLabel);
		loginPanel.add(passwordPasswordField);
		loginPanel.add(loginButton);
		loginPanel.add(registerButton);
		loginPanel.add(exitButton);

		// 设置登录窗口
		setResizable(false);
		setSize(500, 300);
		setVisible(true);
		
		// 三个按钮注册监听
		loginButton.addActionListener(this);
		registerButton.addActionListener(this);
		exitButton.addActionListener(this);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		Object source = e.getSource();
		if (source.equals(loginButton)) {
			if (userNameTextField.getText().equals("") || passwordPasswordField.getText().equals("")) {	// 判断用户名和密码是否为空
				JOptionPane op1 = new JOptionPane();
				op1.showMessageDialog(null, "用户名或密码不能为空");
			} else {
				serverIp = serverTextField.getText();
				login();
			}
		}
		if (source.equals(registerButton)) {
			serverIp = serverTextField.getText();
			this.dispose();
			System.out.println("注册");
			new Register(serverIp);
		}
		if (source == exitButton) {
			System.exit(0);
		}
	}

	public void login() {
		// TODO Auto-generated method stub
		// 获取客户的详细资料
		User temp = new User();
		temp.name = userNameTextField.getText();
		temp.password = passwordPasswordField.getText();

		try {
			// 与服务器建立连接
			Socket toServer;
			toServer = new Socket(serverIp, 6668);
			ObjectOutputStream streamToServer = new ObjectOutputStream(toServer.getOutputStream());//向服务器传输User对象
			// 写客户详细资料到服务器socket
			streamToServer.writeObject((User) temp);
			// 读来自服务器socket的登录状态
			BufferedReader fromServer = new BufferedReader(new InputStreamReader(toServer.getInputStream()));
			String status = fromServer.readLine();					//从服务器读取信息
			if (status.equals("登录成功")) {							//若接受到的信息为“登录成功”，表示用户成功登录
				new ChatWin((String)temp.name, serverIp);			//用户成功登录后，显示聊天窗口
				this.dispose();
				// 关闭流对象
				streamToServer.close();
				fromServer.close();
				toServer.close();
			} else {
				JOptionPane.showMessageDialog(null, status);
				// 关闭流对象
				streamToServer.close();
				fromServer.close();
				toServer.close();
			}
		} catch (HeadlessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
