package de.dakror.liturfaliar.map.creature;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.item.Equipment;
import de.dakror.liturfaliar.item.skillanim.SkillAnimation;
import de.dakror.liturfaliar.item.skillanim.Sword0;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.map.creature.ai.CreatureAI;
import de.dakror.liturfaliar.settings.Attributes;
import de.dakror.liturfaliar.settings.CFG;
import de.dakror.liturfaliar.settings.DamageType;
import de.dakror.liturfaliar.ui.Talk;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.Database;
import de.dakror.liturfaliar.util.Vector;

public class NPC extends Creature
{
  public static final String[] CHARS = { "001-Fighter01", "002-Fighter02", "003-Fighter03", "004-Fighter04", "005-Fighter05", "006-Fighter06", "007-Fighter07", "008-Fighter08", "009-Lancer01", "010-Lancer02", "011-Lancer03", "012-Lancer04", "013-Warrior01", "014-Warrior02", "015-Warrior03", "016-Thief01", "017-Thief02", "018-Thief03", "019-Thief04", "020-Hunter01", "021-Hunter02", "022-Hunter03", "023-Gunner01", "024-Gunner02", "025-Cleric01", "026-Cleric02", "027-Cleric03", "028-Cleric04", "029-Cleric05", "030-Cleric06", "031-Cleric07", "032-Cleric08", "033-Mage01", "034-Mage02", "035-Mage03", "036-Mage04", "037-Mage05", "038-Mage06", "039-Mage07", "040-Mage08", "041-Mage09", "042-King01", "043-Queen01", "044-Trader01", "045-Fortuneteller01", "046-Grappler01", "047-Grappler02", "048-Fairy01", "049-Soldier01", "050-Soldier02", "051-Undead01", "052-Undead02", "053-Undead03", "054-Undead04", "055-Snake01", "056-Snake02", "057-Snake03", "058-Snake04", "059-Aquatic01", "060-Aquatic02", "061-Aquatic03", "062-Aquatic04", "063-Beast01", "064-Beast02", "065-Beast03", "066-Beast04", "067-Goblin01", "068-Goblin02", "069-Goblin03", "070-Goblin04", "071-Bird01", "072-Bird02", "073-Bird03", "074-Bird04", "075-Devil01", "076-Devil02", "077-Devil03", "078-Devil04", "079-Angel01", "080-Angel02", "081-Angel03", "082-Angel04", "083-Elemental01", "084-Elemental02", "085-Elemental03", "086-Elemental04", "087-Monster01", "088-Monster02", "089-Monster03", "090-Monster04", "091-Monster05", "092-Monster06", "094-Monster08", "095-Monster09", "096-Monster10", "097-Monster11", "098-Monster12", "099-Monster13", "100-Monster14", "101-Civilian01", "102-Civilian02", "103-Civilian03", "104-Civilian04", "105-Civilian05", "106-Civilian06", "107-Civilian07", "108-Civilian08", "109-Civilian09", "110-Civilian10", "111-Civilian11", "112-Civilian12", "113-Civilian13", "114-Civilian14", "115-Civilian15", "116-Civilian16", "117-Civilian17", "118-Civilian18", "119-Civilian19", "120-Civilian20", "121-Civilian21", "122-Civilian22", "123-Civilian23", "124-Civilian24", "125-Baby01", "126-Noble01", "127-Noble02", "128-Noble03", "129-Noble04", "130-Noble05", "131-Noble06", "132-Noble07", "133-Noble08", "134-Butler01", "135-Maid01", "136-Bartender01", "137-BunnyGirl01", "138-Cook01", "139-Clown01", "140-Dancer01", "141-Bard01", "142-Scholar01", "143-Farmer01", "144-Farmer02", "145-Prisoner01", "146-Prisoner02", "147-Storekeeper01", "148-Storekeeper02", "149-Captain01", "150-Sailor01", "151-Animal01", "152-Animal02", "153-Animal03", "154-Animal04", "155-Animal05", "156-Animal06", "157-Animal07", "158-Animal08", "159-Small01", "160-Small02", "161-Small03", "162-Small04", "163-Small05", "164-Small06", "165-Small07", "166-Small08", "167-Small09", "168-Small10", "169-Small11", "188-Wagon01", "189-Down01", "190-Down02", "191-Down03", "192-Down04", "femaleTemplate", "maleTemplate" };
  
  private boolean              hostile;
  private boolean              talking;
  private boolean              randomMove;
  private boolean              randomLook;
  
