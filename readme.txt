server.java
	- server class
	- recieves requests from intermediateHost
	- extracts request and process according responses

client.java
	- client class
	- sends requests to intermediateHost and recieves from intermediateHost as well
	- sends 11 requests, 5 write, 5 read, 1 invalid
	
intermediateHost.java
	- intermediateHost class
	- recieves and sends packets from server and client
	- serves as intermediary host
	
request.java
	- request class
	- serves to process and determine types of requests from intermediateHost, server, and client

classDiagramAssignment1.pdf
	- Contains class diagrams for the above 4 classes

sequenceDiagramAssignment2.jpeg
	-Contains sequence diagram for the clients, intermediate host and the server
Setup:

1) run server.java as java application
2) run intermediateHost.java as java application
3) run client.java as java application
** run in the above order