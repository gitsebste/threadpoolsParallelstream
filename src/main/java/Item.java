
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class Item {
    
    //static long s,e;

    private static void usingBlockinQ() {
        final BlockingQueue<Item> queue = new ArrayBlockingQueue<>(100);
        for (int i=0;i<4;i++)new Thread(new Runnable() {
            public void run() {
                for (int it=0;it<25;it++) {
                    try {Item i=new Item();i.produceMe();
                        queue.put(i);
                    } catch (Exception e) { 
                    }
                }
            }
        }).start();
        for (int i=0;i<3;i++)new Thread(new Runnable() {
            public void run() {
                try { 
                    queue.take().consumeMe();
                    //if(allConsumed.get()==99)System.out.println(System.nanoTime()-s);
                    if(allConsumed.incrementAndGet()<98)this.run();
                } catch (Exception e) { 
                }
            }
        }).start();
    }

    private static void streamapi() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        Stream.of(new Thread[7]).forEach(t->{t = new Runnable() {
//            @Override
//            public void run() {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//            }
//        };});
 final BlockingQueue<Item> queue = new ArrayBlockingQueue<>(100);
        Stream.of(new Thread[4]).parallel().map(t->{return new Thread(new Runnable() {
            public void run() {
                Stream.of(new int[25]).parallel().forEach(t->{Item i=new Item();i.produceMe();
                    try {
                        queue.put(i);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Item.class.getName()).log(Level.SEVERE, null, ex);
                    }
});
            }
        });}).forEach(t->t.start()); 
        
                Stream.of(new Thread[3]).parallel().map(t->{return new Thread(new Runnable() {
            public void run() {
                try { 
                    queue.take().consumeMe();
                    if(allConsumed.incrementAndGet()<98)this.run();
                } catch (Exception e) { 
                }
            }
        });}).forEach(t->t.start()); 

    }
    
     static class ConsumerThread extends Thread {
@Override public void run() { 
    items.stream().forEach(i->i.consumeMe());
}
     }
          static class ProducerThread extends Thread {
@Override public void run() { 
    items.stream().forEach(i->i.produceMe());
}
     }
                    static class ProducerOrConsumerThread extends Thread {
@Override public void run() { 
//    //producing speed/consuming speed = 3/2
//    try{items.p.stream().forEach(i->i.consumeMe());}catch(Exception e){
//        if(allProduced.incrementAndGet()<5)
//        items.stream().forEach(i->i.produceMe());
if(items.size()>0)try {
    System.out.println("items.size() = "+items.size());
    items.take().consumeMe();
    if(allConsumed.getAndIncrement()<100){run();}
} catch (Exception ex) {
    ex.printStackTrace();
    Logger.getLogger(Item.class.getName()).log(Level.SEVERE, null, ex);
}
else {
    synchronized(this){
        if(allProduced.getAndIncrement()<100){
            Item i = new Item();
            i.produceMe();
            try {
                items.put(i);
                run();
            } catch (InterruptedException ex) {
                Logger.getLogger(Item.class.getName()).log(Level.SEVERE, null, ex);
            }
            
}
                }
    
}
    }
}
     

    
    static BlockingQueue<Item> items;
    public static void main(String[] args) {
        //java_wyklad.pdf (114) - kolekcje bezpieczne wątkowo
        //● CopyOnWriteArrayList<E> ● CopyOnWriteArraySet<E>
        //● ConcurrentLinkedQueue<E> ● ConcurrentHashMap<K,V> 
        //● ConcurrentSkipListMap<K,V>
       
        //s = System.nanoTime();
        //usingBlockinQ();
        
        //allConsumed = new AtomicInteger();allProduced = new AtomicInteger();
        //pool();
        
        allConsumed = new AtomicInteger();allProduced = new AtomicInteger();
        streamapi();
        
    }

    private static void pool() {
        items = new ArrayBlockingQueue<>(100);
        //for(int i=0;i<100;i++)items.a.add(new Item());
        
        ExecutorService executorService = Executors.newFixedThreadPool(7);
        for (int i = 0; i < 7; i++) {
            executorService.submit(new ProducerOrConsumerThread());
        }
        executorService.shutdown();
        
//        
//        List<Thread> threads = new ArrayList<>();
//        for(int i=0;i<4;i++){
//            Thread t = new ProducerThread();
//            threads.add(t);
//            t.start();
//        }
//        for(int i=0;i<3;i++){
//            Thread t = new ConsumerThread();
//            threads.add(t);
//            t.start();
//        }
    }

    private static final AtomicInteger COUNTER = new AtomicInteger();
    private static    AtomicInteger allConsumed = new AtomicInteger();
    private static    AtomicInteger allProduced = new AtomicInteger();
    private final String name;
    private volatile boolean produced = false;
    private volatile boolean consumed = false;

    public Item() {
        this.name = "Item-" + COUNTER.getAndIncrement();
    }

    public synchronized void produceMe() {
        if (produced) {
            throw new RuntimeException(name + " already produced");
        }
        if (consumed) {
            throw new RuntimeException(name + " already consumed");
        }
        System.out.println("Producing: " + name);
        delay(2);
        produced = true;
        System.out.println("Produced: " + name);
    }

    public synchronized void consumeMe() {
        if (!produced) {
            throw new RuntimeException(name + " not produced yet");
        }
        if (consumed) {
            throw new RuntimeException(name + " already consumed");
        }
        System.out.println("Consuming: " + name);
        delay(3);
        consumed = true;
        System.out.println("Consumed: " + name);
    }

    private void delay(int seconds) {
        try {
            Thread.sleep(seconds * 10);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
