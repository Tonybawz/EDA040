package skeleton.server;

import java.io.IOException;
import java.io.OutputStream;

public class SendServerThread extends Thread {
	private Server server;
	private OutputStream outputStream;

	public SendServerThread(Server server, OutputStream outputStream) {
		this.server = server;
		this.outputStream = outputStream;
	}

	public void run() {
		while (!isInterrupted()) {
			try {
				int msg = server.getMessage();
				switch (msg) {
				case (Codes.PUT_IMAGE): {
					outputStream.write(msg);
					outputStream.write(server.sendImage());
					break;
				}
				case (Codes.SET_MOVIE): {
					outputStream.write(msg);
					outputStream.write(server.getID());
					break;
				}
				default: {
					System.out.println("Unrecognized operation code: " + msg);
				}
				}
				outputStream.flush();
			} catch (IOException e) {
				return;
			}
		}
	}
}
