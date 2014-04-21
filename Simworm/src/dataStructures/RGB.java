package dataStructures;

public class RGB {
	private int red;
	private int green;
	private int blue;
	
	/**
	 * Constructor for an RGB object
	 * @param red The 0-255 value of the red channel
	 * @param green The 0-255 value of the green channel
	 * @param blue The 0-255 value of the blue channel
	 */
	public RGB(int red, int green, int blue){
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public RGB(RGB toDup){
		this.red = toDup.red;
		this.green = toDup.green;
		this.blue = toDup.blue;
	}

	public int getRed() {
		return red;
	}

	public int getGreen() {
		return green;
	}

	public int getBlue() {
		return blue;
	}
}
