package de.mih.core.engine.ecs;

import java.util.HashMap;
import java.util.Map;

public class Event<T> {
	public int entityID;
	public String eventType;
	public T message;
	
	public Map<Integer, Object> ObjectQueue;
	
	public Event(String s){
		eventType = s;
		ObjectQueue = new HashMap<Integer, Object>();
	}
}
