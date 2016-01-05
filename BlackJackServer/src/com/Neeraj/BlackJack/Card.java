package com.Neeraj.BlackJack;

/**
 * An implementation of a card type
 * 
 * @author neeraj
 * @since 13-Oct-2015
 */
public class Card {
	/**
	 * For selecting one of the four suits defined in enum
	 */
	private Suit mySuit;
	/**
	 * For selecting one of the number of the card in Deck as Ace: 1 Jack-King:
	 * 11-13
	 */
	private int myNumber;

	/**
	 * Card Constructor
	 * 
	 * @param uSuit
	 *            the suit of the card
	 * @param uNumber
	 *            the number of the card
	 */
	public Card(Suit uSuit, int uNumber) {

		this.mySuit = uSuit;
		if (uNumber >= 1 && uNumber <= 13) {
			this.myNumber = uNumber;
		} else {
			System.err.println(uNumber + " is not a valid Card numer");
			System.exit(1);
		}
	}

	/**
	 * Return the number of the card
	 * 
	 * @return number
	 */
	public int getNumber() {
		return this.myNumber;
	}

	public String toString() {

		String numberStr = null;

		switch (this.myNumber) {

		case 1:
			numberStr = "Ace";
			break;

		case 2:
			numberStr = "Two";
			break;

		case 3:
			numberStr = "Three";
			break;

		case 4:
			numberStr = "Four";
			break;

		case 5:
			numberStr = "Five";
			break;

		case 6:
			numberStr = "Six";
			break;

		case 7:
			numberStr = "Seven";
			break;

		case 8:
			numberStr = "Eight";
			break;

		case 9:
			numberStr = "Nine";
			break;

		case 10:
			numberStr = "Ten";
			break;

		case 11:
			numberStr = "Jack";
			break;

		case 12:
			numberStr = "Queen";
			break;

		case 13:
			numberStr = "King";
			break;

		}

		return numberStr + " of " + mySuit.toString();
	}
}
