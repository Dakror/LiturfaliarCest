/*******************************************************************************
 * Copyright 2015 Maximilian Stark | Dakror <mail@dakror.de>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


package de.dakror.liturfaliarcest.game;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.imageio.ImageIO;

import de.dakror.gamesetup.GameFrame;
import de.dakror.gamesetup.util.Helper;
import de.dakror.liturfaliarcest.game.animation.Animation;
import de.dakror.liturfaliarcest.game.entity.EntityType;
import de.dakror.liturfaliarcest.game.entity.creature.Player;
import de.dakror.liturfaliarcest.game.item.Item;
import de.dakror.liturfaliarcest.game.quest.Quest;
import de.dakror.liturfaliarcest.game.world.World;
import de.dakror.liturfaliarcest.layer.HUDLayer;
import de.dakror.liturfaliarcest.layer.QuestLayer;
import de.dakror.liturfaliarcest.layer.TalkLayer;
import de.dakror.liturfaliarcest.settings.FlagManager;
import de.dakror.liturfaliarcest.util.SavegameHandler;

public class Game extends GameFrame {
	public static Game currentGame;
	public static World world;
	public static Player player;
	
	public int questLayerIndex;
	
	public static HashMap<String, World> worlds;
	
	public String actionOnFade;
	
	public Game() {
		currentGame = this;
	}
	
	@Override
	public void initGame() {
		w.setIconImage(getImage("system/logo.png"));
		w.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(getImage("system/cursor.png"), new Point(0, 0), "default_cursor"));
		try {
			w.setFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/MorrisRomanBlack.ttf")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		EntityType.init();
		Item.init();
		Quest.init();
		Animation.init();
		
		questLayerIndex = 1;
		
		getImage("system/icons.png"); // for loading purpose
	}
	
	public void setWorld(String map) {
		int index = layers.indexOf(world);
		world = worlds.containsKey(map) ? worlds.get(map) : new World(map);
		world.init();
		layers.set(index, world);
		world.addEntity(player);
	}
	
	@Override
	public void draw(Graphics2D g) {
		if (layers.size() == 0) {
			worlds = new HashMap<>();
			world = new World("Kerstil_0");
			player = new Player(3 * World.TILE_SIZE, 3 * World.TILE_SIZE / 2);
			player.uid = 0;
			addLayer(world);
			world.addEntity(player);
			
			addLayer(new HUDLayer());
		}
		
		drawLayers(g);
		
		Helper.drawString("FPS: " + getFPS(), 10, 26, g, 18);
		Helper.drawString("UPS: " + getUPS(), 10, 52, g, 18);
		Helper.drawString("E: " + world.drawn + " / " + world.components.size(), 10, 52 + 26, g, 18);
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		super.keyPressed(e);
		if (e.getKeyCode() == KeyEvent.VK_J) {
			if (getActiveLayer() instanceof QuestLayer) {
				questLayerIndex = ((QuestLayer) getActiveLayer()).leftIndex;
				removeLayer(getActiveLayer());
			} else addLayer(new QuestLayer(questLayerIndex));
		}
		if (e.getKeyCode() == KeyEvent.VK_S && e.isControlDown()) SavegameHandler.save(null);
		if (e.getKeyCode() == KeyEvent.VK_F1 && FlagManager.jta == null) FlagManager.showDebugWindow();
	}
	
	public void endTalk() {
		if (getActiveLayer() instanceof TalkLayer) ((TalkLayer) getActiveLayer()).endTalk(true);
	}
	
	@Override
	public BufferedImage loadImage(String p) {
		try {
			BufferedImage i = ImageIO.read(GameFrame.class.getResource((p.startsWith("/") ? "" : p.contains("gui") ? "/img/" : "/img/") + p));
			
			return i;
		} catch (Exception e) {
			return null;
		}
	}
}
