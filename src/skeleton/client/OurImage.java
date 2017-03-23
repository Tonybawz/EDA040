package skeleton.client;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

public class OurImage {
	private long delay;
	private byte[] image;
	private int id;
	private long timestamp;

	public OurImage(byte[] picture) throws IOException {
		image = createImage(picture);
		this.delay = System.currentTimeMillis() - timestamp;
	}

	public byte[] createImage(byte[] data) {
		int lengthOfImage = ByteBuffer.wrap(data, 0, 4).getInt();
		this.id = ByteBuffer.wrap(data, 4, 4).getInt();
		this.timestamp = ByteBuffer.wrap(data, 8, 8).getLong();
		byte[] tempImage = new byte[lengthOfImage];
		System.arraycopy(data, 16, tempImage, 0, lengthOfImage);
		return tempImage;

	}

	public byte[] getImage() {
		return image;
	}

	public long getDelay() {
		return delay;
	}

	public int getID() {
		return id;
	}

	public long getTime() {
		return timestamp;
	}

}
