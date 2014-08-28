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
	
	/**
	 * converts integer representation of color used by Processing into an RGB object
	 * In Processing, colors are hex codes with this format: AARRGGBB where A is alpha, R/G/B are red/green/blue
	 * This hex code is then converted to an int
	 * This constructor converts that int into RGB
	 * @param color the int representation of the color to be converted
	 */
	public RGB(int color){
		String hex = Integer.toHexString(color); //convert the int back into the hex string
		this.red = (int) Long.parseLong(hex.substring(2,4), 16); //get digits 2 and 3 and convert into int to get R value
		this.green = (int) Long.parseLong(hex.substring(4,6), 16); //4 and 5 are G value
		this.blue = (int) Long.parseLong(hex.substring(6,8), 16); // 6 and 7 are B value
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
	
	/**
	 * checks whether two colors are similar; this is used to make object picking more accurate (fewer false positives).
	 * a cell should only be picked if the pixel clicked is a similar color to the cell color.
	 * @param to the color being compared to "this" color
	 * @return boolean indicating whether the colors are similar.
	 */
	public boolean colorIsClose(RGB to){		
		//regardless of how much light/shadow is on the pixel, each pixel on the sphere should have the same RATIO of R/G/B values as the assigned color
		float redProp = (float) this.red/ (float) (to.red+1); //adding 1 to denominator barely changes ratio but avoids possibility of division by zero
		float greenProp = (float) this.green/ (float) (to.green+1);
		float blueProp = (float) this.blue/ (float) (to.blue+1);		

		//theoretically the differences in all these ratios should be 0, but we check that it's >.1 to allow for floating point or rounding errors
		//and also because we added 1 to the denominator. no two colors should be similar enough for this to matter
		if(Math.abs(redProp-greenProp) > .1) return false;
		if(Math.abs(greenProp-blueProp) > .1) return false;
		if(Math.abs(blueProp-redProp) > .1) return false;
		
		return true;
	}
}