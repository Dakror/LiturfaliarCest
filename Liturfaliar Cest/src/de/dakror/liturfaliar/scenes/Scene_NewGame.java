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
import de.dakror.liturfaliar.map.MapPack;
import de.dakror.liturfaliar.settings.CFG;
import de.dakror.liturfaliar.ui.Button;
import de.dakror.liturfaliar.ui.Container;
import de.dakror.liturfaliar.ui.Dialog;
import de.dakror.liturfaliar.ui.InputBar;
import de.dakror.liturfaliar.ui.Notification;
import de.dakror.liturfaliar.ui.Chooser;
import de.dakror.liturfaliar.ui.Tooltip;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.FileManager;

public class Scene_NewGame implements Scene
{
  Chooser[]  bodyparts;
  Button     start;
  Button     random;
  Chooser    gender;
  InputBar   name;
  JSONObject charData;
  JSONObject cfg;
  Container  c1, c2;
  Dialog     tutorial;
  boolean    openDialog;
  Viewport   v;
  
  @Override
  public void init(Viewport v)
  {
    this.v = v;
    v.play();
    gender = new Chooser(v.w.getWidth() / 2 + 30, v.w.getHeight() / 3 - 30, v.w.getWidth() / 6 - 60, 35, "", "Mann", "Frau");
    gender.alternate = true;
    gender.showIndex = true;
    openDialog = false;
    try
    {
      charData = new JSONObject(Assistant.getURLContent(getClass().getResource("/json/char.json")));
      JSONObject nat = charData.getJSONObject("native");
      JSONObject f = nat.getJSONObject("female");
      bodyparts = new Chooser[10];
      for (int i = 0; i < 5; i++)
      {
        bodyparts[i] = new Chooser(v.w.getWidth() / 2, v.w.getHeight() / 3 + i * 50 + 20, v.w.getWidth() / 6, 35, new String[] { "Haut", "Haare", "Augen", "Oberteil", "Hose" }[i], new Object[][] { Assistant.getArrayFromLimits(0, 7), JSONArrToObj(f.getJSONArray("hair")), JSONArrToObj(f.getJSONArray("eyes")), JSONArrToObj(f.getJSONArray("shirt")), JSONArrToObj(f.getJSONArray("trouser")) }[i]);
        bodyparts[i].alternate = true;
      }
      JSONObject m = nat.getJSONObject("male");
      for (int i = 0; i < 5; i++)
      {
        bodyparts[i + 5] = new Chooser(v.w.getWidth() / 2, v.w.getHeight() / 3 + i * 50 + 20, v.w.getWidth() / 6, 35, new String[] { "Haut", "Haare", "Augen", "Oberteil", "Hose" }[i], new Object[][] { Assistant.getArrayFromLimits(0, 7), JSONArrToObj(m.getJSONArray("hair")), JSONArrToObj(m.getJSONArray("eyes")), JSONArrToObj(m.getJSONArray("shirt")), JSONArrToObj(m.getJSONArray("trouser")) }[i]);
        bodyparts[i + 5].alternate = true;
      }
      cfg = charData.getJSONObject("default").getJSONObject("Mann");
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
    c1 = new Container(v.w.getWidth() / 3, v.w.getHeight() / 3 + 330, v.w.getWidth() / 3, 200);
    c1.tileset = null;
    c2 = new Container(0, 0, v.w.getWidth(), 55);
    c2.tileset = null;
    name = new InputBar(v.w.getWidth() / 2 - 125, v.w.getHeight() / 3 + 400, 250, 25.0f, "", Color.white);
    name.max = 15;
    name.allowed += "_1234567890";
    name.centered = true;
    random = new Button(v.w.getWidth() / 2 + 150, v.w.getHeight() / 3 + 400, 35, 35, "refresh_icon");
    random.soundMOVER = false;
    random.clickmod = 0;
    random.hovermod = 4;
    random.tooltip = new Tooltip("<#999999;30;1>Zufall[br]<#ffffff;15;1>Generiere einen[br]zufälligen Namen.", random);
    random.tooltip.setX(v.w.getWidth() / 2 + 185);
    start = new Button(v.w.getWidth() / 2 - 140, v.w.getHeight() / 3 + 450, 280, "Start", Color.white, 30.0f);
    start.clickmod = 0;
    start.hovermod = 4;
  }
  
  @Override
  public void update(long timePassed)
  {
    gender.update();
    for (int i = 0; i < bodyparts.length; i++)
    {
      if ((gender.getSelected(false).equals("Mann") && i >= 5) || (gender.getSelected(false).equals("Frau") && i < 5))
      {
        bodyparts[i].update();
        Integer s = (Integer) bodyparts[i].getSelected(true);
        if (s != null)
        {
          try
          {
            cfg.put(new String[] { "skin", "hair", "eyes", "shirt", "trouser" }[i % 5], s);
          }
          catch (Exception e)
          {
            e.printStackTrace();
          }
        }
      }
    }
    String gender = (String) this.gender.getSelected(true);
    if (gender != null)
    {
      try
      {
        cfg = charData.getJSONObject("default").getJSONObject(gender);
      }
      catch (JSONException e)
      {
        e.printStackTrace();
      }
    }
    name.update();
    random.update();
    if (random.getState() == 1)
    {
      name.value = Assistant.getRandomName();
      random.setState(2);
    }
    if (name.value.length() == 0)
    {
      start.disabled = true;
    }
    else
    {
      start.disabled = false;
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
    start.update();
    if (tutorial != null)
      tutorial.update();
  }
  
  public int createSave()
  {
    if (FileManager.getSave(name.value + ".json") != null)
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
      cfg.put("name", name.value);
      save.put("char", cfg);
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
    gender.draw(g, v);
    for (int i = 0; i < bodyparts.length; i++)
    {
      if ((gender.getSelected(false).equals("Mann") && i >= 5) || (gender.getSelected(false).equals("Frau") && i < 5))
        bodyparts[i].draw(g, v);
    }
    if (cfg != null)
      Assistant.drawChar(v.w.getWidth() / 3, v.w.getHeight() / 4, v.w.getWidth() / 6, (int) (v.w.getWidth() / 6 * 4 / 3), 0, 0, cfg, g, v.w, false);
    c1.draw(g, v);
    c2.draw(g, v);
    Assistant.drawHorizontallyCenteredString("Neues Spiel starten", v.w.getWidth(), 43, g, 45, Color.white);
    Assistant.drawHorizontallyCenteredString("Dein Name:", v.w.getWidth() / 2 - 125, 250, v.w.getHeight() / 3 + 370, g, 35, Color.white);
    name.draw(g, v);
    random.draw(g, v);
    start.draw(g, v);
    if (tutorial != null)
      tutorial.draw(g, v);
  }
  
  public static Object[] JSONArrToObj(JSONArray a)
  {
    Object[] res = new Object[a.length()];
    for (int i = 0; i < res.length; i++)
    {
      try
      {
        res[i] = a.get(i);
      }
      catch (JSONException e)
      {
        e.printStackTrace();
      }
    }
    return res;
  }
  
  @Override
  public void keyPressed(KeyEvent e)
  {
    if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
    {
      v.setScene(new Scene_MainMenu());
      v.playSound("002-System02");
    }
    gender.keyPressed(e);
    for (int i = 0; i < bodyparts.length; i++)
    {
      if ((gender.getSelected(false).equals("Mann") && i >= 5) || (gender.getSelected(false).equals("Frau") && i < 5))
        bodyparts[i].keyPressed(e);
    }
    name.keyPressed(e);
  }
  
  @Override
  public void keyReleased(KeyEvent e)
  {}
  
  @Override
  public void mousePressed(MouseEvent e)
  {}
  
  @Override
  public void mouseMoved(MouseEvent e)
  {
    gender.mouseMoved(e);
    if (bodyparts != null)
    {
      for (int i = 0; i < bodyparts.length; i++)
      {
        if ((gender.getSelected(false).equals("Mann") && i >= 5) || (gender.getSelected(false).equals("Frau") && i < 5))
          bodyparts[i].mouseMoved(e);
      }
    }
    name.mouseMoved(e);
    random.mouseMoved(e);
    start.mouseMoved(e);
    if (tutorial != null)
      tutorial.mouseMoved(e);
  }
  
  @Override
  public void mouseDragged(MouseEvent e)
  {}
  
  @Override
  public void mouseReleased(MouseEvent e)
  {
    gender.mouseReleased(e);
    for (int i = 0; i < bodyparts.length; i++)
    {
      if ((gender.getSelected(false).equals("Mann") && i >= 5) || (gender.getSelected(false).equals("Frau") && i < 5))
        bodyparts[i].mouseReleased(e);
    }
    name.mouseReleased(e);
    random.mouseReleased(e);
    start.mouseReleased(e);
    if (tutorial != null)
      tutorial.mouseReleased(e);
  }
  
  @Override
  public void keyTyped(KeyEvent e)
  {}
  
  @Override
  public void mouseClicked(MouseEvent e)
  {}
  
  @Override
  public void mouseEntered(MouseEvent e)
  {}
  
  @Override
  public void mouseExited(MouseEvent e)
  {}
  
  @Override
  public void mouseWheelMoved(MouseWheelEvent e)
  {}
}
