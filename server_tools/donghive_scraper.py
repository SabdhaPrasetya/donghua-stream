#!/usr/bin/env python3
"""
Donghive Scraper - Mengambil data donghua dari Donghive API
Source: https://www.sankavollerei.web.id/anime/donghub
"""

import json
import os
import sys
import io
import time
import urllib.request
import urllib.error
import re
from datetime import datetime

sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

API_BASE = "https://www.sankavollerei.web.id/anime/donghub"
CATALOG_FILE = "donghua_catalog.json"

EXISTING_IDS = {
    "perfect-world": "perfect_world",
    "renegade-immortal": "renegade_immortal",
    "battle-through-the-heavens": "btth",
    "battle-through-the-heavens-season-5": "btth",
    "soul-land": "soul_land",
    "soul-land-2": "soul_land_2",
    "swallowed-star": "swallowed_star",
    "tales-of-herding-gods": "tales_of_herding_gods",
    "shrouding-the-heavens": "shrouding_the_heavens",
    "jade-dynasty": "jade_dynasty",
    "lord-of-the-mysteries": "lord_of_mysteries",
    "throne-of-seal": "throne_of_seal",
    "martial-universe": "martial_universe",
    "the-great-ruler": "the_great_ruler",
    "against-the-gods": "against_the_gods",
    "tales-of-demons-and-gods": "tales_of_demons_and_gods",
    "martial-master": "martial_master",
    "apotheosis": "apotheosis",
    "a-will-eternal": "a_will_eternal",
    "wu-geng-ji": "wu_geng_ji",
    "the-demon-hunter": "the_demon_hunter",
    "supreme-god-emperor": "supreme_god_emperor",
    "tomb-of-fallen-gods": "tomb_of_fallen_gods",
    "against-the-sky-supreme": "against_the_sky_supreme",
    "fog-hill-of-five-elements": "fog_hill",
    "grandmaster-of-demonic-cultivation": "modao",
    "dragon-prince-yuan": "yuan_zun",
    "the-magic-chef-of-ice-and-fire": "magic_chef_ice_fire",
    "azure-legacy-the-demon-hunter": "the_demon_hunter",
}


def api_request(url):
    for attempt in range(3):
        try:
            req = urllib.request.Request(url, headers={
                'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
                'Accept': 'application/json',
                'Referer': 'https://www.sankavollerei.web.id/'
            })
            with urllib.request.urlopen(req, timeout=15) as response:
                return json.loads(response.read().decode('utf-8'))
        except urllib.error.HTTPError as e:
            if e.code == 429:
                time.sleep(10)
            else:
                return None
        except Exception:
            if attempt < 2:
                time.sleep(3)
    return None


def extract_episode_number(title):
    match = re.search(r'episode\s+(\d+)', title.lower())
    if match:
        return int(match.group(1))
    return 0


def clean_title(title):
    title = re.sub(r'\t+', ' ', title)
    title = re.sub(r'\s+', ' ', title)
    title = re.sub(r'\s*Episode\s+\d+\s+Subtitle\s+Indonesia.*$', '', title, flags=re.IGNORECASE)
    return title.strip()


def slug_to_id(slug):
    if slug in EXISTING_IDS:
        return EXISTING_IDS[slug]
    slug = slug.replace('-subtitle-indonesia', '')
    slug = slug.replace('-sub-indo', '')
    slug = re.sub(r'-episode-\d+.*$', '', slug)
    slug = re.sub(r'-season-\d+.*$', '', slug)
    slug = re.sub(r'-s\d+$', '', slug)
    return slug.replace('-', '_')


def main():
    print("=" * 60)
    print("DONGHIVE SCRAPER")
    print("=" * 60)

    existing_catalog = []
    if os.path.exists(CATALOG_FILE):
        with open(CATALOG_FILE, "r", encoding="utf-8") as f:
            existing_catalog = json.load(f)
        print("Existing catalog: {} titles".format(len(existing_catalog)))

    # Fetch from home endpoint (most reliable)
    print("Fetching from Donghive home...")
    url = "{}/home".format(API_BASE)
    data = api_request(url)

    new_entries = []
    if data and data.get("status") == "success":
        d = data.get("data", {})
        # Collect from latest, popular, and slider
        all_items = []
        for key in ["latest", "popular", "slider"]:
            items = d.get(key, [])
            all_items.extend(items)
            print("  {}: {} items".format(key, len(items)))

        seen = set()
        for item in all_items:
            slug = item.get("slug", "")
            if not slug or slug in seen:
                continue
            seen.add(slug)

            title = clean_title(item.get("title", ""))
            episode = extract_episode_number(item.get("title", ""))
            poster = item.get("poster", "")
            status = item.get("status", "Unknown")

            entry_id = slug_to_id(slug)

            # Check existing to preserve higher episode count
            existing = next((e for e in existing_catalog if e["id"] == entry_id), None)
            existing_ep = existing.get("currentEpisodes", 0) if existing else 0

            entry = {
                "id": entry_id,
                "title": title,
                "slug": slug,
                "currentEpisodes": max(episode, existing_ep),
                "totalEpisodes": existing.get("totalEpisodes", 0) if existing else 0,
                "status": "Ongoing",
                "poster": poster,
                "uploadDay": existing.get("uploadDay", "Unknown") if existing else "Unknown",
                "isRecentlyUpdated": episode > existing_ep if existing_ep > 0 else True,
                "latestEpisodeUpdateNote": "Episode {} Sub Indo!".format(max(episode, existing_ep))
            }

            new_entries.append(entry)
            if episode > existing_ep and existing_ep > 0:
                print("  UPDATED: {} -> Episode {} (was {})".format(title[:35], episode, existing_ep))
            elif existing_ep == 0:
                print("  NEW: {} -> Episode {}".format(title[:35], episode))

    # Merge with existing catalog (keep entries not from Donghive)
    existing_ids = {e["id"] for e in new_entries}
    for e in existing_catalog:
        if e["id"] not in existing_ids:
            new_entries.append(e)

    new_entries.sort(key=lambda x: x.get("currentEpisodes", 0), reverse=True)

    with open(CATALOG_FILE, "w", encoding="utf-8") as f:
        json.dump(new_entries, f, ensure_ascii=False, indent=2)

    print("")
    print("Saved {} titles to {}".format(len(new_entries), CATALOG_FILE))
    print("Top 15:")
    for i, e in enumerate(new_entries[:15], 1):
        print("  {}. {} -> Ep {}".format(i, e['title'][:40], e['currentEpisodes']))

    return new_entries


if __name__ == "__main__":
    main()
