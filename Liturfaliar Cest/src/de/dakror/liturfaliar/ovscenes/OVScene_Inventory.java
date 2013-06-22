package de.dakror.liturfaliar.ovscenes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.event.dispatcher.ItemSlotEventDispatcher;
import de.dakror.liturfaliar.item.Categories;
import de.dakror.liturfaliar.item.Inventory;
import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.item.Items;
import de.dakror.liturfaliar.item.Types;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.scenes.Scene_Game;
import de.dakror.liturfaliar.settings.Attribute;
import de.dakror.liturfaliar.settings.Attributes;
import de.dakror.liturfaliar.settings.Attributes.Attr;
import de.dakror.liturfaliar.settings.Colors;
import de.dakror.liturfaliar.ui.Container;
import de.dakror.liturfaliar.ui.Dialog;
import de.dakror.liturfaliar.ui.HTMLLabel;
import de.dakror.liturfaliar.ui.ItemSlot;
import de.dakror.liturfaliar.ui.Notification;
import de.dakror.liturfaliar.ui.TextSelect;
import de.dakror.liturfaliar.ui.hud.PlayerHotbar;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.Database;

public class OVScene_Inventory extends OVScene implements Inventory
{
  public static final String USEITEM   = "Benutzen";
  public static final String TRASHITEM = "Verschrotten";
  public static final String THROWITEM = "Wegwerfen";
  
  public static final int    WIDTH     = 12;
  public static final int    HEIGHT    = 11;
  
  boolean                    showTrashDialog;
  
  Scene_Game                 sg;
  Container                  c1;
  
  ItemSlot[]                 equipSlots;
  ItemSlot[]                 inventory;
  ItemSlot[]                 hotbar;
  
  ItemSlot                   pickedUp;
  ItemSlot                   pickUpSource;
  
  HTMLLabel                  labels1, labels2;
  HTMLLabel                  stats1, stats2;
  HTMLLabel                  invWeight;
  
  TextSelect                 contextMenu;
  ItemSlot                   contextItemSlot;
  
  public OVScene_Inventory(Scene_Game sg)
  {
    sg.setPaused(true);
    this.sg = sg;
  }
  
  @Override
  public void init(Viewport v)
  {
    this.v = v;
    ItemSlotEventDispatcher.addItemSlotEventListener(this);
    c1 = new Container(0, 0, v.w.getWidth(), 55);
    c1.tileset = null;
    
    for (Attr attr : Attr.values())
    {
      Database.setStringVar("ov_inv_attr_" + attr.name(), Attribute.FORMAT.format(sg.getPlayer().getAttributes(true).getAttribute(attr).getValue()));
    }
    
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
    
    inventory = ItemSlot.createSlotGrid(v.w.getWidth() / 2 - 180, v.w.getHeight() / 2 - 350 + 110 - 52, WIDTH, HEIGHT);
    
    hotbar = ItemSlot.createSlotRow(v.w.getWidth() / 2 - PlayerHotbar.SLOTCOUNT * ItemSlot.SIZE / 2, v.w.getHeight() - ItemSlot.SIZE, PlayerHotbar.SLOTCOUNT);
    
    for (ItemSlot is : equipSlots)
    {
      is.setInventory(this);
    }
    
    ItemSlot.loadItemSlots(sg.getPlayer().getInventory(), inventory);
    
    for (ItemSlot is : inventory)
    {
      is.setInventory(this);
    }
    
    for (int i = 0; i < hotbar.length; i++)
    {
      hotbar[i].setInventory(this);
      hotbar[i].setItem(sg.getPlayer().getEquipment().getHotbarItem(i));
      hotbar[i].setHotKey((i < PlayerHotbar.KEYSLOTS.length) ? PlayerHotbar.KEYSLOTS[i] : PlayerHotbar.MOUSESLOTS[i - PlayerHotbar.KEYSLOTS.length], i > PlayerHotbar.KEYSLOTS.length - 1);
    }
    
    updateStats(true);
  }
  
