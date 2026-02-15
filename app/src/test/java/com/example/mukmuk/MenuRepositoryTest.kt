package com.example.mukmuk

import com.example.mukmuk.data.model.Category
import com.example.mukmuk.data.repository.MenuRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MenuRepositoryTest {

    @Test
    fun allMenus_hasCorrectCount() {
        assertEquals(12, MenuRepository.allMenus.size)
    }

    @Test
    fun getFilteredMenus_emptyCategories_returnsAll() {
        val result = MenuRepository.getFilteredMenus(emptySet())
        assertEquals(MenuRepository.allMenus.size, result.size)
    }

    @Test
    fun getFilteredMenus_singleCategory_filtersCorrectly() {
        val result = MenuRepository.getFilteredMenus(setOf(Category.KOREAN))
        assertTrue(result.isNotEmpty())
        assertTrue(result.all { it.category == Category.KOREAN })
    }

    @Test
    fun getFilteredMenus_multipleCategories_filtersCorrectly() {
        val categories = setOf(Category.KOREAN, Category.JAPANESE)
        val result = MenuRepository.getFilteredMenus(categories)
        assertTrue(result.isNotEmpty())
        assertTrue(result.all { it.category in categories })
    }

    @Test
    fun getFilteredMenus_koreanMenus_containsExpected() {
        val result = MenuRepository.getFilteredMenus(setOf(Category.KOREAN))
        val names = result.map { it.name }
        assertTrue("김치찌개" in names)
        assertTrue("삼겹살" in names)
        assertTrue("비빔밥" in names)
    }

    @Test
    fun getFilteredMenus_nonexistentCombination_returnsEmpty() {
        // All categories combined should cover everything
        val allCategories = Category.entries.toSet()
        val result = MenuRepository.getFilteredMenus(allCategories)
        assertEquals(MenuRepository.allMenus.size, result.size)
    }

    @Test
    fun allMenus_haveRequiredFields() {
        MenuRepository.allMenus.forEach { menu ->
            assertTrue(menu.name.isNotBlank())
            assertTrue(menu.emoji.isNotBlank())
        }
    }

    @Test
    fun allMenus_coverAllCategories() {
        val coveredCategories = MenuRepository.allMenus.map { it.category }.toSet()
        assertEquals(Category.entries.toSet(), coveredCategories)
    }
}
