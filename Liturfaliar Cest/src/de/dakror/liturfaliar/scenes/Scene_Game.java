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
import de.dakror.liturfaliar.event.Dispatcher;
import de.dakror.liturfaliar.event.Event;
import de.dakror.liturfaliar.event.Events;
import de.dakror.liturfaliar.event.Listener;
import de.dakror.liturfaliar.fx.Animation;
import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.item.ItemDrop;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.MapPack;
import de.dakror.liturfaliar.map.creature.NPC;
import de.dakror.liturfaliar.map.creature.Player;
import de.dakror.liturfaliar.ovscenes.OVScene_Death;
import de.dakror.liturfaliar.ovscenes.OVScene_Inventory;
import de.dakror.liturfaliar.ovscenes.OVScene_Pause;
import de.dakror.liturfaliar.ovscenes.OVScene_Skills;
import de.dakror.liturfaliar.settings.Attributes.Attr;
import de.dakror.liturfaliar.settings.CFG;
import de.dakror.liturfaliar.settings.Keys;
import de.dakror.liturfaliar.ui.ItemSlot;
import de.dakror.liturfaliar.ui.hud.BottomSegment;
import de.dakror.liturfaliar.ui.hud.PlayerHotbar;
import de.dakror.liturfaliar.ui.hud.TargetLabel;
import de.dakror.liturfaliar.util.Database;

public class Scene_Game implements Scene, Listener
{
  private MapPack       mappack;
  private Player        player;
  
  // -- HUD -- //
  private TargetLabel   targetLabel;
  private BottomSegment bottomSegment;
  
  private boolean       pause;
  private boolean       ctrlDown;
  
  public long           inventoryLastClosed;
  
