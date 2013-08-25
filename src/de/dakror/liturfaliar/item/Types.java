package de.dakror.liturfaliar.item;

public enum Types
{
  MONOSWORD(Categories.WEAPON, "Einhand-Schwert", 1),
  DUALSWORD(Categories.WEAPON, "Zweihand-Schwert", 1),
  SHIRT(Categories.SHIRT, "Oberteil", 1),
  SKIN(Categories.SKIN, "Haut", 1),
  CAPE(Categories.CAPE, "Umhang", 1),
  HAIR(Categories.HAIR, "Haare", 1),
  PANTS(Categories.PANTS, "Hose", 1),
  CLOTHBOOTS(Categories.BOOTS, "Schuhe", 1),
  EYES(Categories.EYES, "Augen", 1),
  SHOES(Categories.BOOTS, "Schuhe", 1),
  ITEM(Categories.ITEM, "Gegenstand", 10),
  POTION(Categories.CONSUMABLE, "Trank", 10),
  SWORDSKILL(Categories.SKILL, "Schwert-Fähigkeit", 1),
  BOWSKILL(Categories.SKILL, "Bogen-Fähigkeit", 1),
  PERKSKILL(Categories.SKILL, "Status-Fähigkeit", 1);
  
  private String     name;
  private Categories category;
  private int        stackSize;
  
  private Types(Categories cat, String name, int stackSize)
  {
    this.name = name;
    this.category = cat;
    this.stackSize = stackSize;
  }
  
  public String getName()
  {
    return name;
  }
  
  public Categories getCategory()
  {
    return category;
  }
  
  public int getStackSize()
  {
    return stackSize;
  }
}
