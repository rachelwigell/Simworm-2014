package dataStructures;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConsList { //holds data about antecedents and consequences
		public List<Consequence> AandC = new ArrayList<Consequence>();
		public List<Consequence> startLate = new ArrayList<Consequence>();
		
	public ConsList(){
		readAandCInfo("AandC.csv");	
	}
	
	public void readAandCInfo(String file){
		String name = null;
		GeneState state = null;
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "";
			while((line = reader.readLine()) != null){
				String[] consInfo = line.split(",");	
				name = consInfo[0];
				if(consInfo[1].equals("A")) state = new GeneState(true);
				else if(consInfo[1].equals("I")) state = new GeneState(false);
				else if(consInfo[1].equals("U")) state = new GeneState();
				else state = new GeneState(Integer.parseInt(consInfo[1]));
				Gene cons = new Gene(name, state);
				int start = Integer.parseInt(consInfo[2]);
				int end = Integer.parseInt(consInfo[3]);
				Gene[] ante = new Gene[(consInfo.length-4)/2];
				int i = 0;
				int j = 0;
				String anteName = null;
				GeneState anteState = null;
				for(String s: consInfo){
					if(i  > 3){
						if(s.equals("A")) anteState = new GeneState(true);
						else if(s.equals("I")) anteState = new GeneState(false);
						else anteName = s;
						if(anteName != null && anteState != null){
							ante[j] = new Gene(anteName, anteState);
							anteName = null;
							anteState = null;
							j++;
						}
					}
					i++;
				}
				if(start == 1) this.AandC.add(new Consequence(ante, cons, start, end));
				else this.startLate.add(new Consequence(ante, cons, start, end));
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


