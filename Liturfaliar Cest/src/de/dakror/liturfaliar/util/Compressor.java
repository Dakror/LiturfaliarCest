package de.dakror.liturfaliar.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.settings.CFG;

public class Compressor
{
  public static void compressFile(File f, String s)
  {
    compressFile(f, (s + ((s.length() < 18) ? "                 " : "")).getBytes());
  }
  
  public static void compressFile(File f, byte[] input)
  {
    byte[] length = ByteBuffer.allocate(4).putInt(input.length).array();
    byte[] buffer = new byte[input.length];
    Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
    deflater.setInput(input);
    deflater.finish();
    int len = deflater.deflate(buffer);
    byte[] compr = Arrays.copyOf(buffer, len);
    byte[] output = new byte[compr.length + 4];
    System.arraycopy(length, 0, output, 0, length.length);
    System.arraycopy(compr, 0, output, 4, compr.length);
    setFileContent(f, output);
  }
  
  public static byte[] decompress(byte[] b)
  {
    try
    {
      int length = ByteBuffer.wrap(Arrays.copyOf(b, 4)).getInt();
      Inflater inflater = new Inflater();
      inflater.setInput(Arrays.copyOfRange(b, 4, b.length));
      byte[] buf = new byte[length];
      inflater.inflate(buf);
      inflater.end();
      return buf;
    }
    catch (DataFormatException e)
    {
      e.printStackTrace();
      return null;
    }
  }
  
  public static String decompressFile(File f)
  {
    byte[] decompressed = decompress(getFileContentAsByteArray(f));
    String text = new String(decompressed);
    return text;
  }
  
  public static void setFileContent(File f, byte[] b)
  {
    try
    {
      f.createNewFile();
      
      FileOutputStream fos = new FileOutputStream(f);
      fos.write(b);
      fos.close();
    }
    catch (Exception e)
    {}
  }
  
  public static byte[] getFileContentAsByteArray(File f)
  {
    try
    {
      byte[] fileData = new byte[(int) f.length()];
      DataInputStream dis = new DataInputStream(new FileInputStream(f));
      dis.readFully(fileData);
      dis.close();
      return fileData;
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return null;
    }
  }
  
  public static JSONObject openMap(File f)
  {
    try
    {
      File debug = new File(f.getPath() + ".debug");
      if (f.exists() && !debug.exists())
      {
        debug.createNewFile();
        Assistant.setFileContent(debug, decompressFile(f));
      }
      if (!f.exists() && CFG.DEBUG)
      {
        f.createNewFile();
        JSONObject o = new JSONObject(Assistant.getFileContent(debug));
        compressFile(f, o.toString());
        return new JSONObject(decompressFile(f));
      }
      
      return new JSONObject(decompressFile(f));
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  public static void saveMap(File f, JSONObject data)
  {
    try
    {
      File debug = new File(f.getPath() + ".debug");
      if (CFG.DEBUG)
      {
        debug.createNewFile();
        Assistant.setFileContent(debug, data.toString());
      }
      f.createNewFile();
      compressFile(f, data.toString());
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public static void compileMaps(File dir)
  {
    if (!CFG.DEBUG)
      return;
    
    File[] files = dir.listFiles(new FileFilter()
    {
      @Override
      public boolean accept(File pathname)
      {
        return pathname.getName().endsWith(".debug");
      }
    });
    for (File f : files)
    {
      try
      {
        File map = new File(f.getPath().substring(0, f.getPath().lastIndexOf(".debug")));
        if (!map.exists())
        {
          saveMap(map, new JSONObject(Assistant.getFileContent(f)));
        }
      }
      catch (JSONException e)
      {
        e.printStackTrace();
        continue;
      }
    }
  }
}
