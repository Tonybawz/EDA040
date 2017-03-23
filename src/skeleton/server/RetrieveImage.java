package skeleton.server;

import se.lth.cs.eda040.proxycamera.AxisM3006V;

public class RetrieveImage extends Thread {
	private Server server;
	private AxisM3006V cam;

	public RetrieveImage(Server server, AxisM3006V myCamera) {
		this.server = server;
		this.cam = myCamera;
	}

	public void run() {

		while (!isInterrupted()) {
			byte[] image = new byte[AxisM3006V.IMAGE_BUFFER_SIZE + 16];
			int length = cam.getJPEG(image, 16);
			System.arraycopy(getIntBytes(length), 0, image, 0, 4);
			System.arraycopy(getIDBytes(server.getID()), 0, image, 4, 4);
			cam.getTime(image, 8);
			server.getImage(image);
			if (cam.motionDetected()) {
				server.onMotionDetected();
			}
		}
	}

	private byte[] getIntBytes(int value) {
		byte[] result = new byte[4];

		result[0] = (byte) (value >> 24);
		result[1] = (byte) (value >> 16);
		result[2] = (byte) (value >> 8);
		result[3] = (byte) (value);

		return result;
	}

	private byte[] getIDBytes(int value) {
		byte[] result = new byte[4];

		result[0] = (byte) (value >> 24);
		result[1] = (byte) (value >> 16);
		result[2] = (byte) (value >> 8);
		result[3] = (byte) (value);

		return result;
	}
}
