JabberChatJavaClient
====================

A Jabber Chat Client implemented in Java

Name: Yang Zhixing
Matric Number: A0091726B

Instructions on how to run it:
It can be run in Eclipse IDE. Here're the instructions:
	- Open Eclipse
	- Import the project: File --> Import --> General --> "Existing Projects into Workspace"
	- Import the codec library: 
		- right click on "src" folder on the left navigation panel
		- Select "BuildPath", then "Configure BuildPath"
		- Click on "Add External Jars"
		- Select the "commons-codec-1.8.jar" in this folder
	- Run:
		- It's runned with command line arguments, to do so:
			- Go to "Run", 
			- then "Run Configurations", 
			- go to "Arguments" tab, under "Program Arguments", input the following:
					email_address password server_address port_number.
			For example:
					example@gmail.com mypassword talk.google.com 5222
			- Click run

The Java file was developed in:
	- Max OS X 10.8.5 Mountain Lion
	- java version "1.7.0_40"
	- Java(TM) SE Runtime Environment (build 1.7.0_40-b43)
	- Java HotSpot(TM) 64-Bit Server VM (build 24.0-b56, mixed mode)

Extra Features:
	- Extension of Task 2: If the Internet is down when the when program begings to run, the error can 
	  still bedetected and a timer will be scheduled to re-connect to the Internet. It's using the 
	  same algorithm (exponential backoff and re-connection countdown). This will make the system safer
	  and more robust.
