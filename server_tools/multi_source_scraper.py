#!/usr/bin/env python3
"""
Multi-Source Donghua Scraper
Sources: Donghive API + Fandom Wiki + Web Data
"""

import json
import os
import sys
import io
import time
import urllib.request
import urllib.error
import re
import ssl

sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

# SSL context for sites with cert issues
ctx = ssl.create_default_context()
ctx.check_hostname = False
ctx.verify_mode = ssl.CERT_NONE

CATALOG_FILE = "donghua_catalog.json"
API_BASE = "https://www.sankavollerei.web.id/anime/donghub"

# Known donghua data from multiple sources (manually curated + scraped)
# This is our comprehensive database
KNOWN_DONGHUA = [
    # === TIER 1: 100+ Episodes, Very Popular ===
    {"id": "supreme_god_emperor", "title": "Kaisar Dewa Tertinggi (Supreme God Emperor)", "cn": "无上神帝", "episodes": 635, "status": "Ongoing", "genres": ["Kultivasi", "Reinkarnasi", "Aksi"], "rating": 8.5, "studio": "Ruo Hong Culture", "day": "Senin"},
    {"id": "against_the_sky_supreme", "title": "Melawan Langit Tertinggi (Against the Sky Supreme)", "cn": "逆天至尊", "episodes": 544, "status": "Ongoing", "genres": ["Kultivasi", "Aksi", "Fantasi"], "rating": 8.4, "studio": "Ruo Hong Culture", "day": "Selasa"},
    {"id": "spirit_sword_sovereign", "title": "Penguasa Pedang Jiwa (Spirit Sword Sovereign)", "cn": "灵剑尊", "episodes": 460, "status": "Ongoing", "genres": ["Reinkarnasi", "Kultivasi", "Wuxia"], "rating": 8.4, "studio": "Ruo Hong Culture", "day": "Jumat"},
    {"id": "martial_master", "title": "Guru Besar Bela Diri (Martial Master)", "cn": "武神主宰", "episodes": 455, "status": "Ongoing", "genres": ["Reinkarnasi", "Kultivasi", "Aksi"], "rating": 8.6, "studio": "Ruo Hong Culture", "day": "Minggu"},
    {"id": "peerless_god_of_war", "title": "Dewa Perang Tanpa Tanding (Peerless God of War)", "cn": "绝世武神", "episodes": 410, "status": "Ongoing", "genres": ["Kultivasi", "Aksi", "Reinkarnasi"], "rating": 8.4, "studio": "Soyep Culture", "day": "Rabu"},
    {"id": "du_bu_xiao_yao", "title": "Satu Langkah Menuju Kebebasan (One Step Toward Freedom)", "cn": "独步逍遥", "episodes": 380, "status": "Ongoing", "genres": ["Kultivasi", "Reinkarnasi", "Aksi"], "rating": 8.5, "studio": "Soyep Culture", "day": "Senin"},
    {"id": "wan_jie_xian_zong", "title": "Dunia Seribu Keajaiban (The Wonderland of Ten Thousands)", "cn": "万界仙踪", "episodes": 360, "status": "Ongoing", "genres": ["Kultivasi", "Wuxia", "Misteri"], "rating": 8.5, "studio": "Ruo Hong Culture", "day": "Selasa"},
    {"id": "tales_of_demons_and_gods", "title": "Kisah Iblis dan Dewa (Tales of Demons and Gods)", "cn": "妖神记", "episodes": 350, "status": "Ongoing", "genres": ["Reinkarnasi", "Kultivasi", "Aksi"], "rating": 8.8, "studio": "Ruo Hong Culture", "day": "Minggu"},
    {"id": "wan_jie_shen_zhu", "title": "Penguasa Semesta Alam (Lord of the Universe)", "cn": "万界神主", "episodes": 340, "status": "Ongoing", "genres": ["Fantasi", "Kultivasi", "Aksi"], "rating": 8.5, "studio": "Ruo Hong Culture", "day": "Sabtu"},
    {"id": "ni_tian_zhun_zun", "title": "Pemberontakan Melawan Dewa (Rebel of the Gods)", "cn": "逆天至尊", "episodes": 330, "status": "Ongoing", "genres": ["Kultivasi", "Reinkarnasi", "Balas Dendam"], "rating": 8.4, "studio": "Ruo Hong Culture", "day": "Rabu"},
    {"id": "soul_land", "title": "Benua Douluo (Soul Land)", "cn": "斗罗大陆", "episodes": 260, "status": "Tamat", "genres": ["Kultivasi", "Aksi", "Fantasi"], "rating": 9.8, "studio": "Sparkly Key", "day": "Senin"},
    {"id": "peerless_martial_spirit", "title": "Jiwa Bertarung Tanpa Tanding (Peerless Martial Spirit)", "cn": "绝世战魂", "episodes": 280, "status": "Ongoing", "genres": ["Kultivasi", "Aksi", "Fantasi"], "rating": 8.4, "studio": "Ruo Hong Culture", "day": "Rabu"},
    {"id": "perfect_world", "title": "Dunia Sempurna (Perfect World)", "cn": "完美世界", "episodes": 284, "status": "Ongoing", "genres": ["Kultivasi", "Fantasi", "Aksi"], "rating": 9.9, "studio": "Shanghai Foch Film", "day": "Jumat"},
    {"id": "alchemy_supreme", "title": "Kaisar Alkimia Tertinggi (Alchemy Supreme)", "cn": "丹道至尊", "episodes": 270, "status": "Ongoing", "genres": ["Kultivasi", "Reinkarnasi", "Fantasi"], "rating": 8.3, "studio": "Ruo Hong Culture", "day": "Kamis"},
    {"id": "wu_geng_ji", "title": "Kronik Perintah Dewa (Wu Geng Ji)", "cn": "武庚纪", "episodes": 260, "status": "Ongoing", "genres": ["Aksi", "Fantasi", "Mitologi"], "rating": 9.3, "studio": "Sparkly Key", "day": "Jumat"},
    {"id": "legend_of_xianwu", "title": "Legenda Kaisar Xianwu (Legend of Xianwu)", "cn": "仙武帝尊", "episodes": 250, "status": "Ongoing", "genres": ["Kultivasi", "Reinkarnasi", "Aksi"], "rating": 8.7, "studio": "Ruo Hong Culture", "day": "Senin"},
    {"id": "everlasting_god_of_sword", "title": "Dewa Pedang Abadi (Everlasting God of Sword)", "cn": "万界独尊", "episodes": 240, "status": "Ongoing", "genres": ["Kultivasi", "Aksi", "Wuxia"], "rating": 8.4, "studio": "Ruo Hong Culture", "day": "Sabtu"},
    {"id": "ancient_myth", "title": "Misteri Kuno Para Dewa (Ancient Myth)", "cn": "上古秘约", "episodes": 230, "status": "Ongoing", "genres": ["Mitologi", "Kultivasi", "Fantasi"], "rating": 8.3, "studio": "Ruo Hong Culture", "day": "Jumat"},
    {"id": "ten_thousand_worlds", "title": "Dewa Sihir Sepuluh Ribu Alam (Ten Thousand Worlds)", "cn": "万界法神", "episodes": 220, "status": "Ongoing", "genres": ["Fantasi", "Sihir", "Reinkarnasi"], "rating": 8.5, "studio": "Ruo Hong Culture", "day": "Senin"},
    {"id": "btth", "title": "Pertarungan Memecah Langit (Battle Through the Heavens)", "cn": "斗破苍穹", "episodes": 208, "status": "Ongoing", "genres": ["Kultivasi", "Wuxia", "Aksi"], "rating": 9.8, "studio": "Shanghai Foch Film", "day": "Minggu"},
    {"id": "urban_miracle_doctor", "title": "Dokter Urban Ajaib (Urban Miracle Doctor)", "cn": "都市全能医生", "episodes": 207, "status": "Ongoing", "genres": ["Urban", "Drama", "Kultivasi"], "rating": 8.5, "studio": "Various", "day": "Unknown"},
    {"id": "swallowed_star", "title": "Bintang Pemangsa (Swallowed Star)", "cn": "吞噬星空", "episodes": 190, "status": "Ongoing", "genres": ["Sci-Fi", "Aksi", "Kultivasi"], "rating": 9.8, "studio": "Sparkly Key", "day": "Rabu"},
    {"id": "a_will_eternal", "title": "Kehendak Abadi (A Will Eternal)", "cn": "一念永恒", "episodes": 180, "status": "Ongoing", "genres": ["Komedi", "Kultivasi", "Aksi"], "rating": 9.6, "studio": "B.CMAY PICTURES", "day": "Rabu"},
    {"id": "fanren", "title": "Catatan Perjalanan Kultivator Fana (A Record of a Mortal's Journey)", "cn": "凡人修仙传", "episodes": 178, "status": "Ongoing", "genres": ["Kultivasi", "Wuxia", "Petualangan"], "rating": 9.8, "studio": "Wonder Cat Animation", "day": "Sabtu"},
    {"id": "shrouding_the_heavens", "title": "Menyelubungi Langit (Shrouding the Heavens)", "cn": "遮天", "episodes": 177, "status": "Ongoing", "genres": ["Kultivasi", "Sci-Fi", "Aksi"], "rating": 9.5, "studio": "Sparkly Key", "day": "Rabu"},
    {"id": "soul_land_2", "title": "Benua Douluo 2: Sekte Tang Tanpa Tanding (Soul Land 2)", "cn": "斗罗大陆II", "episodes": 168, "status": "Ongoing", "genres": ["Kultivasi", "Aksi", "Fantasi"], "rating": 9.6, "studio": "Sparkly Key", "day": "Sabtu"},
    {"id": "renegade_immortal", "title": "Kultivasi Tanpa Batas (Renegade Immortal)", "cn": "仙逆", "episodes": 156, "status": "Ongoing", "genres": ["Kultivasi", "Aksi", "Fantasi"], "rating": 9.9, "studio": "Tencent Penguin Pictures", "day": "Senin"},
    {"id": "big_brother", "title": "Kakak Tertua Terhebat (Big Brother)", "cn": "师兄啊师兄", "episodes": 156, "status": "Ongoing", "genres": ["Komedi", "Kultivasi", "Fantasi"], "rating": 9.6, "studio": "Sparkly Key", "day": "Kamis"},
    {"id": "immortality_yongsheng", "title": "Keabadian Tanpa Batas (Immortality)", "cn": "永生", "episodes": 148, "status": "Ongoing", "genres": ["Kultivasi", "Aksi", "Wuxia"], "rating": 9.4, "studio": "Shanghai Foch Film", "day": "Jumat"},
    {"id": "martial_universe", "title": "Semesta Bela Diri (Martial Universe)", "cn": "武动乾坤", "episodes": 140, "status": "Ongoing", "genres": ["Kultivasi", "Wuxia", "Aksi"], "rating": 9.4, "studio": "Shanghai Foch Film", "day": "Selasa"},
    {"id": "zhen_wu_dianfeng", "title": "Puncak Bela Diri Sejati (The Peak of True Martial Arts)", "cn": "真武巅峰", "episodes": 140, "status": "Ongoing", "genres": ["Kultivasi", "Aksi", "Fantasi"], "rating": 8.5, "studio": "Ruo Hong Culture", "day": "Selasa"},
    {"id": "throne_of_seal", "title": "Singgasana Segel Suci (Throne of Seal)", "cn": "神印王座", "episodes": 120, "status": "Ongoing", "genres": ["Fantasi", "Kultivasi", "Aksi"], "rating": 9.7, "studio": "Sparkly Key", "day": "Kamis"},
    {"id": "tales_of_herding_gods", "title": "Kisah Menggembala Dewa (Tales of Herding Gods)", "cn": "牧神记", "episodes": 97, "status": "Ongoing", "genres": ["Kultivasi", "Aksi", "Fantasi"], "rating": 9.9, "studio": "Sparkly Key", "day": "Minggu"},
    {"id": "the_demon_hunter", "title": "Sang Pemburu Iblis (The Demon Hunter)", "cn": "沧元图", "episodes": 92, "status": "Tamat", "genres": ["Aksi", "Fantasi", "Kultivasi"], "rating": 9.7, "studio": "Sparkly Key", "day": "Kamis"},
    {"id": "magic_chef_ice_fire", "title": "Koki Ajaib Es dan Api (The Magic Chef of Ice and Fire)", "cn": "冰火魔厨", "episodes": 92, "status": "Ongoing", "genres": ["Fantasi", "Sihir", "Kuliner"], "rating": 9.2, "studio": "Sparkly Key", "day": "Sabtu"},
    {"id": "the_great_ruler", "title": "Penguasa Agung Semesta (The Great Ruler)", "cn": "大主宰", "episodes": 88, "status": "Ongoing", "genres": ["Kultivasi", "Romantis", "Aksi"], "rating": 9.4, "studio": "Shanghai Foch Film", "day": "Selasa"},
    {"id": "jade_dynasty", "title": "Legenda Pedang Giok (Jade Dynasty)", "cn": "诛仙", "episodes": 82, "status": "Ongoing", "genres": ["Wuxia", "Romantis", "Kultivasi"], "rating": 9.5, "studio": "Cloud Art", "day": "Sabtu"},
    {"id": "against_the_gods", "title": "Melawan Kehendak Para Dewa (Against the Gods)", "cn": "逆天邪神", "episodes": 52, "status": "Ongoing", "genres": ["Kultivasi", "Reinkarnasi", "Aksi"], "rating": 9.2, "studio": "Shanghai Foch Film", "day": "Senin"},
    {"id": "aliens_among_immortals", "title": "Pendatang Asing Di Antara Para Dewa", "cn": "异人之下", "episodes": 57, "status": "Ongoing", "genres": ["Aksi", "Wuxia", "Misteri"], "rating": 6.8, "studio": "Tencent Penguin Pictures", "day": "Kamis"},
    {"id": "apotheosis", "title": "Menempa Diri Menjadi Dewa (Apotheosis)", "cn": "百炼成神", "episodes": 80, "status": "Ongoing", "genres": ["Kultivasi", "Aksi", "Fantasi"], "rating": 9.1, "studio": "Tencent Penguin Pictures", "day": "Kamis"},
    {"id": "lord_of_mysteries", "title": "Tuan Sang Penguasa Misteri (Lord of the Mysteries)", "cn": "诡秘之主", "episodes": 12, "status": "Tamat", "genres": ["Misteri", "Fantasi", "Sci-Fi"], "rating": 9.8, "studio": "B.CMAY PICTURES", "day": "Jumat"},
    {"id": "fog_hill", "title": "Kabut Lima Elemen (Fog Hill of Five Elements)", "cn": "雾山五行", "episodes": 8, "status": "Tamat", "genres": ["Wuxia", "Aksi", "Fantasi"], "rating": 9.9, "studio": "Samsara Animation", "day": "Kamis"},
    {"id": "modao", "title": "Sang Guru Sekte Iblis (Grandmaster of Demonic Cultivation)", "cn": "魔道祖师", "episodes": 35, "status": "Tamat", "genres": ["Wuxia", "Misteri", "Aksi"], "rating": 9.8, "studio": "B.CMAY PICTURES", "day": "Sabtu"},
    {"id": "heaven_officials_blessing", "title": "Berkah Pejabat Surga (Heaven Official's Blessing)", "cn": "天官赐福", "episodes": 24, "status": "Tamat", "genres": ["Fantasi", "Romantis", "Misteri"], "rating": 9.7, "studio": "B.CMAY PICTURES", "day": "Rabu"},
    {"id": "link_click", "title": "Agen Penjelajah Waktu (Link Click)", "cn": "时光代理人", "episodes": 24, "status": "Tamat", "genres": ["Misteri", "Fantasi", "Sci-Fi"], "rating": 9.8, "studio": "Bilibili Studio", "day": "Jumat"},
    {"id": "yuan_zun", "title": "Pangeran Naga Yuan (Dragon Prince Yuan)", "cn": "元尊", "episodes": 26, "status": "Tamat", "genres": ["Kultivasi", "Aksi", "Fantasi"], "rating": 9.6, "studio": "Sparkly Key", "day": "Kamis"},
    {"id": "hua_jiang_hu_buliangren", "title": "Pendekar Sungai dan Danau: Bu Liang Ren", "cn": "画江湖之不良人", "episodes": 120, "status": "Tamat", "genres": ["Wuxia", "Sejarah", "Aksi"], "rating": 9.9, "studio": "Rocen Digital", "day": "Kamis"},
    {"id": "scissor_seven", "title": "Gunting Sakti Tujuh (Scissor Seven)", "cn": "刺客伍六七", "episodes": 40, "status": "Tamat", "genres": ["Komedi", "Aksi", "Wuxia"], "rating": 9.8, "studio": "AHA Entertainment", "day": "Rabu"},
    {"id": "kings_avatar", "title": "Avatar Sang Raja Game (The King's Avatar)", "cn": "全职高手", "episodes": 36, "status": "Tamat", "genres": ["eSports", "Aksi", "Gaming"], "rating": 9.7, "studio": "B.CMAY", "day": "Jumat"},
    {"id": "hitori_no_shita", "title": "Manusia Luar Biasa (The Outcast)", "cn": "一人之下", "episodes": 54, "status": "Tamat", "genres": ["Aksi", "Wuxia", "Misteri"], "rating": 9.6, "studio": "Haoliners", "day": "Kamis"},
    {"id": "ling_cage", "title": "Sangkar Jiwa (Ling Cage: Incarnation)", "cn": "灵笼", "episodes": 16, "status": "Tamat", "genres": ["Sci-Fi", "Aksi", "Post-Apocalyptic"], "rating": 9.8, "studio": "YHKT Entertainment", "day": "Sabtu"},
    {"id": "fox_spirit_matchmaker", "title": "Mak Comblang Peri Rubah (Fox Spirit Matchmaker)", "cn": "狐妖小红娘", "episodes": 140, "status": "Tamat", "genres": ["Romantis", "Fantasi", "Komedi"], "rating": 9.7, "studio": "Haoliners", "day": "Sabtu"},
    {"id": "the_legend_of_qin", "title": "Legenda Dinasti Qin (The Legend of Qin)", "cn": "秦时明月", "episodes": 175, "status": "Tamat", "genres": ["Wuxia", "Sejarah", "Aksi"], "rating": 9.8, "studio": "Sparkly Key", "day": "Kamis"},
    {"id": "nine_songs_of_moving_heavens", "title": "Sembilan Lagu Langit (Nine Songs of the Moving Heavens)", "cn": "天行九歌", "episodes": 90, "status": "Tamat", "genres": ["Wuxia", "Sejarah", "Misteri"], "rating": 9.7, "studio": "Sparkly Key", "day": "Rabu"},
    {"id": "transcendent_heroes", "title": "Pasukan Pahlawan Transenden (Transcendent Heroes)", "cn": "雄兵连", "episodes": 60, "status": "Tamat", "genres": ["Sci-Fi", "Aksi", "Superhero"], "rating": 9.6, "studio": "Chao Shen Xue Yuan", "day": "Sabtu"},
    {"id": "full_time_magister", "title": "Penyihir Serba Bisa (Full-Time Magister)", "cn": "全职法师", "episodes": 72, "status": "Tamat", "genres": ["Sihir", "Aksi", "Sekolah"], "rating": 9.2, "studio": "Shanghai Foch Film", "day": "Minggu"},
    {"id": "white_cat_legend", "title": "Buku Harian Kucing Putih (White Cat Legend)", "cn": "大理寺日志", "episodes": 24, "status": "Tamat", "genres": ["Misteri", "Komedi", "Sejarah"], "rating": 9.6, "studio": "B.CMAY PICTURES", "day": "Selasa"},
    {"id": "fairies_album", "title": "Buku Catatan Siluman (Fairies Album)", "cn": "百妖谱", "episodes": 36, "status": "Tamat", "genres": ["Fantasi", "Supranatural", "Drama"], "rating": 9.5, "studio": "Haoliners", "day": "Rabu"},
    {"id": "no_doubt_in_us", "title": "Jiwa Tertukar Kaisar dan Ratu (No Doubt In Us)", "cn": "两不疑", "episodes": 48, "status": "Tamat", "genres": ["Romantis", "Komedi", "Sejarah"], "rating": 9.5, "studio": "Paper Plane Animation", "day": "Kamis"},
    {"id": "carp_reborn", "title": "Kelahiran Kembali Ikan Mas (Carp Reborn)", "cn": "元龙", "episodes": 32, "status": "Tamat", "genres": ["Isekai", "Sci-Fi", "Kultivasi"], "rating": 9.2, "studio": "Bilibili Studio", "day": "Minggu"},
    {"id": "tomb_of_fallen_gods", "title": "Makam Para Dewa (Tomb of Fallen Gods)", "cn": "神墓", "episodes": 30, "status": "Ongoing", "genres": ["Kultivasi", "Mitologi", "Aksi"], "rating": 9.1, "studio": "Wonder Cat Animation", "day": "Sabtu"},
    {"id": "island_of_siliang", "title": "Pulau Misteri Siliang (The Island of Siliang)", "cn": "眷思量", "episodes": 15, "status": "Tamat", "genres": ["Fantasi", "Romantis", "Misteri"], "rating": 9.6, "studio": "Year Young Culture", "day": "Kamis"},
    {"id": "snow_eagle_lord", "title": "Tuan Rajawali Salju (Snow Eagle Lord)", "cn": "雪鹰领主", "episodes": 78, "status": "Tamat", "genres": ["Kultivasi", "Aksi", "Fantasi"], "rating": 9.2, "studio": "Shanghai Foch Film", "day": "Selasa"},
    {"id": "stellar_transformations", "title": "Transformasi Bintang (Stellar Transformation)", "cn": "星辰变", "episodes": 65, "status": "Tamat", "genres": ["Kultivasi", "Wuxia", "Aksi"], "rating": 9.3, "studio": "Shanghai Foch Film", "day": "Rabu"},
    {"id": "immortal_king", "title": "Kehidupan Raja Abadi (The Daily Life of the Immortal King)", "cn": "仙王的日常生活", "episodes": 48, "status": "Tamat", "genres": ["Komedi", "Fantasi", "Aksi"], "rating": 9.3, "studio": "Haoliners", "day": "Minggu"},
    {"id": "ze_tian_ji", "title": "Pilihan Takdir (Fighter of the Destiny)", "cn": "择天记", "episodes": 60, "status": "Tamat", "genres": ["Kultivasi", "Fantasi", "Wuxia"], "rating": 9.3, "studio": "Shanghai Foch Film", "day": "Kamis"},
    {"id": "dragon_raja", "title": "Klan Raja Naga (Dragon Raja)", "cn": "龙族", "episodes": 16, "status": "Tamat", "genres": ["Fantasi", "Aksi", "Sci-Fi"], "rating": 9.4, "studio": "Garden Culture", "day": "Jumat"},
    {"id": "first_order", "title": "Urutan Pertama (The First Order)", "cn": "第一序列", "episodes": 16, "status": "Tamat", "genres": ["Sci-Fi", "Aksi", "Komedi"], "rating": 9.3, "studio": "Bilibili", "day": "Jumat"},
    {"id": "spare_me_great_lord", "title": "Ampuni Aku Yang Mulia (Spare Me, Great Lord!)", "cn": "大王饶命", "episodes": 24, "status": "Tamat", "genres": ["Komedi", "Kultivasi", "Aksi"], "rating": 9.5, "studio": "Big Firebird", "day": "Sabtu"},
    {"id": "junior_white_horse", "title": "Masa Muda Mabuk Angin (Dashing Youth)", "cn": "少年白马醉春风", "episodes": 26, "status": "Tamat", "genres": ["Wuxia", "Aksi", "Petualangan"], "rating": 9.5, "studio": "Build Dream", "day": "Rabu"},
    {"id": "shaonian_ge_xing", "title": "Lagu Pemuda Pengelana (Great Journey of Teenagers)", "cn": "少年歌行", "episodes": 52, "status": "Tamat", "genres": ["Wuxia", "Aksi", "Petualangan"], "rating": 9.6, "studio": "Build Dream", "day": "Selasa"},
    {"id": "rakshasa_street", "title": "Jalan Rakshasa (Rakshasa Street)", "cn": "镇魂街", "episodes": 48, "status": "Tamat", "genres": ["Aksi", "Supranatural", "Fantasi"], "rating": 9.6, "studio": "L2Studio", "day": "Sabtu"},
    {"id": "cinderella_chef", "title": "Koki Modern Cinderella (Cinderella Chef)", "cn": "萌妻食神", "episodes": 36, "status": "Tamat", "genres": ["Isekai", "Romantis", "Kuliner"], "rating": 9.2, "studio": "Wulifang", "day": "Sabtu"},
]


