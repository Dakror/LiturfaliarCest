package de.dakror.liturfaliar;

import java.awt.Point;

/**
 * Static global Config class.
 * 
 * @author Dakror
 */
public final class CFG
{ 
  // -- UniVersion -- //
  public static final int      VERSION           = 2013051212;
  public static final int      PHASE             = 0;
  // -- debug -- //
  public static boolean        UIDEBUG           = false;
  public static boolean        DEBUG             = true;
  
  public static boolean        HELPOVERLAYCREATE = false;
  public static boolean        GAMEUPDATE        = true;
  public final static boolean  MAPEDITOR         = true;
  
  public final static double   PLAYERLAYER       = 1.0;
  public final static double   SUPERADDLAYER     = 1000.0;
  public final static double   SUPERDELLAYER     = -1000.0;
  
  public final static String   MAPPACK           = "Liturfaliar Cest";
  public final static String   WINDOWTITLE       = "Liturfaliar Cest";
  
  public final static String[] BODY              = { "skin", "hair", "eyes", "shirt", "trouser", "hat", "gloves", "boots" };
  public final static String[] MUSIC             = { "013-Theme02", "025-Town03", };
  public final static String[] SOUND             = { "001-System01", "002-System02", "003-System03", "004-System04", "005-System05", "006-System06", "007-System07", "008-System08", "009-System09", "010-System10", "011-System11", "012-System12", "013-Move01", "014-Move02", "015-Jump01", "016-Jump02", "017-Jump03", "018-Teleport01", "019-Teleport02", "020-Teleport03", "021-Dive01", "022-Dive02", "023-Dive03", "024-Door01", "025-Door02", "026-Door03", "027-Door04", "028-Door05", "029-Door06", "030-Door07", "031-Door08", "032-Switch01", "033-Switch02", "034-Switch03", "035-Switch04", "036-Switch05", "037-Switch06", "038-Switch07", "039-Switch08", "040-Knock01", "041-Knock02", "042-Knock03", "043-Knock04", "044-Chest01", "045-Push01", "046-Book01", "047-Book02", "048-Explosion01", "049-Explosion02", "050-Explosion03", "051-Explosion04", "052-Cannon01", "053-Cannon02", "054-Cannon03", "055-Right01", "056-Right02", "057-Wrong01", "058-Wrong02", "059-Applause01", "060-Cheer01", "061-Thunderclap01", "062-Swing01", "063-Swing02", "064-Swing03", "065-Swing04", "066-Animal01", "067-Animal02", "068-Animal03", "069-Animal04", "070-Animal05", "071-Animal06", "072-Animal07", "073-Animal08", "074-Small01", "075-Small02", "076-Small03", "077-Small04", "078-Small05", "079-Monster01", "080-Monster02", "081-Monster03", "082-Monster04", "083-Monster05", "084-Monster06", "085-Monster07", "086-Action01", "087-Action02", "088-Action03", "089-Attack01", "090-Attack02", "091-Attack03", "092-Attack04", "093-Attack05", "094-Attack06", "095-Attack07", "096-Attack08", "097-Attack09", "098-Attack10", "099-Attack11", "100-Attack12", "101-Attack13", "102-Attack14", "103-Attack15", "104-Attack16", "105-Heal01", "106-Heal02", "107-Heal03", "108-Heal04", "109-Heal05", "110-Heal06", "111-Heal07", "112-Heal08", "113-Remedy01", "114-Remedy02", "115-Raise01", "116-Raise02", "117-Fire01", "118-Fire02", "119-Fire03", "120-Ice01", "121-Ice02", "122-Ice03", "123-Thunder01", "124-Thunder02", "125-Thunder03", "126-Water01", "127-Water02", "128-Water03", "129-Earth01", "130-Earth02", "131-Earth03", "132-Wind01", "133-Wind02", "134-Wind03", "135-Light01", "136-Light02", "137-Light03", "138-Darkness01", "139-Darkness02", "140-Darkness03", "141-Burst01", "142-Burst02", "143-Support01", "144-Support02", "145-Support03", "146-Support04", "147-Support05", "148-Support06", "149-Support07", "150-Support08", "151-Support09", "152-Support10", "153-Support11", "154-Support12", "155-Support13", "156-Support14", "157-Skill01", "158-Skill02", "159-Skill03", "160-Skill04", "161-Skill05", "162-Skill06", "163-Skill07", "164-Skill08", "165-Skill09", "166-Skill10", "167-Skill11", "168-Skill12", "169-Skill13", "170-Skill14", "171-Skill15", "172-Skill16", "173-Skill17", "174-Skill18", "175-Skill19", "176-Skill20", "177-Skill21", "178-Skill22", "179-Skill23", "180-Skill24", "181-Hover", "182-Click", "183-Beep" };
  
  public final static int      MAINMENU_BG       = 37;
  public final static int      FIELDSIZE         = 32;
  public final static int      DECOSIZE          = 32;
  public final static int      ANIMATIONS        = 59;
  public final static int      TILES             = 123;
  
  public final static int[]    HUMANBOUNDS       = { FIELDSIZE - 3, FIELDSIZE * 3 / 2 - 3 };
  public static Point          MAPCENTER         = new Point(0, 0);
  
  /* Unused music files: "001-Battle01", "002-Battle02", "003-Battle03", "004-Battle04", "005-Boss01", "006-Boss02", "007-Boss03", "008-Boss04", "009-LastBoss01", "010-LastBoss02", "011-LastBoss03", "012-Theme01", "014-Theme03", "015-Theme04", "016-Theme05", "017-Theme06", "018-Field01", "019-Field02", "020-Field03", "021-Field04", "022-Field05", "023-Town01", "024-Town02","026-Town04", "027-Town05", "028-Town06", "029-Town07", "030-Town08", "031-Castle01", "032-Church01", "033-Ship01", "034-Heaven01", "035-Dungeon01", "036-Dungeon02", "037-Dungeon03", "038-Dungeon04", "039-Dungeon05", "040-Dungeon06", "041-Dungeon07", "042-Dungeon08", "043-Positive01", "044-Positive02", "045-Positive03", "046-Positive04", "047-Positive05", "048-Positive06", "049-Positive07", "050-Positive08", "051-Positive09", "052-Negative01", "053-Negative02", "054-Negative03", "055-Negative04", "056-Negative05", "057-Negative06", "058-Slow01", "059-Slow02", "060-Slow03", "061-Slow04", "062-Slow05", "063-Slow06", "064-Slow07" */
  public static void p(Object p)
  {
    System.out.println(p);
  }
  
  public static void b(Object... b)
  {
    String s = "";
    for (int i = 0; i < b.length; i += 2)
    {
      s += b[i] + ": " + b[i + 1] + ",";
    }
    p(s.substring(0, s.length() - 1));
  }
}
