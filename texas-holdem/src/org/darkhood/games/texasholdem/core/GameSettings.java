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

public class GameSettings {
	// Default settings	
	private int buyIn = 20000;
	private int stake = 800;
	private int blindRaiseSum = 400;
	
	public int getBlindRaiseSum() {
		return blindRaiseSum;
	}
	
	public void setBlindRaiseSum(int blindRaiseSum) {
		this.blindRaiseSum = blindRaiseSum;
	}
	
	public int getBuyIn() {
		return buyIn;
	}

	public void setBuyIn(int buyIn) {
		this.buyIn = buyIn;
	}

	public int getStake() {
		return stake;
	}

	public void setStake(int stake) {
		this.stake = stake;
	}
	
	private static GameSettings instance;
	
	public static GameSettings getInstance() {
		if (instance == null) {
			instance = new GameSettings();
		}
		return instance;
	}
}
