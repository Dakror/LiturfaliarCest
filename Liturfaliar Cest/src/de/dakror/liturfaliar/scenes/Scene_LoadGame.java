package de.dakror.liturfaliar.scenes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.item.Equipment;
import de.dakror.liturfaliar.ui.Button;
import de.dakror.liturfaliar.ui.Chooser;
import de.dakror.liturfaliar.ui.Container;
import de.dakror.liturfaliar.ui.Dialog;
import de.dakror.liturfaliar.ui.Tooltip;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.FileManager;

public class Scene_LoadGame implements Scene
{
  Button[]     saves;
  Button       delete;
  Button       start;
  JSONObject[] datas;
  Container    c1;
  Chooser      chooser;
  int          active;
  Area         chars;
  Point        mouse;
  Viewport     v;
  
  @Override
  public void construct(Viewport v)
  {
    this.v = v;
    
    v.playMusic("013-Theme02", false);
    c1 = new Container(0, 0, v.w.getWidth(), 55);
    c1.tileset = null;
    datas = FileManager.getSaves();
    if (datas.length == 0)
      v.setScene(new Scene_MainMenu());
    
    saves = new Button[datas.length];
    int charsperpage = 5;
    int fullwidth = v.w.getWidth() / 2;
    int space = 25;
    int width = fullwidth / 5 - 27;
    
    mouse = new Point(0, 0);
    
    for (int i = 0; i < saves.length; i++)
    {
      try
      {
        BufferedImage bi = new BufferedImage(width, width / 3 * 4, BufferedImage.TYPE_INT_ARGB);
        Assistant.drawChar(0, 0, width, width / 3 * 4, 0, 0, new Equipment(datas[i].getJSONObject("char").getJSONObject("equip")), (Graphics2D) bi.getGraphics(), null, true);
        saves[i] = new Button(v.w.getWidth() / 4 + (i % charsperpage) * (width + space), v.w.getHeight() / 2 - (width / 3 * 2), width, width / 3 * 4, 0, 0, width, width / 3 * 4, bi);
        saves[i].tileset = null;
        saves[i].hovermod = 4;
        saves[i].clickmod = 16;
        saves[i].soundMOVER = false;
        saves[i].tooltip = new Tooltip("<#999999;30;1>" + datas[i].getString("savename") + "[br]<#ffffff;17;1>Zuletzt gespielt: " + getRelativeDate(datas[i].getLong("lastplayed")) + "[br]<#ffffff;17;1>Ort: <#4444ff;17;1>" + datas[i].getJSONObject("mappack").getJSONObject("pos").getString("map"), saves[i]);
        saves[i].tooltip.follow = true;
      }
      catch (JSONException e)
      {
        continue;
      }
    }
    if (saves.length > 5)
    {
      chooser = new Chooser(v.w.getWidth() / 2 - 200, v.w.getHeight() / 2 + (width / 3 * 2), 400, 35, "Seite: ", Assistant.getArrayFromLimits(1, Math.round(saves.length / 5.0f + 0.5f) + 1));
      chooser.alternate = true;
      chooser.showIndex = true;
    }
    chars = new Area(new Rectangle2D.Double(v.w.getWidth() / 4, v.w.getHeight() / 2 - (width / 3 * 2), fullwidth, width / 3 * 4));
    delete = new Button(v.w.getWidth() / 2 - 180, v.w.getHeight() / 2 + (width / 3 * 2) + 50, 360, "Spielstand Löschen", Color.white, 22);
    delete.clickmod = 0;
    delete.hovermod = 0;
    delete.tileset = null;
    delete.soundMOVER = false;
    delete.disabled = true;
    start = new Button(v.w.getWidth() / 2 - 200, v.w.getHeight() / 2 + (width / 3 * 2) + 90, 400, "Spiel starten", Color.white, 35);
    start.clickmod = 0;
    start.disabled = true;
  }
  
