
public class SendDriver {

	public static void main(String[] args) {
		Sender s = new Sender();
    	s.searchForClients();
    	
    	Thread t = new Thread(s);
		t.start();	
		
		s.stopReadingText();
    	
    	s.shutDown();
	}
}
