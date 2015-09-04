package de.mih.core.engine.ecs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class SystemManager {
	List<BaseSystem> registeredSystems;
	PriorityQueue<BaseSystem> rS;
	/**
	 * linked entityManger for iteration over entities
	 */
	EntityManager entityManager;

	public SystemManager(EntityManager entityManager, int initialCapacity) {
		this.entityManager = entityManager;
		this.registeredSystems = new ArrayList<BaseSystem>();
	}

	public void register(BaseSystem s) {
		if (!registeredSystems.contains(s)) {
			registeredSystems.add(s);
			Collections.sort(registeredSystems);
		}
	}

	public void update(double dt){
		for (BaseSystem s : registeredSystems) {
			for (int entity = 0; entity < entityManager.entityCount; entity++) {
				if (s.matchesSystem(entity)) {
					s.update(dt, entity);
				}
			}
			s.update(dt);
		}
		
	}

	public void render(double dt){
		for (BaseSystem s : registeredSystems) {
			for (int entity = 0; entity < entityManager.entityCount; entity++) {
				if (s.matchesSystem(entity)) {
					s.render(entity);
				}
			}
			s.render();
		}
	}
}