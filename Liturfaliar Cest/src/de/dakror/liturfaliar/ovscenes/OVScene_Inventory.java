package de.dakror.liturfaliar.ovscenes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.event.dispatcher.ItemSlotEventDispatcher;
import de.dakror.liturfaliar.item.Categories;
import de.dakror.liturfaliar.item.Inventory;
import de.dakror.liturfaliar.scenes.Scene_Game;
import de.dakror.liturfaliar.settings.Attribute;
import de.dakror.liturfaliar.settings.Attributes;
import de.dakror.liturfaliar.settings.Attributes.Attr;
import de.dakror.liturfaliar.settings.Colors;
import de.dakror.liturfaliar.ui.Container;
import de.dakror.liturfaliar.ui.HTMLLabel;
import de.dakror.liturfaliar.ui.ItemSlot;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.Database;
import de.dakror.liturfaliar.util.Handler;

public class OVScene_Inventory extends OVScene implements Inventory
{
  public static final int WIDTH  = 12;
  public static final int HEIGHT = 8;
  
  Scene_Game              sg;
  Container               c1;
  
  ItemSlot[]              equipSlots;
  ItemSlot[]              inventory;
  
  ItemSlot                pickedUp;
  ItemSlot                pickUpSource;
  
  HTMLLabel               labels1, labels2;
  HTMLLabel               stats1, stats2;
  
  public OVScene_Inventory(Scene_Game sg)
  {
    sg.setPaused(true);
    this.sg = sg;
  }
  
  @Override
  public void init(Viewport v)
  {
    this.v = v;
    v.w.addKeyListener(this);
    v.w.addMouseListener(this);
    v.w.addMouseMotionListener(this);
    v.w.addMouseWheelListener(this);
    ItemSlotEventDispatcher.addItemSlotEventListener(this);
    c1 = new Container(0, 0, v.w.getWidth(), 55);
    c1.tileset = null;
    
    equipSlots = new ItemSlot[12];
    
    equipSlots[0] = new ItemSlot(183, 80); // helmet
    equipSlots[0].setCategoryFilter(Categories.HELMET);
    if (sg.getPlayer().getEquipment().hasEquipmentItem(Categories.HELMET))
      equipSlots[0].setItem(sg.getPlayer().getEquipment().getEquipmentItem(Categories.HELMET));
    
    equipSlots[1] = new ItemSlot(80, 160); // cape
    equipSlots[1].setCategoryFilter(Categories.CAPE);
    if (sg.getPlayer().getEquipment().hasEquipmentItem(Categories.CAPE))
      equipSlots[1].setItem(sg.getPlayer().getEquipment().getEquipmentItem(Categories.CAPE));
    
    equipSlots[2] = new ItemSlot(266, 210); // shoulder
    equipSlots[2].setCategoryFilter(Categories.SHOULDER);
    if (sg.getPlayer().getEquipment().hasEquipmentItem(Categories.SHOULDER))
      equipSlots[2].setItem(sg.getPlayer().getEquipment().getEquipmentItem(Categories.SHOULDER));
    
    equipSlots[3] = new ItemSlot(155, 240); // shirt
    equipSlots[3].setCategoryFilter(Categories.SHIRT);
    if (sg.getPlayer().getEquipment().hasEquipmentItem(Categories.SHIRT))
      equipSlots[3].setItem(sg.getPlayer().getEquipment().getEquipmentItem(Categories.SHIRT));
    
    equipSlots[4] = new ItemSlot(99, 240); // arm
    equipSlots[4].setCategoryFilter(Categories.ARM);
    if (sg.getPlayer().getEquipment().hasEquipmentItem(Categories.ARM))
      equipSlots[4].setItem(sg.getPlayer().getEquipment().getEquipmentItem(Categories.ARM));
    
    equipSlots[5] = new ItemSlot(210, 240); // armor
    equipSlots[5].setCategoryFilter(Categories.ARMOR);
    if (sg.getPlayer().getEquipment().hasEquipmentItem(Categories.ARMOR))
      equipSlots[5].setItem(sg.getPlayer().getEquipment().getEquipmentItem(Categories.ARMOR));
    
    equipSlots[6] = new ItemSlot(320, 270); // gloves
    equipSlots[6].setCategoryFilter(Categories.GLOVES);
    if (sg.getPlayer().getEquipment().hasEquipmentItem(Categories.GLOVES))
      equipSlots[6].setItem(sg.getPlayer().getEquipment().getEquipmentItem(Categories.GLOVES));
    
    equipSlots[7] = new ItemSlot(183, 305); // belt
    equipSlots[7].setCategoryFilter(Categories.BELT);
    if (sg.getPlayer().getEquipment().hasEquipmentItem(Categories.BELT))
      equipSlots[7].setItem(sg.getPlayer().getEquipment().getEquipmentItem(Categories.BELT));
    
    equipSlots[8] = new ItemSlot(75, 330); // left wpn
    equipSlots[8].setCategoryFilter(Categories.WEAPON);
    
    // TODO: for weapon equipment special care is needed (2-hand weapons, etc.)
    
    equipSlots[9] = new ItemSlot(290, 330); // right wpn
    equipSlots[9].setCategoryFilter(Categories.WEAPON);
    
    equipSlots[10] = new ItemSlot(183, 368); // pants
    equipSlots[10].setCategoryFilter(Categories.PANTS);
    if (sg.getPlayer().getEquipment().hasEquipmentItem(Categories.PANTS))
      equipSlots[10].setItem(sg.getPlayer().getEquipment().getEquipmentItem(Categories.PANTS));
    
    equipSlots[11] = new ItemSlot(182, 430); // boots
    equipSlots[11].setCategoryFilter(Categories.BOOTS);
    if (sg.getPlayer().getEquipment().hasEquipmentItem(Categories.BOOTS))
      equipSlots[11].setItem(sg.getPlayer().getEquipment().getEquipmentItem(Categories.BOOTS));
    
    for (ItemSlot is : equipSlots)
    {
      is.setInventory(this);
    }
    
    inventory = ItemSlot.createSlotGrid(0, 0, WIDTH, HEIGHT);
    
    ItemSlot.loadItemSlots(sg.getPlayer().getInventory(), inventory);
    
    for (ItemSlot is : inventory)
    {
      is.setInventory(this);
    }
    
    updateStats(true);
  }
  
