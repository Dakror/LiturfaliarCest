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
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.item.Categories;
import de.dakror.liturfaliar.item.Equipment;
import de.dakror.liturfaliar.settings.CFG;

public final class Assistant
{
  public static boolean charLevelExists(Categories c, String path, String level)
  {
    return Assistant.class.getResource("char/" + c.name().toLowerCase() + "/" + path + "_" + level + ".png") != null;
  }
  
  public static String ColorToHex(Color c)
  {
    return "#" + Integer.toHexString(c.getRed()) + Integer.toHexString(c.getGreen()) + Integer.toHexString(c.getBlue());
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
  
  // dir is real dir from image: down = 0, left = 1, right = 2, up = 3
  public static void drawChar(int x, int y, int w, int h, int dir, int frame, Equipment equip, Graphics2D g, boolean ch)
  {
    if (ch)
    {
      if (equip.hasEquipmentItem(Categories.CAPE) && charLevelExists(Categories.CAPE, equip.getEquipmentItem(Categories.CAPE).getCharPath(), "b"))
      {
        CFG.b("cape_b", equip.getEquipmentItem(Categories.CAPE).getCharPath() + "_b");
        Assistant.drawChar(x, y, w, h, dir, frame, "cape", equip.getEquipmentItem(Categories.CAPE).getCharPath() + "_b", g, ch);
      }
      
      if (equip.hasEquipmentItem(Categories.SKIN))
      {
        Assistant.drawChar(x, y, w, h, dir, frame, "skin", equip.getEquipmentItem(Categories.SKIN).getCharPath() + "_b", g, ch);
        Assistant.drawChar(x, y, w, h, dir, frame, "skin", equip.getEquipmentItem(Categories.SKIN).getCharPath() + "_f", g, ch);
      }
      if (equip.hasEquipmentItem(Categories.EYES)) Assistant.drawChar(x, y, w, h, dir, frame, "eyes", equip.getEquipmentItem(Categories.EYES).getCharPath(), g, ch);
      
      if (equip.hasEquipmentItem(Categories.PANTS)) Assistant.drawChar(x, y, w, h, dir, frame, "pants", equip.getEquipmentItem(Categories.PANTS).getCharPath(), g, ch);
      
      if (equip.hasEquipmentItem(Categories.BOOTS)) Assistant.drawChar(x, y, w, h, dir, frame, "boots", equip.getEquipmentItem(Categories.BOOTS).getCharPath(), g, ch);
      
      if (equip.hasEquipmentItem(Categories.SHIRT)) Assistant.drawChar(x, y, w, h, dir, frame, "shirt", equip.getEquipmentItem(Categories.SHIRT).getCharPath(), g, ch);
      
      if (equip.hasEquipmentItem(Categories.HAIR)) Assistant.drawChar(x, y, w, h, dir, frame, "hair", equip.getEquipmentItem(Categories.HAIR).getCharPath(), g, ch);
      
      
      if (equip.hasEquipmentItem(Categories.CAPE) && charLevelExists(Categories.CAPE, equip.getEquipmentItem(Categories.CAPE).getCharPath(), "m")) Assistant.drawChar(x, y, w, h, dir, frame, "cape", equip.getEquipmentItem(Categories.CAPE).getCharPath() + "_m", g, ch);
      
      if (equip.hasEquipmentItem(Categories.CAPE) && charLevelExists(Categories.CAPE, equip.getEquipmentItem(Categories.CAPE).getCharPath(), "f")) Assistant.drawChar(x, y, w, h, dir, frame, "cape", equip.getEquipmentItem(Categories.CAPE).getCharPath() + "_f", g, ch);
    }
  }
  
  public static void drawChar(int x, int y, int w, int h, int dir, int frame, String type, String image, Graphics2D g, boolean ch)
  {
    Image i = Viewport.loadScaledImage("char/" + type + "/" + image + ".png", w * 4, h * 4);
    int iw = i.getWidth(null) / 4;
    int ih = i.getHeight(null) / 4;
    if (!ch) g.drawImage(i, x, y, x + w, y + h, dir * iw, (frame % 4) * ih, dir * iw + iw, (frame % 4) * ih + ih, Viewport.w);
    else g.drawImage(i, x, y, x + w, y + h, (frame % 4) * iw, dir * ih, (frame % 4) * iw + iw, dir * ih + ih, Viewport.w);
  }
  
  public static int drawHorizontallyCenteredString(String s, int w, int h, Graphics2D g, int size)
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
  
  public static int drawHorizontallyCenteredString(String s, int w, int h, Graphics2D g, int size, Color c)
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
  
  public static int drawHorizontallyCenteredString(String s, int x1, int w, int h, Graphics2D g, int size, Color c)
  {
    Color old = g.getColor();
    Font oldf = g.getFont();
    g.setFont(g.getFont().deriveFont((float) size));
    g.setColor(c);
    FontMetrics fm = g.getFontMetrics();
    int x = x1 + (w - fm.stringWidth(s)) / 2;
    g.drawString(s, x, h);
    g.setColor(old);
    g.setFont(oldf);
    return x;
  }
  
  public static void drawMenuBackground(Graphics2D g)
  {
    g.drawImage(Viewport.loadImage("system/menu.jpg"), 0, 0, Viewport.w.getWidth(), Viewport.w.getHeight(), Viewport.w);
  }
  
  public static void drawString(String s, int x, int y, Graphics2D g, Color c)
  {
    Color old = g.getColor();
    g.setColor(c);
    g.drawString(s, x, y);
    g.setColor(old);
  }
  
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
  
  public static int drawVerticallyCenteredString(String s, int x, int y1, int h, Graphics2D g, int theta, int size, Color c)
  {
    AffineTransform at = new AffineTransform();
    at.rotate(Math.toRadians(theta));
    Color old = g.getColor();
    Font oldf = g.getFont();
    g.setFont(g.getFont().deriveFont((float) size).deriveFont(at));
    g.setColor(c);
    FontMetrics fm = g.getFontMetrics();
    int y = y1 + (h - fm.stringWidth(s)) / 2;
    g.drawString(s, x, y);
    g.setColor(old);
    int nx = x + fm.stringWidth(s);
    g.setFont(oldf);
    return nx;
  }
  
  public static String formatBinarySize(long size, int digits)
  {
    final String[] levels = { "", "K", "M", "G", "T" };
    for (int i = levels.length - 1; i > -1; i--)
      if (size > (long) Math.pow(1024, i))
      {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(digits);
        df.setMinimumFractionDigits(digits);
        return df.format(size / Math.pow(1024, i)) + levels[i] + "B";
      }
    return null;
  }
  
  public static Object[] getArrayFromLimits(int min, int max)
  {
    Object[] res = new Object[max - min];
    for (int i = min; i < max; i++)
      res[i - min] = i;
    return res;
  }
  
  public static String getFileContent(File f)
  {
    String res = "", line = "";
    try
    {
      BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
      while ((line = br.readLine()) != null)
        res += line;
      br.close();
    }
    catch (IOException e)
    {
      return null;
    }
    return res;
  }
  
  public static String[] getFileNames(File[] files, boolean ext)
  {
    String[] names = new String[files.length];
    for (int i = 0; i < files.length; i++)
      names[i] = (ext || files[i].isDirectory()) ? files[i].getName() : files[i].getName().substring(0, files[i].getName().lastIndexOf("."));
    return names;
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
          if (isvowel == 1) data = cfg.getJSONArray("vowels").getJSONArray(rolldie(0, cfg.getJSONArray("vowels").length() - 1));
          else data = cfg.getJSONArray("consonants").getJSONArray(rolldie(0, cfg.getJSONArray("consonants").length() - 1));
          if (i == 1)
          {
            if ((data.getInt(1) & 2) > 0) break;
          }
          else if (i == length)
          {
            if ((data.getInt(1) & 1) > 0) break;
          }
          else if ((data.getInt(1) & 4) > 0) break;
        }
        while (genname == "");
        if (data != null) genname += data.getString(0);
        isvowel = 1 - isvowel;
      }
      if (genname.length() > 1) genname = (genname.substring(0, 1)).toUpperCase() + genname.substring(1);
      return genname;
    }
    catch (JSONException e)
    {
      e.printStackTrace();
      return null;
    }
  }
  
  public static int getSorting(int i)
  {
    if (i > 0) return 1;
    else return -1;
  }
  
  public static String getURLContent(URL u)
  {
    String res = "", line = "";
    try
    {
      BufferedReader br = new BufferedReader(new InputStreamReader(u.openStream()));
      while ((line = br.readLine()) != null)
        res += line;
      br.close();
    }
    catch (IOException e)
    {
      return null;
    }
    return res;
  }
  
  public static boolean isInternetReachable()
  {
    try
    {
      return InetAddress.getByName("dakror.de").isReachable(60000);
    }
    catch (Exception e)
    {
      return false;
    }
  }
  
  public static String joinArray(Object[] o, String glue)
  {
    return Arrays.asList(o).toString().replaceAll("^\\[|\\]$", glue);
  }
  
  public static ArrayList<JSONObject> JSONArrayToArray(JSONArray a)
  {
    ArrayList<JSONObject> list = new ArrayList<JSONObject>();
    for (int i = 0; i < a.length(); i++)
      try
      {
        if (a.get(i) instanceof JSONObject) list.add(a.getJSONObject(i));
      }
      catch (JSONException e)
      {
        e.printStackTrace();
      }
    return list;
  }
  
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
  
  public static Double parseDouble(String s)
  {
    try
    {
      return Double.parseDouble(s);
    }
    catch (Exception e)
    {
      return Double.NaN;
    }
  }
  
  public static Integer parseInt(String s)
  {
    try
    {
      return Integer.parseInt(s);
    }
    catch (Exception e)
    {
      return null;
    }
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
  
  public static int round(int i, int step)
  {
    if (i % step > step / 2.0f) return i + (step - (i % step));
    else return i - (i % step);
  }
  
  public static double scale(double d1, double d2, double d3)
  {
    return d3 * (d2 / d1);
  }
  
  public static int search(JSONArray array, Object thing)
  {
    for (int i = 0; i < array.length(); i++)
      try
      {
        if (array.get(i).equals(thing)) return i;
      }
      catch (JSONException e)
      {
        e.printStackTrace();
        return -1;
      }
    return -1;
  }
  
  public static void setCursor(Image cursor)
  {
    if (cursor == null) cursor = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    Viewport.w.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(cursor, new Point(0, 0), "Cursor"));
  }
  
  public static void setFileContent(File f, String s)
  {
    try
    {
      OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(f), "UTF8");
      osw.write(s);
      osw.close();
    }
    catch (Exception e)
    {}
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
  
  public static void stretchTileset(Image img, int x, int y, int w, int h, Graphics2D g)
  {
    // if (w % 32 != 0 || h % 32 != 0)
    // return;
    for (int i = 32; i < w - 32; i += 32)
      for (int j = 32; j < h - 32; j += 32)
        g.drawImage(img, x + i, y + j, x + i + 32, y + j + 32, 32, 32, 64, 64, Viewport.w);
    // top left
    g.drawImage(img, x, y, x + 32, y + 32, 0, 0, 32, 32, Viewport.w);
    // top right
    g.drawImage(img, x + w - 32, y, x + w, y + 32, 64, 0, 96, 32, Viewport.w);
    // middle left
    for (int i = 0; i < h - 64; i += 32)
      g.drawImage(img, x, y + i + 32, x + 32, y + i + 64, 0, 32, 32, 64, Viewport.w);
    // middle right
    for (int i = 0; i < h - 64; i += 32)
      g.drawImage(img, x + w - 32, y + i + 32, x + w, y + i + 64, 64, 32, 96, 64, Viewport.w);
    // middle top
    for (int i = 0; i < w - 64; i += 32)
      g.drawImage(img, x + i + 32, y, x + i + 64, y + 32, 32, 0, 64, 32, Viewport.w);
    // middle bottom
    for (int i = 0; i < w - 64; i += 32)
      g.drawImage(img, x + i + 32, y + h - 32, x + i + 64, y + h, 32, 64, 64, 96, Viewport.w);
    // bottom left
    g.drawImage(img, x, y + h - 32, x + 32, y + h, 0, 64, 32, 96, Viewport.w);
    // bottom right
    g.drawImage(img, x + w - 32, y + h - 32, x + w, y + h, 64, 64, 96, 96, Viewport.w);
  }
  
  public static Area toArea(Image img)
  {
    BufferedImage image = Assistant.toBufferedImage(img);
    
    Area area = new Area();
    
    for (int i = 0; i < image.getHeight(); i++)
      for (int j = 0; j < image.getWidth(); j++)
        if (((image.getRGB(j, i) >> 24) & 0xff) == 255) area.add(new Area(new Rectangle2D.Double(j, i, 1, 1)));
    return area;
  }
  
  public static BufferedImage toBufferedImage(Image img)
  {
    BufferedImage image = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
    image.getGraphics().drawImage(img, 0, 0, null);
    
    return image;
  }
  
  public static String wrap(String in, int len)
  {
    in = in.trim();
    if (in.length() < len) return in;
    if (in.substring(0, len).contains("\n")) return in.substring(0, in.indexOf("\n")).trim() + "\n\n" + wrap(in.substring(in.indexOf("\n") + 1), len);
    int place = Math.max(Math.max(in.lastIndexOf(" ", len), in.lastIndexOf("\t", len)), in.lastIndexOf("-", len));
    return in.substring(0, place).trim() + "\n" + wrap(in.substring(place), len);
  }
  
  private static int rolldie(int minvalue, int maxvalue)
  {
    int result;
    while (true)
    {
      result = (int) Math.floor(Math.random() * (maxvalue - minvalue + 1) + minvalue);
      if ((result >= minvalue) && (result <= maxvalue)) return result;
    }
  }
  
  public static String getFolderChecksum(File folder)
  {
    if(!folder.exists()) return null;
    try
    {
      MessageDigest md = MessageDigest.getInstance("MD5");
      String f = folder.getName() + Arrays.toString(folder.list()) + getFolderSize(folder);
      return HexBin.encode(md.digest(f.getBytes()));
    }
    catch (NoSuchAlgorithmException e)
    {
      e.printStackTrace();
      return null;
    }
  }
  
  public static long getFolderSize(File directory)
  {
    long length = 0;
    for (File file : directory.listFiles())
    {
      if (file.isFile()) length += file.length();
      else length += getFolderSize(file);
    }
    return length;
  }
}
