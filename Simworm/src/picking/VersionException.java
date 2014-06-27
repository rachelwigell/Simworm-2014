package picking;

/**
 * thrown when the "version" of the ordering algorithm in the Coordinates class doesn't produce a working order
 * this indicates that another of the three versions will need to be used.
 * @author Rachel
 */
public class VersionException extends Exception{
	private static final long serialVersionUID = 1L;

	public VersionException(){}
}