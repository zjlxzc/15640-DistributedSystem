package server;

public class Stub {

}

import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import java.rmi.server.RemoteObject;
import java.rmi.server.RemoteRef;
import java.rmi.server.RemoteStub;

public final class CalculatorServant_Stub
  extends RemoteStub
  implements Calculator, Remote
{
  private static final long serialVersionUID = 2L;
  private static Method $method_add_0;
  
  static
  {
    try
    {
      $method_add_0 = Calculator.class.getMethod("add", new Class[] { Integer.TYPE, Integer.TYPE });
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      throw new NoSuchMethodError("stub class initialization failed");
    }
  }
  
  public CalculatorServant_Stub(RemoteRef paramRemoteRef)
  {
    super(paramRemoteRef);
  }
  
  public int add(int paramInt1, int paramInt2)
    throws RemoteException
  {
    try
    {
      Object localObject = this.ref.invoke(this, $method_add_0, new Object[] { new Integer(paramInt1), new Integer(paramInt2) }, -7734458262622125146L);
      return ((Integer)localObject).intValue();
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException;
    }
    catch (Exception localException)
    {
      throw new UnexpectedException("undeclared checked exception", localException);
    }
  }
}


/* Location:           C:\Users\zjlxz\Downloads\
 * Qualified Name:     CalculatorServant_Stub
 * JD-Core Version:    0.7.0.1
 */