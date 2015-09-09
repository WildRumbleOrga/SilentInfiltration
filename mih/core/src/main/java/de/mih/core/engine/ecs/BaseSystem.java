package de.mih.core.engine.ecs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mih.core.engine.ecs.events.BaseEvent;

import java.lang.Comparable;


/**
 * Systems work on entities that have the wanted components.
 * 
 * @author Tobias
 *
 */
@SuppressWarnings("rawtypes")
public abstract class BaseSystem implements Comparable<BaseSystem>{
	// TODO: how do we link components and systems without searching on every
	// entity? (efficiency!!)
	// TODO(Nico): Make an entity-class instead of just in int? So we could make
	// a list in every component, which stores all the entities having this
	// component. Pro: Efficiency!!, Easy to delete entities. Contra: Entities take more space, Handling entities is a bit slower.
	protected EntityManager entityManager;
	protected int priority;

	public BaseSystem(SystemManager systemManager, EntityManager entityManager) {
		this(systemManager,entityManager,1);
	}
	
	public BaseSystem(SystemManager systemManager, EntityManager entityManager,int priority) {
		systemManager.register(this);
		this.priority = priority;
		this.entityManager = entityManager;
	}

	/**
	 * check if the enitity has the needed components
	 * 
	 * @param entity
	 * @return
	 */
	public abstract boolean matchesSystem(int entityId);

	/**
	 * Call for the game-loop, here all needed changes on the data is made.
	 * In contrast to update(double dt, int entity) this is independent of the entities
	 * 
	 * @param dt
	 *            Time in ms since the last call
	 */
	public abstract void update(double dt);
	
	
	/**
	 * Call for the game-loop, here all needed changes on the data is made.
	 * 
	 * @param dt
	 *            Time in ms since the last call
	 */
	public abstract void update(double dt, int entity);

	/**
	 * Call for the game-loop, only needed for systems that want to render
	 * something. In contrast to render(int entity) this is independent of the entities
	 */
	public abstract void render();
	
	/**
	 * Call for the game-loop, only needed for systems that want to render
	 * something.
	 */
	public abstract void render(int entity);
	
	public abstract void onEventRecieve(Class <? extends BaseEvent> event, ArrayList<Object> params);
	
	@Override
	public int compareTo(BaseSystem other)
	{
		int result = priority - other.priority;
		if(result < 0)
			return 1;
		if(result == 0)
			return 0;
		return -1;
	}
}
