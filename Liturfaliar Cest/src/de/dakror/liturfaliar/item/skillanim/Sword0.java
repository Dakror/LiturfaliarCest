package de.dakror.liturfaliar.item.skillanim;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.item.action.WeaponAction;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.Creature;
import de.dakror.liturfaliar.settings.Attributes.Attr;
import de.dakror.liturfaliar.settings.DamageType;
import de.dakror.liturfaliar.util.Assistant;

public class Sword0 extends SkillAnimation
{
  int   rx, ry;
  int   left;
  
  Image image;
  Area  hitArea;
  Area  realHitArea;
  long  lastTick;
  
  @Override
  public void init()
  {
    image = ((BufferedImage) Viewport.loadImage("system/icons.png")).getSubimage(caster.getEquipment().getFirstWeapon().getIconPoint().x * 24, caster.getEquipment().getFirstWeapon().getIconPoint().y * 24, 24, 24);
    hitArea = Assistant.toArea(image);
    
    left = -15;
    lastTick = 0;
    done = false;
    rx = 0;
    ry = 0;
  }
  
  @Override
  public boolean isInRange(Creature o, Map m)
  {
    if (realHitArea == null)
      return false;
    
    Area intersection = o.getHitArea(m);
    
    intersection.intersect(realHitArea);
    return !intersection.isEmpty();
  }
  
  @Override
  protected void draw(Graphics2D g, Viewport v, Map m)
  {
    if (this.hitArea == null)
      return;
    
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
    
    AffineTransform tr = AffineTransform.getTranslateInstance(caster.getRelativePos()[0] + rx + m.getX(), caster.getRelativePos()[1] + ry + m.getY());
    tr.rotate(Math.toRadians(theta), image.getWidth(null), image.getHeight(null));
    realHitArea = this.hitArea.createTransformedArea(tr);
    
    AffineTransform oldTransform = g.getTransform();
    AffineTransform t = AffineTransform.getRotateInstance(Math.toRadians(theta), caster.getRelativePos()[0] + rx + m.getX() + image.getWidth(null), caster.getRelativePos()[1] + ry + m.getY() + image.getHeight(null));
    
    g.setTransform(t);
    g.drawImage(image, caster.getRelativePos()[0] + rx + m.getX(), caster.getRelativePos()[1] + ry + m.getY(), v.w);
    g.setTransform(oldTransform);
  }
  
  @Override
  public void update(long timePassed, Map m)
  {
    super.update(timePassed, m);
    
    if (System.currentTimeMillis() - lastTick > 0)
    {
      left += 10;
      lastTick = System.currentTimeMillis();
    }
  }
  
  @Override
  public void dealEffect(Creature c)
  {
    if (!c.equals(caster))
    {
      c.dealDamage(DamageType.NORMAL, (int) ((int) item.getAttributes().getAttribute(Attr.health).getValue() + ((WeaponAction) caster.getEquipment().getFirstWeapon().getAction()).getReandomValue(Attr.health)));
      hit = true;
    }
  }
}
