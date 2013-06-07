package de.dakror.liturfaliar.scenes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.net.URISyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.item.Categories;
import de.dakror.liturfaliar.item.Equipment;
import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.item.Types;
import de.dakror.liturfaliar.map.MapPack;
import de.dakror.liturfaliar.settings.Balance;
import de.dakror.liturfaliar.settings.CFG;
import de.dakror.liturfaliar.ui.Button;
import de.dakror.liturfaliar.ui.Chooser;
import de.dakror.liturfaliar.ui.Container;
import de.dakror.liturfaliar.ui.Dialog;
import de.dakror.liturfaliar.ui.InputBar;
import de.dakror.liturfaliar.ui.Notification;
import de.dakror.liturfaliar.ui.Tooltip;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.FileManager;

public class Scene_NewGame implements Scene
{
  boolean   openDialog;
  
  Dialog    tutorial;
  Chooser   gender;
  Chooser[] parts;
  String[]  partsENG = { "hair", "eyes" };
  String[]  partsDEU = { "Haare", "Augen" };
  
  Button    start;
  Button    random;
  InputBar  name;
  Container c1;
  Equipment equip;
  
  Viewport  v;
  
  @Override
  public void init(Viewport v)
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
      try
      {
        parts[i] = new Chooser(v.w.getWidth() / 2 + 20, v.w.getHeight() / 2 - 220 + 40 * (i + 1), 280, 28, partsDEU[i], (Object[]) new File(getClass().getResource("/img/char/" + partsENG[i]).toURI()).list());
        parts[i].alternate = true;
      }
      catch (URISyntaxException e)
      {
        e.printStackTrace();
      }
    }
  }
  
  @Override
  public void update(long timePassed)
  {
    gender.update();
    random.update();
    start.update();
    
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
          Viewport.notification = new Notification("Ein Spielstand mit diesem Namen existiert bereits. Bitte wähle einen Anderen.", Notification.ERROR);
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
        equip.setEquipmentItem(Categories.valueOf(partsENG[i].toUpperCase()), new Item(Types.valueOf(partsENG[i].toUpperCase()), sel.replaceAll("(_.{1}\\.png)|(\\.png)", "")));
    }
    
    if (tutorial != null && tutorial.buttons.length > 0)
    {
      if (tutorial.buttons[0].getState() == 1)
      {
        tutorial.close(v);
        tutorial = null;
        v.setScene(new Scene_Tutorial());
      }
      else if (tutorial.buttons[1].getState() == 1)
      {
        tutorial.close(v);
        tutorial = null;
        v.setScene(new Scene_Game());
      }
    }
    
    if (tutorial != null)
      tutorial.update();
  }
  
  @Override
  public void draw(Graphics2D g)
  {
    if (openDialog)
    {
      tutorial = new Dialog("Tutorial?", "Möchtest du kurz in die Steuerung und[br]Benutzeroberfläche eingeführt werden?", Dialog.MESSAGE, v);
      tutorial.closeDisabled = true;
      tutorial.draw(g, v);
      tutorial.setButtons("Ja", "Nein");
      tutorial.update();
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
    if (tutorial != null)
      tutorial.draw(g, v);
    
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
      cfg.put("equip", equip.serializeEquipment());
      cfg.put("inventory", new JSONArray());
      
      save.put("char", cfg);
      
      // -- attributes -- //
      JSONObject attr = new JSONObject();
      
      attr.put("health", Balance.Player.INITHEALTH);
      attr.put("maxhealth", Balance.Player.INITHEALTH);
      
      attr.put("stamina", Balance.Player.INITSTAMINA);
      attr.put("maxstamina", Balance.Player.INITSTAMINA);
      
      attr.put("speed", 1);
      attr.put("maxspeed", 1);
      
      save.put("attr", attr);
      
      // -- map(-pack) -- //
      MapPack mp = new MapPack(CFG.MAPPACK, v.w);
      JSONObject mappack = new JSONObject();
      mappack.put("name", mp.getName());
      mappack.put("pos", mp.getData().getJSONObject("init"));
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
    gender.mouseMoved(e);
    name.mouseMoved(e);
    random.mouseMoved(e);
    start.mouseMoved(e);
    
    for (Chooser c : parts)
    {
      c.mouseMoved(e);
    }
    
    if (tutorial != null)
      tutorial.mouseMoved(e);
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
    
    if (tutorial != null)
      tutorial.mouseReleased(e);
  }
  
  @Override
  public void mouseEntered(MouseEvent e)
  {}
  
  @Override
  public void mouseExited(MouseEvent e)
  {}
  
}
