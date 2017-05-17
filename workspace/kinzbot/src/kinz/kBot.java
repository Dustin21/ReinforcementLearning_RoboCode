package kinz;

import robocode.AdvancedRobot;

import robocode.BattleEndedEvent;
import robocode.Bullet;
import robocode.BulletHitEvent;
import robocode.DeathEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobocodeFileWriter;
import robocode.RobotStatus;
import robocode.ScannedRobotEvent;
import robocode.StatusEvent;
import robocode.WinEvent;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import nnet.*;

public class kBot extends AdvancedRobot {

	static NNet net = new NNet(9, 8, 3000, 0.009, 0.99, -1, 1, true);
	
	
	static String wlFile = "winLossOut1.txt";
	static String qtFile = "qTableOut.csv";
	
	double reward = 0;
	
	boolean sarsaGo = false;
	
	
	static double alpha = 0.2;
	static double gamma = 0.99;
	//static double epsilon = 0.01;
	static double epsilon = 0.01;
	
	int moveDirection = 1;
	int currentAction = 0;
	boolean enemyLockedOn = false;
	boolean actionComplete = false;
	boolean actionInProgress = false;
	boolean moveComplete = false;
	boolean noShotsRemaining = false;
	double enemyAbsBearing;
	double gunAngleDifference;
	double enemyDistance;
	double health = 100;
	
	int win;
	int death;

	
	static int xQuantize = 8;
	static int yQuantize = 6;
	int binBearing = 12;
	

	double pX = 800, pY=600;
	
	double bulletSuperWeak = 0.5;
	double bulletWeak = 1;
	double bulletMedium = 2;
	double bulletStrong =3;
	Bullet bullet;
	
	int xBinned;
	int yBinned;
	
	
	int xQuantized;
	int yQuantized;
	int distanceQuantized;
	int healthQuantized;
	int binDistanceQuantized = 3;
	int binHealthQuantized = 3;

	double temp1;
	double temp2;
	
	
	ArrayList<Integer> state = new ArrayList<Integer>();
	ArrayList<Integer> nextState = new ArrayList<Integer>();
	ArrayList<Integer> prevState = new ArrayList<Integer>();
	ArrayList<Integer> winLoss = new ArrayList<Integer>();
	
