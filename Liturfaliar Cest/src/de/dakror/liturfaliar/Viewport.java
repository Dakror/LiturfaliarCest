package de.dakror.liturfaliar;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.json.JSONObject;

import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import de.dakror.liturfaliar.editor.MapEditor;
import de.dakror.liturfaliar.ovscenes.OVScene;
import de.dakror.liturfaliar.ovscenes.OVScene_Info;
import de.dakror.liturfaliar.scenes.Scene;
import de.dakror.liturfaliar.scenes.Scene_Logo;
import de.dakror.liturfaliar.ui.CursorText;
import de.dakror.liturfaliar.ui.HelpOverlay;
import de.dakror.liturfaliar.ui.Notification;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.FileManager;
import de.dakror.liturfaliar.util.GameFrame;
import de.dakror.liturfaliar.util.Handler;

/**
 * The Viewport is the framework for all seen stuff. It contains the update- and drawingloop.
 */
public class Viewport extends GameFrame implements WindowListener, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener
{
  private boolean                       initialized    = false;
  private boolean                       takeScreenshot = false;
  private boolean                       pausedfromscene;
  private boolean                       frozenFrames;
  private long                          time           = 0;
  static private HashMap<Image, String> REVcache       = new HashMap<Image, String>();
  static private HashMap<String, Image> cache          = new HashMap<String, Image>();
  public double                         fMusicEffectID = 0.5d;
  public double                         fMusicID       = 0.3d;
  public double                         fSoundID       = 1.0d;
  public boolean                        INTERNET;
  public HashMap<String, OVScene>       ovscenes       = new HashMap<String, OVScene>();
  public JSONObject                     savegame;
  public static Notification                   notification;
  public Scene                          scene;
  public SoundSystem                    ss;
  public String                         MusicEffectID;
  public String                         MusicID;
  public String                         SoundID;
  public Window                         w;
  public MapEditor                      mapeditor;
  
  /**
   * Constructor
   * 
   * @param ss - Soundsystem for playing Sounds and Music with.
   */
  public Viewport(SoundSystem ss)
  {
    this.ss = ss;
  }
  
  /**
   * The loop function for logic stuff in the active {@link Scene}.
   * 
   * @param paramLong - The time from the last call to now.
   */
  public void update(long paramLong)
  {
    if (!initialized)
      return;
    if (scene != null)
      scene.update(paramLong);
    try
    {
      for (String ovscene : ovscenes.keySet())
      {
        ovscenes.get(ovscene).update(paramLong);
        CursorText.removeCursorText("scene");
      }
    }
    catch (ConcurrentModificationException e)
    {}
  }
  
