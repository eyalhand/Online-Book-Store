package bgu.spl.mics.application.passiveObjects;

import java.io.*;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Passive object representing the store finance management. 
 * It should hold a list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class MoneyRegister implements Serializable {
	private  static class SingeltonHolder {
	private static MoneyRegister instance = new MoneyRegister(); }

	private ConcurrentLinkedQueue<OrderReceipt> receiptList;
	private AtomicInteger sum;//Total Earning of the store.

	private MoneyRegister() {
		receiptList = new ConcurrentLinkedQueue();
		sum = new AtomicInteger(0);
	}

	/**
     * Retrieves the single instance of this class.
     */
	public static MoneyRegister getInstance() {
		return SingeltonHolder.instance; }
	
	/**
     * Saves an order receipt in the money register.
     * <p>   
     * @param r		The receipt to save in the money register.
     */
	public void file(OrderReceipt r) {
		receiptList.add(r);
		sum.addAndGet(r.getPrice());
	}
	
	/**
     * Retrieves the current total earnings of the store.  
     */
	public int getTotalEarnings() {
		return sum.get(); }
	
	/**
     * Charges the credit card of the customer a certain amount of money.
     * <p>
     * @param amount 	amount to charge
     */
	public void chargeCreditCard(Customer c, int amount) {
		c.setAvailableCreditAmount(amount); }
	
	/**
     * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts 
     * currently in the MoneyRegister
     * This method is called by the main method in order to generate the output.. 
     */
	public void printOrderReceipts(String filename) {
		LinkedList <OrderReceipt> linkToPrint = new LinkedList();
		for (OrderReceipt or : receiptList) {
			linkToPrint.add(or);
		}
		try {
			FileOutputStream f1 = new FileOutputStream(new File(filename));
			ObjectOutputStream o = new ObjectOutputStream(f1);
			o.writeObject(linkToPrint);
			o.close();
			f1.close();
		} catch (Exception e) {}
	}
}
