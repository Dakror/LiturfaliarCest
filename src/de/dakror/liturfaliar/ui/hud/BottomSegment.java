package de.dakror.liturfaliar.ui.hud;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.Player;

public class BottomSegment
{
  public PlayerHealth     health;
  public PlayerExperience experience;
  public PlayerStamina    stamina;
  public PlayerHotbar     hotbar;
  
  public BottomSegment(Player p)
  {
    health = new PlayerHealth(p);
    experience = new PlayerExperience(p);
    stamina = new PlayerStamina(p);
    hotbar = new PlayerHotbar(p);
  }
  
  public void update(long timePassed, Map m)
  {
    health.update(timePassed, m);
    experience.update(timePassed, m);
    stamina.update(timePassed, m);
    hotbar.update(timePassed, m);
  }
  
  public void draw(Graphics2D g, Map m)
  {
    if (m.talk == null)
    {
      experience.draw(g, m);
      health.draw(g, m);
      stamina.draw(g, m);
      hotbar.draw(g, m);
    }
  }
  
  public void mousePressed(MouseEvent e, Map m)
  {
    hotbar.mousePressed(e, m);
  }
  
  public void mouseReleased(MouseEvent e, Map m)
  {
    hotbar.mouseReleased(e, m);
  }
  
  public void mouseMoved(MouseEvent e, Map m)
  {
    hotbar.mouseMoved(e, m);
  }
  
  public void keyReleased(KeyEvent e, Map m)
  {
    hotbar.keyReleased(e, m);
  }
}
