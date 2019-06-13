package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;

/**
 * Gson class that initializing all the inputs from the Json.
 */
public class Input {

    public BookInventoryInfo[] initialInventory;
    public VehiclesHolder[] initialResources;
    public Services services;

    /**
     * Since the Gson does not calls the constructor, using this function will initialize the fields that we don't get their value from the Json.
     */
    public void setBooks(){
        for(int i = 0 ; i < initialInventory.length;i++){
            initialInventory[i].setBook();
        }
    }
}
