package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import java.io.Serializable;

/**
 * created as part of DeliveryEvent and handled by the resourceService
 */
public class ReleaseVehicleEvent implements Event {

    private DeliveryVehicle vehicle;

    public ReleaseVehicleEvent(DeliveryVehicle vehicle) {
        this.vehicle = vehicle; }

    public DeliveryVehicle getVehicle() {
        return vehicle; }
}
