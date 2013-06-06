package de.dakror.liturfaliar.item;


public enum Items
{
  POCKETKNIFE(1, 274, "Taschenmesser", "", Types.SWORD, 3),
  BOYSHIRT(12, 2, "Jungen-Oberteil", "1_gray", Types.SHIRT, 0),
  GIRLSHIRT(12, 2, "Leichtes Kleid", "16_black", Types.SHIRT, 0),
  BOYPANTS(7, 391, "Hose", "1_brown", Types.PANTS, 0),
  BOYBOOTS(0, 3, "Schuhe", "1_black", Types.PANTS, 0),
  GIRLBOOTS(0, 3, "Halbschuhe", "8_black", Types.PANTS, 0);
  
  private int ix, iy, damage;
  private String name, charPath;
  private Types  type;
  
  private Items(int ix, int iy, String name, String path, Types type, int dmg)
  {
    this.ix = ix;
    this.iy = iy;
    this.name = name;
    this.charPath = path;
    this.type = type;
    this.damage = dmg;
  }
  
  public int getIconX()
  {
    return ix;
  }
  
  public int getIconY()
  {
    return iy;
  }
  
  public int getDamage()
  {
    return damage;
  }
  
  public String getName()
  {
    return name;
  }
  
  public Types getType()
  {
    return type;
  }
  
  public String getCharPath()
  {
    return charPath;
  }
}
