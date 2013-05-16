package de.dakror.liturfaliar.map.event;

public interface DatabaseEventListener
{
  public void stringVarChanged(String key, String value);
  
  public void booleanVarChanged(String key, boolean value);
}
