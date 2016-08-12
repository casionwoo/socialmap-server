package ssomap.hotQueue;

import java.util.ArrayList;

import ssomap.Photos.Photos;


public class HotQueue {

	private static ArrayList<HotQueueItem> queue = new ArrayList<HotQueueItem>();
	private StringBuilder outer = new StringBuilder();
	
	public void push(Photos photo){
		queue.add(new HotQueueItem(photo));
	}

	public static ArrayList<HotQueueItem> getQueue() {
		return queue;
	}

	public static void setQueue(ArrayList<HotQueueItem> queue) {
		HotQueue.queue = queue;
	}

	public StringBuilder getOuter() {
		return outer;
	}

	public void setOuter(StringBuilder outer) {
		this.outer = outer;
	}
	
	public void push(HotQueueItem item){
		queue.add(item);
	}
}


