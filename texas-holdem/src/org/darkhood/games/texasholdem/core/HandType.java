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

/**
 * 
 */
public enum HandType {
	HIGH_CARD (0),
	ONE_PAIR (1),
	TWO_PAIR (2),
	THREE_OF_A_KIND (3),
	STRAIGHT (4),
	FLUSH (5),
	FULL_HOUSE (6),
	FOUR_OF_A_KIND (7),
	STRAIGHT_FLUSH (8);
	/*ROYAL_FLUSH (9);*/
	
	private final int type;
	
	public final int getIntValue() {
		return type;
	}
	
	HandType(int type) {
		this.type = type;
	}
}
