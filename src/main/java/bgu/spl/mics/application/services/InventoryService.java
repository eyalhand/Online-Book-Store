package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.CheckAvailabilityAndPriceEvent;
import bgu.spl.mics.application.messages.TakeBookEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{

	private MessageBusImpl messageBus;
	private Inventory inventory;
	private AtomicInteger Tick;
	private CountDownLatch latch;

	public InventoryService(String name,CountDownLatch latch) {
		super(name);
		Tick = new AtomicInteger(1);
		inventory = Inventory.getInstance();
		messageBus = MessageBusImpl.getInstance();
		this.latch = latch;
	}

	@Override
	protected void initialize() {
		subscribeEvent(CheckAvailabilityAndPriceEvent.class,(CheckAvailabilityAndPriceEvent c1) -> {
			complete(c1,inventory.checkAvailabiltyAndGetPrice(c1.getTitle()));
		});
		subscribeEvent(TakeBookEvent.class, (TakeBookEvent t1)-> {
			complete(t1,inventory.take(t1.getTitle()));
		});
		subscribeBroadcast(TickBroadcast.class,(TickBroadcast t1) -> {
			Tick.incrementAndGet();
		});
		subscribeBroadcast(TerminateBroadcast.class,(TerminateBroadcast t1) -> {
			terminate();
		});
		latch.countDown();
	}
}
