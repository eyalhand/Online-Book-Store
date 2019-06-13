package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import java.io.Serializable;

/**
 * A broadcast which is sent every tick of the program by TimeService
 */
public class TickBroadcast implements Broadcast {

    private long Tick;

    public TickBroadcast(long Tick) {
        this.Tick = Tick; }

    public long getTick(){
        return getTick(); }
}
