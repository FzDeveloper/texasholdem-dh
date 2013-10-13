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

import static org.junit.Assert.*;

import org.junit.Test;

public class DeckTest {

	@Test
	public void testCreateDeck() {
		Deck shuffledDeck = Deck.createDeck(true);
		assertNotNull(shuffledDeck);
		assertTrue(shuffledDeck.isShuffled());
		assertEquals(shuffledDeck.cards.size, Deck.CARDS_IN_DECK);
		
		Deck rawDeck = Deck.createDeck(false);
		assertNotNull(rawDeck);
		assertFalse(rawDeck.isShuffled());
		assertEquals(rawDeck.cards.size, Deck.CARDS_IN_DECK);
	}

	@Test
	public void testIsShuffled() {
		Deck deck = Deck.createDeck(false);
		assertFalse(deck.isShuffled());
		deck.shuffle();
		assertTrue(deck.isShuffled());
	}

	@Test
	public void testReset() {
		Deck deck = Deck.createDeck(false);

		// memorize the first taken card
		Card cardFromDeck = deck.takeCard();
		assertNotNull(cardFromDeck);
		// take the rest cards
		for (int i = 0; i < Deck.CARDS_IN_DECK - 1; i++) {
			deck.takeCard();
		}
		assertNull(deck.takeCard());
		
		deck.reset();
		
		Card cardFromDeckAfterReset = deck.takeCard();
		assertNotNull(cardFromDeckAfterReset);
		// it should match the first card taken before reset 
		assertEquals(cardFromDeck, cardFromDeckAfterReset);
	}

	@Test
	public void testTakeCard() {
		Deck deck = Deck.createDeck(false);
		Card cardFromDeck = deck.takeCard();
		Card anotherCardFromDeck = deck.takeCard();
		
		assertNotNull(cardFromDeck);
		assertNotNull(anotherCardFromDeck);
				
		Card expectedCard1 = deck.cards.get(Deck.CARDS_IN_DECK - 1);
		Card expectedCard2 = deck.cards.get(Deck.CARDS_IN_DECK - 2);
		
		assertEquals(expectedCard1, cardFromDeck);
		assertEquals(expectedCard2, anotherCardFromDeck);
		assertNotEquals(cardFromDeck, anotherCardFromDeck);
		
		for (int i = 0; i < Deck.CARDS_IN_DECK - 2; i++) {
			Card takenCard = deck.takeCard();
			assertNotNull(takenCard);
		}
		// test if it returns null after the last card taken from the deck
		assertNull(deck.takeCard());
	}

	@Test
	public void testShuffle() {
		Deck unshuffledDeck = Deck.createDeck(false);
		Deck deck = Deck.createDeck(false);
		deck.shuffle();
		assertTrue(deck.isShuffled());
		assertNotEquals(unshuffledDeck.cards, deck.cards);
	}

}
