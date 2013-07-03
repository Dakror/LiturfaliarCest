package de.dakror.liturfaliar.item.skillanim;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.Creature;

public class Sword0 extends SkillAnimation
{
  int   rx, ry;
  int   left;
  
  Image image;
  Area  hitArea;
  long  lastTick;
  
  @Override
  public void init()
  {
    image = ((BufferedImage) Viewport.loadImage("system/icons.png")).getSubimage(caster.getEquipment().getFirstWeapon().getIconPoint().x * 24, caster.getEquipment().getFirstWeapon().getIconPoint().y * 24, 24, 24);
    left = -15;
    lastTick = 0;
    done = false;
    rx = 0;
    ry = 0;
  }
  
  @Override
  public boolean isInRange(Creature o)
  {
    return false;
  }
  
  @Override
  protected void draw(Graphics2D g, Viewport v, Map m)
  {
    if (left >= 50)
    {
      done = true;
      return;
    }
    
    int theta = 45; // presuming weapon handle in lower right corner -> rotate 45°
    switch (caster.getDir())
    {
      case 0: // down
      {
        below = false;
        theta -= 180 + left;
        rx = -20;
        ry = 6;
        break;
      }
      case 1: // left
      {
        below = true;
        theta -= 90 + left;
        rx = -6;
        ry = 8;
        break;
      }
      case 2: // right
      {
        below = false;
        theta += 90 + left;
        rx = -12;
        ry = 8;
        break;
      }
      case 3: // up
      {
        below = true;
        rx = 4;
        ry = 12;
        theta -= left;
        break;
      }
    }
    
    AffineTransform oldTransform = g.getTransform();
    g.setTransform(AffineTransform.getRotateInstance(Math.toRadians(theta), caster.getRelativePos()[0] + rx + m.getX() + image.getWidth(null), caster.getRelativePos()[1] + ry + m.getY() + image.getHeight(null)));
    
    g.drawImage(image, caster.getRelativePos()[0] + rx + m.getX(), caster.getRelativePos()[1] + ry + m.getY(), v.w);
    
    g.setTransform(oldTransform);
  }
  
  @Override
  public void update(long timePassed, Map m)
  {
    if (System.currentTimeMillis() - lastTick > 0)
    {
      left += 5;
      lastTick = System.currentTimeMillis();
    }
  }
}
