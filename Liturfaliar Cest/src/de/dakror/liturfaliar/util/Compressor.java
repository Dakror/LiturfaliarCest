package de.dakror.liturfaliar.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import de.dakror.liturfaliar.CFG;

public class Compressor
{
  public static void compressFile(File f, String s)
  {
    compressFile(f, s.getBytes());
    if (CFG.DEBUG)
    {
      File dbg = new File(f.getParentFile(), f.getName() + ".debug");
      try
      {
        dbg.createNewFile();
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
      Assistant.setFileContent(dbg, s);
    }
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
    File dbg = new File(f.getPath() + ".debug");
    if (!f.exists() && dbg.exists() && CFG.DEBUG)
    {
      compressFile(f, Assistant.getFileContent(dbg).replaceAll("(\n)|(\n\r)|(\r\n)|(  )", "").replace(" : ", ":"));
    }
    
    byte[] decompressed = decompress(getFileContentAsByteArray(f));
    String text = new String(decompressed);
    
    if (f.exists() && !dbg.exists() && CFG.DEBUG)
    {
      Assistant.setFileContent(dbg, text);
    }
    
    return text;
  }
  
  public static void setFileContent(File f, byte[] b)
  {
    try
    {
      if (!f.exists())
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
}
