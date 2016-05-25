
/*
 * Sreenidhi Krishna
 * v01 01/28/2016
 * Source : Request.java
 * Aim: Process the client Request
 */

import java.io.File;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;,
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

//Make the class implement Runnable for multi-threading 
public final class Request implements Runnable {

	final static String endLine = "\r\n";// For convenience
	Socket socket;
	String rootPath;

	// Constructor
	public Request(Socket socket, String rootPath) throws Exception {
		this.socket = socket;
		this.rootPath = rootPath;
	}

	// Implement the run() method of the Runnable interface.
	public void run() {

		try {

			// Call the function to handle request
			requestFunction();
		} catch (Exception e) {
			
			e.printStackTrace();
		}

	}

	private void requestFunction() throws Exception {

		InputStream inp = socket.getInputStream();
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		out.flush();

		// Set up input stream filters
		BufferedReader b = new BufferedReader(new InputStreamReader(inp));

		// To Open the requested file.
		FileInputStream fin = null;
		boolean filePresent = true;
		StringTokenizer t;
		String parseFileName = " ";

		File file;
		String fileSet = " ";

		// Construct the response message
		String statusLine = " ";

		// Set initial values to null
		String contentTypeLine = " ";
		String entityBody = " ";
		String contentLengthLine = " ";
		String dateHeader = " ";

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();

		dateHeader = "\n Date-Header: " + dateFormat.format(date);

		String dataRequestLine = " ";

		dataRequestLine = b.readLine();

		if (dataRequestLine != null) {
			// prints request line out to screen
			System.out.println("data request  line " + dataRequestLine);
			

				// String Tokenizer is used to extract file name from this class
				t = new StringTokenizer(dataRequestLine);

				
				String methodType = " ";
				if (t.hasMoreTokens()) {
					methodType = t.nextToken();
				}

				if (!methodType.equals("GET")) {
										
					statusLine = " HTTP/1.0 400 ";
					entityBody = "\n" + " Bad Request " + "\n ";

				} else {

					// go to next token
					if (t.hasMoreTokens()) {

						parseFileName = t.nextToken();

					}
					
					//System.out.println("parsed file name is " + parseFileName);
					file = new File(parseFileName);

					// Set the location of server files
					if (parseFileName.equals("/index.html")) {
						

						// filePresent = true;
						fileSet = rootPath + parseFileName;
						
						//if the file doesnot have read permissions
						
						 if (!(file.canRead())) {
						
						 
						 statusLine =" HTTP/1.0 403 ";
						 entityBody = "\n" + " Permission Denied " +"\n";
						   }
						
					}
					
					else if (parseFileName.equals("/")
							|| parseFileName.equals("")) {

						fileSet = rootPath + "/index.html";
						
					}

					else if (!parseFileName.equals("/index.html")) {

						if ((parseFileName.endsWith(".html"))
								|| (parseFileName.endsWith(".png"))
								|| (parseFileName.endsWith(".jpg"))
								|| (parseFileName.endsWith(".css"))
								|| (parseFileName.endsWith(".htm"))
								|| (parseFileName.endsWith(".js"))
								|| (parseFileName.endsWith(".JPG"))
								|| (parseFileName.endsWith(".txt"))
								|| (parseFileName.endsWith(".jpeg")
								|| (parseFileName.endsWith(".gif"))
								|| (parseFileName.endsWith(".ico"))) ){
							
							fileSet = rootPath
									+ parseFileName;
							//System.out.println(fileSet);
						} else {

							// Check to see if there is an index file in the
							// directory.

							fileSet = rootPath
									+ parseFileName + "/index.html";
							
						}

					}
					
				
					try {
						String str = ".";
						while (!str.equals("")) {
							str = b.readLine();
							System.out.println(str);
						}

					} catch (IOException e) {
						
						e.printStackTrace();
					}

				
					// Open the requested file.
					try {
						fin = new FileInputStream(fileSet);
						
						filePresent = true;

					} catch (FileNotFoundException e) {
						filePresent = false;
						System.out.println("filename not found");

						
					}
				}
				
					if (filePresent) {

					
						statusLine = "HTTP/1.0 200 OK ";
						contentTypeLine = "Content-Type: "
								+ contentType(parseFileName);

						contentLengthLine = "Content-Length: "
								+ Integer.toString(fin.available()) + "\r\n";

						// End of response message construction
						// Send the status line
						out.writeBytes(statusLine);
						out.flush();


						
						// Send the date header line
						out.writeBytes(dateHeader);
						out.flush();
						
						// Send the content type line
						out.writeBytes(contentTypeLine);
						out.flush();

						// Send the content type line
						out.writeBytes(contentLengthLine);
						out.flush();
						

						// Send a blank line to indicate the end of the header
						out.writeBytes(endLine);
						out.flush();
						

						try {
							
							sendFile(fin, out);

						} catch (Exception e) {
							
							e.printStackTrace();
						}
						fin.close();

					} else {
						

						if (statusLine.equals(" ")) {
							
							statusLine = " HTTP/1.0 404 ";
						}

						if (entityBody.equals(" ")) {
							
							entityBody = "\n" +" File Not found "+ "\n ";
					        }
                      

 						 contentTypeLine = "\n" + "Content-Type: text/html"+ "\n" ;
                                                           

                                                contentLengthLine = "\n" + "Content-Length: 0 \r\n";


						out.writeBytes(statusLine);
						System.out.println(statusLine);
						out.flush();
						out.writeBytes(dateHeader);
						out.flush();
                                                out.writeBytes(contentTypeLine);
                                                out.flush();
                                                out.writeBytes(contentLengthLine);
						out.flush();
						out.writeBytes(entityBody);
						out.flush();					
						out.writeBytes(endLine);
						out.flush();
					}
				
		}

		// Close streams and socket
		out.close();
		b.close();
		socket.close();

	}

	// Need this one for sendBytes function called in processRequest
	private static void sendFile(FileInputStream fin, OutputStream out)
			throws Exception {

		
		// Construct a 1K buffer to hold bytes on their way to the socket.
		byte[] buffer = new byte[1048576];
		int bytes = 0;

		// copy requested file into the sockets output stream.
		while ((bytes = fin.read(buffer)) > 0) {

			out.write(buffer, 0, bytes);
			out.flush();
		}
	}

	private static String contentType(String fileName) {
		if (fileName.endsWith(".htm") || fileName.endsWith(".html"))
			return "text/html";
		if (fileName.endsWith(".jpg"))
			return "text/jpg";
		if (fileName.endsWith(".JPG"))
			return "text/JPG";
		if (fileName.endsWith(".jpeg"))
			return "text/jpeg";
		if (fileName.endsWith(".gif"))
			return "text/gif";
		if (fileName.endsWith(".png"))
			return "text/png";
		return "application/octet-stream";
	}
}
