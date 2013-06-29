package de.dakror.liturfaliar.item;

import de.dakror.liturfaliar.item.action.EmptyAction;
import de.dakror.liturfaliar.item.action.ItemAction;
import de.dakror.liturfaliar.item.action.PotionAction;
import de.dakror.liturfaliar.item.action.SkillAction;
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
  HEALTHPOTION(6, 132, "Heiltrank", "", Types.POTION, new Attributes(Attr.cooldown, 5, Attr.weight, 0.2), new Attributes(), new PotionAction("player", new Attributes(Attr.health, 10))),
  TOXICPOTION(3, 132, "Gifttrank", "", Types.POTION, new Attributes(Attr.cooldown, 5, Attr.weight, 0.2), new Attributes(), new PotionAction("player", new Attributes(Attr.health, -10))),
  SCRAP(1, 91, "Beutel mit Schrott", "", Types.ITEM, new Attributes(Attr.weight, 0.25), new Attributes(), new EmptyAction()),
  // -- skills -- //
  
  // -- swordskills -- //
  SWORD0(13, 508, "Schwert-Talent", "", Types.SWORDSKILL, new Attributes(), new Attributes(Attr.skillpoint, 1), new SkillAction("<#ffffff;15;2>Das Talent, mit schwert‰hnlichen Waffen umzu-[br]<#ffffff;15;2>gehen und einen einfachen Schlag auszuf¸hren.")),
  SWORD1(14, 511, "Schwung", "", Types.SWORDSKILL, new Attributes(), new Attributes(Attr.skillpoint, 1, Attr.level, 2), new SkillAction("<#ffffff;15;2>Ein Frontalangriff mit geringem Schaden auf[br]<#ffffff;15;2>mehrere Ziele vertielt und geringem Radius.")),
  SWORD2(13, 511, "Weiter Schwung", "", Types.SWORDSKILL, new Attributes(), new Attributes(Attr.skillpoint, 2, Attr.level, 3), new SkillAction("<#ffffff;15;2>Ein Frontalangriff mit mittlerem Schaden auf[br]<#ffffff;15;2>mehrere Ziele vertielt und weitem Radius.")),
  SWORD3(10, 511, "Rundumschlag", "", Types.SWORDSKILL, new Attributes(), new Attributes(Attr.skillpoint, 3, Attr.level, 5), new SkillAction("<#ffffff;15;2>Ein Rundumangriff mit groﬂem Schaden auf[br]<#ffffff;15;2>mehrere Ziele vertielt.")),
  SWORD4(12, 511, "‹berkopfschlag", "", Types.SWORDSKILL, new Attributes(), new Attributes(), new SkillAction("<#ffffff;15;2>Ein ‹berkopfangriff mit groﬂem Schaden[br]<#ffffff;15;2>auf ein Ziel.")),
  SWORD5(11, 511, "Stich", "", Types.SWORDSKILL, new Attributes(), new Attributes(), new SkillAction("<#ffffff;15;2>Ein Frontalangriff mit groﬂem Schaden auf ein[br]<#ffffff;15;2>Ziel.")), ;
  
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
