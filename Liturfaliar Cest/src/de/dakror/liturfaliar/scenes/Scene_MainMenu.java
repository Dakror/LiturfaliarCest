package de.dakror.liturfaliar.scenes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.editor.MapEditor;
import de.dakror.liturfaliar.settings.CFG;
import de.dakror.liturfaliar.ui.Button;
import de.dakror.liturfaliar.ui.HandleArea;
import de.dakror.liturfaliar.ui.ProgressBar;
import de.dakror.liturfaliar.ui.Tooltip;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.FileManager;
import de.dakror.universion.UniVersion;

public class Scene_MainMenu implements Scene
{
  boolean       optionsToggle;
  Button[]      buttons        = new Button[3];
  HandleArea    credits;
  HandleArea    optionsToggleArea;
  Button        mapeditor;
  ProgressBar[] optionsSliders = new ProgressBar[2];
  Viewport      v;
  
  @Override
  public void construct(Viewport v)
  {
    this.v = v;
    optionsSliders[0] = new ProgressBar(v.w.getWidth() / 2 - 100, v.w.getHeight() - 80, 200, (float) v.fSoundID, true, "ffc744", "Soundeffekte", true);
    optionsSliders[1] = new ProgressBar(v.w.getWidth() / 2 - 100, v.w.getHeight() - 50, 200, (float) v.fMusicID, true, "ffc744", "Musik", true);
    optionsToggle = false;
    optionsToggleArea = new HandleArea(v.w.getWidth() / 2 - 96, v.w.getHeight() - 48, 192, 64);
    credits = new HandleArea(14, v.w.getHeight() - 28, 110, 24);
    buttons[0] = new Button(v.w.getWidth() / 2 - 150, 300, 300, "Spiel laden", Color.white, 40.0f);
    if (FileManager.getSaves().length == 0)
      buttons[0].disabled = true;
    buttons[1] = new Button(v.w.getWidth() / 2 - 150, 400, 300, "Neues Spiel", Color.white, 40.0f);
    buttons[2] = new Button(v.w.getWidth() / 2 - 150, 500, 300, "Spiel beenden", Color.white, 40.0f);
    if (CFG.MAPEDITOR)
    {
      mapeditor = new Button(-8, 50, 60, 60, Viewport.loadImage("system/mapeditor.png"));
      mapeditor.tooltip = new Tooltip("<#ffffff;20;1>Karteneditor", mapeditor);
      mapeditor.tooltip.tileset = null;
      mapeditor.tooltip.follow = true;
      mapeditor.tileset = "Wood";
      mapeditor.iw = -20;
      mapeditor.ih = -20;
      
    }
    v.playMusic("013-Theme02", false);
  }
  
  @Override
  public void update(long timePassed)
  {
    for (Button b : buttons)
    {
      if (b != null)
        b.update();
    }
    
    if (buttons[0] != null && buttons[0].getState() == 1)
    {
      v.setScene(new Scene_LoadGame());
      buttons[0].setState(0);
    }
    
    if (buttons[1] != null && buttons[1].getState() == 1)
    {
      v.setScene(new Scene_NewGame());
      buttons[1].setState(0);
    }
    
    if (buttons[2] != null && buttons[2].getState() == 1)
    {
      buttons[2].setState(0);
      v.stop();
    }
    
    if (mapeditor != null)
    {
      mapeditor.update();
      if (mapeditor.getState() == 1)
      {
        v.mapeditor = new MapEditor(v);
        mapeditor.setState(0);
      }
    }
    
    if (credits == null)
    {
      return;
    }
    
    if (credits.state == 1)
    {
      v.setScene(new Scene_Credits());
      credits.state = 0;
    }
    
    if (optionsSliders[0] != null)
    {
      v.fSoundID = new BigDecimal(optionsSliders[0].value).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
      FileManager.saveOptions(v);
    }
    
    if (optionsSliders[1] != null)
    {
      v.fMusicID = new BigDecimal(optionsSliders[1].value).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
      v.ss.setVolume(v.MusicID, (float) v.fMusicID);
      FileManager.saveOptions(v);
    }
    
    optionsToggleArea.update(v);
    if (optionsToggleArea != null && optionsToggleArea.state == 1)
    {
      optionsToggle = !optionsToggle;
      optionsToggleArea.state = 0;
      optionsToggleArea = null;
    }
    credits.update(v);
  }
  
