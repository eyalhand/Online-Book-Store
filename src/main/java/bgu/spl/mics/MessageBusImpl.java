package bgu.spl.mics;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private  static class SingeltonHolder {
		private static MessageBusImpl instance = new MessageBusImpl();}

	private ConcurrentHashMap<MicroService,Queue<Message>> services;//An HashMap which represent MIcroServices and their queue of events.
	private ConcurrentHashMap<Class<? extends Event<?>>,RoundedQueue<MicroService>> subscribedEvents;//An HashMap which represent events and all the MicroServices which subscribe to it.
	private ConcurrentHashMap<Class<? extends Broadcast>, LinkedList<MicroService>> subscribedBrodcasts;//An HashMap which represent Broadcasts and all the MicroServices which subscribe to it.
	private ConcurrentHashMap<Event,Future> events;//An HashMap which represent events and their matchable future.
	private ConcurrentHashMap<MicroService,LinkedList <Class<? extends Event<?>>>> mSubscribedEvents;//An HashMap which represent MicroServices and their subscribed events.
	private ConcurrentHashMap<MicroService,LinkedList <Class<? extends Broadcast>>> mSubscribedBroadcast;//An HashMap which represent MicroServices and their subscribed broadcasts.

	private MessageBusImpl() {
		services = new ConcurrentHashMap<>();
		subscribedEvents = new ConcurrentHashMap<>();
		subscribedBrodcasts = new ConcurrentHashMap<>();
		events = new ConcurrentHashMap<>();
		mSubscribedEvents = new ConcurrentHashMap<>();
		mSubscribedBroadcast = new ConcurrentHashMap<>();
	}

	public static MessageBusImpl getInstance() {
	return SingeltonHolder.instance;
	}

	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		synchronized (subscribedEvents) {
			if (subscribedEvents.get(type) == null) {
				subscribedEvents.put(type, new RoundedQueueAsLinkedList<>());
			}
		}
		synchronized (subscribedEvents.get(type)) {
			subscribedEvents.get(type).enqueue(m);
		}
		mSubscribedEvents.get(m).add(type);}


	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (subscribedBrodcasts) {
			if (subscribedBrodcasts.get(type) == null) {
				subscribedBrodcasts.put(type, new LinkedList());
			}
		}
		synchronized (subscribedBrodcasts.get(type)) {
			subscribedBrodcasts.get(type).add(m);
		}
		mSubscribedBroadcast.get(m).add(type);
	}

	@Override
	public  <T> void complete(Event<T> e, T result) {
		events.get(e).resolve(result);
		events.remove(e);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		synchronized (subscribedBrodcasts.get(b.getClass())) {//?
			for (MicroService m : subscribedBrodcasts.get(b.getClass())) {
				services.get(m).add(b);
			}
		}
	}

	
	@Override
	public <T> Future<T>  sendEvent(Event<T> e) {
		Future future = new Future();
		events.put(e, future);
		MicroService temp = null;
		synchronized (subscribedEvents.get(e.getClass())) {
			if (subscribedEvents.get(e.getClass()) == null || subscribedEvents.get(e.getClass()).size() == 0)
				complete(e, null);
			else {
				temp = (MicroService) subscribedEvents.get(e.getClass()).moveBack();
			}
			if (temp != null)
				services.get(temp).add(e);
		}
		return future;
	}

	@Override
	public void register(MicroService m) {
		services.put(m,new LinkedBlockingQueue<>());
		mSubscribedEvents.put(m,new LinkedList<>());
		mSubscribedBroadcast.put(m,new LinkedList<>());
	}

	@Override
	public void unregister(MicroService m) {
		LinkedList l = mSubscribedEvents.get(m);
		for (int i = 0; i < l.size(); i++) {
			synchronized (subscribedEvents.get(l.get(i))) {
				subscribedEvents.get(l.get(i)).remove(m);
			}
		}

		LinkedList l2 = mSubscribedBroadcast.get(m);
		for (int i = 0; i < l2.size(); i++) {
			synchronized (subscribedBrodcasts.get(l2.get(i))) {
				subscribedBrodcasts.get(l2.get(i)).remove(m);
			}
		}
		Queue queue = services.get(m);
		for (int i = 0; i < queue.size(); i++) {
			complete((Event) queue.remove(), null);
		}
		services.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		try {
			return ((LinkedBlockingQueue<Message>) services.get(m)).take();
		} catch (InterruptedException e) {
			return null;
		}
	}
}
