import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;

import javax.xml.stream.XMLStreamReader;


public class XmppSender {
	
	private XmppConnection connection;
	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;
	private XMLStreamReader parser;
	
	public XmppSender(XmppConnection connection){
		this.connection = connection;
		this.socket = connection.getSocket();
	    this.reader = connection.getReader();
	    this.writer = connection.getWriter();
	    this.parser = connection.getParser();
	}
}
