package skeleton.server;

import java.io.IOException;
import java.io.InputStream;

public class ReceiveServerThread extends Thread {
	private Server server;
	InputStream is;

	public ReceiveServerThread(Server server, InputStream is) {
		this.server = server;
		this.is = is;
	}

	public void run() {
		try {
			while (!isInterrupted()) {
				int msg = is.read();
				if (msg == -1)
					return;
				switch (msg) {
				case Codes.DISCONNECT:{
					server.disconnect(1);
					break;
				}
				case Codes.DISCONNECTSECOND:{
					server.disconnect(2);
					break;
				}
				case Codes.SET_IDLE: {
					server.setMovie(false);
					break;
				}
				case Codes.SET_MOVIE: {
					server.setMovie(true);
					break;
				}
				case Codes.SET_AUTO_ON: {
					server.setAuto(true);
					break;
				}
				case Codes.SET_AUTO_OFF: {
					server.setAuto(false);
					break;
				}
				default: {
					System.out.println("Unrecognized operation code: " + msg);
				}
				}
			}
		} catch (IOException e) {
			return;
		}
	}
}
