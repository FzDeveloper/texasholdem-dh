//	Copyright (C) 2013 Alexander Dvuzhilov
//
//	This file is part of Texas Holdem.
//	
//	Texas Holdem is free software: you can redistribute it and/or modify
//	it under the terms of the GNU General Public License as published by
//	the Free Software Foundation, either version 3 of the License, or
//	(at your option) any later version.
//	
//	Texas Holdem is distributed in the hope that it will be useful,
//	but WITHOUT ANY WARRANTY; without even the implied warranty of
//	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//	GNU General Public License for more details.
//	
//	You should have received a copy of the GNU General Public License
//	along with Texas Holdem.  If not, see <http://www.gnu.org/licenses/>.

package org.darkhood.games.texasholdem.core;

import com.badlogic.gdx.utils.ArrayMap;

public final class Pot {
	private int totalChips;
	private ArrayMap<Player, Integer> contributions;
	private int cap = 0;
	
	public Pot() {
		this.contributions = new ArrayMap<Player, Integer>(); 
	}
	
	public int getChips() {
		return totalChips;
	}
	
	/**
	 * @return number of chips that can be contributed into this pot
	 * or zero if it is unlimited.
	 */
	public int getCap() {
		return cap;
	}
	
	public void setCap(int newCapValue) {
		this.cap = newCapValue;
	}
	
	public ArrayMap<Player, Integer> getContributions() {
		return contributions;
	}
	
	public void addChips(Player contributedPlayer, int chips) {
		int currentSum = 0;
		if (contributions.containsKey(contributedPlayer)) {
			currentSum = contributions.get(contributedPlayer);
		}
		currentSum += chips;
		totalChips += chips;
		contributions.put(contributedPlayer, currentSum);
	}
	
	public void removeChips(Player player, int chips) {
		if (!contributions.containsKey(player)) {
			return;
		}
		int currentSum = contributions.get(player);
		if (chips <= currentSum) {
			currentSum -= chips;
			totalChips -= chips;
			contributions.put(player, currentSum);
		} else {
			// TODO: throw Exception, maybe?
			totalChips -= currentSum;
			contributions.put(player, 0);
		}
				
	}
	
	public void clear() {
		contributions.clear();
		totalChips = 0;
		cap = 0;
	}
}
