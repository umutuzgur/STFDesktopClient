package Service;

import Model.Wire;
import com.google.protobuf.CodedOutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by umutuzgur on 15/12/15.
 */
public class InputClient {
	private static InputClient inputClient;

	private CodedOutputStream codedOutputStream;

	public static InputClient getInstance() {
		if (inputClient == null) {
			inputClient = new InputClient();
		}
		return inputClient;
	}

	private InputClient() {
		try {
			Socket socket = new Socket("127.0.0.1", 1090);
			OutputStream outputStream = socket.getOutputStream();
			codedOutputStream = CodedOutputStream.newInstance(outputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void unlock() {
		Wire.KeyEventRequest keyEventRequest = Wire.KeyEventRequest.newBuilder().setEvent(Wire.KeyEvent.PRESS).setKeyCode(26).build();
		execute(keyEventRequest);
	}

	public void type(String text) {
		if (text.equals("67")) {
			delete();
		} else if (text.equals("26")) {
			unlock();
		} else {
			send(text);
		}
	}

	public void delete() {
		Wire.KeyEventRequest keyEventRequest = Wire.KeyEventRequest.newBuilder().setEvent(Wire.KeyEvent.PRESS).setKeyCode(67).build();
		execute(keyEventRequest);
	}


	public void execute(Wire.KeyEventRequest keyEventRequest) {
		Wire.Envelope envelope = Wire.Envelope.newBuilder().setType(Wire.MessageType.DO_KEYEVENT).setMessage(keyEventRequest.toByteString()).build();
		try {
			codedOutputStream.writeByteArrayNoTag(envelope.toByteArray());
			codedOutputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send(String text){
		try {
			Wire.DoTypeRequest doTypeRequest = Wire.DoTypeRequest.newBuilder().setText(text).build();
			Wire.Envelope envelope = Wire.Envelope.newBuilder().setType(Wire.MessageType.DO_TYPE).setMessage(doTypeRequest.toByteString()).build();
			codedOutputStream.writeByteArrayNoTag(envelope.toByteArray());
			codedOutputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
