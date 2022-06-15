DEMO
===========================

# InstantMessaging

## 简介

The socket in Java is used to realize the basic functions of the online chat room. There are client and server windows. The main functions are login, registration and chat. It can support multiple users to chat online at the same time. Each user can chat in groups and send messages to a user privately.

## 文件说明

bin:存放编译生成的.class文件

src:存放源代码.java文件

RegisterInformation.txt：聊天室用户注册信息

## 第三方库依赖

### Java库

- java.util
- java.net
- java.io
- java.awt
- javax.swing

## 使用手册

1. 启动服务器：在服务器端运行Server.java，启动服务器。服务器界面包括了在线用户列表、服务器消息、当前在线人数、服务器名称、服务器IP、服务器端口号等信息。
2. 注册：运行Client.java，出现如下登录窗口，输入服务器IP，点击“注册”。输入用户名、密码、确认密码，输入完成后点击确定完成注册。
3. 登录：返回登录界面，输入用户名、密码进行登录（注意服务器IP要正确，要和服务器端的IP一致），登录成功后进入聊天室界面。
4. 群发功能：在下方的输入框中输入想要发送的内容，点击“发送”即可，在线用户均会看到此消息。
5. 私发功能：双击“在线用户”列表中想要发消息的对象，在右下角“发送对象”框中选择该对象，并选中“私发”，输入消息后点击“发送”。
6. 退出聊天室：单击右上角的“关闭”按钮即可退出聊天室。

## 其他信息

作者: 董哲镐

版本: 1.0.0

时间: Jun 14 2022 
