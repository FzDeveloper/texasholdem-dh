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

public class Player {
	public boolean isDealer = false;
	public boolean isSmallBlind = false;
	public boolean isBigBlind = false;
	public final int playerId;
	public final String playerName;
	public int chips;
	public Array<Card> hand;
	public int callSum = 0;
	public int calledSum = 0;
	public boolean folded = false;
	public boolean checked = false;
	
	public Player(int playerId, String playerName) {
		this.playerId = playerId;
		this.playerName = playerName;
		this.chips = GameSettings.getInstance().getBuyIn();
		this.hand = new Array<Card>(2);
	}
	
	public void addCard(Card card) {
		hand.add(card);
	}
}
