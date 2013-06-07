package de.dakror.liturfaliar.ui.hud;

import java.awt.Graphics2D;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.Player;

public class BottomSegment
{
  PlayerHealth  health;
  PlayerStamina stamina;
  PlayerHotbar  hotbar;
  
  public BottomSegment(Player p)
  {
    health = new PlayerHealth(p);
    stamina = new PlayerStamina(p);
    hotbar = new PlayerHotbar();
  }
  
  public void update(Map m)
  {
    health.update(m);
    stamina.update(m);
    hotbar.update(m);
  }
  
  public void draw(Graphics2D g, Viewport v, Map m)
  {
    if (m.talk == null)
    {
      health.draw(g, v, m);
      stamina.draw(g, v, m);
      hotbar.draw(g, v, m);
    }
  }
}