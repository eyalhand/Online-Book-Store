package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderResult;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link OrderBookEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService {

	private static MoneyRegister moneyRegister;
	private AtomicInteger Tick;
	private CountDownLatch latch;

	public SellingService(String name,CountDownLatch latch) {
		super(name);
		moneyRegister = MoneyRegister.getInstance();
		Tick = new AtomicInteger(1);
		this.latch = latch;
	}

	/**
	 * Flow: The selling service creates a CheckAvailabilityAndPriceEvent and waits for it's result.
	 * If the CheckAvailabilityAndPriceEvent is resolved to null or returns the value -1 the orderBook event is resolved to null.
	 * elsewhere the selling service locks the customer from this point to the end of the order (using semaphore as explained in the customer class),
	 * and checks if the customer has enough money in order to purchase the wanted book.
	 * If the customer has enough money the selling service creates a takeBook event and waits for it's result. elsewhere the orderBook event is resolved to null.
	 * If the takeBook event result is that the book exists in the stock the selling service creates a suitable receipt and resolve the event with the receipt. elsewhere the orderBook event is resolved to null.
	 */
	@Override
	protected void initialize() {
		subscribeEvent(OrderBookEvent.class,(OrderBookEvent o1) -> {
			int processTick = Tick.get();
			Event<CheckAvailabilityAndPriceEvent> checkAvailabilityAndPriceEvent1 = new CheckAvailabilityAndPriceEvent(o1.getTitle());
			Future<?> future1 = sendEvent(checkAvailabilityAndPriceEvent1);
			if (future1.get() != null) { //if checkAvailability is not null (1)
				int bookPrice = (Integer) future1.get();
				Customer customer = o1.getCustomer();
				if (!(bookPrice == -1)) { //if Not In Stock (2)
					try {
						customer.semaphore.acquire();
					} catch (InterruptedException e) {}
					if (o1.getCustomer().getAvailableCreditAmount() >= bookPrice) { //if customer doesn't have enough money (3)
						Event<TakeBookEvent> takeBookEvent1 = new TakeBookEvent(o1.getTitle());
						Future<?> future2 = sendEvent(takeBookEvent1);
						if (future2.get() != null) { //if takeBook is not null (4)
							OrderResult orderResult = (OrderResult) future2.get();
							if (orderResult != OrderResult.NOT_IN_STOCK) {//if Available (5)
								OrderReceipt receipt = new OrderReceipt(o1.getId(), this.getName(), customer.getId(), o1.getTitle(), bookPrice, processTick, Tick.get(), o1.getOrderTick());
								moneyRegister.chargeCreditCard(customer, bookPrice);
								moneyRegister.file(receipt);
								complete(o1, receipt);
								customer.semaphore.release();
								Event<DeliveryEvent> deliveryEvent = new DeliveryEvent(customer.getDistance(), customer.getAddress());
								sendEvent(deliveryEvent);
							} else { // (5)
								customer.semaphore.release();
								complete(o1, null);
							}
						} else {// (4)
							customer.semaphore.release();
							complete(o1, null);
						}
					} else {// (3)
						customer.semaphore.release();
						complete(o1, null);
					}
				} else// (2)
					complete(o1, null);
			} else// (1)
				complete(o1,null);
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
