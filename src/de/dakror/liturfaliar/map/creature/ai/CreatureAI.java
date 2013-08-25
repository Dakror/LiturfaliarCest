package de.dakror.liturfaliar.map.creature.ai;

import java.awt.Shape;
import java.awt.geom.Arc2D;

import de.dakror.liturfaliar.map.creature.Creature;
import de.dakror.liturfaliar.map.creature.ai.path.Path;
import de.dakror.liturfaliar.settings.Attributes.Attr;
import de.dakror.liturfaliar.util.Vector;

public abstract class CreatureAI
{
  protected int      trackRadius;
  protected int      trackAngle;
  protected Creature creature;
  
  public CreatureAI(Creature c)
  {
    creature = c;
    trackRadius = (int) c.getAttributes().getAttribute(Attr.trackradius).getValue();
    trackAngle = (int) c.getAttributes().getAttribute(Attr.trackangle).getValue();
  }
  
  public abstract Path findPath(Vector target);
  
  public boolean isInSight(Creature o)
  {
    Vector TN = creature.getTrackingNode();
    int dir = creature.getDir();
    int sAngle = (dir == 0) ? -90 : ((dir == 1) ? 180 : ((dir == 2) ? 0 : 90));
    Shape s = new Arc2D.Double(TN.x - trackRadius, TN.y - trackRadius, trackRadius * 2, trackRadius * 2, sAngle - trackAngle / 2, trackAngle, Arc2D.PIE);
    return s.intersects(o.getBumpArea().getBounds2D());
  }
  
  public boolean isTrackable(Creature o)
  {
    Vector TN = creature.getTrackingNode();
    Shape s = new Arc2D.Double(TN.x - trackRadius, TN.y - trackRadius, trackRadius * 2, trackRadius * 2, 0, 360, Arc2D.PIE);
    return s.intersects(o.getBumpArea().getBounds2D());
  }
  
  public abstract boolean canAttack(Creature o);
}
