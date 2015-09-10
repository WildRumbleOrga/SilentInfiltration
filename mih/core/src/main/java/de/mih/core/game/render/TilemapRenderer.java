package de.mih.core.game.render;

import de.mih.core.engine.tilemap.Tilemap;
import de.mih.core.engine.tilemap.borders.TileBorder;

public class TilemapRenderer {
	Tilemap tilemap;
	public TilemapRenderer(Tilemap tilemap) {
		this.tilemap = tilemap;
	}
	public void render() {
		for(int i = 0; i < tilemap.getLength(); i++)
		{
			for(int x = 0; x < tilemap.getWidth(); x++)
			{
				if(RenderManager.getInstance().isVisible(tilemap.getTileAt(x, i).visual))
				{
					tilemap.getTileAt(x, i).render();
				}
			}
		}
		for(TileBorder border : tilemap.getBorders())
		{
			if(border.hasBorderCollider() && RenderManager.getInstance().isVisible(border.getBorderCollider().getVisual()))
			{
				RenderManager.getInstance().getModelBatch().render(border.getBorderCollider().getVisual().model, RenderManager.getInstance().getEnvironment());
			}
		}
	}

}
