package de.dakror.liturfaliar.item;

import de.dakror.liturfaliar.item.action.EmptyAction;
import de.dakror.liturfaliar.item.action.ItemAction;
import de.dakror.liturfaliar.item.action.PotionAction;
import de.dakror.liturfaliar.item.action.SkillAction;
import de.dakror.liturfaliar.item.action.WeaponAction;
import de.dakror.liturfaliar.item.skillanim.EmptyAnimation;
import de.dakror.liturfaliar.item.skillanim.Sword0;
import de.dakror.liturfaliar.settings.Attributes;
import de.dakror.liturfaliar.settings.Attributes.Attr;
import de.dakror.liturfaliar.settings.DamageType;


public enum Items
{
  POCKETKNIFE(10, 1, /*-10, -9*/0, 0, "Taschenmesser", "", Types.MONOSWORD, new Attributes(), new Attributes(), new WeaponAction(new Attributes(Attr.health, -2, -7), DamageType.NORMAL)),
  BOYSHIRT(12, 2, 0, 0, "Jungen-Oberteil", "1_gray", Types.SHIRT, new Attributes(Attr.protection, 1, Attr.weight, 0.6), new Attributes(), new EmptyAction()),
  GIRLSHIRT(12, 2, 0, 0, "Leichtes Kleid", "16_black", Types.SHIRT, new Attributes(Attr.protection, 1, Attr.weight, 0.4), new Attributes(), new EmptyAction()),
  BOYPANTS(7, 391, 0, 0, "Hose", "1_brown", Types.PANTS, new Attributes(Attr.protection, 1, Attr.weight, 1.1), new Attributes(), new EmptyAction()),
  BOYBOOTS(0, 3, 0, 0, "Schuhe", "1_black", Types.SHOES, new Attributes(Attr.protection, 1, Attr.weight, 0.5), new Attributes(), new EmptyAction()),
  GIRLBOOTS(0, 3, 0, 0, "Halbschuhe", "8_black", Types.SHOES, new Attributes(Attr.protection, 1, Attr.weight, 0.5), new Attributes(), new EmptyAction()),
  HEALTHPOTION(6, 132, 0, -4, "Heiltrank", "", Types.POTION, new Attributes(Attr.cooldown, 5, Attr.weight, 0.2), new Attributes(), new PotionAction("player", new Attributes(Attr.health, 10), DamageType.HEAL)),
  TOXICPOTION(3, 132, 0, -4, "Gifttrank", "", Types.POTION, new Attributes(Attr.cooldown, 5, Attr.weight, 0.2), new Attributes(), new PotionAction("player", new Attributes(Attr.health, -10), DamageType.NORMAL)),
  SCRAP(1, 91, 0, -2, "Beutel mit Schrott", "", Types.ITEM, new Attributes(Attr.weight, 0.25), new Attributes(), new EmptyAction()),
  // -- skills -- //
  
  // -- swordskills -- //
  SWORD0(13, 508, 0, 0, "Schwert-Talent", "", Types.SWORDSKILL, new Attributes(Attr.health, -5), new Attributes(Attr.skillpoint, 1), new SkillAction("<#ffffff;15;2>Das Talent, mit schwert‰hnlichen Waffen umzu-[br]<#ffffff;15;2>gehen und einen einfachen Schlag auszuf¸hren.", new Sword0())),
  SWORD1(14, 511, 0, 0, "Schwung", "", Types.SWORDSKILL, new Attributes(), new Attributes(Attr.skillpoint, 1, Attr.level, 2), new SkillAction("<#ffffff;15;2>Ein Frontalangriff mit geringem Schaden auf[br]<#ffffff;15;2>mehrere Ziele vertielt und geringem Radius.", new EmptyAnimation())),
  SWORD2(13, 511, 0, 0, "Weiter Schwung", "", Types.SWORDSKILL, new Attributes(), new Attributes(Attr.skillpoint, 2, Attr.level, 3), new SkillAction("<#ffffff;15;2>Ein Frontalangriff mit mittlerem Schaden auf[br]<#ffffff;15;2>mehrere Ziele vertielt und weitem Radius.", new EmptyAnimation())),
  SWORD3(10, 511, 0, 0, "Rundumschlag", "", Types.SWORDSKILL, new Attributes(), new Attributes(Attr.skillpoint, 3, Attr.level, 5), new SkillAction("<#ffffff;15;2>Ein Rundumangriff mit groﬂem Schaden auf[br]<#ffffff;15;2>mehrere Ziele vertielt.", new EmptyAnimation())),
  SWORD4(12, 511, 0, 0, "‹berkopfschlag", "", Types.SWORDSKILL, new Attributes(), new Attributes(), new SkillAction("<#ffffff;15;2>Ein ‹berkopfangriff mit groﬂem Schaden[br]<#ffffff;15;2>auf ein Ziel.", new EmptyAnimation())),
  SWORD5(11, 511, 0, 0, "Stich", "", Types.SWORDSKILL, new Attributes(), new Attributes(), new SkillAction("<#ffffff;15;2>Ein Frontalangriff mit groﬂem Schaden auf ein[br]<#ffffff;15;2>Ziel.", new EmptyAnimation()));
  
  private int ix, iy, cx, cy;
  private Attributes attributes, requirements;
  private String     name, charPath;
  private Types      type;
  private ItemAction itemAction;
  
  private Items(int ix, int iy, int cx, int cy, String name, String path, Types type, Attributes attr, Attributes requirements, ItemAction action)
  {
    this.ix = ix;
    this.iy = iy;
    this.cx = cx;
    this.cy = cy;
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
  
  public int getCorrectionX()
  {
    return cx;
  }
  
  public int getCorrectionY()
  {
    return cy;
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
