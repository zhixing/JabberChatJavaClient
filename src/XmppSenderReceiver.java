import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Random;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.codec.digest.DigestUtils;


public class XmppSenderReceiver implements Runnable{
	private XmppConnection connection;
	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;
	private XMLStreamReader parser;
	private JabberID jid;
	private String threadID;
	
	private String currentRecepientResource = "";
		
	public XmppSenderReceiver(XmppConnection connection){
		this.connection = connection;
		this.socket = connection.getSocket();
	    this.reader = connection.getReader();
	    this.writer = connection.getWriter();
	    this.parser = connection.getParser();
	    this.jid = connection.getJabberID();
	    this.threadID = generateThreadID();
	}
	
	@Override
	public void run() {
		
		while(true){
			
			// Get the next XML event
	        int eventType = parser.getEventType();
	        
	        while ( eventType != XMLStreamConstants.END_DOCUMENT ) {
	        	
	            // Only process XML start tag
	        	if ( eventType == XMLStreamConstants.START_ELEMENT ){
	        		// getLocalName only applies to START_ELEMENT or END_ELEMENT, or ENTITY_REFERENCE
	        		// Hence must be inside the if block
		        	String localName = parser.getLocalName();

		        	switch (localName){
		        		case "message":
		        			handleNewMessage();
		        			break;
		        		case "query":
		        			handleQuery();
		        			break;
		        		default:
		        			//System.out.println("XmppReceiver received a non-supported tag: " + localName);
		        			break;
		        	}
	        	}
	            
	            // Get the next XML event from the parser
	            try {
					eventType = parser.next();
				} catch (XMLStreamException e) {
					System.err.println("Error detected when trying to get the next eventType from parser. In XmppReceiver");
					e.printStackTrace();
				}
	        }
		}
	}
	
	private void handleNewMessage(){
		try {
			
			// Firstly, traverse through all attributes on this START_ELEMENT, and see who is the sender
			int count = parser.getAttributeCount();
			String sender = "";
			String thread = "";
			
			for (int i = 0; i < count; i++) {
				// Returns the localName of the attribute at the provided index
				if (parser.getAttributeLocalName(i).equals("from")) {
					sender = parser.getAttributeValue(i);
				}
			}
			
			// Secondly, traverse through all XML events on the parser, and look for all the "body"
			
			// Get the next XML event from the parser. Searching for <body> inside the <message ...></message>
			int eventType = parser.next();
			
			// While it's not the closing tag: </message>:
		
			while (eventType != XMLStreamConstants.END_DOCUMENT || !parser.getLocalName().equals("message")) {
					// Check if the parse event is a XML start tag, and it's a "body"
					if (eventType == XMLStreamConstants.START_ELEMENT && parser.getLocalName().equals("body")) {
							System.out.println(getSenderEmail(sender) + " says: " + parser.getElementText());	
							break;
					}
					eventType = parser.next();
			}
			
			
		} catch (XMLStreamException e) {
			System.err.println("Error detected when handling a new message. In XmppReceiver");
			e.printStackTrace();
		}
	}
	
	private void handleQuery(){
		boolean done = false;
		System.out.println("Contact list:");
		while (!done) {

			int eventType;
			try {
				eventType = parser.next();
				if (eventType == XMLStreamConstants.START_ELEMENT) {

					if (parser.getLocalName().equals("item")) {
						// Add the authentication mechanism to the list
						System.out.println(parser.getAttributeValue(0));

					}
				} else if (eventType == XMLStreamConstants.END_ELEMENT) {
					if (parser.getLocalName().equals("query")) {
						done = true;
					}
				}
			} catch (XMLStreamException e) {
				System.err.println("Error detected when parsing the query. In XmppReceiver");
				e.printStackTrace();
			}
		}
	}
	
	
	/** senderID is in the format of: sender@email.com/resource 	 */
	private String getSenderEmail(String senderID){
		if (senderID.indexOf("/") == -1){
			return senderID;
		} else{
			return senderID.substring(0, senderID.indexOf("/"));
		}
	}
	
	/** senderID is in the format of: sender@email.com/resource 
	 * 
	 * 	@return "/resource"
	 */
	private String getSenderResource(String senderID){
		if (senderID.indexOf("/") == -1){
			return "";
		} else{
			return senderID.substring(senderID.indexOf("/"));
		}
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
		.append(" to='" + receiver).append(currentRecepientResource).append("'")
		.append(" type='").append("chat").append("'")
		.append(" xml:lang='en'>")
		.append(" <body>" + message + "</body>")
		.append("<thread>" + threadID + "</thread>")
		.append("</message>");
		
		String messageToSend = temp.toString();
		
		//System.out.println("Sending this message: " + messageToSend);
		
		writer.write(messageToSend);
        writer.flush();
        
	}
	
	/**
	 * Message format:
	 * <iq from='juliet@example.com/balcony'
	          id='bv1bs71f'
	          type='get'>
	       <query xmlns='jabber:iq:roster'/>
     	</iq>
	 */
	public void sendRoasterRequest() throws Exception{
		StringBuilder temp = new StringBuilder();
		temp.append("<iq")
		.append(" from='").append(jid.getJabberID()).append("/").append(jid.getResource()).append("'")
		.append(" id='" + threadID).append("'")
		.append(" type='").append("get").append("'>")
		.append(" <query xmlns='jabber:iq:roster'/>")
		.append("</iq>");
		
		String messageToSend = temp.toString();
		System.out.println("Sending this query: " + messageToSend);
		writer.write(messageToSend);
        writer.flush();
	}
	
    // Generate a random chatID based on current timestamp
	private String generateThreadID(){
	    Random rn = new Random();
	    return Integer.toString(rn.nextInt());
	}
}
