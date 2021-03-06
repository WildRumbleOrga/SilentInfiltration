package de.mih.core.game.ai.guard;

import de.mih.core.game.GameLogic;
import de.mih.core.game.components.PositionC;
import de.mih.core.game.components.StateMachineComponent;
import de.mih.core.game.components.StateMachineComponent.State;

public class Watching extends State
{

	public float maxFacing;
	public float minFacing;
	public float rotateSpeed;
	boolean up = true;
	
	private GameLogic game;
	
	public Watching(StateMachineComponent stateMachine, GameLogic game)
	{
		super(stateMachine);
		this.game = game;
	}

	@Override
	public void onEnter()
	{
		int       self         = this.stateMachine.entityID;
		PositionC posC         = game.getEntityManager().getComponent(self, PositionC.class);
		float     currentAngle = posC.getAngle();
		if (currentAngle >= minFacing && currentAngle <= maxFacing)
			up = true;
		else
			up = currentAngle > maxFacing;
	}

	@Override
	public void onLeave()
	{
	}

	@Override
	public void update()
	{
		int       self         = this.stateMachine.entityID;
		PositionC posC         = game.getEntityManager().getComponent(self, PositionC.class);
		float     currentAngle = posC.getAngle();
		if (up)
		{
			if (currentAngle <= maxFacing)
				currentAngle += rotateSpeed;
			else
				up = false;
		}
		else
		{
			if (currentAngle >= minFacing)
				currentAngle -= rotateSpeed;
			else
				up = true;
		}
		float radian = (float) Math.toRadians(currentAngle);
		posC.setFacing((float) (Math.cos(radian)),posC.getFacing().y,(float) (Math.sin(radian)));
		posC.setAngle(currentAngle);
	}
}
