package de.dakror.liturfaliar.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.Viewport;

/**
 * Static class for small tool functions.
 * 
 * @author Dakror
 * @version 05.10.2012
 */
public final class Assistant
{
  public static void setCursor(Image cursor, Window w)
  {
    if (cursor == null)
    {
      cursor = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    }
    w.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(cursor, new Point(0, 0), "Cursor"));
  }
  
  /**
   * Loads a Image out of the Resources within the jar file.
   * 
   * @param name - The path of the image to be loaded.
   * @return - Loaded Image
   */
  public static Image loadImage(String name)
  {
    try
    {
      return ImageIO.read(Assistant.class.getResource("/img/" + name));
    }
    catch (Exception e)
    {
      return null;
    }
  }
  
  /**
   * Opens up a link in the users default browser.
   * 
   * @param url - Link to follow.
   */
  public static void openLink(String url)
  {
    try
    {
      Desktop.getDesktop().browse(new URI(url));
    }
    catch (Exception e)
    {
      return;
    }
  }
  
  /**
   * Draws a string to the graphics.
   * 
   * @param s - String to draw.
   * @param x - X coordinate.
   * @param y - Y coordinate.
   * @param g - Graphcsi2D to draw on.
   * @param c - Textcolor.
   */
  public static void drawString(String s, int x, int y, Graphics2D g, Color c)
  {
    Color old = g.getColor();
    g.setColor(c);
    g.drawString(s, x, y);
    g.setColor(old);
  }
  
  /**
   * Draws a string to the graphics.
   * 
   * @param s - String to draw.
   * @param x - X coordinate.
   * @param y - Y coordinate.
   * @param g - Graphcsi2D to draw on.
   * @param f - Custom font to be used.
   */
  public static void drawString(String s, int x, int y, Graphics2D g, Color c, Font f)
  {
    Color old = g.getColor();
    Font oldf = g.getFont();
    g.setFont(f);
    g.setColor(c);
    g.drawString(s, x, y);
    g.setColor(old);
    g.setFont(oldf);
  }
  
  /**
   * Draws a x-centered string to the graphics.
   * 
   * @param s - String to draw.
   * @param w - Width to be centered in..
   * @param h - Y coordinate.
   * @param g - Graphcsi2D to draw on.
   * @param size - Textsize.
   * @param c - Textcolor.
   */
  public static int drawCenteredString(String s, int w, int h, Graphics2D g, int size, Color c)
  {
    Color old = g.getColor();
    Font oldf = g.getFont();
    g.setFont(g.getFont().deriveFont((float) size));
    g.setColor(c);
    FontMetrics fm = g.getFontMetrics();
    int x = (w - fm.stringWidth(s)) / 2;
    // int y = (fm.getAscent() + (h - (fm.getAscent() + fm.getDescent())) /
    // 2);
    g.drawString(s, x, h);
    g.setColor(old);
    int nx = x + fm.stringWidth(s);
    g.setFont(oldf);
    return nx;
  }
  
  /**
   * Draws a x-centered string to the graphics.
   * 
   * @param s - String to draw.
   * @param x1 - X-value to be added to the centered coordinate.
   * @param w - Width to be centered in..
   * @param h - Y coordinate.
   * @param g - Graphics2D to draw on.
   * @param size - Textsize.
   * @param c - Textcolor.
   */
  public static int drawCenteredString(String s, int x1, int w, int h, Graphics2D g, int size, Color c)
  {
    Color old = g.getColor();
    Font oldf = g.getFont();
    g.setFont(g.getFont().deriveFont((float) size));
    g.setColor(c);
    FontMetrics fm = g.getFontMetrics();
    int x = x1 + (w - fm.stringWidth(s)) / 2;
    g.drawString(s, x, h);
    g.setColor(old);
    int nx = x + fm.stringWidth(s);
    g.setFont(oldf);
    return nx;
  }
  
  public static int drawCenteredString(String s, int w, int h, Graphics2D g, int size)
  {
    Font old = g.getFont();
    g.setFont(g.getFont().deriveFont((float) size));
    FontMetrics fm = g.getFontMetrics();
    int x = (w - fm.stringWidth(s)) / 2;
    // int y = (fm.getAscent() + (h - (fm.getAscent() + fm.getDescent())) /
    // 2);
    g.drawString(s, x, h);
    int nx = x + fm.stringWidth(s);
    g.setFont(old);
    return nx;
  }
  
  public static String getURLContent(URL u)
  {
    String res = "", line = "";
    try
    {
      BufferedReader br = new BufferedReader(new InputStreamReader(u.openStream()));
      while ((line = br.readLine()) != null)
      {
        res += line;
      }
      br.close();
    }
    catch (IOException e)
    {
      return null;
    }
    return res;
  }
  
  public static String getFileContent(File f)
  {
    String res = "", line = "";
    try
    {
      BufferedReader br = new BufferedReader(new FileReader(f));
      while ((line = br.readLine()) != null)
      {
        res += line;
      }
      br.close();
    }
    catch (IOException e)
    {
      return null;
    }
    return res;
  }
  
  public static void setFileContent(File f, String s)
  {
    try
    {
      BufferedWriter bw = new BufferedWriter(new FileWriter(f));
      bw.write(s);
      bw.close();
    }
    catch (Exception e)
    {}
  }
  
  public static void stretchTileset(Image img, int x, int y, int w, int h, Graphics2D g, Window win)
  {
    // if (w % 32 != 0 || h % 32 != 0)
    // return;
    for (int i = 32; i < w - 32; i += 32)
    {
      for (int j = 32; j < h - 32; j += 32)
      {
        g.drawImage(img, x + i, y + j, x + i + 32, y + j + 32, 32, 32, 64, 64, win);
      }
    }
    // top left
    g.drawImage(img, x, y, x + 32, y + 32, 0, 0, 32, 32, win);
    // top right
    g.drawImage(img, x + w - 32, y, x + w, y + 32, 64, 0, 96, 32, win);
    // middle left
    for (int i = 0; i < h - 64; i += 32)
    {
      g.drawImage(img, x, y + i + 32, x + 32, y + i + 64, 0, 32, 32, 64, win);
    }
    // middle right
    for (int i = 0; i < h - 64; i += 32)
    {
      g.drawImage(img, x + w - 32, y + i + 32, x + w, y + i + 64, 64, 32, 96, 64, win);
    }
    // middle top
    for (int i = 0; i < w - 64; i += 32)
    {
      g.drawImage(img, x + i + 32, y, x + i + 64, y + 32, 32, 0, 64, 32, win);
    }
    // middle bottom
    for (int i = 0; i < w - 64; i += 32)
    {
      g.drawImage(img, x + i + 32, y + h - 32, x + i + 64, y + h, 32, 64, 64, 96, win);
    }
    // bottom left
    g.drawImage(img, x, y + h - 32, x + 32, y + h, 0, 64, 32, 96, win);
    // bottom right
    g.drawImage(img, x + w - 32, y + h - 32, x + w, y + h, 64, 64, 96, 96, win);
  }
  
  public static void drawChar(int x, int y, int w, int h, int dir, int frame, JSONObject cfg, Graphics2D g, Window window, boolean ch)
  {
    try
    {
      String[] parts = { "skin", "eyes", "hair", "hat", "trouser", "gloves", "shirt" };
      for (String s : parts)
      {
        if (cfg.getInt(s) != -1)
          drawChar(x, y, w, h, dir, frame, s, cfg.getInt(s) + "", g, window, ch);
      }
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
  }
  
  public static void drawChar(int x, int y, int w, int h, int dir, int frame, String type, String image, Graphics2D g, Window window, boolean ch)
  {
    Image i = Viewport.loadImage("char/" + type + "/" + image + ".png");
    int sw = i.getWidth(null) / 4;
    int sh = i.getHeight(null) / 4;
    if (!ch)
      g.drawImage(i, x, y, x + w, y + h, dir * sw, (frame % 4) * sh, dir * sw + sw, (frame % 4) * sh + sh, window);
    else g.drawImage(i, x, y, x + w, y + h, (frame % 4) * sw, dir * sh, (frame % 4) * sw + sw, dir * sh + sh, window);
  }
  
  public static int round(int i, int step)
  {
    if (i % step > step / 2.0f)
      return i + (step - (i % step));
    else return i - (i % step);
  }
  
  public static int search(JSONArray array, Object thing)
  {
    for (int i = 0; i < array.length(); i++)
    {
      try
      {
        if (array.get(i).equals(thing))
          return i;
      }
      catch (JSONException e)
      {
        e.printStackTrace();
        return -1;
      }
    }
    return -1;
  }
  
  public static double scale(double d1, double d2, double d3)
  {
    return d3 * (d2 / d1);
  }
  
  public static Object[] getArrayFromLimits(int min, int max)
  {
    Object[] res = new Object[max - min];
    for (int i = min; i < max; i++)
    {
      res[i - min] = i;
    }
    return res;
  }
  
  public static void Rect(int x, int y, int w, int h, Color border, Color fill, Graphics2D g)
  {
    Color old = g.getColor();
    g.setColor(border);
    g.drawRect(x, y, w, h);
    if (fill != null)
    {
      g.setColor(fill);
      g.fillRect(x, y, w, h);
    }
    g.setColor(old);
  }
  
  private static int rolldie(int minvalue, int maxvalue)
  {
    int result;
    while (true)
    {
      result = (int) Math.floor(Math.random() * (maxvalue - minvalue + 1) + minvalue);
      if ((result >= minvalue) && (result <= maxvalue))
      {
        return result;
      }
    }
  }
  
  public static String getRandomName()
  {
    try
    {
      JSONObject cfg = new JSONObject(getURLContent(Assistant.class.getResource("/json/nameConst.json")));
      JSONArray data = null;
      String genname = "";
      int length = rolldie(3, 5);
      int isvowel = rolldie(0, 1);
      for (int i = 1; i <= length; i++)
      {
        do
        {
          {
            if (isvowel == 1)
            {
              data = cfg.getJSONArray("vowels").getJSONArray(rolldie(0, cfg.getJSONArray("vowels").length() - 1));
            }
            else
            {
              data = cfg.getJSONArray("consonants").getJSONArray(rolldie(0, cfg.getJSONArray("consonants").length() - 1));
            }
            if (i == 1)
            {
              if ((data.getInt(1) & 2) > 0)
                break;
            }
            else if (i == length)
            {
              if ((data.getInt(1) & 1) > 0)
                break;
            }
            else
            {
              if ((data.getInt(1) & 4) > 0)
                break;
            }
          }
        }
        while (genname == "");
        if (data != null)
          genname += data.getString(0);
        isvowel = 1 - isvowel;
      }
      if (genname.length() > 1)
        genname = (genname.substring(0, 1)).toUpperCase() + genname.substring(1);
      return genname;
    }
    catch (JSONException e)
    {
      e.printStackTrace();
      return null;
    }
  }
  
  public static String wrap(String in, int len)
  {
    in = in.trim();
    if (in.length() < len)
      return in;
    if (in.substring(0, len).contains("\n"))
      return in.substring(0, in.indexOf("\n")).trim() + "\n\n" + wrap(in.substring(in.indexOf("\n") + 1), len);
    int place = Math.max(Math.max(in.lastIndexOf(" ", len), in.lastIndexOf("\t", len)), in.lastIndexOf("-", len));
    return in.substring(0, place).trim() + "\n" + wrap(in.substring(place), len);
  }
  
  public static String[] getFileNames(File[] files, boolean ext)
  {
    String[] names = new String[files.length];
    for (int i = 0; i < files.length; i++)
    {
      names[i] = (ext || files[i].isDirectory()) ? files[i].getName() : files[i].getName().substring(0, files[i].getName().lastIndexOf("."));
    }
    return names;
  }
  
  public static void Shadow(Shape shape, Color c, float alpha, Graphics2D g)
  {
    try
    {
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
    }
    catch (IllegalArgumentException e)
    {
      return;
    }
    Color old = g.getColor();
    g.setColor(c);
    g.fill(shape);
    g.setColor(old);
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static Integer getHighestKey(HashMap h)
  {
    ArrayList<Integer> keys = new ArrayList<Integer>(h.keySet());
    Collections.sort(keys, new Comparator<Integer>()
    {
      @Override
      public int compare(Integer o1, Integer o2)
      {
        return o1 - o2;
      }
    });
    return keys.get(keys.size() - 1);
  }
  
  public static void drawMenuBackground(Graphics2D g, Window w)
  {
    g.drawImage(Viewport.loadImage("system/menu.jpg"), 0, 0, w.getWidth(), w.getHeight(), w);
  }
  
  public static String formatBinarySize(long size, int digits)
  {
    final String[] levels = { "", "K", "M", "G", "T" };
    for (int i = levels.length - 1; i > -1; i--)
    {
      if (size > (long) Math.pow(1024, i))
      {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(digits);
        df.setMinimumFractionDigits(digits);
        return df.format(size / Math.pow(1024, i)) + levels[i] + "B";
      }
    }
    return null;
  }
  
  public static boolean isInternetReachable()
  {
    try
    {
      URL u = new URL("http://dakror.de");
      u.openStream();
      return true;
    }
    catch (Exception e1)
    {
      return false;
    }
  }
  
  public static String ColorToHex(Color c)
  {
    return "#" + Integer.toHexString(c.getRed()) + Integer.toHexString(c.getGreen()) + Integer.toHexString(c.getBlue());
  }
  
  public static String joinArray(Object[] o, String glue)
  {
    return Arrays.asList(o).toString().replaceAll("^\\[|\\]$", glue);
  }
  
  public static int getSorting(int i)
  {
    if (i > 0)
      return 1;
    else return -1;
  }
  
  public static ArrayList<JSONObject> JSONArrayToArray(JSONArray a)
  {
    ArrayList<JSONObject> list = new ArrayList<JSONObject>();
    for (int i = 0; i < a.length(); i++)
    {
      try
      {
        if (a.get(i) instanceof JSONObject)
          list.add(a.getJSONObject(i));
      }
      catch (JSONException e)
      {
        e.printStackTrace();
      }
    }
    return list;
  }
  
  public static <T> T[] concat(T[] first, T[] second)
  {
    T[] result = Arrays.copyOf(first, first.length + second.length);
    System.arraycopy(second, 0, result, first.length, second.length);
    return result;
  }
  
  public static Graphics2D copyGraphics2DAttributes(Graphics2D a, Graphics2D b)
  {
    b.setRenderingHints(a.getRenderingHints());
    b.setFont(a.getFont());
    b.setColor(a.getColor());
    return b;
  }
}