	static Qtable q = new Qtable(4, 5, false);

	
	public void onStatus(StatusEvent e)
	{
		if(actionComplete)
		{	
			if(sarsaGo == false)
			{
				health = e.getStatus().getEnergy();
				
				xQuantized = q.quantize(e.getStatus().getX(), 0.0, 800.0, xQuantize);
				yQuantized = q.quantize(e.getStatus().getY(), 0.0, 600.0, yQuantize);
				distanceQuantized = q.quantize(enemyDistance, 0, 1000, binDistanceQuantized - 1);
				healthQuantized = q.quantize(health, 0, 100, binHealthQuantized - 1);
				
				
				nextState.set(0, xQuantized);
				nextState.set(1, yQuantized);
				nextState.set(2, distanceQuantized);
				nextState.set(3, healthQuantized);
				
				/*temp1 = (1-alpha) * q.QTable[state.get(0)][state.get(1)][state.get(2)][state.get(3)][currentAction];
				
				double max = -9999999;
				for(int i = 0; i < 5; i++)
				{
					if(q.QTable[nextState.get(0)][nextState.get(1)][nextState.get(2)][nextState.get(3)][i] > max)
					{
						max = q.QTable[nextState.get(0)][nextState.get(1)][nextState.get(2)][nextState.get(3)][i];
					}
				}
				
				temp2 = alpha * (reward + max);
				
				q.QTable[state.get(0)][state.get(1)][state.get(2)][state.get(3)][currentAction] = temp1 + temp2;
				
				*/
								
				reward = 2*((reward + 100)/(150 + 100) - 0.5);
				
				double max = -9999999;
				for(int i = 0; i < 5; i++)
				{
					double[] in = new double[9];
					in[0] = (double)xQuantized/xQuantize - 0.5;
					in[1] = (double)yQuantized/yQuantize - 0.5;
					in[2] = (double)distanceQuantized/binDistanceQuantized - 0.5;
					in[3] = (double)healthQuantized/binHealthQuantized - 0.5;
					double a0 = 0;
					double a1 = 0;
					double a2 = 0;
					double a3 = 0;
					double a4 = 0;
					
					switch(i)
					{
					case 0: a0=1;a1=-1;a2=-1;a3=-1;a4=-1; break;
					case 1: a0=-1;a1=1;a2=-1;a3=-1;a4=-1; break;
					case 2: a0=-1;a1=-1;a2=1;a3=-1;a4=-1; break;
					case 3: a0=-1;a1=-1;a2=-1;a3=1;a4=-1; break;
					case 4: a0=-1;a1=-1;a2=-1;a3=-1;a4=1; break;
					}

				 	in[4]=a0;
				 	in[5]=a1;
				 	in[6]=a2;
				 	in[7]=a3;
				 	in[8]=a4;

					double val = net.feedForward2(in);
					if(val > max)
					{
						max = val;
					}
					
					
					
				}
				
				double target = reward+gamma* max;
				
				
				target = 2*((target - 1)/2 - 0.5);
				
				double[] in = new double[9];
				in[0] = state.get(0)/xQuantize - 0.5;
				in[1] = state.get(1)/yQuantize - 0.5;
				in[2] = state.get(2)/binDistanceQuantized - 0.5;
				in[3] = state.get(3)/binHealthQuantized - 0.5;
				double a0 = 0;
				double a1 = 0;
				double a2 = 0;
				double a3 = 0;
				double a4 = 0;
				
				switch(currentAction)
				{
				case 0: a0=1;a1=-1;a2=-1;a3=-1;a4=-1; break;
				case 1: a0=-1;a1=1;a2=-1;a3=-1;a4=-1; break;
				case 2: a0=-1;a1=-1;a2=1;a3=-1;a4=-1; break;
				case 3: a0=-1;a1=-1;a2=-1;a3=1;a4=-1; break;
				case 4: a0=-1;a1=-1;a2=-1;a3=-1;a4=1; break;
				}

			 	in[4]=a0;
			 	in[5]=a1;
			 	in[6]=a2;
			 	in[7]=a3;
			 	in[8]=a4;

			 	net.train2(target,  in);
				
				
			}
			reward = 0;
			actionComplete = false;
			
		}
		
		RobotStatus status =  e.getStatus();
		
		if(enemyLockedOn)
		{
			
			if(!actionComplete && !actionInProgress)
			{
				moveComplete = false;
				health = e.getStatus().getEnergy();
				
				xQuantized = q.quantize(e.getStatus().getX(), 0.0, 800.0, xQuantize);
				yQuantized = q.quantize(e.getStatus().getY(), 0.0, 600.0, yQuantize);
				distanceQuantized = q.quantize(enemyDistance, 0, 1000, binDistanceQuantized - 1);
				healthQuantized = q.quantize(health, 0, 100, binHealthQuantized - 1);
				
				
				state.set(0, xQuantized);
				state.set(1, yQuantized);
				state.set(2, distanceQuantized);
				state.set(3, healthQuantized);
				
				
				//actionInProgress = true;
				
				Random r = new Random();
				
				double rand = r.nextDouble();
				
				
				
				double[] in = new double[9];
				in[0] = (double)xQuantized/xQuantize - 0.5;
				in[1] = (double)yQuantized/yQuantize - 0.5;
				in[2] = (double)distanceQuantized/binHealthQuantized - 0.5;
				in[3] = (double)healthQuantized/binHealthQuantized - 0.5;
				double a0 = 0;
				 double a1 = 0;
				 double a2 = 0;
				 double a3 = 0;
				 double a4 = 0;
				 
				
				
				int actionDecided =0;
				if(rand <= 1-epsilon)
				{
					double max = -9999999;
					for(int i = 0; i < 5; i++)
					{
						 switch(i)
							{
							case 0: a0=1;a1=-1;a2=-1;a3=-1;a4=-1; break;
							case 1: a0=-1;a1=1;a2=-1;a3=-1;a4=-1; break;
							case 2: a0=-1;a1=-1;a2=1;a3=-1;a4=-1; break;
							case 3: a0=-1;a1=-1;a2=-1;a3=1;a4=-1; break;
							case 4: a0=-1;a1=-1;a2=-1;a3=-1;a4=1; break;
							}

						 	in[4]=a0;
						 	in[5]=a1;
						 	in[6]=a2;
						 	in[7]=a3;
						 	in[8]=a4;
						 	double val = net.feedForward2(in);
						 	
							if( val > max)
							{
								max = val;
								actionDecided = i;
							}
						
							
							actionDecided = actionDecided;
						/**
						if(q.QTable[state.get(0)][state.get(1)][state.get(2)][state.get(3)][i] > max)
						{
							max = q.QTable[state.get(0)][state.get(1)][state.get(2)][state.get(3)][i];
							actionDecided = i;
						}
						**/
					}
				}
				else
				{
					actionDecided = r.nextInt(5);
				}
				
				currentAction = actionDecided;
				System.out.format("ActionL: %d\n", currentAction);
				
				
				if(sarsaGo == true)
				{
					health = e.getStatus().getEnergy();
					
					xQuantized = q.quantize(e.getStatus().getX(), 0.0, 800.0, xQuantize);
					yQuantized = q.quantize(e.getStatus().getY(), 0.0, 600.0, yQuantize);
					distanceQuantized = q.quantize(enemyDistance, 0, 1000, binDistanceQuantized - 1);
					healthQuantized = q.quantize(health, 0, 100, binHealthQuantized - 1);
					
					
					nextState.set(0, xQuantized);
					nextState.set(1, yQuantized);
					nextState.set(2, distanceQuantized);
					nextState.set(3, healthQuantized);
					
					temp1 = (1-alpha) * q.QTable[state.get(0)][state.get(1)][state.get(2)][state.get(3)][currentAction];
					
					
					double max = q.QTable[nextState.get(0)][nextState.get(1)][nextState.get(2)][nextState.get(3)][currentAction];
					
					temp2 = alpha * (reward + max);
					
					q.QTable[state.get(0)][state.get(1)][state.get(2)][state.get(3)][currentAction] = temp1 + temp2;
					reward = 0;
				}
				
				if(!actionInProgress)
				switch(currentAction)
				{
				
				// move up one block
				case 0:
					double heading = status.getHeading();
					
					if(heading <= 180)
					{
						turnLeft(heading);
					}
					else
					{
						turnRight(360 - heading);
					}
					actionInProgress = true;
					
					break;
					
				// move down one block
				case 1:
					heading = status.getHeading();
					
					if(heading <= 180)
					{
						turnRight(180-heading);
					}
					else
					{
						turnLeft(heading-180);
					}
					actionInProgress = true;
					break;
					
				// move left one block
				case 2:
					heading = status.getHeading();
					
					if(heading <= 90 && heading >= 0)
					{
						turnLeft(90 + heading);
					}
					else if(heading >= 270 && heading <= 359)
					{
						turnLeft(heading-270);
					}
					else if(heading < 270 && heading > 90)
					{
						turnRight(270 - heading); 
					}
					actionInProgress = true;
					break;
				
				// move right one block
				case 3:
					heading = status.getHeading();
					
					if(heading <= 90 && heading >= 0)
					{
						turnRight(90-heading);
					}
					else if(heading >= 270 && heading <= 359)
					{
						turnRight((360-heading)+90);
					}
					else if(heading < 270 && heading > 90)
					{
						turnLeft(heading-90); 
					}
					
					actionInProgress = true;
					break;
					
					
				case 4:
					

					if(getGunHeat() > 0)
					{
						noShotsRemaining = true;
						actionInProgress = false;
						actionComplete = true;
						break;
					}
				
					fire(3.0);
					//execute();
					
					
					actionInProgress = false;
					actionComplete = true;
					
					break;


				case 5:
					
					turnGunRight(360.0/(double)binBearing);
					actionInProgress = false;
					actionComplete = true;
					
					gunAngleDifference = robocode.util.Utils.normalRelativeAngle(enemyAbsBearing- getGunHeadingRadians());
					
					if(Math.abs(gunAngleDifference) <= (binBearing*Math.PI/180))
					{
						//reward += 20;
						//reward = 20;
					}	
					else
					{
						//reward = -1;
					}
					
					break;
						
				case 6:
					turnGunLeft(360.0/(double)binBearing);
					actionInProgress = false;
					actionComplete = true;
					
					gunAngleDifference =robocode.util.Utils.normalRelativeAngle(enemyAbsBearing- getGunHeadingRadians());
					
					if(Math.abs(gunAngleDifference) <= (binBearing*Math.PI/180))
					{
						//reward += 20;
						//reward = 20;
					}	
					else
					{
						//reward = -1;
					}
					break;
					
				}
			}
			
			
			if(!actionComplete && actionInProgress)
			{
				switch(currentAction)
				{
				case 0:
					if(getTurnRemaining() == 0 && !moveComplete)
					{
						double upY = ((pY/yQuantize)*(yQuantized + 1)) + (pY/yQuantize)/2;
						double dist = upY - status.getY();
						setAhead(dist);
						//execute();
						
						moveComplete = true;
					}
					
					
					
					if(getDistanceRemaining() == 0)
					{
						actionInProgress = false;
						actionComplete = true;
						
						break;
					}
					
					break;

					
				case 1:
					if(getTurnRemaining() == 0 && !moveComplete)
					{
						double upY = ((pY/yQuantize)*(yQuantized - 1)) + (pY/yQuantize)/2;
						double dist = upY - status.getY();
						setAhead(-dist);
						//execute();
						
						moveComplete = true;
						
					}
					
					if(getDistanceRemaining() == 0)
					{
						actionInProgress = false;
						actionComplete = true;
						
						break;
					}
					
					break;
					
					
				case 2:
					
					if(getTurnRemaining() == 0 && !moveComplete)
					{
						double upX = ((pX/xQuantize)*(xQuantized - 1)) + (pX/xQuantize)/2;
						double dist = upX - status.getX();
						setAhead(-dist);
						//execute();
						
						moveComplete = true;
						
					}
					
					if(getDistanceRemaining() == 0)
					{
						actionInProgress = false;
						actionComplete = true;
						
						break;
					}
					
					break;
					
					
				case 3:
					
					if(getTurnRemaining() == 0 && !moveComplete)
					{
						double upX = ((pX/xQuantize)*(xQuantized + 1)) + (pX/xQuantize)/2;
						double dist = upX - status.getX();
						setAhead(dist);
						//execute();
						
						moveComplete = true;
						
					}
			
					if(getDistanceRemaining() == 0)
					{
						actionInProgress = false;
						actionComplete = true;
						
						break;
					}
					
					break;
					
					
				}
			}
		}
	}

	
	/**
	 * run:  Tracker's main run function
	 */
	public void run() {
		
		state.add(0);
		state.add(0);
		state.add(0);
		state.add(0);
		nextState.add(0);
		nextState.add(0);
		nextState.add(0);
		nextState.add(0);
				
		setAdjustRadarForRobotTurn(true); //keep the radar still while we turn
		setBodyColor(new Color(128, 128, 50));
		setGunColor(new Color(50, 50, 20));
		setRadarColor(new Color(200, 200, 70));
		setScanColor(Color.white);
		setBulletColor(Color.red);
		setAdjustGunForRobotTurn(true); // Keep the gun still when we turn
		turnRadarRightRadians(Double.POSITIVE_INFINITY);//keep turning radar right		
	}
 
