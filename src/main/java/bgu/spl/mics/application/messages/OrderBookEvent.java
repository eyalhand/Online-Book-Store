package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;
import java.io.Serializable;

/**
 * created by an APIService in the correct timeTick and handled by SellingService
 *
 * @param <T>
 */
public class OrderBookEvent<T> implements Event<T> {

    int id;
    private String title;
    private Customer customer;
    private int orderTick;

    public OrderBookEvent(String title, Customer c, int id, int orderTick) {
        this.id = id;
        this.title = title;
        this.customer = c;
        this.orderTick = orderTick;
    }

    public String getTitle(){
        return title; }

    public Customer getCustomer() {
        return customer; }

    public int getId() {
        return id; }

    public int getOrderTick() {
        return orderTick; }
}
