package de.dakror.liturfaliar.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import de.dakror.liturfaliar.fx.EmoticonSequencer;
import de.dakror.liturfaliar.map.Map;

public class TalkString extends HTMLString
{
  long              time = 0;
  String            s;
  EmoticonSequencer emoticonSequencer;
  TalkChooser       chooser;
  
  public TalkString(Map m, String st, Color color, int styl)
  {
    super(st, Talk.SIZE, color, styl);
    s = ""; 
    emoticonSequencer = new EmoticonSequencer(m, st);    
    string = emoticonSequencer.getClearedString();
    
  }
  
  public TalkString(Map m, HTMLString htmls, String st)
  {
    this(m, st, htmls.c, htmls.style);
  }
  
  public void drawStringAnimated(int x, int y, Graphics2D g)
  {
    if (time == 0)
    {
      time = System.currentTimeMillis();
    }
    
    Color oldColor = g.getColor();
    Font oldFont = g.getFont();
    g.setColor(c);
    g.setFont(oldFont.deriveFont(style, size));
    g.drawString(s, x, y);
    g.setFont(oldFont);
    g.setColor(oldColor);
    
  }
  
  public void showAll()
  {
    s = string;
  }
  
  public boolean isAllShown()
  {
    return s.equals(string);
  }
  
  public boolean updateAnimatedString(int speed)
  {
    if (string.length() == 0)
      return false;
    
    if (s.length() < string.length())
    {
      s += String.valueOf(string.charAt(s.length()));
    }
    
    emoticonSequencer.update(s);
    
    return isAllShown();
  }
}
