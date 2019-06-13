package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.messages.OrderBookEvent;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.toOrder;


import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link OrderBookEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{

	private Customer customer;
	private List<toOrder> orderSchedule;
	private AtomicInteger Tick;
	private int id;
	private List<Future> unresolvedFutures; // A link of futures that will be resolved to receipts once the selling service will hanlde the suitable orderBook event.
	private CountDownLatch latch;

	public APIService(String name, List orderScheduale, Customer customer,CountDownLatch latch) {
		super(name);
		this.orderSchedule = orderScheduale;
		Tick = new AtomicInteger(1);
		this.customer = customer;
		id = 0;
		unresolvedFutures = new LinkedList();
		this.latch = latch;
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class,(TickBroadcast t1) -> {
			//sending all the order books event that need to be send in the current tick according to the customer order schedule
			for(int i = 0; i < orderSchedule.size(); i++) {
				 toOrder order= orderSchedule.get(i);
				if (order.tick == Tick.get()) {
					Event<OrderBookEvent> event = new OrderBookEvent(order.bookTitle,customer,id,order.tick);
					id++;
					Future<?> future = sendEvent(event);
					unresolvedFutures.add(future);
				}
			}
			Tick.incrementAndGet();
		});
		subscribeBroadcast(TerminateBroadcast.class,(TerminateBroadcast t1) -> {
			// adding all the receipts to the customer receipts list once we get to the the final tick and all of the customer's order book event should be completed or resolved to null.
			for (int i = 0; i < unresolvedFutures.size(); i++) {
				Future future = unresolvedFutures.get(i);
				OrderReceipt orderReceipt = (OrderReceipt) future.get();
				if (orderReceipt != null)
					customer.getCustomerReceiptList().add(orderReceipt);
			}
			terminate();

		});
		latch.countDown();
	}

}
