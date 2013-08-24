package de.dakror.liturfaliar.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipAssistant extends Thread
{
  public String              state;
  public int                 fullsize;
  public int                 downloaded;
  public URL                 url;
  private File               targetDir;
  public static final byte[] BUFFER = new byte[0xFFFF];
  
  public ZipAssistant(URL url, File targetDir)
  {
    this.url = url;
    this.targetDir = targetDir;
  }
  
  public void run()
  {
    this.state = "Herunterladen";
    if (!this.targetDir.exists())
    {
      this.targetDir.mkdirs();
    }
    try
    {
      this.fullsize = this.url.openConnection().getContentLength();
      InputStream in = new BufferedInputStream(this.url.openStream(), 1024);
      File zip = File.createTempFile("arc", ".zip", this.targetDir);
      OutputStream out = new BufferedOutputStream(new FileOutputStream(zip));
      copyInputStream(in, out);
      out.close();
      unzip(zip, this.targetDir).delete();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  public File unzip(File zip, File dest)
  {
    this.state = "Entpacken";
    try
    {
      ZipFile zipFile = new ZipFile(zip);
      this.downloaded = 0;
      this.fullsize = 0;
      for (ZipEntry entry : Collections.list(zipFile.entries()))
      {
        if (!entry.isDirectory()) this.fullsize += entry.getSize();
      }
      for (ZipEntry entry : Collections.list(zipFile.entries()))
      {
        extractEntry(zipFile, entry, dest.getPath());
      }
      zipFile.close();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    this.state = "Fertig";
    return zip;
  }
  
  public void extractEntry(ZipFile zipFile, ZipEntry entry, String destDir) throws IOException
  {
    File file = new File(destDir + File.separator + entry.getName());
    if (entry.isDirectory()) file.mkdirs();
    else
    {
      file.getParentFile().mkdirs();
      InputStream is = null;
      OutputStream os = null;
      try
      {
        is = zipFile.getInputStream(entry);
        os = new FileOutputStream(file);
        for (int len; (len = is.read(BUFFER)) != -1;)
        {
          this.downloaded += len;
          os.write(BUFFER, 0, len);
        }
      }
      finally
      {
        if (os != null) os.close();
        if (is != null) is.close();
      }
    }
  }
  
  public void copyInputStream(InputStream in, OutputStream out) throws IOException
  {
    byte[] buffer = new byte[1024];
    int len = in.read(buffer);
    while (len >= 0)
    {
      out.write(buffer, 0, len);
      this.downloaded += len;
      len = in.read(buffer);
    }
    in.close();
    out.close();
  }
}
