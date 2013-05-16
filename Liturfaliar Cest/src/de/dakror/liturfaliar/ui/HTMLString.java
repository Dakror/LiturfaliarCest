package de.dakror.liturfaliar.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Arrays;

import de.dakror.liturfaliar.Viewport;

public class HTMLString
{
  public int     style;
  public float   size;
  public Color   c;
  public String  string;
  public int     w = 0;
  public boolean br;
  
  public HTMLString(String string, float size, Color c, int style)
  {
    this.string = string;
    this.size = size;
    this.c = c;
    this.style = style;
  }
  
  public HTMLString(HTMLString htmls, String string)
  {
    this.string = string;
    size = htmls.size;
    c = htmls.c;
    style = htmls.style;
  }
  
  public int getWidth(Graphics2D g)
  {
    return g.getFontMetrics(new Font(g.getFont().getFontName(), style, (int) size)).stringWidth(string);
  }
  
  public int getHeight(Graphics2D g)
  {
    return g.getFontMetrics(new Font(g.getFont().getFontName(), style, (int) size)).getHeight();
  }
  
  // tepmplate for string: "<#Color;Size;Style>Message"
  // line break: "[br]"
  public static HTMLString[] decodeString(String decodeString)
  {
    String[] tags = decodeString.split("<#");
    ArrayList<HTMLString> strings = new ArrayList<HTMLString>();
    for (int i = 1; i < tags.length; i++)
    {
      if (tags[i].length() == 0)
        continue;
      String[] options = tags[i].substring(0, tags[i].indexOf(">")).split(";");
      String text = tags[i].substring(tags[i].indexOf(">") + 1);
      if (text.indexOf("[br]") > -1)
      {
        String[] lines = text.split("\\[br\\]");
        for (int j = 0; j < lines.length; j++)
        {
          HTMLString string = new HTMLString(lines[j], Float.parseFloat((options[1] != null) ? options[1] : "16"), Color.decode("#" + ((options[0] != null) ? options[0] : "ffffff")), Integer.parseInt((options[2] != null) ? options[2] : "0"));
          if (j == lines.length - 1)
          {
            if (text.lastIndexOf("[br]") == text.length() - new String("[br]").length())
              string.br = true;
            else string.br = false;
          }
          else string.br = true;
          strings.add(string);
        }
      }
      else
      {
        HTMLString string = new HTMLString(text, Float.parseFloat((options[1] != null) ? options[1] : "16"), Color.decode("#" + ((options[0] != null) ? options[0] : "ffffff")), Integer.parseInt((options[2] != null) ? options[2] : "0"));
        strings.add(string);
      }
    }
    return strings.toArray(new HTMLString[] {});
  }
  
  public static HTMLString[] decodeString(String raw, int w, Graphics2D g)
  {
    HTMLString[] lines = decodeString(raw);
    ArrayList<HTMLString> arr = new ArrayList<HTMLString>();
    for (int i = 0; i < lines.length; i++)
    {
      arr.addAll(Arrays.asList(rec_limitline(lines[i], w, g)));
    }
    return arr.toArray(new HTMLString[] {});
  }
  
  private static HTMLString[] rec_limitline(HTMLString l, int w, Graphics2D g)
  {
    if (l.getWidth(g) <= w)
    {
      return new HTMLString[] { l };
    }
    else
    {
      String t = l.string;
      String t2 = t.replaceAll("(.{" + (w / new HTMLString(l, "^").getWidth(g)) + "})(\\s)?", "$1[br]").replaceAll("(\\s{1})(\\S{1,})(\\[br\\])(\\S{1,})", "[br]" + l.getTag() + "$2$4").replaceAll("(\\s{1})(\\[br\\])", "[br]" + l.getTag());
      return decodeString(l.getTag() + t2);
    }
  }
  
  public String getTag()
  {
    return "<#" + Integer.toHexString(c.getRGB()).substring(2) + ";" + size + ";" + style + ">";
  }
  
  public void drawString(int x, int y, Graphics2D g)
  {
    Color oldColor = g.getColor();
    Font oldFont = g.getFont();
    g.setColor(c);
    g.setFont(oldFont.deriveFont(style, size));
    g.drawString(string, x, y);
    g.setFont(oldFont);
    g.setColor(oldColor);
  }
  
  public boolean drawStringAnimated(int x, int y, int speed, Graphics2D g, Viewport v)
  {
    Shape oldClip = g.getClip();
    FontMetrics fm = g.getFontMetrics(g.getFont().deriveFont(style, size));
    int h = fm.getHeight();
    int w = fm.stringWidth(string);
    if (this.w < w)
      this.w += speed;
    g.setClip(x, y - fm.getLeading() - fm.getMaxAscent(), this.w, h);
    drawString(x, y, g);
    g.setClip(oldClip);
    return this.w > w - speed;
  }
  
  public boolean equals(HTMLString o)
  {
    return string == o.string && style == o.style && size == o.size && c == o.c;
  }
}
