package de.mih.core.game.network;

import java.io.IOException;
import java.net.InetAddress;

import de.mih.core.engine.ecs.events.BaseEvent;
import de.mih.core.engine.ecs.events.BaseEvent.EntityEvent;
import de.mih.core.engine.ecs.events.EventDatagram;
import de.mih.core.engine.ecs.events.EventListener;
import de.mih.core.engine.network.server.Connection;
import de.mih.core.engine.network.server.DatagramReceiveHandler;
import de.mih.core.engine.network.server.UDPClient;
import de.mih.core.engine.network.server.datagrams.BaseDatagram;
import de.mih.core.engine.network.server.datagrams.ConnectApprove;
import de.mih.core.engine.network.server.datagrams.ConnectRequest;
import de.mih.core.game.Game;
import de.mih.core.game.GameLogic;
import de.mih.core.game.components.PositionC;
import de.mih.core.game.events.order.SelectEvent;
import de.mih.core.game.network.datagrams.PlayerJoinedDatagram;
import de.mih.core.game.player.Player;

public class GameClient implements DatagramReceiveHandler, EventListener//<BaseEvent>
{
	UDPClient client;
	GameLogic game;
	
	public GameClient(GameLogic game, String ip, int port) throws IOException
	{
		this.game = game;
		client = new UDPClient(ip, port);
		client.setDatagramReceiveHandler(this);
		client.start();
		game.getEventManager().register(this);
		client.sendData(new ConnectRequest(), true);
	}
	
	
//	public void connect(int timeout, String address, int tcpPort, int udpPort) throws IOException
//	{
//		System.out.println(address + " " +  tcpPort + " " + udpPort);
//		new Thread("GameClientConnect") {
//			public void run()
//			{
//				try
//				{
//					client.connect(timeout, address, tcpPort,udpPort);
//				}
//				catch (IOException ex)
//				{
//					ex.printStackTrace();
//					System.exit(1);
//				}
//			}
//		}.start();
//	}

	
	@Override
	public void connected(Connection connection, ConnectApprove datagram)
	{		
		int robo = game.getBlueprintManager().createEntityFromBlueprint("robocop.json", datagram.playerId);
		game.getEntityManager().getComponent(robo, PositionC.class).setPos(8, 0, 53);
		
//		if(((Game) GameLogic.getCurrentGame()).getActivePlayer().getHero() == -1)
//		{
		((Game) GameLogic.getCurrentGame()).getActivePlayer().setHero(robo);
//		}
		
	}


	@Override
	public void disconnected(Connection connection)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receive(Connection connection, BaseDatagram datagram) throws IOException
	{
		System.out.println("got packet");
		if(datagram instanceof EventDatagram)
		{
			EventDatagram eventDatagram = (EventDatagram) datagram;
			eventDatagram.event.fromRemote = true;
			game.getEventManager().fire(eventDatagram.event);
		}
		else if(datagram instanceof PlayerJoinedDatagram)
		{
			PlayerJoinedDatagram playerJoined = (PlayerJoinedDatagram) datagram;
			int robo = game.getBlueprintManager().createEntityFromBlueprint("robocop.json", playerJoined.heroID);
			game.getEntityManager().getComponent(robo, PositionC.class).setPos(8, 0, 53);
		}
	}
	

	@Override
	public void packetLost(Connection connection, BaseDatagram lostDatagram)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleEvent(BaseEvent event)
	{
		System.out.println("client handle event");
		//TODO: better filtering?
		if(event instanceof SelectEvent)
			return;
		if(!event.fromRemote && event instanceof EntityEvent && !event.onlyServerSends && ((EntityEvent) event).entityId == ((Game) Game.getCurrentGame()).getActivePlayer().getHero())
		{
			System.out.println("Sending: " + event.toString());
			EventDatagram eventDatagram = new EventDatagram();
			eventDatagram.event = event;
			client.sendData(eventDatagram, true);
		}
	}


}


