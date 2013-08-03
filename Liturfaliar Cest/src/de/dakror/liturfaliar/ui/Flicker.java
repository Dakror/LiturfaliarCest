package de.dakror.liturfaliar.ui;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import de.dakror.liturfaliar.Viewport;
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
  
  FlickObject[] objects;
  
  int           selectedIndex;
  int           FselectedIndex = -1;
  int           dragX;
  int           dragInit;
  int           s              = (height - 36);
  int           space          = 12;
  int           scrollSpeed    = 10;
  long          press;
  Point         Ppress;
  
  public Flicker(int x, int y, int w, int h, FlickObject... o)
  {
    super(x, y, w, h);
    objects = o;
    if (objects.length % 2 == 0) dragX = s / 2 + space / 2;
  }
  
  @Override
  public void update()
  {
    if (FselectedIndex > -1)
    {
      if (FselectedIndex != selectedIndex)
      {
        int ix = (int) (FselectedIndex * (s + space) + space + width / 2.0 - ((objects.length * (s + space)) / 2.0) - dragX) - space / 2;
        int left = (ix + this.s / 2) - (width / 2);
        dragX += ((FselectedIndex > selectedIndex) ? 1 : -1) * ((Math.abs(left) > scrollSpeed) ? scrollSpeed : Math.abs(left));
      }
      else FselectedIndex = -1;
    }
  }
  
  @Override
  public void draw(Graphics2D g, Viewport v)
  {
    Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), x, y, width, height, g, v.w);
    Assistant.stretchTileset(Viewport.loadImage("tileset/EmbededWood.png"), x + width / 2 - s / 2 - 11, y, s + 22, height, g, v.w);
    Shape oldClip = g.getClip();
    g.setClip(x + 10, y, width - 20, height);
    for (int i = 0; i < objects.length; i++)
    {
      int ix = (int) (x + i * (s + space) + space + width / 2.0 - ((objects.length * (s + space)) / 2.0) - dragX) - space / 2;
      
      int iy = y + height / 2 - s / 2;
      int s = this.s;
      
      if (Math.abs((ix + this.s / 2) - (x + width / 2)) < 1) selectedIndex = i;
      
      g.drawImage(objects[i].getIcon(), ix - (s - this.s) / 2, iy - (s - this.s) / 2, s, s, v.w);
    }
    g.setClip(oldClip);
    
    Assistant.stretchTileset(Viewport.loadImage("tileset/LFWood.png"), x, y, height, height, g, v.w);
    Assistant.stretchTileset(Viewport.loadImage("tileset/RFWood.png"), x + width - height, y, height, height, g, v.w);
  }
  
  @Override
  public void mouseReleased(MouseEvent e)
  {
    if (!getArea().contains(e.getPoint())) return;
    
    if (e.getButton() == 1 && e.getWhen() - press < 200 && e.getPoint().distance(Ppress) < 10)
    {
      int index = (int) ((e.getX() - x) - (space + width / 2.0 - ((objects.length * (s + space)) / 2.0) - dragX)) / (s + 13);
      if (index > -1 && index < objects.length) FselectedIndex = index;
      
      else FselectedIndex = -1;
    }
    else if (e.getButton() == 1)
    {
      dragInit = 0;
      int modolo = (dragX + (objects.length - 1) * (s + space) / 2) % (s + space);
      if (modolo < (s + space) / 2) dragX -= modolo;
      
      else dragX += (s + space) - modolo;
    }
  }
  
  @Override
  public void mousePressed(MouseEvent e)
  {
    if (e.getButton() != 1) return;
    
    press = e.getWhen();
    Ppress = e.getPoint();
  }
  
  public FlickObject getSelectedObject()
  {
    return objects[selectedIndex];
  }
  
  public int getSelectedIndex()
  {
    return selectedIndex;
  }
  
  public void setSelectedIndex(int i)
  {
    FselectedIndex = i;
  }
}