  @Override
  public void draw(Graphics2D g)
  {
    Assistant.drawMenuBackground(g, v.w);
    // title
    g.drawImage(Viewport.loadImage("system/lc.png"), 400, 75, v.w.getWidth() - 800, (int) (((v.w.getWidth() - 800) / (float) Viewport.loadImage("system/lc.png").getWidth(v.w)) * Viewport.loadImage("system/lc.png").getHeight(v.w)), v.w);
    
    if (mapeditor != null)
    {
      mapeditor.draw(g, v);
    }
    
    // buttons
    int highlighted = -1;
    for (int i = 0; i < buttons.length; i++)
    {
      if (buttons[i] == null)
        continue;
      buttons[i].draw(g, v);
      if (buttons[i].getState() != 0)
      {
        highlighted = i;
      }
    }
    if (highlighted > -1)
      buttons[highlighted].draw(g, v);
    // version stuff
    g.drawImage(Viewport.loadImage("system/logo.png"), 0, 0, 35, 35, v.w);
    
    Assistant.drawString(UniVersion.prettyVersion(), 35, 25, g, Color.white);
    
    Assistant.drawString("Mitwirkende", 12, v.w.getHeight() - 10, g, Color.white);
    credits.draw(g, v);
    // options
    if (optionsToggle)
    {
      Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), v.w.getWidth() / 2 - 96, v.w.getHeight() - 150, 192, 64, g, v.w);
      Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), v.w.getWidth() / 2 - 128, v.w.getHeight() - 102, 256, 96, g, v.w);
      Assistant.drawHorizontallyCenteredString("Optionen", v.w.getWidth() / 2 - 96, 192, v.w.getHeight() - 110, g, 26, Color.white);
      for (int i = 0; i < optionsSliders.length; i++)
      {
        optionsSliders[i].draw(g, v);
      }
      if (optionsToggleArea == null)
      {
        optionsToggleArea = new HandleArea(v.w.getWidth() / 2 - 96, v.w.getHeight() - 150, 192, 64);
      }
    }
    else
    {
      Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), v.w.getWidth() / 2 - 96, v.w.getHeight() - 48, 192, 64, g, v.w);
      Assistant.drawHorizontallyCenteredString("Optionen", v.w.getWidth() / 2 - 96, 192, v.w.getHeight() - 8, g, 26, Color.white);
      if (optionsToggleArea == null)
      {
        optionsToggleArea = new HandleArea(v.w.getWidth() / 2 - 96, v.w.getHeight() - 48, 192, 64);
      }
    }
  }
  
  @Override
  public void keyPressed(KeyEvent e)
  { 
    
  }
  
  @Override
  public void keyReleased(KeyEvent e)
  {}
  
  @Override
  public void mousePressed(MouseEvent e)
  {}
  
  @Override
  public void mouseMoved(MouseEvent e)
  {
    try
    {
      for (int i = 0; i < buttons.length; i++)
      {
        if (buttons[i] == null)
          continue;
        buttons[i].mouseMoved(e);
      }
    }
    catch (Exception e2)
    {
      return;
    }
    
    if (mapeditor != null)
    {
      mapeditor.mouseMoved(e);
      
    }
  }
  
  @Override
  public void mouseReleased(MouseEvent e)
  {
    for (int i = 0; i < buttons.length; i++)
    {
      if (buttons[i] == null)
        continue;
      buttons[i].mouseReleased(e);
    }
    optionsToggleArea.mouseReleased(e);
    credits.mouseReleased(e);
    if (optionsToggle && optionsToggleArea.state == 0)
    {
      for (int i = 0; i < optionsSliders.length; i++)
      {
        optionsSliders[i].mouseReleased(e);
      }
    }
    
    
    if (mapeditor != null)
    {
      mapeditor.mouseReleased(e);
    }
  }
  
  @Override
  public void mouseDragged(MouseEvent e)
  {
    for (int i = 0; i < optionsSliders.length; i++)
    {
      optionsSliders[i].mouseDragged(e);
    }
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
