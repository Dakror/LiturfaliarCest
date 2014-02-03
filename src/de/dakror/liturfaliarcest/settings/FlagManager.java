package de.dakror.liturfaliarcest.settings;

import java.util.ArrayList;

/**
 * @author Dakror
 */
public class FlagManager
{
	public static ArrayList<String> flags = new ArrayList<>();
	
	public static void toggleFlag(String name)
	{
		if (flags.contains(name.toUpperCase())) flags.remove(name.toUpperCase());
		else flags.add(name.toUpperCase());
	}
	
	public static void setFlag(String name)
	{
		if (!flags.contains(name)) flags.add(name.toUpperCase());
	}
	
	public static void addFlag(String name)
	{
		flags.add(name.toUpperCase());
	}
	
	public static boolean isFlag(String name)
	{
		return flags.contains(name.toUpperCase());
	}
	
	public static boolean matchesFlags(String text)
	{
		String[] flags = text.split(" ");
		for (String flag : flags)
		{
			String f = flag.substring(1);
			if (flag.startsWith("!") == isFlag(f)) return false;
		}
		
		return true;
	}
	
	public static int countFlags(String name)
	{
		int i = 0;
		for (String n : flags)
			if (n.toUpperCase().equals(name.toUpperCase())) i++;
		
		return i;
	}
}
