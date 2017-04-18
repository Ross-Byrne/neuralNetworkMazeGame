package ie.gmit.sw.ai.neuralNetwork.activator;

public class ActivatorFactory {
	private static ActivatorFactory fact = new ActivatorFactory();
	
	private ActivatorFactory(){
	}
	
	public static ActivatorFactory getInstance(){
		return fact;
	}
	
	public Activator getActivator(Activator.ActivationFunction function){
		if (function == Activator.ActivationFunction.HyperbolicTangent){
			return new HyperbolicTangentActivator();			
		}else{
			return new SigmoidActivator();		
		}		
	}
}