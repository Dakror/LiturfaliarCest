package de.dakror.liturfaliar.scenes;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.util.ArrayList;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.settings.CFG;
import de.dakror.liturfaliar.ui.Container;
import de.dakror.liturfaliar.ui.HandleArea;
import de.dakror.liturfaliar.ui.ProgressBar;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.FileManager;
import de.dakror.liturfaliar.util.ZipAssistant;

public class Scene_Logo implements Scene
{
  float[]                 alphas;
  HandleArea              homepageButton;
  long                    time;
  boolean                 update;
  float                   size;
  int                     fullsize;
  ProgressBar             progress;
  ArrayList<ZipAssistant> downloader;
  
  @Override
  public void construct()
  {
    time = 0;
    alphas = new float[2];
    homepageButton = new HandleArea(0, Viewport.w.getHeight() / 2 + 160 - (int) (26 * 1.4f), Viewport.w.getWidth() - 1, (int) (26 * 1.4f));
    homepageButton.soundCLICK = false;
    homepageButton.soundMOVER = false;
    update = CFG.INTERNET && (FileManager.checkMapPackUpdate() || FileManager.checkMediaUpdate("Sound") || FileManager.checkMediaUpdate("Music") || FileManager.checkMediaUpdate("Animations") || FileManager.checkMediaUpdate("Tiles"));
    downloader = new ArrayList<ZipAssistant>();
    if (update)
    {
      progress = new ProgressBar(9, Viewport.w.getHeight() - 40, Viewport.w.getWidth() - 20, 0, false, "ffc744", null, false);
      downloader.add(FileManager.onMapPackUpdate());
      downloader.add(FileManager.onMediaUpdate("Sound"));
      downloader.add(FileManager.onMediaUpdate("Music"));
      downloader.add(FileManager.onMediaUpdate("Animations"));
      downloader.add(FileManager.onMediaUpdate("Tiles"));
    }
    time = System.currentTimeMillis();
    
    new Thread()
    {
      public void run()
      {
        for (File f : new File(FileManager.dir, "Animations").listFiles())
        {
          if (!f.getName().endsWith(".png")) continue;
          
          Viewport.loadImage("Animations/" + f.getName());
        }
      }
    }.start();
  }
  
  @Override
  public void update(long timePassed)
  {
    alphas[0] += 0.015f;
    if (alphas[0] > 1) alphas[0] = 1;
    if (alphas[0] == 1) alphas[1] += 0.015f;
    if (alphas[1] > 1) alphas[1] = 1;
    if (homepageButton != null && homepageButton.state == 1)
    {
      Assistant.openLink("http://www.dakror.de");
      homepageButton.state = 0;
    }
    if (update)
    {
      fullsize = 0;
      int prog = 0;
      int done = 0;
      for (int i = 0; i < downloader.size(); i++)
      {
        if (downloader.get(i) != null)
        {
          fullsize += downloader.get(i).fullsize;
          prog += downloader.get(i).downloaded;
          if (downloader.get(i).state != null && downloader.get(i).state.equals("Fertig")) done++;
          if (done == downloader.size())
          {
            break;
          }
        }
      }
      progress.value = prog / (float) fullsize;
      progress.title = Assistant.formatBinarySize(prog, 2) + " / " + Assistant.formatBinarySize(fullsize, 2) + " @ " + Assistant.formatBinarySize((long) ((prog / (float) ((System.currentTimeMillis() - time) / 1000))), 1) + "/s";
      
      if (progress.value == 1 && (System.currentTimeMillis() - time) > 5000) Viewport.setScene(new Scene_MainMenu());
    }
    else
    {
      if ((System.currentTimeMillis() - time) > 5000) Viewport.setScene(new Scene_MainMenu());
    }
  }
  
  @Override
  public void draw(Graphics2D g)
  {
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphas[0]));
    g.drawImage(Viewport.loadImage("system/dakror.png"), (Viewport.w.getWidth() - 1000) / 2, (Viewport.w.getHeight() - 305) / 2, 1000, 305, Viewport.w);
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphas[1]));
    Assistant.drawHorizontallyCenteredString("Homepage: www.dakror.de", Viewport.w.getWidth(), Viewport.w.getHeight() / 2 + 155, g, 26);
    homepageButton.draw(g);
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    if (update)
    {
      new Container(0, Viewport.w.getHeight() - 90, Viewport.w.getWidth(), 90).draw(g);
      if (progress != null) progress.draw(g);
      Assistant.drawHorizontallyCenteredString("Aktualisierung", Viewport.w.getWidth(), Viewport.w.getHeight() - 50, g, 30, Color.white);
    }
  }
  
  @Override
  public void keyPressed(KeyEvent e)
  {
    if (e.getKeyCode() == KeyEvent.VK_SPACE)
    {
      if (update)
      {
        boolean finished = false;
        for (int i = 0; i < downloader.size(); i++)
        {
          if (downloader.get(i) != null)
          {
            finished = downloader.get(i).state.equals("Fertig");
          }
        }
        if (finished) Viewport.setScene(new Scene_MainMenu());
      }
      else Viewport.setScene(new Scene_MainMenu());
    }
  }
  
  @Override
  public void keyReleased(KeyEvent e)
  {}
  
  @Override
  public void mousePressed(MouseEvent e)
  {}
  
  @Override
  public void mouseReleased(MouseEvent e)
  {
    if (homepageButton != null) homepageButton.mouseReleased(e);
  }
  
  public void mouseDragged(MouseEvent e)
  {}
  
  public void mouseMoved(MouseEvent e)
  {
    if (homepageButton != null) homepageButton.mouseMoved(e);
  }
  
  @Override
  public void keyTyped(KeyEvent e)
  {}
  
  @Override
  public void mouseClicked(MouseEvent e)
  {}
  
  @Override
  public void mouseEntered(MouseEvent e)
  {}
  
  @Override
  public void mouseExited(MouseEvent e)
  {}
  
  @Override
  public void mouseWheelMoved(MouseWheelEvent e)
  {}
  
  @Override
  public void destruct()
  {}
}
