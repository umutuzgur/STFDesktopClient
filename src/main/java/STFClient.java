import Controller.FrameUpdater;
import Service.DeviceChecker;
import View.VNCScreen;

import java.awt.*;

/**
 * Created by umutuzgur on 14/12/15.
 */
public class STFClient {

	public static void main(String[] args) {
		DeviceChecker deviceChecker = new DeviceChecker();
		VNCScreen vncScreen = new VNCScreen(new Dimension(360,680));
		FrameUpdater frameUpdater = new FrameUpdater(vncScreen);
	}
}
