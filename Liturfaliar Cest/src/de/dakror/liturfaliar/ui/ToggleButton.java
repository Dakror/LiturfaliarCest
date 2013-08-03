package de.dakror.liturfaliar.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

import de.dakror.liturfaliar.Viewport;

public class ToggleButton extends Button
{
  boolean checked;
  
  public ToggleButton(int x, int y, int w, int h)
  {
    super(x, y, w, h, "toggle_icon");
    this.hovermod = 0;
    this.clickmod = 0;
    this.soundMOVER = false;
  }
  
  public void draw(Graphics2D g, Viewport v)
  {
    this.handle.update(v);
    if (this.checked)
    {
      g.drawImage(Viewport.loadImage("icon/white/" + icon + ".png"), x, y, width, height, v.w);
    }
    else
    {
      g.drawImage(Viewport.loadImage("icon/black/" + icon + ".png"), x, y, width, height, v.w);
    }
    if (this.disabled)
    {
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
      g.setColor(Color.black);
      g.fill(new RoundRectangle2D.Double(x, y, width, height, 8, 8));
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }
  }
  
  @Override
  public int getState()
  {
    if (this.checked) return 1;
    else return 0;
  }
  
  public void update()
  {
    super.update();
    if (this.handle.state == 1)
    {
      this.checked = !this.checked;
      this.handle.state = 0;
    }
  }
}
