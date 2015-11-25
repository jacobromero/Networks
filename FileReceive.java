import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

public class FileReceive {
	
	static JFrame jfrm;
	static JLabel text;
	static Receiver r;
	
	/* Method Name: *default constructor*
	 * Creator: George Zhang
	 * Description:
	 * Creates the window and components.
	 */
	
	public FileReceive() throws InterruptedException {
		Random rand = new Random();
		int result = rand.nextInt(3);
		jfrm = new JFrame( "Receive a File" );
		if ( result == 0 ) {
			System.out.println( "0" );
			jfrm.setSize( 386,  337 );
			jfrm.add( new JLabel( new ImageIcon( "resources\\waiting gif.gif" ) ) );
		} else if ( result == 1 ) {
			System.out.println( "1" );
			jfrm.setSize( 256, 228 );
			jfrm.add( new JLabel( new ImageIcon( "resources\\cat spinning.gif" ) ) );
		} else if ( result == 2 ) {
			System.out.println( "2" );
			jfrm.setSize( 416, 283 );
			jfrm.add( new JLabel( new ImageIcon( "resources\\motorcycle.gif" ) ) );
		}
		jfrm.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE ); // TODO Call a method to notify other party.
		jfrm.setLocationRelativeTo( null );
		text = new JLabel( "Waiting for a file...", SwingConstants.CENTER );
		jfrm.add( text, BorderLayout.SOUTH );
		jfrm.setVisible( true );
	}
	
	/* Method Name: authenticate
	 * Creator: George Zhang
	 * Description:
	 * This method waits for the other side to pass a username and a password
	 * It then checks if the combination is valid.
	 */
	
	public static boolean authenticate() throws InterruptedException {
		String username = "";
		String password = "";
		
		
		//don't need since already listened in main method, and found server then
//		Receiver r = new Receiver();
//		r.listen();
		
		
		Thread t = new Thread(r);
		t.start();
		boolean authenticated = false;
		boolean keepchecking = true;
		while (!authenticated) {
			while (keepchecking) { 
				Thread.sleep(50);
				if ( ! r.messages.isEmpty() ) {
					username = r.messages.remove();
					keepchecking = false;
				}
			}
			
			while (keepchecking) { 
				Thread.sleep(50);
				if ( ! r.messages.isEmpty() ) {
					password = r.messages.remove();
					keepchecking = false;
				}
			}
			keepchecking = true;
			System.out.println( username + " " + password );
			// TODO: Authenticate with the salted hash
			authenticated = username.equals( "George" );
			if ( authenticated ) {
				r.sendText( "Pass" );
			} else {
				r.sendText( "rekt faggot" );
			}
		}
		t.interrupt();
		r.stopReadingText();
		
		//don't shutdown connection here
//		r.shutDown();
		
		return true;
	}
	
	/* Method Name: init
	 * Creator: George Zhang
	 * Description:
	 * This is the method that is called by FileSendReceive.java. 
	 * It calls the constructor of the program and gets it started.
	 */

	public static void init() throws InterruptedException {
		new FileReceive();
	}
	
	/* Method Name: main
	 * Creator: George Zhang
	 * Description: 
	 * It calls the constructor.
	 * It probably won't be used because FileSendReceive is supposed to call the init method in this program to start it.
	 */
	
	public static void main( String[] args ) throws InvocationTargetException, InterruptedException {
		SwingUtilities.invokeAndWait( new Runnable() {
			public void run() {
				try {
					new FileReceive();
					r = new Receiver();
					r.listen();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		
		networkPortion();
	}
	
	public static void networkPortion() throws InterruptedException {
		boolean authenticated = false;
		text.setText( "1" );
		while ( ! authenticated ) {
			authenticated = authenticate();
		}
		text.setText( "2" );
		Receiver r = new Receiver();
		Thread t = new Thread(r);
		t.start();
		r.listen();
		boolean keepchecking = true;
		JOptionPane.showMessageDialog( jfrm, "ABOOT TO ENTER THE LOOP" );
		while (keepchecking) { 
			Thread.sleep(50);
			if ( ! r.messages.isEmpty() ) {
				JOptionPane.showMessageDialog( jfrm, r.messages.remove());
				keepchecking = false;
			}
		}
		FilePacket packet = r.receiveData();
		text.setText( "3" );
		r.stopReadingText();
		r.shutDown();
		
		//user .interrupt method, same as shutdown, but not deprecated
		t.interrupt();
	}
}
