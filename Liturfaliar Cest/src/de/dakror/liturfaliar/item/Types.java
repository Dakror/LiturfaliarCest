package de.dakror.liturfaliar.item;

public enum Types
{
  SWORD(Categories.WEAPON, "Schwert"),
  SHIRT(Categories.SHIRT, "Oberteil"),
  SKIN(Categories.SKIN, "Haut"),
  CAPE(Categories.CAPE, "Umhang"),
  HAIR(Categories.HAIR, "Haare"),
  PANTS(Categories.PANTS, "Hose"),
  CLOTHBOOTS(Categories.BOOTS, "Schuhe"),
  EYES(Categories.EYES, "Augen"),
  SHOES(Categories.BOOTS, "Schuhe");
  
  private String     name;
  private Categories category;
  
  private Types(Categories cat, String name)
  {
    this.name = name;
    this.category = cat;
  }
  
  public String getName()
  {
    return name;
  }
  
  public Categories getCategory()
  {
    return category;
  }
  
}
