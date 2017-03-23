package skeleton.client;

import java.io.IOException;
import java.io.OutputStream;
import skeleton.server.Codes;

public class SendThread extends Thread {

	private Client client;
	private OutputStream output;
	private int id;

	public SendThread(Client client, OutputStream output, int id) {
		this.client = client;
		this.output = output;
		this.id = id;
	}

	public void run() {
		while (true) {
			try {
				int msg = client.getMessage(id);
				if (msg != 99) {
					switch (msg) {
					case Codes.SET_IDLE: {
						output.write(msg);
						break;
					}
					case Codes.SET_MOVIE: {
						output.write(msg);
						break;
					}
					case Codes.SET_AUTO_ON: {
						output.write(msg);
						break;
					}
					case Codes.SET_AUTO_OFF: {
						output.write(msg);
						break;
					}
					case Codes.DISCONNECT: {
						output.write(msg);
						break;
					}
					case Codes.DISCONNECTSECOND: {
						output.write(msg);
						break;
					}
					default: {
						System.out.println("Unrecognized operation code: "
								+ msg);
					}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