  private int                  randomMoveT;
  private int                  randomLookT;
  
  private JSONArray            talkdata;
  private String               name;
  
  int                          ID;
  long                         time;
  long                         time2;
  
  String                       character;
  Vector[]                     playerTalkTo;
  
  public NPC(int x, int y, int w, int h, int d, String name, String c, double speed, boolean move, boolean look, int moveT, int lookT, boolean hostile, int id, Attributes attributes, Equipment equip, JSONArray talkdata, String aiName)
  {
    super(x, y, w, h);
    
    setHuman();
    
    attr = attributes;
    
    BufferedImage chr = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) chr.getGraphics();
    
    if (equip.isProperlyFilled())
    {
      equipment = equip;
      Assistant.drawChar(0, 0, w, h, 0, 0, equipment, g, null, true);
    }
    else
    {
      // still setting equipment, may contain weapons
      equipment = equip;
      character = c;
      Assistant.drawChar(0, 0, w, h, 0, 0, "chars", character, g, null, true);
    }
    
    hitArea = Assistant.toArea(chr);
    
    layer = CFG.PLAYERLAYER;
    frozen = false;
    randomMove = move;
    randomLook = look;
    randomMoveT = moveT;
    randomLookT = lookT;
    massive = true;
    by = CFG.FIELDSIZE;
    bh = CFG.FIELDSIZE / 2;
    dir = d;
    ID = id;
    emoticon = null;
    
    try
    {
      AI = (CreatureAI) Class.forName("de.dakror.liturfaliar.map.creature.ai." + aiName).getDeclaredConstructors()[0].newInstance(this);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    
    setName(name);
    setSpeed(speed);
    setTalkData(talkdata);
    setHostile(hostile);
    
  }
  
