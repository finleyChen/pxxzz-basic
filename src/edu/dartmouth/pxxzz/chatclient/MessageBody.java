package edu.dartmouth.pxxzz.chatclient;

public class MessageBody {
	private static final String seperator = ",";
	public static final int FLAG_UNLOCK = 0;
	public static final int FLAG_LOCK = 1;
	public static final int FLAG_UPDATE = 2;
	public static final int FLAG_REQUEST = 5;
	public static final int FLAG_ACCEPT = 6;
	public static final int FLAG_REJECT = 7;
	
	
	public int flag;
	public String user_name;
	public String data;
 
	public MessageBody(String s){
		String[] parsed = s.split(seperator);
		this.flag = -1;
		if (parsed.length != 3){
			return;
		}
		flag =  Integer.valueOf(parsed[0]);
		user_name = parsed[1];
		data = parsed[2];
	}
	
	public MessageBody(int flag, String user_name, String data){
		this.flag = flag;
		this.user_name = user_name;
		this.data = data;
	}
	
	public String encodeMessage(){
		return Integer.toString(flag) + seperator + user_name + seperator + data;
	}
	
}