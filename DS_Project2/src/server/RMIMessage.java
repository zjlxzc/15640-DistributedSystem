package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.UnknownHostException;

import connection.RemoteConnection;

public class RMIMessage extends RemoteConnection implements Serializable{
	private RemoteObjectRef ref;
	//private Method method;
	private String methodName;
	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

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

	public void sendOut(RemoteObjectRef ref, String methodName, Class<?>[] types,
				Object[] parameters) throws IOException {
		this.ref = ref;
		//this.method = m;
		this.parameters = parameters;
		this.methodName = methodName;
		
		this.types = types;
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
	
	public Object unmarshalling(Class<?> clas, ObjectInputStream inStream)
			throws IOException, ClassNotFoundException {
		
		if(!clas.isPrimitive()) {
			return inStream.readObject();
		} else {
			if (clas == int.class) {
				return Integer.valueOf(((RMIMessage)inStream.readObject()).getResult().toString());
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
	
	public Object getResultValue(Class<?> returnType) throws IOException, ClassNotFoundException {
		//Class<?> returnType = .getReturnType();
		if (returnType == void.class) {
			return null;
		}		
		
		result = unmarshalling(returnType, inStream);
		return result;
	}

	public RemoteObjectRef getRef() {
		return ref;
	}

	public void setRef(RemoteObjectRef ref) {
		this.ref = ref;
	}

	public Class<?>[] getTypes() {
		return types;
	}

	public void setTypes(Class<?>[] types) {
		this.types = types;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}

	public Object[] getValues() {
		return values;
	}

	public void setValues(Object[] values) {
		this.values = values;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

}
