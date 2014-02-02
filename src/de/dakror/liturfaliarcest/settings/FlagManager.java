package de.dakror.liturfaliarcest.settings;

import java.util.ArrayList;

/**
 * @author Dakror
 */
public class FlagManager
{
	static ArrayList<String> flags = new ArrayList<>();
	
	public static void toggle(String name)
	{
		if (flags.contains(name.toUpperCase())) flags.remove(name.toUpperCase());
		else flags.add(name.toUpperCase());
	}
	
	public static void set(String name)
	{
		if (!flags.contains(name)) flags.add(name.toUpperCase());
	}
	
	public static void add(String name)
	{
		flags.add(name.toUpperCase());
	}
	
	public static boolean is(String name)
	{
		return flags.contains(name.toUpperCase());
	}
	
	public static int count(String name)
	{
		int i = 0;
		for (String n : flags)
			if (n.toUpperCase().equals(name.toUpperCase())) i++;
		
		return i;
	}
}
