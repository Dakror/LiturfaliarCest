package de.dakror.liturfaliarcest.layer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import de.dakror.gamesetup.layer.Layer;
import de.dakror.gamesetup.util.Helper;
import de.dakror.liturfaliarcest.game.Game;
import de.dakror.liturfaliarcest.game.quest.Quest;
import de.dakror.liturfaliarcest.settings.FlagManager;

/**
 * @author Dakror
 */
public class QuestLayer extends Layer
{
	int x, y, width, height, questCount, leftIndex;
	boolean leftHover, rightHover;
	
	public QuestLayer()
	{
		modal = true;
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		drawModality(g);
		g.drawImage(Game.getImage("system/book.png"), x, y, Game.w);
		
		Color o = g.getColor();
		g.setColor(Color.decode("#421414"));
		if (FlagManager.isFlag("QUEST_" + leftIndex + "_DONE") || FlagManager.isFlag("QUEST_" + leftIndex + "_ACCEPTED"))
		{
			Quest q = Quest.quests.get(leftIndex);
			int lines = Helper.drawStringWrapped(q.getName(), x + 30, y + 40, width / 2, g, 35);
			
			Helper.drawStringWrapped(q.getText(), x + 30, y + 40 + lines * 40, width / 2 - 35, g, 22);
			
			if (FlagManager.isFlag("QUEST_" + leftIndex + "_DONE")) g.drawImage(Game.getImage("system/checked.png"), x + width / 2 - 70, y + height - 60, 64, 52, Game.w);
		}
		if (FlagManager.isFlag("QUEST_" + (leftIndex + 1) + "_DONE") || FlagManager.isFlag("QUEST_" + (leftIndex + 1) + "_ACCEPTED"))
		{
			Quest q = Quest.quests.get(leftIndex + 1);
			int lines = Helper.drawStringWrapped(q.getName(), x + width / 2 + 20, y + 40, width / 2 - 30, g, 35);
			
			Helper.drawStringWrapped(q.getText(), x + width / 2 + 20, y + 40 + lines * 40, width / 2 - 35, g, 22);
			if (FlagManager.isFlag("QUEST_" + (leftIndex + 1) + "_DONE")) g.drawImage(Game.getImage("system/checked.png"), x + width / 2 + 10, y + height - 60, 64, 52, Game.w);
		}
		g.setColor(o);
		
		if (leftHover) g.drawImage(Game.getImage("system/book_left_turn.png"), x, y, Game.w);
		if (rightHover) g.drawImage(Game.getImage("system/book_right_turn.png"), x, y, Game.w);
	}
	
	@Override
	public void update(int tick)
	{}
	
	@Override
	public void init()
	{
		BufferedImage book = Game.getImage("system/book.png");
		width = book.getWidth();
		height = book.getHeight();
		x = (Game.getWidth() - width) / 2;
		y = (Game.getHeight() - height) / 2;
		
		leftHover = rightHover = false;
		leftIndex = 1;
		questCount = 0;
		for (String f : FlagManager.flags)
			if (f.startsWith("QUEST_") && (f.endsWith("_DONE") || f.endsWith("_ACCEPTED"))) questCount++;
	}
	
	@Override
	public void mouseMoved(MouseEvent e)
	{
		super.mouseMoved(e);
		
		int size = 70;
		leftHover = (leftIndex > 1) && new Rectangle(x, y, size, height).contains(e.getPoint());
		rightHover = (leftIndex + 1 < questCount) && new Rectangle(x + width - size, y, size, height).contains(e.getPoint());
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		super.mousePressed(e);
		if (rightHover) leftIndex += 2;
		if (leftHover) leftIndex -= 2;
		
		int size = 70;
		leftHover = (leftIndex > 1) && new Rectangle(x, y, size, height).contains(e.getPoint());
		rightHover = (leftIndex + 1 < questCount) && new Rectangle(x + width - size, y, size, height).contains(e.getPoint());
	}
}
