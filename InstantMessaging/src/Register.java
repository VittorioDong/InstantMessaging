import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class Register extends JFrame  implements ActionListener{
	private static final long serialVersionUID = 4241832706558948996L;

	JPanel  registerPanel;
	JLabel  userNameLabel, passwordLabel,againPasswordLabel;
	JTextField  userNameTextField;
	JPasswordField  userPassword,againPassword;
    JButton  okButton,cancelButton,clearButton;
	String  serverIp;

    //构造函数
	public Register(String ip)
	{
		super("注册");
		serverIp=ip;
		registerPanel=new JPanel();
		this.getContentPane().add(registerPanel);
	
		userNameLabel=new JLabel("用户名:");
		passwordLabel=new JLabel("密码:");
		againPasswordLabel=new JLabel("确认密码:");
		userNameTextField=new JTextField(30);
		userPassword=new JPasswordField(30);
		againPassword=new JPasswordField(30);
	    okButton=new JButton("确定");
		cancelButton=new JButton("返回");
		clearButton=new JButton("清空");
		
		registerPanel.setLayout(null);

		userNameLabel.setBounds(130, 50, 150, 30);
		userNameTextField.setBounds(200, 50, 150, 25);
		passwordLabel.setBounds(130, 100, 150, 30);
		userPassword.setBounds(200, 100, 150, 25);
		againPasswordLabel.setBounds(130, 150, 150, 30);
		againPassword.setBounds(200, 150, 150, 25);
	    okButton.setBounds(50, 200, 80, 25);	
	    cancelButton.setBounds(200, 200, 80, 25);
	    clearButton.setBounds(350, 200, 80, 25);
	
		Font fontstr=new Font("宋体",Font.PLAIN,12);	
		userNameLabel.setFont(fontstr);
		passwordLabel.setFont(fontstr);
		againPasswordLabel.setFont(fontstr);
		userNameTextField.setFont(fontstr);
		okButton.setFont(fontstr);
		cancelButton.setFont(fontstr);
		clearButton.setFont(fontstr);
						
		userNameLabel.setForeground(Color.BLACK);
		passwordLabel.setForeground(Color.BLACK);
		againPasswordLabel .setForeground(Color.BLACK);
		okButton.setBackground(Color.WHITE);	
	    cancelButton.setBackground(Color.WHITE);
	    clearButton.setBackground(Color.WHITE);
		
		registerPanel.add(userNameLabel);
		registerPanel.add(passwordLabel);
		registerPanel.add(againPasswordLabel);
		registerPanel.add(userNameTextField);
		registerPanel.add(userPassword);
		registerPanel.add(againPassword);
		registerPanel.add(okButton);
		registerPanel.add(cancelButton);
		registerPanel.add(clearButton);
	    
	    this.setSize(500, 300);
		this.setVisible(true);
		this.setResizable(false);

		//三个按钮注册监听
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		clearButton.addActionListener(this);

	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		Object source=new Object();
	    source=e.getSource();
	    if (source.equals(okButton))      //"确定"按钮
	    {
	        register();
	    }
	    if (source.equals(cancelButton))  //"返回"按钮
	    {
	    	new Login();
	    	this.dispose();
	    }
	    if (source.equals(clearButton))     //"清空"按钮
	    {
	    	userNameTextField.setText("");
	    	userPassword.setText("");
	    	againPassword.setText("");
	    }
	}
	
	public void register()
	{
		//接受客户的信息
        RegisterInfo temp=new RegisterInfo();
        temp.name = userNameTextField.getText();
        temp.password = userPassword.getText();
		
		//验证用户名是否为空
		if(temp.name.length()==0){
		    JOptionPane.showMessageDialog(null,"用户名不能为空");	
            return;	
		}
		
		//验证密码是否为空
		if(temp.password.length()==0){
		    JOptionPane.showMessageDialog(null,"密码不能为空");	
            return;	
		}
		
		//验证密码是否一致
		if(!temp.password.equals(againPassword.getText())){
		    JOptionPane.showMessageDialog(null,"密码两次输入不一致，请重新输入");	
            return;
		}
		

	    try {
			//连接到服务器
			Socket toServer;
			toServer = new Socket(serverIp,6668);
			ObjectOutputStream streamToServer=new ObjectOutputStream (toServer.getOutputStream());
			//写客户详细资料到服务器socket
			streamToServer.writeObject((RegisterInfo)temp);
			//读来自服务器socket的登陆状态
			BufferedReader fromServer=new BufferedReader(new InputStreamReader(toServer.getInputStream()));
			String status=fromServer.readLine();
			//显示成功消息
			JOptionPane op=new JOptionPane();
			op.showMessageDialog(null,status);
			if(status.equals(temp.name+"注册成功"))
			{
				userNameTextField.setText("");
				userPassword.setText("");
				againPassword.setText("");
			}
			
			//关闭流对象
			streamToServer.close();
			fromServer.close();
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
