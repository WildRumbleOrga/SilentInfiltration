package de.mih.core.engine.ai.navigation.pathfinder.PathGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import de.mih.core.engine.ai.navigation.NavPoint;
import de.mih.core.engine.ai.navigation.pathfinder.Path;

/**
 * A generic implementation of the A*-Algorithm.
 * 
 * @author Cataract
 *
 * @param <T>
 *            A path-type the A*-Algorithm should create.
 * @param <K>
 *            The Node-type the path uses.
 */
public class AStar {

	ArrayList<NavPoint> openlist = new ArrayList<NavPoint>();
	ArrayList<NavPoint> closedlist = new ArrayList<NavPoint>();

	public HashMap<NavPoint, NavPoint> prevs = new HashMap<NavPoint, NavPoint>();
	public HashMap<NavPoint, Float> values = new HashMap<NavPoint, Float>();

	public ArrayList<NavPoint> generatePath(NavPoint first, NavPoint last) {
		openlist.clear();
		closedlist.clear();
		prevs.clear();
		values.clear();
		

		values.put(first, 0f);
		openlist.add(first);

		NavPoint current = null;
		while (!openlist.isEmpty()) {
			current = getMin(openlist, last);
			System.out.println("\nGet Minimum: " + current + "" + current.getPos());
			if (current == last)
				break;
			openlist.remove(current);
			closedlist.add(current);
			expandNode(current, last, openlist, closedlist);
		}

		ArrayList<NavPoint> path = new ArrayList<NavPoint>();

		while (current != first) {
			path.add(current);
			current = prevs.get(current);
		}
		path.add(first);
		for (NavPoint n : path) {
			System.out.print(n + "" + n.getPos() + " ; ");
		}
		System.out.println("");
		Collections.reverse(path);
		return path;
	}
	
	private float getValue(NavPoint nav){
		if (!values.containsKey(nav))
			values.put(nav, Float.MAX_VALUE);
		return values.get(nav);
	}

	private void expandNode(NavPoint current, NavPoint last, ArrayList<NavPoint> openlist,
			ArrayList<NavPoint> closedlist) {
		if (current.isVisibleBy(last)) {
			if (!openlist.contains(last))
				openlist.add(last);
			
			float temp_g = getValue(current) + last.getDistance(current);
			if (temp_g < getValue(last)){
				values.put(last,temp_g);
				prevs.put(last,current);
			}
		}
		System.out.println("Expand: ");
		for (NavPoint node : current.getVisibleNavPoints()) {
			if (closedlist.contains(node))
				continue;
			System.out.print("Node: " + node + "" + node.getPos());
			float temp_g =getValue(current) + current.getDistance(node);
			if (openlist.contains(node) && temp_g >= getValue(node)) {
				System.out.println("already checked");
				continue;
			}
			System.out.print(" value: " + (temp_g + node.getPos().dst(last.getPos())));
			prevs.put(node, current);
			values.put(node, temp_g);
			if (!openlist.contains(node)) {
				openlist.add(node);
				System.out.print(" added to openlist");
			}
			System.out.println("");
		}
	}

	private NavPoint getMin(ArrayList<NavPoint> list, NavPoint last) {
		NavPoint min = list.get(0);
		for (NavPoint node : openlist) {
			if (getValue(node) + node.getPos().dst(last.getPos()) < getValue(min)
					+ min.getPos().dst(last.getPos())) {
				min = node;
			}
		}
		return min;
	}
}