def fetch_donghive_data():
    """Try to fetch from Donghive API"""
    print("Fetching from Donghive API...")
    try:
        url = "{}/home".format(API_BASE)
        req = urllib.request.Request(url, headers={
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
            'Accept': 'application/json'
        })
        with urllib.request.urlopen(req, timeout=15) as response:
            data = json.loads(response.read().decode())
            if data.get("status") == "success":
                d = data.get("data", {})
                items = []
                for key in ["latest", "popular"]:
                    items.extend(d.get(key, []))
                print("  Got {} items from Donghive".format(len(items)))
                return items
    except Exception as e:
        print("  Donghive failed: {}".format(e))
    return []


def merge_data(known_donghua, donghive_items):
    """Merge known data with Donghive data"""
    merged = {}
    
    # Start with known data
    for item in known_donghua:
        merged[item["id"]] = {
            "id": item["id"],
            "title": item["title"],
            "chineseTitle": item.get("cn", ""),
            "currentEpisodes": item["episodes"],
            "totalEpisodes": 0 if item["status"] == "Ongoing" else item["episodes"],
            "status": item["status"],
            "genres": item.get("genres", []),
            "rating": item.get("rating", 8.5),
            "studio": item.get("studio", "Unknown"),
            "uploadDay": item.get("day", "Unknown"),
            "isRecentlyUpdated": True,
            "latestEpisodeUpdateNote": "Episode {} Sub Indo!".format(item["episodes"]),
            "poster": ""
        }
    
    # Update with Donghive data (higher episode count wins)
    for item in donghive_items:
        title = item.get("title", "")
        episode = 0
        match = re.search(r'episode\s+(\d+)', title.lower())
        if match:
            episode = int(match.group(1))
        
        slug = item.get("slug", "")
        # Try to find matching entry
        for existing_id, existing in merged.items():
            cn = existing.get("chineseTitle", "")
            clean_title = re.sub(r'\s+', ' ', title).strip()
            if cn and cn in clean_title:
                if episode > existing["currentEpisodes"]:
                    existing["currentEpisodes"] = episode
                    existing["latestEpisodeUpdateNote"] = "Episode {} Sub Indo!".format(episode)
                    print("  Updated: {} -> Episode {}".format(existing["title"][:35], episode))
                break
    
    return list(merged.values())


