package de.dakror.liturfaliar.ui.hud;

import java.awt.Graphics2D;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.Player;
import de.dakror.liturfaliar.ui.ProgressBar;

public class PlayerHealth extends HUDComponent
{
  ProgressBar bar;
  Player      player;
  
  public PlayerHealth(Player p)
  {
    super(0, 0, 1, 1, 10);
    player = p;
  }
  
  @Override
  public void update(Map m)
  {
    visible = m.talk == null;
    
    if (visible)
      bar.value = player.getAttributes().getAttribute("health").getValue() / player.getAttributes().getAttribute("health").getMaximum();
  }
  
  @Override
  public void draw(Graphics2D g, Viewport v, Map m)
  {
    if (!visible && bar == null)
    {
      setX(v.w.getWidth() / 2 - PlayerHotbar.SLOTSIZE * PlayerHotbar.SLOTCOUNT / 2 - 3);
      setHeight(32);
      setY(v.w.getHeight() - height - PlayerHotbar.SLOTSIZE);
      setWidth(PlayerHotbar.SLOTSIZE * PlayerHotbar.SLOTCOUNT / 2 + 5);
      bar = new ProgressBar(x, y, width, 1, false, "ff3232", null, false);
      bar.setHeight(height);
      visible = true;
    }
    
    if (visible)
      bar.draw(g, v);
  }
}
