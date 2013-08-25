package de.dakror.liturfaliar.ui.hud;

import java.awt.Graphics2D;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.Player;
import de.dakror.liturfaliar.settings.Attributes.Attr;
import de.dakror.liturfaliar.ui.ItemSlot;
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
  public void update(long timePassed, Map m)
  {
    if (visible) bar.value = (float) (player.getAttributes().getAttribute(Attr.health).getValue() / player.getAttributes().getAttribute(Attr.health).getMaximum());
  }
  
  @Override
  public void draw(Graphics2D g, Map m)
  {
    if (!visible && bar == null)
    {
      setX(Viewport.w.getWidth() / 2 - ItemSlot.SIZE * PlayerHotbar.SLOTCOUNT / 2 - 3);
      setHeight(32);
      setY(Viewport.w.getHeight() - height - ItemSlot.SIZE + 5 - 13);
      setWidth(ItemSlot.SIZE * PlayerHotbar.SLOTCOUNT / 2 + 10);
      bar = new ProgressBar(x, y, width, 1, false, "ff3232", null, false);
      bar.setHeight(height);
      visible = true;
    }
    
    if (visible) bar.draw(g);
  }
}
