package processing;

import processing.core.PApplet;
import processing.core.PGraphics;

public class Picker 
{
  protected PApplet parent;
  public Buffer buffer;
  
  public Picker(PApplet parent) 
  {
    this.parent = parent;
    buffer = new Buffer(parent);     
    //parent.registerMethod("pre",this);
    //parent.registerMethod("draw",this);
  }      
  
  public void begin() 
  {
    if(parent.recorder == null)
    {
      parent.recorder = buffer; 
      buffer.beginDraw();           
    }
  }
  
  public void end() 
  {
    buffer.endDraw();  
    parent.recorder = null;
  }

  public void start(int i)
  {
    if (i < 0 || i > 16777214)
    {
      PApplet.println("[Picking error] start(): ID out of range");
      return;
    }
    buffer.setCurrentId(i);            
  }
  
  public void stop() 
        {
    parent.recorder = null;
  }
  
  public void resume() 
        {
    parent.recorder = buffer;
  }
  
  public int get(int x, int y)
  {
    return buffer.getId(x, y);
  }
  
  public PGraphics getBuffer()
  {
    return buffer;
  }    
}