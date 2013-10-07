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

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

// TODO: implement blind raising (use the blindRaiseSum and handCount variables)

public final class TexasHoldemTournament {
	protected GameSettings settings;
	protected final Array<Player> players;
	protected final ArrayMap<Integer, Integer> playerMap;
	private int blindRaiseSum;
	private int gameState;
	private int currentTurnPlayerId;
	private int lastDealerId;
	private int handCount = 0;
	private final Deck deck;
	private int currentBet;
	private Array<Card> sharedCards;
	private Board board;
	
	
	public TexasHoldemTournament() {
		this.players = new Array<Player>();
		this.playerMap = new ArrayMap<Integer, Integer>();
		this.gameState = GameState.LOBBY;
		this.settings = GameSettings.getInstance();
		this.deck = Deck.createDeck(true);
		this.sharedCards = new Array<Card>(5);
		this.blindRaiseSum = settings.getBlindRaiseSum();
		this.board = new Board(this);
	}
	
	public int getGameState() {
		return gameState;
	}
	
	public void setState(int state) {
		this.gameState = state;
		switch (gameState) {
		case GameState.PREFLOP:
			newHand();
			break;
		case GameState.FLOP:
			sharedCards.add(deck.takeCard());
			sharedCards.add(deck.takeCard());
			sharedCards.add(deck.takeCard());
			break;
		case GameState.TURN:
		case GameState.RIVER:
			sharedCards.add(deck.takeCard());
			break;
		}
	}
	
	private Player getPlayer(int playerId) {
		if (!playerMap.containsKey(playerId)) return null;
		int playerIndex = playerMap.get(playerId);
		return players.get(playerIndex);
	}
	
	private void newHand() {		
		if (players.size < 2)
			return;
		
		// Reset deck cursor and shuffle
		board.clearPots();
		//pot = 0;
		currentBet = board.getStake();
		sharedCards.clear();
		deck.reset();
		deck.shuffle();
		// Reset player hands and blinds
		for (int i = 0; i < players.size; i++) {
			Player player = players.get(i);
			player.isSmallBlind = false;
			player.isBigBlind = false;
			player.isDealer = false;
			player.callSum = 0;
			player.calledSum = 0;
			player.folded = false;
			player.checked = false;
			player.hand.clear();
			
			// Starting hand
			player.addCard(deck.takeCard());
			player.addCard(deck.takeCard());
		}
		
		// Arrange new blinds
		if (lastDealerId == 0) {
			Player firstPlayer = players.first();
			lastDealerId = firstPlayer.playerId;
			firstPlayer.isDealer = true;
			if (players.size > 2) {
				players.get(1).isSmallBlind = true;
				players.get(2).isBigBlind = true;
				
				int underTheGunIndex = 3 >= players.size ? 0 : 3;
				currentTurnPlayerId = players.get(underTheGunIndex).playerId;
			} else {
				// only two players (heads-up), 
				// small blind is also a dealer 
				players.get(0).isSmallBlind = true;
				players.get(0).isDealer = true;
				currentTurnPlayerId = players.get(0).playerId;
				
				players.get(1).isBigBlind = true;
			}
		} else {
			Integer newDealerIndex = playerMap.get(lastDealerId) + 1;
			if (newDealerIndex >= players.size) {
				newDealerIndex = 0;
			}
			players.get(newDealerIndex).isDealer = true;
			lastDealerId = players.get(newDealerIndex).playerId; 
			int sbIndex = newDealerIndex + 1 >= players.size ? 0 : newDealerIndex + 1;
			int bbIndex = newDealerIndex + 2 >= players.size ? 0 : newDealerIndex + 2;
			players.get(sbIndex).isSmallBlind = true;
			players.get(bbIndex).isBigBlind = true;
			
			int underTheGunIndex = bbIndex + 1 >= players.size ? 0 : bbIndex + 1;
			currentTurnPlayerId = players.get(underTheGunIndex).playerId;
		}
		
		// Blinds bet
		for (Player player : players) {
			if (player.isBigBlind) {
				player.callSum = 0;
				board.contribute(player, board.getStake());
			} else if (player.isSmallBlind) {
				int betSum = board.getStake() / 2;
				player.callSum = betSum;
				board.contribute(player, betSum);
			} else {
				player.callSum = board.getStake();
			}
		}
		
		handCount++;
	}
	
	public Player getNextTurnPlayer() {
		int currentPlayerIndex = playerMap.get(currentTurnPlayerId);
		Player nextTurnPlayer = null;
		do {
			int nextTurnPlayerIndex = currentPlayerIndex + 1 >= players.size ? 0 : currentPlayerIndex + 1;
			nextTurnPlayer = players.get(nextTurnPlayerIndex);
			// If a player is out of chips, he checks anyway
			if (!nextTurnPlayer.folded && nextTurnPlayer.chips > 0) {
				return nextTurnPlayer;
			}
			currentPlayerIndex = nextTurnPlayerIndex;
		} while (nextTurnPlayer.playerId != currentTurnPlayerId);
		return null;
	}
	
