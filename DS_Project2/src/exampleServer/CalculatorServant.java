package exampleServer;
// CalculatorServant.java        
// A Remote object class that implements Calculator. 
public class CalculatorServant implements Calculator {
       public CalculatorServant() {
       }
       public int add(int x, int y) {
            System.out.println("CalculatorServant : Got request to add " + x + " and " + y);
            return x + y;
    }
}