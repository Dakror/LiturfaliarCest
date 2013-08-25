package de.dakror.liturfaliar.ui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.dakror.liturfaliar.Viewport;

public class Icon
{
  int           x, y, width, height;
  BufferedImage image;
  
  public Icon(int x, int y, int width, int height, int dx, int dy)
  {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    image = ((BufferedImage) Viewport.loadImage("system/icons.png")).getSubimage(dx * 24, dy * 24, 24, 24);
  }
  
  public void draw(Graphics2D g)
  {
    g.drawImage(image, x, y, width, height, Viewport.w);
  }
}