  /**
   * The loop function for draw stuff in the active {@link Scene}.
   * 
   * @param g - Graphics2D from the FullscreenWindow to draw stuff with it.
   */
  public void draw(Graphics2D g)
  {
    if (!initialized)
      return;
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    if (scene != null)
      scene.draw(g);
    if (takeScreenshot)
    {
      try
      {
        BufferedImage screenshot = new BufferedImage(w.getWidth(), w.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = Assistant.copyGraphics2DAttributes(g, (Graphics2D) screenshot.getGraphics());
        Assistant.Rect(0, 0, w.getWidth(), w.getHeight(), Color.black, Color.black, g2);
        scene.draw(g2);
        for (String ovscene : ovscenes.keySet())
        {
          ovscenes.get(ovscene).draw(g2);
        }
        File dir = new File(FileManager.dir, "Screenshots");
        dir.mkdir();
        File file = new File(dir, "Screenshot " + new Date().toString().replace(":", "-") + ".png");
        ImageIO.write(screenshot, "png", file);
        takeScreenshot = false;
        notification = new Notification("Screenshot gespeichert unter\n" + file.getName(), null);
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
    for (String ovscene : ovscenes.keySet())
    {
      ovscenes.get(ovscene).draw(g);
    }
    if (notification != null)
    {
      notification.draw(g, w);
      if (notification.finished)
        notification = null;
    }
    // static drawers
    CursorText.draw(g, w);
    HelpOverlay.draw(g, w);
  }
  
  /**
   * Sets the given Scene to the active and shown one.
   * 
   * @param scene - The new {@link Scene} to be the active one.
   */
  public void setScene(Scene s)
  {
    Assistant.setCursor(Viewport.loadImage("system/loading.png"), w);
    Handler.removeListener(scene);
    initialized = false;
    pause();
    pausedfromscene = true;
    ovscenes.clear();
    HelpOverlay.clear();
    scene = s;
    scene.init(this);
    Handler.addListener(s);
    initialized = true;
    Assistant.setCursor(Viewport.loadImage("system/cursor.png"), w);
  }
  
  public void addOVScene(OVScene scene, String name)
  {
    scene.init(this);
    ovscenes.put(name, scene);
  }
  
  public void toggleOVScene(OVScene scene, String name)
  {
    if (ovscenes.containsKey(name))
    {
      ovscenes.get(name).setListenersEnabled(false);
      ovscenes.remove(name);
    }
    else addOVScene(scene, name);
  }
  
  public void removeOVScene(String name)
  {
    if (ovscenes.containsKey(name))
    {
      ovscenes.get(name).setListenersEnabled(false);
      ovscenes.remove(name);
    }
  }
  
  public static Image loadImage(String path)
  {
    String capitals = "QWERTZUIOPASDFGHJKLYXCVBNM";
    if (cache.containsKey(path))
    {
      return cache.get(path);
    }
    else
    {
      Image i = null;
      if (capitals.indexOf(path.charAt(0)) > -1)
      {
        try
        {
          i = ImageIO.read(FileManager.pullMediaFile(path.substring(0, path.indexOf("/")), path.replace(path.substring(0, path.indexOf("/") + 1), "")));
        }
        catch (IOException e)
        {}
      }
      else i = Assistant.loadImage(path);
      if (i == null)
        System.err.println("Image is missing: " + path);
      cache.put(path, i);
      REVcache.put(i, path);
      return i;
    }
  }
  
  public static String getImagePath(Image image)
  {
    if (REVcache.containsKey(image))
    {
      return REVcache.get(image);
    }
    else
    {
      return "";
    }
  }
  
  public int getFrame()
  {
    if (areFramesFrozen())
      return 0;
    return (int) ((System.currentTimeMillis() - time) / 250);
  }
  
  public int getFrame(long time)
  {
    if (areFramesFrozen())
      return 0;
    return (int) Math.round((System.currentTimeMillis() - time) / 250.0);
  }
  
  public int getFrame(float size)
  {
    if (areFramesFrozen())
      return 0;
    return (int) ((System.currentTimeMillis() - time) / (250 * size));
  }
  
  /**
   * Initializes the game.
   */
  public void init()
  {
    w = s.getFullScreenWindow();
    w.addWindowListener(this);
    w.addKeyListener(this);
    w.addMouseListener(this);
    w.addMouseMotionListener(this);
    w.addMouseWheelListener(this);
    w.setIconImage(Assistant.loadImage("system/logo.png"));
    Assistant.setCursor(Viewport.loadImage("system/loading.png"), w);
    INTERNET = Assistant.isInternetReachable();
    Assistant.setCursor(Viewport.loadImage("system/cursor.png"), w);
    FileManager.mk(this);
    FileManager.loadOptions(this);
    try
    {
      File dir = new File(FileManager.dir, "Logs");
      dir.mkdir();
      for (File f : dir.listFiles())
      {
        if (f.length() == 0)
          f.delete();
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    setScene(new Scene_Logo());
    frozenFrames = false;
    time = System.currentTimeMillis();
    initialized = true;
  }
  
  /**
   * Pauses all played Sounds and BackgroundMusic.
   */
  public void pause()
  {
    try
    {
      if (MusicEffectID != null)
        ss.pause(MusicEffectID);
      if (MusicID != null)
        ss.pause(MusicID);
    }
    catch (Exception e)
    {}
  }
  
  /**
   * Resumes all paused Sounds and BackgroundMusic.
   */
  public void play()
  {
    pausedfromscene = false;
    if (MusicEffectID != null)
      ss.play(MusicEffectID);
    if (MusicID != null)
      ss.play(MusicID);
  }
  
  /**
   * Playes a new Sound.
   * 
   * @param name - Name of the Sound to play
   */
  public void playSound(String name)
  {
    if (FileManager.pullMediaFile("Sound", name + ".wav") == null)
    {
      System.err.println("invalid sound: " + name);
      return;
    }
    ss.loadSound(FileManager.pullMediaFile("Sound", name + ".wav"), name + "wav");
    SoundID = ss.quickPlay(false, name + "wav", false, 0.0f, 0.0f, 0.0f, SoundSystemConfig.ATTENUATION_ROLLOFF, SoundSystemConfig.getDefaultRolloff());
    ss.setVolume(SoundID, (float) fSoundID);
  }
  
  public static double getSoundLength(String name)
  {
    AudioInputStream audioInputStream = null;
    try
    {
      audioInputStream = AudioSystem.getAudioInputStream(FileManager.pullMediaFile("Sound", name + ".wav"));
    }
    catch (UnsupportedAudioFileException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    AudioFormat format = audioInputStream.getFormat();
    long frames = audioInputStream.getFrameLength();
    // -- time in ms -- //
    return ((frames + 0.0) / format.getFrameRate()) * 1000;
  }
  
  public void playSound(String name, float vol)
  {
    if (FileManager.pullMediaFile("Sound", name + ".wav") == null)
    {
      System.err.println("invalid sound: " + name);
      return;
    }
    ss.loadSound(FileManager.pullMediaFile("Sound", name + ".wav"), name + "wav");
    SoundID = ss.quickPlay(false, name + "wav", false, 0.0f, 0.0f, 0.0f, SoundSystemConfig.ATTENUATION_ROLLOFF, SoundSystemConfig.getDefaultRolloff());
    ss.setVolume(SoundID, vol);
  }
  
  /**
   * Stops the played Sound.
   */
  public void stopSound()
  {
    ss.stop(SoundID);
  }
  
  /**
   * Playes new Music.
   * 
   * @param name - Name of the Sound to play.
   * @param force - if set to {@code TRUE}, the Music will be played, even if the same is already playing.
   */
  public void playMusic(String name, boolean force)
  {
    if (!force && MusicID != null && MusicID.equals(name + ".wav"))
    {
      play();
      return;
    }
    if (FileManager.pullMediaFile("Music", name + ".wav") == null)
      return;
    MusicID = name + ".wav";
    ss.backgroundMusic(MusicID, FileManager.pullMediaFile("Music", name + ".wav"), MusicID, false);
    ss.setVolume(MusicID, (float) fMusicID);
  }
  
  public void playMusic(String name, boolean force, float vol)
  {
    if (!force && MusicID != null && MusicID.equals(name + ".wav"))
    {
      play();
      return;
    }
    if (FileManager.pullMediaFile("Music", name + ".wav") == null)
      return;
    MusicID = name + ".wav";
    ss.backgroundMusic(MusicID, FileManager.pullMediaFile("Music", name + ".wav"), MusicID, false);
    ss.setVolume(MusicID, vol);
  }
  
  /**
   * Stops the played Music.
   */
  public void stopMusic()
  {
    ss.stop(MusicID);
  }
  
  /**
   * Not used yet.
   * 
   * @param name
   */
  public void playMusicEffect(String name)
  {
    MusicEffectID = name;
    ss.loadSound(getClass().getResource("/musiceffect/" + name), name);
    MusicEffectID = ss.quickPlay(false, name, false, 0.0f, 0.0f, 0.0f, SoundSystemConfig.ATTENUATION_ROLLOFF, SoundSystemConfig.getDefaultRolloff());
    ss.setVolume(MusicEffectID, (float) fMusicEffectID);
  }
  
  /**
   * Not used yet.
   */
  public void stopMusicEffect()
  {
    ss.stop(MusicEffectID);
  }
  
  @Override
  public void windowClosing(WindowEvent e)
  {
    if (!initialized)
      return;
    FileManager.saveOptions(this);
    stop();
  }
  
  @Override
  public void windowOpened(WindowEvent e)
  {}
  
  @Override
  public void close()
  {}
  
  @Override
  public void windowClosed(WindowEvent e)
  {}
  
  @Override
  public void windowIconified(WindowEvent e)
  {}
  
  @Override
  public void windowDeiconified(WindowEvent e)
  {}
  
  @Override
  public void windowActivated(WindowEvent e)
  {
    if (!pausedfromscene)
      play();
  }
  
  @Override
  public void windowDeactivated(WindowEvent e)
  {
    pause();
  }
  
  public boolean areFramesFrozen()
  {
    return frozenFrames;
  }
  
  public void setFramesFrozen(boolean frozenFrames)
  {
    this.frozenFrames = frozenFrames;
  }
  
  @Override
  public void mouseWheelMoved(MouseWheelEvent e)
  {
    Handler.mouseWheelMoved(e);
  }
  
  @Override
  public void mouseDragged(MouseEvent e)
  {
    Handler.mouseDragged(e);
  }
  
  @Override
  public void mouseMoved(MouseEvent e)
  {
    Handler.mouseMoved(e);
  }
  
  @Override
  public void mouseClicked(MouseEvent e)
  {
    Handler.mouseClicked(e);
  }
  
  @Override
  public void mousePressed(MouseEvent e)
  {
    Handler.mousePressed(e);
  }
  
  @Override
  public void mouseReleased(MouseEvent e)
  {
    Handler.mouseReleased(e);
  }
  
  @Override
  public void mouseEntered(MouseEvent e)
  {
    Handler.mouseEntered(e);
  }
  
  @Override
  public void mouseExited(MouseEvent e)
  {
    Handler.mouseExited(e);
  }
  
  @Override
  public void keyTyped(KeyEvent e)
  {
    Handler.keyTyped(e);
  }
  
  @Override
  public void keyPressed(KeyEvent e)
  {
    switch (e.getExtendedKeyCode())
    {
      case KeyEvent.VK_F2:
        takeScreenshot = true;
        break;
      case KeyEvent.VK_F3:
        if (e.isShiftDown())
          CFG.UIDEBUG = !CFG.UIDEBUG;
        break;
      case KeyEvent.VK_F4:
        toggleOVScene(new OVScene_Info(), "Info");
        break;
    }
    Handler.keyPressed(e);
  }
  
  @Override
  public void keyReleased(KeyEvent e)
  {
    Handler.keyReleased(e);
  }
}