package de.dakror.liturfaliar.ovscenes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.item.ItemDrop;
import de.dakror.liturfaliar.scenes.Scene_Game;
import de.dakror.liturfaliar.scenes.Scene_LoadGame;
import de.dakror.liturfaliar.scenes.Scene_MainMenu;
import de.dakror.liturfaliar.ui.Button;
import de.dakror.liturfaliar.ui.Container;
import de.dakror.liturfaliar.ui.HandleArea;
import de.dakror.liturfaliar.ui.Notification;
import de.dakror.liturfaliar.ui.ProgressBar;
import de.dakror.liturfaliar.ui.TextSelect;
import de.dakror.liturfaliar.ui.Tooltip;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.Database;
import de.dakror.liturfaliar.util.FileManager;

public class OVScene_Pause extends OVScene
{
  final String[] points         = { "Weiter", "Speichern", "Sichern", "Laden", "Beenden" };
  
  boolean        optionsToggle;
  
  Scene_Game     sg;
  TextSelect     ts;
  Container      c1;
  Notification   notification;
  ProgressBar[]  optionsSliders = new ProgressBar[2];
  Button         controls;
  HandleArea     optionsToggleArea;
  
  public OVScene_Pause(Scene_Game sg)
  {
    sg.setPaused(true);
    this.sg = sg;
  }
  
  @Override
  public void construct()
  {
    ts = new TextSelect(Viewport.w.getWidth() / 2 - 150, Viewport.w.getHeight() / 2 - (28 * points.length + 18) / 2, 300, 28 * points.length + 18, (Object[]) points);
    ts.soundCLICK = true;
    ts.soundMOVER = false;
    final String[] tooltips = { null, "<#999999;30;1>Speichern[br]<#ffffff;17;1>Manuelles Speichern deiner Fortschritte.", "<#999999;30;1>Sichern[br]<#ffffff;17;1>Es wird eine Kopie deines aktuellen Spielstands erstellt.", "<#999999;30;1>Laden[br]<#ffffff;17;1>Lade einen �lteren Spielstand.[br]<#6666ff;17;2>Deine Fortschritte werden [br]<#ff3333;17;2>NICHT<#6666ff;17;2> gespeichert![br]<#ff3333;17;2>Das aktuelle Spiel wird verlassen!"/* , "<#999999;30;1>Tastenbelegung[br]<#ffffff;17;1>Hier kannst du deine deine[br]Tastenbelegungen �ndern." */, "<#999999;30;1>Beenden[br]<#ffffff;17;1>Beende das aktuelle Spiel[br]und kehre zum Hauptmen� zur�ck.[br]<#6666ff;17;2>Deine Fortschritte werden gespeichert!" };
    for (int i = 0; i < ts.elements.length; i++)
    {
      if (tooltips[i] != null)
      {
        ts.elements[i].tooltip = new Tooltip(tooltips[i], ts.elements[i]);
        ts.elements[i].tooltip.setX(ts.elements[i].getX() + ts.elements[i].getWidth() + 9);
      }
    }
    
    c1 = new Container(0, 0, Viewport.w.getWidth(), 55);
    c1.tileset = null;
    
    optionsSliders[0] = new ProgressBar(Viewport.w.getWidth() / 2 - 100, Viewport.w.getHeight() - 90, 200, (float) Viewport.fSoundID, true, "ffc744", "Soundeffekte", true);
    optionsSliders[1] = new ProgressBar(Viewport.w.getWidth() / 2 - 100, Viewport.w.getHeight() - 68, 200, (float) Viewport.fMusicID, true, "ffc744", "Musik", true);
    controls = new Button(Viewport.w.getWidth() / 2 - 97, Viewport.w.getHeight() - 43, 195, "Tastenbelegung", Color.white, 18f);
    controls.tileset = null;
    controls.hovermod = 0;
    controls.clickmod = 0;
    controls.soundMOVER = false;
    optionsToggle = false;
    optionsToggleArea = new HandleArea(Viewport.w.getWidth() / 2 - 96, Viewport.w.getHeight() - 48, 192, 64);
  }
  
  @Override
  public void destruct()
  {}
  
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
        Viewport.setFramesFrozen(false);
        
        Viewport.setSceneEnabled(true);
        
