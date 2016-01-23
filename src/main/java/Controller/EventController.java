package Controller;

import Service.EventClient;
import Service.InputClient;
import Service.ThreadUtil;

import java.awt.event.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by umutuzgur on 15/12/15.
 */
public class EventController implements KeyListener, MouseListener, MouseMotionListener {

	EventClient eventClient;
	InputClient inputClient;
	BlockingQueue<String> messages = new LinkedBlockingDeque<>();
	private final Object lock = new Object();

	public EventController() {
		eventClient = EventClient.getInstance();
		inputClient = InputClient.getInstance();
		new Thread(new Runnable() {
			@Override public void run() {
				while (true) {
					String message = "";
					String command;
					while ((command = messages.poll()) != null) {
						if (isSpecialKey(command)) {
							type(message);
							type(command);
							message = "";
							continue;
						}
						message += command;
						ThreadUtil.sleep(100);

					}
					if (message == null || message.equals("")) {
						try {
							synchronized (lock) {
								lock.wait();
								continue;
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					type(message);
				}
			}
		}).start();

	}

	public void touch(int x, int y) {
		eventClient.touch(x, y);
	}

	public void delete() {
		addToMessages("67");
	}

	public void type(String text) {
		inputClient.type(text);
	}

	public void unlock() {
		messages.add("26");
		synchronized (lock) {
			lock.notify();
		}
	}

	private boolean isSpecialKey(String key) {
		return key.equals("67") || key.equals("26");
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getExtendedKeyCode() == KeyEvent.VK_BACK_SPACE) {
			delete();
		} else if (e.getExtendedKeyCode() == KeyEvent.VK_UP) {
			addToMessages("");
		} else if (e.getExtendedKeyCode() == KeyEvent.VK_DOWN) {
			addToMessages("");

		} else if (e.getExtendedKeyCode() == KeyEvent.VK_RIGHT) {
			addToMessages("");

		} else if (e.getExtendedKeyCode() == KeyEvent.VK_LEFT) {
			addToMessages("");

		} else {
			addToMessages(String.valueOf(e.getKeyChar()));
		}
	}

	private void addToMessages(String message) {
		messages.add(message);
		synchronized (lock) {
			lock.notify();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	private long prevClickTimeStamp = 0;
	private int xPrev;
	private int yPrev;

	@Override
	public void mousePressed(MouseEvent e) {
		xPrev = e.getX();
		yPrev = e.getY();
		touch(e.getX(), e.getY());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		eventClient.up();
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (e.getWhen() - prevClickTimeStamp < 100) {
			return;
		}
		if (Math.sqrt(Math.pow(xPrev - e.getX(), 2) + Math.pow(yPrev - e.getY(), 2)) < 5.0)
			return;
		xPrev = e.getX();
		yPrev = e.getY();
		prevClickTimeStamp = e.getWhen();
		move(e.getX(), e.getY());

	}

	private void move(int x, int y) {
		eventClient.move(x, y);
	}

	@Override public void mouseMoved(MouseEvent e) {

	}

}
