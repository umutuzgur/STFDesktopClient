package Service;

import View.VNCScreen;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;

/**
 * Created by umutuzgur on 14/12/15.
 */
public class ImageServer implements Closeable {

	private ImageListener listener;
	private String host;
	private int port;

	public ImageServer(String host, int port) {
		this.host = host;
		this.port = port;

	}

	public void start() {
		Socket socket = null;
		InputStream inputStream = null;
		try {
			socket = new Socket(host, port);
			inputStream = socket.getInputStream();

		} catch (IOException e) {
			e.printStackTrace();
		}

		final InputStream finalInputStream = inputStream;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try (InputStream inputStream = finalInputStream) {

					retrieveGlobalHeaders(inputStream);
					int chunk;
					ByteArrayOutputStream imageBuffer = new ByteArrayOutputStream();
					int imageBodyCursor = 0;
					int frameLength = 0;
					while ((chunk = inputStream.read()) != -1) {
						ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
						arrayOutputStream.write(chunk);
						byte[] data = arrayOutputStream.toByteArray();

						for (int i = 0; i < data.length; i++) {
							if (imageBodyCursor < 4) {
								frameLength += ((data[i] & 0xFF) << (imageBodyCursor * 8)) >>> 0;
								imageBodyCursor++;
							} else {
								if (data.length - i >= frameLength) {
									imageBuffer.write(Arrays.copyOfRange(data, i, i + frameLength));
									final byte[] imageBytes = imageBuffer.toByteArray();
									if (VNCScreen.isFrameAdded()) {
										listener.onImage(ImageIO.read(new ByteArrayInputStream(imageBytes)));
									}
									i += frameLength;
									frameLength = imageBodyCursor = 0;
									imageBuffer.reset();
								} else {
									imageBuffer.write(Arrays.copyOfRange(data, i, data.length));
									frameLength -= data.length - i;
									imageBodyCursor += data.length - i;
									i = data.length;
								}
							}

						}
						arrayOutputStream.close();
					}

				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}).start();
	}

	private void retrieveGlobalHeaders(InputStream inputStream) throws IOException {
		byte[] headers = new byte[24];
		for (int i = 0; i < 24; i++) {
			headers[i] = 0;
			byte[] temp = new byte[1];
			inputStream.read(temp);
			headers[i] = temp[0];
		}

		int width = (int) byteArrayToInt(Arrays.copyOfRange(headers, 14, 18));
		int height = (int) byteArrayToInt(Arrays.copyOfRange(headers, 18, 22));
		System.out.println(width + "x" + height);
	}

	public void setFrameListener(ImageListener imageListener) {
		this.listener = imageListener;
	}

	@Override
	public void close() throws IOException {

	}

	static long byteArrayToInt(byte[] bytes) {
		return ((bytes[3] << 24) | (bytes[2] & 0xFF) << 16 | (bytes[1] & 0xFF) << 8 | (bytes[0] & 0xFF)) & 0xffffffffL;
	}

}
