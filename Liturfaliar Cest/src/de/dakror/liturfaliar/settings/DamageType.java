package de.dakror.liturfaliar.settings;

import java.awt.Color;

public enum DamageType
{
  POSITIVE(Color.white, 25), // from player to mobs
  NEGATIVE(Color.red, 25), // from mobs to player
  CRITICAL(Color.orange, 35),
  HEAL(Color.green, 35),
  SPECIAL(Colors.SPECIAL, 35),
  FIRE(Color.red, 25);
  
  private Color color;
  private int   size;
  
  public Color getColor()
  {
    return color;
  }
  
  public void setColor(Color color)
  {
    this.color = color;
  }
  
  public int getSize()
  {
    return size;
  }
  
  public void setSize(int size)
  {
    this.size = size;
  }
  
  private DamageType(Color c, int s)
  {
    color = c;
    size = s;
  }
}
