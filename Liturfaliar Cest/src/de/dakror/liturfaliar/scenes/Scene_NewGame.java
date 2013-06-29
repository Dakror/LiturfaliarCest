package de.dakror.liturfaliar.scenes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.item.Categories;
import de.dakror.liturfaliar.item.Equipment;
import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.item.Items;
import de.dakror.liturfaliar.item.Types;
import de.dakror.liturfaliar.map.MapPack;
import de.dakror.liturfaliar.settings.Balance;
import de.dakror.liturfaliar.settings.CFG;
import de.dakror.liturfaliar.ui.Button;
import de.dakror.liturfaliar.ui.Chooser;
import de.dakror.liturfaliar.ui.Container;
import de.dakror.liturfaliar.ui.Dialog;
import de.dakror.liturfaliar.ui.InputBar;
import de.dakror.liturfaliar.ui.ItemSlot;
import de.dakror.liturfaliar.ui.Notification;
import de.dakror.liturfaliar.ui.Tooltip;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.FileManager;

public class Scene_NewGame implements Scene
{
  public static final Object[] hair     = { "10_black.png", "10_deepblue.png", "10_deeppurple.png", "10_gold.png", "10_gray.png", "10_navy.png", "10_redbrown.png", "11_aqua.png", "11_black.png", "11_blue.png", "11_brown.png", "11_cream.png", "11_gold.png", "11_gray.png", "11_green.png", "11_purple.png", "11_red.png", "11_wine.png", "12_aqua.png", "12_black.png", "12_blue.png", "12_cream.png", "12_deepblue.png", "12_deeppurple.png", "12_gold.png", "12_gray.png", "12_green.png", "12_navy.png", "12_orange.png", "12_red.png", "12_wine.png", "13_brown.png", "13_gold.png", "13_gray.png", "13_wine.png", "14_aqua.png", "14_black.png", "14_brown.png", "14_cream.png", "14_deepblue.png", "14_deepgreen.png", "14_deeppurple.png", "14_gold.png", "14_gray.png", "14_green.png", "14_navy.png", "14_orange.png", "14_red.png", "14_silver.png", "14_wine.png", "15_black.png", "15_deepblue.png", "15_deepgreen.png", "15_deeppurple.png", "15_navy.png", "15_ocher.png", "15_redbrown.png", "15_wine.png", "1_aqua.png", "1_black.png", "1_blue.png", "1_brown.png", "1_gold.png", "1_gray.png", "1_green.png", "1_lbrown.png", "1_red.png", "1_wine.png", "2_aqua.png", "2_black.png", "2_blue.png", "2_brown.png", "2_gold.png", "2_gray.png", "2_green.png", "2_lbrown.png", "2_red.png", "2_wine.png", "3_aqua.png", "3_black.png", "3_blue.png", "3_brown.png", "3_gold.png", "3_gray.png", "3_green.png", "3_lbrown.png", "3_red.png", "3_wine.png", "4_aqua.png", "4_black.png", "4_blue.png", "4_brown.png", "4_gold.png", "4_gray.png", "4_green.png", "4_lbrown.png", "4_red.png", "4_wine.png", "5_aqua.png", "5_black.png", "5_blue.png", "5_brown.png", "5_gold.png", "5_gray.png", "5_green.png", "5_lbrown.png", "5_red.png", "5_wine.png", "6_black.png", "6_black_s.png", "6_blue.png", "6_blue_s.png", "6_brown.png", "6_brown_s.png", "6_cream.png", "6_cream_s.png", "6_deepblue.png", "6_deepblue_s.png", "6_gold.png", "6_gold_s.png", "6_gray.png", "6_gray_s.png", "6_green.png", "6_green_s.png", "6_navy.png", "6_navy_s.png", "6_purple.png", "6_purple_s.png", "6_red.png", "6_red_s.png", "6_wine.png", "6_wine_s.png", "7_black.png", "7_blue.png", "7_brown.png", "7_cream.png", "7_deepblue.png", "7_gold.png", "7_gray.png", "7_green.png", "7_navy.png", "7_purple.png", "7_red.png", "7_wine.png", "8_black.png", "8_black_s.png", "8_blue.png", "8_blue_s.png", "8_brown.png", "8_brown_s.png", "8_cream.png", "8_cream_s.png", "8_deepblue.png", "8_deepblue_s.png", "8_gold.png", "8_gold_s.png", "8_gray.png", "8_gray_s.png", "8_green.png", "8_green_s.png", "8_navy.png", "8_navy_s.png", "8_purple.png", "8_purple_s.png", "8_red.png", "8_red_s.png", "8_wine.png", "8_wine_s.png", "9_aqua.png", "9_black.png", "9_blue.png", "9_brown.png", "9_gold.png", "9_gray.png", "9_green.png", "9_lbrown.png", "9_navy.png", "9_orange.png", "9_purple.png", "9_red.png", "9_wine.png", "none.png" };
  public static final Object[] eyes     = { "1_aqua.png", "1_black.png", "1_blue.png", "1_brown.png", "1_gold.png", "1_gray.png", "1_green.png", "1_purple.png", "1_red.png", "2_aqua.png", "2_blue.png", "2_brown.png", "2_gold.png", "2_gray.png", "2_green.png", "2_pink.png", "2_purple.png", "2_red.png", "3_aqua.png", "3_black.png", "3_blue.png", "3_brown.png", "3_gold.png", "3_gray.png", "3_green.png", "3_purple.png", "3_red.png", "4_aqua.png", "4_black.png", "4_blue.png", "4_brown.png", "4_gold.png", "4_gray.png", "4_green.png", "4_purple.png", "4_red.png", "6_aqua.png", "6_black.png", "6_blue.png", "6_brown.png", "6_gold.png", "6_gray.png", "6_green.png", "6_pink.png", "6_red.png", "6_redpurple.png", "7_deeppurple.png", "7_gray.png", "7_green.png", "7_red.png", "none.png" };
  
  
  boolean                      openDialog;
  
