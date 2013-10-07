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

package org.darkhood.games.texasholdem.network;

/**
 * Headers that start with CL_ prefix are sent by clients to server
 */
public class PacketHeaders {
	public static final int OK = 0x2;
	public static final int CL_CONNECT = 0x12;
	public static final int CL_DISCONNECT = 0x13;
	public static final int S_CONNECTION_REFUSED = 0x14;
	public static final int S_PLAYER_CONNECTED = 0x15;
	public static final int S_PLAYER_CONNECTED_NAME = 0x16;
	public static final int S_PLAYER_DISCONNECTED = 0x17;
	public static final int CL_READY = 0x20;
	public static final int CL_HOST_START = 0x21;
	// State changes
	public static final int S_STATE_LOBBY = 0x24;
	public static final int S_STATE_GAME_STARTED = 0x25;
	public static final int S_STATE_GAME_OVER = 0x30;
	public static final int S_STATE_PREFLOP = 0x26;
	public static final int S_STATE_FLOP = 0x27;
	public static final int S_STATE_TURN = 0x28;
	public static final int S_STATE_RIVER = 0x29;
	// Player actions
	public static final int S_ACTION_REQUEST = 0x40;
	public static final int CL_FOLD = 0x41;
	public static final int CL_BET = 0x42;
	public static final int CL_RAISE = 0x43;
	public static final int CL_CHECK = 0x44;
	public static final int CL_CALL = 0x45;
	public static final int CL_ALL_IN = 0x46;
	public static final int CL_ACTION_RESPONSE = 0x50;
	public static final int S_PLAYER_FOLDED = 0x51;
	public static final int S_PLAYER_BET = 0x52;
	public static final int S_PLAYER_RAISED = 0x53;
	public static final int S_PLAYER_CHECKS = 0x54;
	public static final int S_PLAYER_CALLED = 0x55;
	public static final int S_PLAYER_ALL_IN = 0x56;
}
