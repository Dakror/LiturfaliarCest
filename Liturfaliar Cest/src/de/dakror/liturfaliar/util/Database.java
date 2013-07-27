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
  private static HashMap<String, String> stringvars  = new HashMap<String, String>();
  private static ArrayList<String>       booleanvars = new ArrayList<String>();
  
  private static JFrame                  frame;
  
  private static void print()
  {
    
    if (frame == null && CFG.UIDEBUG)
    {
      frame = new JFrame("Liturfaliar Cest DatabaseDEBUG");
      frame.setAlwaysOnTop(true);
      frame.setSize(200, 800);
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    if (frame == null)
      return;
    
    JTextArea c = new JTextArea();
    c.setEditable(false);
    
    String string = "";
    
    ArrayList<String> keys = new ArrayList<>(stringvars.keySet());
    Collections.sort(keys);
    
    for (String k : keys)
    {
      string += lengthenString(k) + " = " + stringvars.get(k) + "\n";
    }
    
    ArrayList<String> bools = new ArrayList<>(booleanvars);
    Collections.sort(bools);
    
    for (int i = 0; i < bools.size(); i++)
    {
      string += lengthenString(bools.get(i)) + "\n";
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
  
  public static boolean getBooleanVar(String tag)
  {
    if (booleanvars.contains(tag))
      return true;
    else return false;
  }
  
  public static void setBooleanVar(String key, Boolean value)
  {
    if (value)
      booleanvars.add(key);
    else booleanvars.remove(key);
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
  
  public static String[] getStringVarNames()
  {
    return new ArrayList<String>(stringvars.keySet()).toArray(new String[] {});
  }
  
  public static String[] getBooleanVars()
  {
    return booleanvars.toArray(new String[] {});
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
