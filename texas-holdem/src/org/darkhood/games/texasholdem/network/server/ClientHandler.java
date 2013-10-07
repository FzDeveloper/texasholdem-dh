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

package org.darkhood.games.texasholdem.network.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.ByteBuffer;

import org.darkhood.games.texasholdem.core.GameState;
import org.darkhood.games.texasholdem.network.PacketHeaders;

public final class ClientHandler implements Runnable {	
	private static int newClientId = 0;
	
	private int clientId;
	private String playerName;
	private byte[] playerNameBytes;
	private boolean isHost;
	private final GameServer server;
	private final Socket clientSocket;
	private final InputStream inputStream;
	private final OutputStream outputStream;
	private final DataInputStream dataInputStream;
	private final DataOutputStream dataOutputStream;
	private ByteBuffer intBuffer = ByteBuffer.allocate(4);
	private byte[] clientIdBytes = new byte[4];
	
	public ClientHandler(GameServer server, Socket clientSocket) throws IOException {
		this.clientId = ++newClientId;
		this.intBuffer.clear();
		this.clientIdBytes = this.intBuffer.putInt(clientId).array();
		
		this.server = server;
		this.clientSocket = clientSocket;
		this.inputStream = clientSocket.getInputStream();
		this.outputStream = clientSocket.getOutputStream();
		this.dataInputStream = new DataInputStream(inputStream);
		this.dataOutputStream = new DataOutputStream(outputStream);
		this.isHost = false;
	}
	
	public ClientHandler(GameServer server, Socket clientSocket, boolean isHost) throws IOException {
		this(server, clientSocket);
		this.isHost = isHost;
	}

	private int getPacketSize() throws IOException {
		return dataInputStream.readInt();
	}
	
	private byte[] getPacketData() throws IOException {
		int packetSize = getPacketSize();
		byte[] buffer = new byte[packetSize];
		inputStream.read(buffer, 0, packetSize);
		return buffer;
	}
	
	private String getStringFromPacket(final byte[] data) {
		try {
			String str = new String(data, "UTF-8");
			return str;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Unnamed player";
		}
	}
	
	/**
	 * Handles incoming packet from client 
	 * @param packetHeader
	 * @return true if socket has to be closed
	 * @throws IOException
	 */
	private boolean handlePacket(int packetHeader) throws IOException {
		switch (packetHeader) {
		case PacketHeaders.CL_CONNECT:
			if (server.game.getGameState() != GameState.LOBBY) {
				sendPacket(PacketHeaders.S_CONNECTION_REFUSED);
				return true;
			}
			this.playerNameBytes = getPacketData();
			this.playerName = getStringFromPacket(playerNameBytes);
			
			sendPacket(PacketHeaders.OK);
			server.broadcastPacket(PacketHeaders.S_PLAYER_CONNECTED, clientIdBytes);
			server.broadcastPacket(PacketHeaders.S_PLAYER_CONNECTED_NAME, playerNameBytes);
			break;
		case PacketHeaders.CL_DISCONNECT:
			server.broadcastPacket(PacketHeaders.S_PLAYER_DISCONNECTED, clientIdBytes);
			return true;
		case PacketHeaders.CL_HOST_START:
			if (isHost && server.getPlayerCount() > 1
					&& server.game.getGameState() == GameState.LOBBY) {
				server.game.setState(GameState.GAME_STARTED);
				server.broadcastPacket(PacketHeaders.S_STATE_GAME_STARTED);
			}
			break;
		}
		return false;
	}
	
	public synchronized void sendPacket(int packetHeader) throws IOException {
		sendPacket(packetHeader, null);
	}
	
	public synchronized void sendPacket(int packetHeader, byte[] packetData) throws IOException {
		System.out.printf("Sending packet header %x\n", packetHeader);
		dataOutputStream.write(packetHeader);
		if (packetData != null) {
			int packetSize = packetData.length;
			dataOutputStream.writeInt(packetSize);
			dataOutputStream.write(packetData);
		}
	}
	
	public boolean isHost() {
		return isHost;
	}
	
	@Override
	public void run() {
		try {		
			//Scanner in = new Scanner(inputStream);
			//PrintWriter out = new PrintWriter(new OutputStreamWriter(outputStream), true);			
		
			boolean done = false;
			while (!done) {
				int packetHeader = inputStream.read();
				if (handlePacket(packetHeader)) {
					done = true;
				}
			}
			System.out.println("Done");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				clientSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
