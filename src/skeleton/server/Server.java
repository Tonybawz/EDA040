package skeleton.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import se.lth.cs.eda040.proxycamera.AxisM3006V;
import java.io.IOException;

public class Server {
	private AxisM3006V myCamera;
	private RetrieveImage image;
	static SendServerThread st, s;
	static ReceiveServerThread rt, r;
	private long lastImageSent;
	private boolean idle = true;
	private boolean auto = true;
	private int port;
	private byte[] rawImage;
	private int id;
	private LinkedList<Integer> messages;

	public Server(String host, int port, int id, int proxyport) {
		this.port = port;
		this.id = id;
		messages = new LinkedList<Integer>();
		myCamera = new AxisM3006V();
		myCamera.init();
		myCamera.setProxy("argus-" + host + ".student.lth.se", proxyport);
		JPEGHTTPServer jhs = new JPEGHTTPServer(port+3, this);
		jhs.start();
		if (!myCamera.connect()) {
			System.out.println("Failed to connect to camera");
			System.exit(0);
		}
		image = new RetrieveImage(this, myCamera);
		image.start();
	}

	public static void main(String[] args) {
		Server server = new Server(args[0], Integer.parseInt(args[1]),
				Integer.parseInt(args[2]), Integer.parseInt(args[3]));
		ServerSocket serversocket = null;
		try {
			serversocket = new ServerSocket(Integer.parseInt(args[1]));
			System.out.println("Server connected to port: "
					+ Integer.parseInt(args[1]));

			while (true) {
				Socket clientSocket = serversocket.accept();
				System.out.println("Server accepted");
				clientSocket.setTcpNoDelay(true);
				SendServerThread std = new SendServerThread(server,
						clientSocket.getOutputStream());
				std.start();
				ReceiveServerThread rtd = new ReceiveServerThread(server,
						clientSocket.getInputStream());
				rtd.start();
				if (Integer.parseInt(args[1]) == 6077) {
					st = std;
					rt = rtd;
				} else if (Integer.parseInt(args[1]) == 6078) {
					s = std;
					r = rtd;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Connection failed.");
		} finally {
			try {
				serversocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	// public synchronized void TonyMode()

	public int getPort() {
		return port;
	}

	public int getID() {
		return id;
	}

	public void getImage(byte[] image) {
		rawImage = image;
	}

	public synchronized int getMessage() {
		while (true) {
			if (messages.isEmpty() == false)
				return messages.poll();
			long nextSend = lastImageSent + (idle ? 5000 : 40);
			long curTime = System.currentTimeMillis();
			if (curTime >= nextSend) {
				lastImageSent = curTime;
				return Codes.PUT_IMAGE;
			}
			try {
				wait(nextSend - curTime);
			} catch (InterruptedException e) {
			}
		}
	}

	public byte[] sendImage() {
		return rawImage;
	}

	public synchronized void onMotionDetected() {
		if (!idle || !auto)
			return;
		idle = false;
		messages.offer(Codes.SET_MOVIE);
		notifyAll();
	}

	public void destroy() {
		myCamera.destroy();
	}

	public int getMessages() {
		return messages.poll();
	}

	public boolean messagesIsEmpty() {
		return messages.isEmpty();
	}

	public synchronized void setMovie(boolean status) {
		idle = !status;
		notifyAll();
	}

	public synchronized void setAuto(boolean status) {
		auto = status;
	}

	public void disconnect(int camera) {
		if (camera == 1) {
			st.interrupt();
			rt.interrupt();

		} else if (camera == 2) {
			s.interrupt();
			r.interrupt();
		}
	}

}