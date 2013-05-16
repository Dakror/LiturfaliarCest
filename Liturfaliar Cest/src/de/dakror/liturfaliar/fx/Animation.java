package de.dakror.liturfaliar.fx;

import java.awt.Graphics2D;
import java.awt.Image;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.map.Map;

public class Animation
{
  int            x;
  int            y;
  int            w;
  int            h;
  int            s;
  Image          file;
  final int      cell  = 192;
  int            frames;
  public boolean done;
  public String  sound = "";
  int            speed;
  int            start = 0;
  public boolean onTop;
  
  public Animation(int x, int y, int s, int speed, boolean layer, String file)
  {
    this.x = x;
    this.y = y;
    this.s = s;
    this.file = Viewport.loadImage("Animations/" + this.file + ".png");
    w = this.file.getWidth(null);
    h = this.file.getHeight(null);
    frames = (int) ((w * h) / Math.pow(cell, 2));
    done = false;
    onTop = layer;
    this.speed = speed;
  }
  
  public boolean draw(Graphics2D g, Viewport v, Map m)
  {
    if (start == 0)
      start = v.getFrame(speed);
    int frame = v.getFrame(speed) - start;
    if (frame == frames)
      done = true;
    if (done)
      return false;
    g.drawImage(file, x + ((m != null) ? m.getX() : 0), y + ((m != null) ? m.getY() : 0), x + s + ((m != null) ? m.getX() : 0), y + s + ((m != null) ? m.getY() : 0), (frame % frames) * cell, (frame / frames) * cell, (frame % frames) * cell + cell, (frame / frames) * cell + cell, v.w);
    return true;
  }
}
