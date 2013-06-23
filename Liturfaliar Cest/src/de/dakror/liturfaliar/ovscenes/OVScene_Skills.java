package de.dakror.liturfaliar.ovscenes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.item.Inventory;
import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.item.Items;
import de.dakror.liturfaliar.item.Types;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.scenes.Scene_Game;
import de.dakror.liturfaliar.ui.Container;
import de.dakror.liturfaliar.ui.Flicker;
import de.dakror.liturfaliar.ui.Flicker.FlickObject;
import de.dakror.liturfaliar.ui.ItemSlot;
import de.dakror.liturfaliar.ui.SkillSlot;
import de.dakror.liturfaliar.ui.hud.PlayerHotbar;
import de.dakror.liturfaliar.util.Assistant;

public class OVScene_Skills extends OVScene implements Inventory
{
  Scene_Game           sg;
  Container            c1;
  int                  lastSelectedTree = -1;
  Flicker              flicker;
  
  ItemSlot[]           hotbar;
  ArrayList<SkillSlot> slots            = new ArrayList<SkillSlot>();
  
  int                  rX;
  int                  rY;
  
  public OVScene_Skills(Scene_Game sg)
  {
    sg.setPaused(true);
    this.sg = sg;
  }
  
  @Override
  public void init(Viewport v)
  {
    this.v = v;
    c1 = new Container(0, 0, v.w.getWidth(), 55);
    c1.tileset = null;
    
    hotbar = ItemSlot.createSlotRow(v.w.getWidth() / 2 - PlayerHotbar.SLOTCOUNT * ItemSlot.SIZE / 2, v.w.getHeight() - ItemSlot.SIZE, PlayerHotbar.SLOTCOUNT);
    
    for (int i = 0; i < hotbar.length; i++)
    {
      hotbar[i].setInventory(this);
      hotbar[i].setItem(sg.getPlayer().getEquipment().getHotbarItem(i));
      hotbar[i].setHotKey((i < PlayerHotbar.KEYSLOTS.length) ? PlayerHotbar.KEYSLOTS[i] : PlayerHotbar.MOUSESLOTS[i - PlayerHotbar.KEYSLOTS.length], i > PlayerHotbar.KEYSLOTS.length - 1);
    }
    int h = 100 - 36;
    flicker = new Flicker(v.w.getWidth() / 2 - 400, v.w.getHeight() - 200, 800, 100, new FlickObject(9, 8, h, Types.PERKSKILL.getName()), new FlickObject(1, 0, h, Types.SWORDSKILL.getName()), new FlickObject(0, 253, h, Types.BOWSKILL.getName()));
  }
  
  @Override
  public void update(long timePassed)
  {
    flicker.update();
    
    if (flicker.getSelectedIndex() != lastSelectedTree)
    {
      lastSelectedTree = flicker.getSelectedIndex();
      loadSkillTree();
    }
  }
  
  @Override
  public void draw(Graphics2D g)
  {
    Assistant.Shadow(v.w.getBounds(), Color.black, 0.6f, g);
    c1.draw(g, v);
    Assistant.drawHorizontallyCenteredString("Fähigkeiten", v.w.getWidth(), 43, g, 45, Color.white);
    
    // -- tree area -- //
    Assistant.stretchTileset(Viewport.loadImage("tileset/EmbededWood.png"), v.w.getWidth() / 2 - 400, 100, 800, v.w.getHeight() - 300, g, v.w);
    for (SkillSlot ss : slots)
    {
      ss.draw(g, v);
    }
    
    // -- type area -- //
    flicker.draw(g, v);
    
    // -- hotbar -- //
    for (ItemSlot is : hotbar)
    {
      is.draw(g, v);
    }
    
    // -- tooltips -- //
    for (SkillSlot is : slots)
    {
      is.getItem().tooltip.draw(g, v);
    }
    for (ItemSlot is : hotbar)
    {
      is.drawTooltip(g, v);
    }
  }
  
  public void loadSkillTree()
  {
    int x = v.w.getWidth() / 2 - 400 + 50;
    int y = 100 + 50;
    
    slots.clear();
    
    String key = flicker.getSelectedObject().getKey();
    if (key.equals(Types.SWORDSKILL.getName()))
    {
      SkillSlot s = new SkillSlot(x, y, new Item(Items.SWORD0, 1));
      SkillSlot s1 = new SkillSlot(x, y + SkillSlot.VGAP, new Item(Items.SWORD1, 1));
      SkillSlot s2 = new SkillSlot(x + SkillSlot.HGAP, y + SkillSlot.VGAP, new Item(Items.SWORD1, 1));
      s.setChildren(s1, s2);
      slots.add(s);
      slots.add(s1);
      slots.add(s2);
    }
  }
  
  @Override
  public void slotPressed(MouseEvent e, ItemSlot slot)
  {}
  
  @Override
  public void slotExited(MouseEvent e, ItemSlot slot)
  {}
  
  @Override
  public void slotHovered(MouseEvent e, ItemSlot slot)
  {}
  
  @Override
  public void slotReleased(MouseEvent e, ItemSlot slot)
  {}
  
  @Override
  public ItemSlot getPickedUpItemSlot()
  {
    return null;
  }
  
  @Override
  public void setPickedUpItemSlot(ItemSlot item)
  {}
  
  @Override
  public ItemSlot getFirstSlot(Item item)
  {
    return null;
  }
  
  @Override
  public Map getMap()
  {
    return null;
  }
  
  @Override
  public void showContextMenu(ItemSlot slot, int x, int y)
  {}
  
  @Override
  public void hideContextMenu()
  {}
  
  @Override
  public void keyReleased(KeyEvent e)
  {
    if (e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_J)
    {
      sg.setPaused(false);
      v.setFramesFrozen(false);
      v.removeOVScene("Skills");
      v.skipEvent = e;
    }
  }
  
  @Override
  public void mouseMoved(MouseEvent e)
  {
    for (ItemSlot slot : hotbar)
    {
      slot.mouseMoved(e);
    }
    
    for (SkillSlot slot : slots)
    {
      slot.mouseMoved(e);
    }
  }
  
  @Override
  public void mousePressed(MouseEvent e)
  {
    for (ItemSlot slot : hotbar)
    {
      slot.mousePressed(e);
    }
    
    
    for (SkillSlot slot : slots)
    {
      slot.mousePressed(e);
    }
    
    flicker.mousePressed(e);
  }
  
  @Override
  public void mouseReleased(MouseEvent e)
  {
    for (ItemSlot slot : hotbar)
    {
      slot.mouseReleased(e);
    }
    
    for (SkillSlot slot : slots)
    {
      slot.mouseReleased(e);
    }
    
    flicker.mouseReleased(e);
  }
  
  @Override
  public void mouseDragged(MouseEvent e)
  {
    for (ItemSlot slot : hotbar)
    {
      slot.mouseDragged(e);
    }
  }
}
