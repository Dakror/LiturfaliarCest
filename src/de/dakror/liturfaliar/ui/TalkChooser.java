package de.dakror.liturfaliar.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.Database;

public class TalkChooser extends Component
{
  public static int WIDTH = 300;
  
  String            raw;
  Button[]          choices;
  boolean           closeRequested;
  
  public TalkChooser(String r)
  {
    super(0, 0, WIDTH, 0);
    raw = r;
    closeRequested = false;
  }
  
  public void parse()
  {
    String[] parts = raw.split(",");
    
    choices = new Button[parts.length];
    
    boolean allFalse = true;
    
    for (int i = 0; i < choices.length; i++)
    {
      String text = parts[i].substring(0, parts[i].indexOf("="));
      String name = parts[i].substring(parts[i].indexOf("=") + 1);
      boolean allRequirementsSet = true;
      if (name.indexOf("[") > -1)
      {
        String reqRaw = name.substring(name.indexOf("[") + 1, name.indexOf("]"));
        String[] reqs = reqRaw.split(",");
        for (String r : reqs)
        {
          if (!Database.getBooleanVar(r))
          {
            allRequirementsSet = false;
            break;
          }
        }
      }
      
      if (allFalse && allRequirementsSet)
      {
        allFalse = false;
      }
      
      Button b = new Button(getX() + 9, getY() + 9 + i * 32, getWidth() - 18, text, Color.white, 25f);
      b.round = false;
      b.clickmod = 0;
      b.hovermod = 0;
      b.soundMOVER = false;
      b.tileset = null;
      b.name = name.replaceAll("\\[\\S{1,}\\]", " ");
      if (!allRequirementsSet) b.disabled = true;
      
      choices[i] = b;
    }
    
    if (allFalse) closeRequested = true;
  }
  
  public String getClearedString()
  {
    return raw;
  }
  
  @Override
  public void update()
  {
    for (Button b : choices)
    {
      b.update();
      
      if (b.getState() == 1)
      {
        String[] keys = b.name.split(",");
        for (String k : keys)
          Database.setBooleanVar(k, true);
        closeRequested = true;
      }
    }
  }
  
  @Override
  public void draw(Graphics2D g)
  {
    if (getX() == 0)
    {
      setX(Viewport.w.getWidth() / 6 + Viewport.w.getWidth() / 3 * 2 - getWidth());
      setHeight(raw.split(",").length * 30 + 22);
      setY(Viewport.w.getHeight() / 16 * 13 - getHeight());
      
      parse();
    }
    
    Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), getX(), getY(), getWidth(), getHeight(), g);
    for (Button b : choices)
    {
      b.draw(g);
    }
  }
  
  @Override
  public void mouseMoved(MouseEvent e)
  {
    for (Button b : choices)
    {
      b.mouseMoved(e);
    }
  }
  
  @Override
  public void mouseReleased(MouseEvent e)
  {
    for (Button b : choices)
    {
      b.mouseReleased(e);
    }
  }
  
  @Override
  public void mousePressed(MouseEvent e)
  {
    for (Button b : choices)
    {
      b.mousePressed(e);
    }
  }
  
  public boolean isCloseRequested()
  {
    return closeRequested;
  }
}
