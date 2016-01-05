package com.Neeraj.BlackJack;

import java.io.*;
import java.net.*;
/**
 * An implementation for the Server Creation
 * 
 * @author neeraj
 * @since 15-Oct-2015
 *
 */
public class DealerServer {
	/*
	 * User Socket Connections
	 */
	private Socket socket;
	/*
	 * DataOutput stream to send data to the users
	 */
	private DataOutputStream dOS;
	/*
	 * DataInput stream to listen from the users
	 */
	private DataInputStream dIS;
	/*
	 * Server socket  for the server
	 */
	private ServerSocket serverSocket;
	/*
	 * Server's port no 
	 */
	private int port = 7777;
	/*
	 * Thread for every player who will join the game 
	 * Upto ten players can join a game
	 */
	private DealersThread[] dealerT = new DealersThread[20];
	/*
	 * Array of the Players
	 * In player users are associated with there cards
	 */
	private Player[] players = new Player[20];
	/*
	 * Array of the Groups
	 * Five groups can run simultaneously
	 */
	private Groups[] group = new Groups[5];
	/*
	 * Arrays of deck one deck for each group
	 */
	private Deck[] theDeck = new Deck[5];
	/*
	 * Constructor of this class 
	 * To initialize the Server
	 */
	public DealerServer() {
		initializeServer();
	}
	/*
	 *Listen to the port provided and on every connection 
	 *Create a socket for the users and give them threads 
	 */
	private void initializeServer() {
		try {
			serverSocket = new ServerSocket(port);
			while (true) {
				socket = serverSocket.accept();
				for (int i = 0; i < 10; i++) {
					System.out.println("Socket address : " + socket.getInetAddress());
					dOS = new DataOutputStream(socket.getOutputStream());
					dIS = new DataInputStream(socket.getInputStream());
					if (dealerT[i] == null) {
						dealerT[i] = new DealersThread(dOS, dIS, dealerT, players, group, theDeck);
						Thread thread = new Thread(dealerT[i]);
						thread.start();
						break;
					}
				}
			}
		} catch (Exception e) {
			this.dIS = null;
			this.dOS = null;
		}
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		DealerServer ds = new DealerServer();
	}
}
