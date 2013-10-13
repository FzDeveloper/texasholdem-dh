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

package org.darkhood.games.texasholdem.network.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.darkhood.games.texasholdem.network.PacketHeaders;

public class GameClient {
	
	
	
	public static void main(String[] args) {
		if (args.length <= 0) return;
		String playerName = args[0];
		byte[] playerNameBytes = playerName.getBytes(Charset.forName("UTF-8"));
		
		try {
			Socket s = new Socket();
			try {
				s.connect(new InetSocketAddress("localhost", 44491));
				InputStream inStream = s.getInputStream();
				OutputStream outStream = s.getOutputStream();
				DataInputStream dataInStream = new DataInputStream(inStream);
				DataOutputStream dataOutStream = new DataOutputStream(outStream); 
				
				// Handshake, sending player name
				dataOutStream.write(PacketHeaders.CL_CONNECT);
				dataOutStream.writeInt(playerNameBytes.length);
				dataOutStream.write(playerNameBytes);
				
				int serverResponse = dataInStream.read();
				if (serverResponse == PacketHeaders.OK) {
					System.out.println("OK, connected");
				}
				
				boolean done = false;
				while(!done) {
					serverResponse = dataInStream.read();
					if (serverResponse == PacketHeaders.S_PLAYER_CONNECTED) {
						int packetSize = dataInStream.readInt();
						byte[] buffer = new byte[packetSize];
						dataInStream.read(buffer, 0, packetSize);
						ByteBuffer bb = ByteBuffer.wrap(buffer);
						int connectedClientId = bb.getInt();

						serverResponse = dataInStream.read();
						if (serverResponse == PacketHeaders.S_PLAYER_CONNECTED_NAME) {
							packetSize = dataInStream.readInt();
							buffer = new byte[packetSize];
							dataInStream.read(buffer, 0, packetSize);
							String connectedPlayerName = new String(buffer, "UTF-8");
							System.out.printf("%s has connected with clientId %d\n", connectedPlayerName, connectedClientId);
						}
						
					}
				}
			} finally {
				s.getOutputStream().write(PacketHeaders.CL_DISCONNECT);
				int response = s.getInputStream().read();
				if (response == PacketHeaders.OK) {
					System.out.println("Disconnect handshake was successful");
				} else {
					System.out.println("Something went wrong");
				}
				
				s.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
