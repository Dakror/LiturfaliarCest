package de.dakror.liturfaliar.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.settings.Colors;
import de.dakror.liturfaliar.util.Assistant;

public class Chooser extends Component
{
  private Object[] elem;
  private Image[]  icons;
  private String   title;
  private int      selected, oldselected;
  private Button   prev;
  private Button   next;
  private boolean  focus;
  public boolean   showIndex;
  public boolean   alternate;
  public boolean   disabled;
  public boolean   iconrect;
  Viewport         v;
  
  public Chooser(int x, int y, int w, int h, String title, Object... elem)
  {
    super(x, y, w, h);
    this.title = title;
    this.elem = elem;
    showIndex = false;
    alternate = false;
    selected = 0;
    prev = new Button(x, y, h, h, "sq_prev_icon");
    next = new Button(x + w - h, y, h, h, "sq_next_icon");
    prev.hovermod = 0;
    prev.soundMOVER = false;
    next.hovermod = 0;
    prev.clickmod = 0;
    next.clickmod = 0;
    next.soundMOVER = false;
    disabled = false;
  }
  
  public void setSelected(int i)
  {
    selected = i;
  }
  
  public Object getSelected(boolean reset)
  {
    if (selected != -1)
    {
      if (reset)
      {
        if (selected == oldselected)
          return null;
        else
        {
          oldselected = selected;
          return elem[selected];
        }
      }
      return elem[selected];
    }
    else return null;
  }
  
  @Override
  public void update()
  {
    prev.update();
    next.update();
    if (prev.getState() == 1 && selected > 0)
    {
      selected--;
      prev.setState(2);
    }
    if (next.getState() == 1 && selected < elem.length - 1)
    {
      selected++;
      next.setState(2);
    }
    if (selected == 0)
    {
      prev.setState(0);
      prev.disabled = true;
    }
    else
    {
      prev.disabled = false;
    }
    if (selected == elem.length - 1)
    {
      next.setState(0);
      next.disabled = true;
    }
    else
    {
      next.disabled = false;
    }
  }
  
  @Override
  public void draw(Graphics2D g, Viewport v)
  {
    this.v = v;
    if (elem.length == 0)
      disabled = true;
    if (alternate)
    {
      Color c;
      if (focus)
        c = Colors.ORANGE;
      else c = Colors.DGRAY;
      Assistant.Shadow(new RoundRectangle2D.Double(getX(), getY(), getWidth(), getHeight(), 8, 8), c, 0.6f, g);
    }
    else Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), getX(), getY(), getWidth(), getHeight(), g, v.w);
    if (showIndex && elem.length > 0)
    {
      int x = Assistant.drawHorizontallyCenteredString(title + ((selected != -1) ? ((elem[selected] != null) ? elem[selected] : "") : ""), getX(), getWidth(), (int) (getY() + getHeight() / 1.39f), g, (int) (getHeight() / 1.4f), Color.white);
      if (getIcons() != null && getIcons()[selected] != null)
      {
        Image icon = getIcons()[selected];
        g.drawImage(icon, x + g.getFontMetrics(g.getFont().deriveFont(getHeight() / 1.4f)).stringWidth(title + ((selected != -1) ? ((elem[selected] != null) ? elem[selected] != null : "") : "")) - icon.getWidth(null), getY(), v.w);
        if (iconrect)
          Assistant.Rect(x + g.getFontMetrics(g.getFont().deriveFont(getHeight() / 1.4f)).stringWidth(title + ((selected != -1) ? ((elem[selected] != null) ? elem[selected] != null : "") : "")) - icon.getWidth(null), getY(), icon.getWidth(null), icon.getHeight(null), Color.cyan, null, g);
      }
    }
    else
    {
      Assistant.drawHorizontallyCenteredString(title, getX(), getWidth(), (int) (getY() + getHeight() / 1.39f), g, (int) (getHeight() / 1.4f), Color.white);
    }
    prev.draw(g, v);
    next.draw(g, v);
    if (disabled)
    {
      Assistant.Shadow(new RoundRectangle2D.Double(getX(), getY(), getWidth(), getHeight(), 8, 8), Color.black, 0.6f, g);
    }
  }
  
  @Override
  public void mouseMoved(MouseEvent e)
  {
    if (disabled)
      return;
    focus = getArea().contains(e.getLocationOnScreen());
    prev.mouseMoved(e);
    next.mouseMoved(e);
  }
  
  @Override
  public void mouseReleased(MouseEvent e)
  {
    if (disabled)
      return;
    prev.mouseReleased(e);
    next.mouseReleased(e);
  }
  
  @Override
  public void keyPressed(KeyEvent e)
  {
    if (!focus || disabled)
      return;
    if ((e.getExtendedKeyCode() == KeyEvent.VK_LEFT || e.getExtendedKeyCode() == KeyEvent.VK_A) && selected > 0)
    {
      selected--;
      v.playSound("182-Click");
    }
    if ((e.getExtendedKeyCode() == KeyEvent.VK_RIGHT || e.getExtendedKeyCode() == KeyEvent.VK_D) && selected < elem.length - 1)
    {
      selected++;
      v.playSound("182-Click");
    }
  }
  
  public Image[] getIcons()
  {
    return icons;
  }
  
  public void setIcons(Image[] icons)
  {
    this.icons = icons;
  }
}
