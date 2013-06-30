package de.dakror.liturfaliar.item.skillanim;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.Creature;

public class Sword0 extends SkillAnimation
{
  int    rx, ry;
  double theta;
  
  Image  image;
  Area   hitArea;
  
  @Override
  public void init()
  {
    image = caster.getEquipment().getFirstWeapon().getIcon();
    theta = 45; // presuming weapon handle in lower right corner -> rotate 45°
    switch (caster.getDir())
    {
      case 0: // down
      {
        below = false;
        theta += 180;
        rx = caster.getWidth() / 2 - image.getWidth(null) / 2;
        ry = caster.getRelativePos()[1] + caster.getHeight() - caster.bh;
      }
    }
  }
  
  @Override
  public boolean isInRange(Creature o)
  {
    return false;
  }
  
  @Override
  protected void draw(Graphics2D g, Viewport v, Map m)
  {
    AffineTransform oldTransform = g.getTransform();
    g.setTransform(AffineTransform.getRotateInstance(theta));
    
    g.drawImage(image, rx, ry, v.w);
    
    g.setTransform(oldTransform);
  }
}
