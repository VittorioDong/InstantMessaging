import java.io.*;
import java.net.*;
import java.util.*;

public class Server extends Thread {

	private ServerSocket serverSocket;
	private ServerFrame serverFrame;
	private static Vector userOnline = new Vector(1, 1);
	private static Vector v = new Vector(1, 1);
	//构造函数
	public Server() {
		serverFrame = new ServerFrame();
		try {
			serverSocket = new ServerSocket(6668);// 分配端口号6668
			InetAddress address = InetAddress.getLocalHost();
			serverFrame.serverNameTextField.setText(address.getHostName());// 获取服务器名
			System.out.println(address.getHostName());
			serverFrame.IPTextField.setText(address.getHostAddress());// 获取服务器IP地址
			System.out.println(address.getHostAddress());
			serverFrame.portTextField.setText("6668");
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.start(); // 启动线程
	}

	public void run() {

		try {
			while (true) {
				Socket client = serverSocket.accept();
				new Connection(serverFrame, client, userOnline, v); // 支持多线程
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Server();

	}

}
