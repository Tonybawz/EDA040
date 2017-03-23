package skeleton.client;

import java.io.IOException;
import java.util.Scanner;

import skeleton.server.Server;

public class RunClass {
	public static void main(String[] args) {
		// run with 5 6077 0 5000 8 6078 1 5002
		String[] arguments = new String[8];
		Scanner scan = new Scanner(System.in);
		System.out.println("Arguments for first camera/server connection");
		System.out.println("rt@argus-N, insert chosen N:");
		arguments[0] = scan.nextLine();
		System.out.println("Insert chosen proxyport ****:");
		arguments[1] = scan.nextLine();
		System.out.println("Arguments for second camera/server connection");
		System.out.println("rt@argus-N, insert chosen N:");
		arguments[2] = scan.nextLine();
		System.out.println("Insert chosen proxyport ****:");
		arguments[3] = scan.nextLine();

		Client client = new Client();

		GUIThread gui = new GUIThread(client);

		gui.start();

		Server1 s = new Server1(arguments[0], "6077", "0", arguments[1]);
		s.start();
		Server2 s2 = new Server2(arguments[2], "6078", "1", arguments[3]);
		s2.start();
	}
}

class Server1 extends Thread {
	private String camera, localport, cameraID, proxyport;

	public Server1(String camera, String localport, String cameraID, String proxyport) {
		this.camera = camera;
		this.localport = localport;
		this.cameraID = cameraID;
		this.proxyport = proxyport;
	}

	public void run() {
		String[] s = { camera, localport, cameraID, proxyport };
		Server.main(s);
	}
}

class Server2 extends Thread {
	private String camera, localport, cameraID, proxyport;

	public Server2(String camera, String localport, String cameraID, String proxyport) {
		this.camera = camera;
		this.localport = localport;
		this.cameraID = cameraID;
		this.proxyport = proxyport;
	}

	public void run() {
		String[] s = { camera, localport, cameraID, proxyport };
		Server.main(s);
	}
}