  @Override
  public void update(long timePassed)
  {}
  
  @Override
  public void draw(Graphics2D g)
  {
    
    Assistant.Shadow(v.w.getBounds(), Color.black, 0.6f, g);
    c1.draw(g, v);
    Assistant.drawHorizontallyCenteredString("Inventar", v.w.getWidth(), 43, g, 45, Color.white);
    
    // -- inventory -- //
    Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), v.w.getWidth() / 2 - 190, v.w.getHeight() / 2 - 350, ItemSlot.SIZE * WIDTH + 20, 700, g, v.w);
    
    for (ItemSlot is : inventory)
    {
      is.draw(v.w.getWidth() / 2 - 180, v.w.getHeight() / 2 - 350 + 110, g, v);
    }
    
    // -- character equip -- //
    Assistant.stretchTileset(Viewport.loadImage("tileset/EmbededWood.png"), v.w.getWidth() / 2 - 600, v.w.getHeight() / 2 - 350, 410, 550, g, v.w);
    
    int h = 390;
    int w = (int) (h * (3 / 4.0)); // = 260
    
    g.drawImage(Viewport.loadImage("system/EquipGuy.png").getScaledInstance(w, h, Image.SCALE_SMOOTH), v.w.getWidth() / 2 - 600 + 410 / 2 - w / 2, v.w.getHeight() / 2 - 350 + 550 / 2 - h / 2 - 20, v.w);
    
    for (ItemSlot is : equipSlots)
    {
      is.draw(v.w.getWidth() / 2 - 600, v.w.getHeight() / 2 - 350, g, v);
    }
    
    // -- stats -- //
    Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), v.w.getWidth() / 2 - 600, v.w.getHeight() / 2 - 350 + 550, 410, 150, g, v.w);
    labels1.draw(g, v);
    stats1.draw(g, v);
    labels2.draw(g, v);
    stats2.draw(g, v);
    
    for (ItemSlot is : inventory)
    {
      is.drawTooltip(g, v);
    }
    
    for (ItemSlot is : equipSlots)
    {
      is.drawTooltip(g, v);
    }
    
    if (pickedUp != null)
      pickedUp.getItem().draw(g, v);
  }
  
  @Override
  public void keyReleased(KeyEvent e)
  {
    if (e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_I)
    {
      if (pickedUp != null)
        pickUpSource.setItem(pickedUp.getItem());
      
      sg.getPlayer().setInventory(ItemSlot.serializeItemSlots(inventory));
      
      ItemSlotEventDispatcher.removeItemSlotEventListener(this);
      v.removeOVScene("Inventory");
      sg.setPaused(false);
      v.setFramesFrozen(false);
      Handler.setListenerEnabled(sg, true);
    }
  }
  
  @Override
  public void mouseMoved(MouseEvent e)
  {
    for (ItemSlot slot : inventory)
    {
      slot.mouseMoved(e);
    }
    
    for (ItemSlot slot : equipSlots)
    {
      slot.mouseMoved(e);
    }
    
    if (pickedUp != null)
      pickedUp.mouseMoved(e);
  }
  
  @Override
  public void mousePressed(MouseEvent e)
  {
    for (ItemSlot slot : inventory)
    {
      slot.mousePressed(e);
    }
    
    for (ItemSlot slot : equipSlots)
    {
      slot.mousePressed(e);
    }
  }
  
  @Override
  public void slotPressed(MouseEvent e, ItemSlot slot)
  {
    if (slot.getCategoryFilter() != null) // is from equip menu
      sg.getPlayer().getEquipment().setEquipmentItem(slot.getItem().getType().getCategory(), null);
    
    
    pickedUp = new ItemSlot(slot);
    pickUpSource = slot;
    
    slot.setItem(null);
    
    updateStats(true);
  }
  
  @Override
  public void slotHovered(MouseEvent e, ItemSlot slot)
  {
    if (slot.getCategoryFilter() != null && pickedUp != null && slot.getCategoryFilter().equals(pickedUp.getItem().getType().getCategory())) // is from equip menu
    {
      Attributes attributes = pickedUp.getItem().getAttributes();
      
      if (slot.equals(pickedUp))
        return;
      
      Attributes player = sg.getPlayer().getAttributes(false);
      Attributes totalplayer = sg.getPlayer().getAttributes(true);
      
      for (Attr attr : Attr.values())
      {
        String color = "#ffffff";
        
        if (slot.getItem() == null)
        {
          if (player.getAttribute(attr).getValue() + attributes.getAttribute(attr).getValue() > player.getAttribute(attr).getValue())
            color = (attr.equals(Attr.weight)) ? Colors.WORSE : Colors.BETTER;
          else if (player.getAttribute(attr).getValue() + attributes.getAttribute(attr).getValue() < player.getAttribute(attr).getValue())
            color = (attr.equals(Attr.weight)) ? Colors.BETTER : Colors.WORSE;
        }
        else
        {
          if (attributes.getAttribute(attr).getValue() > slot.getItem().getAttributes().getAttribute(attr).getValue())
            color = (attr.equals(Attr.weight)) ? Colors.WORSE : Colors.BETTER;
          else if (attributes.getAttribute(attr).getValue() < slot.getItem().getAttributes().getAttribute(attr).getValue())
            color = (attr.equals(Attr.weight)) ? Colors.BETTER : Colors.WORSE;
        }
        
        Database.setStringVar("ov_inv_attr_color_" + attr.name(), color);
        Database.setStringVar("ov_inv_attr_" + attr.name(), Attribute.FORMAT.format(totalplayer.getAttribute(attr).getValue() + attributes.getAttribute(attr).getValue() - ((slot.getItem() != null) ? slot.getItem().getAttributes().getAttribute(attr).getValue() : 0.0)));
      }
      
      updateStats(false);
    }
  }
  
  @Override
  public void slotReleased(MouseEvent e, ItemSlot slot)
  {
    if (slot.getCategoryFilter() != null) // is from equip menu
      sg.getPlayer().getEquipment().setEquipmentItem(slot.getItem().getType().getCategory(), slot.getItem());
    
    updateStats(true);
  }
  
  public void updateStats(boolean force)
  {
    Attributes attributes = sg.getPlayer().getAttributes(true);
    
    String w = "<" + Assistant.ColorToHex(Colors.GRAY) + ";20;0>";
    String br = "[br]";
    
    if (force)
    {
      for (Attr attr : Attr.values())
      {
        Database.setStringVar("ov_inv_attr_color_" + attr.name(), "#ffffff");
        Database.setStringVar("ov_inv_attr_" + attr.name(), Attribute.FORMAT.format(attributes.getAttribute(attr).getValue()));
      }
    }
    String lb1 = w + Attr.protection.getText() + br +
    
    Attr.stamina.getText() + br +
    
    Attr.speed.getText() + br +
    
    Attr.attackspeed.getText() + br +
    
    Attr.weight.getText() + br;
    
    String lb2 = "";
    
    labels1 = new HTMLLabel(v.w.getWidth() / 2 - 590, v.w.getHeight() / 2 - 350 + 546, 130, 150, lb1);
    labels2 = new HTMLLabel(v.w.getWidth() / 2 - 590 + 205, v.w.getHeight() / 2 - 350 + 546, 130, 150, lb2);
    
    String st1 =
    
    w + ":<%ov_inv_attr_color_" + Attr.protection.name() + "%;20;1>%ov_inv_attr_" + Attr.protection.name() + "%" + br +
    
    w + ":<%ov_inv_attr_color_" + Attr.stamina.name() + "%;20;1>%ov_inv_attr_" + Attr.stamina.name() + "%" + br +
    
    w + ":<%ov_inv_attr_color_" + Attr.speed.name() + "%;20;1>%ov_inv_attr_" + Attr.speed.name() + "%" + br +
    
    w + ":<%ov_inv_attr_color_" + Attr.attackspeed.name() + "%;20;1>%ov_inv_attr_" + Attr.attackspeed.name() + "%" + br +
    
    w + ":<%ov_inv_attr_color_" + Attr.weight.name() + "%;20;1>%ov_inv_attr_" + Attr.weight.name() + "% kg" + br;
    
    
    String st2 = "";
    
    if (stats1 == null)
      stats1 = new HTMLLabel(v.w.getWidth() / 2 - 590 + 125, v.w.getHeight() / 2 - 350 + 546, 97, 150, st1);
    else stats1.doUpdate(st1);
    
    if (stats2 == null)
      stats2 = new HTMLLabel(v.w.getWidth() / 2 - 590 + 125 + 205, v.w.getHeight() / 2 - 350 + 546, 97, 150, st2);
    else stats2.doUpdate(st2);
  }
  
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
  public void slotExited(MouseEvent e, ItemSlot slot)
  {
    updateStats(true);
  }
}
