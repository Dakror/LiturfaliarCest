package de.dakror.liturfaliar.ovscenes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.item.Item.Categories;
import de.dakror.liturfaliar.scenes.Scene_Game;
import de.dakror.liturfaliar.ui.Container;
import de.dakror.liturfaliar.ui.ItemSlot;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.Handler;

public class OVScene_Inventory extends OVScene
{
  Scene_Game sg;
  Container  c1;
  
  ItemSlot[] equipSlots;
  
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
    c1 = new Container(0, 0, v.w.getWidth(), 55);
    c1.tileset = null;
    
    equipSlots = new ItemSlot[12];
    equipSlots[0] = new ItemSlot(183, 80); // helmet
    equipSlots[0].setCategoryFilter(Categories.HELMET);
    
    equipSlots[1] = new ItemSlot(80, 160); // cape
    equipSlots[1].setCategoryFilter(Categories.CAPE);
    
    equipSlots[2] = new ItemSlot(266, 210); // shoulder
    equipSlots[2].setCategoryFilter(Categories.SHOULDER);
    
    equipSlots[3] = new ItemSlot(155, 240); // shirt
    equipSlots[3].setCategoryFilter(Categories.SHIRT);
    
    equipSlots[4] = new ItemSlot(99, 240); // arm
    equipSlots[4].setCategoryFilter(Categories.ARM);
    
    equipSlots[5] = new ItemSlot(210, 240); // armor
    equipSlots[5].setCategoryFilter(Categories.ARMOR);
    
    equipSlots[6] = new ItemSlot(320, 270); // gloves
    equipSlots[6].setCategoryFilter(Categories.GLOVES);
    
    equipSlots[7] = new ItemSlot(183, 305); // belt
    equipSlots[7].setCategoryFilter(Categories.BELT);
    
    equipSlots[8] = new ItemSlot(75, 330); // left wpn
    equipSlots[8].setCategoryFilter(Categories.WEAPON);
    
    equipSlots[9] = new ItemSlot(290, 330); // right wpn
    equipSlots[9].setCategoryFilter(Categories.WEAPON);
    
    equipSlots[10] = new ItemSlot(183, 368); // pants
    equipSlots[10].setCategoryFilter(Categories.PANTS);
    
    equipSlots[11] = new ItemSlot(182, 430); // boots
    equipSlots[11].setCategoryFilter(Categories.BOOTS); 
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
    Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), v.w.getWidth() / 2 - 190, v.w.getHeight() / 2 - 350, 800, 700, g, v.w);
    
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
  }
  
  @Override
  public void keyReleased(KeyEvent e)
  {
    if (e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_I)
    {
      v.removeOVScene("Inventory");
      sg.setPaused(false);
      v.setFramesFrozen(false);
      Handler.setListenerEnabled(sg, true);
    }
  }
  
  @Override
  public void mouseMoved(MouseEvent e)
  {
    for (ItemSlot slot : equipSlots)
    {
      slot.mouseMoved(e);
    }
  }
}
