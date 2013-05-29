package de.dakror.liturfaliar.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.settings.Colors;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.Handler;

public class Dialog extends Component
{
  public static final int     MESSAGE       = 0;
  public static final int     ERROR         = 0;
  public Button[]             buttons;
  public String               title;
  public String               message;
  public ArrayList<Component> children;
  public int                  selected      = -1;
  Button                      close;
  public Point                mouse;
  public boolean              closeDisabled = false;
  public boolean              closeRequested;
  
  public Dialog(String title, String message, int type, Viewport v)
  {
    super(-1, -1, 640, -1);
    this.title = title;
    this.buttons = new Button[0];
    this.message = message;
    this.children = new ArrayList<Component>();
    this.mouse = new Point(0, 0);
    open(v);
  }
  
  public void open(Viewport v)
  {
    Handler.setListenerEnabled(v.scene, false);
    Handler.addListener(this);
  }
  
  public void close(Viewport v)
  {
    Handler.removeListener(this);
    Handler.setListenerEnabled(v.scene, true);
  }
  
  public void setButtons(String... titles)
  {
    if (getHeight() == -1)
      return;
    this.buttons = new Button[titles.length];
    int width = getWidth() / titles.length;
    for (int i = 0; i < titles.length; i++)
    {
      Button button = new Button(getX() + width * i, getY() + getHeight(), width, titles[i], Color.white, 25.0f);
      this.buttons[i] = button;
    }
  }
  
  public void update()
  {
    if (this.close != null)
      this.close.update();
    if (this.buttons.length > 0)
    {
      for (int i = 0; i < buttons.length; i++)
      {
        if (this.buttons[i].handle.state > 0)
          this.selected = i;
        this.buttons[i].update();
      }
    }
    if (this.close != null && this.close.handle.state == 1)
      this.closeRequested = true;
    else this.closeRequested = false;
    for (Component child : this.children)
    {
      child.update();
    }
  }
  
  public void draw(Graphics2D g, Viewport v)
  {
    setX(v.w.getWidth() / 2 - getWidth() / 2);
    int sy = v.w.getHeight() / 3 - getHeight() / 2;
    if (getHeight() > v.w.getHeight() / 3)
      sy = v.w.getHeight() / 2 - getHeight() / 2;
    if (getY() != sy)
    {
      setY(sy);
      this.close = null;
    }
    if (this.close == null && !this.closeDisabled)
    {
      this.close = new Button(getX() + 8, getY() - 10 - 30, 30, 30, "round_delete_icon");
      this.close.tileset = "";
      this.close.hovermod = 4;
      this.close.soundMOVER = false;
    }
    // -- just init stuff -- //
    String[] lines = this.message.split("\\[br\\]");
    if (getHeight() == -1 && this.message.length() > 0)
      setHeight((int) (lines.length * 20 * 1.4f + 32 + (30 * 1.4f)));
    Assistant.Shadow(new Rectangle2D.Double(0, 0, v.w.getWidth(), v.w.getHeight()), Colors.DGRAY, 0.8f, g);
    Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), getX(), getY(), getWidth(), getHeight(), g, v.w);
    Assistant.drawHorizontallyCenteredString(this.title, getX() + 16, getWidth(), getY() + (int) (30 * 1.4f), g, 30, Color.decode("#999999"));
    if (this.close != null)
    {
      Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), getX(), getY() - 48, 48, 48, g, v.w);
      this.close.draw(g, v);
    }
    for (int i = 0; i < lines.length; i++)
    {
      if (lines[i].length() == 0)
        continue;
      Assistant.drawHorizontallyCenteredString(lines[i], getX() + 16, getWidth(), (int) (getY() + (30 * 1.4f) + (i + 1) * 20 * 1.4f), g, 20, Color.white);
    }
    int selected = -1;
    for (int i = 0; i < this.children.size(); i++)
    {
      Component child = this.children.get(i);
      child.draw(g, v);
      if (child.getArea() != null && child.getArea().contains(this.mouse))
        selected = i;
    }
    if (this.buttons.length > 0)
    {
      for (int i = 0; i < this.buttons.length; i++)
      {
        this.buttons[i].setY(getY() + getHeight());
        this.buttons[i].draw(g, v);
      }
      if (this.selected > -1)
        this.buttons[this.selected].draw(g, v);
    }
    if (selected > -1)
      this.children.get(selected).draw(g, v);
  }
  
  public void mouseReleased(MouseEvent e)
  {
    if (this.buttons.length > 0)
    {
      for (int i = 0; i < buttons.length; i++)
      {
        buttons[i].mouseReleased(e);
      }
    }
    if (this.close != null)
      this.close.mouseReleased(e);
    for (Component child : this.children)
    {
      child.mouseReleased(e);
    }
  }
  
  public void mouseMoved(MouseEvent e)
  {
    this.mouse = e.getLocationOnScreen();
    if (this.buttons.length > 0)
    {
      for (int i = 0; i < this.buttons.length; i++)
      {
        buttons[i].mouseMoved(e);
      }
    }
    if (this.close != null)
      this.close.mouseMoved(e);
    for (Component child : this.children)
    {
      child.mouseMoved(e);
    }
  }
  
  @Override
  public void mouseDragged(MouseEvent e)
  {
    for (Component child : this.children)
    {
      child.mouseDragged(e);
    }
  }
  
  @Override
  public void mouseClicked(MouseEvent e)
  {
    for (Component child : this.children)
    {
      child.mouseClicked(e);
    }
  }
  
  @Override
  public void mousePressed(MouseEvent e)
  {
    for (Component child : this.children)
    {
      child.mousePressed(e);
    }
  }
  
  @Override
  public void mouseEntered(MouseEvent e)
  {
    for (Component child : this.children)
    {
      child.mouseEntered(e);
    }
  }
  
  @Override
  public void mouseExited(MouseEvent e)
  {
    for (Component child : this.children)
    {
      child.mouseExited(e);
    }
  }
  
  @Override
  public void mouseWheelMoved(MouseWheelEvent e)
  {
    for (Component child : this.children)
    {
      child.mouseWheelMoved(e);
    }
  }
  
  @Override
  public void keyTyped(KeyEvent e)
  {
    for (Component child : this.children)
    {
      child.keyTyped(e);
    }
  }
  
  @Override
  public void keyPressed(KeyEvent e)
  {
    if (e.getExtendedKeyCode() == KeyEvent.VK_ESCAPE && !this.closeDisabled)
    {
      this.closeRequested = true;
      return;
    }
    for (Component child : this.children)
    {
      child.keyPressed(e);
    }
  }
  
  @Override
  public void keyReleased(KeyEvent e)
  {
    for (Component child : this.children)
    {
      child.keyReleased(e);
    }
  }
}