  Chooser                      gender;
  Chooser[]                    parts;
  String[]                     partsENG = { "hair", "eyes" };
  String[]                     partsDEU = { "Haare", "Augen" };
  
  Button                       start;
  Button                       random;
  InputBar                     name;
  Container                    c1;
  Equipment                    equip;
  
  Viewport                     v;
  
  @Override
  public void construct(Viewport v)
  {
    this.v = v;
    v.play();
    
    c1 = new Container(0, 0, v.w.getWidth(), 55);
    c1.tileset = null;
    
    equip = Equipment.getDefault(true);
    
    gender = new Chooser(v.w.getWidth() / 2 + 20, v.w.getHeight() / 2 - 220, 280, 28, "", "Junge", "Mädchen");
    gender.alternate = true;
    gender.showIndex = true;
    
    name = new InputBar(v.w.getWidth() / 2 + 16, v.w.getHeight() / 2 + 192, 224, 25, "Name", Color.white);
    name.max = 15;
    name.allowed += "_1234567890";
    name.centered = true;
    
    random = new Button(v.w.getWidth() / 2 + 256, v.w.getHeight() / 2 + 176, 64, 64, "refresh_icon");
    random.soundMOVER = false;
    random.tileset = "Wood";
    random.clickmod = random.hovermod = 0;
    random.iw = random.ih = -44;
    random.tooltip = new Tooltip("<#999999;30;1>Zufall[br]<#ffffff;15;1>Generiere einen[br]zufälligen Namen.", random);
    random.tooltip.setX(v.w.getWidth() / 2 + 320);
    
    start = new Button(v.w.getWidth() / 2 - 320, v.w.getHeight() / 2 + 260, 640, "Spiel starten", Color.white, 25.0f);
    
    parts = new Chooser[partsDEU.length];
    
    for (int i = 0; i < parts.length; i++)
    {
      parts[i] = new Chooser(v.w.getWidth() / 2 + 20, v.w.getHeight() / 2 - 220 + 40 * (i + 1), 280, 28, partsDEU[i], (partsENG[i].equals("hair")) ? hair : eyes);
      parts[i].alternate = true;
    }
  }
  
