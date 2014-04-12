package dataStructures;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConsList { //holds data about antecedents and consequences
	public List<Consequence> AandC = new ArrayList<Consequence>(); //antecedents and consequences that are active at the beginning
	public List<Consequence> startLate = new ArrayList<Consequence>(); //rules that don't start until midway through the simulation

	/**
	 * Constructor for a ConsList object
	 * Just populates AandC and startLate from the CSV
	 */
	public ConsList(){
		//populate AandC and startLate from the CSV
		//for java application
		readAandCInfo("src/components/AandC.csv");
		//for java applet
		//readAandCInfo("AandC.csv");
	}

	/**
	 * Parses a CSV to create the antecedent and consequence rules
	 * @param file The name of the CSV file as a string
	 */
	public void readAandCInfo(String file){
		String name = null;
		GeneState state = null;
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file)); //opens the given file
			String line = "";
			while((line = reader.readLine()) != null){ //sets the line string to be one line of the file and checks that the line isn't empty
				String[] consInfo = line.split(","); //splits the line into an array with commas as the separator
				name = consInfo[0]; //name of the consequence is the first cell in a row
				//second cell in a row is the state of the consequence
				if(consInfo[1].equals("A")) state = new GeneState(true); //active if the row says A
				else if(consInfo[1].equals("I")) state = new GeneState(false); //inactive if I
				Gene cons = new Gene(name, state); //create the consequence gene with this info
				int start = Integer.parseInt(consInfo[2]); //start time of the rule is in the third cell
				int end = Integer.parseInt(consInfo[3]); //end time in the fourth
				//remaining cells house antecedent info, of which there can be an arbitrary number
				//number of antecedents is half of the remaining cells, since each antecedent takes 2 cells to fully describe
				Gene[] ante = new Gene[(consInfo.length-4)/2];
				int i = 0;
				int j = 0;
				String anteName = null;
				GeneState anteState = null;
				for(String s: consInfo){
					if(i  > 3){ //get past the first four spreadsheet cells which don't pertain to antecedents
						if(s.equals("A")) anteState = new GeneState(true); //if it says A or I, it's the state
						else if(s.equals("I")) anteState = new GeneState(false);
						else anteName = s; //otherwise it's the name
						if(anteName != null && anteState != null){ //once both name and state have been filled in, we're ready to create a new antecedent
							ante[j] = new Gene(anteName, anteState); //put in the antecedents array
							anteName = null; //put back to null to repeat for next antecedent
							anteState = null;
							j++; //increment j to place next gene in the right slot of ante[]
						}
					}
					i++;
				}
				if(start == 1) this.AandC.add(new Consequence(ante, cons, start, end)); //if the rule starts at the beginning, put in AandC
				else this.startLate.add(new Consequence(ante, cons, start, end)); //else put in startLate
			}
		}
		catch (FileNotFoundException e){
			e.printStackTrace();
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}
}


