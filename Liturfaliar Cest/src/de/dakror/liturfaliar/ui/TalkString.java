package de.dakror.liturfaliar.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;

import de.dakror.liturfaliar.fx.EmoticonSequencer;
import de.dakror.liturfaliar.map.Map;

public class TalkString extends HTMLString
{
  long              time = 0;
  String            s;
  EmoticonSequencer emoticonSequencer;
  
  public TalkString(Map m, String st, float sz, Color color, int styl)
  {
    super(st, sz, color, styl);
    s = "";
    
    emoticonSequencer = new EmoticonSequencer(m, string);
    
    string = emoticonSequencer.getClearedString();
  }
  
  public TalkString(Map m, HTMLString htmls, String st)
  {
    this(m, st, htmls.size, htmls.c, htmls.style);
  }
  
  public void drawStringAnimated(int x, int y, Graphics2D g)
  {
    if (time == 0)
    {
      time = System.currentTimeMillis();
    }
    
    // Shape oldClip = g.getClip();
    
    // int h = fm.getHeight();
    
    // g.setClip(x, y - fm.getLeading() - fm.getMaxAscent(), w, h);
    Color oldColor = g.getColor();
    Font oldFont = g.getFont();
    g.setColor(c);
    g.setFont(oldFont.deriveFont(style, size));
    g.drawString(s, x, y);
    g.setFont(oldFont);
    g.setColor(oldColor);
    // g.setClip(oldClip);
    
  }
  
  public void showAll()
  {
    s = string;
  }
  
  public boolean updateAnimatedString(int speed)
  {
    if (string.length() == 0)
      return false;
    
    if (s.length() < string.length())
    {
      s += String.valueOf(string.charAt(s.length()));
    }
    
    emoticonSequencer.update(s);
    
    return s.equals(string);
  }
  
  public static TalkString[] decodeString(Map m, String decodeString)
  {
    String[] tags = decodeString.split("<#");
    ArrayList<TalkString> strings = new ArrayList<TalkString>();
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
          TalkString string = new TalkString(m, lines[j], Float.parseFloat((options[1] != null) ? options[1] : "16"), Color.decode("#" + ((options[0] != null) ? options[0] : "ffffff")), Integer.parseInt((options[2] != null) ? options[2] : "0"));
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
        TalkString string = new TalkString(m, text, Float.parseFloat((options[1] != null) ? options[1] : "16"), Color.decode("#" + ((options[0] != null) ? options[0] : "ffffff")), Integer.parseInt((options[2] != null) ? options[2] : "0"));
        strings.add(string);
      }
    }
    return strings.toArray(new TalkString[] {});
  }
  
  public static TalkString[] decodeString(Map m, String raw, int w, Graphics2D g)
  {
    TalkString[] lines = decodeString(m, raw);
    
    ArrayList<TalkString> arr = new ArrayList<TalkString>();
    for (int i = 0; i < lines.length; i++)
    {
      arr.addAll(Arrays.asList(rec_limitline(m, lines[i], w, g)));
    }
    return arr.toArray(new TalkString[] {});
  }
  
  protected static TalkString[] rec_limitline(Map m, TalkString l, int w, Graphics2D g)
  {
    if (l.getWidth(g) <= w)
    {
      return new TalkString[] { l };
    }
    else
    {
      String t = l.string;
      String t2 = t.replaceAll("(.{" + (w / new HTMLString(l, "^").getWidth(g)) + "})(\\s)?", "$1[br]").replaceAll("(\\s{1})(\\S{1,})(\\[br\\])(\\S{1,})", "[br]" + l.getTag() + "$2$4").replaceAll("(\\s{1})(\\[br\\])", "[br]" + l.getTag());
      return decodeString(m, l.getTag() + t2);
    }
  }
}
