package nnet;

public class DataIO {
	
	double[][] trainInputs;
	double[] trainOutput;
	
	public void dataBipolarIn()
	{
		trainInputs[0][0]   =  -1;
	    trainInputs[0][1]   = -1;
	    trainOutput[0]      =  -1;

	    trainInputs[1][0]   = 1;
	    trainInputs[1][1]   = - 1;
	    trainOutput[1]      =  1;
	    
	    trainInputs[2][0]   = -1;
	    trainInputs[2][1]   = 1;
	    trainOutput[2]     =  1;
	    
	    trainInputs[3][0]   = 1;
	    trainInputs[3][1]   = 1;
	    trainOutput[3]      =  -1;
	}
	
	public void dataBinaryIn()
	{
		trainInputs[0][0]   =  0;
	    trainInputs[0][1]   = 0;
	    trainOutput[0]      =  0;

	    trainInputs[1][0]   = 1;
	    trainInputs[1][1]   = 0;
	    trainOutput[1]      =  1;
	    
	    trainInputs[2][0]   = 0;
	    trainInputs[2][1]   = 1;
	    trainOutput[2]      =  1;
	    
	    trainInputs[3][0]   = 1;
	    trainInputs[3][1]   = 1;
	    trainOutput[3]      =  0;
	}
	
	

}
