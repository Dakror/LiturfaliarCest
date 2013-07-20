package de.dakror.liturfaliar.ui.hud;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.Creature;
import de.dakror.liturfaliar.map.creature.NPC;
import de.dakror.liturfaliar.map.creature.Player;
import de.dakror.liturfaliar.settings.Attributes.Attr;
import de.dakror.liturfaliar.ui.CursorText;
import de.dakror.liturfaliar.ui.HTMLString;
import de.dakror.liturfaliar.ui.Icon;
import de.dakror.liturfaliar.ui.ProgressBar;
import de.dakror.liturfaliar.util.Assistant;

public class TargetLabel extends HUDComponent
{
  Creature    target;
  Icon        hostile;
  ProgressBar health;
  Point       mouse = new Point(0, 0);
  
  public TargetLabel()
  {
    super(0, 0, 400, 64, 10);
    target = null;
  }
  
  @Override
  public void update(long timePassed, Map m)
  {
    CursorText.removeCursorText("TargetLabel");
    
    this.target = null;
    for (Creature c : m.creatures)
    {
      if (c != null && c.getClass() != Player.class)
      {
        if (c.isAlive() && ((m.getPlayer().isLookingAt(c, m) && m.getPlayer().getField(m).getNode().getDistance(c.getField(m).getNode()) < 3.0) || (new Rectangle2D.Double(m.getX() + c.getPos().x, m.getY() + c.getPos().y, c.getWidth(), c.getHeight()).contains(mouse))))
        {
          this.target = c;
          if (((NPC) c).getTalkData().length() > 0 && !((NPC) c).isHostile())
          {
            CursorText.setCursorText(new HTMLString(CursorText.cfg, "Reden"), "TargetLabel");
          }
        }
      }
    }
  }
  
  @Override
  public void draw(Graphics2D g, Viewport v, Map m)
  {
    if (this.target != null)
    {
      try
      {
        String name = "";
        if (this.target.getClass() == NPC.class)
          name = ((NPC) this.target).getName();
        else
        {
          this.target = null;
          return;
        }
        
        if (getX() == 0)
        {
          setX(v.w.getWidth() / 2 - getWidth() / 2);
          health = new ProgressBar(getX() + 10, getY() + 25, getWidth() - 20, 1, false, "ff3232", null, false);
        }
        Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), getX(), getY() - 10, getWidth(), getHeight(), g, v.w);
        health.value = (float) (target.getAttributes().getAttribute(Attr.health).getValue() / target.getAttributes().getAttribute(Attr.health).getMaximum());
        int ix = Assistant.drawHorizontallyCenteredString(name, getX(), getWidth(), getY() + getHeight() / 3, g, 22, Color.white);
        if (target instanceof NPC && ((NPC) target).isHostile())
        {
          if (hostile == null)
            hostile = new Icon(ix - 24, getY() + 5, 22, 22, 4, 238);
          
          hostile.draw(g, v.w);
        }
        health.draw(g, v);
      }
      catch (Exception e)
      {}
    }
  }
  
  @Override
  public void mouseMoved(MouseEvent e, Map m)
  {
    mouse = e.getLocationOnScreen();
  }
  
  public Creature getTarget()
  {
    return target;
  }
}
