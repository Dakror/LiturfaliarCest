package de.dakror.liturfaliar.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.util.Assistant;

public class HelpOverlayContainer extends Component
{
  public int     tx, ty;
  private String t;
  
  public HelpOverlayContainer(int x, int y, int w, int h, String t)
  {
    super(x, y, w, h);
    this.t = t;
  }
  
  public HelpOverlayContainer(int x, int y, int tx, int ty, int w, int h, String t)
  {
    super(x, y, w, h);
    this.tx = tx;
    this.ty = ty;
    this.t = t;
  }
  
  public void draw(Graphics2D g)
  {
    Assistant.stretchTileset(Viewport.loadImage("tileset/HelpOverlay.png"), getX(), getY(), getWidth(), getHeight(), g);
    String[] lines = Assistant.wrap(this.t, (getWidth() - this.tx) / g.getFontMetrics(g.getFont().deriveFont(22.0f)).stringWidth("S")).split("\n");
    if (this.tx == 0 && this.ty == 0)
    {
      Assistant.Shadow(new RoundRectangle2D.Double(getX(), getY() - 20 * lines.length - 2, g.getFontMetrics(g.getFont().deriveFont(22.0f)).stringWidth(lines[0]) + 8, lines.length * 24, 8, 8), Color.black, 0.6f, g);
      for (int i = 0; i < lines.length; i++)
      {
        Assistant.drawHorizontallyCenteredString(lines[i], getX(), getWidth(), getY() + 22 - 22 * (lines.length - i), g, 22, Color.white);
      }
    }
    else
    {
      Assistant.Shadow(new RoundRectangle2D.Double(getX() + this.tx - 4, getY() + this.ty - 20 * lines.length - 2, g.getFontMetrics(g.getFont().deriveFont(22.0f)).stringWidth(lines[0]) + 8, lines.length * 24, 8, 8), Color.black, 0.6f, g);
      for (int i = 0; i < lines.length; i++)
      {
        Assistant.drawString(lines[i], getX() + this.tx, getY() + this.ty + 22 - 22 * (lines.length - i), g, Color.white, g.getFont().deriveFont(22.0f));
      }
    }
  }
  
  @Override
  public void update()
  {}
}
