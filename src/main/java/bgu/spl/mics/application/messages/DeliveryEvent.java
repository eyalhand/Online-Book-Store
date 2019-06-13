package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import java.io.Serializable;

/**
 * created when an orderBookEvent is completed and handled by the logisticService
 */
public class DeliveryEvent implements Event,Serializable {

    private int distance;
    private String address;

    public DeliveryEvent(int distance,String address) {
        this.distance = distance;
        this.address = address;
    }

    public int getDistance(){
        return distance; }

    public String getAddress() {
        return address; }
}
