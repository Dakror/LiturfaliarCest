package de.dakror.liturfaliar.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Window;

public abstract class GameFrame
{
  protected boolean       running;
  protected boolean       frozen;
  protected ScreenManager s;
  
  public GameFrame()
  {
    this.s = new ScreenManager();
    this.s.setFullScreen(null);
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
  
  public synchronized void freeze()
  {
    this.frozen = true;
    this.s.getFullScreenWindow().setVisible(false);
  }
  
  
  public synchronized void unfreeze()
  {
    this.frozen = false;
    this.s.getFullScreenWindow().setVisible(true);
  }
  
  public void run()
  {
    try
    {
      Window w = this.s.getFullScreenWindow();
      try
      {
        w.setFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/morpheus.ttf")).deriveFont(20f));
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      w.setAlwaysOnTop(true);
      w.setBackground(Color.black);
      w.setForeground(Color.white);
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
  
  public void mainloop()
  {
    long startTime = System.currentTimeMillis();
    long tickTime = startTime;
    while (this.running)
    {
      if (this.frozen)
      {
        continue;
      }
      
      long timePassed = System.currentTimeMillis() - tickTime;
      if (timePassed > 10)
      {
        tickTime += timePassed;
        update(timePassed);
      }
      
      Graphics2D g = this.s.getGraphics();
      try
      {
        g.clearRect(0, 0, this.s.getFullScreenWindow().getWidth(), this.s.getFullScreenWindow().getHeight());
      }
      catch (Exception e)
      { 
        continue;
      }
      draw(g);
      g.dispose();
      this.s.update();
    }
  }
  
  public abstract void update(long paramLong);
  
  public abstract void draw(Graphics2D paramGraphics2D);
  
  public abstract void init();
  
  public abstract void close();
}
