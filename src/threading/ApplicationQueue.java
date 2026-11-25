package threading;

import model.Application;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Thread-safe shared bounded buffer holding applications awaiting verification.
 * Demonstrates Synchronization, monitor locks, and Inter-Thread Communication
 * using guarded while loops with wait() and notifyAll().
 */
public class ApplicationQueue {

    private final Queue<Application> queue;
    private final int capacity;
    private boolean productionFinished;

    public ApplicationQueue(int capacity) {
        this.queue = new LinkedList<>();
        this.capacity = capacity > 0 ? capacity : 10;
        this.productionFinished = false;
    }

    /**
     * Enqueues an application into the buffer.
     * Blocks if the buffer is currently at full capacity.
     */
    public synchronized void enqueue(Application app) throws InterruptedException {
        while (queue.size() >= capacity) {
            wait(); // Guarded block waiting for consumers to dequeue
        }
        queue.add(app);
        notifyAll(); // Wake up waiting consumers
    }

    /**
     * Dequeues an application from the buffer.
     * Blocks if the buffer is empty until an item is enqueued or production finishes.
     */
    public synchronized Application dequeue() throws InterruptedException {
        while (queue.isEmpty() && !productionFinished) {
            wait(); // Guarded block waiting for producer to enqueue
        }
        if (queue.isEmpty()) {
            return null; // Production finished and queue drained
        }
        Application app = queue.poll();
        notifyAll(); // Wake up waiting producers
        return app;
    }

    /**
     * Signals that no more applications will be produced.
     */
    public synchronized void markProductionFinished() {
        this.productionFinished = true;
        notifyAll(); // Wake up all waiting consumers to notice termination
    }

    public synchronized boolean isDone() {
        return productionFinished && queue.isEmpty();
    }

    public synchronized int size() {
        return queue.size();
    }
}
