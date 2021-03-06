/*
 * Copyright (c) 2014, vincentclee <ssltunnelnet@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package ftp.client;

/**
 * 
 * @author Will Henry
 * @author Vincent Lee
 * @version 1.0
 * @since March 26, 2014
 */

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Path;
import java.util.List;

public class GetWorker implements Runnable {
	private FTPClient ftpClient;
	private Socket socket;
	private Path serverPath, path;
	private List<String> tokens;
	private int terminateID;
	
	//Stream
	private InputStreamReader iStream;
	private BufferedReader reader;
	private DataInputStream byteStream; 
	private OutputStream oStream;
	private DataOutputStream dStream;
	
	public GetWorker(FTPClient ftpClient, String hostname, int nPort, List<String> tokens, Path serverPath, Path path) throws Exception {
		this.ftpClient = ftpClient;
		this.tokens = tokens;
		this.serverPath = serverPath;
		this.path = path;
		
		InetAddress ip = InetAddress.getByName(hostname);
		socket = new Socket();
		socket.connect(new InetSocketAddress(ip.getHostAddress(), nPort), 1000);
		
		iStream = new InputStreamReader(socket.getInputStream());
		reader = new BufferedReader(iStream);
		byteStream = new DataInputStream(socket.getInputStream());
		oStream = socket.getOutputStream();
		dStream = new DataOutputStream(oStream);
	}
	
	public void get() throws Exception {
		//same transfer
		if (!ftpClient.transfer(serverPath.resolve(tokens.get(1)))) {
			System.out.println("error: file already transfering");
			return;
		}
		
		//send command
		dStream.writeBytes("get " + serverPath.resolve(tokens.get(1)) + "\n");
		
		//error messages
		String get_line;
		if (!(get_line = reader.readLine()).equals("")) {
			System.out.println(get_line);
			return;
		}
		
		//wait for terminate ID
		try {
			terminateID = Integer.parseInt(reader.readLine());
		} catch(Exception e) {
			if (Main.DEBUG) System.out.println("Invalid TerminateID");
		}
		System.out.println("TerminateID: " + terminateID);
		
		//CLIENT side locking
		ftpClient.transferIN(serverPath.resolve(tokens.get(1)), terminateID);
		
		if (ftpClient.terminateGET(path.resolve(tokens.get(1)), serverPath.resolve(tokens.get(1)), terminateID)) return;
		
		//get file size
		byte[] fileSizeBuffer = new byte[8];
		byteStream.read(fileSizeBuffer);
		ByteArrayInputStream bais = new ByteArrayInputStream(fileSizeBuffer);
		DataInputStream dis = new DataInputStream(bais);
		long fileSize = dis.readLong();
		
		if (ftpClient.terminateGET(path.resolve(tokens.get(1)), serverPath.resolve(tokens.get(1)), terminateID)) return;
		
		//receive the file
		FileOutputStream f = new FileOutputStream(new File(tokens.get(1)));
		int count = 0;
		byte[] buffer = new byte[8192];
		long bytesReceived = 0;
		while(bytesReceived < fileSize) {
			if (ftpClient.terminateGET(path.resolve(tokens.get(1)), serverPath.resolve(tokens.get(1)), terminateID)) {
				f.close();
				return;
			}
			count = byteStream.read(buffer);
			f.write(buffer, 0, count);
			bytesReceived += count;
		}
		f.close();
		
		//CLIENT side un-locking
		ftpClient.transferOUT(serverPath.resolve(tokens.get(1)), terminateID);
	}
	
	public void run() {
		try {
			get();
			Thread.sleep(100);
			dStream.writeBytes("quit" + "\n");
		} catch (Exception e) {
			if (Main.DEBUG) System.out.println("GetWorker error");
		}
	}
}
