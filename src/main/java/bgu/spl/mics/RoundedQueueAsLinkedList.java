package bgu.spl.mics;

import java.util.LinkedList;

/**
 * Our implementation of RoundedQueue - Round Robin style.
 * @param <T>
 */
public class RoundedQueueAsLinkedList<T> implements RoundedQueue<T> {

    private LinkedList queue;

    public RoundedQueueAsLinkedList() {
        queue = new LinkedList(); }

    @Override
    public void enqueue(T o) {
        if (o != null)
            queue.addLast(o);
    }

    @Override
    public  T moveBack() {
        if (size() != 0) {
            T temp = (T)queue.remove();
            queue.addLast(temp);
            return temp;
        }
        return null;
    }

    @Override
    public int size() {
        return queue.size(); }

    @Override
    public void remove(Object o) {
        if (queue.contains(o)) {
            for (int i = 0; i < queue.size(); i++) {
                if (o.equals(queue.get(i)))
                    queue.remove(i);
            }
        }
    }
}
