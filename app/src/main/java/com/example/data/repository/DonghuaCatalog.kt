package com.example.data.repository

import com.example.R
import com.example.data.model.Donghua
import com.example.data.model.Episode
import com.example.data.model.VipPlan

object DonghuaCatalog {

    val VIP_PLANS = listOf(
        VipPlan(
            id = "vip_7d",
            name = "VIP 7 Hari",
            durationDays = 7,
            priceRupiah = 5000,
            priceFormatted = "Rp 5.000",
            pricePerDay = "Rp 714 / hari",
            isPopular = false,
            discountBadge = "Pilihan Hemat",
            description = "Akses penuh streaming 4K tanpa iklan dengan Subtitle Indonesia lengkap selama 1 minggu."
        ),
        VipPlan(
            id = "vip_14d",
            name = "VIP 14 Hari",
            durationDays = 14,
            priceRupiah = 10000,
            priceFormatted = "Rp 10.000",
            pricePerDay = "Rp 714 / hari",
            isPopular = false,
            discountBadge = "Paket 2 Minggu",
            description = "Streaming bebas gangguan Sub Indo dan download episode tanpa batas selama 2 pekan."
        ),
        VipPlan(
            id = "vip_30d",
            name = "VIP 30 Hari",
            durationDays = 30,
            priceRupiah = 20000,
            priceFormatted = "Rp 20.000",
            pricePerDay = "Rp 667 / hari",
            isPopular = true,
            discountBadge = "Paling Populer 🔥",
            description = "Paket bulanan terlengkap! Akses prioritas rilis tercepat Sub Indo dan kualitas 4K HDR."
        )
    )

    val DAYS_OF_WEEK = listOf(
        "Semua Hari",
        "Senin",
        "Selasa",
        "Rabu",
        "Kamis",
        "Jumat",
        "Sabtu",
        "Minggu"
    )

    val GENRES = listOf(
        "Semua",
        "Kultivasi",
        "Wuxia",
        "Aksi",
        "Fantasi",
        "Reinkarnasi",
        "Romantis",
        "Sci-Fi",
        "Komedi",
        "Misteri",
        "Film Donghua"
    )

    val STUDIOS = listOf(
        "Semua Studio",
        "Sparkly Key (Xuanji)",
        "B.CMAY PICTURES",
        "Shanghai Foch Film",
        "Tencent Penguin Pictures",
        "Bilibili Studio",
        "Light Chaser Animation",
        "Wonder Cat Animation",
        "Ruo Hong Culture",
        "Samsara Animation",
        "Haoliners Animation",
        "Build Dream",
        "Year Young Culture",
        "Rocen Digital",
        "Soyep Culture",
        "L2Studio"
    )

    fun generateEpisodes(donghuaId: String, count: Int, baseTitle: String): List<Episode> {
        return (1..count).map { epNum ->
            val isVip = epNum > (count - 2).coerceAtLeast(3)
            val isNewest = epNum == count
            Episode(
                id = "${donghuaId}_ep_$epNum",
                donghuaId = donghuaId,
                episodeNumber = epNum,
                title = "Episode $epNum (Sub Indo)",
                duration = "22:${(10..55).random()}",
                durationSeconds = 1320 + (epNum * 15) % 180,
                downloadSizeMb = 180 + (epNum * 12) % 150,
                isVipOnly = isVip,
                synopsis = "Pertarungan dahsyat memuncak saat batas kultivasi ditembus. Teknik rahasia kuno dikerahkan menghadapi musuh bebuyutan di episode $epNum. Lengkap Audio Mandarin dan Subtitle Bahasa Indonesia jernih.",
                isNewlyReleased = isNewest,
                releaseDateText = if (isNewest) "Baru Rilis Sub Indo!" else "Episode Sebelumnya"
            )
        }
    }

    private fun d(
        id: String,
        title: String,
        cn: String,
        studio: String,
        genres: List<String>,
        synopsis: String,
        rating: Double,
        totalEp: Int,
        currEp: Int,
        status: String = "Ongoing",
        year: Int = 2023,
        isMovie: Boolean = false,
        views: String = "10.5M",
        posterRes: Int? = null,
        bannerRes: Int? = null,
        badge: String = "Ongoing",
        badgeColor: Long = 0xFF7C4DFF,
        posterColor: Long = 0xFF1E1B4B,
        day: String = "Senin",
        time: String = "10:00 WIB",
        isRecent: Boolean = true,
        updateNote: String = "Episode Baru Sub Indo Rilis!",
        movieDurMin: Int? = null
    ): Donghua {
        return Donghua(
            id = id,
            title = title,
            chineseTitle = cn,
            studio = studio,
            genres = genres,
            synopsis = "$synopsis (Tersedia Audio Asli Mandarin dengan Subtitle Bahasa Indonesia HD/4K)",
            rating = rating,
            totalEpisodes = totalEp,
            currentEpisodes = currEp,
            status = status,
            releaseYear = year,
            isMovie = isMovie,
            isVipExclusive = false,
            viewCount = views,
            posterDrawableRes = posterRes,
            bannerDrawableRes = bannerRes,
            badgeText = badge,
            badgeColorHex = badgeColor,
            posterColorHex = posterColor,
            uploadDay = day,
            uploadTime = time,
            isRecentlyUpdated = isRecent,
            latestEpisodeUpdateNote = updateNote,
            durationMinutes = movieDurMin,
            episodes = if (isMovie) {
                listOf(
                    Episode(
                        id = "${id}_full",
                        donghuaId = id,
                        episodeNumber = 1,
                        title = "$title [Full Movie Sub Indo]",
                        duration = "${movieDurMin ?: 100}:00",
                        durationSeconds = (movieDurMin ?: 100) * 60,
                        downloadSizeMb = 1200,
                        isVipOnly = false,
                        synopsis = "$synopsis Disertai Subtitle Bahasa Indonesia resmi kualitas bioskop."
                    )
                )
            } else {
                generateEpisodes(id, currEp.coerceAtLeast(1), title)
            }
        )
    }

