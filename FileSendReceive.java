// CS 380 01 Computer Networks
// Jingzhou (George) Zhang, Andrew Barro, Jacob Romero
// Fall 2015
// Started October 1, 2015

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;

public class FileSendReceive {
	
	Scanner Porygon = null;
	JFrame select;
	
	/* Method Name: *default constructor*
	 * Creator: George Zhang
	 * Description:
	 * Ok, so to be honest this entire constructor is kind of a mess.
	 * But basically what it does is set everything up, including the login dialog and mode selection window.
	 * This doesn't load the actual send or receive interface as those will be separate programs, I think.
	 * Technically the mode selection windows is already loaded before the user logs in but it's hidden.
	 * The only way to access it is to log in with a valid username/password combo, I think.
	 * Use a Scanner BufferedReader FileReader combo to load in the list of usernames and passwords from a text file.
	 * Honestly it looks like a lot of stuff but there's actually very little going on here.
	 * Mostly I'm just creating the Swing components and arranging them in the windows.
	 * There's some stuff commented out that I originally used for debugging.
	 */
	
	public FileSendReceive() {
		JButton send;
		JButton receive;
		select = new JFrame( "Choose a mode" );
		select.setSize( 300, 300 );
		select.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		select.setLayout( new GridLayout( 2, 1, 1, 1) );
		send = new JButton( "Send" );
		send.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ae ) {
				startSend();
			}
		});
		receive = new JButton( "Receive" );
		receive.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ae ) {
				try {
					startReceive();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		select.add( send );
		select.add( receive );
		select.setLocationRelativeTo( null );
		select.setVisible( true );
	}
	
	/* Method Name: startSend
	 * Creator: George Zhang
	 * Description:
	 * This just starts up the "Send" program and closes this one.
	 * At the time of writing it's not completed yet.
	 */
	
	public void startSend() {
		FileSend.init();
		select.dispose();
	}
	
	/* Method Name: startReceive
	 * Creator: George Zhang
	 * Description:
	 * Same as startSend. But for receiving things.
	 */
	
	public void startReceive() throws InterruptedException {
		// TODO: Write a class for this
		// JOptionPane.showMessageDialog( select, "This feature is not implemented yet.\nThank you for your patience." );
		FileReceive.init();
		select.dispose();
	}
	
	/* Method Name: main
	 * Creator: George Zhang
	 * Description: 
	 * It calls the constructor.
	 */
	
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( new Runnable() {
			public void run() {
				new FileSendReceive();
			}
		});
	}
}
