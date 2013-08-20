package de.dakror.liturfaliar.scenes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.ovscenes.OVScene_Controls;
import de.dakror.liturfaliar.ui.Button;
import de.dakror.liturfaliar.ui.HandleArea;
import de.dakror.liturfaliar.ui.ProgressBar;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.FileManager;
import de.dakror.universion.UniVersion;

public class Scene_MainMenu implements Scene
{
  boolean       optionsToggle;
  Button[]      buttons        = new Button[3];
  Button        controls;
  HandleArea    credits;
  HandleArea    optionsToggleArea;
  ProgressBar[] optionsSliders = new ProgressBar[2];
  
  @Override
  public void construct()
  {
    optionsSliders[0] = new ProgressBar(Viewport.w.getWidth() / 2 - 100, Viewport.w.getHeight() - 90, 200, (float) Viewport.fSoundID, true, "ffc744", "Soundeffekte", true);
    optionsSliders[1] = new ProgressBar(Viewport.w.getWidth() / 2 - 100, Viewport.w.getHeight() - 68, 200, (float) Viewport.fMusicID, true, "ffc744", "Musik", true);
    controls = new Button(Viewport.w.getWidth() / 2 - 97, Viewport.w.getHeight() - 43, 195, "Tastenbelegung", Color.white, 18f);
    controls.tileset = null;
    controls.hovermod = 0;
    controls.clickmod = 0;
    controls.soundMOVER = false;
    optionsToggle = false;
    optionsToggleArea = new HandleArea(Viewport.w.getWidth() / 2 - 96, Viewport.w.getHeight() - 48, 192, 64);
    credits = new HandleArea(14, Viewport.w.getHeight() - 28, 110, 24);
    buttons[0] = new Button(Viewport.w.getWidth() / 2 - 150, 300, 300, "Spiel laden", Color.white, 40.0f);
    if (FileManager.getSaves().length == 0) buttons[0].disabled = true;
    buttons[1] = new Button(Viewport.w.getWidth() / 2 - 150, 400, 300, "Neues Spiel", Color.white, 40.0f);
    buttons[2] = new Button(Viewport.w.getWidth() / 2 - 150, 500, 300, "Spiel beenden", Color.white, 40.0f);
    Viewport.playMusic("013-Theme02", false);
  }
  
  @Override
  public void update(long timePassed)
  {
    for (Button b : buttons)
    {
      if (b != null) b.update();
    }
    
    if (buttons[0] != null && buttons[0].getState() == 1)
    {
      Viewport.setScene(new Scene_LoadGame());
      buttons[0].setState(0);
    }
    
    if (buttons[1] != null && buttons[1].getState() == 1)
    {
      Viewport.setScene(new Scene_NewGame());
      buttons[1].setState(0);
    }
    
    if (buttons[2] != null && buttons[2].getState() == 1)
    {
      buttons[2].setState(0);
      System.exit(0);
    }
    
    if (credits.state == 1)
    {
      Viewport.setScene(new Scene_Credits());
      credits.state = 0;
    }
    
    if (optionsSliders[0] != null)
    {
      double vol = new BigDecimal(optionsSliders[0].value).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
      if (vol != Viewport.fSoundID)
      {
        Viewport.fSoundID = vol;
        FileManager.saveOptions();
      }
    }
    
    if (controls != null)
    {
      controls.update();
      if (controls.getState() == 1)
      {
        Viewport.addOVScene(new OVScene_Controls(), "Controls");
        controls.setState(0);
      }
    }
    
    if (optionsSliders[1] != null)
    {
      double vol = new BigDecimal(optionsSliders[1].value).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
      if (Viewport.fMusicID != vol)
      {
        Viewport.fMusicID = vol;
        Viewport.ss.setVolume(Viewport.MusicID, (float) Viewport.fMusicID);
        FileManager.saveOptions();
      }
    }
    
    optionsToggleArea.update();
    if (optionsToggleArea != null && optionsToggleArea.state == 1)
    {
      optionsToggle = !optionsToggle;
      optionsToggleArea.state = 0;
      optionsToggleArea = null;
    }
    credits.update();
  }
  
