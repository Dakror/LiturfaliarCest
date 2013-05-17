package de.dakror.liturfaliar.map.creature;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import org.json.JSONArray;

import de.dakror.liturfaliar.CFG;
import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.fx.Emoticon;
import de.dakror.liturfaliar.map.Map;
import de.dakror.liturfaliar.ui.Talk;
import de.dakror.liturfaliar.util.Assistant;
import de.dakror.liturfaliar.util.Database;
import de.dakror.liturfaliar.util.Vector;

public class NPC extends Creature
{
  public static final String[] CHARS = { "001-Fighter01", "002-Fighter02", "003-Fighter03", "004-Fighter04", "005-Fighter05", "006-Fighter06", "007-Fighter07", "008-Fighter08", "009-Lancer01", "010-Lancer02", "011-Lancer03", "012-Lancer04", "013-Warrior01", "014-Warrior02", "015-Warrior03", "016-Thief01", "017-Thief02", "018-Thief03", "019-Thief04", "020-Hunter01", "021-Hunter02", "022-Hunter03", "023-Gunner01", "024-Gunner02", "025-Cleric01", "026-Cleric02", "027-Cleric03", "028-Cleric04", "029-Cleric05", "030-Cleric06", "031-Cleric07", "032-Cleric08", "033-Mage01", "034-Mage02", "035-Mage03", "036-Mage04", "037-Mage05", "038-Mage06", "039-Mage07", "040-Mage08", "041-Mage09", "042-King01", "043-Queen01", "044-Trader01", "045-Fortuneteller01", "046-Grappler01", "047-Grappler02", "048-Fairy01", "049-Soldier01", "050-Soldier02", "051-Undead01", "052-Undead02", "053-Undead03", "054-Undead04", "055-Snake01", "056-Snake02", "057-Snake03", "058-Snake04", "059-Aquatic01", "060-Aquatic02", "061-Aquatic03", "062-Aquatic04", "063-Beast01", "064-Beast02", "065-Beast03", "066-Beast04", "067-Goblin01", "068-Goblin02", "069-Goblin03", "070-Goblin04", "071-Bird01", "072-Bird02", "073-Bird03", "074-Bird04", "075-Devil01", "076-Devil02", "077-Devil03", "078-Devil04", "079-Angel01", "080-Angel02", "081-Angel03", "082-Angel04", "083-Elemental01", "084-Elemental02", "085-Elemental03", "086-Elemental04", "087-Monster01", "088-Monster02", "089-Monster03", "090-Monster04", "091-Monster05", "092-Monster06", "094-Monster08", "095-Monster09", "096-Monster10", "097-Monster11", "098-Monster12", "099-Monster13", "100-Monster14", "101-Civilian01", "102-Civilian02", "103-Civilian03", "104-Civilian04", "105-Civilian05", "106-Civilian06", "107-Civilian07", "108-Civilian08", "109-Civilian09", "110-Civilian10", "111-Civilian11", "112-Civilian12", "113-Civilian13", "114-Civilian14", "115-Civilian15", "116-Civilian16", "117-Civilian17", "118-Civilian18", "119-Civilian19", "120-Civilian20", "121-Civilian21", "122-Civilian22", "123-Civilian23", "124-Civilian24", "125-Baby01", "126-Noble01", "127-Noble02", "128-Noble03", "129-Noble04", "130-Noble05", "131-Noble06", "132-Noble07", "133-Noble08", "134-Butler01", "135-Maid01", "136-Bartender01", "137-BunnyGirl01", "138-Cook01", "139-Clown01", "140-Dancer01", "141-Bard01", "142-Scholar01", "143-Farmer01", "144-Farmer02", "145-Prisoner01", "146-Prisoner02", "147-Storekeeper01", "148-Storekeeper02", "149-Captain01", "150-Sailor01", "151-Animal01", "152-Animal02", "153-Animal03", "154-Animal04", "155-Animal05", "156-Animal06", "157-Animal07", "158-Animal08", "159-Small01", "160-Small02", "161-Small03", "162-Small04", "163-Small05", "164-Small06", "165-Small07", "166-Small08", "167-Small09", "168-Small10", "169-Small11", "188-Wagon01", "189-Down01", "190-Down02", "191-Down03", "192-Down04", "femaleTemplate", "maleTemplate" };
  boolean                      random;
  int                          randommovedelay;
  private String               name;
  String                       character;
  long                         time;
  int                          ID;
  private boolean              hostile;
  private JSONArray            talkdata;
  private boolean              talking;
  private Emoticon             emoticon;
  Vector[]                     playerTalkTo;
  
