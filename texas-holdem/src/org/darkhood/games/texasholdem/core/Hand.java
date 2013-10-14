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

import java.util.Arrays;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

public class Hand {
	private static final int MAX_CARDS_IN_HAND = 5;
	private final Array<Card> cards;
	private HandType handType = null;
	private ArrayMap<Integer, Integer> handHistogram = new ArrayMap<Integer, Integer>(5);
	
	public Hand() {
		this.cards = new Array<Card>(MAX_CARDS_IN_HAND);
	}
	
	public Hand(Card[] cards) {
		// TODO: check size of the argument array
		this.cards = new Array<Card>(cards);
		createHistogramForCards(this.cards);
	}
	
	public Hand(Array<Card> cards) {
		// TODO: check size of the argument array
		this.cards = cards;
		createHistogramForCards(this.cards);
	}
	
	public void addCard(Card card) throws Exception {
		if (cards.size >= MAX_CARDS_IN_HAND) {
			// TODO: add new exception type
			throw new Exception();
		}
		handType = null;
		addToHistogram(card);
		cards.add(card);
	}

	/**
	 * Get the cards of the hand 
	 * @return A copy of the original array of the hand cards. 
	 */
	public Array<Card> getCards() {
		return new Array<Card>(cards);
	}
	
	public void clear() {
		this.cards.clear();
		this.handHistogram.clear();
		this.handType = null;
	}
	
	public HandType getHandType() {
		if (cards.size < MAX_CARDS_IN_HAND) {
			return null;
		}
		if (handType == null) {
			
			Integer[] values = handHistogram.values.clone();
			Arrays.sort(values);
			int maxRankCount = values[values.length - 1];
			
			// Check if the hand is quads or boat
			if (handHistogram.size < 3) {
				if (maxRankCount == 4) {
					return HandType.FOUR_OF_A_KIND;
				} else {
					return HandType.FULL_HOUSE;
				}
			} else if (handHistogram.size == 3) {
				if (maxRankCount == 3) {
					return HandType.THREE_OF_A_KIND;
				} else {
					return HandType.TWO_PAIR;
				}
			} else if (handHistogram.size == 4) {
				return HandType.ONE_PAIR;
			}
			
			boolean isFlush = false;
			boolean isStraight = false;
			// Check if the hand is flush
			int firstCardSuit = cards.first().getSuit();
			int sameSuitCount = 0;
			for (Card card : cards) {
				if (card.getSuit() == firstCardSuit) {
					sameSuitCount++;
				}
			}
			isFlush = (sameSuitCount == cards.size);			
						
			Card[] cardsArray = cards.items.clone();
			Arrays.sort(cardsArray);
			// Check if the hand is straight
			int topCardRank = cardsArray[MAX_CARDS_IN_HAND - 1].getValue();
			int nextToTheTopCardRank = cardsArray[MAX_CARDS_IN_HAND - 2].getValue();
			int bottomCardRank = cardsArray[0].getValue();
			if ((nextToTheTopCardRank == CardValues.FIVE 
					&& topCardRank == CardValues.ACE
					&& nextToTheTopCardRank - bottomCardRank == 3)
				|| (topCardRank - bottomCardRank == 4)) {
				isStraight = true;
			}
			if (isStraight && isFlush) {
				return HandType.STRAIGHT_FLUSH;
			} else if (isStraight) {
				return HandType.STRAIGHT;
			} else if (isFlush) {
				return HandType.FLUSH;
			} 
			handType = HandType.HIGH_CARD;
		}
		return handType;
	}
	
	private void createHistogramForCards(Iterable<Card> cards) {
		handHistogram.clear();
		for (Card card : cards) {
			addToHistogram(card);
		}
	}
	
	private void addToHistogram(Card card) {
		if (handHistogram.containsKey(card.getValue())) {
			handHistogram.put(card.getValue(), handHistogram.get(card.getValue()) + 1);
		} else {
			handHistogram.put(card.getValue(), 1);
		}
	}
}