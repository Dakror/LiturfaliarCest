package de.dakror.liturfaliar.util;

import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import de.dakror.liturfaliar.settings.CFG;

public abstract class GameFrame
{
  protected boolean running;
  protected boolean frozen;
  public JFrame     w;
  ScreenManager     s;
  
  public GameFrame()
  {
    this.s = new ScreenManager();
  }
  
  public void start()
  {
    this.running = true;
    this.frozen = false;
  }
  
  public synchronized void stop()
  {
    this.frozen = false;
    this.running = false;
  }
  
  public void run()
  {
    if (!CFG.DIRECTDRAW)
    {
      try
      {
        w = new JFrame(CFG.WINDOWTITLE);
        w.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        w.setUndecorated(true);
        this.running = true;
        init();
        w.setVisible(true);
        try
        {
          w.createBufferStrategy(2);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
        
        mainloop();
        close();
      }
      finally
      {
        w.dispose();
      }
    }
    else
    {
      this.s.setFullScreen(null);
      try
      {
        w = (JFrame) this.s.getFullScreenWindow();
        w.setAlwaysOnTop(true);
        
        this.running = true;
        init();
        mainloop();
        close();
      }
      finally
      {
        this.s.restoreScreen();
      }
    }
  }
  
  public void mainloop()
  {
    long startTime = System.currentTimeMillis();
    long tickTime = startTime;
    while (this.running)
    {
      if (this.frozen) continue;
      
      long timePassed = System.currentTimeMillis() - tickTime;
      if (timePassed >= 30)
      {
        tickTime += timePassed;
        update(timePassed);
      }
      if (!CFG.DIRECTDRAW)
      {
        BufferStrategy s = w.getBufferStrategy();
        Graphics2D g = (Graphics2D) s.getDrawGraphics();
        
        g.clearRect(0, 0, w.getWidth(), w.getHeight());
        draw(g);
        g.dispose();
        
        if (!s.contentsLost()) s.show();
      }
      else
      {
        Graphics2D g = s.getGraphics();
        g.clearRect(0, 0, w.getWidth(), w.getHeight());
        draw(g);
        g.dispose();
        s.update();
      }
    }
  }
  
  public abstract void update(long paramLong);
  
  public abstract void draw(Graphics2D paramGraphics2D);
  
  public abstract void init();
  
  public abstract void close();
}
