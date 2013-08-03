package de.dakror.liturfaliar.ui.hud;

import java.awt.Graphics2D;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.Player;
import de.dakror.liturfaliar.settings.Attributes.Attr;
import de.dakror.liturfaliar.ui.ItemSlot;
import de.dakror.liturfaliar.ui.ProgressBar;

public class PlayerExperience extends HUDComponent
{
  ProgressBar bar;
  Player      player;
  
  public PlayerExperience(Player p)
  {
    super(0, 0, 1, 1, 10);
    player = p;
  }
  
  @Override
  public void update(long timePassed, Map m)
  {
    if (visible) bar.value = (float) ((player.getAttributes().getAttribute(Attr.experience).getValue() - player.getXPforLevel(0)) / (float) (player.getXPforLevel(1) - player.getXPforLevel(0)));
  }
  
  @Override
  public void draw(Graphics2D g, Viewport v, Map m)
  {
    if (!visible && bar == null)
    {
      setX(v.w.getWidth() / 2 - ItemSlot.SIZE * PlayerHotbar.SLOTCOUNT / 2 - 3);
      setHeight(20);
      setY(v.w.getHeight() - height - 12 - ItemSlot.SIZE + 5 + 11);
      setWidth(ItemSlot.SIZE * PlayerHotbar.SLOTCOUNT + 6);
      bar = new ProgressBar(x, y, width, 0, false, "7dd33c", null, false);
      bar.setHeight(height);
      visible = true;
    }
    
    if (visible) bar.draw(g, v);
  }
}
