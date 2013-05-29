package de.dakror.liturfaliar.ui;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.settings.Colors;
import de.dakror.liturfaliar.util.Assistant;

public class CheckBox extends Button
{
  boolean state;
  boolean askedstate;
  
  public CheckBox(int x, int y, int size)
  {
    super(x, y, size, size, 0, 0, 128, 128, Viewport.loadImage("system/check.png"));
    hovermod = 0;
    clickmod = 0;
    soundMOVER = false;
    image = null;
    tileset = "Wood";
    askedstate = false;
  }
  
  public boolean isChecked(boolean reset)
  {
    if (reset)
    {
      if (askedstate == state)
        return false;
      else
      {
        askedstate = state;
        return state;
      }
    }
    return state;
  }
  
  public void setChecked(boolean b)
  {
    state = b;
  }
  
  public void update()
  {
    super.update();
    if (handle.state == 1)
    {
      state = !state;
      handle.state = 2;
    }
    if (!state)
    {
      image = null;
    }
    else
    {
      image = (BufferedImage) Viewport.loadImage("system/check.png");
    }
  }
  
  @Override
  public void draw(Graphics2D g, Viewport v)
  {
    handle.update(v);
    if (tileset != null)
    {
      Assistant.stretchTileset(Viewport.loadImage("tileset/" + tileset + ".png"), getX(), getY(), getWidth(), getHeight(), g, v.w);
      iw = -16;
      ih = -16;
    }
    else
    {
      iw = 0;
      ih = 0;
      if (handle.state > 0)
        g.setColor(Colors.ORANGE);
      else g.setColor(Colors.DGRAY);
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
      if (round)
        g.fill(new RoundRectangle2D.Double(getX(), getY(), getWidth(), getHeight(), 8, 8));
      else g.fill(new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight()));
      if (disabled)
      {
        g.setColor(Colors.DGRAY);
        if (round)
          g.fill(new RoundRectangle2D.Double(getX(), getY(), getWidth(), getHeight(), 8, 8));
        else g.fill(new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight()));
      }
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }
    if (iw != 0 && image != null)
    {
      int x1 = (iw != 0) ? -iw / 2 : 0;
      int y1 = (ih != 0) ? -ih / 2 : 0;
      g.drawImage(image, getX() + x1, getY() + y1, getWidth() + iw, getHeight() + ih, v.w);
    }
    else
    {
      g.drawImage(image, getX(), getY(), getWidth(), getHeight(), v.w);
    }
  }
}
