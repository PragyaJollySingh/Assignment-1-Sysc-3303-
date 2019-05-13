import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 
 */

/**
 * @author Pragya Singh - 100974148
 * Request class its purpose is to determine process and determine the types of requests from the client, server, and intermediate host
 */
public class request {
	public RequestMode rMode;
	public RequestType rType;
	public String fileName =  "";
	public String modeString = "";
	public boolean isValidPacket = true;
	/**
	 * ENUMS for read, write and invalid
	 */
	public enum RequestType{
		READ,
		WRITE,
		INVALID,
	}
	
	public enum RequestMode{
		NETASCII,
		OCTET
	}
	
	public class InvalidRequestTypeException extends Exception{
		private static final long serialVersionUID = 1L;

		public InvalidRequestTypeException(String message){
			super(message);
		}
	}
	
	/*
	 * Default Constructor
	 */
	public request(){
		
	}
	
	public request(RequestMode rMode, RequestType rType, String fileName) {
		this.rMode = rMode;
		this.rType = rType;
		this.fileName = fileName;
	}
	
	public String getFileName() {
		return fileName;
	}

	/*
	 * Creates message byte array
	 */
	public byte[] generateMessageBytesRequest(String filename){
		try{
			//Create Message Byte array
			ByteArrayOutputStream messageByteStream = new ByteArrayOutputStream();
			
			//Always start first byte with a 0
			messageByteStream.write(0);
			
			//Determine request type
			switch(rType){
			case READ:
				messageByteStream.write(1); // Read request has 2nd byte with a 1
				break;
			case WRITE:
				messageByteStream.write(2); // Write request has 2nd byte with a 2
				break;
			case INVALID:
				messageByteStream.write(3); // Invalid request has 2nd byte with a 3
				break;
			default:
				throw new request.InvalidRequestTypeException("Invalid Request!!!");
			}
			
			//Convert filename to bytes
			if(filename != null && !filename.isEmpty()){
				messageByteStream.write(filename.getBytes());
			}
			
			//Add 0 byte
			messageByteStream.write(0);
			
			//Add mode to message byte array
			messageByteStream.write(rMode.toString().getBytes());
			
			//Add 0 byte
			messageByteStream.write(0);
			
			return messageByteStream.toByteArray();
		}catch(IOException e){
			e.printStackTrace();
		} catch (InvalidRequestTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	/*
	 * Generate message in string format
	 */
	public String generateMessageStringRequest(String filename){
		try{
			//Create Message String
			StringBuffer messageString = new StringBuffer();
			
			//Start with a 0
			messageString.append(0);
			
			//Determine request type
			switch(rType){
			case READ:
				messageString.append(1); // Read request has 2nd byte with a 1
				break;
			case WRITE:
				messageString.append(2); // Write request has 2nd byte with a 2
				break;
			case INVALID:
				messageString.append(3); // Invalid request has 2nd byte with a 3
				break;
			default:
				throw new request.InvalidRequestTypeException("Invalid Request!!!");
			}
			
			//Add filename
			if(filename != null && !filename.isEmpty()){
				messageString.append(filename);
			}
			
			//Add 0 byte
			messageString.append(0);
			
			//Add mode to message byte array
			messageString.append(rMode.toString());
			
			//Add 0 byte
			messageString.append(0);
			
			return messageString.toString();
		}catch (InvalidRequestTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	/*
	 * Parses through the received packet and checks if the packet is valid or not	
	 * Returns new request packet
	 */
	public request parsePacket(byte[] receivedPacket){
		
		//Check Request type
		if(receivedPacket[0] == 0 && receivedPacket[1] == 1){
			rType = request.RequestType.READ;
		}
		else if(receivedPacket[0] == 0 && receivedPacket[1] == 2){
			rType = request.RequestType.WRITE;
		}
		else if(receivedPacket[0] == 0 && receivedPacket[1] == 3){
			rType = request.RequestType.INVALID;
			isValidPacket = false;
		}
		
		
		String rpString = receivedPacket.toString();	
		int zeroAfterFileName = rpString.indexOf("0", 2);
		int zeroAfterMode = rpString.indexOf("0", zeroAfterFileName);
		//Extract filename
		fileName = rpString.substring(1, zeroAfterFileName);
		
		//Check if next byte after filename is 0
		if(receivedPacket[zeroAfterFileName] != 0){
			isValidPacket = false;
		}
		
		//Extract mode
		modeString = rpString.substring(zeroAfterFileName, zeroAfterMode);
		if(modeString.equals("octet")){
			rMode = RequestMode.OCTET;
		}
		else if(modeString.equals("netascii")){
			rMode = RequestMode.NETASCII;
		}
		
		//Check if last byte is 0
		if(receivedPacket[zeroAfterMode] != 0){
			isValidPacket = false;
		}
		
		return new request(rMode, rType, fileName);
	}
}
