package com.alican.satellites.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alican.satellites.data.model.Satellite
import com.alican.satellites.ui.screens.list.SatelliteListScreen
import com.alican.satellites.ui.screens.list.SatelliteListScreenContent
import com.alican.satellites.ui.screens.list.SatelliteListUIEvent
import com.alican.satellites.ui.screens.list.SatelliteListUiState
import com.alican.satellites.ui.theme.SatellitesTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SatelliteListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testSatellites = listOf(
        Satellite(id = 1, name = "Starship-1", active = true),
        Satellite(id = 2, name = "Dragon-1", active = false),
        Satellite(id = 3, name = "Falcon-9", active = true)
    )

    @Test
    fun satelliteListScreen_showsErrorState() {
        val errorMessage = "Network error occurred"
        
        composeTestRule.setContent {
            SatellitesTheme {
                SatelliteListScreenContent(
                    uiState = SatelliteListUiState(
                        isLoading = false,
                        error = errorMessage
                    )
                )
            }
        }

        composeTestRule.onNodeWithText(errorMessage)
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("Retry")
            .assertIsDisplayed()
    }

    @Test
    fun satelliteListScreen_showsSatelliteList() {
        composeTestRule.setContent {
            SatellitesTheme {
                SatelliteListScreenContent(
                    uiState = SatelliteListUiState(
                        satellites = testSatellites,
                        filteredSatellites = testSatellites,
                        isLoading = false
                    )
                )
            }
        }

        // Check if all satellites are displayed
        composeTestRule.onNodeWithText("Starship-1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dragon-1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Falcon-9").assertIsDisplayed()
    }

    @Test
    fun satelliteListScreen_searchFunctionality() {
        var searchQuery = ""
        
        composeTestRule.setContent {
            SatellitesTheme {
                SatelliteListScreenContent(
                    uiState = SatelliteListUiState(
                        satellites = testSatellites,
                        filteredSatellites = testSatellites.filter { 
                            it.name.contains(searchQuery, ignoreCase = true) 
                        },
                        searchQuery = searchQuery,
                        isLoading = false
                    ),
                    onSearchQueryChanged = { searchQuery = it }
                )
            }
        }

        // Find search field and type
        composeTestRule.onNodeWithText("Search satellites")
            .performTextInput("Dragon")
        
        // Check if search field is working (this is a basic check)
        composeTestRule.onNodeWithText("Search satellites")
            .assertIsDisplayed()
    }

    @Test
    fun satelliteListScreen_showsEmptySearchResult() {
        composeTestRule.setContent {
            SatellitesTheme {
                SatelliteListScreenContent(
                    uiState = SatelliteListUiState(
                        satellites = testSatellites,
                        filteredSatellites = emptyList(),
                        searchQuery = "NonExistingSatellite",
                        isLoading = false
                    )
                )
            }
        }

        composeTestRule.onNodeWithText("No satellites found for \"NonExistingSatellite\"")
            .assertIsDisplayed()
    }

    @Test
    fun satelliteListScreen_clickOnSatelliteItem() {
        var clickedSatelliteId = -1
        
        composeTestRule.setContent {
            SatellitesTheme {
                SatelliteListScreenContent(
                    uiState = SatelliteListUiState(
                        satellites = testSatellites,
                        filteredSatellites = testSatellites,
                        isLoading = false
                    ),
                    onNavigateToDetail = { satelliteId ->
                        clickedSatelliteId = satelliteId
                    }
                )
            }
        }

        // Click on first satellite
        composeTestRule.onNodeWithText("Starship-1")
            .performClick()
        
        // Verify click was registered (in real test, you'd verify navigation)
        assert(clickedSatelliteId == 1)
    }

    @Test
    fun satelliteListScreen_retryButtonWorks() {
        var retryClicked = false
        
        composeTestRule.setContent {
            SatellitesTheme {
                SatelliteListScreenContent(
                    uiState = SatelliteListUiState(
                        isLoading = false,
                        error = "Network error"
                    ),
                    onEvent = { event ->
                        if (event is SatelliteListUIEvent.RetryClicked) {
                            retryClicked = true
                        }
                    }
                )
            }
        }

        composeTestRule.onNodeWithText("Retry")
            .performClick()
        
        assert(retryClicked)
    }
}