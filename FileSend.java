import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;

public class FileSend {

	JFrame jfrm;
	JDialog login;
	JFormattedTextField IPField;
	JTextField usernameField;
	JPasswordField passwordField;
	JButton SelectFile;
	JButton AsciiArmour;
	JButton Send;
	String ReceiverIP;
	int ReceiverPort;
	String FilePath;
	String KeyPath;
	byte[] EntireFile;
	byte[] ArmoredFile;
	boolean isArmored = false;
	Sender s;
	
	/* Method Name: *default constructor*
	 * Creator: George Zhang
	 * Description:
	 * Ok, so to be honest this entire constructor is kind of a mess.
	 * But basically what it does is set everything up, including the authentication dialog and file send window.
	 * Mostly I'm just creating the Swing components and arranging them in the windows.
	 */
	
	public FileSend() {
		s = new Sender(); // configure port later
    	s.searchForClients();
		loginDialog();
		jfrm = new JFrame( "Send a File" );
		jfrm.setSize( 300,  300 );
		jfrm.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE ); // TODO Same as receiver.
		jfrm.setLocationRelativeTo( null );
		jfrm.setLayout( new GridLayout( 3, 1, 1, 1 ) );
		SelectFile = new JButton( "Select File" );
		AsciiArmour = new JButton( "Ascii Armour" );
		Send = new JButton( "Send File" );
		AsciiArmour.setEnabled( false );
		Send.setEnabled( false );
		SelectFile.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ae ) {
				JFileChooser jfc = new JFileChooser();
				int result = jfc.showOpenDialog( null );
				if ( result == JFileChooser.APPROVE_OPTION ) {
					FilePath = jfc.getSelectedFile().getPath();
					AsciiArmour.setEnabled( true );
					Send.setEnabled( true );
					isArmored = false;
					System.out.println( "DEBUG: File Path is " + FilePath );
					try {
						EntireFile = Files.readAllBytes(Paths.get(FilePath));
					} catch ( IOException ioe ) {
						System.out.println( "How did you even mess this up? Opening the selected file failed." );
					}
				}
			}
		});
		AsciiArmour.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ae ) {
				isArmored = true;
				ArmoredFile = Base64Coding.encode( new String( EntireFile ) ).getBytes();
				JOptionPane.showMessageDialog( jfrm, "Ascii Armoured" );
				System.out.println( "DEBUG: Line 1 of Armor is " + new String( ArmoredFile ) );
				AsciiArmour.setEnabled( false );
			}
		});
		Send.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ae ) {
				FilePacket packet = new FilePacket( EntireFile, saltMD5.computeMD5(EntireFile) );
				s.sendText( "" + packet.fileArray.length );
				if ( isArmored ) {
					s.sendText( "Armoured" );
				} else {
					s.sendText( "Unarmoured" );
				}
				s.sendText( "Finished = True" );
				
				JOptionPane.showMessageDialog( jfrm, "You must choose a key." );
				JFileChooser jfc = new JFileChooser();
				int result = jfc.showOpenDialog( null );
				if ( result == JFileChooser.APPROVE_OPTION ) {
					KeyPath = jfc.getSelectedFile().getPath();
					System.out.println( "DEBUG: Key Path is " + FilePath );
					s.readText();
					if ( s.messages.remove().equals( "Send the file over" ) ) {
						s.sendData( packet, KeyPath );
						System.out.println( "Done sending" );
						AsciiArmour.setEnabled( false );
					}
				}
			}
		});
		jfrm.add( SelectFile );
		jfrm.add( AsciiArmour );
		jfrm.add( Send );
		jfrm.setVisible( true );
	}
	
	/* Method Name: loginDialog
	 * Creator: George Zhang
	 * Description:
	 * This method sets up and displays the authentication dialog.
	 */
	
	public void loginDialog() {
		try {
			MaskFormatter mf = new MaskFormatter( "###.###.###.###:####" );
			IPField = new JFormattedTextField( mf );
			IPField.setValue( "000.000.000.000:0000" );
			IPField.setColumns( 15 );
		} catch ( ParseException pe ) {
			System.out.println( "lmao" );
		}
		login = new JDialog( jfrm, "Login", true );
		login.setSize( new Dimension( 500, 200 ) );
		login.setLocationRelativeTo( null );
		login.setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
		login.setLayout( new GridLayout( 5, 1, 1, 1) );
		login.add( new JLabel( "Please enter your username and password.", SwingConstants.CENTER ) );
		JPanel IPPanel = new JPanel( new FlowLayout() );
		JPanel usernamePanel = new JPanel( new FlowLayout() );
		JPanel passwordPanel = new JPanel( new FlowLayout() );
		IPPanel.add( new JLabel( "Receiver IP" ) );
		usernamePanel.add( new JLabel( "Username " ) );
		passwordPanel.add( new JLabel( "Password " ) );
		IPPanel.add( IPField );
		usernameField = new JTextField( 15 );
		usernamePanel.add( usernameField );
		passwordField = new JPasswordField( 15 );
		passwordField.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ae ) {
				try {
					login();
				} catch (HeadlessException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		passwordPanel.add( passwordField );
		login.add( IPPanel );
		login.add( usernamePanel );
		login.add( passwordPanel );
		JPanel buttonPanel = new JPanel( new GridLayout( 1, 3, 1, 1 ) );
		JButton enter = new JButton( "Login" );
		enter.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ae ) {
				try {
					login();
				} catch (HeadlessException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		JButton forgot = new JButton( "I forgot my password." );
		forgot.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ae ) {
				forgotpassword();
			}
		});
		JButton giveup = new JButton( "I give up." );
		giveup.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ae ) {
				System.exit(0);
			}
		});
		buttonPanel.add( enter );
		buttonPanel.add( forgot );
		buttonPanel.add( giveup );
		login.add( buttonPanel );
		login.setVisible( true );
	}
	
	/* Method Name: init
	 * Creator: George Zhang
	 * Description:
	 * This is the method that is called by FileSendReceive.java. 
	 * It calls the constructor of the program and gets it started.
	 */

	public static void init() {
		new FileSend();
	}
	
	/* Method Name: login
	 * Creator: George Zhang
	 * Description:
	 * This method is called when the user clicks on the "Login" button in the login dialog.
	 * It checks the validity of the credentials with the authenticate method below.
	 * If they're legit it'll dispose of the login dialog and the user will be able to see the mode selection window.
	 */

	public void login() throws HeadlessException, InterruptedException {
		ReceiverIP = (String) IPField.getValue();
		ReceiverPort = ReceiverIP.charAt(19) - '0';
		ReceiverPort = ReceiverPort + 10 * (ReceiverIP.charAt(18) - '0');
		ReceiverPort = ReceiverPort + 100 * (ReceiverIP.charAt(17) - '0');
		ReceiverPort = ReceiverPort + 1000 * (ReceiverIP.charAt(16) - '0');
		ReceiverIP = ReceiverIP.substring(0, 15);
		System.out.println( "DEBUG: IP address is " + ReceiverIP );
		System.out.println( "DEBUG: Port is " + ReceiverPort );
		if ( authenticate( usernameField.getText(), passwordField.getPassword() ) ) {
			login.dispose();
		} else {
			JOptionPane.showMessageDialog( login, "Either your username or password is wrong.\nOr maybe both." );
		}
	}
	
	/* Method Name: authenticate
	 * Creator: George Zhang
	 * Description:
	 * The JPasswordField object actually returns its content as a char[] so I had to convert that to a String first.
	 */
	
	public boolean authenticate( String username, char[] cpassword ) throws InterruptedException {
		String password = "";
		String reply = "";
		for ( int x = 0; x < cpassword.length; x++ ) {
			password = password + cpassword[x];
		}
		s.sendText( username );
		s.sendText( password );
		s.sendText( "Finished = True" );
		s.readText();
		if ( s.messages.isEmpty() ) {
			System.out.println( "Um, there was no reply..." );
			System.exit(1);
		} else {
			reply = s.messages.remove();
		}
		System.out.println( "Reply is " + reply );
		if ( reply.equals( "Pass" ) ) {
			return true;
		}
		return false;
	}
	
	/* Method Name: forgotpassword
	 * Creator: George Zhang
	 * Description:
	 * I don't know why this is here.
	 * I might add some "functionality" to it later but for now I think it works fine.
	 */
	
	public void forgotpassword() {
		JOptionPane.showMessageDialog( login, "Too bad." );
	}
	
	/* Method Name: terminate TODO
	 * Creator: George Zhang
	 * Description:
	 * This is called when the program detects that the other party has gone offline.
	 * Um, I'm still trying to figure out how that detection is supposed to work.
	 */
	
	public void terminate() {
		JOptionPane.showMessageDialog( jfrm, "The other party has gone offline./nThis program will now close." );
		System.exit(0);
	}
	
	/* Method Name: Checksum
	 * Creator: Atai. And also Andrew, kind of. Well, mostly Andrew.
	 * Description:
	 * This takes in a String specifying the location of the file
	 * On the computer and returns a String checksum.
	 */
	
	public byte[][] Checksum( byte[][] chunks ) {
		byte[][] checksum = new byte[chunks.length][16];
		for ( int x = 0; x < chunks.length; x++ ) {
			checksum[x] = saltMD5.computeMD5( chunks[x] );
		}
		return checksum;
	}

	/* Method Name: main
	 * Creator: George Zhang
	 * Description: 
	 * It calls the constructor.
	 * It probably won't be used because FileSendReceive is supposed to call the init method in this program to start it.
	 */
	
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( new Runnable() {
			public void run() {
				new FileSend();
			}
		});
	}
	
	/* Method Name: splitChunks
	 * Creator: George Zhang
	 * Description:
	 * This method splits up the entire file into chunks specified by the chunk size.
	 */
	
	public static byte[][] splitChunks( byte[] entireFile, int chunkSize ) {
		byte[][] chunks = new byte[entireFile.length/chunkSize + 1][chunkSize];
		for ( int x = 0; x < chunks.length; x++ ) {
			for ( int y = 0; y < chunks[0].length; y++ ) {
				if ( entireFile.length > ( x * chunkSize + y) ) { // If there is valid data
					chunks[x][y] = entireFile[x * chunkSize + y];
				}
			}
		}
		return chunks;
	}
	
}
