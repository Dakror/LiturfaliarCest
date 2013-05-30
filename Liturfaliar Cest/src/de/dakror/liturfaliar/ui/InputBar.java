package de.dakror.liturfaliar.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.settings.Colors;
import de.dakror.liturfaliar.util.Assistant;

/**
 * Creates an editable Inputfield.
 * 
 * @author Dakror
 */
public class InputBar extends Component
{
  private float size, blink;
  public String value, pre, allowed = "öäüÖÄÜßABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890 ", tileset;
  public boolean focus, hover, disabled, maxCharsReached, centered, alternate;
  public Color   c;
  public int     max;
  public Tooltip tooltip;
  Notification   info;
  
  /**
   * @param x - X coordinate.
   * @param y - Y coordinate.
   * @param width - Maximum width of the Field.
   * @param size - Font size.
   * @param value - Predefined field value.
   * @param c - Font color.
   */
  public InputBar(int x, int y, int width, float size, String value, Color c)
  {
    super(x, y, width, 1);
    this.size = size;
    this.focus = false;
    maxCharsReached = false;
    this.blink = 0.0f;
    this.centered = false;
    if (value == null)
      this.value = "";
    else
    {
      this.value = value;
      this.pre = value;
    }
    if (c == null)
      this.c = Color.white;
    else this.c = c;
    this.tileset = "tileset/Wood.png";
  }
  
  /**
   * Draws the Inputfield on the given Window.
   * 
   * @param g Graphics2D from {@link Viewport}.
   */
  public void draw(Graphics2D g, Viewport v)
  {
    this.blink += 0.25f;
    Font old = g.getFont();
    g.setFont(old.deriveFont(size));
    if (alternate)
      this.setHeight((int) (this.size * 1.425f));
    else this.setHeight(g.getFontMetrics().getHeight() + 32);
    if (g.getFontMetrics().stringWidth(this.value) >= this.getWidth() || (max != 0 && this.value.length() > max))
    {
      this.maxCharsReached = true;
      this.value = this.value.substring(0, this.value.length() - 1);
    }
    else this.maxCharsReached = false;
    if (this.alternate)
    {
      Color c = Color.white;
      if (this.hover && !this.focus)
        c = Colors.ORANGE;
      else c = Colors.DGRAY;
      Assistant.Shadow(new RoundRectangle2D.Double(getX(), getY(), getWidth(), g.getFontMetrics().getHeight(), 8, 8), c, 0.6f, g);
    }
    else
    {
      Assistant.stretchTileset(Viewport.loadImage(this.tileset), this.getX() - 16, this.getY() - 16, this.getWidth() + 32, g.getFontMetrics().getHeight() + 32, g, v.w);
    }
    // text
    if (!this.centered)
    {
      Assistant.drawString(this.value, this.getX(), this.getY() + (int) this.size, g, this.c);
      if (this.focus && !this.maxCharsReached)
      {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (0.5f * Math.cos(this.blink + 0.5f * Math.PI) + 0.5f)));
        Assistant.Rect(this.getX() + g.getFontMetrics().stringWidth(this.value), this.getY() + (int) this.size, g.getFontMetrics().stringWidth("_"), 3, this.c, this.c, g);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
      }
    }
    else
    {
      int x = Assistant.drawHorizontallyCenteredString(this.value, this.getX(), this.getWidth(), this.getY() + (int) this.size, g, (int) this.size, this.c);
      if (this.focus && !this.maxCharsReached)
      {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (0.5f * Math.cos(this.blink + 0.5f * Math.PI) + 0.5f)));
        Assistant.Rect(x, this.getY() + (int) this.size, g.getFontMetrics().stringWidth("_"), 3, this.c, this.c, g);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
      }
    }
    g.setFont(old);
    if (this.alternate)
    {
      if (this.disabled)
        Assistant.Shadow(new RoundRectangle2D.Double(getX(), getY(), getWidth(), getHeight(), 8, 8), Color.black, 0.6f, g);
    }
    else
    {
      if (this.disabled)
        Assistant.Shadow(getArea(), Color.black, 0.6f, g);
    }
    if (this.info != null)
      this.info.draw(g, v.w);
    if (this.tooltip != null)
      this.tooltip.draw(g, v);
  }
  
  public void mouseReleased(MouseEvent e)
  {
    if (this.disabled)
      return;
    this.focus = new Area(new Rectangle2D.Double(this.getX(), this.getY(), this.getWidth(), this.getHeight())).contains(e.getLocationOnScreen());
    if (this.focus && this.value == this.pre)
    {
      this.value = "";
    }
  }
  
  public void mouseMoved(MouseEvent e)
  {
    if (this.disabled)
      return;
    this.hover = new Area(new Rectangle2D.Double(this.getX(), this.getY(), this.getWidth(), this.getHeight())).contains(e.getLocationOnScreen());
    if (this.tooltip != null)
      this.tooltip.mouseMoved(e);
  }
  
  public void keyPressed(KeyEvent e)
  {
    if (!this.focus || this.disabled)
      return;
    int key = e.getExtendedKeyCode();
    if (e.isControlDown())
    {
      if (key == 0x56)
      {
        try
        {
          this.value = "";
          String clipboard = ((String) Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null).getTransferData(DataFlavor.stringFlavor));
          for (int i = 0; i < clipboard.length(); i++)
          {
            if (this.allowed.indexOf(clipboard.charAt(i)) > -1)
              this.value += clipboard.charAt(i);
          }
        }
        catch (Exception e1)
        {
          e1.printStackTrace();
        }
      }
      else if (key == 0x43)
      {
        StringSelection stringSelection = new StringSelection(this.value);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
        Viewport.notification = new Notification("Text in die Zwischenablage kopiert.", Notification.DEFAULT);
      }
    }
    if (e.isControlDown())
      return;
    if (this.value.length() > 0 && key == 0x8)
    {
      this.value = this.value.substring(0, this.value.length() - 1);
    }
    else if (key == 0x20)
    {
      if (!this.maxCharsReached)
      {
        if (this.allowed != null && this.allowed.indexOf(" ") == -1)
          return;
        this.value = this.value + " ";
      }
    }
    else
    {
      if (!this.maxCharsReached)
      {
        if (this.allowed != null && this.allowed.indexOf(e.getKeyChar()) == -1)
          return;
        this.value = this.value + e.getKeyChar();
      }
    }
  }
  
  @Override
  public void update()
  {}
}
