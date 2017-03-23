package skeleton.client;

import skeleton.server.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

import se.lth.cs.eda040.proxycamera.AxisM3006V;

public class Client {

	private SendThread sendThread, st, std;
	private ReceiveThread receiveThread, rt, rtd;
	private InputStream input;
	private OutputStream output;
	private Buffer buffer;
	int length = 0;
	int timestamp = 0;
	int mode = 1;

	public Client() {
		buffer = new Buffer();
		sendThread = null;
		receiveThread = null;
	}

	public synchronized void requestMessages(int msg) {
		buffer.getMessageList().offer(msg);
		buffer.getMessageListtwo().offer(msg);
		notifyAll();
	}

	public synchronized int getMessage(int id) {
		if (id == 6077) {
			if (buffer.getMessageList().isEmpty()) {
				return 99;
			} else {
				return buffer.getMessageList().poll();
			}
		} else {
			if (buffer.getMessageListtwo().isEmpty()) {
				return 99;
			} else {
				return buffer.getMessageListtwo().poll();
			}
		}
	}

	public synchronized Socket connect(String host, int port) throws Throwable, IOException {
		Socket sock = new Socket(host, port);
		sock.setTcpNoDelay(true);
		output = sock.getOutputStream();
		input = sock.getInputStream();

		sendThread = new SendThread(this, output, port);
		receiveThread = new ReceiveThread(this, input);
		sendThread.start();
		receiveThread.start();
		if (port == 6077) {
			st = sendThread;
			rt = receiveThread;
		} else if (port == 6078) {
			std = sendThread;
			rtd = receiveThread;
		}
		return sock;
	}

	public synchronized void disconnect(Socket socket, int id) {
		if (id == 1) {
			buffer.getMessageList().offer(0);
			st.stop();
			rt.stop();
		} else if (id == 0) {
			buffer.getMessageListtwo().offer(8);
			std.stop();
			rtd.stop();
		}
		notifyAll();
		// sendThread.stop();
		// receiveThread.stop();
		// socket.getInputStream().close();
		// socket.getOutputStream().close();
		// socket.close();
	}

	public Buffer getBuffer() {
		return buffer;
	}

	public void receiveImage(InputStream is) {
		try {
			if (is.available() > 5) {
				int msg = is.read();
				switch (msg) {

				case Codes.PUT_IMAGE: {
					int n = AxisM3006V.IMAGE_BUFFER_SIZE + 16;
					byte[] picture = new byte[n];
					int read = 0;
					while (read < n) {
						read += is.read(picture, read, n - read);
					}
					OurImage image = new OurImage(picture);
					buffer.add(image);
					break;
				}
				case Codes.SET_MOVIE: {
					int id = is.read();
					buffer.setMode(Buffer.MODE_MOVIE, id);
					break;
				}
				default: {
					System.out.println("Unrecognized command.");
				}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
