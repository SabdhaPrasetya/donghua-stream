package com.example

import com.example.data.model.AuthMode
import com.example.data.model.UserAccount
import com.example.data.model.VideoQuality
import com.example.data.repository.DonghuaCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DonghuaAppTest {

    @Test
    fun testVipPlanPricingMatchesRequirements() {
        val plans = DonghuaCatalog.VIP_PLANS
        val weekPlan = plans.find { it.durationDays == 7 }
        val twoWeeksPlan = plans.find { it.durationDays == 14 }
        val monthPlan = plans.find { it.durationDays == 30 }

        assertNotNull("7-day plan should exist", weekPlan)
        assertNotNull("14-day plan should exist", twoWeeksPlan)
        assertNotNull("30-day plan should exist", monthPlan)

        // Requirement: 7 hari 5 ribu, 14 hari 10 ribu, 30 hari 20 ribu
        assertEquals(7, weekPlan!!.durationDays)
        assertEquals(5_000, weekPlan.priceRupiah)
        assertEquals("Rp 5.000", weekPlan.priceFormatted)

        assertEquals(14, twoWeeksPlan!!.durationDays)
        assertEquals(10_000, twoWeeksPlan.priceRupiah)
        assertEquals("Rp 10.000", twoWeeksPlan.priceFormatted)

        assertEquals(30, monthPlan!!.durationDays)
        assertEquals(20_000, monthPlan.priceRupiah)
        assertEquals("Rp 20.000", monthPlan.priceFormatted)
    }

    @Test
    fun testCatalogHasAllUploadDaysAndStudios() {
        val catalog = DonghuaCatalog.ALL_DONGHUA
        assertTrue("Catalog should have items", catalog.isNotEmpty())

        val days = catalog.map { it.uploadDay }.toSet()
        assertTrue("Contains Senin", days.contains("Senin"))
        assertTrue("Contains Selasa", days.contains("Selasa"))
        assertTrue("Contains Rabu", days.contains("Rabu"))
        assertTrue("Contains Kamis", days.contains("Kamis"))
        assertTrue("Contains Jumat", days.contains("Jumat"))
        assertTrue("Contains Sabtu", days.contains("Sabtu"))
        assertTrue("Contains Minggu", days.contains("Minggu"))

        val studios = catalog.map { it.studio }.toSet()
        assertTrue("Contains Sparkly Key", studios.any { it.contains("Sparkly Key", ignoreCase = true) })
        assertTrue("Contains Shanghai Foch", studios.any { it.contains("Foch", ignoreCase = true) })
        assertTrue("Contains B.CMAY PICTURES", studios.any { it.contains("B.CMAY", ignoreCase = true) })
        assertTrue("Contains Wonder Cat", studios.any { it.contains("Wonder Cat", ignoreCase = true) })
    }

    @Test
    fun testQualityLevelsInclude4KUltraHD() {
        val qualities = VideoQuality.entries
        assertTrue(qualities.contains(VideoQuality.UHD_4K))
        assertTrue(VideoQuality.UHD_4K.requiresVip)
        assertFalse(VideoQuality.SD_360P.requiresVip)
        assertFalse(VideoQuality.HD_720P.requiresVip)
    }

    @Test
    fun testAuthModesSupported() {
        val modes = AuthMode.entries
        assertTrue(modes.contains(AuthMode.LOGIN))
        assertTrue(modes.contains(AuthMode.REGISTER))
        assertTrue(modes.contains(AuthMode.FORGOT_PASSWORD))
    }

    @Test
    fun testUserAccountState() {
        val guest = UserAccount(isLoggedIn = false, name = "Tamu", email = "")
        assertFalse(guest.isLoggedIn)

        val loggedInUser = UserAccount(
            isLoggedIn = true,
            name = "Budi Santoso",
            email = "budi@example.com",
            isGoogleUser = true
        )
        assertTrue(loggedInUser.isLoggedIn)
        assertTrue(loggedInUser.isGoogleUser)
        assertEquals("budi@example.com", loggedInUser.email)
    }
}