  @Override
  public void draw(Graphics2D g, Viewport v, Map m)
  {
    super.draw(g, v, m);
    
    boolean move = false;
    double angle = 0;
    if (getDistance() > getSpeed() && !frozen)
    {
      move = true;
      angle = Math.toDegrees(Math.atan2(goTo.y - getRelativePos().y, goTo.x - getRelativePos().x));
      dir = 0;
      try
      {
        int index = (int) Math.round(angle / 90.0) + 1;
        if (index > -1)
          dir = DIRS[index];
        
        else dir = 1;
        
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    
    for (SkillAnimation skill : skills)
      skill.drawBelow(g, v, m);
    
    if (character != null)
      Assistant.drawChar((int) getRelativePos().x + m.getX(), (int) getRelativePos().y + m.getY(), w, h, dir, (move) ? v.getFrame() % 4 : 0, "chars", character, g, v.w, true);
    else Assistant.drawChar((int) getRelativePos().x + m.getX(), (int) getRelativePos().y + m.getY(), w, h, dir, (move) ? v.getFrame() % 4 : 0, equipment, g, v.w, true);
    
    if (emoticon != null)
    {
      emoticon.draw(g, m, v);
      if (emoticon.done && emoticon.getType() != 50)
        emoticon = null;
    }
    
    for (SkillAnimation skill : skills)
      skill.drawAbove(g, v, m);
  }
  
  @Override
  public void update(long timePassed, Map m)
  {
    super.update(timePassed, m);
    
    if (isRandomMoveEnabled() && getDistance() < getSpeed() && System.currentTimeMillis() - time > randomMoveT)
    {
      int direction = (int) Math.round(Math.random() * 3);
      // 0 = left, 1 = up, 2 = right, 3 = down
      int distance = (int) Math.round((Math.random() * CFG.FIELDSIZE * getSpeed()));
      
      int x = (int) getRelativePos().x + bx;
      int y = (int) getRelativePos().y + by;
      int tx = 0;
      int ty = 0;
      
      Area tr = null;
      
      switch (direction)
      {
        case 0:
        {
          tr = new Area(new Rectangle2D.Double(x - distance, y, distance + bw, bh));
          tx = -distance;
          break;
        }
        case 1:
        {
          tr = new Area(new Rectangle2D.Double(x, y - distance, bw, distance + bh));
          ty = -distance;
          break;
        }
        case 2:
        {
          tr = new Area(new Rectangle2D.Double(x, y, distance + bw, bh));
          tx = distance;
          break;
        }
        case 3:
        {
          tr = new Area(new Rectangle2D.Double(x, y, bw, distance + bh));
          ty = distance;
          break;
        }
      }
      
      tr.transform(AffineTransform.getTranslateInstance(m.getX(), m.getY()));
      
      if (m.getBumpMap().contains(tr.getBounds2D()))
      {
        setTarget(x + tx - bx, y + ty - by);
        time = System.currentTimeMillis();
      }
    }
    
    if (isRandomLookEnabled() && System.currentTimeMillis() - time > randomLookT)
    {
      dir = (int) Math.round(Math.random() * 3);
      time = System.currentTimeMillis();
    }
    
    if (m.getPlayer().getField(m) == null)
      return;
    
    if (m.getPlayer().getField(m).getNode().getDistance(getField(m).getNode()) < 1.1 && m.getPlayer().isLookingAt(this, m) && talkdata.length() > 0)
    {
      if (emoticon == null && !isTalking())
      {
        frozen = true;
        setEmoticon(50, true, 400);
      }
    }
    else
    {
      frozen = false;
      emoticon = null;
    }
    
    if (hostile && AI != null)
    {
      path = AI.findPath(m.getPlayer().getRelativePos());
      if (relPos.equals(path.getNode()))
        path.setNodeReached();
      
      goTo = path.getNode();
      
      if (AI.canAttack(m.getPlayer()) && !isPlayingSkill(new Sword0()) && System.currentTimeMillis() - time2 > 500)
      {
        playSkill(equipment.getFirstWeapon(), new Sword0());
        time2 = System.currentTimeMillis();
      }
    }
  }
  
  @Override
  public void mousePressed(MouseEvent e, Map m)
  {
    if (m.getPlayer().getField(m).getNode().getDistance(getField(m).getNode()) < 1.3 && m.getPlayer().isLookingAt(this, m) && e.getButton() == 1 && getArea().contains(new Point(e.getX() - m.getX(), e.getY() - m.getY())) && talkdata.length() > 0)
    {
      if (m.talk == null)
      {
        setTalking(true);
        m.talk = new Talk(this, m);
        emoticon = null;
        lookAt(m.getPlayer(), m);
      }
      else if (m.talk.getBy().equals(this))
        m.talk.triggerNext();
    }
  }
  
  public boolean isRandomMoveEnabled()
  {
    return randomMove;
  }
  
  public void setRandomMoveEnabled(boolean random)
  {
    randomMove = random;
  }
  
  public boolean isRandomLookEnabled()
  {
    return randomLook;
  }
  
  public void setRandomLookEnabled(boolean random)
  {
    randomLook = random;
  }
  
  public boolean isHostile()
  {
    return hostile;
  }
  
  public void setHostile(boolean hostile)
  {
    this.hostile = hostile;
  }
  
  public JSONArray getTalkData()
  {
    return talkdata;
  }
  
  public void setTalkData(JSONArray talkdata)
  {
    this.talkdata = talkdata;
  }
  
  public String getName()
  {
    return name;
  }
  
  public void setName(String name)
  {
    this.name = name;
    Database.setStringVar("npc_" + ID, name);
  }
  
  @Override
  public String getCharacter()
  {
    return character;
  }
  
  public boolean isTalking()
  {
    return talking;
  }
  
  public void setTalking(boolean talking)
  {
    this.talking = talking;
  }
  
  public JSONObject serializeNPC()
  {
    JSONObject data = new JSONObject();
    try
    {
      data.put("x", getRelativePos().x);
      data.put("y", getRelativePos().y);
      data.put("w", w);
      data.put("h", h);
      data.put("id", ID);
      data.put("dir", dir);
      data.put("name", name);
      data.put("char", (character != null) ? character : "");
      data.put("speed", getSpeed());
      data.put("talk", talkdata);
      data.put("attr", attr.serializeAttributes());
      data.put("equip", equipment.serializeEquipment());
      data.put("ai", AI.getClass().getSimpleName());
      data.put("hostile", hostile);
      
      JSONObject random = new JSONObject();
      random.put("move", randomMove);
      random.put("look", randomLook);
      random.put("moveT", randomMoveT);
      random.put("lookT", randomLookT);
      data.put("random", random);
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
    return data;
  }
  
  public int getID()
  {
    return ID;
  }
  
  public void setID(int iD)
  {
    ID = iD;
  }
  
  @Override
  public void dealDamage(Creature causer, DamageType type, Integer damage)
  {
    super.dealDamage(causer, type, damage);
    if (causer instanceof Player)
    {
      setHostile(true);
      frozen = false;
      path = AI.findPath(causer.getTrackingNode());
      goTo = path.getNode();
    }
  }
}
