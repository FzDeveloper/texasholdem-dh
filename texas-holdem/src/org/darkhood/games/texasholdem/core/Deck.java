/**
 * Copyright (C) 2013 Alexander Dvuzhilov
 * 
 * This file is part of Texas Holdem.
 * 
 * Texas Holdem is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Texas Holdem is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Texas Holdem.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.darkhood.games.texasholdem.core;

import com.badlogic.gdx.utils.Array;

public class Deck {
	public static final int CARDS_IN_DECK = 52;
	private static final Deck unshuffledDeck = createDeck(false);
	
	protected Array<Card> cards;
	private int index;
	private boolean isShuffled;
	
	private Deck() {
		this.cards = new Array<Card>(CARDS_IN_DECK);
		this.isShuffled = false;
	}
	
	public static Deck createDeck(boolean shuffle) {
		Deck deck = new Deck();
		for (int i = 2; i <= 14; i++) {
			for (int j = 0; j < 4; j++) {
				Card card = new Card(i, j);
				deck.cards.add(card);
			}
		}
		
		if (shuffle) {
			deck.cards.shuffle();
			deck.isShuffled = true;
		}
		deck.reset();
		return deck;
	}
	
	public boolean isShuffled() {
		return isShuffled;
	}
	
	public void reset() {
		this.index = CARDS_IN_DECK - 1;
	}
	
	public Card takeCard() {
		if (index < 0)
			return null;
		return cards.get(index--);
	}
	
	public void shuffle() {
		// shuffle until it is not equal to the unshuffled deck
		do {
			cards.shuffle();
		} while (unshuffledDeck.cards.equals(cards));
		isShuffled = true;
	}
	
	public static void main(String[] args) {
		Deck deck = Deck.createDeck(true);
		for (Card c : deck.cards) {
			System.out.println(c.toString());
		}
	}

}
