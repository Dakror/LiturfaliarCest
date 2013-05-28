package de.dakror.liturfaliar.scenes;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
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
  Viewport                v;
  ProgressBar             progress;
  ArrayList<ZipAssistant> downloader;
  
  @Override
  public void init(Viewport v)
  {
    this.v = v;
    time = 0;
    alphas = new float[2];
    homepageButton = new HandleArea(0, v.w.getHeight() / 2 + 160 - (int) (26 * 1.4f), v.w.getWidth() - 1, (int) (26 * 1.4f));
    update = CFG.INTERNET && (FileManager.checkMapPackUpdate(v.w) || FileManager.checkMediaUpdate("Sound") || FileManager.checkMediaUpdate("Music") || FileManager.checkMediaUpdate("Animations") || FileManager.checkMediaUpdate("Tiles"));
    downloader = new ArrayList<ZipAssistant>();
    if (update)
    {
      progress = new ProgressBar(9, v.w.getHeight() - 40, v.w.getWidth() - 20, 0, false, "ffc744", null, false);
      downloader.add(FileManager.onMapPackUpdate(v.w));
      downloader.add(FileManager.onMediaUpdate("Sound"));
      downloader.add(FileManager.onMediaUpdate("Music"));
      downloader.add(FileManager.onMediaUpdate("Animations"));
      downloader.add(FileManager.onMediaUpdate("Tiles"));
    }
    time = System.currentTimeMillis();
  }
  
  @Override
  public void update(long timePassed)
  {
    alphas[0] += 0.015f;
    if (alphas[0] > 1)
      alphas[0] = 1;
    if (alphas[0] == 1)
      alphas[1] += 0.015f;
    if (alphas[1] > 1)
      alphas[1] = 1;
    if (homepageButton != null && homepageButton.state == 1)
    {
      Assistant.openLink("http://www.dakror.de");
      homepageButton.state = 0;
    }
    if (update)
    {
      fullsize = 0;
      int prog = 0;
      boolean finished = false;
      int done = 0;
      for (int i = 0; i < downloader.size(); i++)
      {
        if (downloader.get(i) != null)
        {
          fullsize += downloader.get(i).fullsize;
          prog += downloader.get(i).downloaded;
          if (downloader.get(i).state != null && downloader.get(i).state.equals("Fertig"))
            done++;
          if (done == downloader.size())
          {
            finished = true;
            break;
          }
        }
      }
      progress.value = prog / (float) fullsize;
      progress.title = Assistant.formatBinarySize(prog, 2) + " / " + Assistant.formatBinarySize(fullsize, 2) + " @ " + Assistant.formatBinarySize((long) ((prog / (float) ((System.currentTimeMillis() - time) / 1000))), 1) + "/s";
      if (finished && (System.currentTimeMillis() - time) > 5000)
        v.setScene(new Scene_MainMenu());
    }
    else
    {
      if ((System.currentTimeMillis() - time) > 5000)
        v.setScene(new Scene_MainMenu());
    }
  }
  
  @Override
  public void draw(Graphics2D g)
  {
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphas[0]));
    g.drawImage(Viewport.loadImage("system/dakror.png"), (v.w.getWidth() - 1000) / 2, (v.w.getHeight() - 305) / 2, 1000, 305, v.w);
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphas[1]));
    Assistant.drawHorizontallyCenteredString("Homepage: www.dakror.de", v.w.getWidth(), v.w.getHeight() / 2 + 155, g, 26);
    homepageButton.draw(g, v);
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    if (update)
    {
      new Container(0, v.w.getHeight() - 90, v.w.getWidth(), 90).draw(g, v);
      if (progress != null)
        progress.draw(g, v);
      Assistant.drawHorizontallyCenteredString("Aktualisierung", v.w.getWidth(), v.w.getHeight() - 50, g, 30, Color.white);
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
        if (finished)
          v.setScene(new Scene_MainMenu());
      }
      else v.setScene(new Scene_MainMenu());
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
    if (homepageButton != null)
      homepageButton.mouseReleased(e);
  }
  
  public void mouseDragged(MouseEvent e)
  {}
  
  public void mouseMoved(MouseEvent e)
  {
    if (homepageButton != null)
      homepageButton.mouseMoved(e);
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
}
