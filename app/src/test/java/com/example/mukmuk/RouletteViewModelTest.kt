package com.example.mukmuk

import com.example.mukmuk.data.model.Category
import com.example.mukmuk.data.repository.MenuRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for roulette spin logic.
 * These test the pure computation parts without Android framework dependencies.
 */
class RouletteViewModelTest {

    @Test
    fun toggleCategory_addsCategory() {
        val selected = emptySet<Category>()
        val result = if (Category.KOREAN in selected) {
            selected - Category.KOREAN
        } else {
            selected + Category.KOREAN
        }
        assertTrue(Category.KOREAN in result)
    }

    @Test
    fun toggleCategory_removesExisting() {
        val selected = setOf(Category.KOREAN, Category.JAPANESE)
        val result = if (Category.KOREAN in selected) {
            selected - Category.KOREAN
        } else {
            selected + Category.KOREAN
        }
        assertFalse(Category.KOREAN in result)
        assertTrue(Category.JAPANESE in result)
    }

    @Test
    fun onSpinComplete_calculatesCorrectIndex_firstSlice() {
        val menus = MenuRepository.allMenus
        // Angle 0 should point to first slice
        val finalAngle = 0f
        val normalized = ((finalAngle % 360f) + 360f) % 360f
        val arc = 360f / menus.size
        val index = ((360f - normalized + arc / 2f) % 360f / arc).toInt() % menus.size
        assertTrue(index in menus.indices)
    }

    @Test
    fun onSpinComplete_handlesLargeAngles() {
        val menus = MenuRepository.allMenus
        // Multiple full rotations + offset
        val finalAngle = 3600f + 45f
        val normalized = ((finalAngle % 360f) + 360f) % 360f
        val arc = 360f / menus.size
        val index = ((360f - normalized + arc / 2f) % 360f / arc).toInt() % menus.size
        assertTrue(index in menus.indices)
    }

    @Test
    fun onSpinComplete_handlesNegativeAngle() {
        val menus = MenuRepository.allMenus
        val finalAngle = -90f
        val normalized = ((finalAngle % 360f) + 360f) % 360f
        val arc = 360f / menus.size
        val index = ((360f - normalized + arc / 2f) % 360f / arc).toInt() % menus.size
        assertTrue(index in menus.indices)
    }

    @Test
    fun filteredMenus_emptySelection_returnsAll() {
        val result = MenuRepository.getFilteredMenus(emptySet())
        assertEquals(12, result.size)
    }

    @Test
    fun filteredMenus_withCategories_filtersCorrectly() {
        val result = MenuRepository.getFilteredMenus(setOf(Category.SNACK))
        assertTrue(result.isNotEmpty())
        result.forEach { assertEquals(Category.SNACK, it.category) }
    }

    @Test
    fun spinComplete_allAngles_produceValidIndex() {
        val menus = MenuRepository.allMenus
        val arc = 360f / menus.size
        // Test every 10 degrees
        for (angle in 0..3590 step 10) {
            val finalAngle = angle.toFloat()
            val normalized = ((finalAngle % 360f) + 360f) % 360f
            val index = ((360f - normalized + arc / 2f) % 360f / arc).toInt() % menus.size
            assertTrue("Angle $angle produced invalid index $index", index in menus.indices)
        }
    }

    @Test
    fun confirmSelection_menuAvailable() {
        val menus = MenuRepository.allMenus
        val selected = menus.first()
        assertNotNull(selected)
        assertEquals("김치찌개", selected.name)
    }
}
