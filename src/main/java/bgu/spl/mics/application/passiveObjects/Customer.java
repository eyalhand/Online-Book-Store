package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.application.CreditCard;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer implements Serializable {

	private int id;
	private String name;
	private int distance;
	private String address;
	private int creditCardNumber;
	private int creditCardBalance;
	private List<OrderReceipt> receiptList;
	public Semaphore semaphore; //Used in order to lock the customer from ordering 2 books in parallel
	public CreditCard creditCard;
	public toOrder[] orderSchedule;


	public Customer(int id, String name, int distance, String address, int creditCardNumber, int creditCardBalance) {
		this.id = id;
		this.name = name;
		this.address = address;
		this.distance = distance;
		this.creditCardBalance = creditCardBalance;
		this.creditCardNumber = creditCardNumber;
		receiptList = new LinkedList<>();
		semaphore = new Semaphore(1);
	}

	/**
     * Retrieves the name of the customer.
     */
	public String getName() {
		return name; }

	/**
     * Retrieves the ID of the customer  . 
     */
	public int getId() {
		return id; }
	
	/**
     * Retrieves the address of the customer.  
     */
	public String getAddress() {
		return address; }
	
	/**
     * Retrieves the distance of the customer from the store.  
     */
	public int getDistance() {
		return distance; }
	
	/**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     * @return A list of receipts.
     */
	public List<OrderReceipt> getCustomerReceiptList() {
		return receiptList; }
	
	/**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     * @return Amount of money left.
     */
	public int getAvailableCreditAmount() {
		return creditCardBalance; }
	
	/**
     * Retrieves this customers credit card serial number.    
     */
	public int getCreditNumber() {
		return creditCardNumber; }

	/**
	 * Sets the creditCard amount after an order was completed.
	 */
	public void setAvailableCreditAmount(int amount) {
		creditCardBalance = creditCardBalance - amount; }

	/**
	 * Since the Gson does not calls the constructor, using this function will initialize the fields that we don't get their value from the Json.
	 */
	public void setCustomer() {
		semaphore = new Semaphore(1);
		this.creditCardBalance = creditCard.amount;
		this.creditCardNumber = creditCard.number;
		this.receiptList = new LinkedList<>();
	}
}
