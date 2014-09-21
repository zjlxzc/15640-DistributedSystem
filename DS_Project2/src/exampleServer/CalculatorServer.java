package exampleServer;

import registry.LocateRegistry;
import registry.Registry;
import server.RemoteObjectRef;

public class CalculatorServer {
	public static void main(String args[]){
          System.out.println("Calculator Server Running");
          try{
            // create the servant
            Calculator c = new CalculatorServant();
            System.out.println("Created Calculator object");
            System.out.println("Placing in registry");
            // publish to registry
	   
            //OLD USAGE: Naming.rebind("CoolCalculator", c); 
            Registry registry = LocateRegistry.getRegistry(args[0], Integer.parseInt(args[1]));
            registry.bind("CoolCalculator", (RemoteObjectRef)c);
            
            System.out.println("CalculatorServant object ready");
           }catch(Exception e) {
            System.out.println("CalculatorServer error main " + e.getMessage());
        }
    }
}