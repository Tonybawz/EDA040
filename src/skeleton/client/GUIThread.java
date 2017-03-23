package skeleton.client;

import java.util.ArrayList;

public class GUIThread extends Thread {
	private Client client;
	private Buffer buffer;
	private OurGUI gui;

	public GUIThread(Client client) {
		this.client = client;
		buffer = client.getBuffer();
		this.gui = new OurGUI(client);


	}

	public void run() {
		while (true) {
			ArrayList<OurImage> img = buffer.getSyncedImages();
			if (img.size() == 1) {
				gui.putImage(img.get(0));
				continue;
			}
			long diffDelay = Math.abs(img.get(0).getTime() - img.get(1).getTime());

			if (diffDelay < 200 && buffer.getSync() != Buffer.SYNC_ON && buffer.allowSyncToggle())
				buffer.setSync(Buffer.SYNC_ON);
			else if (diffDelay > 200 && buffer.getSync() != Buffer.SYNC_OFF && buffer.allowSyncToggle())
				buffer.setSync(Buffer.SYNC_OFF);
			switch (buffer.getSync()) {
			case Buffer.SYNC_OFF: {
				
				gui.putImage(img.get(0));
				gui.putImage(img.get(1));
				break;
			}
			case Buffer.SYNC_ON: {
				OurImage earliest = (img.get(0).getTime() < img.get(1).getTime()) ? img.get(0) : img.get(1);
				OurImage latest = (img.get(0).getTime() > img.get(1).getTime()) ? img.get(0) : img.get(1);
				long t0 = System.currentTimeMillis();
				long waitTime = t0 + (latest.getTime() - earliest.getTime());
				while (System.currentTimeMillis() < waitTime) {
					gui.putImage(earliest);
					OurImage checkUpdate = buffer.getImageFromCamera(earliest.getID(),
							waitTime - System.currentTimeMillis());
					if (checkUpdate != null)
						earliest = checkUpdate;
					waitTime = t0 + latest.getTime() - earliest.getTime();
				}
				gui.putImage(latest);
				break;
			}
			}
		}
	}
}