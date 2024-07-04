package reservation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SynchronizedAirlineReservationSystem {
    private static final Map<Integer, Boolean> database = new HashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    // Bazı koltuklar ile veri tabanı oluştur
    static {
        database.put(1, false);
        database.put(2, false);
        database.put(3, false);
        database.put(4, false);
        database.put(5, false);
    }

    // Rezervasyon yapma metodu (Writer)
    public void makeReservation(Integer seat) {
    	 
        lock.writeLock().lock();
        try {
        	LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
            String formattedDateTime = currentDateTime.format(formatter);
            System.out.println("Time: " + formattedDateTime);
            
        	System.out.println(Thread.currentThread().getName() + " tries to book the seat " + seat);
        	System.out.println();
        	if (database.get(seat) != null && !database.get(seat)) {     
                database.put(seat, true);
                currentDateTime = LocalDateTime.now();
                formattedDateTime = currentDateTime.format(formatter);
                System.out.println("Time: " + formattedDateTime);
                System.out.println(Thread.currentThread().getName() + " booked seat number " + seat +" successfully.");
                System.out.println();
            } else {
            	currentDateTime = LocalDateTime.now();
                formattedDateTime = currentDateTime.format(formatter);
                System.out.println("Time: " + formattedDateTime);
                System.out.println(Thread.currentThread().getName() + " could not booked " + seat + " since it has been already booked.");
                System.out.println();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    // Rezervasyon iptal etme metodu (Writer)
    public void cancelReservation(Integer seat) {
        lock.writeLock().lock();
        try {
        	LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
            String formattedDateTime = currentDateTime.format(formatter);
            System.out.println("Time: " + formattedDateTime);
            
            if (database.get(seat) != null && database.get(seat)) {
                System.out.println(Thread.currentThread().getName() + " canceling reservation seat " + seat);
                System.out.println();
                database.put(seat, false);
                currentDateTime = LocalDateTime.now();
                formattedDateTime = currentDateTime.format(formatter);
                System.out.println("Time: " + formattedDateTime);
                System.out.println(Thread.currentThread().getName() + " canceled reservation seat " + seat);
                System.out.println();
            } else {
            	currentDateTime = LocalDateTime.now();
                formattedDateTime = currentDateTime.format(formatter);
                System.out.println("Time: " + formattedDateTime);
                
                System.out.println(Thread.currentThread().getName() + " failed to cancel seat " + seat + " (Not reserved)");
                System.out.println();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    // Rezervasyon sorgulama metodu (Reader)
    public void queryReservation() {
        lock.readLock().lock();
        try {
        	LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
            String formattedDateTime = currentDateTime.format(formatter);
            System.out.println("Time: " + formattedDateTime);
            
        	System.out.println(Thread.currentThread().getName() + " looks for available seats. State of the seats are: ");
            StringBuilder result = new StringBuilder();
            database.forEach((key, value) -> {
            	String s = value ? "1" : "0";
                result.append("Seat No ").append(key).append(" : ").append(s).append(" ");
            });
            
            System.out.println(result.toString().trim());
            System.out.println("----------------------------------------------------------------------------");
            System.out.println();
        } finally {
            lock.readLock().unlock();
        }
    }

    public static void main(String[] args) {
        SynchronizedAirlineReservationSystem system = new SynchronizedAirlineReservationSystem();

        // Rezervasyon yapmak için Writer threadleri
        Thread writer1 = new Thread(() -> {
            system.makeReservation(1);
        }, "Writer1");

        Thread writer2 = new Thread(() -> {
            system.makeReservation(1);
        }, "Writer2");

        // Rezervasyon sorgulamak için Reader threadleri
        Thread reader1 = new Thread(() -> {
            system.queryReservation();
        }, "Reader1");

        Thread reader2 = new Thread(() -> {
            system.queryReservation();
        }, "Reader2");
        
        Thread reader3 = new Thread(() -> {
            system.queryReservation();
        }, "Reader3");
        
        Thread writer3 = new Thread(() -> {
            system.makeReservation(1);
        }, "Writer3");
        
        Thread writer4 = new Thread(() -> {
            system.makeReservation(1);
        }, "Writer4");
        
     // Rezervasyon iptali için Writer threadi
        Thread writer1Cancel = new Thread(() -> {
            system.cancelReservation(1);
        }, "Writer1");

        
        // Threadlerin birbirinin bitmesini beklemesi için join()
        try {
        	writer1.start();
            writer1.join();
            reader1.start();
            reader1.join();
            reader3.start();
            reader3.join();
            writer2.start();
            writer2.join();
            reader2.start();
            reader2.join();
            writer3.start();
            writer3.join();
            writer1Cancel.start();
            writer1Cancel.join();
            writer4.start();
            writer4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
