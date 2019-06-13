package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import bgu.spl.mics.application.passiveObjects.Inventory;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import bgu.spl.mics.application.messages.TerminateBroadcast;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	private long tick;
	private Timer timer;
	private TimerTask timerTask;
	private int duration;
	private int speed;
	private CountDownLatch latch;

	public TimeService(String name, int duration, int speed, CountDownLatch latch) {
		super(name);
		this.duration = duration;
		this.speed = speed;
		timer = new Timer();
		this.latch = latch;
		timerTask = new TimerTask() {
			@Override
			public void run() {
				tick++;
				if (tick == duration) {
					Broadcast tickBroadcast = new TickBroadcast(tick);
					sendBroadcast(tickBroadcast);
					timerTask.cancel();
					timer.cancel();
					// sending a terminate broadcast to all the services once the program gets to its final tick.
					Broadcast terminate = new TerminateBroadcast();
					sendBroadcast(terminate);
				}
				else {
					Broadcast tickBroadcast = new TickBroadcast(tick);
					sendBroadcast(tickBroadcast);
				}
			}
		};
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TerminateBroadcast.class,(TerminateBroadcast t1) -> {
			terminate();
		});
		timer.schedule(timerTask,speed,speed);
	}
}
