package de.dakror.liturfaliar.event;

public enum Events
{
  stringVarChanged("key", "value"),
  booleanVarChanged("key", "value"),
  
  slotPressed("e", "slot"),
  slotExited("e", "slot"),
  slotHovered("e", "slot"),
  slotReleased("e", "slot"),
  
  slotTriggered("index", "slot"),
  
  fieldTouched("c", "map"),
  fieldTriggered("c", "map"),
  
  talkStarted("talk", "map"),
  talkEnded("talk", "map"),
  talkChanged("old", "new", "map"),
  
  mapChanged("old", "new"),
  
  equipmentChanged("c"),
  
  levelUp("old");
  
  private String[] keys;
  
  private Events(String... keys)
  {
    this.keys = keys;
  }
  
  public String[] getKeys()
  {
    return keys;
  }
}
