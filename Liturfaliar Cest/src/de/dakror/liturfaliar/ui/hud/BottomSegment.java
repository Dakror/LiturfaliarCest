package de.dakror.liturfaliar.ui.hud;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

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
    hotbar = new PlayerHotbar(p);
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
  
  public void mousePressed(MouseEvent e, Map m)
  {
    hotbar.mousePressed(e, m);
  }
  
  public void keyReleased(KeyEvent e, Map m)
  {
    hotbar.keyReleased(e, m);
  }
}
