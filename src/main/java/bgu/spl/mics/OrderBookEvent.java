package bgu.spl.mics;
import bgu.spl.mics.application.passiveObjects.Customer;

import java.util.concurrent.atomic.AtomicInteger;


public class OrderBookEvent<T> implements Event<T> {

    int id;
    private String title;
    private Customer customer;
    private AtomicInteger orderTick;

    public OrderBookEvent(String title, Customer c, int id, AtomicInteger orderTick) {
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

    public AtomicInteger getOrderTick() {
        return orderTick; }
}
