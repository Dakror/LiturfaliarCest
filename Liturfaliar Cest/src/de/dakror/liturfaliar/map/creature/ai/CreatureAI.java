package de.dakror.liturfaliar.map.creature.ai;

import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Point2D;

import de.dakror.liturfaliar.map.creature.Creature;
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
  
  public boolean isTrackable(Creature o)
  {
    Vector TN = creature.getTrackingNode();
    Area track = new Area(new Arc2D.Double(TN.coords[0], trackRadius * 2, trackRadius * 2, TN.coords[1], 0, 360, Arc2D.PIE)); // TODO: implement looking direction - for now it's all around
    
    Vector oTN = o.getTrackingNode();
    return track.contains(new Point2D.Double(oTN.coords[0], oTN.coords[1]));
  }
}
