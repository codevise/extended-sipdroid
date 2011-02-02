package org.sipdroid.media;

public class RingBuffer {

	private short[] buffer;
	
	private int head, tail;
	
	public RingBuffer(int size) {
		head = 0;
		tail = 0;
		buffer = new short[size];
	}
	
	public synchronized void enqueue(short[] audioSnippet) {
		int snippetSize = audioSnippet.length;
		
		for (int i = 0; i < snippetSize; i++) {
			head = (head + 1) % buffer.length;
			buffer[head] = audioSnippet[i];
			if (head == tail) {
				tail = (tail + 1) % buffer.length;
			}
		}
	}

	public synchronized short[] dequeue(int snippetSize) {
		short[] audioSnippet = new short[snippetSize];
		
		for (int i = 0; i < snippetSize; i++) {
			tail = (tail + 1) % buffer.length;
			audioSnippet[i] = buffer[tail];
			if (tail == head) {
			}
		}
		return audioSnippet;
	}
	
	public int getFreeSlots() {
		int slots;
		if (head >= tail) {
			slots = buffer.length - head + tail;
		} else {
			slots = tail - head;
		}
		return slots;
	}
}
