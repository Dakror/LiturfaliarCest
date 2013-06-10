package de.dakror.liturfaliar.util;

import java.awt.Window;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.map.MapPack;
import de.dakror.liturfaliar.settings.CFG;

/**
 * Manages SaveFiles, Creating needed directories, Saving and loading Options, etc.
 * 
 * @author Dakror
 */
public class FileManager
{
  public static File  dir;
  public static File  saves;
  public static float progress;
  
  /**
   * Creates the needed directories in the appdata.
   * 
   * @param v - {@link Viewport} is needed for a Callback.
   */
  public static void mk(Viewport v)
  {
    dir = new File("C:/Dakror/Liturfaliar Cest");
    saves = new File("C:/Dakror/Liturfaliar Cest/Saves");
    dir.mkdirs();
    saves.mkdirs();
    if (!new File(dir, "options.json").exists() && v != null)
      saveOptions(v);
  }
  
  public static File getDir()
  {
    return dir;
  }
  
  /**
   * @param savename
   * @return The loaded savegame as {@link JSONObject}.
   */
  public static JSONObject getSave(String savename)
  {
    JSONObject result = null;
    try
    {
      result = new JSONObject(Compressor.decompressFile(new File(saves, savename + ".save")));
    }
    catch (Exception e)
    {
      return null;
    }
    return result;
  }
  
  public static boolean doesSaveExist(String savename)
  {
    return new File(saves, savename + ".save").exists();
  }
  
  public static void deleteSave(JSONObject save)
  {
    try
    {
      File f = new File(saves, save.getJSONObject("char").getString("name") + ".save");
      f.delete();
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
  }
  
  public static void setSave(JSONObject save)
  {
    try
    {
      Compressor.compressFile(new File(saves, save.getJSONObject("char").getString("name") + ".save"), save.toString());
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
  }
  
  /**
   * @return A list of all savegames as {@link JSONObject}.
   */
  public static JSONObject[] getSaves()
  {
    JSONObject[] result = new JSONObject[saves.listFiles().length];
    File[] files = saves.listFiles(new FileFilter()
    {
      @Override
      public boolean accept(File pathname)
      {
        return pathname.isFile() && pathname.getName().endsWith(".save");
      }
    });
    List<File> list = Arrays.asList(files);
    Collections.sort(list, new Comparator<File>()
    {
      @Override
      public int compare(File o1, File o2)
      {
        return (int) ((o2.lastModified() / 1000.0f) - (o1.lastModified() / 1000.0f));
      }
    });
    files = list.toArray(new File[] {});
    for (int i = 0; i < files.length; i++)
    {
      result[i] = getSave(files[i].getName().replace(".save", ""));
      try
      {
        result[i].put("lastplayed", files[i].lastModified());
      }
      catch (JSONException e)
      {
        e.printStackTrace();
      }
    }
    return result;
  }
  
  public static File[] getSaveFiles()
  {
    return saves.listFiles();
  }
  
  /**
   * Savess the options in "options.json" from the fields of {@link Viewport}.
   * 
   * @param v - {@link Viewport} with the fields to save.
   */
  public static void saveOptions(Viewport v)
  {
    JSONObject options = new JSONObject();
    try
    {
      options.put("sound", v.fSoundID);
      options.put("music", v.fMusicID);
      options.put("musiceffect", v.fMusicEffectID);
      Assistant.setFileContent(new File(dir, "options.json"), options.toString(4));
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return;
    }
  }
  
  /**
   * Loads the options from "options.json" and sets the fields of {@link Viewport}.
   * 
   * @param v - {@link Viewport} with the fields to be set.
   */
  public static void loadOptions(Viewport v)
  {
    try
    {
      JSONObject options = new JSONObject(Assistant.getFileContent(new File(dir, "options.json")));
      v.fSoundID = (float) options.getDouble("sound");
      v.fMusicID = (float) options.getDouble("music");
      v.fMusicEffectID = (float) options.getDouble("musiceffect");
    }
    catch (Exception e)
    {
      saveOptions(v);
      loadOptions(v);
    }
  }
  
  public static boolean checkMapPackUpdate(Window w)
  {
    if (!new File(dir, "Maps").exists() || !Arrays.asList(MapPack.getMapPacks("Maps")).contains(CFG.MAPPACK))
    {
      new File(dir, "Maps").mkdir();
      return true;
    }
    long version = 0;
    try
    {
      version = new JSONObject(Assistant.getURLContent(new URL("http://liturfaliar.dakror.de/GAMECONTENT/mappack/pack.json"))).getLong("version");
    }
    catch (Exception e)
    {
      return false;
    }
    return version - new MapPack(CFG.MAPPACK, w).getVersion() > 0;
  }
  
  public static ZipAssistant onMapPackUpdate(Window w)
  {
    if (checkMapPackUpdate(w))
    {
      try
      {
        ZipAssistant thread = new ZipAssistant(new URL("http://liturfaliar.dakror.de/GAMECONTENT/mappack/" + CFG.MAPPACK.replaceAll(" ", "%20") + ".zip"), new File(dir, "Maps"));
        thread.start();
        return thread;
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    return null;
  }
  
  public static boolean checkMediaUpdate(String dir)
  {
    new File(FileManager.dir, "Music").mkdir();
    new File(FileManager.dir, "Sound").mkdir();
    new File(FileManager.dir, "Animations").mkdir();
    new File(FileManager.dir, "Tiles").mkdir();
    if (dir == "Sound")
    {
      if (new File(FileManager.dir, dir).listFiles().length < CFG.SOUND.length)
      {
        return true;
      }
    }
    else if (dir == "Music")
    {
      if (new File(FileManager.dir, dir).listFiles().length < CFG.MUSIC.length)
      {
        return true;
      }
    }
    else if (dir == "Animations")
    {
      if (new File(FileManager.dir, dir).listFiles().length < CFG.ANIMATIONS)
      {
        return true;
      }
    }
    else if (dir == "Tiles")
    {
      if (new File(FileManager.dir, dir).listFiles().length < CFG.TILES)
      {
        return true;
      }
    }
    return false;
  }
  
  public static ZipAssistant onMediaUpdate(String dir)
  {
    if (checkMediaUpdate(dir))
    {
      try
      {
        ZipAssistant thread = new ZipAssistant(new URL("http://liturfaliar.dakror.de/GAMECONTENT/" + dir + ".zip"), new File(FileManager.dir, dir));
        thread.start();
        return thread;
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    return null;
  }
  
  public static URL pullMediaFile(String dir, String file)
  {
    File f = new File(FileManager.dir, dir + "/" + file);
    if (!f.exists())
    {
      System.err.println("missing media file: " + dir + "/" + file);
    }
    else
    {
      try
      {
        return f.toURI().toURL();
      }
      catch (MalformedURLException e)
      {
        e.printStackTrace();
      }
    }
    return null;
  }
  
  public static String[] getCharParts(String dir)
  {
    
    try
    {
      return new File(FileManager.class.getResource("/img/char/" + dir).toURI()).list(new FilenameFilter()
      {
        
        @Override
        public boolean accept(File dir, String name)
        {
          if (name.matches("_.{1}\\.png"))
            return name.endsWith("_f.png");
          
          return true;
        }
      });
    }
    catch (URISyntaxException e)
    {
      e.printStackTrace();
      return null;
    }
  }
}
