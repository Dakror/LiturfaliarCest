package de.dakror.liturfaliar.scenes;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import org.json.JSONException;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.event.MapPackEventListener;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.MapPack;
import de.dakror.liturfaliar.map.creature.Player;
import de.dakror.liturfaliar.ovscenes.OVScene_Info;
import de.dakror.liturfaliar.ovscenes.OVScene_Inventory;
import de.dakror.liturfaliar.ovscenes.OVScene_Pause;
import de.dakror.liturfaliar.settings.CFG;
import de.dakror.liturfaliar.ui.CursorText;
import de.dakror.liturfaliar.ui.hud.BottomSegment;
import de.dakror.liturfaliar.ui.hud.TargetLabel;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.Database;
import de.dakror.liturfaliar.util.Handler;

public class Scene_Game implements Scene, MapPackEventListener
{
  Viewport        v;
  private MapPack mappack;
  private Player  player;
  private boolean pause;
  // -- HUD -- //
  TargetLabel     targetLabel;
  
  BottomSegment   bottomSegment;
  
  // --------- //
  
  @Override
  public void init(Viewport v)
  {
    this.v = v;
    v.setFramesFrozen(false);
    CFG.MAPCENTER = new Point((v.w.getWidth() / 2 - CFG.FIELDSIZE / 2), (v.w.getHeight() / 2 - CFG.FIELDSIZE * 3 / 4));
    
    player = new Player(v.savegame, v.w);
    Database.setStringVar("playername", player.getName());
    setMapPack(new MapPack(CFG.MAPPACK, v.w));
    mappack.addMapPackEventListener(this);
    try
    {
      mappack.setActiveMap(new Map(CFG.MAPPACK, v.savegame.getJSONObject("mappack").getJSONObject("pos").getString("map")));
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
    setPaused(false);
    // -- HUD -- //
    targetLabel = new TargetLabel();
    
    bottomSegment = new BottomSegment(player);
    // -- //
  }
  
  @Override
  public void update(long timePassed)
  {
    if (isPaused())
    {
      Assistant.setCursor(Viewport.loadImage("system/cursor.png"), v.w);
      return;
    }
    if (v.ovscenes.size() > 0 || (v.ovscenes.size() == 1 && v.ovscenes.get(0) instanceof OVScene_Info))
    {
      Assistant.setCursor(Viewport.loadImage("system/cursor.png"), v.w);
    }
    else
    {
      Assistant.setCursor(null, v.w);
    }
    mappack.getActiveMap().update(timePassed, this);
    // -- HUD -- //
    targetLabel.update(mappack.getActiveMap());
    
    bottomSegment.update(mappack.getActiveMap());
    // --------- //
  }
  
  @Override
  public void draw(Graphics2D g)
  {
    mappack.getActiveMap().draw(g, v);
    // -- HUD -- //
    targetLabel.draw(g, v, mappack.getActiveMap());
    
    bottomSegment.draw(g, v, mappack.getActiveMap());
    // --------- //
  }
  
  @Override
  public void keyPressed(KeyEvent e)
  {
    if (mappack != null && mappack.getActiveMap() != null)
      mappack.getActiveMap().keyPressed(e);
  }
  
  @Override
  public void keyReleased(KeyEvent e)
  {
    if (mappack != null && mappack.getActiveMap() != null)
      mappack.getActiveMap().keyReleased(e);
    if (e.getExtendedKeyCode() == KeyEvent.VK_ESCAPE)
    {
      v.toggleOVScene(new OVScene_Pause(this), "Pause");
      Handler.setListenerEnabled(this, !isPaused());
      v.setFramesFrozen(isPaused());
      if (isPaused())
        v.playSound("008-System08");
    }
    else if (e.getExtendedKeyCode() == KeyEvent.VK_I)
    {
      v.toggleOVScene(new OVScene_Inventory(this), "Inventory");
      Handler.setListenerEnabled(this, !isPaused());
      v.setFramesFrozen(isPaused());
    }
  }
  
  public boolean isPaused()
  {
    return pause;
  }
  
  public void setPaused(boolean pause)
  {
    this.pause = pause;
  }
  
  public MapPack getMapPack()
  {
    return mappack;
  }
  
  public void setMapPack(MapPack mappack)
  {
    this.mappack = mappack;
  }
  
  @Override
  public void onMapChange(Map oldmap, Map newmap)
  {
    Database.removeDatabaseEventListener(oldmap);
    newmap.creatures.add(player);
    if (!(newmap.getMusic() + ".wav").equals(v.MusicID) && newmap.getMusic().length() > 0)
      v.playMusic(newmap.getMusic(), true);
  }
  
  @Override
  public void mousePressed(MouseEvent e)
  {
    if (mappack != null && mappack.getActiveMap() != null)
      mappack.getActiveMap().mousePressed(e);
  }
  
  @Override
  public void mouseMoved(MouseEvent e)
  {
    if (mappack != null && mappack.getActiveMap() != null)
      mappack.getActiveMap().mouseMoved(e);
    CursorText.mouseMoved(e);
  }
  
  @Override
  public void mouseDragged(MouseEvent e)
  {
    if (mappack != null && mappack.getActiveMap() != null)
      mappack.getActiveMap().mouseDragged(e);
  }
  
  @Override
  public void mouseWheelMoved(MouseWheelEvent arg0)
  {}
  
  @Override
  public void mouseClicked(MouseEvent e)
  {
    if (mappack != null && mappack.getActiveMap() != null)
      mappack.getActiveMap().mouseClicked(e);
  }
  
  @Override
  public void mouseEntered(MouseEvent e)
  {
    if (mappack != null && mappack.getActiveMap() != null)
      mappack.getActiveMap().mouseEntered(e);
  }
  
  @Override
  public void mouseExited(MouseEvent e)
  {
    if (mappack != null && mappack.getActiveMap() != null)
      mappack.getActiveMap().mouseExited(e);
  }
  
  @Override
  public void mouseReleased(MouseEvent e)
  {
    if (mappack != null && mappack.getActiveMap() != null)
      mappack.getActiveMap().mouseReleased(e);
  }
  
  @Override
  public void keyTyped(KeyEvent arg0)
  {}
}
