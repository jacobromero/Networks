public class SendDriver {

	public static void main(String[] args) {
		Sender s = new Sender();
    	s.searchForHost();
    	
    	Thread t = new Thread(s);
		t.start();
		
		s.sendText("Hi");
    	
//    	s.shutDown();
	}
}