	/**
	 * onScannedRobot:  Here's the good stuff
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		enemyLockedOn = true;
		enemyDistance = e.getDistance();

		double absBearing=e.getBearingRadians()+getHeadingRadians();//enemies absolute bearing
		double latVel=e.getVelocity() * Math.sin(e.getHeadingRadians() -absBearing);//enemies later velocity
		double gunTurnAmt;//amount to turn our gun
		setTurnRadarLeftRadians(getRadarTurnRemainingRadians());//lock on the radar
		gunTurnAmt = robocode.util.Utils.normalRelativeAngle(absBearing- getGunHeadingRadians()+latVel/22);//amount to turn our gun, lead just a little bit
		setTurnGunRightRadians(gunTurnAmt); //turn our gun
		
	}
	public void onHitWall(HitWallEvent e){
		reward = -20;//reverse direction upon hitting a wall
	}
	/**
	 * onWin:  Do a victory dance
	 */
	public void onWin(WinEvent e) {
	/*for (int i = 0; i < 50; i++) {
			turnRight(30);
			turnLeft(30);
		}*/
		
		reward = 100;
				
		double temp1 = q.QTable[nextState.get(0)][nextState.get(1)][nextState.get(2)][nextState.get(3)][currentAction];
		q.QTable[nextState.get(0)][nextState.get(1)][nextState.get(2)][nextState.get(3)][currentAction] = (1 - alpha) * temp1 + alpha * reward;
		
		double target = 0.5;
		
		double[] in = new double[9];
		in[0] = state.get(0)/xQuantize - 0.5;
		in[1] = state.get(1)/yQuantize - 0.5;
		in[2] = state.get(2)/binDistanceQuantized - 0.5;
		in[3] = state.get(3)/binHealthQuantized - 0.5;
		double a0 = 0;
		double a1 = 0;
		double a2 = 0;
		double a3 = 0;
		double a4 = 0;
		
		switch(currentAction)
		{
		case 0: a0=1;a1=-1;a2=-1;a3=-1;a4=-1; break;
		case 1: a0=-1;a1=1;a2=-1;a3=-1;a4=-1; break;
		case 2: a0=-1;a1=-1;a2=1;a3=-1;a4=-1; break;
		case 3: a0=-1;a1=-1;a2=-1;a3=1;a4=-1; break;
		case 4: a0=-1;a1=-1;a2=-1;a3=-1;a4=1; break;
		}

	 	in[4]=a0;
	 	in[5]=a1;
	 	in[6]=a2;
	 	in[7]=a3;
	 	in[8]=a4;

	 	net.train2(target,  in);
		
		//winLoss.add(1);
		
		q.saveWins(wlFile, "1");
		
	}
	
