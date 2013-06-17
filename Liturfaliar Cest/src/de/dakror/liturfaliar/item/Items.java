package de.dakror.liturfaliar.item;

import de.dakror.liturfaliar.item.action.EmptyAction;
import de.dakror.liturfaliar.item.action.ItemAction;
import de.dakror.liturfaliar.item.action.Potion;
import de.dakror.liturfaliar.settings.Attributes;
import de.dakror.liturfaliar.settings.Attributes.Attr;


public enum Items
{
  // POCKETKNIFE(1, 274, "Taschenmesser", "", Types.SWORD, new Attributes()),
  BOYSHIRT(12, 2, "Jungen-Oberteil", "1_gray", Types.SHIRT, new Attributes(Attr.protection, 1, Attr.weight, 0.6), new Attributes(), new EmptyAction()),
  GIRLSHIRT(12, 2, "Leichtes Kleid", "16_black", Types.SHIRT, new Attributes(Attr.protection, 1, Attr.weight, 0.4), new Attributes(), new EmptyAction()),
  BOYPANTS(7, 391, "Hose", "1_brown", Types.PANTS, new Attributes(Attr.protection, 1, Attr.weight, 1.1), new Attributes(), new EmptyAction()),
  BOYBOOTS(0, 3, "Schuhe", "1_black", Types.SHOES, new Attributes(Attr.protection, 1, Attr.weight, 0.5), new Attributes(), new EmptyAction()),
  GIRLBOOTS(0, 3, "Halbschuhe", "8_black", Types.SHOES, new Attributes(Attr.protection, 1, Attr.weight, 0.5), new Attributes(), new EmptyAction()),
  HEALTHPOTION(6, 132, "Heiltrank", "", Types.POTION, new Attributes(Attr.cooldown, 30, Attr.weight, 0.2), new Attributes(), new Potion("player", new Attributes(Attr.health, 10))),
  TOXICPOTION(3, 132, "Gifttrank", "", Types.POTION, new Attributes(Attr.cooldown, 30, Attr.weight, 0.2), new Attributes(), new Potion("player", new Attributes(Attr.health, -10))),
  SCRAP(1, 91, "Beutel mit Schrott", "", Types.ITEM, new Attributes(Attr.weight, 0.25), new Attributes(), new EmptyAction());
  
  private int ix, iy;
  private Attributes attributes, requirements;
  private String     name, charPath;
  private Types      type;
  private ItemAction itemAction;
  
  private Items(int ix, int iy, String name, String path, Types type, Attributes attr, Attributes requirements, ItemAction action)
  {
    this.ix = ix;
    this.iy = iy;
    this.name = name;
    this.charPath = path;
    this.type = type;
    this.attributes = attr;
    this.requirements = requirements;
    this.itemAction = action;
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
  
  public ItemAction getItemAction()
  {
    return itemAction;
  }
  
  public void setItemAction(ItemAction itemAction)
  {
    this.itemAction = itemAction;
  }
}
