package de.dakror.liturfaliar.ovscenes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import org.json.JSONObject;

import de.dakror.liturfaliar.CFG;
import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.scenes.Scene_Game;
import de.dakror.liturfaliar.scenes.Scene_LoadGame;
import de.dakror.liturfaliar.scenes.Scene_MainMenu;
import de.dakror.liturfaliar.ui.Notification;
import de.dakror.liturfaliar.ui.TextSelect;
import de.dakror.liturfaliar.ui.Tooltip;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.FileManager;
import de.dakror.liturfaliar.util.Handler;

public class OVScene_Pause implements OVScene
{
  Viewport       v;
  Scene_Game     sg;
  TextSelect     ts;
  Notification   notification;
  final String[] points = { "Weiter", "Speichern", "Laden", "Beenden" };
  
  public OVScene_Pause(Scene_Game sg)
  {
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
    ts = new TextSelect(v.w.getWidth() / 2 - 150, v.w.getHeight() / 2 - ((28 * points.length + 18) / 2), 300, 28 * points.length + 18, (Object[]) points);
    ts.soundCLICK = true;
    ts.soundMOVER = true;
    final String[] tooltips = { null, "<#999999;30;1>Speichern[br]<#ffffff;15;1>Manuelles Speichern deiner Fortschritte.", "<#999999;30;1>Laden[br]<#ffffff;15;1>Lade einen �lteren Spielstand.[br]<#6666ff;15;2>Deine Fortschritte werden [br]<#ff3333;15;2>NICHT<#6666ff;15;2> gespeichert!", "<#999999;30;1>Beenden[br]<#ffffff;15;1>Beende das aktuelle Spiel[br]und kehre zum Hauptmen� zur�ck.[br]<#6666ff;15;2>Deine Fortschritte werden gespeichert!" };
    for (int i = 0; i < ts.elements.length; i++)
    {
      if (tooltips[i] != null)
      {
        ts.elements[i].tooltip = new Tooltip(tooltips[i], ts.elements[i]);
        ts.elements[i].tooltip.setX(ts.elements[i].getX() + ts.elements[i].getWidth() + 9);
      }
    }
  }
  
  @Override
  public void update(long timePassed)
  {
    ts.update();
    int sel = ts.getSelectedIndex(true);
    switch (sel)
    {
      case 0:
      {
        sg.setPaused(false);
        v.setFramesFrozen(false);
        Handler.setListenerEnabled(sg, true);
        v.removeOVScene("Pause");
        break;
      }
      case 1:
      {
        save();
        notification = new Notification("Spielstand gespeichert.", Notification.DEFAULT);
        break;
      }
      case 2:
      {
        v.setScene(new Scene_LoadGame());
        break;
      }
      case 3:
      {
        save();
        v.setScene(new Scene_MainMenu());
        break;
      }
    }
  }
  
  @Override
  public void draw(Graphics2D g)
  {
    Assistant.Shadow(v.w.getBounds(), Color.black, 0.6f, g);
    Assistant.drawCenteredString("Spiel pausiert", v.w.getWidth(), v.w.getHeight() / 2 - ((28 * points.length + 18) / 2) - 40, g, 45);
    ts.draw(g, v);
    if (notification != null)
      notification.draw(g, v.w);
  }
  
  public void save()
  {
    try
    {
      JSONObject save = v.savegame;
      JSONObject mappack = save.getJSONObject("mappack");
      JSONObject pos = new JSONObject();
      pos.put("map", sg.getMapPack().getActiveMap().getName());
      pos.put("x", (v.w.getWidth() / 2 - CFG.FIELDSIZE / 2) - sg.getMapPack().getActiveMap().getX());
      pos.put("y", (v.w.getHeight() / 2 - CFG.FIELDSIZE * 3 / 4) - sg.getMapPack().getActiveMap().getY());
      mappack.put("pos", pos);
      save.put("mappack", mappack);
      FileManager.setSave(save);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  @Override
  public void keyTyped(KeyEvent e)
  {}
  
  @Override
  public void keyPressed(KeyEvent e)
  {}
  
  @Override
  public void keyReleased(KeyEvent e)
  {}
  
  @Override
  public void mouseClicked(MouseEvent e)
  {}
  
  @Override
  public void mousePressed(MouseEvent e)
  {
    ts.mousePressed(e);
  }
  
  @Override
  public void mouseReleased(MouseEvent e)
  {
    ts.mouseReleased(e);
  }
  
  @Override
  public void mouseEntered(MouseEvent e)
  {}
  
  @Override
  public void mouseExited(MouseEvent e)
  {}
  
  @Override
  public void mouseDragged(MouseEvent e)
  {
    ts.mouseDragged(e);
  }
  
  @Override
  public void mouseMoved(MouseEvent e)
  {
    ts.mouseMoved(e);
  }
  
  @Override
  public void mouseWheelMoved(MouseWheelEvent e)
  {
    ts.mouseWheelMoved(e);
  }
  
  @Override
  public void setListenersEnabled(boolean b)
  {
    if (b)
    {
      v.w.addKeyListener(this);
      v.w.addMouseListener(this);
      v.w.addMouseMotionListener(this);
      v.w.addMouseWheelListener(this);
    }
    else
    {
      v.w.removeKeyListener(this);
      v.w.removeMouseListener(this);
      v.w.removeMouseMotionListener(this);
      v.w.removeMouseWheelListener(this);
    }
  }
}