package com.neeraj.blackjack.client;

import java.net.*;
import java.io.*;
import java.util.Scanner;
import javax.swing.*;

public class BlackJackClient {
	/*
	 * User Socket Connections
	 */
	private Socket socket;
	/*
	 * DataOutput stream to send data to the server
	 */
	private DataOutputStream dOS;
	/*
	 * DataInput stream to listen from the server
	 */
	private DataInputStream dIS;
	/*
	 * IP address of the server
	 */
	private String ip = "127.0.0.1";
	/*
	 * Playes name
	 */
	private String userName;
	/*
	 * Initializing the constructor to start the game with getting ip and username from the user
	 */
	public BlackJackClient() {
		JTextField serverAddress = new JTextField();
		JTextField firstName = new JTextField();

		while (ip.isEmpty() || userName == null) {
			final JComponent[] inputs = new JComponent[] { new JLabel("Enter server Address"), serverAddress,
					new JLabel("Enter your Name"), firstName };
			int rc = JOptionPane.showConfirmDialog(null, inputs, "BlackJack Login", JOptionPane.OK_CANCEL_OPTION);
			if (rc == -1 || rc == 2) {
				System.exit(0);
			} else {
				ip = serverAddress.getText();
				userName = firstName.getText();
			}
		}
		while (!ip.matches("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+")) {
			final JComponent[] newInputs = new JComponent[] { new JLabel("Enter server Address"), serverAddress };
			int rc = JOptionPane.showConfirmDialog(null, newInputs, "BlackJack Login", JOptionPane.OK_CANCEL_OPTION);
			if (rc == -1 || rc == 2) {
				System.exit(0);
			} else
				ip = serverAddress.getText();
		}
		connect();
	}
	/*
	 * Connects to the server with the ip and start the thread for conversation
	 */
	private void connect() {
		try {
			socket = new Socket(ip, 7777);
			dOS = new DataOutputStream(socket.getOutputStream());
			dIS = new DataInputStream(socket.getInputStream());
			ClientThread ct = new ClientThread(dIS, dOS, userName);
			Thread thread = new Thread(ct);
			thread.start();
			dOS.writeUTF("NAME/" + userName);
		} catch (IOException e) {
			this.dIS = null;
			this.dOS = null;
			System.out.println("Unable to connect to the address: " + ip + ": 7777 | Starting a server");
		}
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		BlackJackClient jackClient = new BlackJackClient();
	}

}
