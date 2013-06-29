package de.dakror.liturfaliar.ui.hud;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.Player;
import de.dakror.liturfaliar.settings.Attributes.Attr;
import de.dakror.liturfaliar.ui.ItemSlot;
import de.dakror.liturfaliar.ui.ProgressBar;

public class PlayerStamina extends HUDComponent
{
  ProgressBar bar;
  Player      player;
  
  float       alpha;
  long        time;
  
  final float ALPHA = 0.03f;
  final int   TIME  = 200;
  
  public PlayerStamina(Player p)
  {
    super(0, 0, 1, 1, 10);
    player = p;
    
    alpha = 0;
  }
  
  @Override
  public void update(long timePassed, Map m)
  {
    visible = m.talk == null;
    
    if (visible)
      bar.value = (float) (player.getAttributes().getAttribute(Attr.stamina).getValue() / (float) player.getAttributes().getAttribute(Attr.stamina).getMaximum());
    
    if (bar.value == 1)
    {
      if (time == 0)
        time = System.currentTimeMillis();
      
      if (System.currentTimeMillis() - time >= TIME && alpha > 0)
        alpha -= (alpha - ALPHA > 0) ? ALPHA : alpha;
    }
    else
    {
      time = 0;
      alpha = 1.0f;
    }
  }
  
  @Override
  public void draw(Graphics2D g, Viewport v, Map m)
  {
    if (!visible && bar == null)
    {
      if (player.getAttributes().getAttribute(Attr.stamina).getMaximum() == -1)
        return;
      
      setX(v.w.getWidth() / 2 - ItemSlot.SIZE * PlayerHotbar.SLOTCOUNT / 2 - 3);
      setHeight(32);
      setY(v.w.getHeight() - height - ItemSlot.SIZE - 25);
      setWidth(ItemSlot.SIZE * PlayerHotbar.SLOTCOUNT / 2 + 5);
      bar = new ProgressBar(x, y, width, 1, false, "ffc744", null, false);
      bar.setHeight(height);
      visible = true;
    }
    
    if (visible)
    {
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
      bar.draw(g, v);
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }
  }
}
