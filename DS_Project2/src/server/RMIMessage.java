package server;

import java.io.IOException;
import java.io.Serializable;

import remote.RemoteObjectRef;

public class RMIMessage implements Serializable{

	private static final long serialVersionUID = 1L;
	private RemoteObjectRef ref;
	
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

	public RMIMessage(RemoteObjectRef ref, String methodName, Class<?>[] types,
				Object[] parameters) throws IOException {
		this.ref = ref;
		//this.method = m;
		this.parameters = parameters;
		this.methodName = methodName;
		
		this.types = types;
		values = new Object[parameters.length];
		
		marshalling();	
		System.out.println("RMIMessage : sent the message");		
	}
	
	public void marshalling() throws IOException {
		System.out.println("RMIMessage : marshalling...");
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
	
	public Object unmarshalling(Class<?> clas, RMIMessage resultMessage)
			throws IOException, ClassNotFoundException {
		System.out.println("RMIMessage : unmarshalling...");
		System.out.println();
		
		if(!clas.isPrimitive()) {
			return result;
		} else {
			if (clas == int.class) {
				return Integer.valueOf(resultMessage.getResult().toString());
			} else if (clas == long.class) {
				return Long.valueOf(resultMessage.getResult().toString());
			} else if (clas == float.class) {
				return Float.valueOf(resultMessage.getResult().toString());
			} else if (clas == double.class) {
				return Double.valueOf(resultMessage.getResult().toString());
			} else if (clas == short.class) {
				return Short.valueOf(resultMessage.getResult().toString());
			} else if (clas == char.class) {
				return Character.valueOf(resultMessage.getResult().toString().charAt(0));
			} else if (clas == byte.class) {
				return Byte.valueOf(resultMessage.getResult().toString());
			} else if (clas == boolean.class) {
				return Boolean.valueOf(resultMessage.getResult().toString());
			} else {
				System.out.println("Class is not valid " + clas);
				return null;
			}
		}	
	}
	
	public Object getResultValue(Class<?> returnType, RMIMessage resultMessage) throws IOException, ClassNotFoundException {
		//Class<?> returnType = .getReturnType();
		if (returnType == void.class) {
			return null;
		}		
		System.out.println("RMIMessage : get the return value");
		result = unmarshalling(returnType, resultMessage);
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
