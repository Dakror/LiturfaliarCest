package de.dakror.liturfaliar.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import de.dakror.liturfaliar.event.dispatcher.DatabaseEventDispatcher;
import de.dakror.liturfaliar.settings.CFG;

public class Database
{
  private static HashMap<String, String>  stringvars  = new HashMap<String, String>();
  private static HashMap<String, Boolean> booleanvars = new HashMap<String, Boolean>();
  
  private static JFrame                   frame;
  
  private static void print()
  {
    if (!CFG.UIDEBUG)
      return;
    if (frame == null)
    {
      frame = new JFrame("Liturfaliar Cest DatabaseDEBUG");
      frame.setAlwaysOnTop(true);
      frame.setSize(200, 800);
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    JTextArea c = new JTextArea();
    c.setEditable(false);
    
    String string = "";
    
    ArrayList<String> keys = new ArrayList<>(stringvars.keySet());
    Collections.sort(keys);
    
    for (String k : keys)
    {
      string += lengthenString(k) + " = " + stringvars.get(k) + "\n";
    }
    
    keys = new ArrayList<>(booleanvars.keySet());
    Collections.sort(keys);
    
    for (String k : keys)
    {
      string += lengthenString(k) + " = " + booleanvars.get(k) + "\n";
    }
    c.setOpaque(false);
    c.setText(string);
    JScrollPane jsp = new JScrollPane(c, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    frame.setContentPane(jsp);
    frame.setVisible(true);
    
  }
  
  private static String lengthenString(String s)
  {
    String string = s;
    for (int i = 0; i < 40 - s.length(); i++)
    {
      string += " ";
    }
    return string;
  }
  
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
    if (CFG.DEBUG)
      print();
    DatabaseEventDispatcher.dispatchBooleanVarChanged(key, value);
  }
  
  public static void setStringVar(String key, String value)
  {
    stringvars.put(key, value);
    if (CFG.DEBUG)
      print();
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
    try
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
    catch (Exception e)
    {
      return raw;
    }
  }
}
