package com.agrosmart;

import com.agrosmart.models.IotDevice;
import com.agrosmart.models.SensorReading;
import com.agrosmart.models.IrrigationSetting;
import com.agrosmart.models.PumpCommand;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for AgroSmart IoT Smart Farming domain models.
 */
public class IotModelTest {

    @Test
    public void testIotDeviceProperties() {
        IotDevice device = new IotDevice(
                "ESP32_AGRO_01", "North Polyhouse Node", "ESP32_WROOM",
                1L, "9876543210", "Mumbai, Maharashtra",
                "sec_api_key_123", "v1.2.0", true,
                "2026-10-14 10:00:00", "2026-10-01"
        );

        assertEquals("ESP32_AGRO_01", device.getDeviceId());
        assertEquals("North Polyhouse Node", device.getDeviceName());
        assertEquals("Mumbai, Maharashtra", device.getFieldLocation());
        assertTrue(device.isActive());
        assertEquals(1L, device.getOwnerId());
    }

    @Test
    public void testSensorReadingProperties() {
        SensorReading reading = new SensorReading(
                "read_001", "ESP32_AGRO_01", 1L,
                45.5, 2200, "OPTIMAL",
                28.4, 65.0, "VALID",
                "2026-10-14 10:15:00"
        );

        assertEquals("read_001", reading.getId());
        assertEquals("ESP32_AGRO_01", reading.getDeviceId());
        assertEquals(45.5, reading.getSoilMoisturePct(), 0.01);
        assertEquals(2200, reading.getSoilMoistureRaw());
        assertEquals("OPTIMAL", reading.getMoistureStatus());
        assertEquals(28.4, reading.getTemperatureC(), 0.01);
        assertEquals(65.0, reading.getHumidityPct(), 0.01);
        assertEquals("VALID", reading.getSensorStatus());
    }

    @Test
    public void testIrrigationSettingThresholds() {
        IrrigationSetting setting = new IrrigationSetting(
                "ESP32_AGRO_01", 1L, "AUTO",
                25.0, 75.0, 300, 600, 20.0,
                "2026-10-14 10:00:00", 1L
        );

        assertEquals("AUTO", setting.getOperatingMode());
        assertEquals(25.0, setting.getLowMoistureStartThreshold(), 0.01);
        assertEquals(75.0, setting.getHighMoistureStopThreshold(), 0.01);
        assertEquals(300, setting.getMaxContinuousPumpSec());
        assertEquals(600, setting.getCooldownIntervalSec());
    }

    @Test
    public void testPumpCommandExecutionFlow() {
        PumpCommand command = new PumpCommand(
                "cmd_101", "ESP32_AGRO_01", "START_PUMP",
                120, "FARMER_APP", true, null, "PENDING",
                null, "2026-10-14 10:20:00", null
        );

        assertEquals("cmd_101", command.getCommandId());
        assertEquals("START_PUMP", command.getCommand());
        assertTrue(command.isRequestedState());
        assertEquals("PENDING", command.getStatus());

        // Simulate execution
        command.setStatus("EXECUTED");
        command.setConfirmedState(Boolean.TRUE);
        command.setExecutedAt("2026-10-14 10:20:02");

        assertEquals("EXECUTED", command.getStatus());
        assertEquals(Boolean.TRUE, command.getConfirmedState());
        assertNotNull(command.getExecutedAt());
    }
}
