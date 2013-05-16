package de.dakror.liturfaliar.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.Database;

public class Label extends Component
{
  Color                 c;
  String                s;
  float                 size;
  int                   w;
  private BufferedImage img;
  
  public Label(int x, int y, String s, float size, Color c)
  {
    super(x, y, 1, (int) (size * 1.425f));
    this.c = c;
    this.s = Database.filterString(s);
    this.size = size;
  }
  
  public Label(int x, int y, int w, String s, float size, Color c)
  {
    super(x, y, 1, (int) (size * 1.425f));
    this.c = c;
    this.s = Database.filterString(s);
    this.size = size;
    this.w = w;
  }
  
  public Label(int x, int y, int w, int h, BufferedImage b)
  {
    super(x, y, w, h);
    this.setImage(b);
  }
  
  @Override
  public void update()
  {}
  
  @Override
  public void draw(Graphics2D g, Viewport v)
  {
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    if (this.s != null)
    {
      if (getWidth() == 1)
      {
        setWidth(g.getFontMetrics(g.getFont().deriveFont(this.size)).stringWidth(this.s));
      }
      if (this.s != null && this.w != 0)
        Assistant.drawCenteredString(this.s, getX(), this.w, (int) (getY() + this.size), g, (int) this.size, this.c);
      else Assistant.drawString(this.s, getX(), (int) (getY() + this.size), g, this.c, g.getFont().deriveFont(this.size));
    }
    else if (this.img != null)
    {
      g.drawImage(this.getImage(), getX(), getY(), getWidth(), getHeight(), v.w);
    }
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
  
  public BufferedImage getImage()
  {
    return img;
  }
  
  public void setImage(BufferedImage img)
  {
    this.img = img;
  }
}
