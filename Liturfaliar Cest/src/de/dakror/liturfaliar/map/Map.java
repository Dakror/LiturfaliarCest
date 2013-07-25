package de.dakror.liturfaliar.map;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
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

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.event.dispatcher.DatabaseEventDispatcher;
import de.dakror.liturfaliar.event.dispatcher.MapEventDispatcher;
import de.dakror.liturfaliar.event.listener.DatabaseEventListener;
import de.dakror.liturfaliar.fx.Animation;
import de.dakror.liturfaliar.item.Equipment;
import de.dakror.liturfaliar.item.Item;
import de.dakror.liturfaliar.item.ItemDrop;
import de.dakror.liturfaliar.map.creature.Creature;
import de.dakror.liturfaliar.map.creature.NPC;
import de.dakror.liturfaliar.map.creature.Player;
import de.dakror.liturfaliar.scenes.Scene_Game;
import de.dakror.liturfaliar.settings.Attributes;
import de.dakror.liturfaliar.settings.CFG;
import de.dakror.liturfaliar.ui.Talk;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.Compressor;
import de.dakror.liturfaliar.util.FileManager;
import de.dakror.liturfaliar.util.Vector;

public class Map implements DatabaseEventListener
{
  private boolean              peaceful;
  private int                  x, y, height, width;
  
  private BufferedImage        lrender;
  private BufferedImage        hrender;
  private Area                 bump;
  private JSONObject           data;
  private MapPack              mappack;
  private ArrayList<Animation> animations = new ArrayList<Animation>();
  private ArrayList<ItemDrop>  itemDrops;
  private ItemDrop             hoveredItemDrop;
  
  public float                 alpha;
  
