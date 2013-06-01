package de.dakror.liturfaliar;

import java.io.File;

import javax.swing.UIManager;

import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryJavaSound;
import de.dakror.liturfaliar.settings.CFG;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.FileManager;
import de.dakror.reporter.Reporter;
import de.dakror.universion.UniVersion;

public class LiturfaliarCest
{
  public static void main(String[] args)
  {
    CFG.INTERNET = Assistant.isInternetReachable();
    
    UniVersion.offline = !CFG.INTERNET;
    UniVersion.init(LiturfaliarCest.class, CFG.VERSION, CFG.PHASE);
    
    FileManager.mk(null);
    
    if (!CFG.DEBUG)
    {
      Reporter.init(new File(FileManager.dir, "Logs"));
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
}
