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

package org.darkhood.games.texasholdem.network.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.darkhood.games.texasholdem.core.GameState;
import org.darkhood.games.texasholdem.core.TexasHoldemTournament;
import org.darkhood.games.texasholdem.network.PacketHeaders;

import com.badlogic.gdx.utils.Array;

public final class GameServer { 
	protected final TexasHoldemTournament game;
	private int port = 44491;
	private volatile boolean isRunning = false;
	private ServerSocket serverSocket;
	private final int maxPlayers = 5;
	private final Array<ClientHandler> connectedClients;
	//private final ExecutorService executor;
	
	protected void broadcastPacket(int packetHeader) throws IOException {
		broadcastPacket(packetHeader, null);
	}
	
	protected void broadcastPacket(int packetHeader, byte[] packetData) throws IOException {
		for (ClientHandler client : connectedClients) {
			client.sendPacket(packetHeader, packetData);
		}
	}
	
	public int getPlayerCount() {
		return connectedClients.size;
	}
	
	public int getMaxPlayerCount() {
		return maxPlayers;
	}
	
	public GameServer() {
		//this.executor = Executors.newCachedThreadPool();	
		this.connectedClients = new Array<ClientHandler>(maxPlayers);
		this.game = new TexasHoldemTournament();
	}
	
	public void start() {
		try {
			game.setState(GameState.LOBBY);
			serverSocket = new ServerSocket(port);
			isRunning = true;
			
			System.out.println("Awaiting clients");
			
			while(isRunning) {
				Socket clientSocket = serverSocket.accept();
				System.out.println("accepted client");
				handleClient(clientSocket);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//executor.shutdown();
		}
	}
	
	private void handleClient(Socket clientSocket) throws IOException {
		ClientHandler clientHandler = new ClientHandler(this, clientSocket);
		if (connectedClients.size >= maxPlayers) {
			clientHandler.sendPacket(PacketHeaders.S_CONNECTION_REFUSED);
		} else {
			connectedClients.add(clientHandler);
			//executor.execute(clientHandler);
			Thread t = new Thread(clientHandler);
			t.start();
		}
	}
	
	public static void main(String[] args) {
		GameServer server = new GameServer();
		server.start();
	}

}
