package de.dakror.liturfaliar.util;

import java.awt.Color;
import java.awt.Font;
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
  
  public GameFrame()
  {}
  
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
    try
    {
      w = new JFrame(CFG.WINDOWTITLE);
      w.setSize(Toolkit.getDefaultToolkit().getScreenSize());
      w.setUndecorated(true);
      try
      {
        w.setFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/morpheus.ttf")).deriveFont(20f));
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      w.setBackground(Color.black);
      w.setForeground(Color.white);
      this.running = true;
      init();
      w.setVisible(true);
      w.createBufferStrategy(2);
      mainloop();
      close();
    }
    finally
    {
      w.dispose();
    }
  }
  
  public void mainloop()
  {
    long startTime = System.currentTimeMillis();
    long tickTime = startTime;
    while (this.running)
    {
      if (this.frozen)
        continue;
      
      long timePassed = System.currentTimeMillis() - tickTime;
      if (timePassed >= 20)
      {
        tickTime += timePassed;
        update(timePassed);
      }
      BufferStrategy s = w.getBufferStrategy();
      Graphics2D g = (Graphics2D) s.getDrawGraphics();
      g.translate(w.getInsets().left, w.getInsets().top);
      
      g.clearRect(0, 0, w.getWidth(), w.getHeight());
      draw(g);
      g.dispose();
      
      if (!s.contentsLost())
        s.show();
    }
  }
  
  public abstract void update(long paramLong);
  
  public abstract void draw(Graphics2D paramGraphics2D);
  
  public abstract void init();
  
  public abstract void close();
}
