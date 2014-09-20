package connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class RemoteConnection {

	protected ObjectInputStream inStream;
	protected ObjectOutputStream outStream;
	protected Socket socket;
	
	public RemoteConnection() {
	}
	
	public RemoteConnection(String ipAddr, int port) throws UnknownHostException, IOException {
		socket = new Socket(ipAddr, port);
		outStream = new ObjectOutputStream(socket.getOutputStream());
		inStream = new ObjectInputStream(socket.getInputStream());
	}

	public void marshalling(Class<?> clas, Object obj,
			ObjectOutputStream outStream) throws IOException {
		if(!clas.isPrimitive()) {
			outStream.writeObject(obj);
		} else {
			if (clas == int.class) {
				outStream.writeInt(((Integer) obj).intValue());
			} else if (clas == long.class) {
				outStream.writeLong(((Long) obj).longValue());
			} else if (clas == float.class) {
				outStream.writeFloat(((Float) obj).floatValue());
			} else if (clas == double.class) {
				outStream.writeDouble(((Double) obj).doubleValue());
			} else if (clas == short.class) {
				outStream.writeShort(((Short) obj).shortValue());
			} else if (clas == char.class) {
				outStream.writeChar(((Character) obj).charValue());
			} else if (clas == byte.class) {
				outStream.writeByte(((Byte) obj).byteValue());
			} else if (clas == boolean.class) {
				outStream.writeBoolean(((Boolean) obj).booleanValue());
			} else {
				System.out.println("Class is not valid " + clas);
			}
		}
	}
	
	public Object unmarshalling(Class<?> clas, ObjectInputStream inStream)
			throws IOException, ClassNotFoundException {
		if(!clas.isPrimitive()) {
			return inStream.readObject();
		} else {
			if (clas == int.class) {
				return Integer.valueOf(inStream.readInt());
			} else if (clas == long.class) {
				return Long.valueOf(inStream.readLong());
			} else if (clas == float.class) {
				return Float.valueOf(inStream.readFloat());
			} else if (clas == double.class) {
				return Double.valueOf(inStream.readDouble());
			} else if (clas == short.class) {
				return Short.valueOf(inStream.readShort());
			} else if (clas == char.class) {
				return Character.valueOf(inStream.readChar());
			} else if (clas == byte.class) {
				return Byte.valueOf(inStream.readByte());
			} else if (clas == boolean.class) {
				return Boolean.valueOf(inStream.readBoolean());
			} else {
				System.out.println("Class is not valid " + clas);
				return null;
			}
		}
	}
	
	public void close() throws IOException {
		inStream.close();
		outStream.close();
		socket.close();
	}
}
