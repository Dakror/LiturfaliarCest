package de.dakror.liturfaliar.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.TextLayout;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.Creature;
import de.dakror.liturfaliar.map.creature.Player;
import de.dakror.liturfaliar.settings.DamageType;

public class DamageIndicator
{
  public static final int  LENGTH = 20;
  public static final int  SPEED  = 25;
  public static final Font FONT   = new Font("Arial", 0, 0);
  
  int                      pos;
  int                      x;
  long                     time;
  
  Object                   value;
  Creature                 creature;
  DamageType               type;
  HTMLString               text;
  
  Shape                    outline;
  
  public DamageIndicator(Creature c, DamageType t, Object v)
  {
    pos = 0;
    value = v;
    type = t;
    creature = c;
    x = (int) Math.round(Math.random() * c.getWidth() / 2);
    
    Color color = t.getColor();
    
    if (t.equals(DamageType.NORMAL) && c instanceof Player)
      color = Color.red;
    
    text = new HTMLString(v.toString(), t.getSize(), color, Font.BOLD);
  }
  
  public void draw(Map m, Graphics2D g, Viewport v)
  {
    if (v.areFramesFrozen())
      return;
    
    if (outline == null)
    {
      outline = new TextLayout(value.toString(), FONT.deriveFont(text.style, text.size), g.getFontRenderContext()).getOutline(null);
    }
    
    if (System.currentTimeMillis() - time > SPEED && !isDone())
    {
      pos++;
      time = System.currentTimeMillis();
    }
    
    if (isDone())
      return;
    
    int x = m.getX() + (int) creature.getPos().x + this.x - text.getWidth(g) / 2 + creature.getWidth() / 4;
    int y = m.getY() + (int) creature.getPos().y - pos;
    
    Composite oldComposite = g.getComposite();
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1 - (pos / (float) SPEED)));
    
    Font oldFont = g.getFont();
    g.setFont(FONT);
    
    text.drawString(x, y, g);
    
    g.translate(x, y);
    Color oldColor = g.getColor();
    g.setColor(Color.black);
    g.draw(outline);
    g.setColor(oldColor);
    g.translate(-x, -y);
    
    g.setFont(oldFont);
    g.setComposite(oldComposite);
  }
  
  public boolean isDone()
  {
    return pos == LENGTH;
  }
  
  public Object getRawValue()
  {
    return value;
  }
  
  public Integer getIntValue()
  {
    try
    {
      return Integer.parseInt(value.toString());
    }
    catch (Exception e)
    {
      return null;
    }
  }
}
