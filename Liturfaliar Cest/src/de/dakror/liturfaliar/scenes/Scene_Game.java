package de.dakror.liturfaliar.scenes;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.event.dispatcher.DatabaseEventDispatcher;
import de.dakror.liturfaliar.event.dispatcher.MapPackEventDispatcher;
import de.dakror.liturfaliar.event.dispatcher.PlayerEventDispatcher;
import de.dakror.liturfaliar.event.dispatcher.PlayerHotbarEventDispatcher;
import de.dakror.liturfaliar.event.listener.MapPackEventListener;
import de.dakror.liturfaliar.event.listener.PlayerEventListener;
import de.dakror.liturfaliar.event.listener.PlayerHotbarEventListener;
import de.dakror.liturfaliar.item.Categories;
import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.item.ItemDrop;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.MapPack;
import de.dakror.liturfaliar.map.creature.Player;
import de.dakror.liturfaliar.ovscenes.OVScene_Inventory;
import de.dakror.liturfaliar.ovscenes.OVScene_Pause;
import de.dakror.liturfaliar.ovscenes.OVScene_Skills;
import de.dakror.liturfaliar.settings.Attributes.Attr;
import de.dakror.liturfaliar.settings.CFG;
import de.dakror.liturfaliar.ui.CursorText;
import de.dakror.liturfaliar.ui.ItemSlot;
import de.dakror.liturfaliar.ui.hud.BottomSegment;
import de.dakror.liturfaliar.ui.hud.TargetLabel;
import de.dakror.liturfaliar.util.Database;

public class Scene_Game implements Scene, MapPackEventListener, PlayerHotbarEventListener, PlayerEventListener
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
  public void construct(Viewport v)
  {
    this.v = v;
    PlayerHotbarEventDispatcher.addPlayerHotbarEventListener(this);
    PlayerEventDispatcher.addPlayerEventListener(this);
    v.setFramesFrozen(false);
    CFG.MAPCENTER = new Point((v.w.getWidth() / 2 - CFG.FIELDSIZE / 2), (v.w.getHeight() / 2 - CFG.FIELDSIZE * 3 / 4));
    
    player = new Player(v.savegame, v.w);
    Database.setStringVar("playername", player.getName());
    setMapPack(new MapPack(CFG.MAPPACK, v.w));
    MapPackEventDispatcher.addMapPackEventListener(this);
    try
    {
      JSONArray itemDrops = v.savegame.getJSONObject("mappack").getJSONArray("drops");
      
      for (int i = 0; i < itemDrops.length(); i++)
      {
        JSONObject o = itemDrops.getJSONObject(i);
        mappack.addItemDrop(new ItemDrop(new Item(o.getJSONObject("item")), o.getInt("x"), o.getInt("y"), o.getString("map")));
      }
      
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
  
  public Player getPlayer()
  {
    return player;
  }
  
  @Override
  public void update(long timePassed)
  {
    if (!v.areFramesFrozen())
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
    if (e.equals(v.skipEvent))
    {
      v.skipEvent = null;
      return;
    }
    
    if (mappack != null && mappack.getActiveMap() != null)
      mappack.getActiveMap().keyReleased(e);
    if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
      togglePaused();
    
    else if (e.getKeyCode() == KeyEvent.VK_I)
    {
      v.removeOVScene("Skills");
      v.toggleOVScene(new OVScene_Inventory(this), "Inventory");
    }
    
    else if (e.getKeyCode() == KeyEvent.VK_J)
    {
      v.removeOVScene("Inventory");
      v.toggleOVScene(new OVScene_Skills(this), "Skills");
    }
    
    if (bottomSegment != null)
      bottomSegment.keyReleased(e, mappack.getActiveMap());
  }
  
  public boolean isPaused()
  {
    return pause;
  }
  
  public void togglePaused()
  {
    v.toggleOVScene(new OVScene_Pause(this), "Pause");
    
    Viewport.sceneEnabled = !pause;
    
    v.setFramesFrozen(isPaused());
    if (isPaused())
      v.playSound("008-System08");
  }
  
  public void setPaused(boolean pause)
  {
    this.pause = pause;
    Viewport.sceneEnabled = !pause;
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
  public void mapChanged(Map oldmap, Map newmap)
  {
    DatabaseEventDispatcher.removeDatabaseEventListener(oldmap);
    
    newmap.setPlayer(player);
    if (!(newmap.getMusic() + ".wav").equals(v.MusicID) && newmap.getMusic().length() > 0)
      v.playMusic(newmap.getMusic(), true);
  }
  
  @Override
  public void mousePressed(MouseEvent e)
  {
    if (mappack != null && mappack.getActiveMap() != null)
      mappack.getActiveMap().mousePressed(e, v);
    
    if (bottomSegment != null)
      bottomSegment.mousePressed(e, mappack.getActiveMap());
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
  
  @Override
  public void slotTriggered(int index, ItemSlot slot)
  {
    if (slot.getItem() == null)
      return;
    
    if (slot.getItem().getType().getCategory().equals(Categories.CONSUMABLE))
    {
      slot.getItem().triggerAction(mappack.getActiveMap(), v);
      player.getEquipment().setHotbarItem(index, slot.getItem());
    }
  }
  
  @Override
  public void destruct()
  {
    MapPackEventDispatcher.removeMapPackEventListener(this);
    PlayerHotbarEventDispatcher.removePlayerHotbarEventListener(this);
    PlayerEventDispatcher.removePlayerEventListener(this);
  }
  
  @Override
  public void levelUp(int oldLevel)
  {
    v.playSound("111-Heal07");
    player.getAttributes().getAttribute(Attr.skillpoint).increase((int) Math.floor(player.getLevel() / 10.0) + 1);
    Database.setStringVar("player_sp", "" + (int) player.getAttributes().getAttribute(Attr.skillpoint).getValue());
    Database.setStringVar("player_level", "" + player.getLevel());
  }
}
