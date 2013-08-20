package de.dakror.liturfaliar.map;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.event.Dispatcher;
import de.dakror.liturfaliar.event.Events;
import de.dakror.liturfaliar.item.ItemDrop;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.FileManager;

public class MapPack
{
  private Map                 activeMap;
  private JSONObject          data;
  private ArrayList<ItemDrop> itemDrops   = new ArrayList<ItemDrop>();
  private JSONObject          changedMaps = new JSONObject();
  
  public MapPack(String name)
  {
    try
    {
      // -- loading maps -- //
      data = new JSONObject(Assistant.getFileContent(new File(FileManager.dir, "Maps/" + name + "/.pack")));
    }
    catch (Exception e)
    {
      e.printStackTrace();
      System.exit(0);
    }
  }
  
  public String getName()
  {
    try
    {
      return getData().getString("name");
    }
    catch (JSONException e)
    {
      return null;
    }
  }
  
  public long getVersion()
  {
    try
    {
      return getData().getLong("version");
    }
    catch (JSONException e)
    {
      return 0;
    }
  }
  
  public JSONObject getData()
  {
    return data;
  }
  
  public static String[] getMapPacks(String dir)
  {
    File[] files = new File(FileManager.dir, dir).listFiles(new FileFilter()
    {
      @Override
      public boolean accept(File pathname)
      {
        if (pathname.isDirectory())
        {
          if (new File(pathname, ".pack").exists())
          {
            try
            {
              String content = Assistant.getFileContent(new File(pathname, ".pack"));
              JSONObject json = new JSONObject(content);
              if (json.has("init")) return true;
              else return false;
            }
            catch (Exception e)
            {
              return false;
            }
          }
          else return false;
        }
        else return false;
      }
    });
    return Assistant.getFileNames(files, false);
  }
  
  public Map getActiveMap()
  {
    return activeMap;
  }
  
  public void setActiveMap(Map activeMap)
  {
    Dispatcher.dispatch(Events.mapChanged, this.activeMap, activeMap);
    
    if (this.activeMap != null) addChangedMap(this.activeMap);
    
    this.activeMap = activeMap;
    if (getChangedMap(activeMap.getName()) != null)
    {
      activeMap.loadMap(getChangedMap(activeMap.getName()));
    }
    this.activeMap.setMapPack(this);
  }
  
  public void addChangedMap(Map m)
  {
    try
    {
      changedMaps.put(m.getName(), m.serializeMap());
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
  }
  
  public JSONObject getChangedMap(String name)
  {
    try
    {
      return changedMaps.getJSONObject(name);
    }
    catch (JSONException e)
    {
      return null;
    }
  }
  
  public JSONObject getChangedMaps()
  {
    addChangedMap(activeMap);
    return changedMaps;
  }
  
  public void setChangedMaps(JSONObject o)
  {
    changedMaps = o;
  }
  
  public void addItemDrop(ItemDrop d)
  {
    itemDrops.add(d);
  }
  
  public void removeItemDrop(ItemDrop d)
  {
    itemDrops.remove(d);
  }
  
  public ArrayList<ItemDrop> getItemDrops(Map m)
  {
    if (m == null) return itemDrops;
    
    ArrayList<ItemDrop> drops = new ArrayList<ItemDrop>();
    
    for (ItemDrop d : itemDrops)
    {
      if (d.getMap().equals(m.getName())) drops.add(d);
    }
    return drops;
  }
}
