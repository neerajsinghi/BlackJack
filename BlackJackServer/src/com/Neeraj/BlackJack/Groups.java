package com.Neeraj.BlackJack;

import java.util.ArrayList;

/**
 * An implementation for the Group Implementation
 * 
 * @author neeraj
 * @since 15-Oct-2015
 *
 */
public class Groups {
	/*
	 * group name
	 */
	private String gName;
	/*
	 * group size and remaining member
	 */
	private int size, i = 0;
	/*
	 * game restart
	 */
	private int gameRe = 0
	/*
	 * User list in group
	 */;
	private ArrayList<Integer> userID = new ArrayList<Integer>();
	/*
	 * Busted players in a group
	 */
	private boolean[] busted = { false, false, false, false, false };
	/*
	 * Players restarted
	 */
	private boolean[] gameReStart = { false, false, false, false, false };
	/*
	 * Game start if false game is going else game will not start
	 */
	private boolean gameStarted = false;
	/*
	 * Game is still going
	 */
	private boolean gameOver = true;

	/*
	 * Constructor for group
	 */
	public Groups(String groupName, int size, Integer user) {
		this.gName = groupName;
		this.size = size;
		this.userID.add(user);
		this.i++;
		gameOver = false;
	}

	/*
	 *Adding user to the group 
	 */
	public int addUsers(Integer newUserID) {
		if (this.i <= 5) {
			this.userID.add(newUserID);
			this.i++;
			if (this.size - this.i == 0) {
				this.gameStarted = true;
			}
			return (this.size - this.i);
		} else
			return this.size - this.i;
	}

	/*
	 * Total no of users that still be added
	 */
	public int getTotalMembersOfGroups() {
		return this.i;
	}

	/*
	 * Get the size of the group
	 */
	public int getSizeOfGroup() {
		return this.size;
	}
	/* 
	 * send the name of the group
	 */
	public String sendGroupName() {
		if (!gameStarted)
			return "GROUPNAME/" + this.gName + "/TOTALSIZE/" + size + "/AVAILAIBILITY/" + (size - i);
		else
			return "GROUPNAME/" + this.gName + "/TOTALSIZE/" + size + "/AVAILAIBILITY/" + 0;
	}

	/*
	 * Get members of the group
	 */
	public int[] getMembersOfGroups() {
		int[] members = new int[size];
		int i = 0;
		for (int user : userID) {
			members[i] = user;
			i++;
		}
		return members;
	}

	/*
	 * Add user to busted list
	 */
	public boolean addBusted(Integer userID2) {
		int k = 0;
		int i = 0;
		for (int user : userID) {
			if (user == userID2) {
				busted[i] = true;
				break;
			} else {
				i++;
			}
		}
		for (i = 0; i < this.size; i++) {
			if (busted[i] == true) {
				k++;
			}
		}
		if (this.size - k > 1) {
			return false;
		} else
			return true;
	}

	/*
	 * Remove user from a group
	 */
	public boolean removeUserFromGroup(Integer userID2) {
		int i = 0;
		int index = 0;
		for (int user : userID) {
			if (user == userID2) {
				index = i;
				this.i--;
				break;
			} else {
				i++;
			}
		}
		userID.remove(index);
		return true;
	}

	/*
	 * Restart the game
	 */
	public void restart() {

		gameStarted = false;
		gameOver = false;
	}

	/*
	 * Send game status
	 */
	public boolean gameCheck() {
		return gameOver;
	}

	/*
	 * Restart the game
	 */
	public boolean restartGame() {

		restart();
		gameReStart[gameRe] = true;
		gameRe++;
		int j = 0;
		for (int i = 0; i < size; i++) {
			if (gameReStart[i]) {
				j++;
			}
		}
		if (j == size) {
			gameOver = false;
			return true;

		}
		return false;
	}

	/*
	 * Reset the initial variables
	 */
	public void resetRestartVariables() {
		for (int i = 0; i < size; i++) {
			busted[i] = false;
			gameReStart[i] = false;
		}
		gameRe = 0;
		gameOver = true;
	}

}