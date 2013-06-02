package de.dakror.liturfaliar.ui.hud;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.ui.ItemSlot;

public class PlayerHotbar extends HUDComponent
{
  public static final int[] KEYSLOTS   = { KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3, KeyEvent.VK_4, KeyEvent.VK_Q, KeyEvent.VK_E, KeyEvent.VK_R, KeyEvent.VK_H };
  public static final int[] MOUSESLOTS = { MouseEvent.BUTTON1, MouseEvent.BUTTON3 };
  
  public static final int   SLOTCOUNT  = KEYSLOTS.length + MOUSESLOTS.length;
  
  ItemSlot[]                slots;
  
  public PlayerHotbar()
  {
    super(0, 0, ItemSlot.SIZE * SLOTCOUNT, ItemSlot.SIZE, 10);
    
    // slots[0] = new Item(Items.POCKETKNIFE);
  }
  
  @Override
  public void update(Map m)
  {
    visible = m.talk == null;
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
}