  @Override
  public void update(long timePassed)
  {
    if (Viewport.dialog != null && Viewport.dialog.buttons.length > 0)
    {
      if (Viewport.dialog.buttons[0].getState() == 1)
      {
        trashItem();
        Viewport.dialog = null;
      }
      else if (Viewport.dialog.buttons[1].getState() == 1)
      {
        Viewport.dialog = null;
      }
    }
    
    if (contextMenu != null)
    {
      contextMenu.update();
      
      if (contextMenu.getSelected(false) != null)
      {
        switch (contextMenu.getSelected(false))
        {
          case USEITEM:
          {
            contextItemSlot.getItem().triggerAction(getMap(), v);
            break;
          }
          case TRASHITEM:
          {
            if (contextItemSlot.getCategoryFilter() != null)
            {
              Viewport.notification = new Notification("Ausgerüstete Items können nicht\n\nverschrottet werden!", Notification.ERROR);
              break;
            }
            showTrashDialog = true;
            break;
          }
          case THROWITEM:
          {
            int[] pos = sg.getPlayer().getRelativePos(sg.getMapPack().getActiveMap());
            int ran = 16;
            while (contextItemSlot.getItem() != null)
            {
              int rx = (int) Math.round(Math.random() * ran) - ran / 2;
              int ry = (int) Math.round(Math.random() * ran) - ran / 2;
              
              Item item = new Item(contextItemSlot.getItem());
              item.setStack(1);
              
              sg.getMapPack().getActiveMap().addItemDrop(item, pos[0] + rx, pos[1] + ry);
              contextItemSlot.subItem();
            }
            break;
          }
        }
        contextMenu = null;
      }
    }
  }
  
