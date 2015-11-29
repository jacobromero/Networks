import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Random;

public class FileReceive {
	
	static JFrame jfrm;
	static JLabel text;
	static String KeyPath;
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
		System.out.println( "Receiver is in the authenticate function" );
		r = new Receiver();
		r.listen();
		boolean authenticated = false;
		while (!authenticated) {
			r.readText();
			username = r.messages.remove();
			password = r.messages.remove();
			System.out.println( username + " " + password );
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
					text = in.readLine();
				}
				in.close();
			}catch(Exception e){
				e.printStackTrace();
			}
			usernames = ALusernames.toArray( new String[ ALusernames.size() ] );
			hashes = ALhashes.toArray( new String[ ALhashes.size() ] );
			authenticated = check( username, password, usernames, hashes );
			System.out.println( "Authenticated: " + authenticated );
			if ( authenticated ) {
				r.sendText( "Pass" );
				r.sendText( "Finished = True" );
			} else {
				r.sendText( "rekt faggot" );
				r.sendText( "Finished = True" );
			}
		}
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
				String salt = CreateUser.parseSalt(hashes[x]);
				String hash = Base64Coding.decode(CreateUser.parseHash(hashes[x]));
				password = salt + password;
				String calculatedHash = saltMD5.toHexString(saltMD5.computeMD5(password.getBytes()));
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
		while ( ! authenticated ) {
			authenticated = authenticate();
		}
		r.readText();
		boolean armoured;
		if ( ! r.messages.isEmpty() ) {
			r.fileLength = Integer.parseInt(r.messages.remove());
			if ( r.messages.remove().equals( "Armoured" ) ) {
				armoured = true;
			} else {
				armoured = false;
			}
		}
		boolean keySelected = false;
		while ( ! keySelected ) {
			JOptionPane.showMessageDialog( jfrm, "What is the key for the data?" );
			JFileChooser jfc = new JFileChooser();
			int result = jfc.showOpenDialog( null );
			if ( result == JFileChooser.APPROVE_OPTION ) {
				KeyPath = jfc.getSelectedFile().getPath();
				System.out.println( "DEBUG: Key Path is " + KeyPath );
				r.sendText( "Send the file over" );
				r.sendText( "Finished = True" );
				FilePacket packet = r.receiveData( KeyPath, false );
				if ( packet == null ) {
					System.out.println( "The file length is supposedly " + r.fileLength );
					System.out.println( "We got nothing" );
					System.exit(1);
				}
				r.stopReadingText();
				r.shutDown();
				keySelected = true;
				
				FileOutputStream fos;
				try {
					fos = new FileOutputStream("test.txt");
					BufferedOutputStream bos = new BufferedOutputStream(fos);
				 	bos.write(packet.fileArray);
					bos.flush();
					bos.close();
					fos.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} //insert output file name here
					catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		System.out.println( "You reached the end lmao");
	}
}
