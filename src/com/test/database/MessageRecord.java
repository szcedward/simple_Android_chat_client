package com.test.database;

public class MessageRecord {
	
	int _id;
	String _sender_ip;
	String _receiver_ip;
	String _time;
	String _content;
	
	public MessageRecord()
	{
		
	}
	
	public MessageRecord(int id, String senderIp, String receiverIp, String time, String content)
	{
		this._id = id;
		this._sender_ip = senderIp;
		this._receiver_ip = receiverIp;
		this._time = time;
		this._content = content;
	}
	
	public MessageRecord(String senderIp, String receiverIp, String time, String content)
	{
		this._sender_ip = senderIp;
		this._receiver_ip = receiverIp;
		this._time = time;
		this._content = content;
	}
	

	public int getId() {
		return _id;
	}
	public void setId(int _id) {
		this._id = _id;
	}
	
	public String getSenderIp() {
		return _sender_ip;
	}
	public void setSenderIp(String _sender_ip) {
		this._sender_ip = _sender_ip;
	}
	
	public String getReceiverIp() {
		return _receiver_ip;
	}
	public void setReceiverIp(String _receiver_ip) {
		this._receiver_ip = _receiver_ip;
	}
	
	public String getTime() {
		return _time;
	}
	public void setTime(String _time) {
		this._time = _time;
	}
	
	public String getContent() {
		return _content;
	}
	public void setContent(String _content) {
		this._content = _content;
	}
}


