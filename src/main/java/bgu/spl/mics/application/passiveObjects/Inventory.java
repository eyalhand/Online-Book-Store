package bgu.spl.mics.application.passiveObjects;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Array;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory implements Serializable {

	private static class singeltonHolder {
		private static Inventory instance = new Inventory(); }

	private ConcurrentHashMap <String,BookInventoryInfo> booksInventory;

	private Inventory() {
		booksInventory = new ConcurrentHashMap<>(); }

	/**
	 * Retrieves the single instance of this class.
	 */
	public static Inventory getInstance() {
		return singeltonHolder.instance; }
	
	/**
     * Initializes the store inventory. This method adds all the items given to the store
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
     */
	public void load (BookInventoryInfo[] inventory ) {
		for(int i = 0; i < inventory.length; i++) {
			booksInventory.put(inventory[i].getBookTitle(),inventory[i]);
		}
	}
	
	/**
     * Attempts to take one book from the store.
     * <p>
     * @param book 		Name of the book to take from the store
     * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
     * 			The first should not change the state of the inventory while the 
     * 			second should reduce by one the number of books of the desired type.
     */
	public OrderResult take (String book) {
		BookInventoryInfo book1 = booksInventory.get(book);
		if(!book1.semaphore.tryAcquire()) //Checks if there is a book with this title in the inventory.
			return OrderResult.NOT_IN_STOCK;
		book1.setAmountInInventory();
		return OrderResult.SUCCESSFULLY_TAKEN;
	}
	
	/**
     * Checks if a certain book is available in the inventory.
     * <p>
     * @param book 		Name of the book.
     * @return the price of the book if it is available, -1 otherwise.
     */
	public int checkAvailabiltyAndGetPrice(String book) {
		if (booksInventory.get(book) != null && booksInventory.get(book).getAmountInInventory() != 0)
			return booksInventory.get(book).getPrice();
		else
			return -1;
	}
	
	/**
     * 
     * <p>
     * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
     * should be the titles of the books while the values (type {@link Integer}) should be
     * their respective available amount in the inventory. 
     * This method is called by the main method in order to generate the output.
     */
	public void printInventoryToFile(String filename){
		try {
			FileOutputStream f1 = new FileOutputStream(new File(filename));
			ObjectOutputStream o = new ObjectOutputStream(f1);
			Map <String,Integer> booksToPrint = new HashMap<>();
			Iterator it = booksInventory.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry map = (Map.Entry) it.next();
				booksToPrint.put((String) map.getKey(), ((BookInventoryInfo) map.getValue()).getAmountInInventory());
			}
			o.writeObject(booksToPrint);
			o.close();
			f1.close();
		} catch (Exception e) {
		}
	}
}
