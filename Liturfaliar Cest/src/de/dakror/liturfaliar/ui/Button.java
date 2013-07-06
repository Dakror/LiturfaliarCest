package de.dakror.liturfaliar.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.settings.Colors;
import de.dakror.liturfaliar.util.Assistant;

/**
 * Describes the button object.
 * 
 * @author Dakror
 */
public class Button extends Component
{
  boolean              Bicon;
  public boolean       centered;
  public boolean       disabled;
  public boolean       round      = true;
  public boolean       soundCLICK = true;
  public boolean       soundMOVER = true;
  public boolean       imagelower = false;
  public int           iw;
  public int           ih;
  public BufferedImage image;
  public Color         c;
  public float         size;
  protected float      alpha      = 0.6f;
  HandleArea           handle;
  public int           clickmod   = 16;
  public int           hovermod   = 16;
  String               icon;
  public String        tileset;
  public String        title;
  public Tooltip       tooltip;
  
  public Button(int x, int y, int w, String title, Color c, float size)
  {
    super(x, y, w, 1);
    this.title = title;
    this.c = c;
    this.size = size;
    handle = new HandleArea(x, y, w, 1);
    tileset = "Wood";
    centered = true;
  }
  
  public Button(int x, int y, int w, int h, String icon)
  {
    super(x, y, w, h);
    tileset = null;
    Bicon = true;
    this.icon = icon;
    handle = new HandleArea(x, y, w, h);
  }
  
  public Button(int x, int y, int w, int h, Image i)
  {
    this(x, y, w, h, 0, 0, i.getWidth(null), i.getHeight(null), i);
  }
  
  public Button(int x, int y, int w, int h, int sx, int sy, int dx, int dy, Image i)
  {
    super(x, y, w, h);
    if (w + h > 0)
    {
      image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
      image.getGraphics().drawImage(i, 0, 0, w, h, sx, sy, dx, dy, null);
    }
    handle = new HandleArea(x, y, w, h);
  }
  
  @Override
  public void setWidth(int w)
  {
    super.setWidth(w);
    if (handle != null)
      handle.setWidth(w);
  }
  
  @Override
  public void setHeight(int h)
  {
    super.setHeight(h);
    if (handle != null)
      handle.setHeight(h);
  }
  
  public int getState()
  {
    return handle.state;
  }
  
  public void setState(int state)
  {
    handle.state = state;
  }
  
  @Override
  public void update()
  {
    handle.soundMOVER = soundMOVER;
    handle.soundCLICK = soundCLICK;
    if (getHeight() == 1)
    {
      setHeight((int) (size * 1.3f + ((tileset != null) ? 32 : 0)));
      handle.setHeight(getHeight());
    }
    handle.setX(getX());
    handle.setY(getY());
    if (disabled)
      handle.state = 0;
  }
  
