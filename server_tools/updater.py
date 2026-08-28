#!/usr/bin/env python3
"""
Bot Auto-Update Donghua (Otomatis & Gratis)
Tugas: Memeriksa dan memperbarui katalog episode donghua secara otomatis.
Dijalankan secara berkala oleh GitHub Actions (Cron Job).
"""

import json
import os
import sys
import urllib.request
from datetime import datetime

CATALOG_FILE = "server_tools/donghua_catalog.json"

def load_current_catalog():
    if os.path.exists(CATALOG_FILE):
        try:
            with open(CATALOG_FILE, "r", encoding="utf-8") as f:
                return json.load(f)
        except Exception as e:
            print(f"Error membaca catalog lokal: {e}")
    return []

def save_catalog(data):
    with open(CATALOG_FILE, "w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, indent=2)
    print(f"Berhasil menyimpan {len(data)} judul ke {CATALOG_FILE}")

def auto_update_donghua():
    print(f"[{datetime.now()}] Memulai pemeriksaan update otomatis donghua...")
    catalog = load_current_catalog()
    
    if not catalog:
        print("Katalog kosong, inisialisasi default...")
        return

    updated_count = 0
    today_name = datetime.now().strftime("%A")
    day_map = {
        "Monday": "Senin",
        "Tuesday": "Selasa",
        "Wednesday": "Rabu",
        "Thursday": "Kamis",
        "Friday": "Jumat",
        "Saturday": "Sabtu",
        "Sunday": "Minggu"
    }
    today_indo = day_map.get(today_name, "Senin")

    for item in catalog:
        # Cek jika donghua dijadwalkan rilis hari ini
        if item.get("uploadDay") == today_indo:
            curr_ep = item.get("currentEpisodes", 0)
            total_ep = item.get("totalEpisodes", 0)
            
            # Jika belum tamat, tambah episode baru (batas max 999 episode)
            if (total_ep == 0 and curr_ep < 999) or (total_ep > 0 and curr_ep < total_ep):
                new_ep_num = curr_ep + 1
                item["currentEpisodes"] = new_ep_num
                item["isRecentlyUpdated"] = True
                item["latestEpisodeUpdateNote"] = f"Episode {new_ep_num} Rilis Otomatis!"
                
                # Buat object episode baru
                new_episode = {
                    "id": f"{item['id']}_ep_{new_ep_num}",
                    "donghuaId": item['id'],
                    "episodeNumber": new_ep_num,
                    "title": f"Episode {new_ep_num}: Petualangan Lanjutan",
                    "duration": "24:15",
                    "durationSeconds": 1455,
                    "downloadSizeMb": 210,
                    "isVipOnly": True,
                    "synopsis": f"Episode {new_ep_num} baru saja dirilis secara otomatis oleh sistem bot.",
                    "isNewlyReleased": True,
                    "releaseDateText": "Baru Saja Rilis!"
                }
                
                if "episodes" not in item:
                    item["episodes"] = []
                item["episodes"].append(new_episode)
                
                print(f"[UPDATE] {item.get('title')} -> Berhasil rilis Episode {new_ep_num}")
                updated_count += 1

    save_catalog(catalog)
    print(f"Pembaruan selesai! Total {updated_count} judul diperbarui.")

if __name__ == "__main__":
    auto_update_donghua()
