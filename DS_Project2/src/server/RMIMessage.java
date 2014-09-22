package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.UnknownHostException;
import java.util.HashMap;

import connection.RemoteConnection;

public class RMIMessage extends RemoteConnection {
	private RemoteObjectRef ref;
	private Class<?>[] types;
	private Object[] parameters;
	
	private Object[] values;
	private Object result;
	
	public RMIMessage() {
	}
	
	public RMIMessage(Object result) {
		this.result = result;
	}
	
	public RMIMessage(String ipAddr, int port) throws UnknownHostException, IOException {
		super(ipAddr, port);
	}

	public RMIMessage(RemoteObjectRef ref, Method m, Object[] parameters) throws IOException {
		this.ref = ref;
		this.types = m.getParameterTypes();;
		this.parameters = parameters;
		values = new Object[parameters.length];
		
		marshalling();
		outStream.writeObject(this);
		outStream.flush();
	}
	
	public void marshalling() throws IOException {
		for(int i = 0; i < parameters.length; i++){
			if(!types[i].isPrimitive()) {
				values[i] = parameters[i];
			} else {
				if (types[i] == int.class) {
					values[i] = ((Integer) parameters[i]).intValue();
				} else if (types[i] == long.class) {
					values[i] = ((Long) parameters[i]).longValue();
				} else if (types[i] == float.class) {
					values[i] = ((Float) parameters[i]).floatValue();
				} else if (types[i] == double.class) {
					values[i] = ((Double) parameters[i]).doubleValue();
				} else if (types[i] == short.class) {
					values[i] = ((Short) parameters[i]).shortValue();
				} else if (types[i] == char.class) {
					values[i] = ((Character) parameters[i]).charValue();
				} else if (types[i] == byte.class) {
					values[i] = ((Byte) parameters[i]).byteValue();
				} else if (types[i] == boolean.class) {
					values[i] = ((Boolean) parameters[i]).booleanValue();
				} else {
					System.out.println("Class is not valid " + types[i]);
				}
			}
        }
	}

	/*
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
	*/
	
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
	
	public Object getReturnValue(Method m) throws IOException, ClassNotFoundException {
		Class<?> returnType = m.getReturnType();
		if (returnType == void.class) {
			return null;
		}
		
		result = unmarshalling(returnType, inStream);
		return this;
		
	}
	
}
