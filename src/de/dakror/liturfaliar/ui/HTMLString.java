package de.dakror.liturfaliar.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents Text with custom font settings.<br>
 * <br>
 * Syntax: <code>&lt;HEX-Color; SIZE; STYLE&gt;</code><br>
 * HEX-Color is the hexadecimal value of {@link Color}. e.g <code>#ff0000 = red</code><br>
 * SIZE is just the font size.<br>
 * STYLE is one of the following values for different Font style. Possible values: 0 (=plain text), 1 (=<i>italics</i>), 2 (=<b>bold</b>)<br>
 * 
 * @author Dakror
 */
public class HTMLString
{
	public int style;
	public float size;
	public boolean br;
	
	public Color c;
	public String string;
	
	public HTMLString(String st, float sz, Color color, int styl)
	{
		string = st;
		size = sz;
		c = color;
		style = styl;
	}
	
	public HTMLString(HTMLString htmls, String st)
	{
		this(st, htmls.size, htmls.c, htmls.style);
	}
	
	public int getWidth(Graphics2D g)
	{
		return g.getFontMetrics(new Font(g.getFont().getFontName(), style, (int) size)).stringWidth(string);
	}
	
	public int getHeight(Graphics2D g)
	{
		return g.getFontMetrics(new Font(g.getFont().getFontName(), style, (int) size)).getHeight();
	}
	
	/**
	 * tepmplate for string: "<#Color;Size;Style>Message"<br>
	 * line break: "[br]"
	 */
	public static HTMLString[] decodeString(String decodeString)
	{
		String[] tags = decodeString.split("<#");
		ArrayList<HTMLString> strings = new ArrayList<HTMLString>();
		for (int i = 1; i < tags.length; i++)
		{
			if (tags[i].length() == 0) continue;
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
						if (text.lastIndexOf("[br]") == text.length() - new String("[br]").length()) string.br = true;
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
	
	protected static HTMLString[] rec_limitline(HTMLString l, int w, Graphics2D g)
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
	
	public boolean equals(HTMLString o)
	{
		return string == o.string && style == o.style && size == o.size && c == o.c;
	}
	
	public String toString()
	{
		return getClass().getName() + "[color=" + c + ", size=" + size + ", style=" + style + ", string=" + string + "]";
	}
}
