package de.dakror.liturfaliar.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.CFG;
import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.fx.Animation;
import de.dakror.liturfaliar.map.creature.Creature;
import de.dakror.liturfaliar.map.creature.NPC;
import de.dakror.liturfaliar.map.creature.Player;
import de.dakror.liturfaliar.map.event.DatabaseEventListener;
import de.dakror.liturfaliar.map.event.MapEventListener;
import de.dakror.liturfaliar.scenes.Scene_Game;
import de.dakror.liturfaliar.ui.Talk;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.Compressor;
import de.dakror.liturfaliar.util.Database;
import de.dakror.liturfaliar.util.FileManager;

public class Map implements DatabaseEventListener
{
  private int                         x, y, height, width;
  private BufferedImage               lrender;
  private BufferedImage               hrender;
  public ArrayList<Field>             fields;
  public ArrayList<Creature>          creatures;
  private Area                        bump;
  private JSONObject                  data;
  public float                        alpha;
  public Talk                         talk;
  private MapPack                     mappack;
  private ArrayList<MapEventListener> listeners  = new ArrayList<MapEventListener>();
  private ArrayList<Animation>        animations = new ArrayList<Animation>();
  
  public Map(JSONObject data)
  {
    try
    {
      this.data = data;
      init();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public Map(String mappack, String mapname)
  {
    this(mappack, mapname, "Maps");
  }
  
  public Map(String mappack, String mapname, String dir)
  {
    this(createData(Compressor.decompressFile(new File(FileManager.dir, dir + "/" + mappack + "/maps/" + mapname + ".map"))));
  }
  
  private static JSONObject createData(String s)
  {
    try
    {
      return new JSONObject(s);
    }
    catch (JSONException e)
    {
      e.printStackTrace();
      return null;
    }
  }
  
  public JSONObject getData()
  {
    return data;
  }
  
  public String getMusic()
  {
    try
    {
      return data.getString("music");
    }
    catch (JSONException e)
    {
      return null;
    }
  }
  
  public String getName()
  {
    try
    {
      return data.getString("name");
    }
    catch (JSONException e)
    {
      return null;
    }
  }
  
  public void startTalk(Talk t)
  {
    if (talk == null)
      talk = t;
    for (MapEventListener l : listeners)
    {
      if (l != null)
        l.talkStarted(t, this);
    }
  }
  
  public void endTalk()
  {
    talk = null;
    for (MapEventListener l : listeners)
    {
      if (l != null)
        l.talkEnded(talk, this);
    }
  }
  
  public void changeTalk(Talk t)
  {
    talk = t;
    for (MapEventListener l : listeners)
    {
      if (l != null)
        l.talkChanged(talk, t, this);
    }
  }
  
  public void init() throws Exception
  {
    Database.addDatabaseEventListener(this);
    alpha = 1.0f;
    creatures = new ArrayList<Creature>();
    fields = new ArrayList<Field>();
    int w = 0, h = 0;
    for (int i = 0; i < data.getJSONArray("tile").length(); i++)
    {
      JSONObject o = data.getJSONArray("tile").getJSONObject(i);
      fields.add(new Field(o.getInt("x"), o.getInt("y"), o.getInt("tx"), o.getInt("ty"), o.getDouble("l"), o.getString("tileset"), Field.loadFieldData(o.getJSONObject("data"))));
      if (o.getInt("x") + CFG.FIELDSIZE > w)
        w = o.getInt("x") + CFG.FIELDSIZE;
      if (o.getInt("y") + CFG.FIELDSIZE > h)
        h = o.getInt("y") + CFG.FIELDSIZE;
    }
    setWidth(w);
    setHeight(h);
    lrender = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    hrender = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    ArrayList<Field> cpy = fields;
    Collections.sort(cpy, new Comparator<Field>()
    {
      @Override
      public int compare(Field e1, Field e2)
      {
        return (int) (e1.getLayer() * 100) - (int) (e2.getLayer() * 100);
      }
    });
    bump = new Area(new Rectangle2D.Double(0, 0, 1, 1));
    for (Field f : cpy)
    {
      if (f.getLayer() <= CFG.PLAYERLAYER && f.getLayer() > CFG.SUPERDELLAYER)
        lrender.getGraphics().drawImage(f.getImage(), f.getX(), f.getY(), null);
      else if (f.getLayer() > CFG.PLAYERLAYER && f.getLayer() < CFG.SUPERADDLAYER)
        hrender.getGraphics().drawImage(f.getImage(), f.getX(), f.getY(), null);
    }
    Collections.reverse(cpy);
    for (Field f : cpy)
      if (f.getLayer() <= CFG.PLAYERLAYER && f.getLayer() > CFG.SUPERDELLAYER)
        bump.add(new Area(new Rectangle2D.Double(f.getX(), f.getY(), CFG.FIELDSIZE, CFG.FIELDSIZE)));
      else if (f.getLayer() > CFG.PLAYERLAYER && f.getLayer() < CFG.SUPERADDLAYER)
        bump.add(new Area(new Rectangle2D.Double(f.getX(), f.getY(), CFG.FIELDSIZE, CFG.FIELDSIZE)));
    for (Field f : cpy)
      if (f.getLayer() == CFG.PLAYERLAYER)
        bump.subtract(new Area(new Rectangle2D.Double(f.getX(), f.getY(), CFG.FIELDSIZE, CFG.FIELDSIZE)));
    for (Field f : cpy)
      if (f.getLayer() >= CFG.SUPERADDLAYER)
        bump.add(new Area(new Rectangle2D.Double(f.getX(), f.getY(), CFG.FIELDSIZE, CFG.FIELDSIZE)));
    Collections.reverse(cpy);
    for (Field f : cpy)
      if (f.getLayer() <= CFG.SUPERDELLAYER)
        bump.subtract(new Area(new Rectangle2D.Double(f.getX(), f.getY(), CFG.FIELDSIZE, CFG.FIELDSIZE)));
    JSONArray npcs = data.getJSONArray("npc");
    for (int i = 0; i < npcs.length(); i++)
    {
      JSONObject o = npcs.getJSONObject(i);
      NPC npc = new NPC(o.getInt("x"), o.getInt("y"), o.getInt("w") * CFG.HUMANBOUNDS[0], o.getInt("h") * CFG.HUMANBOUNDS[1], o.getString("name"), o.getString("char"), o.getDouble("speed"), o.getBoolean("rand"), o.getInt("randmd"), o.getInt("id"), o.getJSONArray("talk"));
      creatures.add(npc);
    }
  }
  
  public void update(Scene_Game sg)
  {
    for (Field f : fields)
    {
      f.update(this);
    }
    for (Creature c : creatures)
    {
      c.update(this);
    }
    /*
     * final Map self = this; Comparator<Creature> comp = new Comparator<Creature>() {
     * @Override public int compare(Creature o1, Creature o2) { double pos1 = Math.sqrt(Math.pow(o1.getRelativePos(self)[0], 2) + Math.pow(o1.getRelativePos(self)[1], 2)); double pos2 = Math.sqrt(Math.pow(o2.getRelativePos(self)[0], 2) + Math.pow(o2.getRelativePos(self)[1], 2)); return (int) (pos1 - pos2); } }; Collections.sort(creatures, comp); for (Creature c : creatures) { c.update(this); for (Field[] f : ground) { for (Field f2 : f) { if (c.getBumpArea(this).contains(new Point2D.Double(f2.getX() * CFG.FIELDSIZE + CFG.FIELDSIZE * 0.5, f2.getY() * CFG.FIELDSIZE + CFG.FIELDSIZE * 0.5))) { for (MapEventListener l : listeners) { if (l != null) { l.fieldTriggered(c); } } } else if (c.getBumpArea(this).intersects(f2.getX() * CFG.FIELDSIZE, f2.getY() * CFG.FIELDSIZE, CFG.FIELDSIZE, CFG.FIELDSIZE)) { for (MapEventListener l : listeners) { if (l != null) { l.fieldTouched(c); } } } } } }
     */
  }
  
  public void draw(Graphics2D g, Viewport v)
  {
    g.drawImage(lrender, getX(), getY(), v.w);
    for (int i = 0; i < animations.size(); i++)
    {
      Animation j = animations.get(i);
      if (!j.onTop)
        j.draw(g, v, this);
      if (j.done)
        animations.remove(i);
    }
    // -- field data -- //
    for (Field f : this.fields)
    {
      f.drawData(this, g, v);
    }
    final Map self = this;
    Comparator<Creature> comp = new Comparator<Creature>()
    {
      public int compare(Creature o1, Creature o2)
      {
        return o1.getRelativePos(self)[1] - o2.getRelativePos(self)[1];
      }
    };
    Collections.sort(creatures, comp);
    for (Creature c : creatures)
    {
      c.draw(g, v, this);
    }
    for (int i = 0; i < animations.size(); i++)
    {
      Animation j = animations.get(i);
      if (j.onTop)
        j.draw(g, v, this);
      if (j.done)
        animations.remove(i);
    }
    g.drawImage(hrender, getX(), getY(), v.w);
    // -- field data -- // for (Field[] f1 : ground) { for (Field f : f1) { f.drawUp(g, v, this); } } */
    if (CFG.UIDEBUG)
    {
      Assistant.Shadow(v.w.getBounds(), Color.black, 1, g);
      Assistant.Shadow(getBumpMap(), Color.white, 1, g);
      for (Creature c : creatures)
      {
        c.draw(g, v, this);
      }
    }
    Assistant.Shadow(v.w.getBounds(), Color.black, 1 - alpha, g);
    if (talk != null)
      talk.draw(g, v);
  }
  
  public void setPos(int x, int y)
  {
    setX(x);
    setY(y);
  }
  
  public void move(int x, int y)
  {
    setX(getX() + x);
    setY(getY() + y);
  }
  
  public Area getBumpMap()
  {
    AffineTransform at = new AffineTransform();
    at.setToTranslation(getX() - bump.getBounds2D().getX(), getY() - bump.getBounds2D().getY());
    bump.transform(at);
    return bump;
  }
  
  public BufferedImage getRendered(int size, Viewport v)
  {
    BufferedImage bi = new BufferedImage(size * width, size * height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) bi.getGraphics();
    draw(g, v);
    return bi;
  }
  
  public static String[] getMaps(String pack, String dir)
  {
    if (!new File(FileManager.dir, dir + "/" + pack + "/maps").exists())
      return null;
    File[] files = new File(FileManager.dir, dir + "/" + pack + "/maps").listFiles(new FileFilter()
    {
      @Override
      public boolean accept(File pathname)
      {
        return pathname.isFile() && pathname.getName().endsWith(".map");
      }
    });
    return Assistant.getFileNames(files, false);
  }
  
  public Player getPlayer()
  {
    for (Creature c : creatures)
    {
      if (c instanceof Player)
      {
        return (Player) c;
      }
    }
    return null;
  }
  
  public int getX()
  {
    return x;
  }
  
  public void setX(int x)
  {
    this.x = x;
  }
  
  public int getY()
  {
    return y;
  }
  
  public void setY(int y)
  {
    this.y = y;
  }
  
  public int getWidth()
  {
    return width;
  }
  
  public int setWidth(int width)
  {
    this.width = width;
    return width;
  }
  
  public int getHeight()
  {
    return height;
  }
  
  public int setHeight(int height)
  {
    this.height = height;
    return height;
  }
  
  public MapPack getMapPack()
  {
    return mappack;
  }
  
  public void setMapPack(MapPack mappack)
  {
    this.mappack = mappack;
  }
  
  public void playAnimation(Animation a)
  {
    animations.add(a);
  }
  
  public void mouseDragged(MouseEvent e)
  {
    for (Creature c : creatures)
    {
      c.mouseDragged(e, this);
    }
  }
  
  public void mouseMoved(MouseEvent e)
  {
    for (Creature c : creatures)
    {
      c.mouseMoved(e, this);
    }
  }
  
  public void mouseClicked(MouseEvent e)
  {
    for (Creature c : creatures)
    {
      c.mouseClicked(e, this);
    }
  }
  
  public void mousePressed(MouseEvent e)
  {
    for (Creature c : creatures)
    {
      c.mousePressed(e, this);
    }
    if (talk != null)
      talk.mousePressed(e, this);
  }
  
  public void mouseReleased(MouseEvent e)
  {
    for (Creature c : creatures)
    {
      c.mouseReleased(e, this);
    }
  }
  
  public void mouseEntered(MouseEvent e)
  {
    for (Creature c : creatures)
    {
      c.mouseEntered(e, this);
    }
  }
  
  public void mouseExited(MouseEvent e)
  {
    for (Creature c : creatures)
    {
      c.mouseExited(e, this);
    }
  }
  
  public void keyTyped(KeyEvent e)
  {}
  
  public void keyPressed(KeyEvent e)
  {
    for (Creature c : creatures)
    {
      c.keyPressed(e, this);
    }
  }
  
  public void keyReleased(KeyEvent e)
  {
    for (Creature c : creatures)
    {
      c.keyReleased(e, this);
    }
  }
  
  @Override
  public void stringVarChanged(String key, String value)
  {}
  
  @Override
  public void booleanVarChanged(String key, boolean value)
  {}
  
  public void addMapEventListener(MapEventListener t)
  {
    listeners.add(t);
  }
  
  public void removeMapEventListener(MapEventListener t)
  {
    listeners.set(listeners.indexOf(t), null);
  }
}
