package de.dakror.liturfaliar.event.dispatcher;

import java.util.ArrayList;

import de.dakror.liturfaliar.event.listener.DatabaseEventListener;

public class DatabaseEventDispatcher
{
  static ArrayList<DatabaseEventListener> listeners = new ArrayList<DatabaseEventListener>();
  
  public static void addDatabaseEventListener(DatabaseEventListener l)
  {
    listeners.add(l);
  }
  
  public static void removeDatabaseEventListener(DatabaseEventListener l)
  {
    if (listeners.indexOf(l) == -1)
      return;
    listeners.set(listeners.indexOf(l), null);
  }
  
  public static void dispatchStringVarChanged(String key, String value)
  {
    for (DatabaseEventListener l : listeners)
    {
      if (l != null)
        l.stringVarChanged(key, value);
    }
  }
  
  public static void dispatchBooleanVarChanged(String key, boolean value)
  {
    for (DatabaseEventListener l : listeners)
    {
      if (l != null)
        l.booleanVarChanged(key, value);
    }
  }
}
