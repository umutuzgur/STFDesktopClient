package Controller;

import Service.ImageListener;
import Service.ImageServer;
import View.VNCScreen;

import java.awt.image.BufferedImage;

/**
 * Created by umutuzgur on 15/12/15.
 */
public class FrameUpdater implements ImageListener {
	ImageServer imageServer;
	VNCScreen vncScreen;
	public FrameUpdater(VNCScreen vncScreen) {
		this.vncScreen = vncScreen;
		imageServer = new ImageServer("127.0.0.1",1717);
		imageServer.setFrameListener(this);
		imageServer.start();

	}

	@Override
	public void onImage(BufferedImage image) {
		vncScreen.addFrame(image);
	}


}
