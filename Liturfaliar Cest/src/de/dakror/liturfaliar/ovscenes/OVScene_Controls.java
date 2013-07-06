package de.dakror.liturfaliar.ovscenes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.settings.Colors;
import de.dakror.liturfaliar.settings.Keys;
import de.dakror.liturfaliar.ui.Button;
import de.dakror.liturfaliar.ui.Container;
import de.dakror.liturfaliar.ui.HTMLLabel;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.FileManager;

public class OVScene_Controls extends OVScene
{
  Container c1;
  HTMLLabel label;
  
  // -- buttons -- //
  Button[]  buttons;
  String[]  order = { "UP", "DOWN", "LEFT", "RIGHT", "SPRINT", "PAUSE", "INVENTORY", "SKILLS" };
  
  boolean   waitForInput;
  int       input;
  Button    waiting;
  
  public OVScene_Controls()
  {}
  
  @Override
  public void construct(Viewport v)
  {
    Viewport.sceneEnabled = false;
    this.v = v;
    c1 = new Container(0, 0, v.w.getWidth(), 55);
    c1.tileset = null;
    
    label = new HTMLLabel(v.w.getWidth() / 2 - 180, v.w.getHeight() / 2 - 185, 200, 600, "<#888888;30;1>Bewegung[br]<#ffffff;22;0>  Oben[br]  Unten[br]  Links[br]  Rechts[br]  Sprint[br] [br]<#888888;25;1>Hot-Keys[br]<#ffffff;22;0>  Pause[br]  Inventar[br]  Fähigkeiten[br]");
    buttons = new Button[order.length];
    for (int i = 0; i < order.length; i++)
    {
      try
      {
        Button b = new Button(v.w.getWidth() / 2, v.w.getHeight() / 2 - 130 + i * 29 + ((i > 4) ? 57 : 0), 180, KeyEvent.getKeyText(Keys.class.getField(order[i]).getInt(null)), Color.white, 20);
        b.hovermod = 0;
        b.clickmod = 0;
        b.soundMOVER = false;
        b.round = false;
        b.tileset = null;
        buttons[i] = b;
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
  }
  
  @Override
  public void destruct()
  {}
  
  @Override
  public void update(long timePassed)
  {
    for (Button b : buttons)
    {
      b.update();
      int alreadyBoundIndex = isKeyAlreadyBound(b);
      if (alreadyBoundIndex != -1)
      {
        b.c = Color.decode(Colors.WORSE);
        buttons[alreadyBoundIndex].c = Color.decode(Colors.WORSE);
      }
      else
      {
        b.c = Color.white;
      }
      if (b.getState() == 1)
      {
        if (waitForInput == false && input > 0)
        {
          b.title = KeyEvent.getKeyText(input);
          try
          {
            Keys.class.getField(order[Arrays.asList(buttons).indexOf(b)]).setInt(null, input);
          }
          catch (Exception e)
          {
            e.printStackTrace();
          }
          input = 0;
          b.setState(2);
          waiting = null;
          continue;
        }
        waitForInput = true;
        waiting = b;
        b.title = "< Taste drücken >";
      }
    }
  }
  
  @Override
  public void draw(Graphics2D g)
  {
    Assistant.Shadow(v.w.getBounds(), Color.black, 0.6f, g);
    c1.draw(g, v);
    Assistant.drawHorizontallyCenteredString("Tastenbelegung", v.w.getWidth(), 43, g, 45, Color.white);
    
    Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), v.w.getWidth() / 2 - 200, v.w.getHeight() / 2 - 190, 400, 380, g, v.w);
    label.draw(g, v);
    
    for (Button b : buttons)
    {
      b.draw(g, v);
    }
  }
  
  @Override
  public void keyReleased(KeyEvent e)
  {
    if (e.getKeyCode() == KeyEvent.VK_ESCAPE && !waitForInput)
    {
      v.removeOVScene("Controls");
      Viewport.sceneEnabled = true;
      FileManager.saveOptions(v);
    }
    else if (waitForInput)
    {
      input = e.getKeyCode();
      waitForInput = false;
    }
  }
  
  @Override
  public void mouseMoved(MouseEvent e)
  {
    if (waitForInput)
      return;
    
    for (Button b : buttons)
    {
      b.mouseMoved(e);
    }
  }
  
  @Override
  public void mouseReleased(MouseEvent e)
  {
    if (waitForInput)
      return;
    
    for (Button b : buttons)
    {
      b.mouseReleased(e);
    }
  }
  
  @Override
  public void mousePressed(MouseEvent e)
  {
    if (waitForInput)
      return;
    
    for (Button b : buttons)
    {
      b.mousePressed(e);
    }
  }
  
  public int isKeyAlreadyBound(Button b)
  {
    for (int i = 0; i < order.length; i++)
    {
      Button button = buttons[i];
      if (button.equals(b))
        continue;
      if (button.title.equals(b.title))
        return i;
    }
    return -1;
  }
}
