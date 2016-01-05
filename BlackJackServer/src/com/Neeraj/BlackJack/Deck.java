package com.Neeraj.BlackJack;

import java.util.Random;

/**
 * An implementation of a Deck of cards
 * 
 * @author neeraj
 * @since 13-Oct-2015
 */
public class Deck {

	/**
	 * The array of cards, where the top card is in the first index.
	 */
	private Card[] myCards;
	/**
	 * The Number of cards currently in the deck
	 */
	private int numCards;

	/**
	 * Constructor with a default of one deck (i.e. 52 cards) with shuffling
	 */
	public Deck() {

		// call the other constructor defining one deck with shuffling
		this(1, true);
	}

	/**
	 * Constructor that initialize the number of decks(i.e how many sets of 52
	 * card are in the deck) and whether we want to shuffle the deck
	 * 
	 * @param numDecks
	 *            the number of individual decks in this deck
	 * @param Shuffle
	 *            whether to shuffle the card or not
	 */
	public Deck(int numDecks, Boolean Shuffle) {

		this.numCards = numDecks * 52;
		this.myCards = new Card[this.numCards];

		// initialize card index
		int c = 0;
		// for each deck
		for (int d = 0; d < numDecks; d++) {

			// for each suits
			for (int s = 0; s < 4; s++) {

				// for each card number
				for (int n = 1; n <= 13; n++) {

					// add a new card to the deck
					this.myCards[c] = new Card(Suit.values()[s], n);
					c++;
				}
			}
		}

		// shuffle if necessary
		if (Shuffle) {
			this.shuffler();
		}
	}

	/**
	 * Shuffle deck by randomly snapping pairs of cards.
	 */
	private void shuffler() {

		// initialize random number generator
		Random randNo = new Random();

		// temporary card
		Card temp;

		int j;
		for (int i = 0; i < this.numCards; i++) {

			// get a random card j to swap i's value with
			j = randNo.nextInt(this.numCards);

			// do the swap
			temp = this.myCards[i];
			this.myCards[i] = this.myCards[j];
			this.myCards[j] = temp;
		}
	}

	/**
	 * Deal the next card from the top of the deck
	 * 
	 * @return the dealt card
	 */
	public Card dealNextCard() {

		// get the top card
		Card top = this.myCards[0];

		// Shift all subsequent cards to left with one index
		for (int c = 1; c < this.numCards; c++) {
			this.myCards[c - 1] = this.myCards[c];
		}
		this.myCards[this.numCards - 1] = null;

		// Decrement the number of card in the deck
		this.numCards--;

		return top;
	}

	/**
	 * Print the top card in the deck.
	 * 
	 * @param numToPrint
	 *            the number of cards from the top to print
	 */
	public void printDeck(int numToPrint) {
		for (int c = 0; c < numToPrint; c++) {
			System.out.printf("% 3d/%d %s\n", c + 1, this.numCards, this.myCards[c].toString());
		}
		System.out.printf("\t[%d others]\n",this.numCards-numToPrint);
	}
}
