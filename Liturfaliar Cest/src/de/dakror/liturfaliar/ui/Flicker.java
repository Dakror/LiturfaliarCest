package de.dakror.liturfaliar.ui;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
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
  int                    FselectedIndex = -1;
  int                    dragX;
  int                    dragInit;
  int                    s              = (height - 36);
  int                    space          = 10;
  int                    scrollSpeed    = 5;
  long                   press;
  Point                  Ppress;
  
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
    if (press != 0 && FselectedIndex > -1)
    {
      if (FselectedIndex != selectedIndex)
      {
        int ix = (int) (FselectedIndex * (s + space) + space + width / 2.0 - ((objects.size() * (s + space)) / 2.0) - dragX) - space / 2;
        int left = (ix + this.s / 2) - (width / 2);
        dragX += ((FselectedIndex > selectedIndex) ? 1 : -1) * ((Math.abs(left) > scrollSpeed) ? scrollSpeed : Math.abs(left));
      }
      else FselectedIndex = -1;
    }
    
    Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), x + width / 2 - s / 2 - 11, y + height / 2 - s / 2 - 11, s + 22, s + 22, g, v.w);
    Shape oldClip = g.getClip();
    g.setClip(x + 10, y, width - 20, height);
    for (int i = 0; i < objects.size(); i++)
    {
      
      int ix = (int) (x + i * (s + space) + space + width / 2.0 - ((objects.size() * (s + space)) / 2.0) - dragX) - space / 2;
      
      int iy = y + height / 2 - s / 2;
      int s = this.s;
      
      if (Math.abs((ix + this.s / 2) - (x + width / 2)) < 1)
      {
        // CFG.b("sel", i);
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
    if (!getArea().contains(e.getLocationOnScreen()))
      return;
    
    if (e.getButton() == 1 && e.getWhen() - press < 200 && e.getLocationOnScreen().distance(Ppress) < 10)
    {
      int index = (int) ((e.getXOnScreen() - x) - (space + width / 2.0 - ((objects.size() * (s + space)) / 2.0) - dragX)) / (s + 13);
      if (index > -1 && index < objects.size())
        FselectedIndex = index;
      
      else FselectedIndex = -1;
    }
    else if (e.getButton() == 1)
    {
      dragInit = 0;
      int modolo = (dragX + (objects.size() - 1) * (s + space) / 2) % (s + space);
      if (modolo < (s + space) / 2)
        dragX -= modolo;
      
      else dragX += (s + space) - modolo;
    }
  }
  
  @Override
  public void mousePressed(MouseEvent e)
  {
    if (e.getButton() != 1)
      return;
    
    press = e.getWhen();
    Ppress = e.getLocationOnScreen();
  }
  
  @Override
  public void mouseDragged(MouseEvent e)
  {
    if (dragInit == 0)
      dragInit = e.getXOnScreen() - (x + 13) + dragX;
    
    else
    {
      int d = e.getXOnScreen() - (x + 13) - dragInit;
      
      int fx = (int) (space + width / 2.0 - ((objects.size() * (s + space)) / 2.0) - d) - (s + space) / 2;
      int lx = (int) ((objects.size() - 1) * (s + space) + space + width / 2.0 - ((objects.size() * (s + space)) / 2.0) - d) + (s - space) / 2;
      if (fx + s / 2 < width / 2 && lx + s / 2 > width / 2)
        dragX = -d;
    }
  }
}
