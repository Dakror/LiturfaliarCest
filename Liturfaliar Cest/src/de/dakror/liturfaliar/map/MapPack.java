package de.dakror.liturfaliar.map;

import java.awt.Window;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.map.event.MapPackEventListener;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.FileManager;

public class MapPack
{
  private ArrayList<MapPackEventListener> mappackeventlisteners;
  private Map                             activeMap;
  private JSONObject                      data;
  
  public MapPack(String name, Window w)
  {
    try
    {
      // -- loading maps -- //
      data = new JSONObject(Assistant.getFileContent(new File(FileManager.dir, "Maps/" + name + "/pack.json")));
      mappackeventlisteners = new ArrayList<MapPackEventListener>();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      System.exit(0);
    }
  }
  
  public void addMapPackEventListener(MapPackEventListener mpel)
  {
    mappackeventlisteners.add(mpel);
  }
  
  public void removeMapPackEventListener(MapPackEventListener mpel)
  {
    mappackeventlisteners.set(mappackeventlisteners.indexOf(mpel), null);
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
          if (new File(pathname, "pack.json").exists())
          {
            try
            {
              String content = Assistant.getFileContent(new File(pathname, "pack.json"));
              JSONObject json = new JSONObject(content);
              if (json.has("init"))
                return true;
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
    for (MapPackEventListener mpel : mappackeventlisteners)
    {
      if (mpel != null)
        mpel.onMapChange(activeMap, activeMap);
    }
    this.activeMap = activeMap;
    this.activeMap.setMapPack(this);
  }
}
