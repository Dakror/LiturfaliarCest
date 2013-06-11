package de.dakror.liturfaliar.item;

import de.dakror.liturfaliar.settings.Attributes;
import de.dakror.liturfaliar.settings.Attributes.Attr;


public enum Items
{
  // POCKETKNIFE(1, 274, "Taschenmesser", "", Types.SWORD, new Attributes()),
  BOYSHIRT(12, 2, "Jungen-Oberteil", "1_gray", Types.SHIRT, new Attributes(Attr.protection, 1, Attr.weight, 0.6), new Attributes()),
  GIRLSHIRT(12, 2, "Leichtes Kleid", "16_black", Types.SHIRT, new Attributes(Attr.protection, 1, Attr.weight, 0.4), new Attributes()),
  BOYPANTS(7, 391, "Hose", "1_brown", Types.PANTS, new Attributes(Attr.protection, 1, Attr.weight, 1.1), new Attributes()),
  BOYBOOTS(0, 3, "Schuhe", "1_black", Types.SHOES, new Attributes(Attr.protection, 1, Attr.weight, 0.5), new Attributes()),
  GIRLBOOTS(0, 3, "Halbschuhe", "8_black", Types.SHOES, new Attributes(Attr.protection, 1, Attr.weight, 0.5), new Attributes());
  
  private int ix, iy;
  private Attributes attributes, requirements;
  private String     name, charPath;
  private Types      type;
  
  private Items(int ix, int iy, String name, String path, Types type, Attributes attr, Attributes requirements)
  {
    this.ix = ix;
    this.iy = iy;
    this.name = name;
    this.charPath = path;
    this.type = type;
    this.attributes = attr;
    this.requirements = requirements;
  }
  
  public int getIconX()
  {
    return ix;
  }
  
  public int getIconY()
  {
    return iy;
  }
  
  public Attributes getAttributes()
  {
    return attributes;
  }
  
  public Attributes getRequirement()
  {
    return requirements;
  }
  
  public void setRequirement(Attributes requirement)
  {
    this.requirements = requirement;
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
