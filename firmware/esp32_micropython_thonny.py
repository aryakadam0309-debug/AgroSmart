# ==============================================================================
# AGROSMART — ESP32 MICROPYTHON SENSOR STATION (NO RELAY / SENSORS ONLY)
# ==============================================================================
# Target Hardware: ESP32 DevKit V1
# Sensors:
#   - Capacitive Soil Moisture Sensor v1.2 (Analog on GPIO 34)
#   - Optional DHT22 Temp & Humidity Sensor (Digital on GPIO 4)
#
# Steps in Thonny IDE:
# 1. Connect ESP32 to laptop via USB.
# 2. In Thonny: Run -> Select Interpreter -> MicroPython (ESP32).
# 3. Enter your Wi-Fi SSID, Password, and your Laptop's LAN IP below.
# 4. Click Run (F5).
# ==============================================================================

import machine
import time
import network
import urequests
import ujson

# ------------------------------------------------------------------------------
# 1. NETWORK & BACKEND CONFIGURATION
# ------------------------------------------------------------------------------
WIFI_SSID = "Arya"            # <-- Replace with your phone hotspot or Wi-Fi name
WIFI_PASS = "12345678"        # <-- Replace with your Wi-Fi password

LAPTOP_IP = "10.10.211.230"   # <-- Replace with your Laptop's IP (from 'ipconfig')
BACKEND_URL = f"http://{LAPTOP_IP}:8080/api/iot"

DEVICE_ID = "ESP32-AGRO-01"
API_KEY = "AGRO-ESP32-SECURE-KEY-2026"

# ------------------------------------------------------------------------------
# 2. SENSOR SETUP
# ------------------------------------------------------------------------------
# Capacitive Soil Moisture Sensor on GPIO 34 (ADC1 channel, works with Wi-Fi)
soil_pin = machine.Pin(34)
adc = machine.ADC(soil_pin)
adc.atten(machine.ADC.ATTN_11DB)      # Full 0 - 3.3V range
adc.width(machine.ADC.WIDTH_12BIT)    # 12-bit ADC (0 - 4095)

# Optional DHT22 (if attached on GPIO 4)
try:
    import dht
    dht_sensor = dht.DHT22(machine.Pin(4))
except Exception:
    dht_sensor = None

# Calibration limits:
# Raw ADC in dry air: ~3200 - 3500
# Raw ADC in water / wet soil: ~1300 - 1500
DRY_ADC = 3500
WET_ADC = 1400

# ------------------------------------------------------------------------------
# 3. HELPER FUNCTIONS
# ------------------------------------------------------------------------------
def connect_wifi():
    wlan = network.WLAN(network.STA_IF)
    wlan.active(True)
    if not wlan.isconnected():
        print(f"\n[WIFI] Connecting to '{WIFI_SSID}'...")
        wlan.connect(WIFI_SSID, WIFI_PASS)
        timeout = 25
        while not wlan.isconnected() and timeout > 0:
            time.sleep(0.5)
            timeout -= 1
            print(".", end="")
        print()

    if wlan.isconnected():
        ip = wlan.ifconfig()[0]
        print(f"[WIFI] Connected! ESP32 IP: {ip}")
        return True
    else:
        print("[WIFI] Warning: Could not connect to Wi-Fi. Check SSID/Password.")
        return False

def read_moisture():
    # 15-sample average for rock-solid stability
    total = 0
    for _ in range(15):
        total += adc.read()
        time.sleep_ms(10)
    raw = total // 15

    # Voltage calculation (mV)
    voltage_mv = (raw / 4095.0) * 3300.0

    # Inverted capacitive formula: higher ADC = dryer soil
    moisture_pct = ((DRY_ADC - raw) / (DRY_ADC - WET_ADC)) * 100.0
    moisture_pct = max(0.0, min(100.0, moisture_pct))

    if moisture_pct < 30.0:
        status = "DRY"
    elif moisture_pct <= 70.0:
        status = "MODERATE"
    else:
        status = "WET"

    # Read DHT22 if connected, else use ambient defaults
    temp_c = 28.5
    humidity_pct = 60.0
    if dht_sensor:
        try:
            dht_sensor.measure()
            temp_c = round(dht_sensor.temperature(), 1)
            humidity_pct = round(dht_sensor.humidity(), 1)
        except Exception:
            pass

    return raw, voltage_mv, moisture_pct, status, temp_c, humidity_pct

def send_telemetry(raw, pct, status, temp_c, humidity_pct):
    payload = {
        "deviceId": DEVICE_ID,
        "farmerId": 1,
        "soilMoisturePct": round(pct, 1),
        "soilMoistureRaw": raw,
        "moistureStatus": status,
        "temperatureC": temp_c,
        "humidityPct": humidity_pct,
        "sensorStatus": "VALID"
    }

    headers = {
        "Content-Type": "application/json",
        "X-Device-ApiKey": API_KEY
    }

    try:
        url = f"{BACKEND_URL}/readings"
        res = urequests.post(url, data=ujson.dumps(payload), headers=headers)
        if res.status_code in [200, 201]:
            print(f"📡 [LIVE TELEMETRY] Moisture: {pct:.1f}% ({status}) | Raw ADC: {raw} | Temp: {temp_c}°C | Hum: {humidity_pct}%")
        else:
            print(f"[SERVER] Response: {res.status_code}")
        res.close()
    except Exception as e:
        print(f"[SERVER ERR] Could not send to {BACKEND_URL}: {e}")

# ------------------------------------------------------------------------------
# 4. MAIN LOOP
# ------------------------------------------------------------------------------
print("==================================================")
print("  🌱 AGROSMART SENSOR TELEMETRY STATION RUNNING   ")
print("==================================================")

connect_wifi()

while True:
    try:
        raw, mv, pct, status, temp_c, hum = read_moisture()
        send_telemetry(raw, pct, status, temp_c, hum)
    except Exception as e:
        print(f"[LOOP ERROR] {e}")

    # Transmit reading every 4 seconds
    time.sleep(4)
