package de.dakror.liturfaliarcest.settings;

import java.io.File;
import java.util.Arrays;

public class CFG {
	public static final File DIR = new File(System.getProperty("user.home") + "/.dakror/Liturfaliar Cest");
	
	public static final String[] TILES = { "CUSTOMS.png", "Beach01.png", "Bridge01.png", "Castle01.png", "Castle02.png", "Castle03.png", "CastleTown01.png", "CastleTown02.png", "Cave01.png", "Cave02.png", "Cave03.png", "Cave04.png", "Church01.png", "Church02.png", "DarkSpace01.png", "Desert01.png", "DesertTown01.png", "DesertTown02.png", "Door01.png", "EvilCastle01.png", "EvilCastle02.png", "FarmVillage01.png", "FarmVillage02.png", "Forest01.png", "ForestTown01.png", "ForestTown02.png", "Fort01.png", "Fort02.png", "Grassland01.png", "Heaven01.png", "Heaven02.png", "InnerBody01.png", "Mine01.png", "MineTown01.png", "MineTown02.png", "Mountain01.png", "PortTown01.png", "PortTown02.png", "PostTown01.png", "PostTown02.png", "Ruins01.png", "Sewer01.png", "Ship01.png", "Ship02.png", "Shop01.png", "Snowfield01.png", "SnowTown01.png", "SnowTown02.png", "Swamp01.png", "Tower01.png", "Tower02.png", "Woods01.png" };
	public static final String[] AUTOTILES = { "B_Ground01.png", "B_Ground02.png", "Carpet01.png", "Carpet02.png", "CE_Grass01.png", "CE_Ground01.png", "CE_Ground02.png", "CE_Road01.png", "CE_Shadow01.png", "CF_Ground01.png", "CF_Ground02.png", "CF_Ground03.png", "CF_Ground04.png", "CF_Shadow01.png", "CI_Ground01.png", "CI_Ground02.png", "CI_Ice01.png", "CI_Shadow01.png", "CI_Snow01.png", "CI_Water01.png", "CW_Grass01.png", "CW_Grass02.png", "CW_Grass03.png", "CW_Ground01.png", "CW_Shadow01.png", "Flower01.png", "G2_Ground01.png", "G2_Ground02.png", "G2_Swamp01.png", "G2_Swamp02.png", "G2_Swamp03.png", "Grass01.png", "Grass02.png", "Ground01.png", "Ground02.png", "G_Ground01.png", "G_Ground02.png", "G_Road01.png", "G_Road02.png", "G_Shadow01.png", "G_Undulation01.png", "G_Undulation02.png", "Road01.png", "Road02.png", "Road03.png", "Roof01.png", "Roof02.png", "Sa_Grass01.png", "Sa_Grass02.png", "Sa_Grass03.png", "Sa_Ground01.png", "Sa_Road01.png", "Sa_Shadow01.png", "Sa_Undulation01.png", "Sn_Ground01.png", "Sn_Shadow01.png", "Sn_Undulation01.png", "St_Shadow01.png", "Tree01.png", "Tree02.png", "Tree03.png", "Tree04.png", "Wall01.png", "Wall02.png", "Wall03.png" };
	static long time = 0;
	
	static {
		DIR.mkdirs();
	}
	
	public static void u() {
		if (time == 0) time = System.currentTimeMillis();
		else {
			p(System.currentTimeMillis() - time);
			time = 0;
		}
	}
	
	public static void p(Object... p) {
		if (p.length == 1) System.out.println(p[0]);
		else System.out.println(Arrays.toString(p));
	}
}
