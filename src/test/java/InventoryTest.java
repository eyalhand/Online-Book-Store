import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Inventory;
import static bgu.spl.mics.application.passiveObjects.OrderResult.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.*;

public class InventoryTest {

  private Inventory inventory;
  private BookInventoryInfo[] inventoryList;


    @Before
    public void setUp() throws Exception { // create new inventory and books that we will use in the test
        inventory = inventory.getInstance();
        BookInventoryInfo book1 = new BookInventoryInfo( "The Catcher in the rye", 2, 2 );
        BookInventoryInfo book2 = new BookInventoryInfo( "The Fountainhead", 6, 3 );
        inventoryList = new BookInventoryInfo [2];
        inventoryList [0] = book1;
        inventoryList [1] = book2;
    }

    @Test
    public void getInstance() { // checks the get instance function
        assertNotNull(inventory);
        Inventory inventory2 = Inventory.getInstance();
        assertSame(inventory,inventory2);
    }

    @Test
    public void load() {
        inventory.load(inventoryList);
        for (int i = 0; i < inventoryList.length; i++) { // check if the load function indeed loaded the right books.
            assertTrue(inventory.checkAvailabiltyAndGetPrice(inventoryList[i].getBookTitle()) == inventoryList[i].getPrice());
            assertFalse(inventory.checkAvailabiltyAndGetPrice(inventoryList[i].getBookTitle()) != inventoryList[i].getPrice());
        }
        BookInventoryInfo [] inventoryList2 = new BookInventoryInfo[1];
        inventoryList2[0] = new BookInventoryInfo("The book of love", 10, 1);
        inventory.load(inventoryList2);
        for (int i = 0; i < inventoryList.length; i++) { // checks if the second load worked and didn't influnce the books that were already in the inventory
            assertTrue(inventory.checkAvailabiltyAndGetPrice(inventoryList[i].getBookTitle()) == inventoryList[i].getPrice());
            assertFalse(inventory.checkAvailabiltyAndGetPrice(inventoryList[i].getBookTitle()) != inventoryList[i].getPrice());
        }
        assertTrue(inventory.checkAvailabiltyAndGetPrice(inventoryList2[0].getBookTitle()) == inventoryList2[0].getPrice());
    }

    @Test
    public void take() { // checks if after we load books the take function retrieves the right books and update their amount in inventory
      inventory.load(inventoryList);
      for (int i = 0; i < inventoryList.length; i++) {
          while (inventoryList[i].getAmountInInventory() > 0) {
              assertEquals(SUCCESSFULLY_TAKEN, inventory.take(inventoryList[i].getBookTitle()));
          } // while
          assertEquals(NOT_IN_STOCK, inventory.take(inventoryList[i].getBookTitle()));
      }// for
        assertEquals(NOT_IN_STOCK ,inventory.take("book Doesn't Exist"));
    }

    @Test
    public void checkAvailabiltyAndGetPrice() { // checks if the this function finds the books that were loaded to the inventory and if it retrieves the right price.
    inventory.load(inventoryList);
    for (int i = 0; i < inventoryList.length; i++) {
      assertTrue(inventoryList[i].getPrice() == inventory.checkAvailabiltyAndGetPrice(inventoryList[i].getBookTitle()));
      assertFalse(inventoryList[i].getPrice() != inventory.checkAvailabiltyAndGetPrice(inventoryList[i].getBookTitle()));
    }
        }

    @Test
    public void printInventoryToFile() {
    }

    @After
    public void tearDown() throws Exception {
        inventory = null; }
}