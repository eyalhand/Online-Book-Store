package bgu.spl.mics;

import java.util.concurrent.atomic.AtomicInteger;

public class OrderSchedule {

    private String title;
    private AtomicInteger orderTick;

    public OrderSchedule(String title, AtomicInteger orderTick){
        this.title = title;
        this.orderTick = orderTick;
    }

    public AtomicInteger getOrderTick() {
        return orderTick; }

    public String getTitle() {
        return title; }
}
