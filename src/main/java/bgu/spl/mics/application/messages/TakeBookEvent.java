package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import java.io.Serializable;

/**
 * created as part of orderBookEvent and handled by the inventoryService
 */
public class TakeBookEvent implements Event {

    private String title;

    public TakeBookEvent(String title) {
        this.title = title; }

    public String getTitle() {
        return title; }
}
