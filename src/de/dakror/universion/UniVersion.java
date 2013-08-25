package de.dakror.universion;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JOptionPane;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UniVersion
{
  public final static String[] PHASES      = { "Pre-Alpha", "Alpha", "Beta", "Release" };
  static String                app;
  static int                   version;
  static int                   phase;
  static JSONObject            get;
  public static boolean        initialized = false;
  
  public static boolean        offline     = false;
  
  public static void init(Class<?> a, int v, int p)
  {
    init(a.getName(), v, p);
  }
  
  public static void init(String a, int v, int p)
  {
    app = a;
    version = v;
    phase = p;
    
    if (offline) return;
    
    try
    {
      get = new JSONObject(query("get", "app=" + app));
    }
    catch (JSONException e1)
    {
      return;
    }
    int compare = compareToOnline();
    if (compare == -1)
    {
      if (JOptionPane.showConfirmDialog(null, "Es ist eine Aktualisierung für " + getSimpleName() + " verfügbar.\nMöchten Sie sie jetzt herunterladen?", "Aktualisierung verfügbar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null) == 0)
      {
        try
        {
          Desktop.getDesktop().browse(new URL(("http://dakror.de/download?u=" + get.getString("PATH") + "/" + getSimpleName() + ".jar").replace(" ", "%20")).toURI());
          System.exit(0);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    }
    else if (compare == 0)
    {
      System.out.println("[UniVersion]: " + getSimpleName() + " is up to date (" + prettyVersion() + ")");
    }
    initialized = true;
  }
  
  public static int compareToOnline()
  {
    try
    {
      return Integer.compare(version, get.getInt("VERSION"));
    }
    catch (JSONException e)
    {
      
      return 2;
    }
  }
  
  public static String prettyOnlineVersion()
  {
    try
    {
      return getOnlinePhase() + " " + prettyVersion(get.getInt("VERSION"));
    }
    catch (JSONException e)
    {
      return null;
    }
  }
  
  public static String prettyVersion()
  {
    return getPhase() + " " + prettyVersion(version);
  }
  
  private static String prettyVersion(int ver)
  {
    String version = String.valueOf(ver);
    return version.replaceAll("([0-9]{4})([0-9]{2})([0-9]{2})([0-9]{2})", "$3.$2.$1 $4:00");
  }
  
  public static String getPhase()
  {
    return PHASES[phase];
  }
  
  public static String getOnlinePhase()
  {
    try
    {
      return PHASES[get.getInt("PHASE")];
    }
    catch (JSONException e)
    {
      return null;
    }
  }
  
  public static String getFullName()
  {
    return app;
  }
  
  public static void patch()
  {
    query("patch", "app=" + app + "&ver=" + version + "&phase=" + phase);
  }
  
  public static JSONArray all()
  {
    try
    {
      return new JSONArray(query("all", ""));
    }
    catch (JSONException e)
    {
      return null;
    }
  }
  
  public static String getSimpleName()
  {
    return app.replaceAll("[a-z]{1,}\\.", "");
  }
  
  public static String query(String function, String params)
  {
    try
    {
      URL u = new URL("http://dakror.de/UniVersion/universion.php?f=" + function + "&" + params);
      return getURLContents(u);
    }
    catch (MalformedURLException e)
    {
      return null;
    }
  }
  
  public static String getURLContents(URL u)
  {
    String res = "", line = "";
    try
    {
      BufferedReader br = new BufferedReader(new InputStreamReader(u.openStream()));
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
