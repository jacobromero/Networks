import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Random;

public class FileReceive {
	
	static JFrame jfrm;
	static JLabel text;
	
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
		Receiver r = new Receiver();
		r.listen();
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
			keepchecking = true;
			while (keepchecking) { 
				Thread.sleep(50);
				if ( ! r.messages.isEmpty() ) {
					password = r.messages.remove();
					keepchecking = false;
				}
			}
			keepchecking = true;
			System.out.println( username + " " + password );
			r.stopReadingText();
			r.shutDown();
			t.suspend();
			ArrayList<String> ALusernames = new ArrayList<String>();
			ArrayList<String> ALhashes = new ArrayList<String>();
			String[] usernames;
			String[] hashes;
			try{
				String text = "";
	            BufferedReader in = new BufferedReader(new FileReader(new File("users.txt")));
	            text = in.readLine();
				while ( text != null ) {
					ALusernames.add( text );
					ALhashes.add(in.readLine());
					in.readLine();
				}
				in.close();
			}catch(Exception e){
				e.printStackTrace();
			}
			usernames = ALusernames.toArray( new String[ ALusernames.size() ] );
			hashes = ALhashes.toArray( new String[ ALhashes.size() ] );
			authenticated = check( username, password, usernames, hashes );
			if ( authenticated ) {
				r.sendText( "Pass" );
			} else {
				r.sendText( "rekt faggot" );
			}
		}
		r.stopReadingText();
		r.shutDown();
		return true;
	}
	
	/* Method Name: check
	 * Creator: George Zhang
	 * Description:
	 * This takes in the user/pass combo and checks to see if it matches any of the existing users.
	 */
	
	public static boolean check( String username, String password, String[] usernames, String[] hashes ) {
		for ( int x = 0; x < usernames.length; x++ ) {
			if ( username.equals( usernames[x] ) ) {
				String salt = Base64Coding.decode(CreateUser.parseSalt(hashes[x]));
				String hash = Base64Coding.decode(CreateUser.parseHash(hashes[x]));
				String calculatedHash = saltMD5.toHexString(saltMD5.computeMD5((salt + password).getBytes()));
				if ( hash.equals( calculatedHash ) ) {
					return true;
				}
			}
		}
		return false;
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
		text.setText("3");
		Thread t = new Thread(r);
		text.setText("4");
		t.start();
		text.setText("5");
		r.listen();
		text.setText("6");
		boolean keepchecking = true;
		JOptionPane.showMessageDialog( jfrm, "ABOOT TO ENTER THE LOOP" );
		while (keepchecking) { 
			if ( ! r.messages.isEmpty() ) {
				JOptionPane.showMessageDialog( jfrm, r.messages.remove());
				keepchecking = false;
			}
		}
		FilePacket packet = r.receiveData();
		text.setText( "3" );
		r.stopReadingText();
		r.shutDown();
		t.stop();
	}
}
