package de.dakror.liturfaliar;

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
      
      for (String arg : args)
      {
        if (arg.toLowerCase().equals("help"))
        {
          CFG.p("Usage: LiturfaliarCest [options]");
          CFG.p("  All options are optional, listed here:");
          CFG.p("   -hd{alternative harddrive-index}        Enter the index of the harddrive where the game media files are downloaded to. Example: -hdE = Sets the harddrive to E:\\");
          CFG.p("   -d                                      When set, the Java-directdraw method is used, which may improve or worsen your game performance.");
          CFG.p("   -p{IP}                                  Sets the HTTP proxy server.");
          CFG.p("   -pp{PORT}                               Sets the HTTP proxy server port.");
          System.exit(0);
        }
        else if (arg.startsWith("-hd"))
        {
          CFG.HARDDRIVE = arg.replace("-hd", "");
        }
        else if (arg.equals("-d"))
        {
          CFG.DIRECTDRAW = true;
        }
        else if (arg.startsWith("-p"))
        {
          System.getProperties().put("http.proxyHost", arg.replace("-p", ""));
        }
        else if (arg.startsWith("-pp"))
        {
          System.getProperties().put("http.proxyPort", arg.replace("-pp", ""));
        }
      }
    }
  }
}
