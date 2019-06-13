package bgu.spl.mics.application;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import java.io.FileReader;
import java.io.FileNotFoundException;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;


/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static void main(String[] args) {
        Input toStart = null;
        Gson gson = new Gson();
        try {
            JsonReader reader = new JsonReader(new FileReader(args[0]));
            Input init = gson.fromJson(reader, Input.class);
            Inventory inventory = Inventory.getInstance();
            init.setBooks();
            init.services.setCustomer();
            inventory.load(init.initialInventory);
            ResourcesHolder resource = ResourcesHolder.getInstance();
            resource.load(init.initialResources[0].vehicles);
            toStart = init;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        LinkedList<Thread> servicesList = new LinkedList<>();
        int counter = toStart.services.selling + toStart.services.customers.length + toStart.services.inventoryService + toStart.services.logistics + toStart.services.resourcesService;
        CountDownLatch latch = new CountDownLatch(counter);

        //Creating sellingServices.
        for (int i = 0; i < toStart.services.selling; i++) {
            MicroService sellingService = new SellingService("sellingService" + (i + 1), latch);
            Thread r = new Thread(sellingService);
            servicesList.add(r);
            r.start();
        }

        //Creating inventoryServices.
        for (int i = 0; i < toStart.services.inventoryService; i++) {
            MicroService inventoryService = new InventoryService("inventoryService" + (i + 1), latch);
            Thread r = new Thread(inventoryService);
            servicesList.add(r);
            r.start();
        }

        //Creating resourceServices
        for (int i = 0; i < toStart.services.resourcesService; i++) {
            MicroService resourcesService = new ResourceService("resourcesService" + (i + 1), latch);
            Thread r = new Thread(resourcesService);
            servicesList.add(r);
            r.start();
        }

        //Creating logisticServices.
        for (int i = 0; i < toStart.services.logistics; i++) {
            MicroService logisticsService = new LogisticsService("logistics" + (i + 1), latch,toStart.services.time.duration, toStart.services.time.speed);
            Thread r = new Thread(logisticsService);
            servicesList.add(r);
            r.start();
        }

        //Creating customers and its matchable APIService.
        Map<Integer, Customer> customersToPrint = new HashMap<>();
        Customer[] customers = new Customer[toStart.services.customers.length];//delete after tester
        for (int i = 0; i < toStart.services.customers.length; i++) {
            LinkedList<toOrder> orderSchedules = new LinkedList<>();
            Customer c = toStart.services.customers[i];
            customersToPrint.put(c.getId(), c);
            customers[i] = c;
            for (int j = 0; j < c.orderSchedule.length; j++)
                orderSchedules.add(c.orderSchedule[j]);
            MicroService apiService = new APIService("apiService" + (i + 1), orderSchedules, c, latch);
            Thread r = new Thread(apiService);
            servicesList.add(r);
            r.start();

        }

        //Creating the timeService.
        TimeService timeService = new TimeService("timeService1", toStart.services.time.duration, toStart.services.time.speed, latch);
        Thread timeServiceRunner = new Thread(timeService);
        servicesList.add(timeServiceRunner);
        try {
            latch.await();//Making sure that timeService will be created last of all the services.
        } catch (InterruptedException e) {}
        timeServiceRunner.start();
        for (Thread t : servicesList) {
            try {
                t.join();//Making sure that all the printing to file in our program will occur after all the services was terminated.
            } catch (InterruptedException e) {}
        }

        FileOutputStream customersPrint;
        try {
            customersPrint = new FileOutputStream(args[1]);
            ObjectOutputStream o = new ObjectOutputStream(customersPrint);
            o.writeObject(customersToPrint);
            o.close();
            customersPrint.close();
        } catch (IOException e) {}

        Inventory.getInstance().printInventoryToFile(args[2]);
        MoneyRegister.getInstance().printOrderReceipts(args[3]);
        FileOutputStream moneyRegisterPrint = null;
        try {
            moneyRegisterPrint = new FileOutputStream(args[4]);
            ObjectOutputStream o = new ObjectOutputStream(moneyRegisterPrint);
            o.writeObject(MoneyRegister.getInstance());
            o.close();
            moneyRegisterPrint.close();
        } catch (IOException e) {}
    }
}
