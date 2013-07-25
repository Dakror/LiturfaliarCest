package de.dakror.liturfaliar.ui;

import java.awt.Color;
import java.awt.Graphics2D;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.settings.CFG;

public class TalkChooser extends Component
{
  public static int WIDTH = 120;
  
  String            raw;
  Button[]          choices;
  
  public TalkChooser(String r)
  {
    super(0, 0, WIDTH, 0);
    raw = r;
  }
  
  public void parse()
  {
    String[] parts = raw.split(",");
    
    choices = new Button[parts.length];
    for (int i = 0; i < choices.length; i++)
    {
      String text = parts[i].substring(0, parts[i].indexOf("="));
      String name = parts[i].substring(parts[i].indexOf("="));
      boolean allRequirementsSet = true; 
      if(name.indexOf("[") > -1) {
        String req = name.substring(name.indexOf("["), name.indexOf("]"));
      }
      
      Button b = new Button(getX() + 10, getY() + 10 + i * 30,text, Color.white);
    }
  }
  
  public String getClearedString()
  {
    return raw;
  }
  
  @Override
  public void update()
  {}
  
  @Override
  public void draw(Graphics2D g, Viewport v)
  {
    if (getX() == 0)
    {
      setX(v.w.getWidth() / 6 + v.w.getWidth() / 3 * 2 - getWidth());
      setHeight(raw.split(",").length * 30);
      setY(v.w.getHeight() / 16 * 13 - getHeight());
      parse();
    }
  }
  
}