    val ALL_DONGHUA: List<Donghua> = listOf(
        // === SERIAL DONGHUA UTAMA 3D CGI DENGAN JUDUL BAHASA INDONESIA & SUB INDO ===
        d("tales_of_herding_gods", "Kisah Menggembala Dewa (Tales of Herding Gods / Mu Shen Ji)", "牧神记", "Sparkly Key / Tencent", listOf("Kultivasi", "Aksi", "Fantasi", "Misteri"), "Qin Mu dibesarkan oleh sembilan orang tua cacat sakti di Desa Orang Cacat di Reruntuhan Besar. Membawa Pedang Jiwa dan Tubuh Hegemon Overlord menembus misteri para dewa purba.", 9.86, 52, 28, "Ongoing", 2024, false, "18.4M", R.drawable.poster_tales_of_herding_gods, null, "Hot", 0xFFEAB308, 0xFF713F12, "Minggu", "10:00 WIB", true, "Episode 28 Sub Indo Rilis!"),
        d("renegade_immortal", "Kultivasi Tanpa Batas (Renegade Immortal / Xian Ni)", "仙逆", "Tencent Penguin Pictures", listOf("Kultivasi", "Aksi", "Fantasi", "Wuxia"), "Wang Lin adalah pemuda biasa yang memasuki jalan kultivasi demi membalas dendam keluarga dan melindungi orang terkasih. Menempuh jalan pembantaian demi menjadi Penguasa Alam Semesta.", 9.92, 100, 62, "Ongoing", 2023, false, "19.8M", R.drawable.poster_renegade_immortal, null, "Hot", 0xFFDC2626, 0xFF18181B, "Senin", "10:00 WIB", true, "Episode 62 Sub Indo Rilis!"),
        d("perfect_world", "Dunia Sempurna (Perfect World / Wanmei Shijie)", "完美世界", "Shanghai Foch Film", listOf("Kultivasi", "Fantasi", "Aksi", "Reinkarnasi"), "Dilahirkan dengan Tulang Tertinggi yang dicuri, Shi Hao dibesarkan di Desa Batu terpencil. Dengan tekad membara, Kaisar Huang bangkit menaklukkan Delapan Wilayah Bawah hingga Wilayah Atas.", 9.88, 200, 188, "Ongoing", 2021, false, "22.4M", R.drawable.poster_perfect_world, null, "Hot", 0xFFEA580C, 0xFF78350F, "Jumat", "10:00 WIB", true, "Episode 188 Pertempuran Kaisar Sub Indo!"),
        d("swallowed_star", "Bintang Pemangsa (Swallowed Star / Tunshi Xingkong)", "吞噬星空", "Sparkly Key (Xuanji)", listOf("Sci-Fi", "Aksi", "Kultivasi", "Fantasi"), "Virus RR mengubah bumi menjadi sarang monster buas. Luo Feng bermutasi menjadi Petarung Roh Bintang, menembus atmosfer bumi menuju panggung alam semesta kosmik.", 9.78, 160, 142, "Ongoing", 2020, false, "17.9M", R.drawable.poster_swallowed_star, null, "Hot", 0xFF2563EB, 0xFF0F172A, "Rabu", "10:00 WIB", true, "Episode 142 Pertarungan Kosmik Sub Indo!"),
        d("soul_land", "Benua Douluo (Soul Land / Douluo Dalu)", "斗罗大陆", "Sparkly Key (Xuanji)", listOf("Kultivasi", "Aksi", "Fantasi", "Romantis"), "Tang San bereinkarnasi ke Benua Douluo membawa warisan Sekte Tang. Membangkitkan Spirit Kembar 'Blue Silver Grass' dan 'Clear Sky Hammer' bersama 7 Iblis Shrek.", 9.84, 260, 260, "Tamat", 2018, false, "35.5M", null, R.drawable.banner_soul_land, "Tamat", 0xFF059669, 0xFF1E3A8A, "Senin", "10:00 WIB", true, "Semua 260 Episode Tamat Sub Indo Remastered 4K"),
        d("btth", "Pertarungan Memecah Langit (Battle Through the Heavens / BTTH)", "斗破苍穹", "Shanghai Foch Film", listOf("Kultivasi", "Wuxia", "Aksi", "Romantis"), "Xiao Yan yang kehilangan bakatnya bangkit kembali di bawah bimbingan Yao Lao untuk menaklukkan Heavenly Flame dan membalaskan dendamnya.", 9.76, 130, 112, "Ongoing", 2017, false, "28.2M", null, R.drawable.banner_btth, "Ongoing", 0xFFEA580C, 0xFF7C2D12, "Minggu", "10:00 WIB", true, "Episode 112 Baru Rilis Sub Indo!"),
        d("fanren", "Catatan Perjalanan Kultivator Fana (A Record of a Mortal's Journey)", "凡人修仙传", "Wonder Cat Animation", listOf("Kultivasi", "Wuxia", "Petualangan", "Aksi"), "Han Li adalah pemuda desa miskin yang menemukan botol misterius pemupuk herbal kultivasi. Melangkah dengan penuh kehati-hatian di dunia kultivator kejam.", 9.85, 150, 118, "Ongoing", 2020, false, "21.6M", R.drawable.poster_fanren, null, "Top Rated", 0xFF10B981, 0xFF064E3B, "Sabtu", "11:00 WIB", true, "Episode 118 Arc Lautan Bintang Sub Indo!"),
        d("shrouding_the_heavens", "Menyelubungi Langit (Shrouding the Heavens / Zhe Tian)", "遮天", "Sparkly Key (Xuanji)", listOf("Kultivasi", "Sci-Fi", "Aksi", "Fantasi"), "Sembilan naga raksasa menarik peti perunggu kuno di puncak Gunung Tai membawa Ye Fan melintasi lubang cacing kosmik menuju Bintang Biduk Purba.", 9.54, 100, 76, "Ongoing", 2023, false, "13.8M", R.drawable.poster_shrouding_heavens, null, "Hot", 0xFFDC2626, 0xFF451A03, "Rabu", "10:00 WIB", true, "Episode 76 Pertarungan Sekte Purba Sub Indo!"),
        d("fog_hill", "Kabut Lima Elemen (Fog Hill of Five Elements / Wu Shan Wu Xing)", "雾山五行", "Samsara Animation", listOf("Wuxia", "Aksi", "Fantasi", "Seni"), "Mahakarya animasi lukisan tinta tradisional Tiongkok yang memukau. Kisah lima penguasa elemen menjaga segel monster purba di Gunung Berkabut.", 9.90, 8, 8, "Tamat", 2020, false, "18.1M", R.drawable.poster_fog_hill, null, "Masterpiece", 0xFFD97706, 0xFF9A3412, "Kamis", "12:00 WIB", false, "Semua Episode Season 1 & 2 Lengkap Sub Indo"),
        d("jade_dynasty", "Legenda Pedang Giok (Jade Dynasty / Zhu Xian)", "诛仙", "Cloud Art / Tencent", listOf("Wuxia", "Romantis", "Kultivasi", "Aksi"), "Zhang Xiaofan selamat dari pembantaian desa dan diterima di Sekte Qingyun, terjebak cinta antara Lu Xueqi dan Bi Yao.", 9.48, 78, 52, "Ongoing", 2022, false, "12.5M", R.drawable.poster_jade_dynasty, null, "Ongoing", 0xFF059669, 0xFF065F46, "Sabtu", "10:00 WIB", true, "Episode 52 Pertempuran Bi Yao Sub Indo!"),
        d("lord_of_mysteries", "Tuan Sang Penguasa Misteri (Lord of the Mysteries)", "诡秘之主", "B.CMAY PICTURES", listOf("Misteri", "Fantasi", "Sci-Fi", "Aksi"), "Zhou Mingrui bertransmigrasi ke era uap Victoria yang dipenuhi ramuan mistis, dewa kuno, dan tarot perkumpulan Tarot Club sebagai 'The Fool'.", 9.80, 12, 12, "Tamat", 2024, false, "14.6M", R.drawable.poster_lord_of_mysteries, null, "Trending", 0xFF6366F1, 0xFF1E1B4B, "Jumat", "18:00 WIB", true, "Musim Pertama Lengkap Sub Indo!"),
        d("soul_land_2", "Benua Douluo 2: Sekte Tang Tanpa Tanding (Soul Land 2)", "斗罗大陆II 绝世唐门", "Sparkly Key (Xuanji)", listOf("Kultivasi", "Aksi", "Fantasi", "Romantis"), "Sepuluh ribu tahun setelah Sekte Tang berdiri, Yuhao Huo dengan Mata Roh bangkit bersama Roh Naga Es demi kejayaan sekte.", 9.62, 104, 68, "Ongoing", 2023, false, "15.7M", R.drawable.poster_soul_land_2, null, "New", 0xFF7C4DFF, 0xFF0369A1, "Sabtu", "10:00 WIB", true, "Episode 68 Baru Update Sub Indo!"),
        d("modao", "Sang Guru Sekte Iblis (Grandmaster of Demonic Cultivation)", "魔道祖师", "B.CMAY PICTURES", listOf("Wuxia", "Misteri", "Aksi", "Fantasi"), "Wei Wuxian sang Yiling Laozu bereinkarnasi 13 tahun setelah kematiannya. Bersama Lan Wangji menyelidiki konspirasi mayat pembunuh.", 9.82, 35, 35, "Tamat", 2018, false, "24.7M", R.drawable.poster_modao, null, "Tamat", 0xFF059669, 0xFF18181B, "Sabtu", "12:00 WIB", false, "Final Season Lengkap Sub Indo"),
        d("aliens_among_immortals", "Pendatang Asing Di Antara Para Dewa (Aliens Among Immortals)", "盗墓笔记 / 异人之下", "Tencent Penguin Pictures", listOf("Aksi", "Wuxia", "Misteri", "Kultivasi"), "Di tengah dunia kultivator modern dan kuno, para pengembara misterius memperebutkan artefak kuno dan jurus rahasia delapan keajaiban langit.", 6.76, 60, 57, "Ongoing", 2023, false, "7.8M", R.drawable.poster_aliens_among_immortals, null, "New", 0xFF7C4DFF, 0xFF1E1B4B, "Kamis", "10:00 WIB", true, "Episode 57 Baru Rilis Sub Indo!"),

        // === SERIAL ADAPTASI NOVEL KULTIVASI & XIANXIA ===
        d("throne_of_seal", "Singgasana Segel Suci (Throne of Seal / Shen Yin Wang Zuo)", "神印王座", "Sparkly Key (Xuanji)", listOf("Fantasi", "Kultivasi", "Aksi", "Romantis"), "Long Haochen berjuang bersama Ksatria Kuil Suci mempertahankan umat manusia dari invasi 72 Pilar Dewa Iblis bersama Cai'er.", 9.68, 120, 114, "Ongoing", 2022, false, "18.9M", null, null, "Hot", 0xFFEAB308, 0xFF78350F, "Kamis", "10:00 WIB", true, "Episode 114 Pertempuran Kuil Suci Sub Indo!"),
        d("yuan_zun", "Pangeran Naga Yuan (Dragon Prince Yuan / Yuan Zun)", "元尊", "Sparkly Key / Tencent", listOf("Kultivasi", "Aksi", "Fantasi"), "Zhou Yuan yang Duri Sucinya dirampas oleh Dinasti Wu bangkit membuka delapan saluran meridian bersama anjing suci Tuntun dan gadis misterius Yaoyao.", 9.60, 26, 26, "Tamat", 2024, false, "16.2M", null, null, "Top 2024", 0xFF2563EB, 0xFF1E3A8A, "Kamis", "10:00 WIB", false, "Musim 1 Lengkap 4K Sub Indo"),
        d("martial_universe", "Semesta Bela Diri (Martial Universe / Wu Dong Qian Kun)", "武动乾坤", "Shanghai Foch Film", listOf("Kultivasi", "Wuxia", "Aksi"), "Lin Dong dari klan cabang tertindas menemukan jimat batu misterius pemurni bela diri untuk membasmi Klan Yimo.", 9.45, 60, 48, "Ongoing", 2019, false, "11.2M", null, null, "Hot", 0xFF7C3AED, 0xFF312E81, "Selasa", "10:00 WIB", true, "Episode 48 Pertarungan Benua Timur Sub Indo!"),
        d("the_great_ruler", "Penguasa Agung Semesta (The Great Ruler / Da Zhu Zai)", "大主宰", "Shanghai Foch Film", listOf("Kultivasi", "Romantis", "Aksi"), "Mu Chen dari Benua Seribu Besar melangkah ke Akademi Spiritual Utara dengan Roh Burung Sembilan Nether.", 9.35, 52, 52, "Tamat", 2023, false, "10.3M", null, null, "Tamat", 0xFF059669, 0xFF047857, "Selasa", "10:00 WIB", true, "Semua Episode 1-52 Tamat Sub Indo"),
        d("magic_chef_ice_fire", "Koki Ajaib Es dan Api (The Magic Chef of Ice and Fire)", "冰火魔厨", "Sparkly Key (Xuanji)", listOf("Fantasi", "Sihir", "Kuliner", "Aksi"), "Rong Nianbing menguasai sihir ganda es dan api untuk memasak hidangan surgawi sekaligus membalaskan dendam keluarganya.", 9.22, 100, 92, "Ongoing", 2021, false, "13.1M", null, null, "Ongoing", 0xFF06B6D4, 0xFF083344, "Sabtu", "10:00 WIB", true, "Episode 92 Seni Kuliner Es Api Sub Indo!"),
        d("against_the_gods", "Melawan Kehendak Para Dewa (Against the Gods / Ni Tian Xie Shen)", "逆天邪神", "Shanghai Foch Film", listOf("Kultivasi", "Reinkarnasi", "Aksi", "Romantis"), "Yun Che reinkarnasi dengan Mutiara Racun Langit dan Vena Dewa Jahat menguasai Benua Langit Mendalam.", 9.15, 50, 40, "Ongoing", 2023, false, "9.4M", null, null, "New", 0xFF7C4DFF, 0xFF581C87, "Senin", "10:00 WIB", true, "Episode 40 Sub Indo Update!"),
        d("heaven_officials_blessing", "Berkah Pejabat Surga (Heaven Official's Blessing)", "天官赐福", "B.CMAY PICTURES", listOf("Fantasi", "Romantis", "Misteri", "Wuxia"), "Xie Lian naik ke surga ketiga kalinya dan bertemu Hua Cheng sang Raja Iblis Hujan Merah.", 9.70, 24, 24, "Tamat", 2020, false, "18.8M", null, null, "Popular", 0xFFEC4899, 0xFF831843, "Rabu", "10:00 WIB", true, "Season 1 & 2 Lengkap Sub Indo!"),
        d("link_click", "Agen Penjelajah Waktu (Link Click / Shiguang Dailiren)", "时光代理人", "Bilibili Studio / Haoliners", listOf("Misteri", "Fantasi", "Sci-Fi", "Drama"), "Cheng Xiaoshi dan Lu Guang masuk ke dalam foto untuk menyelesaikan kasus masa lalu klien.", 9.75, 24, 24, "Tamat", 2021, false, "19.3M", null, null, "Tamat", 0xFF059669, 0xFF1E293B, "Jumat", "11:00 WIB", false, "Musim 1 & 2 Lengkap HD Sub Indo"),
        d("tales_of_demons_and_gods", "Kisah Iblis dan Dewa (Tales of Demons and Gods / Yao Shen Ji)", "妖神记", "Ruo Hong Culture", listOf("Reinkarnasi", "Kultivasi", "Aksi", "Fantasi"), "Nie Li sang Spiritualis Iblis terkuat terlahir kembali ke masa muda untuk melindungi Kota Glory.", 8.85, 350, 320, "Ongoing", 2017, false, "20.1M", null, null, "Ongoing", 0xFFF59E0B, 0xFF713F12, "Minggu", "10:00 WIB", true, "Episode 320 Baru Update Sub Indo!"),
        d("immortal_king", "Kehidupan Sehari-hari Raja Abadi (The Daily Life of the Immortal King)", "仙王的日常生活", "Haoliners Animation", listOf("Komedi", "Fantasi", "Aksi", "Sekolah"), "Wang Ling memiliki kultivasi tak terbatas dan ingin hidup santai dengan mie instan di SMA Songhai.", 9.30, 48, 48, "Tamat", 2020, false, "16.4M", null, null, "Popular", 0xFF06B6D4, 0xFF164E63, "Minggu", "11:00 WIB", false, "Musim 1-4 Lengkap Sub Indo"),
        d("stellar_transformations", "Transformasi Bintang (Stellar Transformation / Xing Chen Bian)", "星辰变", "Shanghai Foch Film", listOf("Kultivasi", "Wuxia", "Aksi"), "Qin Yu yang tanpa bakat meridian menguasai fisik luar dan Air Mata Meteorit menuju alam dewa bintang.", 9.28, 65, 65, "Tamat", 2018, false, "11.1M", null, null, "Tamat", 0xFF6366F1, 0xFF1E1B4B, "Rabu", "10:00 WIB", true, "Semua Episode 1-65 Tamat Sub Indo"),
        d("scumbag_system", "Sistem Penyelamat Penjahat (Scumbag System)", "穿书自救指南", "Shanghai Foch Film", listOf("Komedi", "Reinkarnasi", "Wuxia", "Fantasi"), "Shen Yuan bertransmigrasi menjadi Shen Qingqiu sang guru penjahat yang harus merayu muridnya.", 8.95, 10, 10, "Tamat", 2020, false, "8.7M", null, null, "Tamat", 0xFF059669, 0xFF14532D, "Kamis", "10:00 WIB", false, "Semua Episode Lengkap Sub Indo"),
        d("thousand_autumns", "Ribuan Musim Gugur (Thousand Autumns / Shanhe Jian Xin)", "山河剑心", "Sparkly Key (Xuanji)", listOf("Wuxia", "Aksi", "Misteri"), "Shen Qiao sang pemimpin sekte yang buta diselamatkan oleh Yan Wushi ketua Sekte Iblis.", 9.40, 16, 16, "Tamat", 2021, false, "9.8M", null, null, "Tamat", 0xFF059669, 0xFF3F3F46, "Senin", "10:00 WIB", false, "Semua Episode Lengkap Sub Indo"),
        d("island_of_siliang", "Pulau Misteri Siliang (The Island of Siliang / Juan Siliang)", "眷思量", "Year Young Culture", listOf("Fantasi", "Romantis", "Misteri", "Kultivasi"), "Pulau Siliang tempat pengasingan dewa. Jing Xuan dan Tu Li mengungkap rahasia kutukan pulau abadi.", 9.55, 15, 15, "Tamat", 2021, false, "10.6M", null, null, "CGI Top", 0xFF06B6D4, 0xFF0F766E, "Kamis", "10:00 WIB", false, "Visual 3D Menakjubkan Sub Indo"),
        d("snow_eagle_lord", "Tuan Rajawali Salju (Snow Eagle Lord / Xue Ying Ling Zhu)", "雪鹰领主", "Shanghai Foch Film", listOf("Kultivasi", "Aksi", "Fantasi"), "Dongbo Xueying melatih seni tombak mutlak demi membebaskan orang tuanya dari klan Mo Yang.", 9.20, 78, 78, "Tamat", 2018, false, "11.7M", null, null, "Tamat", 0xFF2563EB, 0xFF1E3A8A, "Selasa", "10:00 WIB", true, "Semua 78 Episode Tamat Sub Indo"),
        d("martial_master", "Guru Besar Bela Diri (Martial Master / Wu Shen Zhu Zai)", "武神主宰", "Ruo Hong Culture", listOf("Reinkarnasi", "Kultivasi", "Aksi"), "Qin Chen sang Dewa Bela Diri dikhianati sahabatnya dan terlahir kembali 300 tahun kemudian di tubuh pangeran klan rendahan.", 8.65, 500, 450, "Ongoing", 2020, false, "25.3M", null, null, "Ongoing", 0xFFF59E0B, 0xFF78350F, "Minggu", "10:00 WIB", true, "Episode 450 Baru Sub Indo!"),
        d("apotheosis", "Menempa Diri Menjadi Dewa (Apotheosis / Bai Lian Cheng Shen)", "百炼成神", "Tencent Penguin Pictures", listOf("Kultivasi", "Aksi", "Fantasi"), "Luo Zheng yang jatuh menjadi budak menemukan teknik penempaan tubuh menjadi Senjata Ilahi tertinggi.", 9.12, 100, 80, "Ongoing", 2022, false, "12.9M", null, null, "Ongoing", 0xFF7C3AED, 0xFF4C1D95, "Kamis", "10:00 WIB", true, "Episode 80 Sub Indo Update!"),
        d("immortality_yongsheng", "Keabadian Tanpa Batas (Immortality / Yong Sheng)", "永生", "Shanghai Foch Film", listOf("Kultivasi", "Aksi", "Wuxia"), "Fang Han, budak rendahan yang berpegang pada keyakinan 'lebih baik menjadi pengemis mandiri daripada budak kaisar', menembus gerbang keabadian.", 9.38, 48, 36, "Ongoing", 2022, false, "14.1M", null, null, "Hot", 0xFFDC2626, 0xFF450A0A, "Jumat", "10:00 WIB", true, "Episode 36 Baru Sub Indo!"),
        d("big_brother", "Kakak Tertua Terhebat (Big Brother / Shi Xiong A Shi Xiong)", "师兄啊师兄", "Sparkly Key (Xuanji)", listOf("Komedi", "Kultivasi", "Fantasi"), "Li Changshou bereinkarnasi ke zaman kuno sebelum Perang Penyegelan Dewa. Ia menyembunyikan kekuatannya secara ekstrem demi bertahan hidup.", 9.60, 60, 52, "Ongoing", 2023, false, "16.8M", null, null, "Top Comedy", 0xFF059669, 0xFF064E3B, "Kamis", "10:00 WIB", true, "Episode 52 Li Changshou Sub Indo!"),
        d("a_will_eternal", "Kehendak Abadi (A Will Eternal / Yi Nian Yong Heng)", "一念永恒", "B.CMAY PICTURES", listOf("Komedi", "Kultivasi", "Aksi"), "Bai Xiaochun pemuda penakut yang terobsesi dengan hidup abadi selalu membuat kekacauan jenaka di Sekte Lingxi.", 9.65, 106, 92, "Ongoing", 2020, false, "20.4M", null, null, "Hot", 0xFF2563EB, 0xFF1E3A8A, "Rabu", "10:00 WIB", true, "Episode 92 Bai Xiaochun Sub Indo!"),
        d("wu_geng_ji", "Kronik Perintah Dewa (Wu Geng Ji)", "武庚纪", "Sparkly Key (Xuanji)", listOf("Aksi", "Fantasi", "Mitologi"), "Pangeran Wu Geng memimpin umat manusia dan klan gelap memberontak melawan tirani para dewa langit.", 9.25, 150, 140, "Ongoing", 2016, false, "18.3M", null, null, "Epic", 0xFFD97706, 0xFF78350F, "Jumat", "10:00 WIB", true, "Episode 140 Pemberontakan Dewa Sub Indo!"),
        d("the_demon_hunter", "Sang Pemburu Iblis (The Demon Hunter / Cang Yuan Tu)", "沧元图", "Sparkly Key (Xuanji)", listOf("Aksi", "Fantasi", "Kultivasi"), "Meng Chuan bersumpah menjadi Pembasmi Iblis terhebat setelah desanya dihancurkan oleh monster laut.", 9.72, 26, 26, "Tamat", 2023, false, "15.9M", null, null, "Top CGI", 0xFF7C3AED, 0xFF2E1065, "Kamis", "10:00 WIB", true, "Pertarungan Final Meng Chuan Sub Indo!"),
        d("supreme_god_emperor", "Kaisar Dewa Tertinggi (Supreme God Emperor / Wu Shang Shen Di)", "无上神帝", "Ruo Hong Culture", listOf("Reinkarnasi", "Kultivasi", "Aksi"), "Muyun sang Kaisar Abadi terlahir kembali di tubuh guru rendahan, menaklukkan kembali sembilan surga.", 8.50, 320, 290, "Ongoing", 2020, false, "17.2M", null, null, "Ongoing", 0xFFEA580C, 0xFF7C2D12, "Senin", "10:00 WIB", true, "Episode 290 Muyun Sub Indo!"),
        d("peerless_martial_spirit", "Jiwa Bertarung Tanpa Tanding (Peerless Martial Spirit)", "绝世战魂", "Ruo Hong Culture", listOf("Kultivasi", "Aksi", "Fantasi"), "Qin Nan membangkitkan Jiwa Pertarungan Ilahi yang menghancurkan batasan takdir di Benua Canglan.", 8.45, 280, 250, "Ongoing", 2021, false, "13.5M", null, null, "Ongoing", 0xFF6366F1, 0xFF312E81, "Rabu", "10:00 WIB", true, "Episode 250 Qin Nan Sub Indo!"),
        d("spirit_sword_sovereign", "Penguasa Pedang Jiwa (Spirit Sword Sovereign / Ling Jian Zun)", "灵剑尊", "Ruo Hong Culture", listOf("Reinkarnasi", "Kultivasi", "Wuxia"), "Chu Xingyun kembali ke masa mudanya saat keluarganya belum hancur dengan Pedang Jiwa Kuno.", 8.40, 450, 420, "Ongoing", 2019, false, "21.0M", null, null, "Ongoing", 0xFF059669, 0xFF065F46, "Jumat", "10:00 WIB", true, "Episode 420 Pedang Jiwa Sub Indo!"),
        d("wan_jie_shen_zhu", "Penguasa Semesta Alam (Lord of the Universe / Wan Jie Shen Zhu)", "万界神主", "Ruo Hong Culture", listOf("Fantasi", "Kultivasi", "Aksi"), "Ye Chen sang Penguasa Kuno menyembunyikan identitasnya di tengah perselisihan dewa-dewa kuno.", 8.55, 340, 310, "Ongoing", 2019, false, "15.4M", null, null, "Ongoing", 0xFF9333EA, 0xFF581C87, "Sabtu", "10:00 WIB", true, "Episode 310 Ye Chen Sub Indo!"),
        d("wan_jie_xian_zong", "Dunia Seribu Keajaiban (The Wonderland of Ten Thousands)", "万界仙踪", "Ruo Hong Culture", listOf("Kultivasi", "Wuxia", "Misteri"), "Ye Xingyun penguasa Sekte Bintang menjelajahi dinasti kekaisaran untuk mengungkap konspirasi dewa.", 8.52, 360, 330, "Ongoing", 2018, false, "16.1M", null, null, "Ongoing", 0xFF2563EB, 0xFF1E3A8A, "Selasa", "10:00 WIB", true, "Episode 330 Sekte Bintang Sub Indo!"),
        d("tomb_of_fallen_gods", "Makam Para Dewa (Tomb of Fallen Gods / Shen Mu)", "神墓", "Wonder Cat Animation", listOf("Kultivasi", "Mitologi", "Aksi"), "Chen Nan bangkit dari makam para dewa dan iblis setelah tidur selama sepuluh ribu tahun.", 9.10, 36, 30, "Ongoing", 2022, false, "11.8M", null, null, "Hot", 0xFFDC2626, 0xFF450A0A, "Sabtu", "10:00 WIB", true, "Episode 30 Bangkitnya Makam Dewa Sub Indo!"),
        d("carp_reborn", "Kelahiran Kembali Ikan Mas (Carp Reborn / Yuan Long)", "元龙", "Bilibili Studio / CG Year", listOf("Isekai", "Sci-Fi", "Kultivasi", "Aksi"), "Wang Sheng, prajurit pasukan khusus modern bertransmigrasi ke dunia kultivasi dengan jiwa Roh Ikan Mas.", 9.15, 32, 32, "Tamat", 2020, false, "14.8M", null, null, "Tamat", 0xFF059669, 0xFF047857, "Minggu", "10:00 WIB", false, "Season 1-3 Lengkap Sub Indo"),
        d("legend_of_xianwu", "Legenda Kaisar Xianwu (Legend of Xianwu)", "仙武帝尊", "Ruo Hong Culture", listOf("Kultivasi", "Reinkarnasi", "Aksi"), "Ye Chen diusir dari sekte karena dantiannya hancur, namun memperoleh Api Sejati Kuno dan mata roda enam.", 8.70, 160, 140, "Ongoing", 2023, false, "12.3M", null, null, "Ongoing", 0xFFF59E0B, 0xFF78350F, "Senin", "10:00 WIB", true, "Episode 140 Ye Chen Xianwu Sub Indo!"),
        d("peerless_god_of_war", "Dewa Perang Tanpa Tanding (Peerless God of War)", "绝世武神", "Soyep Culture", listOf("Kultivasi", "Aksi", "Reinkarnasi"), "Lin Feng bereinkarnasi ke Benua Sembilan Surga dengan Jiwa Belati Kegelapan.", 8.42, 220, 200, "Ongoing", 2020, false, "13.9M", null, null, "Ongoing", 0xFF7C3AED, 0xFF3B0764, "Rabu", "10:00 WIB", true, "Episode 200 Lin Feng Sub Indo!"),
        d("alchemy_supreme", "Kaisar Alkimia Tertinggi (Alchemy Supreme)", "丹道至尊", "Ruo Hong Culture", listOf("Kultivasi", "Reinkarnasi", "Fantasi"), "Kaisar Alkimia Yun Ding Tian terlahir kembali menembus rekor pembuatan pil obat surgawi.", 8.35, 180, 160, "Ongoing", 2021, false, "10.1M", null, null, "Ongoing", 0xFF10B981, 0xFF064E3B, "Kamis", "10:00 WIB", true, "Episode 160 Pil Surgawi Sub Indo!"),
        d("ancient_myth", "Misteri Kuno Para Dewa (Ancient Myth)", "上古秘约", "Ruo Hong Culture", listOf("Mitologi", "Kultivasi", "Fantasi"), "Kisah pertempuran para dewa purba mempertahankan segel monster neraka di zaman purbakala.", 8.30, 120, 110, "Ongoing", 2022, false, "8.9M", null, null, "Ongoing", 0xFFDC2626, 0xFF450A0A, "Jumat", "10:00 WIB", true, "Episode 110 Rahasia Kuno Sub Indo!"),
        d("everlasting_god_of_sword", "Dewa Pedang Abadi (Everlasting God of Sword)", "万界独尊", "Ruo Hong Culture", listOf("Kultivasi", "Aksi", "Wuxia"), "Lin Jie melindungi warisan pedang kuno sembilan alam semesta dari pembantaian klan.", 8.38, 200, 180, "Ongoing", 2021, false, "11.4M", null, null, "Ongoing", 0xFF2563EB, 0xFF1E3A8A, "Sabtu", "10:00 WIB", true, "Episode 180 Pedang Abadi Sub Indo!"),
        d("great_doctor_miss_nine", "Tabib Agung Nona Sembilan (Great Doctor Miss Nine)", "神医九小姐", "Ruo Hong Culture", listOf("Reinkarnasi", "Fantasi", "Romantis"), "Dokter modern Jun Jiuling bertransmigrasi ke tubuh nona kesembilan klan bangsawan yang tertindas.", 8.75, 80, 70, "Ongoing", 2021, false, "9.5M", null, null, "Ongoing", 0xFFEC4899, 0xFF831843, "Minggu", "10:00 WIB", true, "Episode 70 Nona Sembilan Sub Indo!"),
        d("ten_thousand_worlds", "Dewa Sihir Sepuluh Ribu Alam (Ten Thousand Worlds)", "万界法神", "Ruo Hong Culture", listOf("Fantasi", "Sihir", "Reinkarnasi"), "Ye Xuan dewa sihir terakhir mengorbankan diri melintasi waktu untuk menyelamatkan peradaban manusia.", 8.48, 190, 175, "Ongoing", 2021, false, "12.1M", null, null, "Ongoing", 0xFF8B5CF6, 0xFF4C1D95, "Senin", "10:00 WIB", true, "Episode 175 Ye Xuan Sub Indo!"),

        // === SERIAL WUXIA KLASIK & AKSI EPIC ===
        d("hua_jiang_hu_buliangren", "Pendekar Sungai dan Danau: Bu Liang Ren (Bu Liang Ren 1-6)", "画江湖之不良人", "Rocen Digital", listOf("Wuxia", "Sejarah", "Aksi", "Misteri"), "Li Xingyun keturunan Dinasti Tang yang runtuh terseret perebutan takhta bersama organisasi rahasia Bu Liang Ren.", 9.88, 120, 120, "Tamat", 2014, false, "34.5M", null, null, "Masterpiece", 0xFFDC2626, 0xFF450A0A, "Kamis", "10:00 WIB", false, "Musim 1-6 Masterpiece Wuxia Sub Indo"),
        d("hua_jiang_hu_lingzhu", "Pendekar Sungai dan Danau: Penguasa Roh (Ling Zhu)", "画江湖之灵主", "Rocen Digital", listOf("Wuxia", "Misteri", "Aksi"), "Bai Liting dan Liang You mengungkap misteri arwah orang mati yang membalas dendam melalui perantara Lingzhu.", 9.50, 41, 41, "Tamat", 2015, false, "16.8M", null, null, "Classic", 0xFF7C3AED, 0xFF3B0764, "Selasa", "10:00 WIB", false, "Serial Lengkap 41 Episode Sub Indo"),
        d("hua_jiang_hu_beimoting", "Pendekar Sungai dan Danau: Cangkir Tak Berhenti (Bei Mo Ting)", "画江湖之杯莫停", "Rocen Digital", listOf("Wuxia", "Aksi", "Drama"), "Tiga keluarga besar persilatan Zong, Yu, dan Wanyan bertarung dalam aturan 'Bunuh dalam satu cangkir arak'.", 9.42, 40, 40, "Tamat", 2016, false, "14.3M", null, null, "Classic", 0xFF2563EB, 0xFF1E3A8A, "Rabu", "10:00 WIB", false, "Serial Lengkap Sub Indo"),
        d("hua_jiang_hu_guiye", "Pendekar Sungai dan Danau: Balapan Malam (Gui Ye Xing)", "画江湖之轨夜行", "Rocen Digital", listOf("Aksi", "Sci-Fi", "Balapan"), "Dong Guofa pemuda jalanan berpacu di dunia balap liar bawah tanah yang dipenuhi konspirasi sindikat.", 9.20, 40, 40, "Tamat", 2020, false, "11.5M", null, null, "Action", 0xFFEA580C, 0xFF7C2D12, "Jumat", "10:00 WIB", false, "Serial Balapan Lengkap Sub Indo"),
        d("an_he_zhuan", "Kisah Pembunuh Sungai Gelap (Legend of Assassin: Anhe)", "暗河传", "Build Dream", listOf("Wuxia", "Aksi", "Pembunuh"), "Kisah organisasi pembunuh tergelap Jianghu 'Sungai Kegelapan' dan Su Muyu sang Pembawa Payung.", 9.68, 26, 26, "Tamat", 2023, false, "16.2M", null, null, "Top Action", 0xFF18181B, 0xFF27272A, "Rabu", "10:00 WIB", false, "Koreografi Pedang Memukau Sub Indo"),
        d("shaonian_ge_xing", "Lagu Pemuda Pengelana (Great Journey of Teenagers / Shaonian Ge Xing)", "少年歌行", "Build Dream / Youku", listOf("Wuxia", "Aksi", "Petualangan"), "Lei Wujie, Xiao Se, dan biksu Wu Xin berkelana melintasi dunia persilatan Jianghu yang penuh intrik.", 9.64, 52, 52, "Tamat", 2018, false, "19.7M", null, null, "Top Wuxia", 0xFFDC2626, 0xFF450A0A, "Selasa", "10:00 WIB", false, "Musim 1 & 2 Lengkap Sub Indo"),
        d("junior_white_horse", "Masa Muda Mabuk Angin Musim Semi (Dashing Youth)", "少年白马醉春风", "Build Dream", listOf("Wuxia", "Aksi", "Petualangan"), "Prequel Shaonian Ge Xing. Baili Dongjun pemuda pembuat arak terbaik melangkah ke Jianghu dengan pedang.", 9.52, 26, 26, "Tamat", 2022, false, "13.4M", null, null, "Hot", 0xFF059669, 0xFF065F46, "Rabu", "10:00 WIB", false, "Kisah Baili Dongjun Lengkap Sub Indo"),
        d("rakshasa_street", "Jalan Rakshasa (Rakshasa Street / Zhen Hun Jie)", "镇魂街", "L2Studio / Bilibili", listOf("Aksi", "Supranatural", "Fantasi"), "Cao Yanbing penjaga Jalan Rakshasa bersama Roh Pelindung Jenderal Penjaga Tiga Kerajaan Cao Cao.", 9.58, 48, 48, "Tamat", 2016, false, "22.5M", null, null, "Hot", 0xFFDC2626, 0xFF450A0A, "Sabtu", "11:00 WIB", false, "Musim 1-3 Lengkap Sub Indo"),
        d("scissor_seven", "Gunting Sakti Tujuh (Scissor Seven / Wu Liuqi)", "刺客伍六七", "AHA Entertainment", listOf("Komedi", "Aksi", "Wuxia"), "Seven sang pembunuh bayaran amnesia dengan gunting sakti yang membuka kedai potong rambut di Pulau Ayam.", 9.80, 40, 40, "Tamat", 2018, false, "30.1M", null, null, "Masterpiece", 0xFF059669, 0xFF064E3B, "Rabu", "12:00 WIB", false, "Musim 1-4 Lengkap Sub Indo"),
        d("kings_avatar", "Avatar Sang Raja Game (The King's Avatar / Quan Zhi Gao Shou)", "全职高手", "B.CMAY / Colored-Pencil", listOf("eSports", "Aksi", "Gaming"), "Ye Xiu master legendaris game Glory dikeluarkan dari tim dan bangkit kembali dari warnet Happy Net Cafe.", 9.68, 36, 36, "Tamat", 2017, false, "28.9M", null, null, "Epic", 0xFFEA580C, 0xFF7C2D12, "Jumat", "12:00 WIB", false, "Musim 1-2 & OVA Lengkap Sub Indo"),
        d("hitori_no_shita", "Manusia Luar Biasa (The Outcast / Yi Ren Zhi Xia)", "一人之下", "Haoliners / Tencent", listOf("Aksi", "Wuxia", "Misteri"), "Zhang Chulan menyembunyikan jurus rahasia 'Qi Origin' sampai ia bertemu Feng Baobao yang abadi.", 9.62, 54, 54, "Tamat", 2016, false, "26.4M", null, null, "Hot", 0xFF2563EB, 0xFF1E3A8A, "Kamis", "11:00 WIB", false, "Musim 1-5 Lengkap Sub Indo"),
        d("spare_me_great_lord", "Ampuni Aku, Yang Mulia! (Spare Me, Great Lord!)", "大王饶命", "Big Firebird / Tencent", listOf("Komedi", "Kultivasi", "Aksi"), "Lu Shu memperoleh sistem pengumpul poin kejengkelan orang lain demi membeli buah kekuatan surgawi.", 9.50, 24, 24, "Tamat", 2021, false, "21.3M", null, null, "Popular", 0xFF06B6D4, 0xFF164E63, "Sabtu", "10:00 WIB", true, "Animasi Viral Lengkap Sub Indo"),
        d("dragon_raja", "Klan Raja Naga (Dragon Raja / Long Zu)", "龙族", "Garden Culture / Tencent", listOf("Fantasi", "Aksi", "Sci-Fi"), "Lu Mingfei remaja biasa menerima undangan rahasia ke Cassell College untuk memburu klan naga purba.", 9.42, 16, 16, "Tamat", 2022, false, "15.2M", null, null, "Trending", 0xFF6366F1, 0xFF312E81, "Jumat", "10:00 WIB", false, "Musim 1 Lengkap 4K Sub Indo"),
        d("ling_cage", "Sangkar Jiwa: Reinkarnasi (Ling Cage: Incarnation)", "灵笼", "YHKT Entertainment", listOf("Sci-Fi", "Aksi", "Post-Apocalyptic"), "Umat manusia bertahan hidup di mercusuar terbang setelah monster pemakan jiwa menguasai permukaan bumi.", 9.84, 16, 16, "Tamat", 2019, false, "24.1M", null, null, "Masterpiece", 0xFFD97706, 0xFF78350F, "Sabtu", "10:00 WIB", false, "Sci-Fi CGI Nomor 1 Sub Indo"),
        d("full_time_magister", "Penyihir Serba Bisa (Full-Time Magister / Quan Zhi Fa Shi)", "全职法师", "Shanghai Foch Film", listOf("Sihir", "Aksi", "Sekolah"), "Mo Fan bangun di dunia di mana sains digantikan sihir. Ia membangkitkan elemen ganda Petir dan Api.", 9.18, 72, 72, "Tamat", 2016, false, "23.6M", null, null, "Popular", 0xFF7C3AED, 0xFF3B0764, "Minggu", "10:00 WIB", false, "Musim 1-6 Lengkap Sub Indo"),
        d("first_order", "Urutan Pertama (The First Order / Di Yi Xu Lie)", "第一序列", "Bilibili / Sparkly Key", listOf("Sci-Fi", "Aksi", "Komedi"), "Ren Xiaosu bertahan hidup di era pasca-apokaliptik dengan otak cerdas dan sistem misterius.", 9.32, 16, 16, "Tamat", 2023, false, "11.6M", null, null, "New", 0xFFEA580C, 0xFF7C2D12, "Jumat", "10:00 WIB", false, "Musim 1 Lengkap Sub Indo"),
        d("white_cat_legend", "Buku Harian Kucing Putih (White Cat Legend)", "大理寺日志", "B.CMAY PICTURES", listOf("Misteri", "Komedi", "Sejarah"), "Kucing putih Li Bing diangkat menjadi wakil menteri Pengadilan Dali di era Dinasti Tang untuk memecahkan kasus.", 9.55, 24, 24, "Tamat", 2020, false, "14.0M", null, null, "Top Rated", 0xFFF59E0B, 0xFF78350F, "Selasa", "11:00 WIB", false, "Musim 1 & 2 Lengkap Sub Indo"),
        d("fairies_album", "Buku Catatan Siluman (Fairies Album / Bai Yao Pu)", "百妖谱", "Haoliners Animation", listOf("Fantasi", "Supranatural", "Drama"), "Tao Yao sang tabib roh mengobati penyakit hati para siluman dan manusia di sepanjang perjalanannya.", 9.48, 36, 36, "Tamat", 2020, false, "16.2M", null, null, "Masterpiece", 0xFF059669, 0xFF064E3B, "Rabu", "11:00 WIB", false, "Musim 1-3 Lengkap Sub Indo"),
        d("cinderella_chef", "Koki Modern Cinderella (Cinderella Chef / Meng Qi Shi Shen)", "萌妻食神", "Wulifang / Bilibili", listOf("Isekai", "Romantis", "Kuliner"), "Ye Jiayao chef modern terlempar ke zaman kuno dan menjadi istri kepala bandit yang tampan.", 9.20, 36, 36, "Tamat", 2018, false, "15.8M", null, null, "Popular", 0xFFEC4899, 0xFF831843, "Sabtu", "11:00 WIB", false, "Musim 1-3 Lengkap Sub Indo"),
        d("no_doubt_in_us", "Jiwa Tertukar Kaisar dan Ratu (No Doubt In Us / Liang Bu Yi)", "两不疑", "Paper Plane Animation", listOf("Romantis", "Komedi", "Sejarah"), "Kaisar Xiao Jinyun dan Ratu Xu Yu bertukar tubuh dan harus saling memahami peran masing-masing.", 9.45, 48, 48, "Tamat", 2021, false, "17.4M", null, null, "Popular", 0xFFE11D48, 0xFF881337, "Kamis", "11:00 WIB", false, "Musim 1 & 2 Lengkap Sub Indo"),

        // === SERIAL POPULER LAINNYA ===
        d("fox_spirit_matchmaker", "Mak Comblang Peri Rubah (Fox Spirit Matchmaker)", "狐妖小红娘", "Haoliners Animation", listOf("Romantis", "Fantasi", "Komedi"), "Tushan Susu peri rubah kecil dan Bai Yuechu membantu reinkarnasi cinta antara manusia dan siluman.", 9.68, 140, 140, "Tamat", 2015, false, "32.0M", null, null, "Classic", 0xFFEC4899, 0xFF831843, "Sabtu", "10:00 WIB", false, "Semua Arc Lengkap Sub Indo"),
        d("du_bu_xiao_yao", "Satu Langkah Menuju Kebebasan (One Step Toward Freedom)", "独步逍遥", "Soyep Culture", listOf("Kultivasi", "Reinkarnasi", "Aksi"), "Ye Yu terlahir kembali melangkah bebas di antara para dewa tanpa batasan aturan surga.", 8.45, 380, 350, "Ongoing", 2020, false, "19.5M", null, null, "Ongoing", 0xFF2563EB, 0xFF1E3A8A, "Senin", "10:00 WIB", true, "Episode 350 Ye Yu Sub Indo!"),
        d("zhen_wu_dianfeng", "Puncak Bela Diri Sejati (The Peak of True Martial Arts)", "真武巅峰", "Ruo Hong Culture", listOf("Kultivasi", "Aksi", "Fantasi"), "Nie Feng membangkitkan garis keturunan Kaisar Bela Diri Sejati menembus puncak tertinggi bela diri.", 8.52, 160, 140, "Ongoing", 2021, false, "12.8M", null, null, "Ongoing", 0xFFEA580C, 0xFF7C2D12, "Selasa", "10:00 WIB", true, "Episode 140 Nie Feng Sub Indo!"),
        d("ni_tian_zhun_zun", "Pemberontakan Melawan Dewa (Rebel of the Gods)", "逆天至尊", "Ruo Hong Culture", listOf("Kultivasi", "Reinkarnasi", "Balas Dendam"), "Tan Yun dibantai oleh musuh-musuhnya dan terlahir kembali membantai balik semua pengkhianat.", 8.40, 240, 220, "Ongoing", 2021, false, "15.7M", null, null, "Ongoing", 0xFFDC2626, 0xFF450A0A, "Rabu", "10:00 WIB", true, "Episode 220 Tan Yun Sub Indo!"),
        d("ze_tian_ji", "Pilihan Takdir (Fighter of the Destiny / Ze Tian Ji)", "择天记", "Shanghai Foch Film", listOf("Kultivasi", "Fantasi", "Wuxia"), "Chen Changsheng yang berumur pendek meninggalkan kuil membawa gulungan kitab untuk mengubah takdir kematiannya.", 9.30, 60, 60, "Tamat", 2015, false, "18.3M", null, null, "Tamat", 0xFF059669, 0xFF064E3B, "Kamis", "10:00 WIB", false, "Musim 1-5 Lengkap Sub Indo"),
        d("kuang_shen", "Dewa Kegilaan (Mad God / Kuang Shen)", "狂神", "Sparkly Key / Tencent", listOf("Kultivasi", "Fantasi", "Aksi"), "Lei Xiang blasteran manusia, iblis, dan binatang buas membangkitkan kekuatan Dewa Kegilaan terlarang.", 9.15, 30, 30, "Tamat", 2022, false, "10.4M", null, null, "Tamat", 0xFF7C3AED, 0xFF3B0764, "Jumat", "10:00 WIB", false, "Serial Lengkap Sub Indo"),
        d("transcendent_heroes", "Pasukan Pahlawan Transenden (Transcendent Heroes)", "雄兵连", "Chao Shen Xue Yuan", listOf("Sci-Fi", "Aksi", "Superhero"), "Pasukan Black Troop manusia super mempertahankan bumi dari invasi dewa alien dan armada iblis galaksi.", 9.55, 60, 60, "Tamat", 2017, false, "21.5M", null, null, "Sci-Fi Top", 0xFF2563EB, 0xFF1E3A8A, "Sabtu", "10:00 WIB", false, "Musim 1-3 Lengkap Sub Indo"),
        d("spiritual_field", "Alam Roh Pertarungan (Realm of Spirits / Ling Yu)", "灵域", "Shanghai Foch Film", listOf("Kultivasi", "Aksi", "Fantasi"), "Qin Lie pemuda amnesia membangkitkan Darah Dewa Bertarung di Benua Roh Merah.", 9.25, 60, 60, "Tamat", 2015, false, "16.1M", null, null, "Tamat", 0xFF6366F1, 0xFF312E81, "Minggu", "10:00 WIB", false, "Musim 1-6 Lengkap Sub Indo"),
        d("nine_songs_of_moving_heavens", "Sembilan Lagu Langit (Nine Songs of the Moving Heavens)", "天行九歌", "Sparkly Key (Xuanji)", listOf("Wuxia", "Sejarah", "Misteri"), "Han Fei mendirikan organisasi 'Quicksand' bersama Wei Zhuang untuk mempertahankan Kerajaan Han.", 9.72, 90, 90, "Tamat", 2016, false, "25.0M", null, null, "Masterpiece", 0xFFD97706, 0xFF78350F, "Rabu", "10:00 WIB", false, "Karya Legendaris Xuanji Sub Indo"),
        d("the_legend_of_qin", "Legenda Dinasti Qin (The Legend of Qin)", "秦时明月", "Sparkly Key (Xuanji)", listOf("Wuxia", "Sejarah", "Aksi"), "Jing Tianming membawa pedang pusaka melintasi era Dinasti Qin bersama para pendekar Mohist.", 9.78, 175, 175, "Tamat", 2007, false, "38.2M", null, null, "Classic King", 0xFFDC2626, 0xFF450A0A, "Kamis", "10:00 WIB", false, "Pondasi Animasi Donghua 3D Sub Indo"),
        d("legend_of_assassins", "Nyanyian Pedang Pembalas Dendam (Legend of Assassins)", "枕刀歌", "ZhenDao Animation", listOf("Wuxia", "Aksi", "Balas Dendam"), "He Shuangxi menuntut balas dendam kematian keluarganya dengan ilmu golok mematikan melintasi sungai Jiangnan.", 9.68, 14, 14, "Tamat", 2021, false, "12.7M", null, null, "Top Action", 0xFF18181B, 0xFF27272A, "Jumat", "10:00 WIB", false, "Koreografi Wuxia Terbaik Sub Indo"),
        d("biao_ren", "Pengawal Gurun Pasir (Blades of the Guardians / Biao Ren)", "镖人", "Colored-Pencil / Tencent", listOf("Wuxia", "Aksi", "Sejarah"), "Dao Ma sang pengawal bayaran mengawal biksu misterius melintasi gurun pasir di penghujung Dinasti Sui.", 9.65, 14, 14, "Tamat", 2023, false, "15.3M", null, null, "Top Wuxia", 0xFFB45309, 0xFF78350F, "Sabtu", "10:00 WIB", false, "Koreografi Brutal HD Sub Indo"),
        d("bident_of_the_dragon", "Cinta dan Kutukan Bunga Anggrek (Love Between Fairy and Devil)", "苍兰诀", "HengGao Animation / iQIYI", listOf("Fantasi", "Romantis", "Kultivasi"), "Raja Iblis Dongfang Qingcang bertukar jiwa dengan peri anggrek kecil Xiao Lanhua di Menara Haotian.", 9.40, 24, 24, "Tamat", 2022, false, "16.8M", null, null, "Popular", 0xFF9333EA, 0xFF581C87, "Minggu", "10:00 WIB", false, "Musim 1 Lengkap Sub Indo"),
        d("cultivation_chat_group", "Grup Obrolan Kultivator (Cultivation Chat Group)", "修真聊天群", "Bilibili Studio", listOf("Komedi", "Kultivasi", "Urban"), "Song Shuhang tidak sengaja masuk ke grup chat para kultivator senior yang dikiranya komunitas cosplayer.", 9.25, 24, 24, "Tamat", 2022, false, "12.8M", null, null, "Top Comedy", 0xFF06B6D4, 0xFF164E63, "Senin", "10:00 WIB", false, "Grup Chat Kultivasi Sub Indo!"),
        d("knights_on_debris", "Ksatria Puing Bintang (Knights on Debris)", "星骸骑士", "Sparkly Key / Tencent", listOf("Sci-Fi", "Aksi", "Mecha"), "Chen Tianji terdampar di planet reruntuhan alien dan membangkitkan zirah ksatria galaksi.", 9.35, 28, 28, "Tamat", 2020, false, "10.9M", null, null, "Sci-Fi Top", 0xFF2563EB, 0xFF1E3A8A, "Jumat", "10:00 WIB", false, "Musim 1 & 2 Lengkap Sub Indo"),
        d("the_silver_guardian", "Penjaga Makam Perak (The Silver Guardian)", "银之守墓人", "Haoliners Animation", listOf("Gaming", "Aksi", "Fantasi"), "Lu Shuiyin siswa miskin adalah pemain game terbaik yang bertarung di Makam Para Dewa virtual.", 8.80, 24, 24, "Tamat", 2017, false, "11.5M", null, null, "Tamat", 0xFF6366F1, 0xFF312E81, "Sabtu", "10:00 WIB", false, "Musim 1 & 2 Lengkap Sub Indo"),
        d("word_of_honor_donghua", "Pengembara Ujung Dunia (Word of Honor / Tian Ya Ke)", "天涯客", "Tencent Penguin Pictures", listOf("Wuxia", "Misteri", "Aksi"), "Zhou Zishu mantan pemimpin organisasi rahasia kekaisaran mengembara Jianghu bersama Wen Kexing.", 9.30, 20, 20, "Tamat", 2022, false, "10.7M", null, null, "Wuxia", 0xFF059669, 0xFF065F46, "Minggu", "10:00 WIB", false, "Serial Lengkap Sub Indo"),
        d("legend_of_sword_fairy", "Legenda Pedang dan Peri 3D (The Legend of Sword and Fairy)", "仙剑奇侠传", "Sparkly Key (Xuanji)", listOf("Wuxia", "Romantis", "Fantasi"), "Li Xiaoyao pemuda penginapan bertemu putri Zhao Ling'er dan berpetualang menaklukkan Sekte Bulan.", 9.60, 26, 26, "Tamat", 2024, false, "18.6M", null, null, "Remake 3D", 0xFFD97706, 0xFF78350F, "Selasa", "10:00 WIB", false, "Remake CGI 3D Lengkap Sub Indo"),
        d("the_defective", "Manusia Cacat Bintang (The Defective / Can Ci Pin)", "残次品", "Bilibili / LX Animation", listOf("Sci-Fi", "Aksi", "Misteri"), "Jenderal Lu Bixing dan Lin Jingheng memimpin distrik kedelapan melawan aliansi tirani bintang.", 9.10, 16, 16, "Tamat", 2021, false, "8.9M", null, null, "Sci-Fi", 0xFF4F46E5, 0xFF1E1B4B, "Kamis", "10:00 WIB", false, "Serial Lengkap Sub Indo"),
        d("drowning_sorrows", "Tenggelam Dalam Kobaran Api (Drowning Sorrows in Raging Fire)", "烈火浇愁", "Shengying Animation", listOf("Fantasi", "Misteri", "Aksi"), "Xuan Ji direkrut ke Biro Pengendalian Anomali dan membangkitkan kaisar kuno Sheng Lingyuan dari jurang api.", 9.35, 12, 12, "Tamat", 2021, false, "11.2M", null, null, "Trending", 0xFFEA580C, 0xFF7C2D12, "Sabtu", "10:00 WIB", false, "Musim 1 Lengkap Sub Indo"),
        d("god_of_ten_thousand_realms", "Dewa Sepuluh Ribu Alam (God of Ten Thousand Realms)", "万界至尊", "Ruo Hong Culture", listOf("Kultivasi", "Aksi", "Fantasi"), "Chu Yun sang penerus takhta dewa bintang merebut kembali tahta leluhur dari tangan pengkhianat.", 8.40, 160, 140, "Ongoing", 2022, false, "9.8M", null, null, "Ongoing", 0xFF7C3AED, 0xFF4C1D95, "Senin", "10:00 WIB", true, "Episode 140 Chu Yun Sub Indo!"),
        d("peerless_dan_god", "Dewa Pil Tiada Tanding (Peerless Dan God)", "绝世丹神", "Ruo Hong Culture", listOf("Kultivasi", "Reinkarnasi", "Fantasi"), "Qin Feng meracik pil ilahi pembalik kematian demi membangkitkan kembali kekasihnya.", 8.35, 140, 120, "Ongoing", 2022, false, "8.6M", null, null, "Ongoing", 0xFF059669, 0xFF065F46, "Selasa", "10:00 WIB", true, "Episode 120 Qin Feng Sub Indo!"),
        d("i_am_great_god", "Aku Adalah Dewa Agung (I Am a Great God)", "我是大神仙", "Zhonghe Animation / Tencent", listOf("Isekai", "Kultivasi", "Gaming"), "Shi Jiang bocah jenius bertransmigrasi ke alam kultivasi dan mengelola sekte layaknya perusahaan modern.", 9.05, 32, 32, "Tamat", 2020, false, "11.9M", null, null, "Popular", 0xFF06B6D4, 0xFF164E63, "Rabu", "10:00 WIB", false, "Musim 1 & 2 Lengkap Sub Indo"),
        d("dragon_disciple", "Murid Dewa Naga (Dragon's Disciple)", "龙神之徒", "Soyep Culture", listOf("Kultivasi", "Aksi", "Fantasi"), "Pewaris takhta Naga Biru menaklukkan sepuluh klan siluman liar di Benua Shenzhou.", 8.45, 100, 90, "Ongoing", 2023, false, "7.9M", null, null, "Ongoing", 0xFF2563EB, 0xFF1E3A8A, "Kamis", "10:00 WIB", true, "Episode 90 Dewa Naga Sub Indo!"),
        d("god_of_martial_arts", "Penguasa Jalan Bela Diri (God of Martial Arts)", "武道独尊", "Ruo Hong Culture", listOf("Kultivasi", "Aksi", "Wuxia"), "Ye Ming memulihkan meridian yang hancur berkat cincin dewa perang kuno.", 8.35, 150, 130, "Ongoing", 2022, false, "9.1M", null, null, "Ongoing", 0xFFEA580C, 0xFF7C2D12, "Jumat", "10:00 WIB", true, "Episode 130 Ye Ming Sub Indo!"),
        d("legend_of_luoxiaohei", "Kisah Kucing Hitam Xiaohei (The Legend of Luo Xiaohei)", "罗小黑战记", "HMNetwork", listOf("Fantasi", "Komedi", "Supranatural"), "Kucing siluman Xiaohei kehilangan rumahnya di hutan dan menemukan persahabatan di dunia manusia.", 9.88, 40, 40, "Tamat", 2011, false, "27.5M", null, null, "Masterpiece", 0xFF10B981, 0xFF064E3B, "Sabtu", "10:00 WIB", false, "Karya Masterpiece 2D Sub Indo"),

        // === FILM DONGHUA BIOSKOP & LAYAR LEBAR (BAHASA INDONESIA & SUB INDO) ===
        d("nezha_movie", "Kelahiran Sang Anak Iblis (Nezha: Birth of the Demon Child)", "哪吒之魔童降世", "Light Chaser / Coco Cartoon", listOf("Film Donghua", "Fantasi", "Aksi", "Mitologi"), "Lahir dari Mutiara Iblis, Nezha ditakdirkan membawa kehancuran dan disambar petir surga pada ulang tahunnya yang ke-3. 'Takdirku ditentukan olehku, bukan oleh surga!'", 9.85, 1, 1, "Tamat", 2019, true, "42.0M", R.drawable.poster_nezha, null, "Film", 0xFFDC2626, 0xFF991B1B, "Senin", "12:00 WIB", true, "Film Animasi Terlaris Sepanjang Masa Sub Indo", 110),
        d("white_snake", "Siluman Ular Putih (White Snake 1: Asal Usul)", "白蛇：缘起", "Light Chaser Animation", listOf("Film Donghua", "Romantis", "Fantasi", "Wuxia"), "Kisah 500 tahun sebelum Legenda Siluman Ular Putih. Bai Suzhen yang kehilangan ingatan bertemu Ah Xuan sang penangkap ular.", 9.45, 1, 1, "Tamat", 2019, true, "18.5M", null, null, "Film", 0xFFEC4899, 0xFF831843, "Selasa", "12:00 WIB", true, "Kisah Romansa Ular Putih Sub Indo", 99),
        d("green_snake", "Siluman Ular Hijau: Bencana Asuraville (Green Snake / White Snake 2)", "白蛇2：青蛇劫起", "Light Chaser Animation", listOf("Film Donghua", "Aksi", "Fantasi", "Sci-Fi"), "Xiao Qing terseret ke Kota Asuraville yang futuristik dan penuh bahaya setelah kakaknya dipenjara di bawah Pagoda Leifeng.", 9.25, 1, 1, "Tamat", 2021, true, "14.2M", null, null, "Film", 0xFF059669, 0xFF064E3B, "Rabu", "12:00 WIB", true, "Petualangan Xiao Qing di Asuraville Sub Indo", 120),
        d("white_snake_afloat", "Siluman Ular Putih 3: Kehidupan Terapung (White Snake 3: Afloat 2024)", "白蛇：浮生", "Light Chaser Animation", listOf("Film Donghua", "Romantis", "Fantasi", "Wuxia"), "Kelanjutan kisah cinta abadi Bai Suzhen dan Xu Xian di tepi Danau Barat Hangzhou.", 9.50, 1, 1, "Tamat", 2024, true, "23.4M", null, null, "Film", 0xFFEC4899, 0xFF831843, "Rabu", "12:00 WIB", true, "Rilis Layar Lebar 2024 Sub Indo", 133),
        d("jiang_ziya", "Legenda Dewa Jiang Ziya (Jiang Ziya: Legend of Deification)", "姜子牙", "Light Chaser Animation", listOf("Film Donghua", "Fantasi", "Aksi", "Mitologi"), "Komandan Jiang Ziya memimpin pasukan menumbangkan Siluman Rubah Sembilan Ekor, namun menemukan kebenaran kelam para dewa surga.", 8.90, 1, 1, "Tamat", 2020, true, "16.8M", null, null, "Film", 0xFFF59E0B, 0xFF78350F, "Kamis", "12:00 WIB", true, "Kisah Epik Grandmaster Jiang Ziya Sub Indo", 110),
        d("big_fish", "Ikan Raksasa dan Begonia (Big Fish & Begonia)", "大鱼海棠", "B&T Studio / Enlight Pictures", listOf("Film Donghua", "Fantasi", "Romantis", "Drama"), "Chun gadis dari dunia mistis di bawah samudra berubah menjadi lumba-lumba merah dan diselamatkan oleh manusia.", 9.35, 1, 1, "Tamat", 2016, true, "22.1M", null, null, "Film", 0xFF0284C7, 0xFF0C4A6E, "Jumat", "12:00 WIB", true, "Mahakarya Dongeng Animasi Klasik Sub Indo", 105),
        d("new_gods_nezha_reborn", "Dewa Baru: Kelahiran Kembali Nezha (New Gods: Nezha Reborn)", "新神榜：哪吒重生", "Light Chaser Animation", listOf("Film Donghua", "Sci-Fi", "Aksi", "Cyberpunk"), "3000 tahun setelah perang dewa, Nezha bereinkarnasi sebagai Li Yunxiang pemuda pembalap motor di kota cyberpunk Donghai.", 9.15, 1, 1, "Tamat", 2021, true, "15.6M", null, null, "Film", 0xFFDC2626, 0xFF7F1D1D, "Sabtu", "12:00 WIB", true, "Nezha Era Cyberpunk 4K Sub Indo", 116),
        d("new_gods_yang_jian", "Dewa Baru: Yang Jian Dewa Mata Tiga (New Gods: Yang Jian)", "新神榜：杨戬", "Light Chaser Animation", listOf("Film Donghua", "Fantasi", "Aksi", "Mitologi"), "Dewa Erlang Shen Yang Jian kini hidup sebagai pemburu hadiah miskin dan memburu keponakannya Chenxiang demi Lentera Teratai.", 9.40, 1, 1, "Tamat", 2022, true, "19.8M", null, null, "Film", 0xFF2563EB, 0xFF1E3A8A, "Minggu", "12:00 WIB", true, "Visual CGI Paling Mewah 4K Sub Indo", 127),
        d("deep_sea", "Laut Dalam Misterius (Deep Sea / Shen Hai)", "深海", "October Media / Coloroom", listOf("Film Donghua", "Fantasi", "Drama", "Seni"), "Shenxiu terlempar ke kedalam samudra mimpi yang penuh warna partikel lukisan air 3D dan restoran terapung Nanhe.", 9.60, 1, 1, "Tamat", 2023, true, "17.4M", null, null, "Film", 0xFF06B6D4, 0xFF083344, "Senin", "12:00 WIB", true, "Animasi Partikel 3D Air Pertama Sub Indo", 112),
        d("monkey_king_hero_is_back", "Kembalinya Sang Raja Kera (Monkey King: Hero Is Back)", "西游记之大圣归来", "October Animation", listOf("Film Donghua", "Aksi", "Fantasi", "Mitologi"), "Sun Wukong disegel di bawah Gunung Lima Jari selama 500 tahun sebelum dibebaskan oleh biksu kecil Jiang Liuer.", 9.30, 1, 1, "Tamat", 2015, true, "26.3M", null, null, "Film", 0xFFDC2626, 0xFF7C2D12, "Selasa", "12:00 WIB", true, "Kebangkitan Raja Kera Sun Wukong Sub Indo", 89),
        d("changan_30000_miles", "Chang'an Tiga Puluh Ribu Mil (Chang'an: 30,000 Miles)", "长安三万里", "Light Chaser Animation", listOf("Film Donghua", "Sejarah", "Drama", "Puisi"), "Kisah persahabatan seumur hidup antara penyair legendaris Li Bai dan Jenderal Gao Shi di era keemasan Dinasti Tang.", 9.75, 1, 1, "Tamat", 2023, true, "28.9M", null, null, "Film", 0xFFD97706, 0xFF78350F, "Rabu", "12:00 WIB", true, "Mahakarya Puisi Dinasti Tang Sub Indo", 168),
        d("wind_guardians", "Mantra Angin Pelindung (The Wind Guardians / Feng Yu Zhou)", "风语咒", "Rocen Digital", listOf("Film Donghua", "Wuxia", "Aksi", "Fantasi"), "Lang Ming, pemuda buta yang menguasai kekuatan angin mistis terlarang untuk menyegel monster purba Taotie.", 9.20, 1, 1, "Tamat", 2018, true, "12.6M", null, null, "Film", 0xFF059669, 0xFF064E3B, "Kamis", "12:00 WIB", true, "Film Wuxia Bu Liang Ren Universe Sub Indo", 105),
        d("i_am_what_i_am", "Aku Adalah Singa Juara (I Am What I Am / Xiong Shi Shao Nian)", "雄狮少年", "Sun Hai Peng Animation", listOf("Film Donghua", "Komedi", "Drama", "Bela Diri"), "Tiga remaja desa berlatih keras menguasai seni tarian barongsai tradisional demi meraih impian di Kejuaraan Guangzhou.", 9.65, 1, 1, "Tamat", 2021, true, "16.1M", null, null, "Film", 0xFFEA580C, 0xFF7C2D12, "Jumat", "12:00 WIB", true, "Juara Animasi Realistis Sub Indo", 104),
        d("realm_of_terracotta", "Kota Prajurit Terakota (Realm of Terracotta)", "俑之城", "Dingliang Cartoon", listOf("Film Donghua", "Fantasi", "Aksi", "Petualangan"), "Meng Yuan prajurit terakota rendahan di makam kaisar Qin bertualang memburu monster Kuning Digu bersama gadis misterius Jade.", 9.10, 1, 1, "Tamat", 2021, true, "10.8M", null, null, "Film", 0xFFB45309, 0xFF78350F, "Sabtu", "12:00 WIB", true, "Petualangan Prajurit Terakota Sub Indo", 111),
        d("hei_movie", "Kisah Kucing Xiaohei: Layar Lebar (The Legend of Hei Movie)", "罗小黑战记大电影", "HMNetwork", listOf("Film Donghua", "Fantasi", "Aksi", "Petualangan"), "Kisah perjalanan kucing siluman Xiaohei bersama Fengxi dan manusia kuat Wugen mencari tempat tinggal sejati.", 9.70, 1, 1, "Tamat", 2019, true, "21.5M", null, null, "Film", 0xFF10B981, 0xFF064E3B, "Minggu", "12:00 WIB", true, "Film Bioskop Animasi 2D Terbaik Sub Indo", 101),
        d("kuiba_movie", "Petualangan Kuiba di Alam Bawah (Kuiba)", "魁拔之十万火急", "Vasoon Animation", listOf("Film Donghua", "Fantasi", "Aksi", "Petualangan"), "Man Ji bocah pembawa benih monster sakti Kuiba berlayar bersama kakek Man Xiao menuju medan perang pahlawan Yuan Shen.", 9.25, 1, 1, "Tamat", 2011, true, "13.4M", null, null, "Film", 0xFF2563EB, 0xFF1E3A8A, "Senin", "12:00 WIB", true, "Trilogi Epik Kuiba Sub Indo", 88),
        d("master_ji_gong", "Biksu Gila Sakti Ji Gong (Master Ji Gong / Crazy Monk)", "济公之降龙降世", "Zhonghe Animation", listOf("Film Donghua", "Fantasi", "Komedi", "Mitologi"), "Li Xiuyuan bereinkarnasi sebagai Arhat Naga Penakluk untuk menyelamatkan desa dari iblis burung merak emas.", 8.95, 1, 1, "Tamat", 2021, true, "9.8M", null, null, "Film", 0xFFF59E0B, 0xFF78350F, "Selasa", "12:00 WIB", true, "Kisah Jenaka dan Heroik Biksu Ji Gong Sub Indo", 95),
        d("crystal_sky_of_yesterday", "Langit Kristal Masa Lalu (Crystal Sky of Yesterday)", "昨日青空", "Coloroom Pictures", listOf("Film Donghua", "Romantis", "Sekolah", "Nostalgia"), "Kisah manis dan mengharukan tentang masa SMA, persahabatan, dan cinta pertama di kota kecil Nanfang era 1990-an.", 9.05, 1, 1, "Tamat", 2018, true, "11.7M", null, null, "Film", 0xFF06B6D4, 0xFF083344, "Rabu", "12:00 WIB", true, "Animasi Nostalgia Romantis Masa SMA Sub Indo", 82),
        d("the_guardian_movie", "Sang Hakim Pelindung (The Guardian / Da Hu Fa)", "大护法", "Coloroom Pictures", listOf("Film Donghua", "Misteri", "Aksi", "Fantasi"), "Pengawal bertubuh bulat merah dengan seni bela diri tinggi mencari pangeran yang kabur ke desa kacang yang aneh.", 9.15, 1, 1, "Tamat", 2017, true, "12.1M", null, null, "Film", 0xFFDC2626, 0xFF7F1D1D, "Kamis", "12:00 WIB", true, "Animasi Misteri Dewasa Penuh Filosofi Sub Indo", 95)
    )
}
