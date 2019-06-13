package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Customer;

/**
 * Gson class that initializing the services and customers.
 */
public class Services {

    public TimeSet time;
    public int selling;
    public int inventoryService;
    public int logistics;
    public int resourcesService;
    public Customer[] customers;

    /**
     * Since the Gson does not calls the constructor, using this function will initialize the fields that we don't get their value from the Json.
     */
    public void setCustomer(){
        for(int i = 0 ; i < customers.length ; i++){
            customers[i].setCustomer();
        }
    }
}
