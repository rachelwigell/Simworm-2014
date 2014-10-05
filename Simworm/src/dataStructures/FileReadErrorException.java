package dataStructures;

public class FileReadErrorException extends Exception {
	private static final long serialVersionUID = 1L;

	public FileReadErrorException(String filename){
		System.out.println("File " + filename + " was not found.\n"
				+ "Please ensure that all CSV files are located in the same directory as this executable\n"
				+ "and that no filenames have been altered from their original states.");
	}
}