  public NPC(int x, int y, int w, int h, String name, String character, double speed, boolean random, int randommovedelay, int id, JSONArray talkdata)
  {
    super(x, y, w, h);
    setName(name);
    this.character = character;
    layer = CFG.PLAYERLAYER;
    setSpeed(speed);
    frozen = false;
    this.random = random;
    this.randommovedelay = randommovedelay;
    massive = true;
    by = CFG.FIELDSIZE;
    bh = CFG.FIELDSIZE / 2;
    dir = 0;
    ID = id;
    emoticon = null;
    setTalkData(talkdata);
    setHostile(false);
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
      angle = Math.toDegrees(Math.atan2(goTo.coords[1] - getPos()[1], goTo.coords[0] - getPos()[0]));
      dir = 0;
      try
      {
        dir = DIRS[(int) Math.round((angle + 10) / 90.0) + 1];
      }
      catch (Exception e)
      {}
    }
    Assistant.drawChar(getPos()[0] + m.getX(), getPos()[1] + m.getY(), w, h, dir, (move) ? v.getFrame() % 4 : 0, "chars", character, g, v.w, true);
    if (emoticon != null)
    {
      emoticon.draw(g, m, v);
      if (emoticon.done && emoticon.getType() != 50)
        emoticon = null;
    }
  }
  
  @Override
  public void update(Map m)
  {
    super.update(m);
    if (isRandomMovementEnabled() && getDistance() < getSpeed() && System.currentTimeMillis() - time > randommovedelay)
    {
      int rx = (int) Math.round(Math.random() * 2) - 1;
      int ry = (int) Math.round(Math.random() * 2) - 1;
      if (m.getBumpMap().contains(new Rectangle2D.Double(m.getX() + getRelativePos(m)[0] + rx * CFG.FIELDSIZE, m.getY() + getRelativePos(m)[1] + ry * CFG.FIELDSIZE, w, h)))
      {
        setTarget(getRelativePos(m)[0] + rx * CFG.FIELDSIZE, getRelativePos(m)[1] + ry * CFG.FIELDSIZE);
        time = System.currentTimeMillis();
      }
    }
    if (m.getPlayer().getField(m).distance(getField(m)) < 1.1 && m.getPlayer().isLookingAt(this, m))
    {
      if (emoticon == null)
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
  }
  
  @Override
  public void mousePressed(MouseEvent e, Map m)
  {
    if (m.getPlayer().getField(m).distance(getField(m)) < 1.3 && m.getPlayer().isLookingAt(this, m) && e.getButton() == 1 && m.talk == null)
    {
      setTalking(true);
      m.talk = new Talk(this, m);
      lookAt(m.getPlayer(), m);
    }
  }
  
  public boolean isRandomMovementEnabled()
  {
    return random;
  }
  
  public void setRandomMovementEnabled(boolean random)
  {
    this.random = random;
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
  
  public void setEmoticon(int type, boolean animate, long length)
  {
    emoticon = new Emoticon(this, type, animate, length);
  }
  
  public Emoticon getEmoticon()
  {
    return emoticon;
  }
  
  public boolean isTalking()
  {
    return talking;
  }
  
  public void setTalking(boolean talking)
  {
    this.talking = talking;
  }
}