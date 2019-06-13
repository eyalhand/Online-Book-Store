package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a information about a certain book in the inventory.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class BookInventoryInfo implements Serializable {

	private String bookTitle;
	private int price;
	private AtomicInteger amount;
	protected Semaphore semaphore; //The amount of the book in the inventory is represented by a semaphore.

	public BookInventoryInfo (String bookName, int price, int amountInInventory) {
		this.bookTitle = bookName;
		this.price = price;
		this.amount = new AtomicInteger(amountInInventory);
	}

	/**
     * Retrieves the title of this book.
     * <p>
     * @return The title of this book.   
     */
	public String getBookTitle() {
		return bookTitle; }

	/**
     * Retrieves the amount of books of this type in the inventory.
     * <p>
     * @return amount of available books.      
     */
	public int getAmountInInventory() {
		return amount.get(); }

	/**
     * Retrieves the price for  book.
     * <p>
     * @return the price of the book.
     */
	public int getPrice() {
		return price; }

	public void setAmountInInventory() {
		amount.decrementAndGet(); }

	/**
	 * Since the Gson does not calls the constructor, using this function will initialize the fields that we don't get their value from the Json.
	 */
	public void setBook() {
		semaphore = new Semaphore(amount.get()); }
}