  public ArrayList<Field>      belowFields;
  public ArrayList<Field>      aboveFields;
  public ArrayList<Field>      fields;
  public ArrayList<Creature>   creatures;
  public Talk                  talk;
  
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
    this(Compressor.openMap(new File(FileManager.dir, dir + "/" + mappack + "/maps/" + mapname + ".map")));
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
    MapEventDispatcher.dispatchTalkStarted(t, this);
  }
  
  public void endTalk()
  {
    talk = null;
    MapEventDispatcher.dispatchTalkEnded(talk, this);
  }
  
  public void changeTalk(Talk t)
  {
    talk = t;
    
    MapEventDispatcher.dispatchTalkChanged(talk, t, this);
  }
  
  public void init() throws Exception
  {
    DatabaseEventDispatcher.addDatabaseEventListener(this);
    alpha = 1.0f;
    creatures = new ArrayList<Creature>();
    aboveFields = new ArrayList<Field>();
    belowFields = new ArrayList<Field>();
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
    
    setPeaceful(data.getBoolean("peaceful"));
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
      {
        lrender.getGraphics().drawImage(f.getImage(), f.getX(), f.getY(), null);
        if (f.getLayer() < CFG.PLAYERLAYER)
          belowFields.add(f);
      }
      else if (f.getLayer() > CFG.PLAYERLAYER && f.getLayer() < CFG.SUPERADDLAYER)
      {
        hrender.getGraphics().drawImage(f.getImage(), f.getX(), f.getY(), null);
        aboveFields.add(f);
      }
    }
    Collections.reverse(cpy);
    for (Field f : cpy)
    {
      if (f.getLayer() <= CFG.PLAYERLAYER && f.getLayer() > CFG.SUPERDELLAYER)
      {
        bump.add(new Area(new Rectangle2D.Double(f.getX(), f.getY(), CFG.FIELDSIZE, CFG.FIELDSIZE)));
      }
      else if (f.getLayer() > CFG.PLAYERLAYER && f.getLayer() < CFG.SUPERADDLAYER)
      {
        bump.add(new Area(new Rectangle2D.Double(f.getX(), f.getY(), CFG.FIELDSIZE, CFG.FIELDSIZE)));
      }
    }
    for (Field f : cpy)
    {
      if (f.getLayer() == CFG.PLAYERLAYER)
      {
        bump.subtract(new Area(new Rectangle2D.Double(f.getX(), f.getY(), CFG.FIELDSIZE, CFG.FIELDSIZE)));
      }
    }
    for (Field f : cpy)
    {
      if (f.getLayer() >= CFG.SUPERADDLAYER)
      {
        bump.add(new Area(new Rectangle2D.Double(f.getX(), f.getY(), CFG.FIELDSIZE, CFG.FIELDSIZE)));
      }
    }
    
    Collections.reverse(cpy);
    
    for (Field f : cpy)
    {
      if (f.getLayer() <= CFG.SUPERDELLAYER)
      {
        bump.subtract(new Area(new Rectangle2D.Double(f.getX(), f.getY(), CFG.FIELDSIZE, CFG.FIELDSIZE)));
      }
    }
    
    JSONArray npcs = data.getJSONArray("npc");
    for (int i = 0; i < npcs.length(); i++)
    {
      JSONObject o = npcs.getJSONObject(i);
      JSONObject random = o.getJSONObject("random");
      NPC npc = new NPC(o.getInt("x"), o.getInt("y"), o.getInt("w"), o.getInt("h"), o.getInt("dir"), o.getString("name"), o.getString("char"), o.getDouble("speed"), random.getBoolean("move"), random.getBoolean("look"), random.getInt("moveT"), random.getInt("lookT"), o.getBoolean("hostile"), o.getInt("id"), new Attributes(o.getJSONObject("attr")), new Equipment(o.getJSONObject("equip")), o.getJSONArray("talk"), o.getString("ai"));
      creatures.add(npc);
    }
  }
  
  public void update(long timePassed, Scene_Game sg)
  {
    for (Field f : fields)
    {
      f.update(this);
    }
    
    for (Creature c : creatures)
    {
      if (c.isAlive())
        c.update(timePassed, this);
    }
    
    if (talk != null)
    {
      talk.update();
    }
    
    // -- animations -- //
    try
    {
      for (Animation a : animations)
      {
        if (a.isDone())
          animations.remove(a);
      }
    }
    catch (Exception e)
    {}
  }
  
  public void draw(Graphics2D g, Viewport v)
  {
    g.drawImage(lrender, getX(), getY(), v.w);
    
    // -- animations -- //
    for (Animation a : animations)
    {
      if (a.isBelow())
        a.draw(this, g, v);
    }
    
    // -- field data -- //
    for (Field f : this.fields)
    {
      f.drawData(this, g, v);
    }
    try
    {
      Collections.sort(itemDrops, ItemDrop.COMPARATOR);
      for (ItemDrop id : itemDrops)
      {
        if ((hoveredItemDrop != null && id.equals(hoveredItemDrop)) || hoveredItemDrop == null)
          id.draw(this, g, v);
        
        else id.drawWithoutTooltip(this, g, v);
      }
    }
    catch (Exception e)
    {}
    Comparator<Creature> comp = new Comparator<Creature>()
    {
      public int compare(Creature o1, Creature o2)
      {
        return (int) o1.getPos().y - (int) o2.getPos().y;
      }
    };
    Collections.sort(creatures, comp);
    for (Creature c : creatures)
    {
      if (c.isAlive())
        c.draw(g, v, this);
    }
    
    // -- animations -- //
    for (Animation a : animations)
    {
      if (!a.isBelow())
        a.draw(this, g, v);
    }
    
    for (Field field : aboveFields)
    {
      if (getPlayer() != null)
      {
        if (new Vector(field.getX(), field.getY()).getDistance(getPlayer().getPos()) < CFG.FIELDSIZE * 0.8)
          g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
      }
      g.drawImage(field.getImage(), x + field.getX(), y + field.getY(), v.w);
      
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }
    
    try
    {
      for (ItemDrop id : itemDrops)
      {
        if ((hoveredItemDrop != null && id.equals(hoveredItemDrop)) || hoveredItemDrop == null)
          id.getItem().tooltip.draw(g, v);
      }
    }
    catch (Exception e)
    {}
    
    if (CFG.UIDEBUG)
    {
      Assistant.Shadow(v.w.getBounds(), Color.black, 0.4f, g);
      Assistant.Shadow(getBumpMap(), Color.white, 0.4f, g);
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
  
  public void move(double x, double y)
  {
    setX((int) (getX() + x));
    setY((int) (getY() + y));
  }
  
  public Area getBumpMap()
  {
    Area bump = getAbsoluteBumpMap();
    AffineTransform at = new AffineTransform();
    at.setToTranslation(getX() - bump.getBounds2D().getX(), getY() - bump.getBounds2D().getY());
    bump.transform(at);
    return bump;
  }
  
  public Area getAbsoluteBumpMap()
  {
    return (Area) this.bump.clone();
  }
  
  public BufferedImage getRendered(int size, Viewport v)
  {
    BufferedImage bi = new BufferedImage(size * width, size * height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) bi.getGraphics();
    draw(g, v);
    return bi;
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
  
  public void setPlayer(Player p)
  {
    creatures.add(p);
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
  
  /**
   * @param key:<br>
   *          <ul>
   *          <li>for NPCs : npc_ID</li>
   *          <li>for player: player</li>
   *          </ul>
   */
  public Creature getCreatureByAccessKey(String key)
  {
    if (key.startsWith("npc_"))
    {
      for (Creature c : creatures)
      {
        if (c instanceof NPC && ((NPC) c).getID() == Integer.parseInt(key.replace("npc_", "")))
        {
          return (NPC) c;
        }
      }
      return null;
    }
    else if (key.equals("player"))
    {
      return getPlayer();
    }
    else
    {
      return null;
    }
  }
  
  public MapPack getMapPack()
  {
    return mappack;
  }
  
  public void setMapPack(MapPack mappack)
  {
    this.mappack = mappack;
    
    itemDrops = mappack.getItemDrops(this);
  }
  
  public void playAnimation(Animation a)
  {
    animations.add(a);
    a.playAnimation();
  }
  
  public void mouseDragged(MouseEvent e)
  {
    for (Creature c : creatures)
    {
      if (c.isAlive())
        c.mouseDragged(e, this);
    }
  }
  
  public void mouseMoved(MouseEvent e)
  {
    hoveredItemDrop = null;
    for (Creature c : creatures)
    {
      if (c.isAlive())
        c.mouseMoved(e, this);
    }
    
    try
    {
      for (ItemDrop id : itemDrops)
      {
        id.mouseMoved(e, this);
        
        if (hoveredItemDrop == null && id.getArea().contains(e.getXOnScreen() - x, e.getYOnScreen() - y))
          hoveredItemDrop = id;
      }
    }
    catch (Exception e1)
    {}
  }
  
  public void mouseClicked(MouseEvent e)
  {
    for (Creature c : creatures)
    {
      if (c.isAlive())
        c.mouseClicked(e, this);
    }
  }
  
  public void mousePressed(MouseEvent e, Viewport v)
  {
    for (Creature c : creatures)
    {
      if (c.isAlive())
        c.mousePressed(e, this);
    }
    
    try
    {
      for (ItemDrop id : itemDrops)
      {
        id.mousePressed(e, this, v);
      }
    }
    catch (Exception e1)
    {}
  }
  
  public void mouseReleased(MouseEvent e)
  {
    for (Creature c : creatures)
    {
      if (c.isAlive())
        c.mouseReleased(e, this);
    }
  }
  
  public void mouseEntered(MouseEvent e)
  {
    for (Creature c : creatures)
    {
      if (c.isAlive())
        c.mouseEntered(e, this);
    }
  }
  
  public void mouseExited(MouseEvent e)
  {
    for (Creature c : creatures)
    {
      if (c.isAlive())
        c.mouseExited(e, this);
    }
  }
  
  public void keyTyped(KeyEvent e)
  {}
  
  public void keyPressed(KeyEvent e)
  {
    for (Creature c : creatures)
    {
      if (c.isAlive())
        c.keyPressed(e, this);
    }
    
    if (talk != null)
      talk.keyPressed(e);
  }
  
  public void keyReleased(KeyEvent e)
  {
    for (Creature c : creatures)
    {
      if (c.isAlive())
        c.keyReleased(e, this);
    }
  }
  
  @Override
  public void stringVarChanged(String key, String value)
  {}
  
  @Override
  public void booleanVarChanged(String key, boolean value)
  {}
  
  public static String[] getMaps(String pack, String dir)
  {
    File directory = new File(FileManager.dir, dir + "/" + pack + "/maps");
    
    if (!directory.exists())
      return null;
    
    Compressor.compileMaps(directory);
    
    File[] files = directory.listFiles(new FileFilter()
    {
      @Override
      public boolean accept(File pathname)
      {
        return pathname.isFile() && pathname.getName().endsWith(".map");
      }
    });
    return Assistant.getFileNames(files, false);
  }
  
  public boolean isPeaceful()
  {
    return peaceful;
  }
  
  public void setPeaceful(boolean peaceful)
  {
    this.peaceful = peaceful;
  }
  
  public void addItemDrop(Item item, int rx, int ry)
  {
    int highestZ = -1;
    for (ItemDrop id : itemDrops)
    {
      if (id.getArea().intersects(new Rectangle(rx, ry, ItemDrop.SIZE, ItemDrop.SIZE)) && id.getZ() > highestZ)
        highestZ = id.getZ();
    }
    ItemDrop d = new ItemDrop(item, rx, ry, highestZ + 1, getName());
    mappack.addItemDrop(d);
    itemDrops.add(d);
  }
  
  public void removeItemDrop(ItemDrop d)
  {
    mappack.removeItemDrop(d);
    itemDrops.remove(d);
  }
  
  public JSONArray serializeCreatures()
  {
    JSONArray arr = new JSONArray();
    
    for (Creature c : creatures)
    {
      if (c instanceof NPC)
        arr.put(((NPC) c).serializeNPC());
    }
    
    return arr;
  }
  
  public void loadCreatures(JSONArray arr) throws JSONException
  {
    for (int i = 0; i < arr.length(); i++)
    {
      JSONObject o = arr.getJSONObject(i);
      JSONObject random = o.getJSONObject("random");
      NPC npc = new NPC(o.getInt("x"), o.getInt("y"), o.getInt("w"), o.getInt("h"), o.getInt("dir"), o.getString("name"), o.getString("char"), o.getDouble("speed"), random.getBoolean("move"), random.getBoolean("look"), random.getInt("moveT"), random.getInt("lookT"), o.getBoolean("hostile"), o.getInt("id"), new Attributes(o.getJSONObject("attr")), new Equipment(o.getJSONObject("equip")), o.getJSONArray("talk"), o.getString("ai"));
      for (int j = 0; j < creatures.size(); j++)
      {
        if (creatures.get(j) instanceof NPC)
        {
          if (((NPC) creatures.get(j)).getID() == npc.getID())
            creatures.set(j, npc);
        }
      }
    }
  }
  
  public JSONObject serializeMap()
  {
    JSONObject o = new JSONObject();
    try
    {
      o.put("npc", serializeCreatures());
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
    return o;
  }
  
  public void loadMap(JSONObject o)
  {
    try
    {
      loadCreatures(o.getJSONArray("npc"));
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
  }
  
  public Field findField(double x, double y)
  {
    for (Field f : belowFields)
    {
      if (new Area(new Rectangle2D.Double(f.getX(), f.getY(), CFG.FIELDSIZE, CFG.FIELDSIZE)).contains(x, y))
        return f;
    }
    return null;
  }
  
  public boolean isLineAccessible(Vector from, Vector to, int offsetX, int offsetY, int width, int height)
  {
    Area line = new Area();
    
    Polygon polygon = new Polygon();
    polygon.addPoint((int) from.x, (int) from.y);
    polygon.addPoint((int) to.x, (int) to.y);
    polygon.addPoint((int) to.x, (int) to.y + height);
    polygon.addPoint((int) from.x, (int) from.y + height);
    line.add(new Area(polygon));
    
    polygon = new Polygon();
    polygon.addPoint((int) from.x + width, (int) from.y);
    polygon.addPoint((int) to.x + width, (int) to.y);
    polygon.addPoint((int) to.x + width, (int) to.y + height);
    polygon.addPoint((int) from.x + width, (int) from.y + height);
    line.add(new Area(polygon));
    
    polygon = new Polygon();
    polygon.addPoint((int) from.x, (int) from.y + height);
    polygon.addPoint((int) to.x, (int) to.y + height);
    polygon.addPoint((int) to.x + width, (int) to.y + height);
    polygon.addPoint((int) from.x + width, (int) from.y + height);
    line.add(new Area(polygon));
    
    polygon = new Polygon();
    polygon.addPoint((int) from.x, (int) from.y);
    polygon.addPoint((int) to.x, (int) to.y);
    polygon.addPoint((int) to.x + width, (int) to.y);
    polygon.addPoint((int) from.x + width, (int) from.y);
    line.add(new Area(polygon));
    
    line.transform(AffineTransform.getTranslateInstance(getX() + offsetX, getY() + offsetY));
    
    Area copy = (Area) getBumpMap().clone();
    copy.add(line);
    
    return copy.equals(getBumpMap());
  }
  
  public Field[] getNeighbors(Field f)
  {
    ArrayList<Field> neighbors = new ArrayList<>();
    for (Field field : fields)
    {
      if (f.getNode().getDistance(field.getNode()) <= CFG.FIELDSIZE + 1 && field.getLayer() < CFG.PLAYERLAYER && bump.contains(field.getX(), field.getY(), CFG.FIELDSIZE, CFG.FIELDSIZE))
        neighbors.add(field);
    }
    
    return neighbors.toArray(new Field[] {});
  }
  
  public ArrayList<ItemDrop> getItemDrops()
  {
    return itemDrops;
  }
}
