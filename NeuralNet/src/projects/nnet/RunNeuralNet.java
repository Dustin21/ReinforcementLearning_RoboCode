package projects.nnet;

public class RunNeuralNet {
	
	public static void main(String args[])
	{
		int i;
		for(i = 0; i < 1; i++)
		{
			NNet net = new NNet(9, 8, 3000, 0.009, 0.99, -1, 1, false);
			net.readCSV();
			net.dataBinaryIn();
			//net.dataBipolarIn();
			net.train();
			net.saveWeights("weights.csv");
			net.loadWeights("weights.csv");
		}
	}

}