        Viewport.removeOVScene("Pause");
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
        copy();
        notification = new Notification("Spielstand gesichert.", Notification.DEFAULT);
        break;
      }
      case 3:
      {
        Viewport.setScene(new Scene_LoadGame());
        break;
      }
      case 4:
      {
        save();
        Viewport.setScene(new Scene_MainMenu());
        break;
      }
    }
    if (optionsSliders[0] != null)
    {
      double vol = new BigDecimal(optionsSliders[0].value).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
      if (vol != Viewport.fSoundID)
      {
        Viewport.fSoundID = vol;
        FileManager.saveOptions();
      }
    }
    
    if (controls != null)
    {
      controls.update();
      if (controls.getState() == 1)
      {
        Viewport.removeOVScene("Pause");
        Viewport.addOVScene(new OVScene_Controls(), "Controls");
        controls.setState(0);
      }
    }
    
    if (optionsSliders[1] != null)
    {
      double vol = new BigDecimal(optionsSliders[1].value).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
      if (Viewport.fMusicID != vol)
      {
        Viewport.fMusicID = vol;
        Viewport.ss.setVolume(Viewport.MusicID, (float) Viewport.fMusicID);
        FileManager.saveOptions();
      }
    }
    
    optionsToggleArea.update();
    if (optionsToggleArea != null && optionsToggleArea.state == 1)
    {
      optionsToggle = !optionsToggle;
      optionsToggleArea.state = 0;
      optionsToggleArea = null;
    }
  }
  
  @Override
  public void draw(Graphics2D g)
  {
    Assistant.Shadow(Viewport.w.getBounds(), Color.black, 0.6f, g);
    c1.draw(g);
    Assistant.drawHorizontallyCenteredString("Spiel pausiert", Viewport.w.getWidth(), 43, g, 45, Color.white);
    
    ts.draw(g);
    
    // options
    if (optionsToggle)
    {
      Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), Viewport.w.getWidth() / 2 - 96, Viewport.w.getHeight() - 150, 192, 64, g);
      Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), Viewport.w.getWidth() / 2 - 128, Viewport.w.getHeight() - 102, 256, 96, g);
      Assistant.drawHorizontallyCenteredString("Optionen", Viewport.w.getWidth() / 2 - 96, 192, Viewport.w.getHeight() - 110, g, 26, Color.white);
      for (int i = 0; i < optionsSliders.length; i++)
      {
        optionsSliders[i].draw(g);
      }
      controls.draw(g);
      if (optionsToggleArea == null) optionsToggleArea = new HandleArea(Viewport.w.getWidth() / 2 - 96, Viewport.w.getHeight() - 150, 192, 64);
      
    }
    else
    {
      Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), Viewport.w.getWidth() / 2 - 96, Viewport.w.getHeight() - 48, 192, 64, g);
      Assistant.drawHorizontallyCenteredString("Optionen", Viewport.w.getWidth() / 2 - 96, 192, Viewport.w.getHeight() - 8, g, 26, Color.white);
      if (optionsToggleArea == null)
      {
        optionsToggleArea = new HandleArea(Viewport.w.getWidth() / 2 - 96, Viewport.w.getHeight() - 48, 192, 64);
      }
    }
    
    if (notification != null) notification.draw(g);
  }
  
  public void save()
  {
    FileManager.setSave(getData());
  }
  
  public void copy()
  {
    FileManager.copySave(getData());
  }
  
  public JSONObject getData()
  {
    JSONObject save = sg.getPlayer().getData();
    try
    {
      JSONObject mappack = save.getJSONObject("mappack");
      
      JSONArray itemDrops = new JSONArray();
      for (ItemDrop d : sg.getMapPack().getItemDrops(null))
      {
        itemDrops.put(d.serializeItemDrop());
      }
      mappack.put("drops", itemDrops);
      
      JSONObject pos = new JSONObject();
      pos.put("map", sg.getMapPack().getActiveMap().getName());
      pos.put("x", (int) sg.getPlayer().getPos().x);
      pos.put("y", (int) sg.getPlayer().getPos().y);
      mappack.put("pos", pos);
      
      mappack.put("cmaps", sg.getMapPack().getChangedMaps());
      
      save.put("mappack", mappack);
      
      save.put("flags", new JSONArray(Database.getBooleanVars()));
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
    
    return save;
  }
  
  @Override
  public void mousePressed(MouseEvent e)
  {
    ts.mousePressed(e);
    
    if (optionsToggle && optionsToggleArea.state == 0) for (int i = 0; i < optionsSliders.length; i++)
      optionsSliders[i].mousePressed(e);
    
    if (optionsToggle && optionsToggleArea.state == 0) controls.mousePressed(e);
  }
  
  @Override
  public void mouseReleased(MouseEvent e)
  {
    ts.mouseReleased(e);
    
    optionsToggleArea.mouseReleased(e);
    
    if (optionsToggle && optionsToggleArea.state == 0) controls.mouseReleased(e);
  }
  
  @Override
  public void mouseDragged(MouseEvent e)
  {
    ts.mouseDragged(e);
    
    if (optionsToggle && optionsToggleArea.state == 0)
    {
      for (int i = 0; i < optionsSliders.length; i++)
      {
        optionsSliders[i].mouseDragged(e);
      }
    }
  }
  
  @Override
  public void mouseMoved(MouseEvent e)
  {
    ts.mouseMoved(e);
    
    if (optionsToggle && optionsToggleArea.state == 0) controls.mouseMoved(e);
  }
  
  @Override
  public void mouseWheelMoved(MouseWheelEvent e)
  {
    ts.mouseWheelMoved(e);
  }
}
