import bgu.spl.mics.Future;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import static org.junit.Assert.*;

public class FutureTest {

    private Future<Object> future;//the Future object which we will test our code on.

    @Before
    public void setUp() throws Exception {//constructor
        try {
            future = new Future<>();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void get() {
        Object o = new Object();//result
        future.resolve(o);
        assertEquals(o,future.get());//checks that the returned value is what it should be
    }

    @Test
    public void resolve() {
      Object o = new Object();//result
      future.resolve(o);
      assertEquals(o,future.get());//checks that the returned value is what it should be
      assertTrue(future.isDone());//should be resolved
    }

    @Test
    public void isDone() {
        assertFalse(future.isDone());//without the call to the resolve() function, should return false
        Object o = new Object();//result
        future.resolve(o);
        assertTrue(future.isDone());//now, after the resolve() function called, should return true.
    }

    @Test
    public void get1() {
        assertNull(future.get(1,TimeUnit.MILLISECONDS));//the future was not resolved
        Object result = new Object();//result
        future.resolve(result);
        assertNotNull(future.get(1,TimeUnit.MILLISECONDS));//now, the future was resolved
    }
}