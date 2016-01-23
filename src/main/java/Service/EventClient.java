package Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Created by umutuzgur on 15/12/15.
 */
public class EventClient {
	private static EventClient eventClient;
	private static final Logger logger = LogManager.getLogger();
	private OutputStream outputStream;

	public static EventClient getInstance() {
		if (eventClient == null) {
			eventClient = new EventClient();
		}
		return eventClient;
	}

	private EventClient() {
		try {
			Socket socket = new Socket("127.0.0.1", 1313);
			outputStream = socket.getOutputStream();
			InputStream inputStream = socket.getInputStream();
			readInitialInfo(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void readInitialInfo(final InputStream inputStream) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		new Thread(() -> {
			int data = 0;
			try {
				while ((data = inputStream.read()) != -1) {
					byteArrayOutputStream.write(data);
					if ((new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8).split("\n").length) == 3)
						break;
				}
				String str = new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
				byteArrayOutputStream.close();
				System.out.println(str);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	private void sendCommand(String command) {

		try {
			outputStream.write(command.getBytes(StandardCharsets.US_ASCII));
			outputStream.flush();
			System.out.println(command.trim());
		} catch (IOException e) {
			logger.error("Output stream was closed");
			e.printStackTrace();
		}
	}


	public void up() {
		sendCommand("u 0\n");
		sendCommand("c\n");
	}

	public void touch(int x, int y) {
		String template = "d 0 %d %d 50\n";
		sendCommand(String.format(template, 3 * x, 3 * y));
		sendCommand("c\n");
	}

	public void move(int x, int y) {
		String template = "m 0 %d %d 50\n";
		sendCommand(String.format(template, 3 * x, 3 * y));
		sendCommand("c\n");
	}

}
