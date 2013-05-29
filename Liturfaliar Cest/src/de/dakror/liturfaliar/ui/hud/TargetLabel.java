package de.dakror.liturfaliar.ui.hud;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.Creature;
import de.dakror.liturfaliar.map.creature.NPC;
import de.dakror.liturfaliar.map.creature.Player;
import de.dakror.liturfaliar.util.Assistant;

public class TargetLabel extends HUDComponent
{
  Creature target;
  
  public TargetLabel()
  {
    super(0, 0, 1, 1, 10);
    this.target = null;
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
        float fontsize = 35.0f;
        setWidth(g.getFontMetrics(g.getFont().deriveFont(Font.BOLD, fontsize)).stringWidth(name) + 384);
        setY(0);
        setX(v.w.getWidth() / 2 - getWidth() / 2);
        setHeight(g.getFontMetrics(g.getFont().deriveFont(Font.BOLD, fontsize)).getHeight() + 64);
        Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), getX(), getY() - getHeight() / 2, getWidth(), getHeight(), g, v.w);
        Assistant.drawHorizontallyCenteredString(name, getX(), getWidth(), getY() + getHeight() / 3, g, (int) fontsize, Color.white);
      }
      catch (Exception e)
      {}
    }
  }
}
