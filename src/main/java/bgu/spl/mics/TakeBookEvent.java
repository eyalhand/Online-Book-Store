package bgu.spl.mics;

public class TakeBookEvent implements Event {

    private String title;

    public TakeBookEvent(String title) {
        this.title = title; }

    public String getTitle() {
        return title; }
}
