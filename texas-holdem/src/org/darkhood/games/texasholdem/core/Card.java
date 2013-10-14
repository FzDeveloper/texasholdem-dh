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

public class Card implements Comparable<Card> {
	private final int suit;
	private final int value;
	
	public final int getSuit() {
		return suit;
	}
	
	public final int getValue() {
		return suit;
	}
	
	public Card(int value, int suit) {
		this.suit = suit;
		this.value = value;
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
        hash = hash * 17 + suit;
        hash = hash * 31 + value;
        return hash;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
            return true;
		}
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		}
		Card otherCard = (Card) obj;
		if (suit != otherCard.suit) {
			return false;
		}
		if (value != otherCard.value) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		// TODO: implement this
		return String.format("%s %s", value, suit);
	}

	@Override
	public int compareTo(Card otherCard) {
		if (this.value != otherCard.value) {
			return this.value - otherCard.value;
		}
		if (this.suit != otherCard.suit) {
			return this.suit - otherCard.suit;
		}
		return 0;
	}
}
