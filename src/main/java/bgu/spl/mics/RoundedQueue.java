package bgu.spl.mics;

public interface RoundedQueue<T> {

    public void enqueue(T o);

    public Object moveBack();

    public int size();

    public void remove(Object o);
}