  @Override
  public void update(long timePassed)
  {
    gender.update();
    random.update();
    start.update();
    name.update();
    
    if (name.value.length() == 0 || name.value.equals("Name"))
      start.disabled = true;
    else start.disabled = false;
    
    if (random.getState() == 1)
    {
      name.value = Assistant.getRandomName();
      random.setState(2);
    }
    
    if (start.getState() == 1)
    {
      switch (createSave())
      {
        case 0:
        {
          Viewport.notification = new Notification("Ein Spielstand mit diesem Namen\n\nexistiert bereits. Bitte wähle einen anderen.", Notification.ERROR);
          break;
        }
        case 1:
        {
          v.savegame = getSave();
          openDialog = true;
          break;
        }
        case 2:
        {
          Viewport.notification = new Notification("Es ist ein Fehler aufgetreten! Bitte versuche es erneut.", Notification.ERROR);
          break;
        }
      }
      start.setState(0);
    }
    
    for (Chooser c : parts)
    {
      c.update();
    }
    
    if (gender.getSelected(false).equals("Junge") != equip.isMale())
      equip = Equipment.getDefault(!equip.isMale());
    for (int i = 0; i < parts.length; i++)
    {
      String sel;
      if ((sel = (String) parts[i].getSelected(true)) != null)
        equip.setEquipmentItem(Categories.valueOf(partsENG[i].toUpperCase()), new Item(Types.valueOf(partsENG[i].toUpperCase()), sel.replaceAll("(_.{1}\\.png)|(\\.png)", ""), 1));
    }
    
    if (Viewport.dialog != null && Viewport.dialog.buttons.length > 0)
    {
      if (Viewport.dialog.buttons[0].getState() == 1)
      {
        Viewport.dialog = null;
        v.setScene(new Scene_Tutorial());
      }
      else if (Viewport.dialog.buttons[1].getState() == 1)
      {
        Viewport.dialog = null;
        v.setScene(new Scene_Game());
      }
    }
  }
  
