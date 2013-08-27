package de.dakror.liturfaliar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.InputEvent;
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
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.json.JSONObject;

import paulscode.sound.FilenameURL;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.Source;
import de.dakror.liturfaliar.editor.MapEditor;
import de.dakror.liturfaliar.ovscenes.OVScene;
import de.dakror.liturfaliar.ovscenes.OVScene_Info;
import de.dakror.liturfaliar.scenes.Scene;
import de.dakror.liturfaliar.scenes.Scene_Game;
import de.dakror.liturfaliar.scenes.Scene_Logo;
import de.dakror.liturfaliar.settings.CFG;
import de.dakror.liturfaliar.ui.CursorText;
import de.dakror.liturfaliar.ui.Dialog;
import de.dakror.liturfaliar.ui.Notification;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.FileManager;
import de.dakror.liturfaliar.util.GameFrame;

/**
 * The Viewport is the framework for all seen stuff. It contains the update- and drawingloop.
 */
public class Viewport extends GameFrame implements WindowListener, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener
{
	private static HashMap<Image, String> REVcache = new HashMap<Image, String>();
	private static HashMap<String, Image> cache = new HashMap<String, Image>();
	private static HashMap<String, Scale> SCcache = new HashMap<String, Scale>();
	private static HashMap<String, OVScene> ovscenes = new HashMap<String, OVScene>();
	private static boolean pausedfromscene;
	private static boolean frozenFrames;
	private static boolean sceneEnabled;
	private static boolean initialized = false;
	private static long time = 0;
	
	public static Notification notification;
	public static Scene scene;
	public static SoundSystem ss;
	public static String MusicEffectID;
	public static String MusicID;
	public static String SoundID;
	public static Dialog dialog;
	public static InputEvent skipEvent;
	public static JSONObject savegame;
	public static double fMusicEffectID = 0.5d;
	public static double fMusicID = 0.3d;
	public static double fSoundID = 1.0d;
	
	private boolean takeScreenshot = false;
	
	public MapEditor editor;
	
	/**
	 * Constructor
	 * 
	 * @param ss - Soundsystem for playing Sounds and Music with.
	 */
	public Viewport(SoundSystem ss)
	{
		Viewport.ss = ss;
	}
	
	/**
	 * The loop function for logic stuff in the active {@link Scene}.
	 * 
	 * @param paramLong - The time from the last call to now.
	 */
	public void update(long paramLong)
	{
		if (!initialized) return;
		if (scene != null) scene.update(paramLong);
		
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
		
		if (dialog != null) dialog.update();
	}
	
	/**
	 * The loop function for draw stuff in the active {@link Scene}.
	 * 
	 * @param g - Graphics2D from the FullscreenWindow to draw stuff with it.
	 */
	public void draw(Graphics2D g)
	{
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		if (scene != null && initialized) scene.draw(g);
		
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
		try
		{
			for (String ovscene : ovscenes.keySet())
			{
				ovscenes.get(ovscene).draw(g);
			}
		}
		catch (Exception e)
		{}
		if (notification != null)
		{
			notification.draw(g);
			if (notification.finished) notification = null;
		}
		// static drawers
		if (dialog != null) dialog.draw(g);
		
		CursorText.draw(g);
	}
	
	/**
	 * Sets the given Scene to the active and shown one.
	 * 
	 * @param scene - The new {@link Scene} to be the active one.
	 */
	public static void setScene(final Scene s)
	{
		Assistant.setCursor(Viewport.loadImage("system/loading.png"));
		initialized = false;
		pause();
		pausedfromscene = true;
		
		clearOVScenes();
		
		if (scene != null) scene.destruct();
		
		scene = s;
		scene.construct();
		initialized = true;
		sceneEnabled = true;
		Assistant.setCursor(Viewport.loadImage("system/cursor.png"));
	}
	
	public static void clearOVScenes()
	{
		ArrayList<String> keys = new ArrayList<String>(ovscenes.keySet());
		HashMap<String, OVScene> newov = new HashMap<String, OVScene>();
		for (int i = 0; i < ovscenes.size(); i++)
		{
			if (ovscenes.get(keys.get(i)).consistent) newov.put(keys.get(i), ovscenes.get(keys.get(i)));
			else ovscenes.get(keys.get(i)).destruct();
		}
		ovscenes = newov;
	}
	
	public static void addOVScene(OVScene scene, String name)
	{
		scene.construct();
		ovscenes.put(name, scene);
	}
	
