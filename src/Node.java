import java.util.Date;

public class Node {
	Node next = null;
	Node prev = null;
	String user = "";   	// user name
	Date date	= null;		// date sent
	String message = "";
	 
	public Node(String user, Date date, String message) {
		this.user = user;
		this.date = date;
		this.message = message;
	}
}
