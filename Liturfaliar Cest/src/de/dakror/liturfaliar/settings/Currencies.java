package de.dakror.liturfaliar.settings;

import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

public class Currencies
{
  public static enum Curr
  {
    gold("Gold", "G"),
    skillpoint("Fähigkeitspunkte", "SP");
    
    private String text;
    private String token;
    
    private Curr(String text, String token)
    {
      this.text = text;
      this.token = token;
    }
    
    public String getText()
    {
      return text;
    }
    
    public String getToken()
    {
      return token;
    }
  }
  
  private HashMap<Curr, Integer> currencies;
  
  public Currencies()
  {
    currencies = new HashMap<Curr, Integer>();
    for (Curr curr : Curr.values())
    {
      currencies.put(curr, 0);
    }
  }
  
  public Currencies(Object... args)
  {
    this();
    
    if (args.length % 2 != 0)
      return;
    
    for (int i = 0; i < args.length; i += 2)
    {
      if (args[i] instanceof Curr)
      {
        currencies.put((Curr) args[i], Integer.parseInt(args[i + 1].toString()));
      }
    }
  }
  
  public Currencies(JSONObject o)
  {
    this();
    loadCurrencies(o);
  }
  
  public void loadCurrencies(JSONObject o)
  {
    try
    {
      for (Iterator<?> i = o.keys(); i.hasNext();)
      {
        String key = i.next().toString();
        
        currencies.put(Curr.valueOf(key), o.getInt(key));
      }
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
  }
  
  public JSONObject serializeCurrencies()
  {
    JSONObject o = new JSONObject();
    try
    {
      
      for (Curr curr : Curr.values())
      {
        o.put(curr.name(), currencies.get(curr));
      }
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
    return o;
  }
  
  public int getCurrency(Curr curr)
  {
    return currencies.get(curr);
  }
  
  public void setCurrency(Curr curr, int v)
  {
    currencies.put(curr, v);
  }
}