def main():
    print("=" * 60)
    print("MULTI-SOURCE DONGHUA SCRAPER")
    print("=" * 60)
    print()
    
    # Load existing catalog
    existing_catalog = []
    if os.path.exists(CATALOG_FILE):
        with open(CATALOG_FILE, "r", encoding="utf-8") as f:
            existing_catalog = json.load(f)
        print("Existing catalog: {} titles".format(len(existing_catalog)))
    
    # Fetch from Donghive
    donghive_items = fetch_donghive_data()
    
    # Merge all data
    print()
    print("Merging data from all sources...")
    new_catalog = merge_data(KNOWN_DONGHUA, donghive_items)
    
    # Sort by episode count
    new_catalog.sort(key=lambda x: x.get("currentEpisodes", 0), reverse=True)
    
    # Save
    with open(CATALOG_FILE, "w", encoding="utf-8") as f:
        json.dump(new_catalog, f, ensure_ascii=False, indent=2)
    
    print()
    print("=" * 60)
    print("RESULTS: {} donghua saved to {}".format(len(new_catalog), CATALOG_FILE))
    print("=" * 60)
    print()
    print("Top 20 by episode count:")
    for i, entry in enumerate(new_catalog[:20], 1):
        print("  {}. {} -> Episode {}".format(
            i, entry['title'][:45], entry['currentEpisodes']))
    
    ongoing = sum(1 for e in new_catalog if e["status"] == "Ongoing")
    tamat = sum(1 for e in new_catalog if e["status"] == "Tamat")
    total_eps = sum(e["currentEpisodes"] for e in new_catalog)
    print()
    print("Stats: {} ongoing | {} tamat | {} total episodes".format(ongoing, tamat, total_eps))


if __name__ == "__main__":
    main()
