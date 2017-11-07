1. Working Environment
	> Amazon EC2 t2.micro instance
	> SSH using 	
				>> url:		ubuntu@ec2-35-164-165-233.us-west-2.compute.amazonaws.com
				>> username:	ubuntu
				>> password:	123456789

	> Prerequisites: 	Install PuTTY (http://www.chiark.greenend.org.uk/~sgtatham/putty/download.html)
				Open Putty > Connection > SSH > Auth > Private key file for authentication > Select "privatekey1.ppk" file
				Now Click on Session > enter url in hostname and click on Open.
				Next click yes for the confirmation dialogue.
				Enter the password to login.


2. Program Requirement ( IDE, library, test tool)
	> Java Platform (JDK) 8u111 / 8u112 			(http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
	> IntelliJ IDEA 					(https://www.jetbrains.com/idea/#chooseYourEdition)
	> PuTTY 						(http://www.chiark.greenend.org.uk/~sgtatham/putty/download.html)
	> WinSCP 5.9.3 						(https://winscp.net/eng/download.php)


3. How to compile and execute your project program.
	
	-- Server --
	> Transfer the server file "FTPServer\src\Main.java" to EC2 using WinSCP
				>> Prerequisites:	Open WinSCP, Enter Hostname ec2-35-164-165-233.us-west-2.compute.amazonaws.com, username "ubuntu"
							Click on Advanced > SSH > AUthentication > Private key file: and
							Locate the "privatekey1.ppk" file and click "ok"

	> Start PuTTy session and complie using Super User privileges
		sudo javac Main.java
	> Run the java file using Super User privileges
		sudo java Main

	-- Client --
	> Open command prompt or terminal and run the client file "FTPClient\out\production\FTPClient\Main"
		java Main
	> Once the GUI pops up, Enter the "HOSTNAME" "ec2-35-164-170-132.us-west-2.compute.amazonaws.com" with ther server running in PuTTy Session.
		Enter the desired "USERNAME" and click on "Login" button
		A session between the client and server will be established.

4. Your FTP command list (or how to execute command)
	> "Login" button implements the USER command and PASV command.
	> "Upload" button enables you to select one or more file(s) and upload to the server while connected.
	> Enter "Filename" to Either download or delete.
	> Conflicting Upload/Download or Delete operations cannot happen
	> Press "Quit" to terminate the session and terminate the client application.

5. sample scenario ( Server function/ Client function /upload / download)
	
	-- Login --
	> With server running in PuTTy open the client and enter the "HOSTNAME" and "USERNAME"
		click Login to start the FTP session

	-- upload --
	> After login, click on "upload", 
		select one or more file(s) using the file chooser and upload by clicking "OK"
		Files will be uploaded

	-- download --
	> After login, with atleast on file in the server to download, 
		enter "Filename" to download and select the location where to download.
		Files will be downloaded

	-- delete --
	> After login, with atleast on file in the server to delete, 
		enter "Filename" to delete and click delete.
		File will be delete

	-- quit --
	> After login, click on "QUIT",
		The session will be terminated and the client window closes
	
	-- Status text --
	> No user interactions involved.
		will display corresponding messages for all the above mentioned operations