  @Override
  public void draw(Graphics2D g, Viewport v)
  {
    handle.update(v);
    Font oldFont = g.getFont();
    Color oldColor = g.getColor();
    int x = getX();
    int y = getY();
    int w = getWidth();
    int h = getHeight();
    float size = this.size;
    if (handle.state == 2)
    {
      x -= hovermod;
      y -= hovermod;
      w += hovermod * 2;
      h += hovermod * 2;
      size += hovermod;
    }
    if (handle.state == 1)
    {
      x -= clickmod;
      y -= clickmod;
      w += clickmod * 2;
      h += clickmod * 2;
      size += clickmod;
    }
    g.setFont(oldFont.deriveFont(size));
    g.setColor(c);
    if (image != null && imagelower)
    {
      if (disabled)
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
      if (tileset != null)
        Assistant.stretchTileset(Viewport.loadImage("tileset/" + tileset + ".png"), x, y, w, h, g, v.w);
      if (iw != 0)
      {
        int x1 = (iw != 0) ? -iw / 2 : 0;
        int y1 = (ih != 0) ? -ih / 2 : 0;
        g.drawImage(image, x + x1, y + y1, w + iw, h + ih, v.w);
      }
      else
      {
        g.drawImage(image, x, y, w, h, v.w);
      }
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }
    if (title != null)
    {
      if (tileset == null)
      {
        if (handle.state != 0)
          g.setColor(Colors.ORANGE);
        else g.setColor(Colors.DGRAY);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        if (round)
          g.fill(new RoundRectangle2D.Double(x, y, w, h, 8, 8));
        else g.fill(new Rectangle2D.Double(x, y, w, h));
        if (disabled)
        {
          g.setColor(Colors.DGRAY);
          if (round)
            g.fill(new RoundRectangle2D.Double(x, y, w, h, 8, 8));
          else g.fill(new Rectangle2D.Double(x, y, w, h));
        }
        g.setColor(c);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        if (centered)
        {
          FontMetrics fm = g.getFontMetrics();
          int x1 = (w - fm.stringWidth(title)) / 2;
          g.drawString(title, x + x1, y + size);
        }
        else
        {
          g.drawString(title, x, y + size);
        }
      }
      else
      {
        Assistant.stretchTileset(Viewport.loadImage("tileset/" + tileset + ".png"), x, y, w, h, g, v.w);
        FontMetrics fm = g.getFontMetrics();
        int y1 = (fm.getAscent() + (h - (fm.getAscent() + fm.getDescent())) / 2) - 40 / 2;
        if (centered)
        {
          int x1 = (w - fm.stringWidth(title)) / 2;
          g.drawString(title, x + x1, y + y1 + 16);
        }
        else
        {
          g.drawString(title, x, y + y1);
        }
        if (disabled)
          Assistant.Shadow(new Rectangle2D.Double(x, y, w, h), Color.black, 0.6f, g);
      }
    }
    else if (Bicon == true)
    {
      if (tileset != null && tileset.length() > 0)
        Assistant.stretchTileset(Viewport.loadImage("tileset/" + tileset + ".png"), x, y, w, h, g, v.w);
      if (handle.state == 2)
      {
        g.drawImage(Viewport.loadImage("icon/white/" + icon + ".png"), x + ((iw != 0) ? (w / 2 - iw / 2) : 0), y + ((ih != 0) ? (h / 2 - ih / 2) : 0), (iw != 0) ? iw : w, (ih != 0) ? ih : h, v.w);
      }
      else
      {
        g.drawImage(Viewport.loadImage("icon/black/" + icon + ".png"), x + ((iw != 0) ? (w / 2 - iw / 2) : 0), y + ((ih != 0) ? (h / 2 - ih / 2) : 0), (iw != 0) ? iw : w, (ih != 0) ? ih : h, v.w);
      }
      if (disabled)
        Assistant.Shadow(new RoundRectangle2D.Double(x, y, w, h, 8, 8), Color.black, 0.6f, g);
    }
    else
    {
      if (tileset != null && tileset.length() > 0)
        Assistant.stretchTileset(Viewport.loadImage("tileset/" + tileset + ".png"), x, y, w, h, g, v.w);
    }
    if (image != null && !imagelower)
    {
      if (disabled)
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
      if (tileset != null)
        Assistant.stretchTileset(Viewport.loadImage("tileset/" + tileset + ".png"), x, y, w, h, g, v.w);
      if (iw != 0)
      {
        int x1 = (iw != 0) ? -iw / 2 : 0;
        int y1 = (ih != 0) ? -ih / 2 : 0;
        g.drawImage(image, x + x1, y + y1, w + iw, h + ih, v.w);
      }
      else
      {
        g.drawImage(image.getScaledInstance(w, h, BufferedImage.SCALE_REPLICATE), x, y, w, h, v.w);
      }
    }
    g.setFont(oldFont);
    g.setColor(oldColor);
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    handle.draw(g, v);
    if (tooltip != null)
      tooltip.draw(g, v);
  }
  
  public Image getIcon()
  {
    return Viewport.loadImage("icon/black/" + icon + ".png");
  }
  
  @Override
  public void mouseMoved(MouseEvent e)
  {
    if (disabled)
    {
      return;
    }
    if (tooltip != null)
    {
      tooltip.mouseMoved(e);
    }
    handle.mouseMoved(e);
  }
  
  @Override
  public void mousePressed(MouseEvent e)
  {
    if (!disabled)
      handle.mousePressed(e);
  }
  
  @Override
  public void mouseReleased(MouseEvent e)
  {
    HelpOverlayClicked(e, (title != null) ? title : "button");
    if (!disabled)
      handle.mouseReleased(e);
  }
}
