package de.mih.core.engine.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import de.mih.core.engine.ecs.EntityManager;
import de.mih.core.engine.io.Blueprints.Blueprint;
import de.mih.core.engine.io.Blueprints.EntityBlueprint;
import de.mih.core.engine.io.Blueprints.Tilemap.TileBorderBlueprint;
import de.mih.core.engine.io.Blueprints.Tilemap.TilemapBlueprint;
import de.mih.core.engine.tilemap.Tile;
import de.mih.core.engine.tilemap.Tilemap;
import de.mih.core.game.Game;
import de.mih.core.game.components.BorderC;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * The BlueprintManager reads enitityBlueprints from XML and stores them internally.
 * With the stored enitityBlueprints the manager creates new entities.
 *
 * @author Tobias
 */
public class BlueprintManager
{
	Map<String,EntityBlueprint> entityBlueprints = new HashMap<>();

	private EntityManager entityManager;

	public BlueprintManager(EntityManager entityManager)
	{
		this.entityManager = entityManager;
	}

	static BlueprintManager blueprintManager;

	private Json json = new Json();

	@Deprecated
	public static BlueprintManager getInstance()
	{
		return blueprintManager;
	}

	public Tilemap readTilemapBlueprint(String path){
		File file = Gdx.files.internal(path).file();
		if (!file.exists())
		{
			return null;
		}
		try
		{
			String           content = new String(Files.readAllBytes(file.toPath()), "UTF-8");
			TilemapBlueprint bp      = json.fromJson(TilemapBlueprint.class, content);
			Tilemap tilemap = new Tilemap(bp.getLength(),bp.getWidth(),bp.getTILESIZE(),bp.getName());
			for (TileBorderBlueprint tileBorderBlueprint : bp.getBorders()){


				Tile tile = tilemap.getTileAt(tileBorderBlueprint.getBorders()[0].getX(),tileBorderBlueprint.getBorders()[0].getY());

				int entity = Game.getCurrentGame().getBlueprintManager().createEntityFromBlueprint(tileBorderBlueprint.getCollider());

				BorderC.BorderType borderType = Game.getCurrentGame().getEntityManager().getComponent(entity,BorderC.class).getBorderType();

				switch(borderType)
				{
					case Door:
					{
						tile.getBorder(tileBorderBlueprint.getBorders()[0].getDirection()).setToDoor(entity,tileBorderBlueprint.getCollider());
						break;
					}

					case Wall:
					{
						tile.getBorder(tileBorderBlueprint.getBorders()[0].getDirection()).setToWall(entity,tileBorderBlueprint.getCollider());
						break;
					}
				}

			}
			return tilemap;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public boolean readEntityBlueprint(String path)
	{
		try
		{
			Files.walk(Paths.get(path)).forEach(filePath ->
			{
				if (Files.isRegularFile(filePath))
				{
					FileHandle handle = Gdx.files.internal(filePath.toAbsolutePath().toString());
					if (handle.extension().equals("json"))
					{
						readEntityBlueprintFromPath(handle.path());
					}
				}
			});
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}


	public boolean readEntityBlueprintFromPath(String path)
	{
		File file = Gdx.files.internal(path).file();
		if (!file.exists())
		{
			return false;
		}
		try
		{
			String          content = new String(Files.readAllBytes(file.toPath()), "UTF-8");
			EntityBlueprint bp      = json.fromJson(EntityBlueprint.class, content);
			entityBlueprints.put(file.getName(), bp);
			return true;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public void writeBlueprintToJson(Blueprint blueprint, String path) throws IOException
	{
		FileWriter writer = new FileWriter(path);
		writer.write(json.prettyPrint(blueprint));
		writer.close();
	}

	public int createEntityFromBlueprint(String name)
	{
		return this.entityBlueprints.get(name).generateEntity();
	}

	public int createEntityFromBlueprint(String name, int entityId)
	{
		return this.entityBlueprints.get(name).generateEntity(entityId);
	}
}
