package skeleton.client;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Observable;

import skeleton.server.Codes;

public class Buffer extends Observable {
	private ArrayList<LinkedList<OurImage>> images;
	private LinkedList<OurImage> firstImages;
	private LinkedList<OurImage> secondImages;
	private LinkedList<Integer> messages;
	private LinkedList<Integer> messagestwo;
	
	public static final int MODE_IDLE = 0;
	public static final int MODE_MOVIE = 1;
	public static final int MODE_AUTO = 2;

	public static final int SYNC_OFF = 0;
	public static final int SYNC_ON = 1;
	public static final int SYNC_AUTO = 2;

	private int mode;
	private int sync;
	private int guiMode;
	private int guiSync;
	private int lastMotionIndex = -1;

	public Buffer() {
		images = new ArrayList<LinkedList<OurImage>>();
		firstImages = new LinkedList<OurImage>();
		secondImages = new LinkedList<OurImage>();
		images.add(firstImages);
		images.add(secondImages);
		messages = new LinkedList<Integer>();
		messagestwo = new LinkedList<Integer>();
		sync = SYNC_ON;
		mode = MODE_IDLE;
		guiMode = MODE_IDLE;
		guiSync = SYNC_ON;
	}

	public synchronized void add(OurImage image) {
		images.get(image.getID()).add(image);
		notifyAll();
	}

	public synchronized ArrayList<OurImage> getSyncedImages() {
		ArrayList<OurImage> list = new ArrayList<OurImage>();
		waitForImages();
		for(LinkedList<OurImage> q : images){
			list.add(q.poll());
		}
		return list;
	}

	public synchronized void waitForImages() {
		while (true) {
			boolean hasImages = true;
			for(LinkedList<OurImage> q : images){
				if(q.isEmpty()){
					hasImages = false;
				}
			}
			if (hasImages && images.size() > 0){
				break;
			}try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}


	public synchronized int getMode() {
		return mode;
	}

	public synchronized int getSync() {
		return sync;
	}

	public synchronized boolean allowSyncToggle() {
		return guiSync == SYNC_AUTO;
	}

	public synchronized void setMode(int mode, int sourceCamera) {
		if (this.mode == mode) {
			return;
		} else if (mode == MODE_MOVIE) {
			lastMotionIndex = sourceCamera;
			broadcastMessage(Codes.SET_MOVIE);
		} else if(mode == MODE_IDLE){
			lastMotionIndex = -1;
			broadcastMessage(Codes.SET_IDLE);
		}
		this.mode = mode;
		notifyAll();
	}

	public synchronized int getLastMotionIndex() {
		return lastMotionIndex;
	}

	public synchronized void setSync(int sync) {
		if (this.sync == sync)
			return;
		this.sync = sync;
	}

	public synchronized void setGuiMode(int mode) {
		if (this.guiMode == mode) {
			return;
		} else if (this.guiMode == MODE_AUTO) {
			broadcastMessage(Codes.SET_AUTO_OFF);
		}  if (mode == MODE_AUTO)
			broadcastMessage(Codes.SET_AUTO_ON);
		else {
			setMode(mode, -1);
		}
		this.guiMode = mode;
	}

	private synchronized void broadcastMessage(int message) {
		messages.offer(message);
		messagestwo.offer(message);
		notifyAll();
	}

	public synchronized void setGuiSync(int sync) {
		if (this.guiSync == sync)
			return;
		this.guiSync = sync;
		if (this.guiSync != SYNC_AUTO)
			this.sync = sync;

	}

	public synchronized LinkedList<Integer> getMessageList() {
		return messages;
	}
	public synchronized LinkedList<Integer> getMessageListtwo() {
		return messagestwo;
	}

	public synchronized OurImage getImageFromCamera(int index, long timeout) {
		long maxTime = System.currentTimeMillis() + timeout;
		while (images.get(index).isEmpty()
				&& System.currentTimeMillis() < maxTime) {
			try {
				wait(maxTime - System.currentTimeMillis());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return images.get(index).poll();
		
	}

}
