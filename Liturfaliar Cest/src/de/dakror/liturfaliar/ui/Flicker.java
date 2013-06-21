package de.dakror.liturfaliar.ui;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.settings.Colors;
import de.dakror.liturfaliar.util.Assistant;

public class Flicker extends Component
{
  public static class FlickObject
  {
    String key;
    int    ix, iy;
    Image  image;
    
    public FlickObject(int ix, int iy, int size, String key)
    {
      this.ix = ix;
      this.iy = iy;
      this.key = key;
      image = ((BufferedImage) Viewport.loadImage("system/icons.png")).getSubimage(ix * 24, iy * 24, 24, 24).getScaledInstance(size, size, BufferedImage.SCALE_REPLICATE);
    }
    
    public Image getIcon()
    {
      return image;
    }
    
    public String getKey()
    {
      return key;
    }
  }
  
  ArrayList<FlickObject> objects;
  
  int                    selectedIndex;
  int                    FselectedIndex;
  int                    dragX;
  int                    dragInit;
  int                    s     = (height - 36);
  int                    space = 14;
  long                   press;
  
  public Flicker(int x, int y, int w, int h, FlickObject... o)
  {
    super(x, y, w, h);
    objects = new ArrayList<FlickObject>(Arrays.asList(o));
  }
  
  @Override
  public void update()
  {}
  
  @Override
  public void draw(Graphics2D g, Viewport v)
  {
    if (press != 0 && FselectedIndex != selectedIndex)
    {
      if (FselectedIndex < selectedIndex)
        dragX -= 10;
      else if (FselectedIndex > selectedIndex)
        dragX += 10;
    }
    Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), x, y, width, height, g, v.w);
    Shape oldClip = g.getClip();
    g.setClip(x + 10, y, width - 20, height);
    for (int i = 0; i < objects.size(); i++)
    {
      
      int ix = (int) (x + i * (s + space) + space + width / 2.0 - ((objects.size() * (s + space)) / 2.0) - dragX);
      
      int iy = y + height / 2 - s / 2;
      int s = this.s;
      double range = 2;
      
      if (Math.abs((ix + this.s / 2) - (x + width / 2)) < this.s * range)
      {
        double dif = Math.abs((ix + this.s / 2) - (x + width / 2)) / (this.s * range);
        s = this.s + (int) ((20) * (1 - dif));
        
        if (s > this.s + 18)
          selectedIndex = i;
      }
      
      Assistant.Shadow(new RoundRectangle2D.Double(ix - (s - this.s) / 2, iy - (s - this.s) / 2, s, s, 16, 16), Colors.DGRAY, 0.7f, g);
      g.drawImage(objects.get(i).getIcon(), ix - (s - this.s) / 2, iy - (s - this.s) / 2, s, s, v.w);
    }
    g.setClip(oldClip);
  }
  
  @Override
  public void mouseReleased(MouseEvent e)
  {
    if (e.getButton() == 1 && e.getWhen() - press < 200)
    {
      int index = (int) ((e.getXOnScreen() - x) - (space + width / 2.0 - ((objects.size() * (s + space)) / 2.0) - dragX)) / (s + 13);
      if (index > -1 && index < objects.size())
        FselectedIndex = index;
    }
    else dragInit = 0;
  }
  
  @Override
  public void mousePressed(MouseEvent e)
  {
    if (e.getButton() != 1)
      return;
    
    press = e.getWhen();
  }
  
  @Override
  public void mouseDragged(MouseEvent e)
  {
    if (dragInit == 0)
      dragInit = e.getXOnScreen() - (x + 13) - dragX;
    
    else
    {
      int d = e.getXOnScreen() - (x + 13) - dragInit;
      
      int fx = (int) (space + width / 2.0 - ((objects.size() * (s + space)) / 2.0) - d);
      int lx = (int) ((objects.size() - 1) * (s + space) + space + width / 2.0 - ((objects.size() * (s + space)) / 2.0) - d);
      if (fx + s / 2 < width / 2 && lx + s / 2 > width / 2)
        dragX = d;
    }
  }
}
