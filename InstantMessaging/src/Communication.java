import java.io.Serializable;

public class Communication implements Serializable{
	private static final long serialVersionUID = 5334870812888103579L;
	
	public String chatMessage;//消息内容
	public boolean privateChat;//是否是私聊
	public String chatUser;//发言人的用户名
	public String chatToUser;//聊天对象的用户名
}
