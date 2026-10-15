package com.agrosmart.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.agrosmart.models.Tractor;
import com.agrosmart.models.TractorBooking;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TractorRepository {

    private static TractorRepository instance;
    private final List<Tractor> tractorFleet = new ArrayList<>();
    private final List<TractorBooking> bookings = new ArrayList<>();
    private final MutableLiveData<List<TractorBooking>> bookingsLiveData = new MutableLiveData<>();

    public static synchronized TractorRepository getInstance() {
        if (instance == null) {
            instance = new TractorRepository();
        }
        return instance;
    }

    private TractorRepository() {
        initTractors();
        initSampleBookings();
    }

    private void initTractors() {
        tractorFleet.clear();
        tractorFleet.add(new Tractor(
                "TR01", "Mahindra 575 DI XP Plus", "Mahindra", "47 HP",
                800.0, 5800.0, "Rotavator, 9-Tyne Cultivator, Trolley",
                "Ramesh Patil", "9822334455", "Baramati, Pune",
                true, 4.9f
        ));
        tractorFleet.add(new Tractor(
                "TR02", "John Deere 5050 D GearPro", "John Deere", "50 HP",
                900.0, 6500.0, "Heavy-Duty Rotavator, MB Plough",
                "Suresh Jagtap", "9822445566", "Shirur, Pune",
                true, 4.8f
        ));
        tractorFleet.add(new Tractor(
                "TR03", "Swaraj 855 FE 4WD", "Swaraj", "52 HP",
                950.0, 6800.0, "Laser Land Leveler, Disk Harrow",
                "Vikas Deshmukh", "9822556677", "Daund, Pune",
                true, 4.9f
        ));
        tractorFleet.add(new Tractor(
                "TR04", "Sonalika DI 745 III Sikander", "Sonalika", "50 HP",
                850.0, 6000.0, "Cultivator, Seed Drill, Trolley",
                "Ganesh Shinde", "9822667788", "Niphad, Nashik",
                true, 4.7f
        ));
        tractorFleet.add(new Tractor(
                "TR05", "Eicher 380 Super DI", "Eicher", "40 HP",
                700.0, 5000.0, "Rigid Cultivator, Trolley",
                "Baburao Kadam", "9822778899", "Dindori, Nashik",
                true, 4.6f
        ));
    }

    private void initSampleBookings() {
        bookings.clear();
        bookings.add(new TractorBooking(
                "BK-1001", "TR01", "Mahindra 575 DI XP Plus",
                "Arya Farmer", "9876543210", "15 Sep 2026",
                4, 3200.0, "Plot 14, Baramati Road",
                "Land tilling for Rabi wheat sowing", "CONFIRMED", "10 Sep 2026"
        ));
        bookingsLiveData.setValue(new ArrayList<>(bookings));
    }

    public void addTractor(Tractor tractor) {
        tractorFleet.add(0, tractor);
    }

    public LiveData<List<Tractor>> getTractors(String brand) {
        MutableLiveData<List<Tractor>> liveData = new MutableLiveData<>();
        List<Tractor> filtered = new ArrayList<>();
        for (Tractor t : tractorFleet) {
            if (brand == null || brand.equals("All Brands") || t.getBrand().equalsIgnoreCase(brand)) {
                filtered.add(t);
            }
        }
        liveData.setValue(filtered);
        return liveData;
    }

    public LiveData<List<TractorBooking>> getBookings() {
        return bookingsLiveData;
    }

    public void addBooking(TractorBooking booking) {
        bookings.add(0, booking);
        bookingsLiveData.setValue(new ArrayList<>(bookings));
    }

    public void cancelBooking(String bookingId) {
        for (TractorBooking b : bookings) {
            if (b.getId().equals(bookingId)) {
                b.setStatus("CANCELLED");
                break;
            }
        }
        bookingsLiveData.setValue(new ArrayList<>(bookings));
    }
}
