package de.dakror.liturfaliar.ui.hud;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.settings.Colors;
import de.dakror.liturfaliar.util.Assistant;

public class PlayerHotbar extends HUDComponent
{
  public static final int[] KEYSLOTS   = { KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3, KeyEvent.VK_4, KeyEvent.VK_Q, KeyEvent.VK_E, KeyEvent.VK_R, KeyEvent.VK_H };
  public static final int[] MOUSESLOTS = { MouseEvent.BUTTON1, MouseEvent.BUTTON3 };
  
  public static final int   SLOTCOUNT  = KEYSLOTS.length + MOUSESLOTS.length;
  
  public static final int   SLOTSIZE   = 55;
  
  BufferedImage             bg;
  
  public PlayerHotbar()
  {
    super(0, 0, SLOTSIZE * SLOTCOUNT, SLOTSIZE, 10);
  }
  
  @Override
  public void update(Map m)
  {}
  
  @Override
  public void draw(Graphics2D g, Viewport v, Map m)
  {
    if (!visible)
    {
      bg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2 = (Graphics2D) bg.getGraphics();
      for (int i = 0; i < SLOTCOUNT; i++)
      {
        g2.drawImage(Viewport.loadImage("tileset/Wood.png"), i * SLOTSIZE, 0, SLOTSIZE, SLOTSIZE, null);
        Font font = new Font("Arial", Font.BOLD, 18);
        g2.setFont(font);
        String string = (i < KEYSLOTS.length) ? KeyEvent.getKeyText(KEYSLOTS[i]) : ((i == SLOTCOUNT - 2) ? "LM" : "RM");
        int width = g2.getFontMetrics().getAscent();
        int height = width;
        if (g2.getFontMetrics().stringWidth(string) > width)
        {
          height = g2.getFontMetrics().getAscent();
          width = g2.getFontMetrics().stringWidth(string);
        }
        
        Assistant.Shadow(new RoundRectangle2D.Double(i * SLOTSIZE, SLOTSIZE - height, width, height, 5, 5), Colors.DGRAY, 0.6f, g2);
        Assistant.drawHorizontallyCenteredString(string, i * SLOTSIZE, width, SLOTSIZE - 2, g2, font.getSize() - 2, Colors.GRAY);// .drawString(string, (i + 1) * SLOTSIZE - width, SLOTSIZE, g2, Color.GRAY.brighter(), font);
      }
      
      setX(v.w.getWidth() / 2 - width / 2);
      setY(v.w.getHeight() - SLOTSIZE - 4);
      visible = true;
    }
    
    g.drawImage(bg, x, y, width, height, v.w);
  }
  
}
