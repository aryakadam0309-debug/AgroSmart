# ==============================================================================
# AGROSMART — ESP32 MICROPYTHON SENSOR & TELEMETRY SCRIPT (FOR THONNY IDE)
# ==============================================================================
# Instructions:
# 1. Connect ESP32 to Laptop via USB and open Thonny IDE.
# 2. Select MicroPython (ESP32) interpreter in Thonny bottom-right.
# 3. Paste this code into Thonny and click Run (F5).
# ==============================================================================

import machine
import time
import urequests
import network
import ujson

# 1. Wi-Fi Configuration (Connects to your phone hotspot or home Wi-Fi)
WIFI_SSID = "Arya"            # Replace with your phone's hotspot name if different
WIFI_PASS = "12345678"        # Replace with your phone's hotspot password

# 2. Backend Server URL (Your laptop IP on the phone's hotspot)
BACKEND_URL = "http://10.10.115.182:8080/api/iot"
DEVICE_ID = "ESP32-AGRO-01"

# 3. Pin Definitions
# Capacitive Soil Moisture Sensor on GPIO 34 (ADC1, works with Wi-Fi)
soil_pin = machine.Pin(34)
adc = machine.ADC(soil_pin)
# ATTN_11DB is CRITICAL: Allows reading the full 0 - 3.3V range from the sensor
adc.atten(machine.ADC.ATTN_11DB)
adc.width(machine.ADC.WIDTH_12BIT)  # 0 to 4095

# Relay Pin on GPIO 18 (Active LOW or HIGH depending on your relay)
relay = machine.Pin(18, machine.Pin.OUT)
relay.value(1) # Start with Relay OFF

# 4. Sensor Calibration Values
# In dry air: raw ADC is usually ~3200 - 3700
# In water/wet soil: raw ADC is usually ~1300 - 1600
DRY_ADC = 3500
WET_ADC = 1350

def connect_wifi():
    wlan = network.WLAN(network.STA_IF)
    wlan.active(True)
    if not wlan.isconnected():
        print(f"[WIFI] Connecting to {WIFI_SSID}...")
        wlan.connect(WIFI_SSID, WIFI_PASS)
        timeout = 20
        while not wlan.isconnected() and timeout > 0:
            time.sleep(0.5)
            timeout -= 1
    if wlan.isconnected():
        print(f"[WIFI] Connected! ESP32 IP: {wlan.ifconfig()[0]}")
        return True
    else:
        print("[WIFI] Connection failed. Operating in offline sensor test mode.")
        return False

def read_moisture():
    # 15-sample average for high stability
    total = 0
    for _ in range(15):
        total += adc.read()
        time.sleep_ms(10)
    raw = total // 15
    
    # Calculate voltage (mV)
    voltage_mv = (raw / 4095.0) * 3300.0

    # Inverted capacitive calculation:
    # When soil is WET, raw ADC is LOW. When soil is DRY, raw ADC is HIGH.
    pct = ((DRY_ADC - raw) / (DRY_ADC - WET_ADC)) * 100.0
    if pct < 0.0:
        pct = 0.0
    elif pct > 100.0:
        pct = 100.0

    if pct < 35.0:
        status = "DRY"
    elif pct <= 70.0:
        status = "MODERATE"
    else:
        status = "WET"

    return raw, voltage_mv, pct, status

def send_telemetry(raw, pct, status):
    payload = {
        "deviceId": DEVICE_ID,
        "farmerId": 1,
        "soilMoisturePct": round(pct, 1),
        "soilMoistureRaw": raw,
        "moistureStatus": status,
        "temperatureC": 28.5,
        "humidityPct": 62.0,
        "sensorStatus": "VALID"
    }
    
    headers = {"Content-Type": "application/json"}
    try:
        url = f"{BACKEND_URL}/reading"
        response = urequests.post(url, data=ujson.dumps(payload), headers=headers)
        if response.status_code == 200:
            print(f"[SERVER ACK] Telemetry saved to MySQL database (HTTP 200)")
        else:
            print(f"[SERVER] Response: {response.status_code}")
        response.close()
    except Exception as e:
        print(f"[SERVER ERR] Could not send to {BACKEND_URL}: {e}")

# ==============================================================================
# MAIN EXECUTION LOOP
# ==============================================================================
print("==================================================")
print("  AGROSMART ESP32 MICROPYTHON MONITOR RUNNING     ")
print("==================================================")

connect_wifi()

while True:
    try:
        raw, mv, pct, status = read_moisture()
        print(f"📊 [Moisture] Raw ADC: {raw:4d} | Voltage: {mv:6.1f} mV | Moisture: {pct:5.1f}% | Status: {status}")
        
        # Send live telemetry to laptop Spring Boot backend
        send_telemetry(raw, pct, status)
        
    except Exception as e:
        print(f"Error in main loop: {e}")
        
    time.sleep(5)
