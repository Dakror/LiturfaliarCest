package de.dakror.liturfaliar.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.Creature;
import de.dakror.liturfaliar.map.creature.NPC;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.Database;


/**
 * Represents interactive Dialogs with NPCs.<br>
 * Talk-Syntax: <br>
 * You can set font style, size and color by using the Syntax of {@link HTMLString}.<br>
 * To declare the person talking, write their accessID in square braces. e.g <code>[npc_0]</code><br>
 * To show custom emoticons use the Syntax of {@link de.dakror.liturfaliar.fx.EmoticonSequencer EmoticonSequencer}.<br>
 * To force a line break, write <code>[br]</code>.<br>
 * And of course the most important thing is the text to display, which is written without any other restrictions.<br>
 * <br>
 * A finished talk could look like this:<br>
 * <code>&lt;#ff3500;17;0&gt;[npc_0]{npc_0:35}Hello Player!&lt;#ff3500;17;0&gt;[player]Hello NPC #0.</code>
 */
public class Talk extends Component
{
  public static final int    SPEED        = 10;
  public static final int    LINEHEIGHT   = 30;
  public static final int    SIZE         = 27;
  
  public static final String defaultColor = "#ffffff";
  public static final int    defaultStyle = 0;
  
  int                        talkID;
  int                        page;
  int                        maxCols;
  int                        maxLines;
  int                        perspective;
  int                        activeLine;
  
  NPC                        initiator;
  Map                        m;
  HTMLString                 nameLabel;
  BufferedImage              speakerFace;
  TalkString[][]             lines;
  String[]                   perspectives;
  
  public Talk(NPC init, Map m)
  {
    super(0, 0, 0, 0);
    initiator = init;
    this.m = m;
  }
  