  @Override
  public void draw(Graphics2D g)
  {
    Assistant.drawMenuBackground(g);
    // title
    g.drawImage(Viewport.loadImage("system/lc.png"), 400, 75, Viewport.w.getWidth() - 800, (int) (((Viewport.w.getWidth() - 800) / (float) Viewport.loadImage("system/lc.png").getWidth(Viewport.w)) * Viewport.loadImage("system/lc.png").getHeight(Viewport.w)), Viewport.w);
    
    // buttons
    int highlighted = -1;
    for (int i = 0; i < buttons.length; i++)
    {
      if (buttons[i] == null) continue;
      buttons[i].draw(g);
      if (buttons[i].getState() != 0)
      {
        highlighted = i;
      }
    }
    if (highlighted > -1) buttons[highlighted].draw(g);
    // version stuff
    g.drawImage(Viewport.loadImage("system/logo.png"), 0, 0, 35, 35, Viewport.w);
    
    Assistant.drawString(UniVersion.prettyVersion(), 35, 25, g, Color.white);
    
    Assistant.drawString("Mitwirkende", 12, Viewport.w.getHeight() - 10, g, Color.white);
    credits.draw(g);
    // options
    if (optionsToggle)
    {
      Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), Viewport.w.getWidth() / 2 - 96, Viewport.w.getHeight() - 150, 192, 64, g);
      Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), Viewport.w.getWidth() / 2 - 128, Viewport.w.getHeight() - 102, 256, 96, g);
      Assistant.drawHorizontallyCenteredString("Optionen", Viewport.w.getWidth() / 2 - 96, 192, Viewport.w.getHeight() - 110, g, 26, Color.white);
      
      for (int i = 0; i < optionsSliders.length; i++)
        optionsSliders[i].draw(g);
      
      controls.draw(g);
      if (optionsToggleArea == null) optionsToggleArea = new HandleArea(Viewport.w.getWidth() / 2 - 96, Viewport.w.getHeight() - 150, 192, 64);
      
    }
    else
    {
      Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), Viewport.w.getWidth() / 2 - 96, Viewport.w.getHeight() - 48, 192, 64, g);
      Assistant.drawHorizontallyCenteredString("Optionen", Viewport.w.getWidth() / 2 - 96, 192, Viewport.w.getHeight() - 8, g, 26, Color.white);
      
      if (optionsToggleArea == null) optionsToggleArea = new HandleArea(Viewport.w.getWidth() / 2 - 96, Viewport.w.getHeight() - 48, 192, 64);
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
  {
    if (optionsToggle && optionsToggleArea.state == 0) for (int i = 0; i < optionsSliders.length; i++)
      optionsSliders[i].mousePressed(e);
    
    if (optionsToggle && optionsToggleArea.state == 0) controls.mousePressed(e);
  }
  
  @Override
  public void mouseMoved(MouseEvent e)
  {
    try
    {
      for (int i = 0; i < buttons.length; i++)
      {
        if (buttons[i] == null) continue;
        buttons[i].mouseMoved(e);
      }
    }
    catch (Exception e2)
    {}
    
    if (optionsToggle && optionsToggleArea.state == 0) controls.mouseMoved(e);
  }
  
  @Override
  public void mouseReleased(MouseEvent e)
  {
    for (int i = 0; i < buttons.length; i++)
    {
      if (buttons[i] == null) continue;
      buttons[i].mouseReleased(e);
    }
    optionsToggleArea.mouseReleased(e);
    credits.mouseReleased(e);
    
    if (optionsToggle && optionsToggleArea.state == 0) controls.mouseReleased(e);
  }
  
  @Override
  public void mouseDragged(MouseEvent e)
  {
    if (optionsToggle && optionsToggleArea.state == 0) for (int i = 0; i < optionsSliders.length; i++)
      optionsSliders[i].mouseDragged(e);
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
