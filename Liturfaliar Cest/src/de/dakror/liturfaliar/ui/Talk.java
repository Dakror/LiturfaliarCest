package de.dakror.liturfaliar.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.NPC;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.Database;

public class Talk extends Component
{
  public static final int SPEED        = 10;
  NPC                     by;
  String[]                raw;
  HTMLString[][]          lines;
  HTMLString              namelabel;
  String[]                perspectives;
  BufferedImage           speaker;
  int                     perspective;
  int                     ID;
  int                     cay;
  int                     linemax;
  int                     displayindex = 0;
  long                    time;
  boolean                 firstclickskipped;
  boolean                 showall;
  Map                     m;
  
  public Talk(NPC by, Map m)
  {
    super(0, 0, 1, 1);
    try
    {
      this.firstclickskipped = false;
      this.showall = false;
      this.by = by;
      this.m = m;
      int highest = -1;
      JSONObject sel = null;
      for (int i = 0; i < by.getTalkData().length(); i++)
      {
        JSONObject t = by.getTalkData().getJSONObject(i);
        int count = 0;
        JSONArray c = t.getJSONArray("cond");
        for (int j = 0; j < c.length(); j++)
        {
          if (Database.getBooleanVar(c.getString(j)))
            count++;
        }
        if (count > highest)
        {
          sel = t;
          highest = count;
        }
      }
      String raw = sel.getString("text");
      ArrayList<String> l = new ArrayList<String>(Arrays.asList(raw.split("\\[")));
      l.remove(0);
      String[] perspec_raw = (String[]) l.toArray(new String[0]);
      this.perspectives = new String[perspec_raw.length];
      this.raw = new String[perspec_raw.length];
      for (int i = 0; i < this.perspectives.length; i++)
      {
        this.perspectives[i] = perspec_raw[i].substring(0, perspec_raw[i].indexOf("]"));
        this.raw[i] = perspec_raw[i].substring(perspec_raw[i].indexOf("]") + 1);
      }
      this.namelabel = new HTMLString(by.getName(), 32.0F, Color.decode("#999999"), 1);
      this.cay = 0;
      this.ID = sel.getInt("id");
      this.perspective = 0;
      findSpeaker();
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
  }
  
  public void findSpeaker()
  {
    BufferedImage bi = new BufferedImage(32, 32, 2);
    Graphics g = bi.getGraphics();
    g.setClip(0, 0, 32, 21);
    if (this.perspectives[this.perspective].indexOf("npc") > -1)
      Assistant.drawChar(0, 0, 32, 48, 0, 0, "chars", this.by.getCharacter(), (Graphics2D) g, null, true);
    else if (this.perspectives[this.perspective].indexOf("player") > -1)
      try
      {
        Assistant.drawChar(0, 0, 32, 48, 0, 0, this.m.getPlayer().getData().getJSONObject("char"), (Graphics2D) g, null, true);
      }
      catch (JSONException localJSONException)
      {}
    this.speaker = bi;
  }
  
  public void update()
  {}
  
  public void draw(Graphics2D g, Viewport v)
  {
    if (this.lines == null)
    {
      setX(v.w.getWidth() / 6);
      setY(v.w.getHeight() / 16 * 13);
      setWidth(v.w.getWidth() / 3 * 2);
      setHeight(v.w.getHeight() / 16 * 3);
      ArrayList<HTMLString[]> l = new ArrayList<HTMLString[]>();
      for (int i = 0; i < this.raw.length; i++)
      {
        l.add(HTMLString.decodeString("<#d9d9d9;27;0>" + Database.filterString(this.raw[i]), this.width - 64, g));
      }
      this.lines = ((HTMLString[][]) l.toArray(new HTMLString[0][]));
    }
    for (int i = 0; i < this.lines[this.perspective].length; i++)
    {
      if (getHeightOfPreviousRows(i + 1, g) > getHeight() - 20 - this.namelabel.getHeight(g))
      {
        this.linemax = i;
        break;
      }
    }
    if (this.linemax == 0)
      this.linemax += 1;
    Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), getX(), getY(), getWidth(), getHeight(), g, v.w);
    this.namelabel.string = Database.filterString(this.perspectives[this.perspective]);
    this.namelabel.drawString(getX() + 10, getY() + 40, g);
    int doneline = this.displayindex - 1;
    int dif = this.lines[this.perspective].length - this.displayindex;
    for (int i = this.displayindex; i < this.displayindex + ((dif > this.linemax) ? this.linemax : dif); i++)
    {
      try
      {
        if (!this.showall)
        {
          if (i > doneline + 1)
            break;
          if (i > 0)
          {
            if (this.lines[this.perspective][i].drawStringAnimated(getX() + 24 + (!this.lines[this.perspective][(i - 1)].br ? this.lines[this.perspective][(i - 1)].getWidth(g) : 0), getY() + this.namelabel.getHeight(g) + getHeightOfPreviousRows(i + 1, g) - getHeightOfPreviousRows(this.displayindex, g), SPEED, g, v))
              doneline = i;
          }
          else if (this.lines[this.perspective][i].drawStringAnimated(getX() + 24, getY() + this.namelabel.getHeight(g) + this.lines[this.perspective][i].getHeight(g), SPEED, g, v))
            doneline = i;
        }
        else
        {
          if (i > 0)
            this.lines[this.perspective][i].drawString(getX() + 24 + (!this.lines[this.perspective][(i - 1)].br ? this.lines[this.perspective][(i - 1)].getWidth(g) : 0), getY() + this.namelabel.getHeight(g) + getHeightOfPreviousRows(i + 1, g) - getHeightOfPreviousRows(this.displayindex, g), g);
          else
          {
            this.lines[this.perspective][i].drawString(getX() + 24, getY() + this.namelabel.getHeight(g) + this.lines[this.perspective][i].getHeight(g), g);
          }
          doneline = this.displayindex + ((dif > this.linemax) ? this.linemax : dif) - 1;
        }
      }
      catch (Exception e)
      {}
    }
    if (doneline == this.displayindex + ((dif > this.linemax) ? this.linemax : dif) - 1)
    {
      this.showall = true;
      if (this.time == 0L)
        this.time = System.currentTimeMillis();
      int size = 2;
      int cay = (int) (Math.sin(0.2D * this.cay) * 5.0D);
      g.drawImage(Viewport.loadImage("system/Arrow.png"), getX() + getWidth() - 18 * size - 10, getY() + getHeight() - 5 - 9 * size + cay, 18 * size, 9 * size, v.w);
      this.cay += 1;
    }
    if (this.speaker != null)
    {
      int size = 96;
      int height = getHeight() > size ? size : getHeight();
      Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), getX() - height, getY(), height, height - 10, g, v.w);
      g.drawImage(this.speaker, getX() - height + 10, getY() + 10, height - 20, height - 10, v.w);
    }
  }
  
  private int getHeightOfPreviousRows(int index, Graphics2D g)
  {
    int height = 0;
    for (int i = 0; i < index; i++)
    {
      if (this.lines[this.perspective][0].br)
        height += this.lines[this.perspective][i].getHeight(g) - 5;
    }
    return height;
  }
  
  public void mouseWheelMoved(MouseWheelEvent e)
  {}
  
  public void mouseDragged(MouseEvent e)
  {}
  
  public void mouseMoved(MouseEvent e)
  {}
  
  public void mouseClicked(MouseEvent e)
  {}
  
  public void mousePressed(MouseEvent e, Map m)
  {
    if (!this.firstclickskipped)
    {
      this.firstclickskipped = true;
      return;
    }
    if (!this.showall && this.lines[this.perspective].length > this.linemax)
      this.showall = true;
    else if (this.displayindex + this.linemax < this.lines[this.perspective].length)
    {
      this.showall = false;
      this.displayindex += this.linemax;
    }
    else if (this.perspective < this.perspectives.length - 1)
    {
      this.displayindex = 0;
      this.showall = false;
      this.perspective += 1;
      findSpeaker();
    }
    else
    {
      this.by.setTalking(false);
      this.by.frozen = false;
      m.talk = null;
      Database.setBooleanVar("talked_" + this.ID, Boolean.valueOf(true));
    }
  }
  
  public void mouseReleased(MouseEvent e)
  {}
  
  public void mouseEntered(MouseEvent e)
  {}
  
  public void mouseExited(MouseEvent e)
  {}
  
  public void keyTyped(KeyEvent e)
  {}
  
  public void keyReleased(KeyEvent e)
  {}
  
  public void keyPressed(KeyEvent e)
  {}
  
  @Override
  public void mousePressed(MouseEvent e)
  {}
}
