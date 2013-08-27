package de.dakror.liturfaliar.util;

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
import de.dakror.liturfaliar.settings.Keys;

/**
 * Manages SaveFiles, Creating needed directories, Saving and loading Options, etc.
 * 
 * @author Dakror
 */
public class FileManager
{
	public static File dir;
	public static File saves;
	public static float progress;
	
	/**
	 * Creates the needed directories in the appdata.
	 * 
	 * @param v - {@link Viewport} is needed for a Callback.
	 */
	public static void mk(boolean save)
	{
		dir = new File(System.getProperty("user.home") + "/.dakror/Liturfaliar Cest");
		saves = new File(dir, "Saves");
		if (CFG.DEBUG) CFG.ccs();
		
		dir.mkdirs();
		saves.mkdir();
		if (!new File(dir, "options.json").exists() && save) saveOptions();
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
	
	public static void deleteSave(JSONObject save, boolean backup)
	{
		try
		{
			File f = new File(saves, save.getJSONObject("char").getString("name") + ".save");
			if (backup) f = new File(saves, save.getJSONObject("char").getString("name") + " - Sicherung.save");
			
			f.delete();
			
			if (new File(f.getPath() + ".debug").exists()) new File(f.getPath() + ".debug").delete();
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void copySave(JSONObject save)
	{
		try
		{
			Compressor.compressFile(new File(saves, save.getJSONObject("char").getString("name") + " - Sicherung.save"), save.toString());
			if (CFG.DEBUG) Assistant.setFileContent(new File(saves, save.getJSONObject("char").getString("name") + " - Sicherung.save.debug"), save.toString());
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
			if (CFG.DEBUG) Assistant.setFileContent(new File(saves, save.getJSONObject("char").getString("name") + ".save.debug"), save.toString());
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
		File[] files = saves.listFiles(new FileFilter()
		{
			@Override
			public boolean accept(File pathname)
			{
				return pathname.isFile() && pathname.getName().endsWith(".save");
			}
		});
		
		JSONObject[] result = new JSONObject[files.length];
		
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
				result[i].put("savename", files[i].getName().replace(".save", ""));
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
	public static void saveOptions()
	{
		JSONObject o = new JSONObject();
		try
		{
			JSONObject volume = new JSONObject();
			volume.put("sound", Viewport.fSoundID);
			volume.put("music", Viewport.fMusicID);
			volume.put("musiceffect", Viewport.fMusicEffectID);
			
			o.put("volume", volume);
			o.put("keys", Keys.saveKeys());
			
			Assistant.setFileContent(new File(dir, "options.json"), o.toString(4));
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
	public static void loadOptions()
	{
		try
		{
			JSONObject o = new JSONObject(Assistant.getFileContent(new File(dir, "options.json")));
			JSONObject volume = o.getJSONObject("volume");
			Viewport.fSoundID = (float) volume.getDouble("sound");
			Viewport.fMusicID = (float) volume.getDouble("music");
			Viewport.fMusicEffectID = (float) volume.getDouble("musiceffect");
			
			Keys.loadKeys(o.getJSONObject("keys"));
		}
		catch (Exception e)
		{
			saveOptions();
			loadOptions();
		}
	}
	
	public static boolean checkMapPackUpdate()
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
		return version - new MapPack(CFG.MAPPACK).getVersion() > 0;
	}
	
	public static ZipAssistant onMapPackUpdate()
	{
		if (checkMapPackUpdate())
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
		String checksum = Assistant.getFolderChecksum(new File(FileManager.dir, dir));
		
		try
		{
			if (!checksum.equals(CFG.class.getField(dir.toUpperCase() + "_CS").get(null))) return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
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
			CFG.p(dir);
			return new File(FileManager.class.getResource("/img/char/" + dir).toURI()).list(new FilenameFilter()
			{
				
				@Override
				public boolean accept(File dir, String name)
				{
					if (name.matches("_.{1}\\.png")) return name.endsWith("_f.png");
					
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
	
	public static String[] getMediaFiles(String dir)
	{
		return new File(FileManager.dir, dir).list();
	}
}
