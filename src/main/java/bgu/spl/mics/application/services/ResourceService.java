package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.AcquireVehicleEvent;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourcesHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{

	private ResourcesHolder resourcesHolder;
	private AtomicInteger Tick;
	private CountDownLatch latch;

	public ResourceService(String name,CountDownLatch latch) {
		super(name);
		Tick = new AtomicInteger(1);
		resourcesHolder = ResourcesHolder.getInstance();
		this.latch = latch;
	}

	@Override
	protected void initialize() {
		subscribeEvent(AcquireVehicleEvent.class,(AcquireVehicleEvent a1) -> {
			Future<DeliveryVehicle> vehicle = resourcesHolder.acquireVehicle();
			complete(a1,vehicle);
		});
		subscribeEvent(ReleaseVehicleEvent.class,(ReleaseVehicleEvent r1) -> {
			resourcesHolder.releaseVehicle(r1.getVehicle());
			complete(r1,true);
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
