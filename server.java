import java.io.*;
import java.net.*;

/**
 * @author Pragya Singh - 100974148
 * The purpose of the server is to send responses to the intermediate host. From the server's point of view,
 * the intermediate host appears to be the client.
 *
 */
public class server {
	private DatagramSocket receiveSocket;
	private DatagramPacket receivePacket;
	private int serverPort = 69;
	private byte[] message;
	
	/*
	 * Default constructor
	 */
	public server(){
		
	}
	
	/*
	 * Create sockets for client and bind them to appropriate ports
	 */
	public void createSockets(){
		try{
			System.out.println("Server Connection: " + InetAddress.getLocalHost().getHostAddress() + ": " + serverPort);
			//Create a Datagram socket that is bound to any avaliable port on the client
			//This socket will be used by the client to send and receive UDP datagrams
			this.receiveSocket = new DatagramSocket(serverPort, InetAddress.getLocalHost());		
		}catch(SocketException se){
			se.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
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
	
	/*
	 * Receive request from intermediate host, then prints out the information received including the byte array
	 	* if invalid the program exits.
	 	* if valid read request then response 0 3 0 1 in 4 bytes is sent back
	 	* if valid write request then response 0 4 0 0 in 4 bytes is sent back
	 */
	public void recievePacketFromIntermediateHostAndSendResponse(){
		try{
			byte [] dataBytes = new byte [100];
			receivePacket = new DatagramPacket(dataBytes, dataBytes.length);
			
			//Receive datagram packet from the intermediateHost, using the receivedSocket
			this.receiveSocket.receive(receivePacket);
			
			//Create packet
			request parsedReq = new request();
			parsedReq.parsePacket(receivePacket.getData());
			
			this.message = parsedReq.generateMessageBytesRequest(parsedReq.getFileName());
			String msgString = parsedReq.generateMessageStringRequest(parsedReq.getFileName());
			
			//Print information 
			System.out.println("Server: Receiving the following: " + msgString);
			System.out.println("Server: Receiving the following in bytes: " + this.message);
			
			//Create response data based of received request
			byte response[] = null;
			
			//Read Request Response
			if(parsedReq.rType == request.RequestType.READ){
				response = new byte[]{0, 3, 0, 1};
			}
			//Write Request Response
			else if(parsedReq.rType == request.RequestType.WRITE){
				response = new byte[]{0, 4, 0, 0};
			}
			//Invalid Response and exit
			else if(parsedReq.rType == request.RequestType.INVALID){
				System.out.println("Invalid");
				System.exit(1);
			}
			
			//Create Response socket
			DatagramSocket sendSocket = new DatagramSocket();
			
			//Create Response packet
			DatagramPacket responsePacket = new DatagramPacket(response, response.length, receivePacket.getAddress(), receivePacket.getPort());
			
			//Process and print out information regarding the received datagram.
			System.out.println("Server Response To Following Packet: " + responsePacket.getAddress() + ": " + responsePacket.getPort());
			System.out.println("Packet Length: " + responsePacket.getLength());
			System.out.println("Containing: ");
			printPacketAsBytesAndString(responsePacket);
			
			//Send Response packet to intermediate host
			sendSocket.send(responsePacket);
			
			//Close Socket
			sendSocket.close();
		}catch(IOException e){
			e.printStackTrace();
		} 
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		server s = new server();
		s.createSockets();
		s.recievePacketFromIntermediateHostAndSendResponse();
	}

}
