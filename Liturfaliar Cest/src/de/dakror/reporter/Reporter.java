package de.dakror.reporter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

import javax.swing.JOptionPane;

import de.dakror.universion.UniVersion;

public class Reporter
{
  private static File     LOG       = null;
  private static String   SESSION_START;
  public static final int MAXLENGTH = 4096;
  
  public static void init(File log)
  {
    if (!UniVersion.initialized)
    {
      System.out.println("[Reporter]: Initialize UniVersion first!");
      System.exit(0);
    }
    LOG = log;
    LOG.mkdirs();
    SESSION_START = new Date().toString().replace(":", "-");
    cleanupLogs();
    try
    {
      System.setErr(new ErrorOutputStream(System.out, new File(LOG, SESSION_START + ".log")));
    }
    catch (FileNotFoundException e1)
    {
      e1.printStackTrace();
    }
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler()
    {
      @Override
      public void uncaughtException(Thread t, Throwable e)
      {
        JOptionPane.showMessageDialog(null, "Ein kritischer Fehler ist aufgetreten, der " + UniVersion.getSimpleName() + " zum Absturz gebracht hat.\nEin Fehlerbericht wird an dakror.de gesendet, um diesen Fehler zu beheben.", "Kritischer Fehler!", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
        cleanupLogs();
        System.exit(0);
      }
    });
  }
  
  public static void cleanupLogs()
  {
    for (File f : LOG.listFiles())
    {
      if (f.length() == 0)
      {
        f.delete();
        continue;
      }
      try
      {
        String stacktrace = getFileContents(f);
        if (stacktrace.length() > MAXLENGTH) stacktrace = stacktrace.substring(0, MAXLENGTH - 3) + "...";
        
        new URL("http://dakror.de/ajax/errorreport.php?r=" + URLEncoder.encode("App: " + UniVersion.getFullName() + "\nDate: " + (System.currentTimeMillis() / 1000) + "\nVersion: " + UniVersion.prettyVersion() + " \nError: " + stacktrace, "UTF-8")).openStream().close();
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      if (f.getName().indexOf(SESSION_START) == -1) while (!f.delete())
        System.gc();
    }
  }
  
  public static String getFileContents(File f)
  {
    String res = "", line = "";
    try
    {
      BufferedReader br = new BufferedReader(new FileReader(f));
      while ((line = br.readLine()) != null)
        res += line + "\r\n";
      br.close();
    }
    catch (IOException e)
    {
      return null;
    }
    return res;
  }
}
