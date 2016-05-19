package de.mih.core.engine.ecs;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.badlogic.gdx.Gdx;

import de.mih.core.engine.ecs.component.ComponentInfo;
import de.mih.core.game.components.info.UnittypeComponentInfo;

/**
 * The BlueprintManager reads blueprints from XML and stores them internally.
 * With the stored blueprints the manager creates new entities.
 * 
 * @author Tobias
 */
public class BlueprintManager
{

	Map<String, EntityBlueprint> blueprints = new HashMap<>();
	Map<String, Class<? extends ComponentInfo<?>>> componentInfoTypes = new HashMap<>();

	private EntityManager entityManager;

	public BlueprintManager(EntityManager entityManager)
	{
		this.entityManager = entityManager;
	}

	static BlueprintManager blueprintManager;

	@Deprecated
	public static BlueprintManager getInstance()
	{
		return blueprintManager;
	}

	public void registerComponentInfoType(String name, Class<? extends ComponentInfo<?>> componentInfoType)
	{
		componentInfoTypes.put(name, componentInfoType);
	}

	public boolean readBlueprintFromXML(String path)
	{

		File file = Gdx.files.internal(path).file();

		if (!file.exists())
		{
			return false;
		}
		DocumentBuilder db = null;

		try
		{
			db = dbf.newDocumentBuilder();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		}
		Document dom = null;
		try
		{
			dom = db.parse(file);
		}
		catch (SAXException | IOException e)
		{
			e.printStackTrace();
		}
		if (dom == null)
			return false;

		dom.getDocumentElement().normalize();
		try
		{
			readBlueprint(dom.getDocumentElement());
			return true;
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchFieldException e)
		{
			e.printStackTrace();
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	public ComponentInfo readComponentInfo(Node node)
			throws InstantiationException, IllegalAccessException, NoSuchFieldException, SecurityException
	{
		String componentTypeName = node.getNodeName().toLowerCase();
		ComponentInfo<?> componentInfo = componentInfoTypes.get(componentTypeName).newInstance();

		Map<String, String> fields = new HashMap<>();
		NodeList attr = node.getChildNodes();
		for (int j = 0; j < attr.getLength(); j++)
		{
			Node a = attr.item(j);
			if (a.getNodeType() != Node.ELEMENT_NODE)
				continue;
			fields.put(a.getNodeName(), a.getTextContent());
		}
		componentInfo.readFields(fields);
		return componentInfo;
	}

	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

	private void readBlueprint(Node node)
			throws InstantiationException, IllegalAccessException, NoSuchFieldException, SecurityException
	{
		String name = node.getAttributes().getNamedItem("name").getNodeValue();
		EntityBlueprint blueprint = new EntityBlueprint(this.entityManager, name);
		NodeList comps = node.getChildNodes();

		for (int i = 0; i < comps.getLength(); i++)
		{
			if (comps.item(i).getNodeType() == Node.ELEMENT_NODE)
			{
				Node n = comps.item(i);
				String componentType = n.getNodeName().toLowerCase();
				if (componentInfoTypes.containsKey(componentType))
				{
					blueprint.addComponentInfo(readComponentInfo(n));
				}

			}
		}
		UnittypeComponentInfo unittype = new UnittypeComponentInfo();
		unittype.unitType = name;
		blueprint.addComponentInfo(unittype);
		this.blueprints.put(name, blueprint);
	}

	public int createEntityFromBlueprint(String name)
	{
		return this.blueprints.get(name).generateEntity();
	}

	public int createEntityFromBlueprint(String name, int entityId)
	{
		return this.blueprints.get(name).generateEntity(entityId);
	}
}
