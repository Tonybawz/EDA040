package skeleton.client;

import java.io.InputStream;

public class ReceiveThread extends Thread {
	private Client client;
	private InputStream is;

	public ReceiveThread(Client client, InputStream is) {
		this.client = client;
		this.is = is;
	}

	public void run() {
		while (true) {
			client.receiveImage(is);
		}
	}

}
