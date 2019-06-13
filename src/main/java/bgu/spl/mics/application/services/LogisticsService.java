package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {

	private AtomicInteger Tick;
	private CountDownLatch latch;
	private int duration;
	private int speed;

	public LogisticsService(String name,CountDownLatch latch, int duration , int speed) {
		super(name);
		Tick = new AtomicInteger(1);
		this.latch = latch;
		this.duration = duration;
		this.speed = speed;
	}

	/**
	 * Flow: the logistic service creates an acquireVehicleEvent which will be handled by the resource service and will be supply a vehicle from the resource holder.
	 * The logistic service calls the deliver function with the supplied vehicle as an argument.
	 * After the deliver function is ended the logistic service creates a releaseVehicle event that will be handled by the resource service as well.
	 * Important mention: when the logistic service waits for the acquireVehicleEvent to supply a vehicle we use the future get(time) function,
	 * in order to make sure that a logistic service won't wait to a vehicle longer than the duration of the program.
	 * since after this time period all the resource services will be terminated and no service will be able to access the resource holder and provide a vehicle
	 *
	 */
	@Override
	protected void initialize() {
		subscribeEvent(DeliveryEvent.class,(DeliveryEvent d1) -> {
			AcquireVehicleEvent acquireVehicleEvent = new AcquireVehicleEvent();
			Future<Future<DeliveryVehicle>> vehicle = sendEvent(acquireVehicleEvent);
			DeliveryVehicle deliveryVehicle = null;
			if(vehicle.get((duration - Tick.get())*speed, TimeUnit.MILLISECONDS) != null)
			 	deliveryVehicle = vehicle.get().get((duration - Tick.get())*speed, TimeUnit.MILLISECONDS);
			if (deliveryVehicle != null) {
				try {
					deliveryVehicle.deliver(d1.getAddress(), d1.getDistance());
				} catch (InterruptedException e) {}

				Event<ReleaseVehicleEvent> releaseVehicleEvent = new ReleaseVehicleEvent(deliveryVehicle);
				Future future = sendEvent(releaseVehicleEvent);
				complete(d1,future.get());
			} else
				complete(d1,null);
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
