package kinz;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Qtable implements LUTInterface {
		
	int xQuantized = 8;
	int yQuantized = 6;
	int distanceQuantized = 3;
	int healthQuantized = 3;
	
	double[][][][][] QTable;
		
	int nStates;
	int nActions;
	
	int policy;
	double quantizer;

	@Override
	public double outputFor(double[] X) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double train(double[] X, double argValue) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	//@Override
	public void saveToFile(String wlFile, ArrayList<Integer> winLoss) {
		
		try
		{
			FileWriter fileWriter = new FileWriter(wlFile, true);
			
			for(int i=0; i < winLoss.size(); i++)
			{

				String s = String.format("%f,",winLoss.get(i));
				fileWriter.write(s);
				
				fileWriter.write("\n");
			}
			fileWriter.close();
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	@Override
	public void load(String argFileName) throws IOException {
	
		FileReader file = new FileReader(argFileName);
	       
        Scanner rowScanner = new Scanner(file);
        Scanner colScanner;

        String line = null;
        
        int rows = 0;
        int cols = 0;
        
        while (rowScanner.hasNextLine()) 
        {

          line = rowScanner.nextLine();

          colScanner = new Scanner(line).useDelimiter(",");
          cols = 0;
          
          while (colScanner.hasNext()) 
          {
            double val =  Float.parseFloat(colScanner.next());
            QTable.get(rows).set(cols, val);
            
            cols++;
           
          } 
          colScanner.close();
          rows++;
        }
        
        rowScanner.close();
		
        try 
        {
			file.close();
		}
        catch (IOException e) 
        {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

	@Override
	public void initialiseLUT() {
		
		Random r = new Random();
		int i=0, j=0;
	
		QTable = new ArrayList<ArrayList<Double>>();
		
		// add rows to QTable (rows are states)
		for(i = 0; i < nStates; i++) 
		{
			QTable.add(new ArrayList<Double>());
		}
		
		for(i = 0; i < nStates; i++)
		{
			for(j = 0; j < nActions; j++)
			{
				QTable.get(i).add(0.0);
			}
		}
		
	}
	
	 **/
	
	
	Qtable(int _dimStates, int _numActions, boolean loadFile)
	{
		 QTable = new double[xQuantized][yQuantized][distanceQuantized][healthQuantized][5];
		 
		
		 
			
		/**
		nStates = _dimStates;
		nActions = _numActions;
		initialiseLUT();
		if(loadFile == true)
		{
			try 
			{
				load("QTable.csv");
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		**/
	}
	
	 public void saveWins(String f, String win)
	 {
		 try{

	    		File file =new File(f);

	    		//if file doesn't exists, then create it
	    		if(!file.exists()){
	    			file.createNewFile();
	    		}

	    		//true = append file
	    		FileWriter fileWriter = new FileWriter(f,true);
	    	        BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
	    	        bufferWriter.write(String.format(win + "\n").toString());
	    	        bufferWriter.close();
	    	        
	    	    System.out.format(win + "\n");
	    	    
	    	    fileWriter.close();

	    	}catch(IOException e){
	    		e.printStackTrace();
	    	}
		 
	 }
	 
	 
	 public void saveQtable(String f)
	 {
		 double a0 = 0;
		 double a1 = 0;
		 double a2 = 0;
		 double a3 = 0;
		 double a4 = 0;
		 
		 
		 try{

	    		File file =new File(f);

	    		//if file doesn't exists, then create it
	    		if(!file.exists()){
	    			file.createNewFile();
	    		}
	    		
	    		
	    		int i,j,k,l,m;
	    		
	    		for(i = 0; i < healthQuantized; i++)
	    		{
	    			for(j = 0; j < distanceQuantized; j++)
	    			{
	    				for(k = 0; k < yQuantized; k++)
	    				{
	    					for(l = 0; l < xQuantized; l++)
	    					{
	    						for(m = 0; m < 5; m++)
	    						{
	    							switch(m)
	    							{
	    							case 0: a0=1;a1=-1;a2=-1;a3=-1;a4=-1; break;
	    							case 1: a0=-1;a1=1;a2=-1;a3=-1;a4=-1; break;
	    							case 2: a0=-1;a1=-1;a2=1;a3=-1;a4=-1; break;
	    							case 3: a0=-1;a1=-1;a2=-1;a3=1;a4=-1; break;
	    							case 4: a0=-1;a1=-1;a2=-1;a3=-1;a4=1; break;
	    							}
	    							
	    							String s = String.format("%f,%f,%f,%f", (double)i/healthQuantized - 0.5,
	    									(double)j/distanceQuantized - 0.5, (double)k/yQuantized - 0.5,
	    									(double)l/xQuantized - 0.5);
	    							
	    							String s2 = String.format(",%f,%f,%f,%f,%f", a0, a1, a2, a3, a4);
	    							
	    							String s3 = String.format(",%f\n", QTable[l][k][j][i][m]);
	    							
	    							//true = append file
	    				    		FileWriter fileWriter = new FileWriter(f, true);
	    				    	        
	    				    	    fileWriter.write(s+s2+s3);
	    				    	    
	    				    	    fileWriter.close();

	    				    	}
	    						}
	    					}
	    				}
	    			}
	    		}catch(IOException e){
		    		e.printStackTrace();
		    	}
	    		
	    		
		 
	 }
	 
	 
	
	public void saveRewards(ArrayList <Double> r, String f) throws IOException
	{
		FileWriter fileWriter = new FileWriter(f, false);
		
		
		for(int i=0; i<r.size(); i++)
		{
			String s = String.format("%d,%f\n",i,r.get(i));
			fileWriter.write(s);
		}
	
		fileWriter.close();
	}

	@Override
	public int indexFor(double[] X) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public int quantize(double nonQuantized, double minimum, double maximum, int q)
	{
		double qCreate = (nonQuantized - minimum)/(maximum - minimum);
		
		return (int) (qCreate * q);
	}

	@Override
	public void save(File argFile) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void load(String argFileName) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initialiseLUT() {
		// TODO Auto-generated method stub
		
	}
	

	
	

}
