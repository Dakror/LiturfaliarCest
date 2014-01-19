package de.dakror.liturfaliarcest.settings;

import java.util.Arrays;

public class CFG
{
	public static final String[] TILES = { "Beach01.png", "Bridge01.png", "Castle01.png", "Castle02.png", "Castle03.png", "CastleTown01.png", "CastleTown02.png", "Cave01.png", "Cave02.png", "Cave03.png", "Cave04.png", "Church01.png", "Church02.png", "DarkSpace01.png", "Desert01.png", "DesertTown01.png", "DesertTown02.png", "EvilCastle01.png", "EvilCastle02.png", "FarmVillage01.png", "FarmVillage02.png", "Forest01.png", "ForestTown01.png", "ForestTown02.png", "Fort01.png", "Fort02.png", "Grassland01.png", "Heaven01.png", "Heaven02.png", "InnerBody01.png", "Mine01.png", "MineTown01.png", "MineTown02.png", "Mountain01.png", "PortTown01.png", "PortTown02.png", "PostTown01.png", "PostTown02.png", "Ruins01.png", "Sewer01.png", "Ship01.png", "Ship02.png", "Shop01.png", "Snowfield01.png", "SnowTown01.png", "SnowTown02.png", "Swamp01.png", "Tower01.png", "Tower02.png", "Woods01.png" };
	
	static long time = 0;
	
	public static void u()
	{
		if (time == 0) time = System.currentTimeMillis();
		else
		{
			p(System.currentTimeMillis() - time);
			time = 0;
		}
	}
	
	public static void p(Object... p)
	{
		if (p.length == 1) System.out.println(p[0]);
		else System.out.println(Arrays.toString(p));
	}
}
