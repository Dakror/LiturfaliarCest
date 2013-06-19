package de.dakror.liturfaliar.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.NPC;
import de.dakror.liturfaliar.settings.Colors;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.Database;

public class Talk extends Component
{
  public static final int SPEED        = 10;
  public static final int LINEHEIGHT   = 30;
  
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
  TalkString[][]          lines;
  HTMLString              nameLabel;
  String[]                perspectives;
  BufferedImage           speaker;
  Map                     m;
  
  public Talk(NPC b, Map map)
  {
    super(0, 0, 1, 1);
    try
    {
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
      String[] perspec_raw = (String[]) l.toArray(new String[] {});
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
      getSpeakerFace();
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
  }
  
  public void draw(Graphics2D g, Viewport v)
  {
    if (lines == null)
    {
      setX(v.w.getWidth() / 6);
      setY(v.w.getHeight() / 16 * 13);
      setWidth(v.w.getWidth() / 3 * 2);
      setHeight(v.w.getHeight() - getY());
      
      lineMax = (int) Math.floor((getHeight() - 20 - nameLabel.getHeight(g)) / (double) LINEHEIGHT + 0.5D);
      
      ArrayList<TalkString[]> l = new ArrayList<TalkString[]>();
      for (int i = 0; i < raw.length; i++)
      {
        l.add(TalkString.decodeString(m, "<" + Assistant.ColorToHex(Colors.GRAY) + ";27;0>" + Database.filterString(raw[i]), this.width - 24, g));
      }
      lines = ((TalkString[][]) l.toArray(new TalkString[0][]));
    }
    
    Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), getX(), getY(), getWidth(), getHeight(), g, v.w);
    
    nameLabel.string = Database.filterString(perspectives[perspective]);
    nameLabel.drawString(getX() + 10, getY() + 40, g);
    
    for (int i = displayIndex; i < displayIndex + ((dif > lineMax) ? lineMax : dif); i++)
    {
      try
      {
        if (i > 0)
        {
          lines[perspective][i].drawStringAnimated(getX() + 24 + (!lines[perspective][(i - 1)].br ? lines[perspective][(i - 1)].getWidth(g) : 0), getY() + nameLabel.getHeight(g) - 5 + LINEHEIGHT * (i + 1 - displayIndex), g);
        }
        else
        {
          lines[perspective][i].drawStringAnimated(getX() + 24, getY() + nameLabel.getHeight(g) + lines[perspective][i].getHeight(g) - 5, g);
        }
      }
      catch (Exception e)
      {}
    }
    
    if (speaker != null)
    {
      int size = 96;
      int height = getHeight() > size ? size : getHeight();
      Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), getX() - height, getY(), height, height - 10, g, v.w);
      g.drawImage(speaker, getX() - height + 10, getY() + 10 + ((this.by.getCharacter() == null) ? 5 : 0), height - 20, height - 10, v.w);
    }
    
    if (partDone)
    {
      int size = 2;
      int c = (int) (Math.sin(0.25D * cay) * 5.0D);
      g.drawImage(Viewport.loadImage("system/Arrow.png"), getX() + getWidth() - 18 * size - 10, getY() + getHeight() - 5 - 9 * size + c, 18 * size, 9 * size, v.w);
      
    }
  }
  
  public void getSpeakerFace()
  {
    BufferedImage bi = new BufferedImage(96, 96, BufferedImage.TYPE_INT_ARGB);
    Graphics g = bi.getGraphics();
    g.setClip(0, 0, 96, 63);
    if (perspectives[perspective].indexOf("npc") > -1)
    {
      if (by.getCharacter() != null)
        Assistant.drawChar(0, 0, 96, 128, 0, 0, "chars", by.getCharacter(), (Graphics2D) g, null, true);
      else Assistant.drawChar(0, 0, 96, 128, 0, 0, by.getEquipment(), (Graphics2D) g, null, true);
    }
    else if (perspectives[perspective].indexOf("player") > -1)
    {
      Assistant.drawChar(0, 0, 96, 128, 0, 0, m.getPlayer().getEquipment(), (Graphics2D) g, null, true);
    }
    speaker = bi;
  }
  
  public void triggerNext()
  {
    if (!partDone)
    {
      showAll = true;
    }
    else
    {
      if (lines[perspective].length > displayIndex + 1)
      {
        showAll = false;
        displayIndex += lineMax;
      }
      else if (perspective + 1 < perspectives.length)
      {
        displayIndex = 0;
        showAll = false;
        perspective += 1;
        getSpeakerFace();
      }
      else
      {
        for (TalkString[] p : lines)
        {
          for (TalkString ts : p)
          {
            ts.emoticonSequencer.clearCreatureEmoticons();
          }
        }
        by.setTalking(false);
        by.frozen = false;
        m.talk = null;
        Database.setBooleanVar("talked_" + ID, Boolean.valueOf(true));
      }
    }
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
}
