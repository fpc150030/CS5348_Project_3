import java.util.Date;

public class MessageNode {
	MessageNode next = null;
	MessageNode prev = null;
	String fromUser = "";   	// user name
	Date date	= null;		// date sent
	String message = "";
	public MessageNode() {
		// default constructor to avoid null entry..
	}
	public MessageNode(String user, Date date, String message) {
		this.fromUser = user;
		this.date = date;
		this.message = message;
	}
}
