package de.dakror.liturfaliar.settings;

import java.awt.Point;
import java.io.File;

import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.FileManager;

/**
 * Static global Config class.
 * 
 * @author Dakror
 */
public final class CFG
{
  // -- UniVersion -- //
  public static final int      VERSION             = 2013072215;
  public static final int      PHASE               = 0;
  // -- debug -- //
  public static boolean        UIDEBUG             = false;
  public static boolean        DEBUG               = true;
  
  public static boolean        INTERNET;
  
  public static boolean        HELPOVERLAYCREATE   = false;
  public static boolean        GAMEUPDATE          = true;
  public static boolean        MAPEDITOR           = false;
  
  public static final double   PLAYERLAYER         = 1.0;
  public static final double   SUPERADDLAYER       = 1000.0;
  public static final double   SUPERDELLAYER       = -1000.0;
  
  public static final String   MAPPACK             = "Liturfaliar Cest";
  public static final String   WINDOWTITLE         = "Liturfaliar Cest";
  public static final String   MAPEDITORDIR        = "Maps";
  public static final String   MAPEDITOROBJECTSDIR = "Objects";
  public static final String[] BODY                = { "skin", "hair", "eyes", "shirt", "trouser", "hat", "gloves", "boots" };
  public static final String   TILES_CS            = "8327E677486AF0642D7580F5220BCDAB";
  public static final String   SOUND_CS            = "216FC81DAE599EA03612E716CE676ABF";
  public static final String   MUSIC_CS            = "3B36CF7AD35DE2BC0AB276D32A7D7BBA";
  public static final String   ANIMATIONS_CS       = "E64DEF386BCA2334CD99ED322A309EB7";
  
  public static final int      MAINMENU_BG         = 37;
  public static final int      FIELDSIZE           = 32;
  public static final int      DECOSIZE            = 32;
  public static final int      ANIMATIONS          = 59;
  public static final int      TILES               = 123;
  
  public static String         HARDDRIVE           = "C";
  public static boolean        DIRECTDRAW          = false;
  
  public static final int      BOUNDMALUS          = 0;
  public static final int      BUMPMALUS           = 5;
  public static final int[]    HUMANBOUNDS         = { FIELDSIZE - BOUNDMALUS, FIELDSIZE * 3 / 2 - BOUNDMALUS };
  public static final int[]    HUMANBUMPS          = { BUMPMALUS, FIELDSIZE, HUMANBOUNDS[0] - BUMPMALUS * 2, CFG.FIELDSIZE / 2 };
  public static Point          MAPCENTER           = new Point(0, 0);
  
  
  private static String        t;
  private static int           n;
  
  private static long          lastTime;
  private static int           minPeriod;
  
  public static void ccs()
  {
    String[] fs = { "Tiles", "Sound", "Music", "Animations" };
    for (String f : fs)
    {
      String cs = Assistant.getFolderChecksum(new File(FileManager.dir, f));
      try
      {
        p(f + ": " + cs);
        if (!cs.equals(CFG.class.getField(f.toUpperCase() + "_CS").get(null))) System.exit(0);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
  }
  
  public static void p(Object p)
  {
    System.out.println(p);
  }
  
  public static void t(String... s)
  {
    if (s != null && s.length > 0)
    {
      t = s[0];
      n = 0;
    }
    
    if (t != null)
    {
      CFG.p(t + ": " + n);
      n++;
    }
  }
  
  public static void u(int... i)
  {
    if (lastTime == 0)
    {
      lastTime = System.currentTimeMillis();
      minPeriod = i[0];
    }
    else
    {
      if (System.currentTimeMillis() - lastTime > minPeriod) CFG.b("timespan", System.currentTimeMillis() - lastTime);
      lastTime = 0;
      minPeriod = 0;
    }
  }
  
  public static void b(Object... b)
  {
    String s = "";
    for (int i = 0; i < b.length; i += 2)
    {
      s += b[i] + ": " + b[i + 1] + ",";
    }
    p(s.substring(0, s.length() - 1));
  }
}
