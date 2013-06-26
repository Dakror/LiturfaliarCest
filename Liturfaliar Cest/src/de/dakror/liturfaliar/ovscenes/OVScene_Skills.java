package de.dakror.liturfaliar.ovscenes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.event.dispatcher.ItemSlotEventDispatcher;
import de.dakror.liturfaliar.item.Inventory;
import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.item.Items;
import de.dakror.liturfaliar.item.Types;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.scenes.Scene_Game;
import de.dakror.liturfaliar.settings.Attributes.Attr;
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
  
  ItemSlot             pickedUp;
  ItemSlot             pickUpSource;
  
  public OVScene_Skills(Scene_Game sg)
  {
    sg.setPaused(true);
    this.sg = sg;
  }
  
  @Override
  public void construct(Viewport v)
  {
    this.v = v;
    ItemSlotEventDispatcher.addItemSlotEventListener(this);
    c1 = new Container(0, 0, v.w.getWidth(), 55);
    c1.tileset = null;
    
    hotbar = ItemSlot.createSlotRow(v.w.getWidth() / 2 - PlayerHotbar.SLOTCOUNT * ItemSlot.SIZE / 2, v.w.getHeight() - ItemSlot.SIZE, PlayerHotbar.SLOTCOUNT);
    
    for (int i = 0; i < hotbar.length; i++)
    {
      hotbar[i].setInventory(this);
      hotbar[i].setItem(sg.getPlayer().getEquipment().getHotbarItem(i));
      hotbar[i].setHotKey((i < PlayerHotbar.KEYSLOTS.length) ? PlayerHotbar.KEYSLOTS[i] : PlayerHotbar.MOUSESLOTS[i - PlayerHotbar.KEYSLOTS.length], i > PlayerHotbar.KEYSLOTS.length - 1);
    }
    int h = 96 - 36;
    flicker = new Flicker(v.w.getWidth() / 2 - 400, v.w.getHeight() - 200, 800, 96, new FlickObject(9, 8, h, Types.PERKSKILL.getName()), new FlickObject(1, 0, h, Types.SWORDSKILL.getName()), new FlickObject(0, 253, h, Types.BOWSKILL.getName()));
  }
  
  @Override
  public void destruct()
  {
    ItemSlotEventDispatcher.removeItemSlotEventListener(this);
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
      ss.drawArrows(g, v);
    }
    
    for (SkillSlot ss : slots)
    {
      ss.draw(g, v);
    }
    
    // -- skillpoints -- //
    Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), v.w.getWidth() / 2 + 400, 100, 192, 96, g, v.w);
    String string = (int) sg.getPlayer().getAttributes().getAttribute(Attr.skillpoint).getValue() + " SP";
    int size = 70;
    Font oldFont = g.getFont();
    g.setFont(new Font("Times New Roman", 0, 1));
    if (g.getFontMetrics(g.getFont().deriveFont(size)).stringWidth(string) > 50)
      size -= g.getFontMetrics(g.getFont().deriveFont(size)).stringWidth(string) - 50;
    
    Assistant.drawHorizontallyCenteredString(string, v.w.getWidth() / 2 + 410, 172, 174, g, size, Color.white);
    g.setFont(oldFont);
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
    
    if (pickedUp != null && pickedUp instanceof SkillSlot)
    {
      ((SkillSlot) pickedUp).getItem().draw(g, v);
    }
  }
  
  public void loadSkillTree()
  {
    int x = v.w.getWidth() / 2 - 400;
    
    int y = 100 + 50;
    int width = 800;
    // int height = v.w.getHeight() - 300;
    int cx = x + width / 2 - SkillSlot.SIZE / 2;
    
    slots.clear();
    
    String key = flicker.getSelectedObject().getKey();
    if (key.equals(Types.SWORDSKILL.getName()))
    {
      SkillSlot s = new SkillSlot(cx, y, new Item(Items.SWORD0, 1), sg);
      slots.add(s);
      
      SkillSlot s1 = new SkillSlot(cx - SkillSlot.HGAP * 3, y, new Item(Items.SWORD1, 1), sg);
      slots.add(s1);
      s1.setParents(s);
      
      SkillSlot s2 = new SkillSlot(cx - SkillSlot.HGAP * 3, y + SkillSlot.VGAP, new Item(Items.SWORD2, 1), sg);
      slots.add(s2);
      s2.setParents(s1);
      
      SkillSlot s3 = new SkillSlot(cx - SkillSlot.HGAP * 3, y + SkillSlot.VGAP * 2, new Item(Items.SWORD3, 1), sg);
      slots.add(s3);
      s3.setParents(s2);
    }
  }
  
  @Override
  public void slotPressed(MouseEvent e, ItemSlot slot)
  {
    if (slot instanceof SkillSlot)
      pickedUp = new SkillSlot((SkillSlot) slot);
  }
  
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
    return pickedUp;
  }
  
  @Override
  public void setPickedUpItemSlot(ItemSlot item)
  {
    pickedUp = item;
  }
  
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
    
    if(pickedUp != null && pickedUp instanceof SkillSlot) {
      ((SkillSlot)pickedUp).getItem().mouseMoved(e);
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
