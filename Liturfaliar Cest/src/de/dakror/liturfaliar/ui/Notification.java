package de.dakror.liturfaliar.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.util.Assistant;

public class Notification
{
  public final static String ERROR   = null; // "system/Error.png";
  public final static String DEFAULT = null;
  public final static String WARNING = null; // "system/Info.png";
  String                     info;
  String                     type;
  float                      cos;
  public boolean             finished;
  long                       timewait;
  BufferedImage              render;
  int                        lineCount;
  
  public Notification(String i, String t)
  {
    info = i;
    type = t;
    cos = 0;
    timewait = System.currentTimeMillis();
    finished = false;
  }
  
  public void draw(Graphics2D g, Window w)
  {
    if (render == null)
    {
      Font old = g.getFont();
      g.setFont(old.deriveFont(28.0f));
      int width = w.getWidth() / 3;
      List<String> lines = Arrays.asList(Assistant.wrap(this.info, width / g.getFontMetrics().stringWidth("S")).split("\n\n"));
      lineCount = lines.size();
      render = new BufferedImage(width, (int) (g.getFontMetrics().getHeight() * lines.size()) + 16, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2 = Assistant.copyGraphics2DAttributes(g, (Graphics2D) render.getGraphics());
      g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
      g2.setColor(Color.decode("#222222"));
      g2.fill(new RoundRectangle2D.Double(0, 0, width, render.getHeight(), 8, 8));
      g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
      for (int i = 0; i < lines.size(); i++)
      {
        Assistant.drawCenteredString(lines.get(i), 0, width, (int) (g.getFontMetrics().getHeight() * (i + 1)), g2, 27, Color.white);
      }
      g.setFont(old);
    }
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (0.5f * Math.sin(this.cos + 0.5f * Math.PI) + 0.5f)));
    g.drawImage(render, w.getWidth() / 3, 150, w);
    if (type != null)
    {
      g.drawImage(Viewport.loadImage(type), w.getWidth() / 3 - 32, 168, 64, 64, w);
    }
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    if (System.currentTimeMillis() - timewait > 1000 * lineCount)
    {
      if (cos < 3)
      {
        cos += 0.10f;
      }
      else this.finished = true;
    }
  }
}
