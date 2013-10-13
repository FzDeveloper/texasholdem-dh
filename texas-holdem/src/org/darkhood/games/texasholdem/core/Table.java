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
import com.badlogic.gdx.utils.ArrayMap;

public class Table {
	private int stake;
	private final Array<Pot> pots;
	private final Tournament game;
	
	private Pot getNextPotAfter(Pot pot) {
		int potIndex = pots.indexOf(pot, true);
		if (potIndex < 0) {
			// TODO: throw Exception
		}
		if (pots.size < potIndex + 2) {
			pots.add(new Pot());
		}
		return pots.get(potIndex + 1);
	}
	
	public final int getStake() {
		return this.stake;
	}
	
	public void setStake(int newStake) {
		this.stake = newStake;
	}
	
	public Table(Tournament game) {
		this.game = game;
		this.stake = game.settings.getStake();
		
		// Initializing the Main Pot and Side-Pots
		// Main Pot is the first pot in the array
		this.pots = new Array<Pot>(8);
		for (int i = 0; i < 8; i++) {
			this.pots.add(new Pot());
		}
	}
	
	public void clearPots() {
		for (Pot p : pots) {
			p.clear();
		}
	}
		
	public void contribute(Player contributor, int chips) {
		int remainingChips = 0;
		
		if (contributor.chips <= chips) {
			// All-In, baby
			remainingChips = contributor.chips;
		} else {
			remainingChips = chips;
		}
		
		Pot pot = null;
		
		while (remainingChips > 0) {	
			if (pot == null) {
				pot = pots.first();
			} else {
				pot = getNextPotAfter(pot);
			}
			
			ArrayMap<Player, Integer> potContributions = pot.getContributions();
							
			int contributorSum = 0;
			if (potContributions.containsKey(contributor)) {
				contributorSum = potContributions.get(contributor);
			}
			
			int potCap = pot.getCap();
			// If there is a cap in this pot, contribute the difference to the pot
			if (potCap > 0) {
				if (contributorSum < potCap) {
					int potSumLeft = potCap - contributorSum;
					remainingChips -= potSumLeft;
					contributor.chips -= potSumLeft;
					pot.addChips(contributor, potSumLeft);
				} else {
					// This pot is full, move to the next one
					continue;
				}
			} else {
				// This pot is unlimited, we are free to contribute the remaining chips					
				// However, it's essential that we check other players' contributions
				// It may happen that we need to create another pot if someone has 
				// already contributed more than current player has.
				int contributorFutureSum = contributorSum + remainingChips;
				
				Pot newSidePot = getNextPotAfter(pot);
				
				boolean potGetsCapped = false;
				// The sums in the current pot get equalized if there are values
				// higher than contributed chips. 
				// The difference will be transfered to the new pot
				for (Player player : game.players) {
					if (player != contributor && potContributions.containsKey(player)) {
						int otherPlayerContributedSum = potContributions.get(player);
						if (otherPlayerContributedSum > contributorFutureSum) {
							potGetsCapped = true;
							int difference = otherPlayerContributedSum - contributorFutureSum;
							pot.removeChips(player, difference);
							newSidePot.addChips(player, difference);
						}
					}
				}
				if (potGetsCapped) {
					pot.setCap(contributorFutureSum);
				}
				pot.addChips(contributor, remainingChips);
				contributor.chips -= remainingChips;
			}
		}
		
	}
}
