package com.Neeraj.BlackJack;

/**
 * An implementation for the players
 * 
 * @author neeraj
 * @since 13-Oct-2015
 *
 */
public class Player {
	/*
	 * The names of the players
	 */
	private String playerName;

	/*
	 * Cards in the hand of the user
	 */
	private Card[] hand = new Card[10];

	/*
	 * The numbers of card in a hand
	 */
	private int numCards;
	/*
	 * Total no. of card that can be in the players hand
	 */
	private int maxNumCards = 10;

	private boolean playerStandStatus = false;

	/*
	 * Player Constructor.
	 * 
	 * @param pName name of the player
	 */
	public Player(String pName) {
		this.playerName = pName;

		// start the player with empty hands
		this.emptyHand();

	}

	/**
	 * Reset all card from players hands
	 */
	public void emptyHand() {

		for (int c = 0; c < this.maxNumCards; c++) {
			this.hand[c] = null;
		}
		this.playerStandStatus = false;
		this.numCards = 0;
	}

	/**
	 * Add a card to players hand
	 * 
	 * @param pCard
	 *            the card to add
	 * @return whether the sum of new hand is below or above 21
	 */
	public boolean addACard(Card pCard) {

		// print error when user hand is full(number of cards is ten)
		if (this.numCards == this.maxNumCards) {

			System.err.printf("%s's Hand already has 10 cards :" + " Cannot add another card \n", this.playerName);
			System.exit(1);
		}

		// add new card in player's hand
		this.hand[this.numCards] = pCard;
		this.numCards++;

		return (this.getHandsSum() <= 21);

	}

	/**
	 * Check if user can split or not
	 * 
	 * @return
	 */
	public boolean checkForSplit() {
		if (this.hand[0].getNumber() == this.hand[1].getNumber()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method calculate the total value in user's hand
	 * 
	 * @return the total value in users hand
	 */
	public int getHandsSum() {

		int totalHandValue = 0;
		int cardNumber;
		int numAces = 0;
		// look for each card and count its contribution to the hand value
		for (int c = 0; c < numCards; c++) {

			// get the no of each card
			cardNumber = this.hand[c].getNumber();

			if (cardNumber == 1) {// ace
				numAces++;
				totalHandValue += 11;
			} else if (cardNumber > 10) {// face card
				totalHandValue += 10;
			} else {
				totalHandValue += cardNumber;
			}

			// if we have aces and our sum is greater than 21 subtract 10
			while (totalHandValue > 21 && numAces > 0) {
				totalHandValue -= 10;
				numAces--;
			}
		}

		return totalHandValue;
	}

	/**
	 * Print the card in players hand
	 * 
	 * @param showFirstCard
	 *            whether the first card is hidden or not
	 */
	public String[] printHand(boolean showFirstCard) {
		String[] printH = new String[10];
		int i = 0;
		printH[i] = this.playerName + "'s cards.\n";
		i++;
		System.out.printf("%s's cards:", this.playerName);
		for (int c = 0; c < this.numCards; c++) {
			if (c == 0 && !showFirstCard) {
				System.out.println("[HIDDEN]");
				printH[i] = "[HIDDEN]";
				i++;
			} else {
				System.out.printf("  %s\n", this.hand[c].toString());
				printH[i] = this.hand[c].toString();
				i++;
			}
		}
		return printH;
	}

	/**
	 * Get the first card of the current user
	 * 
	 * @return the card
	 */
	public Card firstCard() {

		return this.hand[0];

	}

	/**
	 * Get the second card of the current user
	 * 
	 * @return the card
	 */
	public Card secondCard() {
		return this.hand[1];

	}

	public void changePlayerStandStatus(boolean status) {
		this.playerStandStatus = status;
	}

	public boolean getPlayerStandStatus() {
		return this.playerStandStatus;
	}
}
