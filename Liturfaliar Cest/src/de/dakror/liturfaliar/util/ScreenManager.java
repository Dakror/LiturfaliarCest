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
}
