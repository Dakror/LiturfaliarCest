package de.dakror.liturfaliar.util;

import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import de.dakror.liturfaliar.settings.CFG;

public class ScreenManager
{
  private GraphicsDevice vc;
  
  public ScreenManager()
  {
    GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
    this.vc = e.getDefaultScreenDevice();
  }
  
  public void setFullScreen(DisplayMode dm)
  {
    JFrame window = new JFrame();
    window.setAlwaysOnTop(true);
    window.setTitle(CFG.WINDOWTITLE);
    window.setUndecorated(true);
    window.setIgnoreRepaint(true);
    window.setResizable(false);
    this.vc.setFullScreenWindow(window);
    if ((dm != null) && (this.vc.isDisplayChangeSupported()))
      try
      {
        this.vc.setDisplayMode(dm);
      }
      catch (Exception localException)
      {}
    window.createBufferStrategy(2);
  }
  
  public Graphics2D getGraphics()
  {
    Window window = this.vc.getFullScreenWindow();
    if (window != null)
    {
      BufferStrategy s = window.getBufferStrategy();
      return (Graphics2D) s.getDrawGraphics();
    }
    return null;
  }
  
  public void update()
  {
    Window window = this.vc.getFullScreenWindow();
    if (window != null)
    {
      BufferStrategy s = window.getBufferStrategy();
      if (!s.contentsLost())
        s.show();
    }
  }
  
  public Window getFullScreenWindow()
  {
    return this.vc.getFullScreenWindow();
  }
  
  public void restoreScreen()
  {
    Window window = this.vc.getFullScreenWindow();
    if (window != null)
      window.dispose();
    this.vc.setFullScreenWindow(null);
  }
  
  // public DisplayMode[] getCompatibleDisplayModes()
  // {
  // return this.vc.getDisplayModes();
  // }
  
  // public DisplayMode findFirstCompatibleMode(DisplayMode[] modes)
  // {
  // DisplayMode[] goodModes = this.vc.getDisplayModes();
  // for (DisplayMode mode : modes)
  // for (DisplayMode goodmode : goodModes)
  // if (displayModesMatch(mode, goodmode))
  // return mode;
  // return null;
  // }
  
  // public DisplayMode getCurrentDisplayMode()
  // {
  // return this.vc.getDisplayMode();
  // }
  
  // private boolean displayModesMatch(DisplayMode m1, DisplayMode m2)
  // {
  // if ((m1.getWidth() != m2.getWidth()) || (m1.getHeight() != m2.getHeight()))
  // return false;
  // if ((m1.getBitDepth() != -1) && (m2.getBitDepth() != -1) && (m1.getBitDepth() != m2.getBitDepth()))
  // {
  // return false;
  // }
  // return (m1.getRefreshRate() == 0) || (m2.getRefreshRate() == 0) || (m1.getRefreshRate() == m2.getRefreshRate());
  // }
  
  // public int getWidth()
  // {
  // Window w = this.vc.getFullScreenWindow();
  // if (w != null)
  // {
  // return w.getWidth();
  // }
  // return 0;
  // }
  
  // public int getHeight()
  // {
  // Window w = this.vc.getFullScreenWindow();
  // if (w != null)
  // {
  // return w.getHeight();
  // }
  // return 0;
  // }
  
  // public BufferedImage createCompatibleImage(int width, int height, int alpha)
  // {
  // Window window = this.vc.getFullScreenWindow();
  // if (window != null)
  // {
  // GraphicsConfiguration gc = window.getGraphicsConfiguration();
  // return gc.createCompatibleImage(width, height, alpha);
  // }
  // return null;
  // }
}
