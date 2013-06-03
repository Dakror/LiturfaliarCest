package de.dakror.liturfaliar.event.listener;

public interface DatabaseEventListener
{
  public void stringVarChanged(String key, String value);
  
  public void booleanVarChanged(String key, boolean value);
}
