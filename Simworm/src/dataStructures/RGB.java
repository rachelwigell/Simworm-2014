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
	 * Used to generate unique colors. First increments red until reaches 255, then green, etc.
	 */
	public void incrementColor(){
		if(this.red >= 255){
			if(this.green >= 255){
				if(this.blue >= 255){
					return; //make it throw an exception later
				}
				else{
					this.blue++;
					return;
				}
			}
			else{
				this.green++;
				return;
			}
		}
		else{
			this.red++;
			return;
		}
	}
}