package com.agrosmart;

import com.agrosmart.models.CropHarvest;
import com.agrosmart.models.FertilizerItem;
import com.agrosmart.models.NotificationItem;
import com.agrosmart.models.Tractor;
import com.agrosmart.models.TractorBooking;
import com.agrosmart.repository.TractorRepository;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests verifying business logic for Tractor Bookings,
 * Inventory Models (Fertilizer and Harvested Crops), and Notification generation.
 */
public class TractorAndInventoryValidationTest {

    private TractorRepository tractorRepository;

    @Before
    public void setUp() {
        tractorRepository = TractorRepository.getInstance();
    }

    @Test
    public void testTractorModelCreationAndPricing() {
        Tractor tractor = new Tractor(
                "TR-TEST", "Mahindra 575 DI", "Mahindra", "47 HP",
                800.0, 5800.0, "Rotavator",
                "Ramesh Patil", "9822334455", "Nashik, Maharashtra",
                true, 4.9f
        );

        assertEquals("TR-TEST", tractor.getId());
        assertEquals("Mahindra 575 DI", tractor.getModelName());
        assertEquals(800.0, tractor.getHourlyRate(), 0.001);
        assertEquals(5800.0, tractor.getDailyRate(), 0.001);
        assertTrue(tractor.isAvailable());
    }

    @Test
    public void testTractorBookingConflictValidation() {
        // Sample initial booking BK-1001 for TR01 on "15 Sep 2026"
        tractorRepository.addBooking(new TractorBooking(
                "BK-1001", "TR01", "Mahindra 575 DI XP Plus",
                "Ramesh Patil", "9822334455", "15 Sep 2026",
                4, 3200.0, "Nashik", "Ploughing", "CONFIRMED", "14 Sep 2026"
        ));
        assertTrue("Conflict should be detected for same tractor on same date",
                tractorRepository.hasBookingConflict("TR01", "15 Sep 2026"));

        assertFalse("No conflict on a different date for TR01",
                tractorRepository.hasBookingConflict("TR01", "20 Oct 2026"));

        assertFalse("No conflict for different tractor on 15 Sep 2026",
                tractorRepository.hasBookingConflict("TR02", "15 Sep 2026"));
    }

    @Test
    public void testTractorBookingDurationValidation() {
        TractorBooking invalidZeroDuration = new TractorBooking(
                "BK-VAL-01", "TR02", "John Deere",
                "Farmer Ram", "9876543210", "22 Oct 2026",
                0, 0.0, "Nashik", "Ploughing", "CONFIRMED", "21 Oct 2026"
        );
        assertFalse("Booking with 0 duration hours must be rejected",
                tractorRepository.addBooking(invalidZeroDuration));

        TractorBooking validBooking = new TractorBooking(
                "BK-VAL-02", "TR02", "John Deere",
                "Farmer Ram", "9876543210", "22 Oct 2026",
                3, 2700.0, "Nashik", "Ploughing", "CONFIRMED", "21 Oct 2026"
        );
        assertTrue("Valid booking should be accepted",
                tractorRepository.addBooking(validBooking));

        // Now adding another booking for same tractor on same date should fail due to conflict
        TractorBooking duplicateDateBooking = new TractorBooking(
                "BK-VAL-03", "TR02", "John Deere",
                "Farmer Shyam", "9876543211", "22 Oct 2026",
                2, 1800.0, "Nashik", "Ploughing", "CONFIRMED", "21 Oct 2026"
        );
        assertFalse("Duplicate booking for same tractor on same date must be rejected",
                tractorRepository.addBooking(duplicateDateBooking));
    }

    @Test
    public void testFertilizerItemModelAndCalculations() {
        FertilizerItem item = new FertilizerItem(
                "FERT-01", "IFFCO Urea 46% N", "Chemical",
                25, 45.0, 266.50, "2026-04-10", 10
        );

        assertEquals("FERT-01", item.getId());
        assertEquals("IFFCO Urea 46% N", item.getName());
        assertEquals("Chemical", item.getCategory());
        assertEquals(25, item.getQuantityBags());
        assertEquals(45.0, item.getBagWeightKg(), 0.001);
        assertEquals(266.50, item.getCostPerBag(), 0.001);
        assertEquals(10, item.getLowStockThreshold());
        assertFalse("25 bags is greater than threshold 10, so not low stock", item.isLowStock());

        // Update quantity to low stock level
        item.setQuantityBags(5);
        assertEquals(5, item.getQuantityBags());
        assertTrue("5 bags is <= threshold 10, so isLowStock must be true", item.isLowStock());
    }

    @Test
    public void testCropHarvestModelAndStockCalculations() {
        CropHarvest harvest = new CropHarvest(
                "CR-01", "Sharbati Wheat", "Lokwan",
                120.0, 40.0, "2026-03-20", "Silo 2, Plot A", 2600.0
        );

        assertEquals("CR-01", harvest.getId());
        assertEquals("Sharbati Wheat", harvest.getCropName());
        assertEquals("Lokwan", harvest.getVariety());
        assertEquals(120.0, harvest.getHarvestedQuintals(), 0.001);
        assertEquals(40.0, harvest.getSoldQuintals(), 0.001);
        assertEquals(80.0, harvest.getStockRemainingQuintals(), 0.001);
        assertEquals(2600.0, harvest.getExpectedPricePerQtl(), 0.001);

        // Record additional sale
        harvest.setSoldQuintals(100.0);
        assertEquals(100.0, harvest.getSoldQuintals(), 0.001);
        assertEquals(20.0, harvest.getStockRemainingQuintals(), 0.001);
    }

    @Test
    public void testNotificationItemCreation() {
        NotificationItem notif = new NotificationItem(
                "NOTIF-99", "🌧️ Weather Alert",
                "Heavy thunderstorm warning for Nashik district in next 24 hours.",
                "10 mins ago", "WEATHER", false
        );

        assertEquals("NOTIF-99", notif.getId());
        assertEquals("🌧️ Weather Alert", notif.getTitle());
        assertEquals("WEATHER", notif.getType());
        assertFalse(notif.isRead());

        notif.setRead(true);
        assertTrue(notif.isRead());
    }
}
