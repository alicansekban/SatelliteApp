package com.alican.satellites.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alican.satellites.data.model.Satellite
import com.alican.satellites.ui.screens.list.SatelliteListScreenContent
import com.alican.satellites.ui.screens.list.SatelliteListUiState
import com.alican.satellites.ui.theme.SatellitesTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SatelliteSearchTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testSatellites = listOf(
        Satellite(id = 1, name = "Starship-1", active = true),
        Satellite(id = 2, name = "Dragon-1", active = false),
        Satellite(id = 3, name = "Falcon-9", active = true),
        Satellite(id = 4, name = "Starship-2", active = true)
    )

    @Test
    fun search_filtersResultsCorrectly() {
        val filteredSatellites = testSatellites.filter { 
            it.name.contains("Starship", ignoreCase = true) 
        }
        
        composeTestRule.setContent {
            SatellitesTheme {
                SatelliteListScreenContent(
                    uiState = SatelliteListUiState(
                        satellites = testSatellites,
                        filteredSatellites = filteredSatellites,
                        searchQuery = "Starship",
                        isLoading = false
                    )
                )
            }
        }

        // Should show only Starship satellites
        composeTestRule.onNodeWithText("Starship-1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Starship-2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dragon-1").assertDoesNotExist()
        composeTestRule.onNodeWithText("Falcon-9").assertDoesNotExist()
    }

    @Test
    fun search_caseInsensitiveSearch() {
        val filteredSatellites = testSatellites.filter { 
            it.name.contains("dragon", ignoreCase = true) 
        }
        
        composeTestRule.setContent {
            SatellitesTheme {
                SatelliteListScreenContent(
                    uiState = SatelliteListUiState(
                        satellites = testSatellites,
                        filteredSatellites = filteredSatellites,
                        searchQuery = "dragon",
                        isLoading = false
                    )
                )
            }
        }

        composeTestRule.onNodeWithText("Dragon-1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Starship-1").assertDoesNotExist()
    }
}