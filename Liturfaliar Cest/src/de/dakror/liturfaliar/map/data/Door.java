package de.dakror.liturfaliar.map.data;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;

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
  public static final String[] ARGS  = { "int_x", "int_y", "int_dx", "int_dy", "int_dir", "int_arr", "int_t", "string_map", "string_img", "string_sound" };
  public static final String[] CHARS = { "170-Door01", "171-Door02", "172-Door03", "173-Door04" };
  public int                   x, y, t, dir, arr, dx, dy;
  private long                 timeStart, soundlength, time;
  public String                map, img, sound;
  private Image                image;
  private Map                  dest;
  private Creature             c;
  
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
    if (!map.equals(m.getName()))
    {
      dest = new Map(CFG.MAPPACK, map);
      m.getMapPack().setActiveMap(dest);
    }
    ((Player) c).setPos(dx, dy);
    ((Player) c).setTarget(dx, dy);
    if (dir != -1) c.setDir(dir);
    timeStart = 0;
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
  public void draw(Map m, Field f, Graphics2D g)
  {
    if (time == 0 && arr != -1) time = System.currentTimeMillis();
    
    if (System.currentTimeMillis() == timeStart) Viewport.playSound(sound);
    if (arr != -1)
    {
      // down - left - right - up
      double tr = 3 * Math.cos((System.currentTimeMillis() - time) / 300.0) + 3;
      AffineTransform at = AffineTransform.getTranslateInstance(m.getX() + f.getX() - CFG.FIELDSIZE * 0.3 + ((arr == 1 || arr == 2) ? tr : 0), m.getY() + f.getY() - CFG.FIELDSIZE * 0.3 + ((arr == 0 || arr == 3) ? tr : 0));
      at.rotate(Math.toRadians(new int[] { 0, 90, 270, 180 }[arr]), CFG.FIELDSIZE * 0.7, CFG.FIELDSIZE * 0.7);
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (0.25f * Math.cos((System.currentTimeMillis() - time) / 300.0) + 0.75f)));
      g.drawImage(Viewport.loadScaledImage("system/wArrowGlow.png", (int) (CFG.FIELDSIZE * 1.4), (int) (CFG.FIELDSIZE * 1.4), Image.SCALE_SMOOTH), at, Viewport.w);
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
      
      g.drawImage(Viewport.loadScaledImage("system/wArrow.png", (int) (CFG.FIELDSIZE * 1.4), (int) (CFG.FIELDSIZE * 1.4), Image.SCALE_SMOOTH), at, Viewport.w);
    }
    if (image == null) return;
    int w = image.getWidth(null) / 4;
    int h = image.getHeight(null) / 4;
    int x = m.getX() + f.getX();
    int y = m.getY() + f.getY() - (h - CFG.FIELDSIZE);
    int fr = (System.currentTimeMillis() - timeStart > 0 && System.currentTimeMillis() - timeStart < soundlength / 2) ? Viewport.getFrame(timeStart) % 4 : 0;
    g.drawImage(image, x, y, x + w, y + h, w * t, fr * h, w, fr * h + h, Viewport.w);
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
}
