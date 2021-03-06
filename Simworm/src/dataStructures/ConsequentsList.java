package dataStructures;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConsequentsList { //holds data about antecedents and consequences
	public List<Consequence> antecedentsAndConsequents = new ArrayList<Consequence>(); //antecedents and consequences that are active at the beginning
	public List<Consequence> startLate = new ArrayList<Consequence>(); //rules that don't start until midway through the simulation
	public List<String> validGeneNames = new ArrayList<String>(); //gene names in genes.csv, for error checking

	/**
	 * Constructor for a ConsList object
	 * Just populates antecedentsAndConsequents and startLate from the CSV
	 */
	public ConsequentsList(){
		try{
			populateValidGenes("src/components/genes.csv");
		}
		catch(Exception e){
			try{
				populateValidGenes("genes.csv");
			}
			catch(Exception f){
				System.out.println("Couldn't find genes.csv at either of the expected locations.");
			}
		}
		
		//populate antecedentsAndConsequents and startLate from the CSV
		//for java application
		try{
			readantecedentsAndConsequentsInfo("src/components/antecedentsAndConsequents.csv");
		}
		catch(Exception e){
			try{
				//for java applet or executable
				readantecedentsAndConsequentsInfo("antecedentsAndConsequents.csv");
			}
				catch(Exception f){
					System.out.println("Couldn't find antecedentsAndConsequents.csv at either of the expected locations.");
				}
		}
	}
	
	/**
	 * Creates a dictionary of valid C. elegans gene names from the wormbase text file
	 * @param file the location of the text file
	 * @throws FileReadErrorException if the file could not be found
	 */
	public void populateValidGenes(String file) throws FileReadErrorException{
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file)); //open the file
			String line = "";
			while((line = reader.readLine()) != null){
				String[] geneInfo = line.split(",");
				validGeneNames.add(geneInfo[0]);
			}
			reader.close();
		}
		catch (FileNotFoundException e){
			throw new FileReadErrorException(file);
		}
		catch (IOException e){
			throw new FileReadErrorException(file);
		}
	}

	/**
	 * Parses a CSV to create the antecedent and consequence rules
	 * @param file The name of the CSV file as a string
	 * @return 0 to indicate success
	 * @throws FileNotFoundException if the given file name couldn't be found
	 * @throws InvalidFormatException if there is a formatting error in the provided file
	 */
	public int readantecedentsAndConsequentsInfo(String file) throws FileNotFoundException, InvalidFormatException{
		String name = null;
		GeneState state = null;
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file)); //opens the given file
			String line = "";
			int row = 1;			
			while((line = reader.readLine()) != null){ //sets the line string to be one line of the file and checks that the line isn't empty
				String[] consInfo = line.split(","); //splits the line into an array with commas as the separator
				name = consInfo[0]; //name of the consequence is the first cell in a row
				if(!validGeneNames.contains(name)){
					reader.close();
					throw new InvalidFormatException(FormatProblem.INVALIDGENE, row, 0);
				}
				//second cell in a row is the state of the consequence
				if(consInfo[1].equals("A")) state = new GeneState(GeneStates.ACTIVE); //active if the row says A
				else if(consInfo[1].equals("I")) state = new GeneState(GeneStates.INACTIVE); //inactive if I
				else{
					reader.close();
					throw new InvalidFormatException(FormatProblem.INVALIDSTATESTRICT, row, 1);
				}
				Gene cons = new Gene(name, state); //create the consequence gene with this info
				int start;
				try{
					start = Integer.parseInt(consInfo[2]); //start time of the rule is in the third cell
				}
				catch(NumberFormatException e){
					reader.close();
					throw new InvalidFormatException(FormatProblem.EXPECTEDNUMBER, row, 2);
				}
				int end;
				try{
					end = Integer.parseInt(consInfo[3]); //end time in the fourth
				}
				catch(NumberFormatException e){
					reader.close();
					throw new InvalidFormatException(FormatProblem.EXPECTEDNUMBER, row, 3);
				}
				//remaining cells house antecedent info, of which there can be an arbitrary number
				//number of antecedents is half of the remaining cells, since each antecedent takes 2 cells to fully describe
				Gene[] ante = new Gene[(consInfo.length-4)/2];
				int i = 0;
				int j = 0;
				String anteName = null;
				GeneState anteState = null;
				for(String s: consInfo){
					if(i  > 3){ //get past the first four spreadsheet cells which don't pertain to antecedents
						if(i % 2 == 1){ //odd number rows are the state
							if(s.equals("A")) anteState = new GeneState(GeneStates.ACTIVE);
							else if(s.equals("I")) anteState = new GeneState(GeneStates.INACTIVE);
							else if(s.equals("N")) anteState = new GeneState(GeneStates.NOTPRESENT);
							else{ //if not A or I, it's an invalid state
								reader.close();
								throw new InvalidFormatException(FormatProblem.INVALIDSTATESTRICT, row, i);
							}
						}
						else anteName = s; //even number rows are the name
						if(!validGeneNames.contains(anteName)){
							reader.close();
							throw new InvalidFormatException(FormatProblem.INVALIDGENE, row, i);
						}
						if(anteName != null && anteState != null){ //once both name and state have been filled in, we're ready to create a new antecedent
							ante[j] = new Gene(anteName, anteState); //put in the antecedents array
							anteName = null; //put back to null to repeat for next antecedent
							anteState = null;
							j++; //increment j to place next gene in the right slot of ante[]
						}
					}
					i++;
				}
				if(start == 1) this.antecedentsAndConsequents.add(new Consequence(ante, cons, start, end)); //if the rule starts at the beginning, put in antecedentsAndConsequents
				else this.startLate.add(new Consequence(ante, cons, start, end)); //else put in startLate
				row++;
			}
			reader.close();
			return 0;
		}
		catch (FileNotFoundException e){
			throw new FileNotFoundException();
		}
		catch (IOException e){
			throw new FileNotFoundException();
		}
	}
}