package de.dakror.liturfaliar.item;

import de.dakror.liturfaliar.item.Item.Types;

public enum Items
{
  POCKETKNIFE(1, 274, "Taschenmesser", Types.SWORD, 3);
  
  
  private int    ix, iy, damage;
  private String name;
  private Types  type;
  
  private Items(int ix, int iy, String name, Item.Types type, int dmg)
  {
    this.ix = ix;
    this.iy = iy;
    this.name = name;
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
}
