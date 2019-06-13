package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class ReleaseVehicleEvent implements Event {

    private DeliveryVehicle vehicle;

    public ReleaseVehicleEvent(DeliveryVehicle vehicle) {
        this.vehicle = vehicle; }

    public DeliveryVehicle getVehicle() {
        return vehicle; }
}
