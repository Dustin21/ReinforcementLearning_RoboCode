package projects.nnet;

import java.util.ArrayList;
import java.util.Random;

public class Neuron {
	
	/**
	 * Neuron class: 
	 * builds each neuron and layer individually and randomly initializes 
	 * weights by a uniform distribution. Sigmoid and the derivative of
	 * the sigmoid are also computed here to be used in the training class.
	 */
	
	ArrayList <Double> weights;
	ArrayList <Double> inputs;
	double bias;
	int neuronIsInput;
	int nNeuronInputs;
	double a;
	double b;
	double in;
	double out;
	ArrayList <Double> previousDeltaW;
	double previousDeltaB;		

	
	public Neuron (int arg_nNeuronInputs, double arg_Bias, int isInput, double arg_A, double arg_B)
	{
		// initialize variables
		a = arg_A;
		b = arg_B;
		weights = new ArrayList<Double>();
		bias = arg_Bias;
		in = 0;
		out = 0;
		nNeuronInputs = arg_nNeuronInputs;
		previousDeltaW = new ArrayList <Double>();
		previousDeltaB = 0;
		
		// initialize weights for input layer
		int i = 0;
		for(i = 0; i < nNeuronInputs; i++)
		{
			Random r = new Random();
			weights.add(0.0);
			weights.set(i, r.nextDouble() - 0.5);
			previousDeltaW.add(0.0);
		}
		
		// initialize bias for neuron
		Random r = new Random();
		bias = r.nextDouble() - 0.5;
		
		// initialize empty neurons
		inputs = new ArrayList<Double>();
		for(i =0; i < nNeuronInputs; i++)
		{
			inputs.add(0.0);
		}
		
		neuronIsInput = isInput;
	}
	
	public double getSigmoidOutput ()
	{
		// check that neuron is an input layer neuron
		if(neuronIsInput == 1)
		{
			return inputs.get(0);
		}
		
		// dot product of weight and input vector
		double sum = 0;
		int i = 0;
		for(i = 0; i < nNeuronInputs; i++)
		{
			sum += weights.get(i) * inputs.get(i);
		}
		
		// add the bias
		sum += bias;
		
		// compute sigmoid output:
		// 1/(1 + e^-(bias + w %*% x))
		in = sum;
		out = (b - a) / (1 + Math.exp(-sum)) + a; 
		
		return out;
	}
	
	public double getSigmoidDerivativeOutput ()
	{
		// compute the sigmoid derivative output:
		// (b - a) * e^-(b + w %*% x)/(1 + e^-(b + w %*% x))^2
		return (b - a) * Math.exp(in) / Math.pow((1 + Math.exp(in)), 2);
				
	}

}
