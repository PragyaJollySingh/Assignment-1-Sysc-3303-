import java.io.*;
import java.net.*;

/**
 * @author Pragya Singh - 100974148
 * The purpose of the client is to send requests to the intermediate host, and send them to the server.
 * From the client's point of view, the intermediate host appears to be the server.
 */
public class client {

	private DatagramSocket sendReceiveSocket;
	private DatagramPacket sendPacket, receivePacket;
	private static String fileName = "PragyaSingh.txt";		//filename text
	private byte [] message;								//Message to be sent								
	private request.RequestMode defaultMode = request.RequestMode.NETASCII; //Default Mode
	private int intermediateHostPort = 23;
	/*
	 * Default Constructor
	 */
	public client(){

	}
	
	/*
	 * Create sockets for client and bind them to appropriate ports
	 */
	public void createSockets(){
		try{
			System.out.println("Client Connection.....");
			//Create a Datagram socket that is bound to any avaliable port on the client
			//This socket will be used by the client to send and receive UDP datagrams
			this.sendReceiveSocket = new DatagramSocket();		
		}catch(SocketException se){
			se.printStackTrace();
			System.exit(1);
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
	 * Creation of Write Request
	 */
	public void writeRequest(String filename){
		System.out.println("Write Request...");
		sendRequestToIntermediateHost(new request(defaultMode, request.RequestType.WRITE, filename));
	}
	
	/*
	 * Creation of Read Request
	 */
	public void readRequest(String filename){
		System.out.println("Read Request...");
		sendRequestToIntermediateHost(new request(defaultMode, request.RequestType.READ, filename));
	}
	
	/*
	 * Creation of Write Request
	 */
	public void invalidRequest(String filename){
		System.out.println("Invalid Request...");
		sendRequestToIntermediateHost(new request(defaultMode, request.RequestType.INVALID, filename));
	}
	
	/*
	 * Send request to intermediate host on the port 23
	 */
	public void sendRequestToIntermediateHost(request req){
		try{
			//Create message to send
			this.message = req.generateMessageBytesRequest(fileName);
			String msgString = req.generateMessageStringRequest(fileName);
			
			//Print information 
			System.out.println("Client: Sending the following: " + msgString);
			System.out.println("Client: Sending the following in bytes: " + this.message);
			
			//Build packet to send to the intermediate host
			System.out.println("Client: Sending packet to intermediate host.....");
			sendPacket = new DatagramPacket(this.message, this.message.length, InetAddress.getLocalHost(), intermediateHostPort);	
			
			//Send packet to port 23 of intermediate host
			sendReceiveSocket.send(sendPacket);
			
			System.out.println("Client: Waiting for packet......");
			
			//Receive response from intermediateHost
			recievePacketFromIntermediateHost();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/*
	 * Receive request from intermediate host, then prints out the information received including the byte array
	 */
	public void recievePacketFromIntermediateHost(){
		try{
			InetAddress intermediateHostAddress;
			int intermediatePort;
			byte [] response = new byte [100];
			receivePacket = new DatagramPacket(response, response.length);
			
			//Receive datagram packet from the client, using the receivedSocket
			sendReceiveSocket.receive(receivePacket);
			intermediateHostAddress = receivePacket.getAddress();
			intermediatePort = receivePacket.getPort();
			
			//Process and print out information regarding the received datagram.
			System.out.println("Client: Recieved request from Intermediate Host" + intermediateHostAddress + ": " + intermediatePort);
			System.out.println("Packet Length: " + receivePacket.getLength());
			System.out.println("Containing: ");
			printPacketAsBytesAndString(receivePacket);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		client c = new client();
		c.createSockets();
		for(int i=0; i<=11; i++){
			//Write Request
			if(i%2 == 0){
				c.writeRequest(fileName);
			}
			//Read Request
			else if(i%2 == 1){
				c.readRequest(fileName);
			}
			//Invalid Request
			else if(i==11){
				c.invalidRequest(fileName);
			}
		}

	}

}
