function teleport(e, x, y, bumpPos)
{
	e.setX(x - (bumpPos ? e.bumpX : 0));
	e.setY(y - (bumpPos ? e.bumpY : 0));
}

function teleportMap(x, y, map, bumpPos)
{
	game.fadeTo(1.0, 0.05);
	game.actionOnFade = "function() {game.setWorld('" + map + "'); teleport(game.player, " + x + ", " + y + ", " + bumpPos + ");game.fadeTo(0, 0.05);}";
}
