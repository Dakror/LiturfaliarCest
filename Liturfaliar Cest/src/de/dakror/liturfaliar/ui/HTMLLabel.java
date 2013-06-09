package de.dakror.liturfaliar.ui;

import java.awt.Graphics2D;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.Database;

public class HTMLLabel extends Component
{
  HTMLString[] lines;
  String       raw;
  
  boolean      update;
  
  public HTMLLabel(int x, int y, int w, int h, String raw)
  {
    super(x, y, w, h);
    this.raw = Database.filterString(raw);
    update = true;
  }
  
  @Override
  public void update()
  {}
  
  @Override
  public void draw(Graphics2D g, Viewport v)
  {
    if (update)
    {
      raw = Database.filterString(raw);
      lines = HTMLString.decodeString(raw, getWidth(), g);
      update = false;
    }
    g.setClip(getX(), getY(), getWidth(), getHeight());
    for (int i = 0; i < lines.length; i++)
    {
      if (i > 0)
      {
        Assistant.drawString(lines[i].string, getX() + ((!lines[i - 1].br) ? lines[i - 1].getWidth(g) : 0), getY() + getHeightOfPreviousRows(i + 1, g), g, lines[i].c, g.getFont().deriveFont(lines[i].style, (int) lines[i].size));
      }
      else Assistant.drawString(lines[i].string, getX(), getY() + getHeightOfPreviousRows(i + 1, g), g, lines[i].c, g.getFont().deriveFont(lines[i].style, (int) lines[i].size));
    }
    g.setClip(null);
  }
  
  public void doUpdate(String newraw)
  {
    if (newraw != null)
      raw = newraw;
    
    update = true;
  }
  
  private int getHeightOfPreviousRows(int index, Graphics2D g)
  {
    int height = 0;
    for (int i = 0; i < index; i++)
    {
      if (lines[(i > 0) ? i - 1 : 0].br)
        height += lines[i].getHeight(g);
    }
    if (!lines[0].br)
      height += lines[0].getHeight(g);
    
    return height;
  }
}
