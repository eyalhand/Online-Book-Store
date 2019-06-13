package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;

public class Input {

    public BookInventoryInfo[] initialInventory;
    public VehiclesHolder[] initialResources;
    public Services services;

    public void setBooks(){
        for(int i = 0 ; i<initialInventory.length;i++){
            initialInventory[i].setBook();
        }
    }
}