	private void nextPlayerTurn() {
		int calledPlayerCount = 0;
		int foldedPlayerCount = 0;
		Player lastNotFoldedPlayer = null;
		for (Player p : players) {
			if (!p.folded && p.callSum <= 0 && p.checked)
				calledPlayerCount++;
			if (p.folded) {
				foldedPlayerCount++;
			} else {
				lastNotFoldedPlayer = p;
			}
		}
		
		// Check if anyone is eligible to take the pot
		if (foldedPlayerCount == players.size - 1) {
			// TODO: lastNotFoldedPlayer take the pot
			// Change gameState to PREFLOP
		}
		
		Player nextTurnPlayer = getNextTurnPlayer();
		if (calledPlayerCount == players.size || nextTurnPlayer == null) {
			// change the hand state
			switch (gameState) {
			case GameState.PREFLOP:
				setState(GameState.FLOP);
				break;
			case GameState.FLOP:
				setState(GameState.TURN);
				resetHandRound();
				break;
			case GameState.TURN:
				setState(GameState.RIVER);
				resetHandRound();
				break;
			case GameState.RIVER:
				resetHandRound();
				showdown();
				break;
			}
			
		} else {
			currentTurnPlayerId = nextTurnPlayer.playerId;
		}
	}
	
	private void resetHandRound() {
		currentBet = board.getStake();
		// reset player round specific flags
		for (Player p : players) {
			if (p.folded) 
				continue;
			p.callSum = 0;
			p.calledSum = 0;
			if (p.chips > 0) {
				// if a player went All-In, he checks anyway
				p.checked = false;
			}
		}
	}
	
	private void showdown() {
		// TODO: implement the showdown
		
		// evaluate players' hands
		// choose winners
		
		// change the game state. next hand.
		setState(GameState.PREFLOP);
	}
	
	public boolean addPlayer(Player player) {
		this.players.add(player);
		int index = players.size - 1;
		this.playerMap.put(player.playerId, index);
		return true;
	}
	
	public void removePlayer(Player player) {
		// TODO: implement safe removal from the tournament
		//this.players.remove(player);
	}
	
	public Array<Player> getPlayers() {
		return players;
	}
	
	public void fold(int playerId) {
		if (playerId != currentTurnPlayerId) 
			return;
		
		Player playerFold = null; 
		int foldedPlayerCount = 0;
		for (Player player : players) {
			if (player.playerId == playerId) {
				playerFold = player;
			}
			if (player.folded) {
				foldedPlayerCount++;
			}
		}
		if (players.size - foldedPlayerCount == 1) {
			// can't fold if there is only one player left
			return;
		}
		if (playerFold != null) {
			playerFold.folded = true;
			nextPlayerTurn();
		}
	}
	
	public void bet(int playerId, int betSum) {
		if (playerId != currentTurnPlayerId) 
			return;
		// Can't bet if there is already a bet. 
		// Player should raise instead.
		if (currentBet > board.getStake()) {
			return;
		}
		// The minimum bet sum is current bet sum.
		if (betSum < currentBet)
			return;
		Player player = getPlayer(playerId);
		if (player != null && player.chips >= betSum) {
			currentBet = betSum;
			board.contribute(player, betSum);
			player.calledSum = betSum;
			player.callSum = player.callSum - betSum < 0 ? 0 : player.callSum - betSum;
			
			for (Player p : players) {
				if (p == player || p.folded)
					continue;
				p.callSum = currentBet;
			}
			player.checked = true;
			
			//if (player.chips == 0) {
				//// Forced to going All-In
			//}
			
			nextPlayerTurn();
		}
	}
	
	public void raise(int playerId, int raiseSum) {
		if (playerId != currentTurnPlayerId) 
			return;
		// The minimum raise sum is current bet sum.
		if (raiseSum < currentBet) 
			return;
		Player player = getPlayer(playerId);
		if (player != null && player.chips >= raiseSum) {		
			for (Player p : players) {
				if (p == player || p.folded)
					continue;
				int difference = raiseSum - p.calledSum;
				p.callSum += difference;
			}
			player.calledSum = raiseSum;
			player.callSum = player.callSum - raiseSum < 0 ? 0 : player.callSum - raiseSum;			
			currentBet = raiseSum;
			board.contribute(player, raiseSum);
			player.checked = true;
			nextPlayerTurn();
		}
	}
	
	public void call(int playerId) {
		if (playerId != currentTurnPlayerId) 
			return;
		Player player = getPlayer(playerId);
		if (player != null && player.callSum > 0) {
			if (player.chips < player.callSum) {
				player.callSum -= player.chips;
				player.calledSum += player.chips;
				// Forced to going All-In
				board.contribute(player, player.chips);
			} else {
				player.calledSum += player.callSum;
				player.callSum = 0;
				board.contribute(player, player.callSum);
			}
			player.checked = true;
			nextPlayerTurn();
		}
	}
	
	public void check(int playerId) {
		if (playerId != currentTurnPlayerId) 
			return;
		Player player = getPlayer(playerId);
		if (player != null) {
			// if there wasn't any bet or raise,
			// the player is eligible to check
			if (player.callSum == 0) {
				player.checked = true;
				nextPlayerTurn();
			}
		}
	}
}
