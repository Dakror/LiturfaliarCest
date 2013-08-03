package de.dakror.liturfaliar.map.data;

import java.awt.Graphics2D;
import java.awt.Image;

import org.json.JSONObject;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.event.Event;
import de.dakror.liturfaliar.event.Events;
import de.dakror.liturfaliar.map.Field;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.Creature;
import de.dakror.liturfaliar.map.creature.Player;
import de.dakror.liturfaliar.settings.CFG;

public class Door implements FieldData
{
  public static final String[] ARGS  = { "int_x", "int_y", "int_dx", "int_dy", "int_dir", "int_t", "string_map", "string_img", "string_sound" };
  public static final String[] CHARS = { "170-Door01", "171-Door02", "172-Door03", "173-Door04" };
  public int                   x;
  public int                   y;
  public int                   t;
  public int                   dir;
  public int                   dx;
  public int                   dy;
  public String                map;
  public String                img;
  public String                sound;
  private Image                image;
  private Map                  dest;
  private long                 timeStart;
  private Creature             c;
  private long                 soundlength;
  
  @Override
  public void onEvent(Event e)
  {
    if (!e.equals(Events.fieldTriggered)) return;
    Creature c = (Creature) e.getParam("c");
    if (c instanceof Player && timeStart == 0)
    {
      timeStart = System.currentTimeMillis();
      c.setFrozen(true);
      c.setDir(c.getDir());
      this.c = c;
      if (soundlength == 0)
      {
        doTeleport((Map) e.getParam("map"));
        c.setFrozen(false);
      }
    }
  }
  
  private void doTeleport(Map m)
  {
    dest = new Map(CFG.MAPPACK, map);
    dest.setPos(CFG.MAPCENTER.x - dx, CFG.MAPCENTER.y - dy);
    m.getMapPack().setActiveMap(dest);
    ((Player) c).setPos(dx, dy);
    ((Player) c).setTarget(dx, dy);
    if (dir != -1) c.setDir(dir);
  }
  
  @Override
  public void update(Map m, Field f)
  {
    if (timeStart > 0)
    {
      double x = System.currentTimeMillis() - timeStart;
      m.alpha = 0.5f * (float) (Math.cos((2 * Math.PI) / (double) soundlength * x) + 1);
      if (Math.abs(x - soundlength / 2.0) < soundlength / 100.0)
      {
        doTeleport(m);
        c.setFrozen(false);
      }
    }
  }
  
  @Override
  public void draw(Map m, Field f, Graphics2D g, Viewport v)
  {
    if (System.currentTimeMillis() == timeStart) v.playSound(sound);
    if (image == null) return;
    int w = image.getWidth(null) / 4;
    int h = image.getHeight(null) / 4;
    int x = m.getX() + f.getX();
    int y = m.getY() + f.getY() - (h - CFG.FIELDSIZE);
    int fr = (System.currentTimeMillis() - timeStart > 0 && System.currentTimeMillis() - timeStart < soundlength / 2) ? v.getFrame(timeStart) % 4 : 0;
    g.drawImage(image, x, y, x + w, y + h, w * t, fr * h, w, fr * h + h, v.w);
  }
  
  @Override
  public void loadData(JSONObject data) throws Exception
  {
    for (String s : ARGS)
    {
      String name = s.substring(s.indexOf("_") + 1);
      java.lang.reflect.Field f = getClass().getField(name);
      f.set(this, data.get(name));
    }
    timeStart = 0;
    if (img.length() > 0) image = Viewport.loadImage("char/objects/" + img + ".png");
    if (sound.length() > 0) soundlength = (long) Viewport.getSoundLength(sound);
    else soundlength = 0;
  }
  // String to; Point where; String sound; String file; int type; Field f; boolean active; boolean soundplayed; long time; public void init(Field f) { this.f = f; soundplayed = false; time = 0; } public void fieldTouched(Creature c) {} public void loadData(JSONObject fielddata) { try { to = fielddata.getString("goto"); where = new Point(fielddata.getInt("x"), fielddata.getInt("y")); sound = fielddata.getString("sound"); file = (fielddata.getInt("char") > 0) ? CHARS[(fielddata.getInt("char") - 1) / 4] : null; type = (fielddata.getInt("char") - 1) % 4; } catch (JSONException e) { e.printStackTrace(); } } public void update(Map m, Scene_Game sg) { if (time != 0) { m.getPlayer().setFreezed(true); } if (active && soundplayed) { Map to = new Map(sg.getMapPack().getName(), this.to); to.setPos(CFG.MAPCENTER.x - where.x, CFG.MAPCENTER.y - where.y); sg.getMapPack().setActiveMap(to); m.getPlayer().setRelativePos(where.x, where.y); m.getPlayer().setTarget(where.x, where.y); m.getPlayer().setFreezed(false); } } public void drawDown(Graphics2D g, Viewport v, Map m) { if (active && !soundplayed) { if (sound.length() > 0) { if (time == 0) { v.playSound(sound); time = System.currentTimeMillis(); } } else { soundplayed = true; } } if (time > 0) { double t = (System.currentTimeMillis() - time) - v.getSoundLength(sound) / 2.0; if (t > -30) { soundplayed = true; } else if (System.currentTimeMillis() - time < v.getSoundLength(sound) / 2.0f) { m.alpha = 1 - (float) ((System.currentTimeMillis() - time) / (0.5f * v.getSoundLength(sound))); } else if (System.currentTimeMillis() - time > v.getSoundLength(sound) / 2.0f) { m.alpha = (float) ((System.currentTimeMillis() - time) / (v.getSoundLength(sound))); } } if (file != null) { Image i = Viewport.loadImage("char/objects/" + file + ".png"); int x = f.getX() * CFG.FIELDSIZE + m.getX() - (i.getWidth(null) / 4 - CFG.FIELDSIZE) / 2; int y = f.getY() * CFG.FIELDSIZE + m.getY() - (i.getHeight(null) / 4 - CFG.FIELDSIZE); Assistant.drawChar(x, y, i.getWidth(null) / 4, i.getHeight(null) / 4, type, 0, "objects", file, g, v.w, false); } } public void drawUp(Graphics2D g, Viewport v, Map m) {} public void fieldTriggered(Creature c) { if (c instanceof Player) { active = true; } } public void talkStarted(Talk t) {} public void talkEnded(Talk t) {} public void talkChanged(Talk old, Talk n) {}
}
