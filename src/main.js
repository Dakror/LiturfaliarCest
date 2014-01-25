function teleport(e, x, y, bumpPos)
{
	e.setX(x - (bumpPos ? e.bumpX : 0));
	e.setY(y - (bumpPos ? e.bumpY : 0));
}

function teleportMap(x, y, map, bumpPos)
{
	game.setWorld(map);
	teleport(game.player, x, y, bumpPos);
}
