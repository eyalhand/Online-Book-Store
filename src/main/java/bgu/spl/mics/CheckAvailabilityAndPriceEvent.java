package bgu.spl.mics;

public class CheckAvailabilityAndPriceEvent implements Event {

    private String title;

    public CheckAvailabilityAndPriceEvent(String title) {
        this.title = title; }

    public String getTitle() {
        return title; }
}
