package Service;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by umutuzgur on 14/12/15.
 */
public class DeviceChecker {

	private AndroidDebugBridge adb;
	private static IDevice device;
	private static final Logger logger = LogManager.getLogger();

	public DeviceChecker() {
		AndroidDebugBridge.init(true);
		setPortForwardings();
		startComponents();
		startAdbThread();

		ThreadUtil.sleep(4000);

	}

	private void startAdbThread() {

		ThreadUtil.sleep(3000);
		adb = AndroidDebugBridge.createBridge(System.getenv("ANDROID_HOME") + "platform-tools/adb", true);
		Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder()
				.setNameFormat("Connection checker").build()).scheduleAtFixedRate(() -> {
			try {
				IDevice[] devices = adb.getDevices();
				if (devices.length != 0 && devices[0].getState() == IDevice.DeviceState.ONLINE) {
					device = devices[0];
				} else {
					logger.error("Device lost");
					device = null;
					adb.createBridge();
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}, 0, 5, TimeUnit.SECONDS);

	}

	private void startComponents() {
		try {
			Runtime.getRuntime().exec("sh minicap/run.sh -P 1080x1920@360x640/0");
			Runtime.getRuntime().exec("sh minitouch/run.sh");
			Runtime.getRuntime().exec("sh service/run.sh");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setPortForwardings() {
		try {
			Runtime.getRuntime().exec("adb forward tcp:1717 localabstract:minicap");
			Runtime.getRuntime().exec("adb forward tcp:1313 localabstract:minitouch");
			Runtime.getRuntime().exec("adb forward tcp:1090 tcp:1090");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static IDevice getDevice() {
		return device;
	}

}