  @Override
  public void draw(Graphics2D g)
  {
    if (openDialog)
    {
      Viewport.dialog = new Dialog("Tutorial", "Möchtest du kurz in die Steuerung und[br]Benutzeroberfläche eingeführt werden?", Dialog.MESSAGE);
      Viewport.dialog.closeDisabled = true;
      Viewport.dialog.draw(g, v);
      Viewport.dialog.setButtons("Ja", "Nein");
      Viewport.dialog.update();
      openDialog = false;
    }
    
    Assistant.drawMenuBackground(g, v.w);
    c1.draw(g, v);
    Assistant.drawHorizontallyCenteredString("Neues Spiel", v.w.getWidth(), 43, g, 45, Color.white);
    
    if (equip != null)
      Assistant.drawChar(v.w.getWidth() / 2 - 320, v.w.getHeight() / 2 - 240, 320, 480, 0, v.getFrame(2.5f) % 4, equip, g, v.w, true);
    
    Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), v.w.getWidth() / 2, v.w.getHeight() / 2 - 240, 320, 416, g, v.w);
    
    gender.draw(g, v);
    name.draw(g, v);
    random.draw(g, v);
    start.draw(g, v);
    
    for (Chooser c : parts)
    {
      c.draw(g, v);
    }
  }
  
  public int createSave()
  {
    if (FileManager.doesSaveExist(name.value))
      return 0;
    else
    {
      JSONObject save = getSave();
      if (save == null)
        return 2;
      FileManager.setSave(save);
      return 1;
    }
  }
  
  public JSONObject getSave()
  {
    JSONObject save = new JSONObject();
    try
    {
      // -- char -- //
      JSONObject cfg = new JSONObject();
      cfg.put("name", name.value);
      
      // -- equipment -- //
      cfg.put("equip", equip.serializeEquipment());
      
      // -- skills -- //
      cfg.put("skills", new JSONArray());
      
      // -- inventory -- //
      JSONArray inv = new JSONArray();
      
      inv.put(ItemSlot.serializeFakeItemSlot(new Item(Items.HEALTHPOTION, 10)));
      inv.put(ItemSlot.serializeFakeItemSlot(new Item(Items.TOXICPOTION, 10)));
      inv.put(ItemSlot.serializeFakeItemSlot(new Item(Items.SCRAP, 8)));
      
      cfg.put("inventory", inv);
      
      // -- attributes -- //
      JSONObject attr = new JSONObject();
      
      attr.put("health", Balance.Player.INITHEALTH);
      attr.put("maxhealth", Balance.Player.INITHEALTH);
      
      attr.put("stamina", Balance.Player.INITSTAMINA);
      attr.put("maxstamina", Balance.Player.INITSTAMINA);
      
      attr.put("gold", Balance.Player.INITGOLD);
      attr.put("maxgold", Balance.Player.INITGOLD);
      
      attr.put("skillpoint", 1);
      attr.put("maxskillpoint", 1);
      
      attr.put("experience", 0);
      attr.put("maxexperience", 1);
      
      attr.put("speed", 1);
      attr.put("maxspeed", 1);
      
      attr.put("level", 1);
      attr.put("maxlevel", 1);
      
      cfg.put("attr", attr);
      
      save.put("char", cfg);
      
      // -- map(-pack) -- //
      MapPack mp = new MapPack(CFG.MAPPACK, v.w);
      JSONObject mappack = new JSONObject();
      mappack.put("name", mp.getName());
      mappack.put("pos", mp.getData().getJSONObject("init"));
      mappack.put("npc", new JSONArray());
      mappack.put("drops", new JSONArray());
      save.put("mappack", mappack);
      
      return save;
    }
    catch (JSONException e)
    {
      return null;
    }
  }
  
  @Override
  public void keyTyped(KeyEvent e)
  {}
  
  @Override
  public void keyPressed(KeyEvent e)
  {
    if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
    {
      v.setScene(new Scene_MainMenu());
      v.playSound("002-System02");
    }
    
    if (gender != null)
      gender.keyPressed(e);
    name.keyPressed(e);
    
    for (Chooser c : parts)
    {
      c.keyPressed(e);
    }
  }
  
  @Override
  public void keyReleased(KeyEvent e)
  {}
  
  @Override
  public void mouseWheelMoved(MouseWheelEvent e)
  {}
  
  @Override
  public void mouseDragged(MouseEvent e)
  {}
  
  @Override
  public void mouseMoved(MouseEvent e)
  {
    try
    {
      gender.mouseMoved(e);
      name.mouseMoved(e);
      random.mouseMoved(e);
      start.mouseMoved(e);
      
      for (Chooser c : parts)
      {
        if (c != null)
          c.mouseMoved(e);
      }
    }
    catch (Exception e1)
    {}
  }
  
  @Override
  public void mouseClicked(MouseEvent e)
  {}
  
  @Override
  public void mousePressed(MouseEvent e)
  {
    random.mousePressed(e);
    start.mousePressed(e);
  }
  
  @Override
  public void mouseReleased(MouseEvent e)
  {
    gender.mouseReleased(e);
    name.mouseReleased(e);
    random.mouseReleased(e);
    start.mouseReleased(e);
    
    for (Chooser c : parts)
    {
      c.mouseReleased(e);
    }
  }
  
  @Override
  public void mouseEntered(MouseEvent e)
  {}
  
  @Override
  public void mouseExited(MouseEvent e)
  {}
  
  @Override
  public void destruct()
  {}
}