  private JSONObject find()
  {
    try
    {
      int highest = -1;
      JSONObject sel = null;
      int index = -1;
      for (int i = 0; i < initiator.getTalkData().length(); i++)
      {
        JSONObject t = initiator.getTalkData().getJSONObject(i);
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
      talkID = index;
      return sel;
    }
    catch (Exception e)
    {
      return null;
    }
  }
  
  private void parse(String firstParsed)
  {
    try
    {
      JSONObject talk = find();
      String rawText = talk.getString("text");
      if (firstParsed != null)
        rawText = firstParsed;
      
      String[] persp = rawText.split("\\[");
      persp = Arrays.copyOfRange(persp, 1, persp.length);
      perspectives = new String[persp.length];
      lines = new TalkString[persp.length][];
      
      for (int i = 0; i < persp.length; i++)
      {
        String pers = persp[i];
        
        String speaker = pers.substring(0, pers.indexOf("]"));
        perspectives[i] = speaker;
        
        pers = pers.substring(pers.indexOf("]") + 2);
        
        String[] lns = pers.split("(br>|#)");
        ArrayList<TalkString> strings = new ArrayList<>();
        
        TalkString cache = null;
        for (int j = 0; j < lns.length; j++)
        {
          String line = lns[j];
          if (line.length() == 0)
            continue;
          
          if (firstParsed == null)
            rawText = rawText.replace(line, limitLine(line));
          
          boolean br = false;
          if (line.endsWith("<"))
          {
            br = true;
            line = line.substring(0, line.length() - 2);
          }
          
          if (cache == null)
          {
            String color = defaultColor;
            int style = defaultStyle;
            if (line.length() > 8 && Character.toString(line.charAt(6)).equals(":"))
            {
              color = "#" + line.substring(0, 6);
              style = Integer.parseInt(Character.toString(line.charAt(7)));
              line = line.substring(8);
            }
            cache = new TalkString(m, line, Color.decode(color), style);
            cache.br = br;
          }
          else
          {
            strings.add(cache);
            Color color = cache.c;
            int style = cache.style;
            
            if (line.length() > 8 && Character.toString(line.charAt(6)).equals(":"))
            {
              color = Color.decode("#" + line.substring(0, 6));
              style = Integer.parseInt(Character.toString(line.charAt(7)));
              line = line.substring(8);
            }
            
            cache = new TalkString(m, line, color, style);
            cache.br = br;
          }
        }
        
        strings.add(cache);
        lines[i] = strings.toArray(new TalkString[] {});
        
        if (firstParsed == null)
          parse(rawText);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  @Override
  public void update()
  {}
  
  public void next()
  {
    if (perspective == -1)
      perspective = 0;
    else if (activeLine == lines[perspective].length - 1)
      perspective++;
    
    BufferedImage bi = new BufferedImage(96, 96, BufferedImage.TYPE_INT_ARGB);
    Graphics g = bi.getGraphics();
    g.setClip(0, 0, 96, 63);
    if (perspectives[perspective].indexOf("npc") > -1)
    {
      Creature c = m.getCreatureByAccessKey(perspectives[perspective]);
      nameLabel.string = c.getName();
      
      if (c.getCharacter() != null)
        Assistant.drawChar(0, 0, 96, 128, 0, 0, "chars", c.getCharacter(), (Graphics2D) g, null, true);
      else Assistant.drawChar(0, 0, 96, 128, 0, 0, c.getEquipment(), (Graphics2D) g, null, true);
    }
    else if (perspectives[perspective].indexOf("player") > -1)
    {
      nameLabel.string = m.getPlayer().getName();
      Assistant.drawChar(0, 0, 96, 128, 0, 0, m.getPlayer().getEquipment(), (Graphics2D) g, null, true);
    }
    speakerFace = bi;
    
    activeLine = 0;
  }
  
  public String limitLine(String s)
  {
    return s.replaceAll("([,.!?]{1})(\\S{1})", "$1 $2").replace("#", " #").replace("<br>", " <br>").replaceAll("(.{" + (maxCols - 10) + "," + maxCols + "})( )", "$1<br>");
  }
  
  @Override
  public void draw(Graphics2D g, Viewport v)
  {
    if (getWidth() == 0)
    {
      setX(v.w.getWidth() / 6);
      setY(v.w.getHeight() / 16 * 13);
      setWidth(v.w.getWidth() / 3 * 2);
      setHeight(v.w.getHeight() - getY());
      
      maxCols = (getWidth() - 34) / g.getFontMetrics(g.getFont().deriveFont((float) SIZE)).stringWidth("r");
      
      parse(null);
      
      perspective = -1;
      nameLabel = new HTMLString("", 32.0F, Color.decode("#999999"), 1);
      
      next();
      
      maxLines = (int) Math.floor((getHeight() - 20 - nameLabel.getHeight(g)) / (double) LINEHEIGHT + 0.5D);
    }
    
    Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), getX(), getY(), getWidth(), getHeight(), g, v.w);
    
    nameLabel.drawString(getX() + 10, getY() + 40, g);
    
    if (speakerFace != null)
    {
      Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), getX() - 80, getY(), 80, 80, g, v.w);
      g.drawImage(speakerFace, getX() - 80 + 7, getY() + 13, 70, 75, v.w);
    }
    
    int y = -1;
    for (int i = page * maxLines; i < lines[perspective].length; i++)
    {
      if (y >= maxLines)
        break;
      
      TalkString line = lines[perspective][i];
      
      if (i > 0)
      {
        TalkString prev = lines[perspective][i - 1];
        line.drawString(getX() + 24 + ((!prev.br) ? prev.getWidth(g) : 0), getY() + nameLabel.getHeight(g) + LINEHEIGHT + ((y > 0) ? y * LINEHEIGHT : 0) + ((prev.br) ? LINEHEIGHT : 0), g);
      }
      else
      {
        line.drawString(getX() + 24, getY() + nameLabel.getHeight(g) + LINEHEIGHT, g);
      }
      
      if (line.br)
        y++;
    }
  }
  
  @Override
  public void keyPressed(KeyEvent e)
  {
    // TODO: next
  }
}
