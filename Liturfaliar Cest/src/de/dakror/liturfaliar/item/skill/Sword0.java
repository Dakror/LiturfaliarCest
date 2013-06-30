package de.dakror.liturfaliar.item.skill;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.Creature;

public class Sword0 extends Skill
{
  int     rx, ry;
  double  theta;
  
  Image   image;
  Area    hitArea;
  
  boolean below;
  
  public Sword0(Item i, Creature c)
  {
    super(i, c);
    image = i.getIcon();
    theta = 45; // presuming weapon handle in lower right corner -> rotate 45°
    switch (c.getDir())
    {
      case 0: // down
      {
        below = false;
        theta += 180;
        rx = c.getWidth() / 2 - image.getWidth(null) / 2;
        ry = c.getRelativePos()[1] + c.getHeight() - c.bh;
      }
    }
  }
  
  @Override
  public boolean isInRange(Creature o)
  {
    return false;
  }
  
  @Override
  public void drawBelow(Graphics2D g, Viewport v, Map m)
  {}
  
  @Override
  public void draw(Graphics2D g, Viewport v, Map m)
  {
    AffineTransform oldTransform = g.getTransform();
    g.setTransform(AffineTransform.getRotateInstance(theta));
    
    g.drawImage(image, rx, ry, v.w);
    
    g.setTransform(oldTransform);
  }
}
