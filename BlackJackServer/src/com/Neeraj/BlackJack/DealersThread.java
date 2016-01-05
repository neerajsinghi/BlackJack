package com.Neeraj.BlackJack;

import java.io.*;
import java.util.ArrayList;

/**
 * An implementation for the Server threads for users
 * 
 * @author neeraj
 * @since 15-Oct-2015
 *
 */
public class DealersThread implements Runnable {
	/*
	 * DataOutput stream to send data to the users
	 */
	private DataOutputStream dataOS;
	/*
	 * DataInput stream to listen from the users
	 */
	private DataInputStream dataIS;
	/*
	 * Array of deck for the groups
	 */
	private Deck[] deck = new Deck[5];
	/*
	 * Array of players who can connect to the server
	 */
	private Player[] player = new Player[20];
	/*
	 * Array of thread for each users
	 */
	private DealersThread[] dealerT = new DealersThread[20];
	/*
	 * user name of the user connected  to this thread
	 */
	private String pName;
	/*
	 * Array of group in which we can start a game
	 */
	private Groups[] group = new Groups[5];

	/*
	 * Dealer threads constructor for initializing various variables
	 */
	public DealersThread(DataOutputStream dOS, DataInputStream dIS, DealersThread[] dThreads, Player[] players,
			Groups[] groups, Deck[] theDeck) {
		this.dataOS = dOS;
		this.dataIS = dIS;
		this.dealerT = dThreads;
		this.deck = theDeck;
		this.player = players;
		this.group = groups;
	}
	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while (true) {
			try {

				String message = dataIS.readUTF();
				System.out.println(message);
				String[] tokens = message.split("/");
				if (tokens[0].equals("NAME")) {
					pName = tokens[1];
					System.out.println(pName);
					int j = 0;
					for (int i = 0; i < 10; i++) {
						if (dealerT[i] != null) {
							if (player[i] == null) {
								player[i] = new Player(pName);
								j = i;
								break;
							}
						}
					}
					dealerT[j].dataOS.writeUTF("ID/" + j + "/Name/" + pName);
				} else if (tokens[0].equals("GAMETYPE")) {

					seletTypeOfGame(tokens);
				} else if (tokens[0].equals("GROUPNAME")) {

					createNewGroup(tokens);
				} else if (tokens[0].equals("GROUPNUMBER")) {
					
					startGame(tokens);
				} else if (tokens[0].equals("HIT")) {
					
					playersMove(tokens);
				} else if (tokens[0].equals("BUSTED")) {
					
					playerBusted(tokens);
				} else if (tokens[0].equals("CONTINUE")) {
					
					playerWantsToContinue(tokens);
				} else if (tokens[0].equals("REMOVEUSER")) {
					Integer userID = new Integer(tokens[4]);
					Integer groupID = new Integer(tokens[2]);
					group[groupID].removeUserFromGroup(userID);

				}
			} catch (Exception e) {
				this.dataOS = null;
				this.dataIS = null;
			}

		}
	}

	



	

	
	/*
	 * Select the type of game by users
	 */
	private void seletTypeOfGame(String[] tokens) throws Exception {
		if (tokens[1].toUpperCase().equals("N")) {
			Integer userID = new Integer(tokens[3]);
			int j = 1;
			for (int i = 0; i < 5; i++) {

				if (group[i] != null) {
					j++;
				}
			}
			if (j < 5) {
				dealerT[userID].dataOS.writeUTF("GROUPAVAILABILITTY/AVAILABLE");
			} else {
				dealerT[userID].dataOS.writeUTF("GROUPAVAILABILITTY/UNAVAILABLE");
			}

		}
		if (tokens[1].toUpperCase().contains("J")) {
			Integer userID = new Integer(tokens[3]);
			int j = 0;
			dealerT[userID].dataOS.writeUTF("GROUP/");
			for (int i = 0; i < 5; i++) {
				if (group[i] != null) {
					j++;
					dealerT[userID].dataOS.writeUTF(group[i].sendGroupName() + "/" + i);
				} else {
					break;
				}
			}
			if (j > 0) {
				dealerT[userID].dataOS.writeUTF("LIST/SENT");
			} else if (j == 0) {
				dealerT[userID].dataOS.writeUTF("LIST/CREATE");
			}
		}
	}
	/*
	 * Create new group and a user
	 */
	private void createNewGroup(String[] tokens) throws Exception {
		Integer userID = new Integer(tokens[5]);
		for (int i = 0; i < 5; i++) {
			if (group[i] == null) {
				group[i] = new Groups(tokens[1], Integer.parseInt(tokens[3]), Integer.parseInt(tokens[5]));
				dealerT[userID].dataOS.writeUTF("GROUPSTATUS/CREATED/" + i);
				break;
			}
		}
	}
	/*
	 * Start the first game
	 */
	@SuppressWarnings("unused")
	private void startGame(String[] tokens) throws Exception{


		Integer grpNo = new Integer(tokens[1]);
		Integer userID = new Integer(tokens[3]);
		deck[grpNo] = new Deck(1, true);
		String[] allPlayerVisibleHand = new String[10];
		String[] allPlayerInVisibleHand = new String[10];
		if (group[grpNo].addUsers(userID) == 0) {
			int[] members = group[grpNo].getMembersOfGroups();
			for (int i = 0; i < group[grpNo].getSizeOfGroup(); i++) {
				player[members[i]].addACard(deck[grpNo].dealNextCard());
			}
			for (int i = 0; i < group[grpNo].getSizeOfGroup(); i++) {
				player[members[i]].addACard(deck[grpNo].dealNextCard());
			}
			int j = 0;
			for (int i = 0; i < group[grpNo].getSizeOfGroup(); i++) {
				String[] cardString = player[members[i]].printHand(true);
				for (String string : cardString) {
					if (string != null) {
						if (j == 0) {
							allPlayerVisibleHand[i] = string + "/";
							j++;
						} else {
							allPlayerVisibleHand[i] += string + "/";
						}
					} else
						break;
				}
				j = 0;
			}
			j = 0;
			for (int i = 0; i < group[grpNo].getSizeOfGroup(); i++) {
				String[] cardString = player[members[i]].printHand(false);
				for (String string : cardString) {
					if (string != null) {
						if (j == 0) {
							allPlayerInVisibleHand[i] = string + "/";
							j++;
						} else
							allPlayerInVisibleHand[i] += string + "/";
					} else
						break;
				}
				j = 0;
			}
			for (int i = 0; i < group[grpNo].getSizeOfGroup(); i++) {
				/*
				 * if (members[i] != i && allPlayerInVisibleHand[i]
				 * != null) { } else {
				 */
				dealerT[members[i]].dataOS.writeUTF(allPlayerVisibleHand[i]);
				dealerT[members[i]].dataOS
						.writeUTF(dealerT[members[i]].pName + " is   " + player[members[i]].getHandsSum());

			}
			for (j = 0; j < group[grpNo].getSizeOfGroup(); j++) {
				int k = 0;
				for (int i : members) {
					dealerT[members[j]].dataOS.writeUTF(allPlayerInVisibleHand[k]);
					k++;
				}

			}
			for (int i = 0; i < group[grpNo].getSizeOfGroup(); i++) {

				dealerT[members[i]].dataOS.writeUTF("STARTGAME/Now/" + player[members[i]].getHandsSum());

			}

		} else {
			dealerT[userID].dataOS.writeUTF("STARTGAME/Wait/N");
		}
	
	}
	/*
	 * Players move hit or stand
	 */
	private void playersMove(String[] tokens) throws Exception{

		Integer userID = new Integer(tokens[3]);
		Integer groupID = new Integer(tokens[5]);

		int j = 0;
		if (tokens[1].equals("H") && !group[groupID].gameCheck()) {
			player[userID].addACard(deck[groupID].dealNextCard());
			String[] cardString = player[userID].printHand(true);
			String usersHand = new String();
			for (String string : cardString) {
				if (string != null) {
					if (j == 0) {
						usersHand = string + "/";
						j++;
					} else
						usersHand += string + "/";
				} else
					break;
			}
			j = 0;
			dealerT[userID].dataOS.writeUTF(usersHand);
			dealerT[userID].dataOS.writeUTF("STARTGAME/Now/" + player[userID].getHandsSum());

		} else if (tokens[1].equals("S") && !group[groupID].gameCheck()) {
			int k = group[groupID].getTotalMembersOfGroups();
			player[userID].changePlayerStandStatus(true);
			int[] members = group[groupID].getMembersOfGroups();
			for (j = 0; j < group[groupID].getSizeOfGroup(); j++) {
				if (player[members[j]].getPlayerStandStatus()) {
					k--;
				}
			}
			if (k <= 0) {
				ArrayList<Integer> results = decideresult(groupID);
				for (int result : results) {
					String[] cardString = player[result].printHand(true);
					for (int i = 0; i < group[groupID].getSizeOfGroup(); i++) {
						dealerT[members[i]].dataOS
								.writeUTF("Winner of this game is " + dealerT[result].pName + " with  ");
						String usersHand = new String();
						for (String string : cardString) {
							if (string != null) {
								if (j == 0) {
									usersHand = string + "/";
									j++;
								} else
									usersHand += string + "/";
							} else
								break;
						}
						j = 0;
						group[groupID].resetRestartVariables();
						dealerT[members[i]].dataOS
								.writeUTF("WINNER/" + usersHand + "/With " + player[result].getHandsSum());
					}

				}
				for (int i = 0; i < group[groupID].getSizeOfGroup(); i++) {
					dealerT[members[i]].dataOS.writeUTF("RESTART/Game");
					}
			} else
				dealerT[userID].dataOS.writeUTF("STARTGAME/Wait/S");
		}
	}
	/*
	 * When player gets busted
	 */
	private void playerBusted(String[] tokens) throws Exception{

		Integer groupID = new Integer(tokens[4]);
		Integer userID = new Integer(tokens[2]);
		player[userID].changePlayerStandStatus(true);
		int j = 0;
		if (group[groupID].addBusted(userID) && !group[groupID].gameCheck()) {
			ArrayList<Integer> results = decideresult(groupID);
			int[] members = group[groupID].getMembersOfGroups();
			for(int result : results){
			String[] cardString = player[result].printHand(true);
			for (int i = 0; i < group[groupID].getSizeOfGroup(); i++) {
				dealerT[members[i]].dataOS
						.writeUTF("Winner of this game is " + dealerT[result].pName + " with  ");
				String usersHand = new String();

				for (String string : cardString) {
					if (string != null) {
						if (j == 0) {
							usersHand = string + "/";
							j++;
						} else
							usersHand += string + "/";
					}
				}
				j = 0;
				group[groupID].resetRestartVariables();
				dealerT[members[i]].dataOS
						.writeUTF("WINNER/" + usersHand + "/With " + player[result].getHandsSum());
			}
			
			}
			for (int i = 0; i < group[groupID].getSizeOfGroup(); i++) {
				dealerT[members[i]].dataOS.writeUTF("RESTART/Game");
				}
		}
	}
	
	/*
	 * If player want to continue
	 */
	@SuppressWarnings("unused")
	private void playerWantsToContinue(String[] tokens) throws Exception {

		Integer userID = new Integer(tokens[3]);
		Integer groupID = new Integer(tokens[5]);
		String[] allPlayerVisibleHand = new String[10];
		String[] allPlayerInVisibleHand = new String[10];
		if (tokens[1].equals("Y")) {
			player[userID].emptyHand();
			if (group[groupID].restartGame()) {
				deck[groupID] = new Deck(1, true);
				int[] members = group[groupID].getMembersOfGroups();
				for (int i = 0; i < group[groupID].getSizeOfGroup(); i++) {
					player[members[i]].addACard(deck[groupID].dealNextCard());
				}
				for (int i = 0; i < group[groupID].getSizeOfGroup(); i++) {
					player[members[i]].addACard(deck[groupID].dealNextCard());
				}
				int j = 0;
				for (int i = 0; i < group[groupID].getSizeOfGroup(); i++) {
					String[] cardString = player[members[i]].printHand(true);
					for (String string : cardString) {
						if (string != null) {
							if (j == 0) {
								allPlayerVisibleHand[i] = string + "/";
								j++;
							} else {
								allPlayerVisibleHand[i] += string + "/";
							}
						} else
							break;
					}
					j = 0;
				}
				j = 0;
				for (int i = 0; i < group[groupID].getSizeOfGroup(); i++) {
					String[] cardString = player[members[i]].printHand(false);
					for (String string : cardString) {
						if (string != null) {
							if (j == 0) {
								allPlayerInVisibleHand[i] = string + "/";
								j++;
							} else
								allPlayerInVisibleHand[i] += string + "/";
						} else
							break;
					}
					j = 0;
				}
				for (int i = 0; i < group[groupID].getSizeOfGroup(); i++) {
					dealerT[members[i]].dataOS.writeUTF(allPlayerVisibleHand[i]);
					dealerT[members[i]].dataOS.writeUTF(
							dealerT[members[i]].pName + " is   " + player[members[i]].getHandsSum());

				}
				for (j = 0; j < group[groupID].getSizeOfGroup(); j++) {
					int k = 0;
					for (int i : members) {
						dealerT[members[j]].dataOS.writeUTF(allPlayerInVisibleHand[k]);
						k++;
					}

				}
				for (int i = 0; i < group[groupID].getSizeOfGroup(); i++) {

					dealerT[members[i]].dataOS
							.writeUTF("STARTGAME/Now/" + player[members[i]].getHandsSum());

				}

			} else {
				dealerT[userID].dataOS.writeUTF("STARTGAME/Wait/N");
			}
		} else {
			player[userID].emptyHand();
			group[groupID].restart();
			group[groupID].removeUserFromGroup(userID);
		}
	}
	/*
	 * Decider decide the list of winners
	 */
	public ArrayList<Integer> decideresult(int groupID) {
		int temp = 0;
		int winner = 0;
		ArrayList<Integer> winners=new ArrayList<Integer>();
		int[] members = group[groupID].getMembersOfGroups();
		for (int j = 0; j < group[groupID].getSizeOfGroup(); j++) {
			if (player[members[j]].getHandsSum() > temp && player[members[j]].getHandsSum() <= 21) {
				temp = player[members[j]].getHandsSum();
				winner = members[j];
			}
		}
		for (int j = 0; j < group[groupID].getSizeOfGroup(); j++) {
			if (player[members[j]].getHandsSum() ==player[winner].getHandsSum()) {
					winners.add(members[j]);
			}
		}
		return winners;
	}
}