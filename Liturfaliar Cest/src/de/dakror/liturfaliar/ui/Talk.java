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
  
  boolean                 firstClickSkipped;
  boolean                 showAll;
  boolean                 partDone;
  
  int                     perspective;
  int                     ID;
  int                     cay;
  int                     dif;
  int                     lineMax;
  int                     displayIndex = 0;
  
  long                    time;
  
  NPC                     by;
  String[]                raw;
  HTMLString[][]          lines;
  HTMLString              nameLabel;
  String[]                perspectives;
  BufferedImage           speaker;
  Map                     m;
  
  public Talk(NPC b, Map map)
  {
    super(0, 0, 1, 1);
    try
    {
      
      firstClickSkipped = false;
      showAll = false;
      by = b;
      m = map;
      int highest = -1;
      JSONObject sel = null;
      int index = -1;
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
          index = i;
          highest = count;
        }
      }
      String r = sel.getString("text");
      ArrayList<String> l = new ArrayList<String>(Arrays.asList(r.split("\\[")));
      l.remove(0);
      String[] perspec_raw = (String[]) l.toArray(new String[0]);
      perspectives = new String[perspec_raw.length];
      raw = new String[perspec_raw.length];
      for (int i = 0; i < perspectives.length; i++)
      {
        perspectives[i] = perspec_raw[i].substring(0, perspec_raw[i].indexOf("]"));
        raw[i] = perspec_raw[i].substring(perspec_raw[i].indexOf("]") + 1);
      }
      nameLabel = new HTMLString(by.getName(), 32.0F, Color.decode("#999999"), 1);
      cay = 0;
      ID = index;
      perspective = 0;
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
    if (perspectives[perspective].indexOf("npc") > -1)
      Assistant.drawChar(0, 0, 32, 48, 0, 0, "chars", by.getCharacter(), (Graphics2D) g, null, true);
    else if (perspectives[perspective].indexOf("player") > -1)
    {
      try
      {
        Assistant.drawChar(0, 0, 32, 48, 0, 0, m.getPlayer().getData().getJSONObject("char"), (Graphics2D) g, null, true);
      }
      catch (JSONException e)
      {}
    }
    speaker = bi;
  }
  
  public void update()
  {
    if (lines == null)
      return;
    
    if (lineMax == 0)
      lineMax = 1;
    
    
    int doneline = displayIndex - 1;
    dif = lines[perspective].length - displayIndex;
    
    for (int i = displayIndex; i < displayIndex + ((dif > lineMax) ? lineMax : dif); i++)
    {
      if (!showAll)
      {
        if (i > doneline + 1)
        {
          break;
        }
        
        if (lines[perspective][i].updateAnimatedString(SPEED))
        {
          doneline = i;
        }
      }
      else
      {
        lines[perspective][i].showAll();
        doneline = displayIndex + ((dif > lineMax) ? lineMax : dif) - 1;
      }
    }
    
    partDone = (doneline == displayIndex + ((dif > lineMax) ? lineMax : dif) - 1);
    
    if (partDone)
    {
      showAll = true;
      if (time == 0)
        time = System.currentTimeMillis();
      cay++;
      
    }
    
  }
  
  public void draw(Graphics2D g, Viewport v)
  {
    if (lines == null)
    {
      setX(v.w.getWidth() / 6);
      setY(v.w.getHeight() / 16 * 13);
      setWidth(v.w.getWidth() / 3 * 2);
      setHeight(v.w.getHeight() / 16 * 3);
      ArrayList<HTMLString[]> l = new ArrayList<HTMLString[]>();
      for (int i = 0; i < raw.length; i++)
      {
        l.add(HTMLString.decodeString("<#d9d9d9;27;0>" + Database.filterString(raw[i]), this.width - 64, g));
      }
      lines = ((HTMLString[][]) l.toArray(new HTMLString[0][]));
    }
    
    for (int i = 0; i < lines[perspective].length; i++)
    {
      if (getHeightOfPreviousRows(i + 1, g) > getHeight() - 20 - nameLabel.getHeight(g))
      {
        lineMax = i;
        break;
      }
    }
    
    if (lineMax == 0)
    {
      lineMax += 1;
    }
    
    Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), getX(), getY(), getWidth(), getHeight(), g, v.w);
    
    nameLabel.string = Database.filterString(perspectives[perspective]);
    nameLabel.drawString(getX() + 10, getY() + 40, g);
    
    for (int i = displayIndex; i < displayIndex + ((dif > lineMax) ? lineMax : dif); i++)
    {
      if (i > 0)
        lines[perspective][i].drawStringAnimated(getX() + 24 + (!lines[perspective][(i - 1)].br ? lines[perspective][(i - 1)].getWidth(g) : 0), getY() + nameLabel.getHeight(g) + getHeightOfPreviousRows(i + 1, g) - getHeightOfPreviousRows(displayIndex, g), g);
      else lines[perspective][i].drawStringAnimated(getX() + 24, getY() + nameLabel.getHeight(g) + lines[perspective][i].getHeight(g), g);
    }
    
    if (speaker != null)
    {
      int size = 96;
      int height = getHeight() > size ? size : getHeight();
      Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), getX() - height, getY(), height, height - 10, g, v.w);
      g.drawImage(speaker, getX() - height + 10, getY() + 10, height - 20, height - 10, v.w);
    }
    
    if (partDone)
    {
      int size = 2;
      int c = (int) (Math.sin(0.25D * cay) * 5.0D);
      g.drawImage(Viewport.loadImage("system/Arrow.png"), getX() + getWidth() - 18 * size - 10, getY() + getHeight() - 5 - 9 * size + c, 18 * size, 9 * size, v.w);
      
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
    if (!this.firstClickSkipped)
    {
      this.firstClickSkipped = true;
      return;
    }
    if (!this.showAll && this.lines[this.perspective].length > this.lineMax)
      this.showAll = true;
    else if (this.displayIndex + this.lineMax < this.lines[this.perspective].length)
    {
      this.showAll = false;
      this.displayIndex += this.lineMax;
    }
    else if (this.perspective < this.perspectives.length - 1)
    {
      this.displayIndex = 0;
      this.showAll = false;
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
