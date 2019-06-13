package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import bgu.spl.mics.Future;


/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder implements Serializable {
	private  static class SingeltonHolder {
		private static ResourcesHolder instance = new ResourcesHolder();}

	private ConcurrentLinkedQueue<DeliveryVehicle> deliveryVehicleQueue;//A queue of the available vehicles.
	private Semaphore semaphore;//Represent the current amount of vehicles.
	private ConcurrentLinkedQueue<Future<DeliveryVehicle>> waitingVehicles;//A queue of futures (which represent orders) that need to be resolved by an allocation of a vehicle.

	private ResourcesHolder() {
		deliveryVehicleQueue = new ConcurrentLinkedQueue<>();
		waitingVehicles = new ConcurrentLinkedQueue<>();
	}
	
	/**
     * Retrieves the single instance of this class.
     */
	public static ResourcesHolder getInstance() {
		return SingeltonHolder.instance;
	}
	
	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public Future<DeliveryVehicle> acquireVehicle() {
		Future<DeliveryVehicle> future = new Future();
		/* locking the waitingVehicles queue in order to prevent a case in which a vehicle will be released before the needed future will be added to the waitingVehicles queue.
		in this case the relevant future might never be resolved with a vehicle.
		 */

		synchronized (waitingVehicles) {
			if (semaphore.tryAcquire())
				future.resolve(deliveryVehicleQueue.remove());
			else
				waitingVehicles.add(future);
		}
		return future;
	}
	
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		if (waitingVehicles.size() > 0)
				waitingVehicles.poll().resolve(vehicle);
		else {
			deliveryVehicleQueue.add(vehicle);
			semaphore.release();
		}
	}
	
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
		for (int i = 0; i < vehicles.length; i++)
			deliveryVehicleQueue.add(vehicles[i]);
		semaphore = new Semaphore(deliveryVehicleQueue.size());
	}
}
