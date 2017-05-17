package projects.nnet;


import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

import projects.nnet.DataIO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class NNet extends DataIO {
	
	ArrayList<Neuron> net;
	ArrayList <Double> totalError;
	ArrayList <Double> errorList;
	int nInputs;
	int nHidden;
	int nEpochs;
	double learnRate;
	double momentum;
	double a;
	double b;
    static double[][] data;
	static int numRows = 0;


	
	public NNet(int arg_nInputs, int arg_nHidden, int arg_nEpochs, 
			double arg_learnRate, double arg_momentum, double arg_A, double arg_B, boolean loadFile)
	{
		trainInputs = new double[4][2];
		trainOutput = new double[4];
		nInputs = arg_nInputs;
		nHidden = arg_nHidden;
		nEpochs = arg_nEpochs;
		learnRate = arg_learnRate;
		momentum = arg_momentum;
		a = arg_A;
		b = arg_B;
		net = new ArrayList <Neuron>();
		totalError = new ArrayList <Double>();
		errorList = new ArrayList <Double>();
		
		int i;
		
		// input layer
		for(i = 0; i < nInputs; i++)
		{
			Neuron neuron = new Neuron(1, 0, 1, a, b);
			net.add(neuron);
		}
		
		// hidden layer
		for(i = 0; i < nHidden; i++)
		{
			Neuron neuron = new Neuron(arg_nInputs, 1.0, 0, a, b);
			net.add(neuron);
		}
		
		// output layer
		Neuron neuron = new Neuron(arg_nHidden, 1.0, 0, a, b);
		net.add(neuron);
		
		if(loadFile == true)
		{
			loadWeights("/Users/KinZ/Documents/java_directory/NeuralNet/weights.csv");
		}
		
	}
	
	// feed forward
	public void feedForward(int index)
	{
		int i;
		for(i = 0; i < nInputs; i++)
		{
			net.get(i).out = data[index][i];
		}
		
		for(i = 0; i < nHidden; i++)
		{
			net.get(nInputs + i).inputs.set(0, data[index][0]);
			net.get(nInputs + i).inputs.set(1, data[index][1]);
			
			net.get(nInputs + i).getSigmoidOutput();
		}
				
		net.get(nHidden + nInputs).inputs.set(0,net.get(nInputs).out);
		net.get(nHidden + nInputs).inputs.set(1,net.get(nInputs + 1).out);
		net.get(nHidden + nInputs).inputs.set(2,net.get(nInputs + 2).out);
		net.get(nHidden + nInputs).inputs.set(3,net.get(nInputs + 3).out);
		
		net.get(nHidden + nInputs).getSigmoidOutput();
				
	}
	
	// feed forward
		public double feedForward2(double[] in)
		{			
			int i;
			for(i = 0; i < nInputs; i++)
			{
				net.get(i).out = in[i];
			}
			
			for(i = 0; i < nHidden; i++)
			{
				net.get(nInputs + i).inputs.set(0, in[0]);
				net.get(nInputs + i).inputs.set(1, in[1]);
				net.get(nInputs + i).inputs.set(2, in[2]);
				net.get(nInputs + i).inputs.set(3, in[3]);
				net.get(nInputs + i).inputs.set(4, in[4]);
				net.get(nInputs + i).inputs.set(5, in[5]);
				net.get(nInputs + i).inputs.set(6, in[6]);
				net.get(nInputs + i).inputs.set(7, in[7]);
				net.get(nInputs + i).inputs.set(7, in[8]);

				
				net.get(nInputs + i).getSigmoidOutput();
			}
					
			net.get(nHidden + nInputs).inputs.set(0,net.get(nInputs).out);
			net.get(nHidden + nInputs).inputs.set(1,net.get(nInputs + 1).out);
			net.get(nHidden + nInputs).inputs.set(2,net.get(nInputs + 2).out);
			net.get(nHidden + nInputs).inputs.set(3,net.get(nInputs + 3).out);
			net.get(nHidden + nInputs).inputs.set(3,net.get(nInputs + 4).out);
			net.get(nHidden + nInputs).inputs.set(3,net.get(nInputs + 5).out);
			net.get(nHidden + nInputs).inputs.set(3,net.get(nInputs + 6).out);
			net.get(nHidden + nInputs).inputs.set(3,net.get(nInputs + 7).out);
			
			net.get(nHidden + nInputs).getSigmoidOutput();
			
			return net.get(nHidden + nInputs).out;
					
		}
		
		
	public void train2(double errin, double [] in)
	{
		int i, j, k, l;
		double tempval=0;
		
		for(i = 0; i < nEpochs; i++)
		{
			//double [] temperrors = new double[numRows];
			for(j = 0; j < 1; j++)
			{
				feedForward2(in);
				
				double error = errin - net.get(nInputs + nHidden).out;
				//System.out.format("Epoch %d Error %f\n", i, error);
				double sigmoidDerivative = net.get(nInputs + nHidden).getSigmoidDerivativeOutput();
				
				
				double u, deltak=0, g, t, ui, sigmoidDerivativeHidden, ow, deltaj=0;
				for(k = 0; k < nHidden; k++)
				{
					u = net.get(nInputs + k).out;
					deltak = error * sigmoidDerivative;
					g = deltak * u;
					
					
					t = net.get(nInputs + nHidden).weights.get(k);
					
					tempval = learnRate * g + momentum * net.get(nInputs + nHidden).previousDeltaW.get(k);
					net.get(nInputs + nHidden).weights.set(k, t + tempval);
					
					net.get(nInputs + nHidden).previousDeltaW.set(k, tempval);
					
				}
				tempval = learnRate*deltak + momentum * net.get(nInputs + nHidden).previousDeltaB;
				t =  net.get(nInputs + nHidden).bias;
				net.get(nInputs + nHidden).bias = t + tempval;
				net.get(nInputs + nHidden).previousDeltaB = tempval;
				
				sigmoidDerivative = net.get(nInputs + nHidden).getSigmoidDerivativeOutput();
				
				for(k = 0; k < nHidden; k++)
				{
					for(l = 0; l < nInputs; l++)
					{
						ui = net.get(l).out;
						sigmoidDerivativeHidden = net.get(nInputs + k).getSigmoidDerivativeOutput();
						ow = net.get(nInputs + nHidden).weights.get(k);
						deltak = error * sigmoidDerivative;
						deltaj = deltak * sigmoidDerivativeHidden * ow;
						g =  ui * deltaj;
						
						
						tempval = learnRate * g + momentum* net.get(nInputs + k).previousDeltaW.get(l);
						t = net.get(nInputs + k).weights.get(l);
						
						net.get(nInputs + k).weights.set(l, t + tempval);
						
						net.get(nInputs + k).previousDeltaW.set(l,tempval);
					}
					
					tempval = learnRate * deltaj + momentum * net.get(nInputs + k).previousDeltaB;
					t = net.get(nInputs + k).bias;
					net.get(nInputs + k).bias = t + tempval;
					net.get(nInputs + k).previousDeltaB = tempval;
					
				}
				
				//temperrors[j] = error;
			}
			
			/*double totalerror=0;
			for(j = 0; j < 1; j++)
			{
				totalerror += Math.pow(temperrors[j],2);				
			}

			totalerror *= 0.5;
			totalError.add(totalerror);
			writeToFile("results/nnetResultsOutput.csv", i, totalerror);
			
			errorList.add(totalerror);*/

		}
		
	}
		
	
	public void train()
	{
		
		int i, j, k, l;
		double tempval=0;
		
		for(i = 0; i < nEpochs; i++)
		{
			double [] temperrors = new double[numRows];
			for(j = 0; j < numRows; j++)
			{
				feedForward(j);
				
				double error = data[j][9] - net.get(nInputs + nHidden).out;
				//System.out.format("Epoch %d Error %f\n", i, error);
				double sigmoidDerivative = net.get(nInputs + nHidden).getSigmoidDerivativeOutput();
				
				
				double u, deltak=0, g, t, ui, sigmoidDerivativeHidden, ow, deltaj=0;
				for(k = 0; k < nHidden; k++)
				{
					u = net.get(nInputs + k).out;
					deltak = error * sigmoidDerivative;
					g = deltak * u;
					
					
					t = net.get(nInputs + nHidden).weights.get(k);
					
					tempval = learnRate * g + momentum * net.get(nInputs + nHidden).previousDeltaW.get(k);
					net.get(nInputs + nHidden).weights.set(k, t + tempval);
					
					net.get(nInputs + nHidden).previousDeltaW.set(k, tempval);
					
				}
				tempval = learnRate*deltak + momentum * net.get(nInputs + nHidden).previousDeltaB;
				t =  net.get(nInputs + nHidden).bias;
				net.get(nInputs + nHidden).bias = t + tempval;
				net.get(nInputs + nHidden).previousDeltaB = tempval;
				
				sigmoidDerivative = net.get(nInputs + nHidden).getSigmoidDerivativeOutput();
				
				for(k = 0; k < nHidden; k++)
				{
					for(l = 0; l < nInputs; l++)
					{
						ui = net.get(l).out;
						sigmoidDerivativeHidden = net.get(nInputs + k).getSigmoidDerivativeOutput();
						ow = net.get(nInputs + nHidden).weights.get(k);
						deltak = error * sigmoidDerivative;
						deltaj = deltak * sigmoidDerivativeHidden * ow;
						g =  ui * deltaj;
						
						
						tempval = learnRate * g + momentum* net.get(nInputs + k).previousDeltaW.get(l);
						t = net.get(nInputs + k).weights.get(l);
						
						net.get(nInputs + k).weights.set(l, t + tempval);
						
						net.get(nInputs + k).previousDeltaW.set(l,tempval);
					}
					
					tempval = learnRate * deltaj + momentum * net.get(nInputs + k).previousDeltaB;
					t = net.get(nInputs + k).bias;
					net.get(nInputs + k).bias = t + tempval;
					net.get(nInputs + k).previousDeltaB = tempval;
					
				}
				
				temperrors[j] = error;
			}
			
			double totalerror=0;
			for(j = 0; j < numRows; j++)
			{
				totalerror += Math.pow(temperrors[j],2);				
			}

			totalerror *= 0.5;
			totalError.add(totalerror);
			writeToFile("results/nnetResultsOutput.csv", i, totalerror);
			
			errorList.add(totalerror);

		}
		
		/*int index=0;
		for(j = errorList.size() - 1; j >= 0; j--)
		{
			if(errorList.get(j) >= 0.05)
			{
				index = j;
				break;
			}
		}
		writeToFile("results/optimalEpoch.csv", 0, (double) index);*/

		
	}
	
	
	public static void readCSV() {

        String csvFile = "/Users/KinZ/Directory/EECE_592/robocode/qTableOut_Normalized.csv";
        String line = "";
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
        	
            while ((line = br.readLine()) != null) {

               numRows++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
                
        data = new double[numRows][10];
        int index = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
        	
            while ((line = br.readLine()) != null) {

            	 String[] rowData = line.split(cvsSplitBy);
            	 
            	 data[index][0] = Float.parseFloat(rowData[0]);
            	 data[index][1] = Float.parseFloat(rowData[1]);
            	 data[index][2] = Float.parseFloat(rowData[2]);
            	 data[index][3] = Float.parseFloat(rowData[3]);
            	 data[index][4] = Float.parseFloat(rowData[4]);
            	 data[index][5] = Float.parseFloat(rowData[5]);
            	 data[index][6] = Float.parseFloat(rowData[6]);
            	 data[index][7] = Float.parseFloat(rowData[7]);
            	 data[index][8] = Float.parseFloat(rowData[8]);
            	 data[index][9] = Float.parseFloat(rowData[9]);
            	 
            	 index++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
	
	
	public void writeToFile(String str, int index, double error)
	{

		
		try{

    		File file =new File(str);

    		//if file doesn't exists, then create it
    		if(!file.exists()){
    			file.createNewFile();
    		}

    		//true = append file
    		FileWriter fileWriter = new FileWriter(str,true);
    	        BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
    	        bufferWriter.write(String.format("%d, %f \n", index, error).toString());
    	        bufferWriter.close();
    	        
    	    System.out.format("Epoch %d TSE %f\n", index, error);

    	}catch(IOException e){
    		e.printStackTrace();
    	}
	}
	
	public void saveWeights(String f)
	{
		
		try{

    		File file = new File(f);

    		//if file doesn't exists, then create it
    		if(!file.exists()){
    			file.createNewFile();
    		}
    		
		
		int i,j,k = 0;
		for(i = 0; i < nHidden; i++)
		{
			for(j = 0; j< nInputs; j++)
			{				
				FileWriter fileWriter = new FileWriter(f, true);
				String s = String.format("%f,", (double) net.get(nInputs + i).weights.get(j));
				fileWriter.write(s);
				fileWriter.close();
			}

		}
		
		for(k = 0; k < nHidden; k++)
		{
			FileWriter fileWriter = new FileWriter(f, true);
			String s = String.format("%f,", (double) net.get(nInputs + nHidden).weights.get(k));
			fileWriter.write(s);
    	    fileWriter.close();
		}
		
		
		
	} catch(IOException e){
		e.printStackTrace();
	}
	}
	
	public void loadWeights(String f)
	{
		String csvFile = f;
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
		
		try {
			
                // use comma as separator
            br = new BufferedReader(new FileReader(csvFile));
			line = br.readLine();
            String[] weights = line.split(cvsSplitBy);
            			
			int count = 0;
			int i,j,k = 0;
			for(i = 0; i < nHidden; i++)
			{
				for(j = 0; j< nInputs; j++)
				{									
					net.get(nInputs + i).weights.set(j,(double) Float.parseFloat(weights[count]));
					count++;
				}

			}
			
			for(k = 0; k < nHidden; k++)
			{
				net.get(nInputs + nHidden).weights.set(k,(double) Float.parseFloat(weights[count]));
				count++;

			}
			
			
			
		} catch(IOException e){
			e.printStackTrace();
		}
	}

}
