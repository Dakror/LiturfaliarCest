package de.dakror.liturfaliar.ui;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.Database;

public class Tooltip extends Component
{
  Component      parent;
  HTMLString[]   text;
  String         rawText;
  public boolean follow;
  public boolean visible;
  public String  tileset = "Tooltip";
  
  public Tooltip(String raw, Component parent)
  {
    super(parent.getX(), parent.getY(), 1, 1);
    this.parent = parent;
    this.visible = false;
    this.rawText = raw;
    this.text = HTMLString.decodeString(Database.filterString(raw));
  }
  
  public void mouseMoved(MouseEvent e)
  {
    if (this.parent.getArea().contains(e.getLocationOnScreen()))
    {
      this.visible = true;
      if (this.follow)
      {
        setX(e.getXOnScreen());
        setY(e.getYOnScreen());
      }
    }
    else
    {
      this.visible = false;
    }
  }
  
  public void draw(Graphics2D g, Viewport v)
  {
    if (getWidth() < 2)
    {
      int mostwidth = 1;
      for (int i = 0; i < this.text.length; i++)
      {
        int width = getTotalLineWidth(i, g);
        if (width > mostwidth)
          mostwidth = width;
      }
      setWidth(mostwidth + 32);
    }
    setHeight(getHeightOfPreviousRows(this.text.length, g) + 32);
    if (this.visible)
    {
      if (this.tileset != null)
        Assistant.stretchTileset(Viewport.loadImage("tileset/" + this.tileset + ".png"), getX(), getY(), getWidth() + 16 - (getWidth() % 16), getHeight(), g, v.w);
      for (int i = 0; i < this.text.length; i++)
      {
        if (i > 0)
        {
          Assistant.drawString(this.text[i].string, getX() + 16 + ((!this.text[i - 1].br) ? this.text[i - 1].getWidth(g) : 0), getY() + getHeightOfPreviousRows(i + 1, g), g, this.text[i].c, g.getFont().deriveFont(this.text[i].style, (int) this.text[i].size));
        }
        else Assistant.drawString(this.text[i].string, getX() + 16, getY() + getHeightOfPreviousRows(i + 1, g), g, this.text[i].c, g.getFont().deriveFont(this.text[i].style, (int) this.text[i].size));
      }
    }
    this.text = HTMLString.decodeString(Database.filterString(this.rawText));
  }
  
  private int getTotalLineWidth(int firstIndex, Graphics2D g)
  {
    int width = 0;
    for (int i = firstIndex; i < this.text.length; i++)
    {
      width += this.text[i].getWidth(g);
      if (this.text[i].br)
        return width;
    }
    return width;
  }
  
  private int getHeightOfPreviousRows(int index, Graphics2D g)
  {
    int height = 0;
    for (int i = 0; i < index; i++)
    {
      if (this.text[(i > 0) ? i - 1 : 0].br)
        height += this.text[i].getHeight(g);
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
  
  @Override
  public void update()
  {}
}
