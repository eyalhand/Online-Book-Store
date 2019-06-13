package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import java.io.Serializable;

/**
 * created as part of the orderBookEvent and handled by the inventoryService
 */
public class CheckAvailabilityAndPriceEvent implements Event {

    private String title;

    public CheckAvailabilityAndPriceEvent(String title) {
        this.title = title; }

    public String getTitle() {
        return title; }
}
