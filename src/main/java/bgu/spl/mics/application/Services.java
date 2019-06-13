package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Customer;

public class Services {

    public TimeSet time;
    public int selling;
    public int inventoryService;
    public int logistics;
    public int resourcesService;
    public Customer[] customers;

    public void setCustomer(){
        for(int i = 0 ; i < customers.length ; i++){
            customers[i].setCustomer();
        }
    }
}
