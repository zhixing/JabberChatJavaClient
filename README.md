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
