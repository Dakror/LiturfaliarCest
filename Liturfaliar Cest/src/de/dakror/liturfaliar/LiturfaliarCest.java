package de.dakror.liturfaliar;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryJavaSound;
import de.dakror.liturfaliar.settings.CFG;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.FileManager;
import de.dakror.universion.UniVersion;

public class LiturfaliarCest
{
  public static void main(String[] args)
  {
    loadParameters(args);
    
    CFG.INTERNET = Assistant.isInternetReachable();
    
    UniVersion.offline = !CFG.INTERNET;
    UniVersion.init(LiturfaliarCest.class, CFG.VERSION, CFG.PHASE);
    
    FileManager.mk(null);
    
    if (!CFG.DEBUG)
    {
      // Reporter.init(new File(FileManager.dir, "Logs"));
    }
    
    try
    {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      SoundSystemConfig.addLibrary(LibraryJavaSound.class);
      SoundSystemConfig.setCodec("wav", CodecWav.class);
      SoundSystem ss = new SoundSystem(LibraryJavaSound.class);
      new Viewport(ss).run();
      ss.cleanup();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public static void loadParameters(String[] args)
  {
    if (args.length > 0)
    {
      if (args[0].toLowerCase().equals("help"))
      {
        CFG.p("Usage: LiturfaliarCest [alternative harddrive-index | -d]");
        CFG.p("  Parameters:");
        CFG.p("   |            NAME              |     MODE     |                DESCRIPTION");
        CFG.p("   |                              |              |");
        CFG.p("   |  alternative harddrive-index |   optional   |   Enter the index of the harddrive where the game media files are downloaded to.");
        CFG.p("   |            -d                |   optional   |   When set, the Java-directdraw methos is used, which may improve or worsen your game performance.");
        System.exit(0);
      }
      if (!args[0].startsWith("-"))
      {
        CFG.HARDDRIVE = args[0];
        if (!new File(CFG.HARDDRIVE + ":/").exists())
        {
          JOptionPane.showMessageDialog(null, "Die Alternativ-Festplatte \"" + args[0] + "\" existiert nicht!", "Fehler!", JOptionPane.ERROR_MESSAGE);
          System.exit(0);
        }
      }
      else
      {
        CFG.DIRECTDRAW = args[0].equals("-d");
        return;
      }
      
      if (args.length > 1)
      {
        CFG.DIRECTDRAW = args[1].equals("-d");
      }
    }
  }
}
