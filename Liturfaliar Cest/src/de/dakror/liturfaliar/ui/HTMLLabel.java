package de.dakror.liturfaliar.ui;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.Database;

public class HTMLLabel extends Component
{
  HTMLString[] lines;
  String       raw;
  
  public HTMLLabel(int x, int y, int w, int h, String raw)
  {
    super(x, y, w, h);
    this.raw = Database.filterString(raw);
  }
  
  @Override
  public void update()
  {}
  
  @Override
  public void draw(Graphics2D g, Viewport v)
  {
    if (this.lines == null)
      this.lines = HTMLString.decodeString(this.raw, getWidth(), g);
    g.setClip(getX(), getY(), getWidth(), getHeight());
    for (int i = 0; i < this.lines.length; i++)
    {
      if (i > 0)
      {
        Assistant.drawString(this.lines[i].string, getX() + ((!this.lines[i - 1].br) ? this.lines[i - 1].getWidth(g) : 0), getY() + getHeightOfPreviousRows(i + 1, g), g, this.lines[i].c, g.getFont().deriveFont(this.lines[i].style, (int) this.lines[i].size));
      }
      else Assistant.drawString(this.lines[i].string, getX(), getY() + getHeightOfPreviousRows(i + 1, g), g, this.lines[i].c, g.getFont().deriveFont(this.lines[i].style, (int) this.lines[i].size));
    }
    g.setClip(null);
  }
  
  private int getHeightOfPreviousRows(int index, Graphics2D g)
  {
    int height = 0;
    for (int i = 0; i < index; i++)
    {
      if (this.lines[(i > 0) ? i - 1 : 0].br)
        height += this.lines[i].getHeight(g);
    }
    return height;
  }
  
  @Override
  public void mouseWheelMoved(MouseWheelEvent e)
  {}
  
  @Override
  public void mouseDragged(MouseEvent e)
  {}
  
  @Override
  public void mouseMoved(MouseEvent e)
  {}
  
  @Override
  public void mouseClicked(MouseEvent e)
  {}
  
  @Override
  public void mousePressed(MouseEvent e)
  {}
  
  @Override
  public void mouseReleased(MouseEvent e)
  {}
  
  @Override
  public void mouseEntered(MouseEvent e)
  {}
  
  @Override
  public void mouseExited(MouseEvent e)
  {}
  
  @Override
  public void keyTyped(KeyEvent e)
  {}
  
  @Override
  public void keyPressed(KeyEvent e)
  {}
  
  @Override
  public void keyReleased(KeyEvent e)
  {}
}
