package de.dakror.liturfaliar.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
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
  
  boolean                    showAll;
  boolean                    partDone;
  boolean                    turnPage;
  
  int                        talkID;
  int                        firstIndex;
  int                        maxCols;
  int                        maxLines;
  int                        perspective;
  int                        activeLine;
  int                        cos;
  
  long                       time;
  
  NPC                        initiator;
  Map                        m;
  HTMLString                 nameLabel;
  BufferedImage              speakerFace;
  TalkString[][]             lines;
  TalkChooser[]              choosers;
  TalkChooser                activeChooser;
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
          if (Database.getBooleanVar(c.getString(j))) count++;
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
      if (firstParsed != null) rawText = firstParsed;
      
      if (firstParsed == null)
      {
        ArrayList<TalkChooser> ch = new ArrayList<>();
        String[] chs = rawText.split("\\(");
        chs = Arrays.copyOfRange(chs, 1, chs.length);
        for (int i = 0; i < chs.length; i++)
        {
          String r = chs[i].substring(0, chs[i].indexOf(")"));
          ch.add(new TalkChooser(r));
          
          rawText = rawText.replace(r, "" + (ch.size() - 1));
        }
        
        choosers = ch.toArray(new TalkChooser[] {});
      }
      
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
          if (line.length() == 0) continue;
          
          if (firstParsed == null)
          {
            if (cache != null && !cache.br) rawText = rawText.replace(line, limitLine(createEmptyString(cache.string.length()) + line).trim());
            else rawText = rawText.replace(line, limitLine(line));
          }
          
          boolean br = false;
          if (line.endsWith("<"))
          {
            br = true;
            line = line.substring(0, line.length() - 1);
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
        
        if (firstParsed == null) parse(rawText);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  private String createEmptyString(int l)
  {
    String s = "";
    for (int i = 0; i < l; i++)
      s += " ";
    return s;
  }
  
  private int getPreviousWidth(int index, Graphics2D g)
  {
    int w = 0;
    for (int i = firstIndex; i < lines[perspective].length; i++)
    {
      if (i == index) return w;
      
      if (!lines[perspective][i].br) w += lines[perspective][i].getWidth(g) + 5;
      else w = 0;
    }
    
    return 0;
  }
  
  @Override
  public void update()
  {
    if (lines == null) return;
    
    if (activeChooser != null) activeChooser.update();
    
    if (showAll)
    {
      activeLine = firstIndex + getLinesForPageMax() - 1;
      lines[perspective][activeLine].showAll();
      showAll = false;
    }
    
    if (turnPage)
    {
      firstIndex += getLinesForPageMax();
      activeLine = firstIndex;
      
      turnPage = false;
    }
    
    partDone = (activeLine - firstIndex) % getLinesForPageMax() == getLinesForPageMax() - 1 && lines[perspective][activeLine].isAllShown();
    
    if (partDone)
    {
      if (time == 0) time = System.currentTimeMillis();
      cos++;
    }
    
    if (lines[perspective][activeLine].updateAnimatedString(SPEED) && !partDone && activeLine < lines[perspective].length - 1 && activeChooser == null) activeLine++;
    
    if (lines[perspective][activeLine].chooser > -1 && activeChooser == null)
    {
      activeChooser = choosers[lines[perspective][activeLine].chooser];
    }
    if (activeChooser != null && !activeChooser.isCloseRequested()) return;
    else activeChooser = null;
    
    if (Database.getBooleanVar("quit_talk"))
    {
      Database.setBooleanVar("quit_talk", false);
      endTalk();
    }
  }
  
  public void next()
  {
    if (lines == null || activeChooser != null) return;
    
    if (perspective == -1)
    {
      perspective = 0;
      firstIndex = 0;
      activeLine = 0;
    }
    else if (activeLine == lines[perspective].length - 1 && perspectives.length > perspective + 1)
    {
      perspective++;
      firstIndex = 0;
      activeLine = 0;
    }
    else if (activeLine == lines[perspective].length - 1 && perspectives.length == perspective + 1)
    {
      endTalk();
    }
    else if (partDone)
    {
      turnPage = true;
    }
    else if (!partDone)
    {
      showAll = true;
    }
    
    BufferedImage bi = new BufferedImage(96, 96, BufferedImage.TYPE_INT_ARGB);
    Graphics g = bi.getGraphics();
    g.setClip(0, 0, 96, 63);
    if (perspectives[perspective].indexOf("npc") > -1)
    {
      Creature c = m.getCreatureByAccessKey(perspectives[perspective]);
      nameLabel.string = c.getName();
      
      if (c.getCharacter() != null) Assistant.drawChar(0, 0, 96, 128, 0, 0, "chars", c.getCharacter(), (Graphics2D) g, true);
      else Assistant.drawChar(0, 0, 96, 128, 0, 0, c.getEquipment(), (Graphics2D) g, true);
    }
    else if (perspectives[perspective].indexOf("player") > -1)
    {
      nameLabel.string = m.getPlayer().getName();
      Assistant.drawChar(0, 0, 96, 128, 0, 0, m.getPlayer().getEquipment(), (Graphics2D) g, true);
    }
    speakerFace = bi;
    
    activeLine = 0;
  }
  
  private void endTalk()
  {
    for (TalkString[] p : lines)
      for (TalkString ts : p)
        ts.emoticonSequencer.clearCreatureEmoticons();
    
    for (String p : perspectives)
    {
      Creature c = m.getCreatureByAccessKey(p);
      if (c instanceof NPC) ((NPC) c).setTalking(false);
      
      c.setFrozen(false);
    }
    m.getPlayer().setLookingEnabled(true);
    Database.setBooleanVar("talked_" + talkID, true);
    m.endTalk();
  }
  
  public String limitLine(String s)
  {
    return s.replaceAll("([,.!?]{1})(\\S{1})", "$1 $2").replace("#", " #").replace("<br>", " <br>").replaceAll("(.{" + (maxCols - 10) + "," + maxCols + "})( )", "$1<br>");
  }
  
  public int getLinesForPageMax()
  {
    if (lines == null) return 0;
    
    int r = 0;
    int y = 0;
    for (int i = firstIndex; i < lines[perspective].length; i++)
    {
      if (y >= maxLines) break;
      r++;
      if (lines[perspective][i].br) y++;
    }
    
    return r;
  }
  
  @Override
  public void draw(Graphics2D g)
  {
    if (getWidth() == 0)
    {
      setX(Viewport.w.getWidth() / 6);
      setY(Viewport.w.getHeight() / 16 * 13);
      setWidth(Viewport.w.getWidth() / 3 * 2);
      setHeight(Viewport.w.getHeight() - getY());
      
      maxCols = (getWidth() - 34) / g.getFontMetrics(g.getFont().deriveFont((float) SIZE)).stringWidth("r");
      
      parse(null);
      
      perspective = -1;
      nameLabel = new HTMLString("", 32.0F, Color.decode("#999999"), 1);
      
      next();
      
      maxLines = (int) Math.floor((getHeight() - 20 - nameLabel.getHeight(g)) / (double) LINEHEIGHT + 0.5D);
      
      for (String p : perspectives)
      {
        Creature c = m.getCreatureByAccessKey(p);
        if (c instanceof NPC) ((NPC) c).setTalking(true);
        
        c.setFrozen(true);
      }
      m.getPlayer().setLookingEnabled(false);
    }
    
    Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), getX(), getY(), getWidth(), getHeight(), g);
    
    nameLabel.drawString(getX() + 10, getY() + 40, g);
    
    if (speakerFace != null)
    {
      Assistant.stretchTileset(Viewport.loadImage("tileset/Wood.png"), getX() - 80, getY(), 80, 80, g);
      g.drawImage(speakerFace, getX() - 80 + 7, getY() + 13, 70, 75, Viewport.w);
    }
    
    int y = -1;
    for (int i = firstIndex; i < lines[perspective].length; i++)
    {
      if (y >= maxLines - 1) break;
      
      if (i > activeLine) continue;
      
      TalkString line = lines[perspective][i];
      
      if (i % getLinesForPageMax() > 0)
      {
        int prev = getPreviousWidth(i, g);
        
        if (i == activeLine) line.drawStringAnimated(getX() + 24 + ((prev > 0) ? prev : 0), getY() + nameLabel.getHeight(g) + LINEHEIGHT + ((y > 0) ? y * LINEHEIGHT : 0) + ((prev == 0) ? LINEHEIGHT : 0), g);
        else line.drawString(getX() + 24 + ((prev > 0) ? prev : 0), getY() + nameLabel.getHeight(g) + LINEHEIGHT + ((y > 0) ? y * LINEHEIGHT : 0) + ((prev == 0) ? LINEHEIGHT : 0), g);
      }
      else
      {
        if (i == activeLine) line.drawStringAnimated(getX() + 24, getY() + nameLabel.getHeight(g) + LINEHEIGHT, g);
        else line.drawString(getX() + 24, getY() + nameLabel.getHeight(g) + LINEHEIGHT, g);
      }
      
      if (line.br) y++;
    }
    
    if (partDone && activeChooser == null)
    {
      int size = 2;
      int c = (int) (Math.sin(0.25D * cos) * 5.0D);
      g.drawImage(Viewport.loadScaledImage("system/Arrow.png", 18 * size, 9 * size, Image.SCALE_FAST), getX() + getWidth() - 18 * size - 10, getY() + getHeight() - 5 - 9 * size + c, Viewport.w);
      
    }
    
    if (activeChooser != null) activeChooser.draw(g);
  }
  
  @Override
  public void mouseMoved(MouseEvent e)
  {
    if (activeChooser != null) activeChooser.mouseMoved(e);
  }
  
  @Override
  public void mouseReleased(MouseEvent e)
  {
    if (activeChooser != null) activeChooser.mouseReleased(e);
  }
  
  @Override
  public void mousePressed(MouseEvent e)
  {
    if (activeChooser != null) activeChooser.mousePressed(e);
  }
  
  @Override
  public void keyPressed(KeyEvent e)
  {
    if (e.getKeyCode() == KeyEvent.VK_SPACE) next();
  }
}
