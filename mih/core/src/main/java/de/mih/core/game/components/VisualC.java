package de.mih.core.game.components;

import de.mih.core.engine.ecs.Component;
import de.mih.core.engine.io.AdvancedAssetManager;
import de.mih.core.engine.io.ComponentParser;
import de.mih.core.engine.render.Visual;
import de.mih.core.game.systems.RenderSystem;

import org.w3c.dom.Node;

import com.badlogic.gdx.math.Vector3;

public class VisualC extends Component {
	public final static String name = "visual";

	
	public Visual visual;
	public VisualC()
	{
		
	}
	public VisualC(Visual visual)
	{
		this.visual = visual;
		AdvancedAssetManager.getInstance().allvisuals.add(this);
	}
	public VisualC(String m_type)
	{
		this.visual = new Visual(AdvancedAssetManager.getInstance().getModelByName(m_type));


		AdvancedAssetManager.getInstance().allvisuals.add(this);
	}
	
	public void onRemove(){
		hide();
	}
	
	public void show(){
		if (ishidden()) AdvancedAssetManager.getInstance().allvisuals.add(this);
	}
	
	public void hide(){
		if (!ishidden()) AdvancedAssetManager.getInstance().allvisuals.remove(this);
	}
	
	public boolean ishidden(){
		return !AdvancedAssetManager.getInstance().allvisuals.contains(this);
	}
	
	public void setScale(float x, float y, float z){
		visual.setScale(x, y, z);
	}
	
	public Vector3 getScale(){
		return visual.getScale();
	}

	@Override
	public Component cpy() {
		return new VisualC(new Visual(visual));
	}
	@Override
	public void setField(String fieldName, String fieldValue) {
		// TODO Auto-generated method stub
		switch(fieldName)
		{
			case "model":
				this.visual = new Visual(AdvancedAssetManager.getInstance().getModelByName(fieldValue));
				break;
		}
	}
	
}
