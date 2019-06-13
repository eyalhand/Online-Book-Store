package bgu.spl.mics;

public class DeliveryEvent implements Event {

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