	public static void toggleOVScene(OVScene scene, String name)
	{
		if (ovscenes.containsKey(name)) removeOVScene(name);
		
		else addOVScene(scene, name);
	}
	
	public static void removeOVScene(String name)
	{
		if (ovscenes.containsKey(name))
		{
			ovscenes.get(name).destruct();
			ovscenes.remove(name);
		}
		
	}
	
	public static void preloadImages()
	{	
		
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
			if (i == null) System.err.println("Image is missing: " + path);
			cache.put(path, i);
			REVcache.put(i, path);
			return i;
		}
	}
	
	public static Image loadScaledImage(String path, int w, int h, int hints)
	{
		if (SCcache.containsKey(path))
		{
			return SCcache.get(path).getDimension(w, h, hints);
		}
		else
		{
			SCcache.put(path, new Scale(path));
			return SCcache.get(path).getDimension(w, h, hints);
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
	
	public static int getFrame()
	{
		if (areFramesFrozen()) return 0;
		return (int) ((System.currentTimeMillis() - time) / 250);
	}
	
	public static int getFrame(long time)
	{
		if (areFramesFrozen()) return 0;
		return (int) Math.round((System.currentTimeMillis() - time) / 250.0);
	}
	
	public static int getFrame(float size)
	{
		if (areFramesFrozen()) return 0;
		return (int) ((System.currentTimeMillis() - time) / (250 * size));
	}
	
	public static int getFrame(long time, float size)
	{
		if (areFramesFrozen()) return 0;
		return (int) ((System.currentTimeMillis() - time) / (250 * size));
	}
	
	/**
	 * Initializes the game.
	 */
	public void init()
	{
		w.addWindowListener(this);
		w.addKeyListener(this);
		w.addMouseListener(this);
		w.addMouseMotionListener(this);
		w.addMouseWheelListener(this);
		w.setIconImage(Assistant.loadImage("system/logo.png"));
		w.setBackground(Color.black);
		w.setForeground(Color.white);
		Assistant.setCursor(Viewport.loadImage("system/cursor.png"));
		
		FileManager.mk(false);
		FileManager.loadOptions();
		try
		{
			w.setFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/morpheus.ttf")).deriveFont(20f));
			
			
			File dir = new File(FileManager.dir, "Logs");
			dir.mkdir();
			for (File f : dir.listFiles())
			{
				if (f.length() == 0) f.delete();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		if (!CFG.MAPEDITOR)
		{
			setScene(new Scene_Logo());
		}
		else
		{
			running = false;
			editor = new MapEditor(this);
		}
		frozenFrames = false;
		time = System.currentTimeMillis();
		initialized = true;
	}
	
	/**
	 * Pauses all played Sounds and BackgroundMusic.
	 */
	public static void pause()
	{
		try
		{
			if (MusicEffectID != null) ss.pause(MusicEffectID);
			if (MusicID != null) ss.pause(MusicID);
		}
		catch (Exception e)
		{}
	}
	
	/**
	 * Resumes all paused Sounds and BackgroundMusic.
	 */
	public static void play()
	{
		pausedfromscene = false;
		if (MusicEffectID != null) ss.play(MusicEffectID);
		if (MusicID != null) ss.play(MusicID);
	}
	
	/**
	 * Playes a new Sound.
	 * 
	 * @param name - Name of the Sound to play
	 */
	public static void playSound(String name)
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
	
	public static void playSound(String name, float vol)
	{
		if (FileManager.pullMediaFile("Sound", name + ".wav") == null)
		{
			System.err.println("invalid sound: " + name);
			return;
		}
		Source s = new Source(false, false, false, name + "wav", new FilenameURL(FileManager.pullMediaFile("Sound", name + ".wav"), name + ".wav"), null, 0, 0, 0, SoundSystemConfig.ATTENUATION_ROLLOFF, SoundSystemConfig.getDefaultRolloff(), true);
		ss.setVolume(s.sourcename, vol);
		ss.play(s.sourcename);
	}
	
	/**
	 * Stops the played Sound.
	 */
	public static void stopSound()
	{
		ss.stop(SoundID);
	}
	
	/**
	 * Playes new Music.
	 * 
	 * @param name - Name of the Sound to play.
	 * @param force - if set to {@code TRUE}, the Music will be played, even if the same is already playing.
	 */
	public static void playMusic(String name, boolean force)
	{
		if (!force && MusicID != null && MusicID.equals(name + ".wav"))
		{
			play();
			return;
		}
		else if (force)
		{
			stopMusic();
		}
		if (FileManager.pullMediaFile("Music", name + ".wav") == null) return;
		MusicID = name + ".wav";
		ss.backgroundMusic(MusicID, FileManager.pullMediaFile("Music", name + ".wav"), MusicID, true);
		ss.setVolume(MusicID, (float) fMusicID);
	}
	
	public static void playMusic(String name, boolean force, float vol)
	{
		if (!force && MusicID != null && MusicID.equals(name + ".wav"))
		{
			play();
			return;
		}
		if (FileManager.pullMediaFile("Music", name + ".wav") == null) return;
		MusicID = name + ".wav";
		ss.backgroundMusic(MusicID, FileManager.pullMediaFile("Music", name + ".wav"), MusicID, true);
		ss.setVolume(MusicID, vol);
	}
	
	/**
	 * Stops the played Music.
	 */
	public static void stopMusic()
	{
		if (MusicID != null) ss.stop(MusicID);
	}
	
	/**
	 * Not used yet.
	 * 
	 * @param name
	 */
	public static void playMusicEffect(String name)
	{
		MusicEffectID = name;
		ss.loadSound(Viewport.class.getResource("/musiceffect/" + name), name);
		MusicEffectID = ss.quickPlay(false, name, false, 0.0f, 0.0f, 0.0f, SoundSystemConfig.ATTENUATION_ROLLOFF, SoundSystemConfig.getDefaultRolloff());
		ss.setVolume(MusicEffectID, (float) fMusicEffectID);
	}
	
	/**
	 * Not used yet.
	 */
	public static void stopMusicEffect()
	{
		ss.stop(MusicEffectID);
	}
	
	@Override
	public void windowClosing(WindowEvent e)
	{
		if (!initialized) return;
		FileManager.saveOptions();
		stop();
	}
	
	@Override
	public void windowOpened(WindowEvent e)
	{}
	
	@Override
	public void close()
	{
		ss.cleanup();
	}
	
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
		if (!pausedfromscene) play();
	}
	
	@Override
	public void windowDeactivated(WindowEvent e)
	{
		if (scene instanceof Scene_Game && !((Scene_Game) scene).isPaused()) ;// ((Scene_Game) scene).togglePaused();
		
		pause();
	}
	
	public static boolean areFramesFrozen()
	{
		return frozenFrames;
	}
	
	public static void setFramesFrozen(boolean frozenFrames)
	{
		Viewport.frozenFrames = frozenFrames;
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		try
		{
			for (String ovscene : ovscenes.keySet())
			{
				if (dialog != null && dialog.freezeOVScene) continue;
				ovscenes.get(ovscene).mouseWheelMoved(e);
			}
		}
		catch (ConcurrentModificationException e1)
		{}
		
		if (scene != null && sceneEnabled && dialog == null) scene.mouseWheelMoved(e);
		
		if (dialog != null) dialog.mouseWheelMoved(e);
	}
	
	@Override
	public void mouseDragged(MouseEvent e)
	{
		try
		{
			for (String ovscene : ovscenes.keySet())
			{
				if (dialog != null && dialog.freezeOVScene) continue;
				ovscenes.get(ovscene).mouseDragged(e);
			}
		}
		catch (ConcurrentModificationException e1)
		{}
		
		if (scene != null && sceneEnabled && dialog == null) scene.mouseDragged(e);
		
		if (dialog != null) dialog.mouseDragged(e);
	}
	
	@Override
	public void mouseMoved(MouseEvent e)
	{
		try
		{
			for (String ovscene : ovscenes.keySet())
			{
				if (dialog != null && dialog.freezeOVScene) continue;
				ovscenes.get(ovscene).mouseMoved(e);
			}
		}
		catch (ConcurrentModificationException e1)
		{}
		
		if (scene != null && sceneEnabled && dialog == null) scene.mouseMoved(e);
		
		if (dialog != null) dialog.mouseMoved(e);
		
		
		CursorText.mouseMoved(e);
	}
	
	@Override
	public void mouseClicked(MouseEvent e)
	{
		try
		{
			for (String ovscene : ovscenes.keySet())
			{
				if (dialog != null && dialog.freezeOVScene) continue;
				ovscenes.get(ovscene).mouseClicked(e);
			}
		}
		catch (ConcurrentModificationException e1)
		{}
		
		if (scene != null && sceneEnabled && dialog == null) scene.mouseClicked(e);
		
		if (dialog != null) dialog.mouseClicked(e);
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		try
		{
			for (String ovscene : ovscenes.keySet())
			{
				if (dialog != null && dialog.freezeOVScene) continue;
				ovscenes.get(ovscene).mousePressed(e);
			}
		}
		catch (ConcurrentModificationException e1)
		{}
		
		if (scene != null && sceneEnabled && dialog == null) scene.mousePressed(e);
		
		if (dialog != null) dialog.mousePressed(e);
	}
	
	@Override
	public void mouseReleased(MouseEvent e)
	{
		try
		{
			for (String ovscene : ovscenes.keySet())
			{
				if (dialog != null && dialog.freezeOVScene) continue;
				ovscenes.get(ovscene).mouseReleased(e);
			}
		}
		catch (ConcurrentModificationException e1)
		{}
		
		if (scene != null && sceneEnabled && dialog == null) scene.mouseReleased(e);
		
		if (dialog != null) dialog.mouseReleased(e);
	}
	
	@Override
	public void mouseEntered(MouseEvent e)
	{
		try
		{
			for (String ovscene : ovscenes.keySet())
			{
				if (dialog != null && dialog.freezeOVScene) continue;
				ovscenes.get(ovscene).mouseEntered(e);
			}
		}
		catch (ConcurrentModificationException e1)
		{}
		
		if (scene != null && sceneEnabled && dialog == null) scene.mouseEntered(e);
		
		if (dialog != null) dialog.mouseEntered(e);
	}
	
	@Override
	public void mouseExited(MouseEvent e)
	{
		try
		{
			for (String ovscene : ovscenes.keySet())
			{
				if (dialog != null && dialog.freezeOVScene) continue;
				ovscenes.get(ovscene).mouseExited(e);
			}
		}
		catch (ConcurrentModificationException e1)
		{}
		
		if (scene != null && sceneEnabled && dialog == null) scene.mouseExited(e);
		
		if (dialog != null) dialog.mouseExited(e);
	}
	
	@Override
	public void keyTyped(KeyEvent e)
	{
		try
		{
			for (String ovscene : ovscenes.keySet())
			{
				if (dialog != null && dialog.freezeOVScene) continue;
				ovscenes.get(ovscene).keyTyped(e);
			}
		}
		catch (ConcurrentModificationException e1)
		{}
		
		if (scene != null && sceneEnabled && dialog == null) scene.keyTyped(e);
		
		if (dialog != null) dialog.keyTyped(e);
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
				CFG.UIDEBUG = !CFG.UIDEBUG;
				break;
			case KeyEvent.VK_F4:
				toggleOVScene(new OVScene_Info(), "Info");
				break;
		}
		
		try
		{
			for (String ovscene : ovscenes.keySet())
			{
				if (dialog != null && dialog.freezeOVScene) continue;
				ovscenes.get(ovscene).keyPressed(e);
			}
		}
		catch (ConcurrentModificationException e1)
		{}
		
		if (scene != null && sceneEnabled && dialog == null) scene.keyPressed(e);
		
		if (dialog != null) dialog.keyPressed(e);
	}
	
	@Override
	public void keyReleased(KeyEvent e)
	{
		try
		{
			for (String ovscene : ovscenes.keySet())
			{
				if (dialog != null && dialog.freezeOVScene) continue;
				ovscenes.get(ovscene).keyReleased(e);
			}
		}
		catch (ConcurrentModificationException e1)
		{}
		
		if (scene != null && sceneEnabled && dialog == null) scene.keyReleased(e);
		
		if (dialog != null) dialog.keyReleased(e);
	}
	
	public static void setSceneEnabled(boolean b)
	{
		CursorText.removeCursorTextByParent(scene.getClass().getSimpleName());
		sceneEnabled = b;
	}
	
	public static boolean isSceneEnabled()
	{
		return sceneEnabled;
	}
	
	static class Scale
	{
		Image image;
		HashMap<Dimension, Image> cache = new HashMap<Dimension, Image>();
		
		public Scale(String p)
		{
			image = loadImage(p);
		}
		
		public Image getDimension(int w, int h, int hints)
		{
			Dimension key = new Dimension(w, h);
			
			if (cache.containsKey(key))
			{
				return cache.get(key);
			}
			else
			{
				cache.put(key, image.getScaledInstance(w, h, hints));
				return cache.get(key);
			}
		}
	}
}