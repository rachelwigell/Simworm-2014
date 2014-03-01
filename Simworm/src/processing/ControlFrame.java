package processing;

import controlP5.ControlP5;
import processing.core.PApplet;

public class ControlFrame extends PApplet {

	  ControlP5 cp5;

	  Object parent;

	  int w, h;

	  int abc;

	  private ControlFrame() {
	  }

	  ControlFrame(Object theParent, int theWidth, int theHeight) {
	    parent = theParent;
	    w = theWidth;
	    h = theHeight;
	  }


	  public ControlP5 control() {
	    return cp5;
	  }
	  public void setup() {
	    size(w, h);
	    frameRate(25);
	    cp5 = new ControlP5(this);
	  }

	  public void draw() {
	      background(abc);
	  }
	}