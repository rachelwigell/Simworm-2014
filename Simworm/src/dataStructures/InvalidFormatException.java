package dataStructures;

public class InvalidFormatException extends Exception {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Converts a number to the equivalent letter (1 == A, etc)
	 * so that the printout can address columns as letters as in Excel 
	 * @param num the number to be converted
	 * @return the equivalent letter
	 */
	char convertNumToLetter(int num){
		num += 65;
		return (char) num;
	}

	/**
	 * Produces a helpful printout for various types of formatting errors that can occur in the CSV files
	 * @param prob What kind of problem occurred
	 * @param row the row of the cell with the problem
	 * @param column the column of the cell with the problem
	 */
	public InvalidFormatException(FormatProblem prob, int row, int column){
		switch(prob){
			case INVALIDCOMPARTMENT:
				System.out.println("Problem reading cell " + convertNumToLetter(column)  + row + "\n"
						+ "Only valid inputs are\n"
						+ "posterior, anterior, or center.\n"
						+ "Be sure to use lower-case.");
				break;
			case INVALIDAXIS:
				System.out.println("Problem reading cell " + convertNumToLetter(column)  + row + "\n"
						+ "Only valid inputs are\n"
						+ "X, Y, or Z.\n"
						+ "Be sure to use caps.");
				break;
			case INVALIDSTATESTRICT:
				System.out.println("Problem reading cell " + convertNumToLetter(column)  + row + "\n"
						+ "Only valid inputs are\n"
						+ "A (active) or I (inactive).\n"
						+ "For antecedents, you may also use N (not present).\n"
						+ "Be sure to use caps.");
				break;
			case INVALIDSTATE:
				System.out.println("Problem reading cell " + convertNumToLetter(column)  + row + "\n"
						+ "Only valid inputs are\n"
						+ "A (active), I (inactive), U (unknown),\n"
						+ "or a number representing the time at which\n"
						+ "an unknown gene state becomes known.");
				break;
			case EXPECTEDNUMBER:
				System.out.println("Problem reading cell " + convertNumToLetter(column)  + row + "\n"
						+ "Expected to receive a number\n"
						+ "but input contained characters other than digits.");
				break;
			case INVALIDGENENAME:
				System.out.println("Problem reading cell " + convertNumToLetter(column)  + row + "\n"
						+ "Detected invalid gene name: be sure to use only\n"
						+ "lower case characters, numbers, and hyphens.\n"
						+ "These names are checked against a database such that only\n"
						+ "known, valid C. elegans gene names are accepted. The database\n"
						+ "we cross-reference is up-to-date as of 6/29/14.");
				break;
			case INVALIDCELL:
				System.out.println("Problem reading cell " + convertNumToLetter(column) + row + "\n"
						+ "Must contain a cell name existing in the events queue, or one of their children.\n"
						+ "Be sure to use only lower case characters, numbers, and hyphens.");
				break;
			case INVALIDGENE:
				System.out.println("Problem reading cell " + convertNumToLetter(column) + row + "\n"
						+ "Must contain a gene name existing in genes.csv.\n"
						+ "Be sure to use only lower case characters, numbers, and hyphens.");
				break;
			case INVALIDCELLNAME:
				System.out.println("Problem reading cell " + convertNumToLetter(column) + row + "\n"
						+ "Detected invalid cell name: be sure to use only\n"
						+ "lower case characters, numbers, and hyphens.\n"
						+ "Also be sure you have only used valid C. elegans cell names.\n");
				break;
			case MUTANTCSV:
				System.out.println("Problem reading cell " + convertNumToLetter(column) + row + "\n"
						+ "Detected invalid mutant information.\n"
						+ "Please review the notes about formatting in mutantRules.csv");
		}
	}
}
