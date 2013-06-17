package de.dakror.liturfaliar.ui.hud;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.event.dispatcher.PlayerHotbarEventDispatcher;
import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.Player;
import de.dakror.liturfaliar.ui.ItemSlot;

public class PlayerHotbar extends HUDComponent
{
  public static final Integer[] KEYSLOTS   = { KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3, KeyEvent.VK_4, KeyEvent.VK_Q, KeyEvent.VK_E, KeyEvent.VK_R, KeyEvent.VK_F };
  public static final Integer[] MOUSESLOTS = { MouseEvent.BUTTON1, MouseEvent.BUTTON3 };
  
  public static final int       SLOTCOUNT  = KEYSLOTS.length + MOUSESLOTS.length;
  
  ItemSlot[]                    slots;
  
  Player                        player;
  
  public PlayerHotbar(Player p)
  {
    super(0, 0, ItemSlot.SIZE * SLOTCOUNT, ItemSlot.SIZE, 10);
    player = p;
  }
  
  @Override
  public void update(Map m)
  {
    visible = m.talk == null;
    
    for (int i = 0; i < SLOTCOUNT; i++)
    {
      Item eItem = player.getEquipment().getHotbarItem(i);
      if (eItem != null)
        slots[i].setItem(new Item(eItem));
      else slots[i].setItem(eItem);
    }
  }
  
  @Override
  public void draw(Graphics2D g, Viewport v, Map m)
  {
    if (!visible)
    {
      setX(v.w.getWidth() / 2 - width / 2);
      setY(v.w.getHeight() - ItemSlot.SIZE);
      
      slots = ItemSlot.createSlotRow(x, y, 10);
      
      for (int i = 0; i < SLOTCOUNT; i++)
      {
        slots[i].setOnlyLabel(true);
        slots[i].setItem(player.getEquipment().getHotbarItem(i));
        slots[i].setHotKey((i < KEYSLOTS.length) ? KEYSLOTS[i] : MOUSESLOTS[i - KEYSLOTS.length], i > KEYSLOTS.length - 1);
      }
      visible = true;
    }
    
    if (visible)
    {
      for (int i = 0; i < slots.length; i++)
        if (slots[i] != null)
          slots[i].draw(g, v);
    }
  }
  
  @Override
  public void keyPressed(KeyEvent e, Map m)
  {
    List<Integer> slots = Arrays.asList(KEYSLOTS);
    if (slots.contains(e.getKeyCode()))
      PlayerHotbarEventDispatcher.dispatchSlotTriggered(slots.indexOf(e.getKeyCode()), this.slots[slots.indexOf(e.getKeyCode())]);
  }
  
  @Override
  public void mousePressed(MouseEvent e, Map m)
  {
    List<Integer> slots = Arrays.asList(MOUSESLOTS);
    if (slots.contains(e.getButton()))
      PlayerHotbarEventDispatcher.dispatchSlotTriggered(slots.indexOf(e.getButton()) + KEYSLOTS.length, this.slots[slots.indexOf(e.getButton()) + KEYSLOTS.length]);
    
  }
}
