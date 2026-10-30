"""
AgroSmart — ESP32 Smart Station Telemetry Simulator
===================================================
Run this script to simulate a live ESP32 hardware station transmitting
real-time soil moisture, DHT22 temperature, humidity, and acknowledging
irrigation pump commands to the AgroSmart backend and Android app.

Usage:
    python simulate_iot_station.py
"""

import time
import random
import requests
import sys

BACKEND_URL = "http://localhost:8080/api/iot"
DEVICE_ID = "ESP32-AGRO-01"
API_KEY = "AGRO-ESP32-SECURE-KEY-2026"

HEADERS = {
    "Content-Type": "application/json",
    "X-Device-ApiKey": API_KEY
}

def simulate():
    print("=" * 65)
    print("  🌱 AgroSmart ESP32 Smart Station Live Telemetry Simulator")
    print(f"  Target Device: {DEVICE_ID}")
    print(f"  Backend URL:   {BACKEND_URL}")
    print("=" * 65)

    # Initial simulated values
    moisture_pct = 32.0  # Starts dry
    temperature = 28.5
    humidity = 58.0
    pump_state = False

    while True:
        try:
            # 1. Heartbeat & check pending commands from Android App
            pending_res = requests.get(f"{BACKEND_URL}/devices/{DEVICE_ID}/commands/pending", timeout=3)
            if pending_res.status_code == 200:
                commands = pending_res.json()
                if isinstance(commands, list) and len(commands) > 0:
                    for cmd in commands:
                        cmd_action = cmd.get("action")
                        cmd_id = cmd.get("commandId")
                        print(f"\n⚡ [PUMP COMMAND RECEIVED]: {cmd_action} (ID: {cmd_id})")
                        if "ON" in cmd_action.upper():
                            pump_state = True
                            print("   >>> Physical Relay Engaged: PUMP IS NOW RUNNING 💧")
                        else:
                            pump_state = False
                            print("   >>> Physical Relay Disengaged: PUMP IS NOW STOPPED 🛑")

                        # Acknowledge command execution back to backend
                        ack_payload = {
                            "relayState": pump_state,
                            "executionStatus": "EXECUTED",
                            "message": f"Pump {'started' if pump_state else 'stopped'} successfully by hardware relay."
                        }
                        requests.post(f"{BACKEND_URL}/commands/{cmd_id}/acknowledge", json=ack_payload, timeout=3)
                        print(f"   >>> Acknowledgment sent to AgroSmart server.\n")

            # 2. Simulate physics
            if pump_state:
                # Pump is ON -> soil moisture rises
                moisture_pct = min(85.0, moisture_pct + random.uniform(1.8, 3.5))
            else:
                # Pump is OFF -> soil moisture slowly drops
                moisture_pct = max(18.0, moisture_pct - random.uniform(0.3, 0.8))

            temperature += random.uniform(-0.2, 0.2)
            humidity += random.uniform(-0.5, 0.5)

            # ADC mapping (Dry: 3200 ADC, Wet: 1400 ADC)
            raw_adc = int(3200 - (moisture_pct / 100.0) * (3200 - 1400))

            payload = {
                "deviceId": DEVICE_ID,
                "soilMoisturePct": round(moisture_pct, 1),
                "soilMoistureRaw": raw_adc,
                "temperatureC": round(temperature, 1),
                "humidityPct": round(humidity, 1),
                "sensorStatus": "OK"
            }

            # 3. Post telemetry reading
            res = requests.post(f"{BACKEND_URL}/readings", json=payload, headers=HEADERS, timeout=3)
            if res.status_code in [200, 201]:
                status_icon = "🟢 WET" if moisture_pct > 70 else ("🟡 MODERATE" if moisture_pct >= 30 else "🔴 DRY")
                pump_str = "💧 RUNNING" if pump_state else "🛑 OFF"
                print(f"📡 Telemetry sent -> Moisture: {moisture_pct:.1f}% ({status_icon}) | Temp: {temperature:.1f}°C | Hum: {humidity:.1f}% | Pump: {pump_str}")
            else:
                print(f"⚠️ Server returned code {res.status_code}: {res.text}")

        except requests.exceptions.ConnectionError:
            print("❌ Cannot connect to backend at http://localhost:8080. Make sure Spring Boot backend is running!")
        except Exception as e:
            print(f"⚠️ Error: {e}")

        # Transmit every 4 seconds
        time.sleep(4)

if __name__ == "__main__":
    simulate()
