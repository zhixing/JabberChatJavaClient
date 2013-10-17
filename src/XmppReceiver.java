import java.io.IOException;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;


public class XmppReceiver implements Runnable{
	
	private XmppConnection connection;
	private XMLStreamReader parser;
	
	public XmppReceiver(XmppConnection connection){
		this.connection = connection;
	    this.parser = connection.getParser();
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
		        			System.out.println("XmppReceiver received a non-supported tag: " + localName);
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
					} 
					eventType = parser.next();
			}
		} catch (XMLStreamException e) {
			System.err.println("Error detected when handling a new message. In XmppReceiver");
			e.printStackTrace();
		}
	}
	
	private void handleQuery(){
		
	}
	
	/** senderID is in the format of: sender@email.com/resource 
	 * 
	 * @param senderID
	 * @return senderID minus the resource
	 */
	private String getSenderEmail(String senderID){
		if (senderID.indexOf("/") == -1){
			return senderID;
		} else{
			return senderID.substring(0, senderID.indexOf("/"));
		}
	}
	

	
}
