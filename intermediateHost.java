/**
 * @author Pragya Singh - 100974148
 * The purpose of the intermediate host is essentially it waits to receive a request
 * from either the server or the client. Requests that are received from the client are
 * passed on to the server by the intermediate host and requests that are received from the
 * server are passed on to the client by the intermediate host. The intermediate host also 
 * prints out all information that has been receieved.
 */

import java.io.*;
import java.net.*;

public class intermediateHost {
	private DatagramPacket sendPacket, receivePacket;
	private DatagramSocket sendRecieveSocket, receiveSocket;
	private int hostPort = 23;
	private int serverPort = 69;
	//Bytes of data containing up to 100 bytes of data, used for receiving and sending datagrams
	byte[] dataBytes = new byte [100];
	
	/*
	 * Default Constructor 
	 */
	public intermediateHost(){

	}
	
	/*
	 * Create sockets for intermediateHost and bind them to appropriate ports
	 */
	public void createSockets() {
		try{
			System.out.println("Intermediate Host Connection: " + InetAddress.getLocalHost().getHostAddress() + ": " + hostPort);
			//Create a Datagram socket that is bound to any avaliable port on the local host
			//This socket will be used by the intermediate host to send and receive UDP datagrams
			this.sendRecieveSocket = new DatagramSocket();
			//Create a Datagram socket that is bound to port 23, this socket will be used by the
			//intemediate host to send recieve UDP Datagrams
			this.receiveSocket = new DatagramSocket(hostPort, InetAddress.getLocalHost());
		}catch(SocketException se){
			System.out.println("Unable to create sockets for intermediate host.");
			se.printStackTrace();
			System.exit(1);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void sendRecievePackets(){
		//Wait forever to receive requests, and print out information received
		while(true){
			System.out.println("Intermediate Host: Waiting for Packet...");
			
			try{
				InetAddress clientAddress, serverAddress;
				int clientPort;
				//Build receivePacket
				receivePacket = new DatagramPacket(this.dataBytes, this.dataBytes.length);
				
				//Receive datagram packet from the client, using the receivedSocket
				receiveSocket.receive(receivePacket);
				clientAddress = receivePacket.getAddress();
				clientPort = receivePacket.getPort();
				
				//Process and print out information regarding the received datagram.
				System.out.println("Intermediate Host: Recieved request from Host" + clientAddress + ": " + clientPort);
				System.out.println("Packet Length: " + receivePacket.getLength());
				System.out.println("Containing: ");
				printPacketAsBytesAndString(receivePacket);
				
				//Build sendPacket to send back to the server that is sending the request
				serverAddress = InetAddress.getLocalHost();
				sendPacket = new DatagramPacket(receivePacket.getData(), receivePacket.getLength(), serverAddress, serverPort);
				sendRecieveSocket.send(sendPacket);
				
				//Receive response from the server, based of the packet that is sent using the sendReceiveSocket
				byte dataBytes[] = new byte[100];
				receivePacket = new DatagramPacket(dataBytes, dataBytes.length);
				sendRecieveSocket.receive(receivePacket);
				
				//Process and print out information regarding the received datagram.
				System.out.println("Intermediate Host: Recieved request from Server" + serverAddress + ": " + serverPort);
				System.out.println("Packet Length: " + receivePacket.getLength());
				System.out.println("Containing: ");
				printPacketAsBytesAndString(receivePacket);
				
				//Forward the response to the client
				sendPacket = new DatagramPacket(receivePacket.getData(), receivePacket.getLength(), clientAddress, clientPort);
				sendRecieveSocket.send(sendPacket); 
				
			}catch(IOException io){
				io.printStackTrace();
			}
		}
	}
	
	/*
	 * Prints out packet information in both Bytes and String
	 */
	public void printPacketAsBytesAndString(DatagramPacket dP){
		System.out.println("Information as a Bytes:");
		System.out.println(dP.getData());
		System.out.println("Information as a String:");
		for(byte b : dP.getData()){
			System.out.println("Byte: " + b);
		}
		System.out.println();
		System.out.println();
		System.out.println();
	}
	
	public static void main(String[] args) {
		intermediateHost iHost = new intermediateHost();
		iHost.createSockets();
		iHost.sendRecievePackets();
	}

}
