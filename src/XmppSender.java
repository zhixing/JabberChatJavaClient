import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Arrays;

import javax.xml.stream.XMLStreamReader;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;


public class XmppSender {
	
	private XmppConnection connection;
	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;
	private XMLStreamReader parser;
	private JabberID jid;
	private String chatID;
	
	public XmppSender(XmppConnection connection){
		this.connection = connection;
		this.socket = connection.getSocket();
	    this.reader = connection.getReader();
	    this.writer = connection.getWriter();
	    this.parser = connection.getParser();
	    this.jid = connection.getJabberID();
	    this.chatID = generateChatID();
	}
	
	/** One-to-one chat session
	 * message format:
	 * 	<message
	       from='romeo@example.net/orchard'
	       id='sl3nx51f'
	       to='juliet@example.com/balcony'
	       type='chat'
	       xml:lang='en'>
     	<body>Neither, fair saint, if either thee dislike.</body>
   		</message>
	 * @param message
	 * @param receiver
	 * @throws IOException
	 */
	public void sendMessageToClient(String message, String receiver) throws IOException {
		StringBuilder temp = new StringBuilder();
		temp.append("<message")
		.append(" from='").append(jid.getJabberID()).append("/").append(jid.getResource()).append("'")
		//.append(" id='" + chatID).append("'")
		.append(" to='" + receiver).append("'")
		.append(" type='").append("chat").append("'")
		.append(" xml:lang='en'>")
		.append(" <body>" + message + "</body>")
		.append("</message>");
		
		String messageToSend = temp.toString();
		System.out.println("Sending this message: " + messageToSend);
		writer.write(messageToSend);
        writer.flush();
	}
	
	public void sendMessageToGroup(){
		
	}
	
    // Generate a random chatID based on current timestamp
	private String generateChatID(){
	    java.util.Date date= new java.util.Date();
	    Timestamp timestamp = new Timestamp(date.getTime());
	    byte[] hashed = DigestUtils.sha1(timestamp.toString());
	    String id = "";
		for(int i = 0; i < hashed.length; i++){
			id += hashed[i];
		}
	    System.out.println("Chat ID is: " + id);
	    return id;
	}
	
}


