package reservation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AsynchronizedAirlineReservationSystem {
    private static final Map<Integer, Boolean> database = Collections.synchronizedMap(new HashMap<>());

    // Bazı koltuklar ile veri tabanı oluştur
    static {
        database.put(1, false);
        database.put(2, false);
        database.put(3, false);
        database.put(4, false);
        database.put(5, false);
    }

    // Rezervasyon yapma metodu (Writer)
    public void makeReservation(int seat) {
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
            System.out.println(Thread.currentThread().getName() + " booked seat number " + seat +"successfully.");
            System.out.println();
        } else {
        	currentDateTime = LocalDateTime.now();
            formattedDateTime = currentDateTime.format(formatter);
            System.out.println("Time: " + formattedDateTime);
            System.out.println(Thread.currentThread().getName() + " could not booked " + seat + " since it has been already booked.");
            System.out.println();
        }
    }

    // Rezervasyon iptal etme metodu (Writer)
    public void cancelReservation(int seat) {
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
    }

    // Rezervasyon sorgulama metodu (Reader)
    public void queryReservation() {
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
    }

    public static void main(String[] args) {
        AsynchronizedAirlineReservationSystem system = new AsynchronizedAirlineReservationSystem();

        // Rezervasyon yapmak için Writer threadleri
        Thread writer1 = new Thread(() -> {
            system.makeReservation(1);
        }, "Writer1");

        Thread writer2 = new Thread(() -> {
            system.makeReservation(1);
        }, "Writer2");

        // Rezervaston sorgulamak için Reader threadleri
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
        
        writer1.start();
        reader1.start();
        reader3.start();
        writer2.start();
        reader2.start();
        writer3.start();
        writer1Cancel.start();
        writer4.start();
        
        // Threadlerin birbirinin bitmesini beklemesi için join()
        try {
            writer1.join();
            reader1.join();
            reader3.join();
            writer2.join();
            reader2.join();
            writer3.join();
            writer1Cancel.join();
            writer4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