  @Override
  public void construct()
  {
    Dispatcher.addListener(this);
    Viewport.setFramesFrozen(false);
    CFG.MAPCENTER = new Point((Viewport.w.getWidth() / 2 - CFG.FIELDSIZE / 2), (Viewport.w.getHeight() / 2));
    
    player = new Player(Viewport.savegame);
    
    Database.setStringVar("playername", player.getName());
    setMapPack(new MapPack(CFG.MAPPACK));
    try
    {
      JSONArray flags = Viewport.savegame.getJSONArray("flags");
      for (int i = 0; i < flags.length(); i++)
      {
        Database.setBooleanVar(flags.getString(i), true);
      }
      
      mappack.setChangedMaps(Viewport.savegame.getJSONObject("mappack").getJSONObject("cmaps"));
      
      JSONArray itemDrops = Viewport.savegame.getJSONObject("mappack").getJSONArray("drops");
      
      for (int i = 0; i < itemDrops.length(); i++)
      {
        JSONObject o = itemDrops.getJSONObject(i);
        mappack.addItemDrop(new ItemDrop(new Item(o.getJSONObject("item")), o.getInt("x"), o.getInt("y"), o.getInt("z"), o.getString("map")));
      }
      
      mappack.setActiveMap(new Map(CFG.MAPPACK, Viewport.savegame.getJSONObject("mappack").getJSONObject("pos").getString("map")));
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
    setPaused(false);
    // -- HUD -- //
    targetLabel = new TargetLabel();
    
    bottomSegment = new BottomSegment(player);
    Dispatcher.addListener(bottomSegment.hotbar);
    
    inventoryLastClosed = System.currentTimeMillis();
  }
  
  public Player getPlayer()
  {
    return player;
  }
  
  @Override
  public void update(long timePassed)
  {
    if (!player.isAlive() && !isPaused())
    {
      Viewport.addOVScene(new OVScene_Death(this), "Death");
      setPaused(true);
      Viewport.stopMusic();
      Viewport.playSound("186-Death");
      Viewport.setSceneEnabled(false);
    }
    
    if (!Viewport.areFramesFrozen())
    {
      mappack.getActiveMap().update(timePassed, this);
      
      targetLabel.update(timePassed, mappack.getActiveMap());
    }
    
    bottomSegment.update(timePassed, mappack.getActiveMap());
  }
  
  @Override
  public void draw(Graphics2D g)
  {
    mappack.getActiveMap().draw(g);
    // -- HUD -- //
    targetLabel.draw(g, mappack.getActiveMap());
    
    bottomSegment.draw(g, mappack.getActiveMap());
  }
  
  @Override
  public void keyPressed(KeyEvent e)
  {
    if (mappack != null && mappack.getActiveMap() != null) mappack.getActiveMap().keyPressed(e);
    
    if (e.getKeyCode() == KeyEvent.VK_CONTROL) ctrlDown = true;
  }
  
  @Override
  public void keyReleased(KeyEvent e)
  {
    if (e.equals(Viewport.skipEvent))
    {
      Viewport.skipEvent = null;
      return;
    }
    
    if (mappack != null && mappack.getActiveMap() != null) mappack.getActiveMap().keyReleased(e);
    
    if (e.getKeyCode() == Keys.PAUSE) togglePaused();
    
    else if (e.getKeyCode() == Keys.INVENTORY)
    {
      Viewport.removeOVScene("Skills");
      Viewport.toggleOVScene(new OVScene_Inventory(this), "Inventory");
    }
    
    else if (e.getKeyCode() == Keys.SKILLS)
    {
      Viewport.removeOVScene("Inventory");
      Viewport.toggleOVScene(new OVScene_Skills(this), "Skills");
    }
    
    if (bottomSegment != null) bottomSegment.keyReleased(e, mappack.getActiveMap());
    
    if (e.getKeyCode() == KeyEvent.VK_CONTROL) ctrlDown = false;
  }
  
  public boolean isPaused()
  {
    return pause;
  }
  
  public void togglePaused()
  {
    Viewport.toggleOVScene(new OVScene_Pause(this), "Pause");
    
    Viewport.setSceneEnabled(!pause);
    
    Viewport.setFramesFrozen(isPaused());
    if (isPaused()) Viewport.playSound("008-System08");
  }
  
  public void setPaused(boolean pause)
  {
    this.pause = pause;
    if (pause) player.disableDirs();
    
    if (bottomSegment != null) bottomSegment.hotbar.frozen = pause;
    
    Viewport.setSceneEnabled(!pause);
    Viewport.setFramesFrozen(pause);
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
  public void onEvent(Event e)
  {
    if (e.equals(Events.mapChanged))
    {
      Map newmap = (Map) e.getParam("new");
      Dispatcher.removeListener((Map) e.getParam("old"));
      newmap.setPlayer(player);
      mappack.getActiveMap().centerOnPlayer(player);
      if (!(newmap.getMusic() + ".wav").equals(Viewport.MusicID) && newmap.getMusic().length() > 0) Viewport.playMusic(newmap.getMusic(), true);
    }
    else if (e.equals(Events.slotTriggered))
    {
      int index = (int) e.getParam("index");
      ItemSlot slot = (ItemSlot) e.getParam("slot");
      if (slot.getItem() == null) return;
      
      // -- is LMB -- //
      if (index == PlayerHotbar.KEYSLOTS.length)
      {
        if (targetLabel.getTarget() == null || !((NPC) targetLabel.getTarget()).isHostile()) if (!ctrlDown)
        {
          player.resetTarget();
          return;
        }
      }
      
      slot.triggerAction(mappack.getActiveMap(), player);
      player.getEquipment().setHotbarItem(index, slot.getItem());
    }
    else if (e.equals(Events.levelUp))
    {
      Viewport.playSound("105-Heal01");
      mappack.getActiveMap().playAnimation(new Animation(-25, -30, 80, 0, 10, 0.35f, false, "Heal3.png", player));
      player.getAttributes().getAttribute(Attr.skillpoint).increase((int) Math.floor(player.getLevel() / 10.0) + 1);
      Database.setStringVar("player_sp", "" + (int) player.getAttributes().getAttribute(Attr.skillpoint).getValue());
      Database.setStringVar("player_level", "" + player.getLevel());
    }
  }
  
  @Override
  public void mousePressed(MouseEvent e)
  {
    if (mappack != null && mappack.getActiveMap() != null) mappack.getActiveMap().mousePressed(e);
    
    if (bottomSegment != null && mappack.getActiveMap().talk == null) bottomSegment.mousePressed(e, mappack.getActiveMap());
  }
  
  @Override
  public void mouseMoved(MouseEvent e)
  {
    if (mappack != null && mappack.getActiveMap() != null) mappack.getActiveMap().mouseMoved(e);
    
    if (targetLabel != null) targetLabel.mouseMoved(e, mappack.getActiveMap());
    
    if (bottomSegment != null) bottomSegment.mouseMoved(e, mappack.getActiveMap());
  }
  
  @Override
  public void mouseDragged(MouseEvent e)
  {
    if (mappack != null && mappack.getActiveMap() != null) mappack.getActiveMap().mouseDragged(e);
  }
  
  @Override
  public void mouseWheelMoved(MouseWheelEvent arg0)
  {}
  
  @Override
  public void mouseClicked(MouseEvent e)
  {
    if (mappack != null && mappack.getActiveMap() != null) mappack.getActiveMap().mouseClicked(e);
  }
  
  @Override
  public void mouseEntered(MouseEvent e)
  {
    if (mappack != null && mappack.getActiveMap() != null) mappack.getActiveMap().mouseEntered(e);
  }
  
  @Override
  public void mouseExited(MouseEvent e)
  {
    if (mappack != null && mappack.getActiveMap() != null) mappack.getActiveMap().mouseExited(e);
  }
  
  @Override
  public void mouseReleased(MouseEvent e)
  {
    if (mappack != null && mappack.getActiveMap() != null) mappack.getActiveMap().mouseReleased(e);
    
    if (bottomSegment != null) bottomSegment.mouseReleased(e, mappack.getActiveMap());
  }
  
  @Override
  public void keyTyped(KeyEvent arg0)
  {}
  
  @Override
  public void destruct()
  {
    Dispatcher.removeListener(this);
    Dispatcher.removeListener(bottomSegment.hotbar);
  }
}
