package de.dakror.liturfaliar.util;

import java.util.ArrayList;
import java.util.HashMap;

import de.dakror.liturfaliar.event.dispatcher.DatabaseEventDispatcher;

public class Database
{
  private static HashMap<String, String>  stringvars  = new HashMap<String, String>();
  private static HashMap<String, Boolean> booleanvars = new HashMap<String, Boolean>();
  
  public static String getStringVar(String key)
  {
    if (stringvars.containsKey(key))
      return stringvars.get(key);
    else return null;
  }
  
  public static boolean getBooleanVar(String key)
  {
    if (booleanvars.containsKey(key))
      return booleanvars.get(key);
    else return false;
  }
  
  public static void setBooleanVar(String key, Boolean value)
  {
    booleanvars.put(key, value);
    DatabaseEventDispatcher.dispatchBooleanVarChanged(key, value);
  }
  
  public static void setStringVar(String key, String value)
  {
    stringvars.put(key, value);
    DatabaseEventDispatcher.dispatchStringVarChanged(key, value);
  }
  
  public String[] getStringVarNames()
  {
    return new ArrayList<String>(stringvars.keySet()).toArray(new String[] {});
  }
  
  public String[] getBooleanVarNames()
  {
    return new ArrayList<String>(booleanvars.keySet()).toArray(new String[] {});
  }
  
  public static String filterString(String raw)
  {
    String res = raw;
    for (String key : stringvars.keySet())
    {
      if (key == null || stringvars.get(key) == null)
        continue;
      res = res.replace("%" + key + "%", stringvars.get(key));
    }
    return res;
  }
}