	public void onHitRobot(HitRobotEvent event)
	{
		reward = -50;
	}
	
	public void onBulletHit(BulletHitEvent e)
	{
		reward = e.getBullet().getPower() * 50;
	}
	
	public void onBulletMissed(BulletHitEvent e)
	{
		reward = -e.getBullet().getPower() * 10;
		//System.out.format("Bullet missed\n");
		
	}
	
	public void onBulletHitBullet(BulletHitEvent e)
	{
		reward = -e.getBullet().getPower() * 2;
		//System.out.format("Own bullet hit enemy bullet");
	}
	
	public void onHitByBullet(HitByBulletEvent e)
	{
		reward = -100;
		//System.out.format("Hit by enemy bullet\n");
	}
	
	public void onDeath(DeathEvent e)
	{
		reward = -100;
		
		double temp1 = q.QTable[nextState.get(0)][nextState.get(1)][nextState.get(2)][nextState.get(3)][currentAction];
		q.QTable[nextState.get(0)][nextState.get(1)][nextState.get(2)][nextState.get(3)][currentAction] = (1 - alpha) * temp1 + alpha * reward;
		
		
		double target = -0.5;
		
		double[] in = new double[9];
		in[0] = state.get(0)/xQuantize - 0.5;
		in[1] = state.get(1)/yQuantize - 0.5;
		in[2] = state.get(2)/binDistanceQuantized - 0.5;
		in[3] = state.get(3)/binHealthQuantized - 0.5;
		double a0 = 0;
		double a1 = 0;
		double a2 = 0;
		double a3 = 0;
		double a4 = 0;
		
		switch(currentAction)
		{
		case 0: a0=1;a1=-1;a2=-1;a3=-1;a4=-1; break;
		case 1: a0=-1;a1=1;a2=-1;a3=-1;a4=-1; break;
		case 2: a0=-1;a1=-1;a2=1;a3=-1;a4=-1; break;
		case 3: a0=-1;a1=-1;a2=-1;a3=1;a4=-1; break;
		case 4: a0=-1;a1=-1;a2=-1;a3=-1;a4=1; break;
		}

	 	in[4]=a0;
	 	in[5]=a1;
	 	in[6]=a2;
	 	in[7]=a3;
	 	in[8]=a4;

	 	net.train2(target,  in);
		
		//winLoss.add(0);
		q.saveWins(wlFile, "0");
				
	}
	
	  public void onBattleEnded(BattleEndedEvent event) {
	      q.saveQtable(qtFile); 
		  out.println("The battle has ended");
	   }
	
}
