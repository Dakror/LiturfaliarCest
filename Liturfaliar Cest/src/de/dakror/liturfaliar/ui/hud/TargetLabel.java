package de.dakror.liturfaliar.ui.hud;

import java.awt.Color;
import java.awt.Graphics2D;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.Creature;
import de.dakror.liturfaliar.map.creature.NPC;
import de.dakror.liturfaliar.map.creature.Player;
import de.dakror.liturfaliar.ui.Icon;
import de.dakror.liturfaliar.util.Assistant;

public class TargetLabel extends HUDComponent
{
  Creature target;
  Icon     hostile;
  
  public TargetLabel()
  {
    super(0, 0, 400, 64, 10);
    target = null;
  }
  
  @Override
  public void update(Map m)
  {
    for (Creature c : m.creatures)
    {
      if (c != null && c.getClass() != Player.class)
      {
        if (m.getPlayer().isLookingAt(c, m) && m.getPlayer().getField(m).distance(c.getField(m)) < 3.0)
        {
          this.target = c;
          return;
        }
      }
    }
    this.target = null;
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
        setX(v.w.getWidth() / 2 - getWidth() / 2);
        Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), getX(), getY() - 10, getWidth(), getHeight(), g, v.w);
        int ix = Assistant.drawHorizontallyCenteredString(name, getX(), getWidth(), getY() + getHeight() / 3, g, 22, Color.white);
        if (target instanceof NPC && ((NPC) target).isHostile())
        {
          if (hostile == null)
            hostile = new Icon(ix - 24, getY() + 5, 22, 22, 4, 238);
          
          hostile.draw(g, v.w);
        }
      }
      catch (Exception e)
      {}
    }
  }
}