  @Override
  public void draw(Graphics2D g)
  {
    if (showTrashDialog)
    {
      Viewport.dialog = new Dialog("Verschrotten", "Bist du sicher, dass du diesen Gegenstand verschrotten möchtest?[br]Diese Aktion kann nicht rückgängig gemacht werden!", Dialog.MESSAGE);
      Viewport.dialog.closeDisabled = true;
      Viewport.dialog.freezeOVScene = true;
      Viewport.dialog.draw(g, v);
      Viewport.dialog.setButtons("Ja", "Nein");
      Viewport.dialog.update();
      showTrashDialog = false;
    }
    Assistant.Shadow(v.w.getBounds(), Color.black, 0.6f, g);
    c1.draw(g, v);
    Assistant.drawHorizontallyCenteredString("Inventar", v.w.getWidth(), 43, g, 45, Color.white);
    
    // -- inventory -- //
    Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), v.w.getWidth() / 2 - 190, v.w.getHeight() / 2 - 350, ItemSlot.SIZE * WIDTH + 20, 700, g, v.w);
    
    for (ItemSlot is : inventory)
    {
      is.draw(g, v);
    }
    
    invWeight.draw(g, v);
    
    // -- character equip -- //
    Assistant.stretchTileset(Viewport.loadImage("tileset/EmbededWood.png"), v.w.getWidth() / 2 - 600, v.w.getHeight() / 2 - 350, 410, 550, g, v.w);
    
    g.drawImage(Viewport.loadScaledImage("system/EquipGuy.png", 292, 390), v.w.getWidth() / 2 - 541, v.w.getHeight() / 2 - 290, v.w);
    
    for (ItemSlot is : equipSlots)
    {
      is.draw(v.w.getWidth() / 2 - 600, v.w.getHeight() / 2 - 350, g, v);
    }
    
    // -- hotbar -- //
    
    for (ItemSlot is : hotbar)
    {
      is.draw(g, v);
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
    
    for (ItemSlot is : hotbar)
    {
      is.drawTooltip(g, v);
    }
    if (pickedUp != null)
      pickedUp.drawLightWeight(g, v);
    
    if (contextMenu != null)
      contextMenu.draw(g, v);
  }
  
  public void trashItem()
  {
    Item scrap = new Item(Items.SCRAP, 1);
    ItemSlot scrapSlot = null;
    ItemSlot nullSlot = getFirstSlot(null);
    
    for (ItemSlot is : inventory)
    {
      if (is.getItem() == null)
        continue;
      if (is.getItem().equals(scrap) && is.getItem().getStack() + 1 <= scrap.getType().getStackSize())
      {
        scrapSlot = is;
        break;
      }
    }
    
    if (scrapSlot != null)
    {
      scrapSlot.addItem();
      contextItemSlot.subItem();
    }
    else if (nullSlot != null)
    {
      nullSlot.setItem(scrap);
      contextItemSlot.subItem();
    }
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
      sg.setPaused(false);
      v.setFramesFrozen(false);
      v.removeOVScene("Inventory");
      v.skipEvent = e;
    }
  }
  
  @Override
  public void mouseMoved(MouseEvent e)
  {
    if (contextMenu != null)
    {
      contextMenu.mouseMoved(e);
      return;
    }
    
    for (ItemSlot slot : inventory)
    {
      slot.mouseMoved(e);
    }
    
    for (ItemSlot slot : equipSlots)
    {
      slot.mouseMoved(e);
    }
    
    for (ItemSlot slot : hotbar)
    {
      slot.mouseMoved(e);
    }
    
    if (pickedUp != null)
      pickedUp.mouseMoved(e);
  }
  
  @Override
  public void mousePressed(MouseEvent e)
  {
    
    if (contextMenu != null && contextMenu.getArea().contains(e.getPoint()))
    {
      contextMenu.mousePressed(e);
      return;
    }
    
    for (ItemSlot slot : inventory)
    {
      slot.mousePressed(e);
    }
    
    for (ItemSlot slot : equipSlots)
    {
      slot.mousePressed(e);
    }
    
    for (ItemSlot slot : hotbar)
    {
      slot.mousePressed(e);
    }
    
    if (contextMenu != null && e.getButton() == 1)
      contextMenu = null;
  }
  
  @Override
  public void mouseReleased(MouseEvent e)
  {
    if (contextMenu != null)
    {
      contextMenu.mouseReleased(e);
      return;
    }
    
    for (ItemSlot slot : inventory)
    {
      slot.mouseReleased(e);
    }
    
    for (ItemSlot slot : equipSlots)
    {
      slot.mouseReleased(e);
    }
    
    for (ItemSlot slot : hotbar)
    {
      slot.mouseReleased(e);
    }
  }
  
  @Override
  public void mouseDragged(MouseEvent e)
  {
    if (contextMenu != null)
    {
      contextMenu.mouseDragged(e);
      return;
    }
    
    for (ItemSlot slot : inventory)
    {
      slot.mouseDragged(e);
    }
    
    for (ItemSlot slot : equipSlots)
    {
      slot.mouseDragged(e);
    }
    
    for (ItemSlot slot : hotbar)
    {
      slot.mouseDragged(e);
    }
  }
  
  @Override
  public void slotPressed(MouseEvent e, ItemSlot slot)
  {
    if (slot.getCategoryFilter() != null && !slot.isOnlyLabel()) // is from equip menu
      sg.getPlayer().getEquipment().setEquipmentItem(slot.getItem().getType().getCategory(), null);
    
    else if (slot.hasHotKey()) // is from hotbar
      sg.getPlayer().getEquipment().setHotbarItem(Arrays.asList(hotbar).indexOf(slot), null);
    
    pickedUp = new ItemSlot(slot);
    pickedUp.setHotKey(-1, true);
    pickUpSource = slot;
    
    slot.setItem(null);
    
    updateStats(true);
    
    for (ItemSlot is : equipSlots)
    {
      if (is.getItem() != null && !is.getItem().areRequirementsSatisfied(Attributes.dif(sg.getPlayer().getAttributes(true), is.getItem().getAttributes())))
      {
        sg.getPlayer().getEquipment().setEquipmentItem(is.getItem().getType().getCategory(), null);
        getFirstSlot(null).setItem(is.getItem());
        is.setItem(null);
        
        updateStats(true);
      }
    }
  }
  
  @Override
  public void slotHovered(MouseEvent e, ItemSlot slot)
  {
    if (slot.getCategoryFilter() != null && pickedUp != null && slot.getCategoryFilter().equals(pickedUp.getItem().getType().getCategory()) && !slot.isOnlyLabel()) // is from equip menu
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
        Database.setStringVar("ov_inv_attr_display_" + attr.name(), Attribute.FORMAT.format(totalplayer.getAttribute(attr).getValue() + attributes.getAttribute(attr).getValue() - ((slot.getItem() != null) ? slot.getItem().getAttributes().getAttribute(attr).getValue() : 0.0)));
      }
      
      updateStats(false);
    }
  }
  
  @Override
  public void slotReleased(MouseEvent e, ItemSlot slot)
  {
    if (slot.getCategoryFilter() != null && !slot.isOnlyLabel()) // is from equip menu
      sg.getPlayer().getEquipment().setEquipmentItem(slot.getItem().getType().getCategory(), slot.getItem());
    
    else if (slot.hasHotKey()) // is from hotbar
      sg.getPlayer().getEquipment().setHotbarItem(Arrays.asList(hotbar).indexOf(slot), slot.getItem());
    
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
        Database.setStringVar("ov_inv_attr_display_" + attr.name(), Attribute.FORMAT.format(attributes.getAttribute(attr).getValue()));
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
    
    w + " <%ov_inv_attr_color_" + Attr.protection.name() + "%;20;1>%ov_inv_attr_display_" + Attr.protection.name() + "%" + br +
    
    w + " <%ov_inv_attr_color_" + Attr.stamina.name() + "%;20;1>%ov_inv_attr_display_" + Attr.stamina.name() + "%" + br +
    
    w + " <%ov_inv_attr_color_" + Attr.speed.name() + "%;20;1>%ov_inv_attr_display_" + Attr.speed.name() + "%" + br +
    
    w + " <%ov_inv_attr_color_" + Attr.attackspeed.name() + "%;20;1>%ov_inv_attr_display_" + Attr.attackspeed.name() + "%" + br +
    
    w + " <%ov_inv_attr_color_" + Attr.weight.name() + "%;20;1>%ov_inv_attr_display_" + Attr.weight.name() + "% kg" + br;
    
    
    String st2 = "";
    
    if (stats1 == null)
      stats1 = new HTMLLabel(v.w.getWidth() / 2 - 590 + 125, v.w.getHeight() / 2 - 350 + 546, 97, 150, st1);
    else stats1.doUpdate(st1);
    
    if (stats2 == null)
      stats2 = new HTMLLabel(v.w.getWidth() / 2 - 590 + 125 + 205, v.w.getHeight() / 2 - 350 + 546, 97, 150, st2);
    else stats2.doUpdate(st2);
    
    if (invWeight == null)
      invWeight = new HTMLLabel(v.w.getWidth() / 2 - 175, v.w.getHeight() / 2 - 350 + 110 - 52 + HEIGHT * ItemSlot.SIZE - 7, 160, 30, w + "Gewicht: <#ffffff;20;1>" + Attribute.FORMAT.format(getInventoryWeight()) + " kg[br]");
    else invWeight.doUpdate(w + "Gewicht: <#ffffff;20;1>" + Attribute.FORMAT.format(getInventoryWeight()) + " kg[br]");
    
    for (ItemSlot is : equipSlots)
    {
      if (is.getItem() != null)
        is.getItem().updateTooltip();
    }
    for (ItemSlot is : inventory)
    {
      if (is.getItem() != null)
        is.getItem().updateTooltip();
    }
  }
  
  public double getInventoryWeight()
  {
    double w = 0.0;
    
    for (ItemSlot is : inventory)
    {
      w += is.getWeight();
    }
    
    return w;
  }
  
  @Override
  public ItemSlot getPickedUpItemSlot()
  {
    return pickedUp;
    
  }
  
  @Override
  public void setPickedUpItemSlot(ItemSlot item)
  {
    if (item != null)
      item.setHotKey(-1, true);
    pickedUp = item;
  }
  
  @Override
  public void slotExited(MouseEvent e, ItemSlot slot)
  {
    updateStats(true);
  }
  
  @Override
  public ItemSlot getFirstSlot(Item item)
  {
    for (ItemSlot is : inventory)
    {
      if (item != null)
      {
        if (is.getItem() != null && is.getItem().equals(item))
          return is;
      }
      else if (is.getItem() == null)
        return is;
    }
    return null;
  }
  
  @Override
  public Map getMap()
  {
    return sg.getMapPack().getActiveMap();
  }
  
  @Override
  public void showContextMenu(ItemSlot slot, int x, int y)
  {
    if (slot.getItem() == null)
      return;
    
    contextItemSlot = slot;
    
    Object[] options = {};
    
    Types type = slot.getItem().getType();
    
    if (type.getCategory().equals(Categories.CONSUMABLE))
      options = new Object[] { USEITEM, THROWITEM };
    
    else if (Arrays.asList(Categories.EQUIPS).indexOf(type.getCategory()) > -1 || type.getCategory().equals(Categories.WEAPON) || type.getCategory().equals(Categories.ITEM))
      options = new Object[] { TRASHITEM, THROWITEM };
    
    int lx = x;
    int ly = y;
    int w = 300;
    int h = 28 * options.length + 18;
    
    if (lx + w > v.w.getWidth())
      lx -= (lx + w) - v.w.getWidth();
    
    if (ly + h > v.w.getHeight())
      ly -= (ly + h) - v.w.getHeight();
    
    contextMenu = new TextSelect(lx, ly, w, h, options);
  }
  
  @Override
  public void hideContextMenu()
  {
    contextMenu = null;
    contextItemSlot = null;
  }
}
