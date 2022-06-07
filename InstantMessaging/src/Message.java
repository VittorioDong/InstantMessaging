import java.io.Serializable;
import java.util.Vector;

public class Message implements Serializable {
	private static final long serialVersionUID = -3831507106408529855L;	
	
	public String notice;//公告
	public Vector userOnlineVector;//用户在线对象集
	public Vector chatVector;//聊天信息集
}