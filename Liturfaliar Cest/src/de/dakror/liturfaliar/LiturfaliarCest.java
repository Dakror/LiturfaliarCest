package de.dakror.liturfaliar;

import de.dakror.liturfaliar.util.FileManager;
import de.dakror.reporter.Reporter;
import de.dakror.universion.UniVersion;
import java.io.File;

import javax.swing.UIManager;

import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryJavaSound;

public class LiturfaliarCest
{
  public static void main(String[] args)
  {
    UniVersion.init(LiturfaliarCest.class, CFG.VERSION, CFG.PHASE);
    FileManager.mk(null);
    Reporter.init(new File(FileManager.dir, "Logs"));
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
