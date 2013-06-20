package de.dakror.liturfaliar.scenes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import org.json.JSONException;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.item.Equipment;
import de.dakror.liturfaliar.ui.Box;
import de.dakror.liturfaliar.ui.Button;
import de.dakror.liturfaliar.ui.Container;
import de.dakror.liturfaliar.ui.HTMLLabel;
import de.dakror.liturfaliar.ui.Label;
import de.dakror.liturfaliar.ui.VirtualKey;
import de.dakror.liturfaliar.ui.VirtualMouse;
import de.dakror.liturfaliar.util.Assistant;

public class Scene_Tutorial implements Scene
{
  Viewport   v;
  Container  c1;
  VirtualKey w;
  Box        move, mouse;
  Button     next;
  boolean[]  dirs = { false, false, false, false };
  
  @Override
  public void construct(Viewport v)
  {
    this.v = v;
    c1 = new Container(0, 0, v.w.getWidth(), 55, null);
    move = new Box(v.w.getWidth() / 5, v.w.getHeight() / 2 - v.w.getHeight() / 5, v.w.getWidth() / 5, 270);
    move.addComponent(new Container(0, 0, v.w.getWidth() / 5, v.w.getWidth() / 5, null), new Label(0, 0, v.w.getWidth() / 5, "Bewegen", 35, Color.white), new HTMLLabel(10, 40, v.w.getWidth() / 5, v.w.getWidth() / 5, "<#aaaaaa;20;0>Mit diesen Tasten kannst du deine Figur bewegen."), new VirtualKey(v.w.getWidth() / 5 / 2 - 35, 110, KeyEvent.VK_W), new VirtualKey(v.w.getWidth() / 5 / 2 - 105, 180, KeyEvent.VK_A), new VirtualKey(v.w.getWidth() / 5 / 2 - 35, 180, KeyEvent.VK_S), new VirtualKey(v.w.getWidth() / 5 / 2 + 35, 180, KeyEvent.VK_D));
    mouse = new Box(v.w.getWidth() - v.w.getWidth() / 5 * 2, v.w.getHeight() / 2 - v.w.getHeight() / 5, v.w.getWidth() / 5, 270);
    mouse.addComponent(new Container(0, 0, v.w.getWidth() / 5, v.w.getWidth() / 5, null), new Label(0, 0, v.w.getWidth() / 5, "Interaktionen", 35, Color.white), new HTMLLabel(10, 40, v.w.getWidth() / 5, v.w.getWidth() / 5, "<#aaaaaa;20;0>Mit der linken Maustaste kannst du mit NPCs und Objekten interagieren."), new VirtualMouse(v.w.getWidth() / 5 / 2 - 50, 110));
    next = new Button(v.w.getWidth() / 2 - 150, v.w.getHeight() / 2 + v.w.getHeight() / 5 * 2, 300, "Weiter", Color.white, 35);
  }
  
  @Override
  public void update(long timePassed)
  {
    next.update();
    if (next.getState() == 1)
      v.setScene(new Scene_Game());
  }
  
  @Override
  public void draw(Graphics2D g)
  {
    Assistant.drawMenuBackground(g, v.w);
    c1.draw(g, v);
    Assistant.drawHorizontallyCenteredString("Tutorial", v.w.getWidth(), 43, g, 45, Color.white);
    move.draw(g, v);
    mouse.draw(g, v);
    next.draw(g, v);
    int frame = 0;
    int dir = 0;
    if (dirs[0] || dirs[1] || dirs[2] || dirs[3])
      frame = v.getFrame();
    if (dirs[0])
      dir = 3;
    if (dirs[1])
      dir = 1;
    if (dirs[2])
      dir = 2;
    if (dirs[3])
      dir = 0;
    try
    {
      Assistant.drawChar(v.w.getWidth() / 5 * 2, v.w.getHeight() / 2 - v.w.getWidth() / 5 * 3 / 4, v.w.getWidth() / 5, v.w.getWidth() / 5 * 3 / 2, dir, frame, new Equipment(v.savegame.getJSONObject("char").getJSONObject("equip")), g, v.w, true);
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
  }
  
  @Override
  public void keyTyped(KeyEvent e)
  {}
  
  @Override
  public void keyPressed(KeyEvent e)
  {
    move.keyPressed(e);
    switch (e.getExtendedKeyCode())
    {
      case KeyEvent.VK_W:
      {
        dirs[0] = true;
        break;
      }
      case KeyEvent.VK_A:
      {
        dirs[1] = true;
        break;
      }
      case KeyEvent.VK_D:
      {
        dirs[2] = true;
        break;
      }
      case KeyEvent.VK_S:
      {
        dirs[3] = true;
        break;
      }
    }
  }
  
  @Override
  public void keyReleased(KeyEvent e)
  {
    move.keyReleased(e);
    switch (e.getExtendedKeyCode())
    {
      case KeyEvent.VK_W:
      {
        dirs[0] = false;
        break;
      }
      case KeyEvent.VK_A:
      {
        dirs[1] = false;
        break;
      }
      case KeyEvent.VK_D:
      {
        dirs[2] = false;
        break;
      }
      case KeyEvent.VK_S:
      {
        dirs[3] = false;
        break;
      }
    }
  }
  
  @Override
  public void mouseClicked(MouseEvent e)
  {}
  
  @Override
  public void mousePressed(MouseEvent e)
  {
    mouse.mousePressed(e);
    next.mousePressed(e);
  }
  
  @Override
  public void mouseReleased(MouseEvent e)
  {
    mouse.mouseReleased(e);
    next.mouseReleased(e);
  }
  
  @Override
  public void mouseEntered(MouseEvent e)
  {}
  
  @Override
  public void mouseExited(MouseEvent e)
  {}
  
  @Override
  public void mouseDragged(MouseEvent e)
  {}
  
  @Override
  public void mouseMoved(MouseEvent e)
  {
    next.mouseMoved(e);
  }
  
  @Override
  public void mouseWheelMoved(MouseWheelEvent e)
  {}
  
  @Override
  public void destruct()
  {}
}
