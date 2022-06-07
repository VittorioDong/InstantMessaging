import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;

import javax.swing.*;

//服务器窗口
public class ServerFrame extends JFrame implements ActionListener {
	
	JPanel serverPanel, userPanel,serverInfoPanel;
	JLabel serverNameLable,IPLable,portLable, numberLable,logLabel,InfoLabel;	
	public JLabel messageLabel, userLabel, noticeLabel;
	JButton sendButton;
	public JTextField numberTextField, serverNameTextField, IPTextField, portTextField, noticeTextField;
	public TextArea logTextArea, messageTextArea;	
	public  JList list;
	JList userList;
	JScrollPane userScrollPane;	
	public String serverMessage ="";
	
	//构造函数
	public ServerFrame() {
		super("服务器");
		setSize(1050, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		
		serverPanel = new JPanel();
		serverPanel.setLayout(null);
		
		//左半部分布局
		messageLabel = new JLabel("用户消息");
		messageLabel.setFont(new Font("宋体", 0, 12));
		messageLabel.setForeground(Color.BLACK);
		messageTextArea = new TextArea(20, 20);
		messageTextArea.setFont(new Font("宋体", 0, 12));
		noticeLabel = new JLabel("公告：");
		noticeLabel.setFont(new Font("宋体", 0, 12));
		noticeTextField = new JTextField(20);
		noticeTextField.setFont(new Font("宋体", 0, 12));
		sendButton = new JButton("发送");
		sendButton.setBackground(Color.WHITE);
		sendButton.setFont(new Font("宋体", 0, 12));
		sendButton.setEnabled(true);
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				serverMessage();
			}
		});
		
		userLabel = new JLabel("在线用户列表");
		userLabel.setFont(new Font("宋体", 0, 12));
		userLabel.setForeground(Color.BLACK);
		
		userList = new JList();
		userList.setFont(new Font("宋体", 0, 12));
		userList.setVisibleRowCount(17);
		userList.setFixedCellWidth(180);
		userList.setFixedCellHeight(18);

		userScrollPane = new JScrollPane();
		userScrollPane.setBackground(Color.decode("#d6f4f2"));
		userScrollPane.setFont(new Font("宋体", 0, 12));
		userScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		userScrollPane.getViewport().setView(userList);	
		
		//右半部分布局		
		serverInfoPanel = new JPanel(new GridLayout(4, 2));
		serverInfoPanel.setFont(new Font("宋体", 0, 12));
				
		serverNameLable = new JLabel("服务器名称:");
		serverNameLable.setForeground(Color.BLACK);
		serverNameLable.setFont(new Font("宋体", 0, 12));
		serverNameTextField = new JTextField(10);
		serverNameTextField.setBackground(Color.decode("#ffffff"));
		serverNameTextField.setFont(new Font("宋体", 0, 12));
		serverNameTextField.setEditable(false);
		
		IPLable = new JLabel("服务器IP:");
		IPLable.setForeground(Color.BLACK);
		IPLable.setFont(new Font("宋体", 0, 12));
		IPTextField = new JTextField(10);
		IPTextField.setBackground(Color.decode("#ffffff"));
		IPTextField.setFont(new Font("宋体", 0, 12));
		IPTextField.setEditable(false);
		
		portLable = new JLabel("服务器端口:");
		portLable.setForeground(Color.BLACK);
		portLable.setFont(new Font("宋体", 0, 12));
		portTextField = new JTextField("6668", 10);
		portTextField.setBackground(Color.decode("#ffffff"));
		portTextField.setFont(new Font("宋体", 0, 12));
		portTextField.setEditable(false);
		
		numberLable = new JLabel("当前在线人数:");
		numberLable.setForeground(Color.BLACK);
		numberLable.setFont(new Font("宋体", 0, 12));
		numberTextField = new JTextField("0 人", 10);
		numberTextField.setBackground(Color.decode("#ffffff"));
		numberTextField.setFont(new Font("宋体", 0, 12));
		numberTextField.setEditable(false);
		
//		stopButton = new JButton("关闭服务器");
//		stopButton.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				closeServer();
//			}
//		});
//		stopButton.setBackground(Color.WHITE);
//		stopButton.setFont(new Font("宋体", 0, 12));
			
		logLabel = new JLabel("服务器消息");
		logLabel.setForeground(Color.BLACK);
		logLabel.setFont(new Font("宋体", 0, 12));
		
		InfoLabel = new JLabel("信息");
		InfoLabel.setForeground(Color.BLACK);
		InfoLabel.setFont(new Font("宋体", 0, 12));

		logTextArea = new TextArea(20, 50);
		logTextArea.setFont(new Font("宋体", 0, 12));
		
		messageLabel.setBounds(5, 5, 100, 30);
		messageTextArea.setBounds(5, 35, 250, 400);
		userLabel.setBounds(5, 5, 100, 30);
		userScrollPane.setBounds(5, 35, 250, 400);
		
		logLabel.setBounds(260, 5, 100, 30);
		logTextArea.setBounds(260, 35, 500, 400);
		InfoLabel.setBounds(770, 5, 100, 30);
		serverInfoPanel.setBounds(770, 35, 250, 250);
		noticeLabel.setBounds(770, 300, 40, 30);
		noticeTextField.setBounds(770, 330, 250, 30);
		sendButton.setBounds(830, 370, 120, 30);
//		stopButton.setBounds(830, 410, 120, 30);	
		
		//serverPanel.add(messageLabel);
		//serverPanel.add(messageTextArea);
		serverPanel.add(userLabel);
		serverPanel.add(userScrollPane);
		list = new JList();
		list.setListData(new String[] { "" });
		userScrollPane.setViewportView(list);
		serverPanel.add(noticeLabel);
		serverPanel.add(noticeTextField);
		serverPanel.add(sendButton);
		serverInfoPanel.add(numberLable);
		serverInfoPanel.add(numberTextField);
		serverInfoPanel.add(serverNameLable);
		serverInfoPanel.add(serverNameTextField);
		serverInfoPanel.add(IPLable);
		serverInfoPanel.add(IPTextField);
		serverInfoPanel.add(portLable);
		serverInfoPanel.add(portTextField);
		serverPanel.add(InfoLabel);
		serverPanel.add(serverInfoPanel);
		serverPanel.add(logLabel);
		serverPanel.add(logTextArea);
		//serverPanel.add(stopButton);
		
		this.getContentPane().add(serverPanel);
		
		setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	protected void serverMessage() {
		this.serverMessage = noticeTextField.getText();
		noticeTextField.setText("");
	}
	
//	protected void closeServer() {
//		this.dispose();
//	}

}
