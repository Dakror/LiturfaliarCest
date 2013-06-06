package de.dakror.liturfaliar.ovscenes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import org.json.JSONObject;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.scenes.Scene_Game;
import de.dakror.liturfaliar.scenes.Scene_LoadGame;
import de.dakror.liturfaliar.scenes.Scene_MainMenu;
import de.dakror.liturfaliar.settings.CFG;
import de.dakror.liturfaliar.ui.Container;
import de.dakror.liturfaliar.ui.Notification;
import de.dakror.liturfaliar.ui.TextSelect;
import de.dakror.liturfaliar.ui.Tooltip;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.FileManager;
import de.dakror.liturfaliar.util.Handler;

public class OVScene_Pause extends OVScene
{
  Scene_Game     sg;
  TextSelect     ts;
  Container      c1;
  Notification   notification;
  final String[] points = { "Weiter", "Speichern", "Laden", "Beenden" };
  
  public OVScene_Pause(Scene_Game sg)
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
    ts = new TextSelect(v.w.getWidth() / 2 - 150, 350, 300, 28 * points.length + 18, (Object[]) points);
    ts.soundCLICK = true;
    ts.soundMOVER = true;
    final String[] tooltips = { null, "<#999999;30;1>Speichern[br]<#ffffff;17;1>Manuelles Speichern deiner Fortschritte.", "<#999999;30;1>Laden[br]<#ffffff;17;1>Lade einen älteren Spielstand.[br]<#6666ff;17;2>Deine Fortschritte werden [br]<#ff3333;17;2>NICHT<#6666ff;17;2> gespeichert![br]<#ff3333;17;2>Das aktuelle Spiel wird verlassen!", "<#999999;30;1>Beenden[br]<#ffffff;17;1>Beende das aktuelle Spiel[br]und kehre zum Hauptmenü zurück.[br]<#6666ff;17;2>Deine Fortschritte werden gespeichert!" };
    for (int i = 0; i < ts.elements.length; i++)
    {
      if (tooltips[i] != null)
      {
        ts.elements[i].tooltip = new Tooltip(tooltips[i], ts.elements[i]);
        ts.elements[i].tooltip.setX(ts.elements[i].getX() + ts.elements[i].getWidth() + 9);
      }
    }
    
    c1 = new Container(0, 0, v.w.getWidth(), 55);
    c1.tileset = null;
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
    c1.draw(g, v);
    Assistant.drawHorizontallyCenteredString("Spiel pausiert", v.w.getWidth(), 43, g, 45, Color.white);
    
    ts.draw(g, v);
    if (notification != null)
      notification.draw(g, v.w);
  }
  
  public void save()
  {
    try
    {
      JSONObject save = sg.getPlayer().getData();
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
}
