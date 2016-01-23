package View;

import Controller.EventController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by umutuzgur on 15/12/15.
 */
public class VNCScreen implements Closeable {
	private static AtomicBoolean frameLock = new AtomicBoolean(false);
	private AtomicBoolean stopVideo = new AtomicBoolean(false);
	JPanel imagePanel = new JPanel();
	JButton unlockButon = new JButton("Unlock");
	private EventController eventController = new EventController();
	private static final Logger logger = LogManager.getLogger();

	public VNCScreen(Dimension dimension) {
		JFrame frame = new JFrame();
		frame.setSize(dimension);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		imagePanel = new JPanel();
		frame.add(imagePanel, BorderLayout.CENTER);
		imagePanel.setLayout(new BorderLayout());
		unlockButon.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				eventController.unlock();
			}
		});
		unlockButon.setFocusable(false);
		frame.getContentPane().add(unlockButon, BorderLayout.SOUTH);
		imagePanel.addKeyListener(eventController);
		imagePanel.addMouseListener(eventController);
		imagePanel.addMouseMotionListener(eventController);
		imagePanel.setFocusable(true);
		imagePanel.requestFocusInWindow();
		frame.setVisible(true);
	}

	public void addFrame(BufferedImage image) {
		frameLock.set(true);
		SwingUtilities.invokeLater(() -> {
					imagePanel.getGraphics().drawImage(image, 0, 0, null);
					image.flush();
					logger.info("Frame set");
					frameLock.set(false);
				}
		);
	}

	public static boolean isFrameAdded() {
		return !frameLock.get();
	}

	@Override
	public void close() throws IOException {
		stopVideo.set(true);
	}

}