  public String getRelativeDate(long date)
  {
    String s = "";
    Calendar then = new GregorianCalendar();
    Calendar now = new GregorianCalendar();
    then.setTimeInMillis(date);
    now.setTimeInMillis(System.currentTimeMillis());
    if (then.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH) - 1)
    {
      s += "Gestern";
    }
    else if (then.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH))
    {
      s += "Heute";
    }
    else
    {
      s += new SimpleDateFormat("dd.MM.yy").format(new Date(date));
    }
    s += ", " + new SimpleDateFormat("HH:mm").format(new Date(date)) + " Uhr";
    return s;
  }
  
  @Override
  public void update(long timePassed)
  {
    if (chooser != null)
    {
      chooser.update();
      for (int i = 5 * ((Integer) chooser.getSelected(false) - 1); i < ((5 * (Integer) chooser.getSelected(false) < saves.length) ? 5 * (Integer) chooser.getSelected(false) : saves.length); i++)
      {
        if (saves[i] == null)
          continue;
        saves[i].update();
      }
    }
    else
    {
      for (int i = 0; i < saves.length; i++)
      {
        if (saves[i] == null)
          continue;
        saves[i].update();
      }
    }
    if (delete != null)
      delete.update();
    if (start != null)
      start.update();
    if (start.getState() == 1)
    {
      v.savegame = datas[active];
      v.setScene(new Scene_Game());
    }
    if (Viewport.dialog != null)
    {
      Viewport.dialog.update();
      if (Viewport.dialog.buttons[0].getState() == 1)
      {
        FileManager.deleteSave(datas[active]);
        Viewport.dialog.closeRequested = true;
        if (datas.length - 1 > 0)
          v.setScene(new Scene_LoadGame());
        else v.setScene(new Scene_MainMenu());
      }
      if (Viewport.dialog.buttons[1].getState() == 1)
        Viewport.dialog.closeRequested = true;
      if (Viewport.dialog.closeRequested)
      {
        Viewport.dialog = null;
      }
    }
  }
  
  @Override
  public void draw(Graphics2D g)
  {
    if (delete.getState() == 1 && Viewport.dialog == null)
    {
      Viewport.dialog = new Dialog("Spielstand löschen", "Bist du sicher, dass du diesen Spielstand[br]löschen möchtest? Diese Aktion kann nicht[br]rückgängig gemacht werden.", Dialog.MESSAGE);
      Viewport.dialog.draw(g, v);
      Viewport.dialog.setButtons("Ja", "Nein");
      delete.setState(0);
    }
    Assistant.drawMenuBackground(g, v.w);
    c1.draw(g, v);
    Assistant.drawHorizontallyCenteredString("Spiel fortsetzen", v.w.getWidth(), 43, g, 45, Color.white);
    int selected = -1;
    if (chooser != null)
    {
      chooser.draw(g, v);
      for (int i = 5 * ((Integer) chooser.getSelected(false) - 1); i < ((5 * (Integer) chooser.getSelected(false) < saves.length) ? 5 * (Integer) chooser.getSelected(false) : saves.length); i++)
      {
        if (saves[i] == null)
          continue;
        saves[i].draw(g, v);
        if (saves[i].getArea().contains(mouse))
          selected = i;
        if (saves[i].getState() == 1)
          active = i;
      }
    }
    else
    {
      for (int i = 0; i < saves.length; i++)
      {
        if (saves[i] == null)
          continue;
        saves[i].draw(g, v);
        if (saves[i].getArea().contains(mouse))
          selected = i;
        if (saves[i].getState() == 1)
          active = i;
      }
    }
    if (active != -1 && saves[active] != null && saves[active].getState() == 1)
    {
      start.disabled = false;
      delete.disabled = false;
    }
    else
    {
      start.disabled = true;
      delete.disabled = true;
    }
    if (delete != null)
      delete.draw(g, v);
    if (start != null)
      start.draw(g, v);
    if (selected != -1)
      saves[selected].draw(g, v);
  }
  
  @Override
  public void keyPressed(KeyEvent e)
  {
    if (e.getExtendedKeyCode() == KeyEvent.VK_ESCAPE)
    {
      v.setScene(new Scene_MainMenu());
      v.playSound("002-System02");
    }
    if (chooser != null)
      chooser.keyPressed(e);
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
    mouse = e.getPoint();
    if (saves == null)
      return;
    if (chooser != null)
    {
      for (int i = 5 * ((Integer) chooser.getSelected(false) - 1); i < ((5 * (Integer) chooser.getSelected(false) < saves.length) ? 5 * (Integer) chooser.getSelected(false) : saves.length); i++)
      {
        if (saves[i] == null)
          continue;
        saves[i].mouseMoved(e);
      }
      chooser.mouseReleased(e);
    }
    else
    {
      for (int i = 0; i < saves.length; i++)
      {
        if (saves[i] == null)
          continue;
        saves[i].mouseMoved(e);
      }
    }
    if (chooser != null)
      chooser.mouseMoved(e);
    if (delete != null)
      delete.mouseMoved(e);
    if (start != null)
      start.mouseMoved(e);
  }
  
  @Override
  public void mouseDragged(MouseEvent e)
  {}
  
  @Override
  public void mouseReleased(MouseEvent e)
  {
    if (saves == null)
      return;
    
    if (chooser != null)
    {
      for (int i = 5 * ((Integer) chooser.getSelected(false) - 1); i < ((5 * (Integer) chooser.getSelected(false) < saves.length) ? 5 * (Integer) chooser.getSelected(false) : saves.length); i++)
      {
        if (saves[i] == null || !chars.contains(e.getPoint()))
          continue;
        saves[i].mouseReleased(e);
      }
      chooser.mouseReleased(e);
    }
    else
    {
      for (int i = 0; i < saves.length; i++)
      {
        if (saves[i] == null || !chars.contains(e.getPoint()))
          continue;
        saves[i].mouseReleased(e);
      }
    }
    if (delete != null)
      delete.mouseReleased(e);
    if (start != null)
      start.mouseReleased(e);
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
  
  @Override
  public void destruct()
  {}
}
