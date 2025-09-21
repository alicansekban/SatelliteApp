
package com.alican.satellites.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alican.satellites.data.model.Position
import com.alican.satellites.data.model.Satellite
import com.alican.satellites.data.model.SatelliteDetail
import com.alican.satellites.ui.screens.detail.SatelliteDetailScreenContent
import com.alican.satellites.ui.screens.detail.SatelliteDetailUiState
import com.alican.satellites.ui.theme.SatellitesTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SatelliteDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testSatellite = Satellite(id = 1, name = "Starship-1", active = true)
    private val testSatelliteDetail = SatelliteDetail(
        id = 1,
        cost_per_launch = 7200000,
        first_flight = "2006-03-24",
        height = 22,
        mass = 30146
    )
    private val testPosition = Position(posX = 0.864328541, posY = 0.646450811)


    @Test
    fun satelliteDetailScreen_showsErrorState() {
        val errorMessage = "Satellite not found"
        
        composeTestRule.setContent {
            SatellitesTheme {
                SatelliteDetailScreenContent(
                    uiState = SatelliteDetailUiState(
                        isLoading = false,
                        error = errorMessage
                    )
                )
            }
        }

        composeTestRule.onNodeWithText(errorMessage)
            .assertIsDisplayed()
    }


    @Test
    fun satelliteDetailScreen_showsInactiveSatellite() {
        val inactiveSatellite = testSatellite.copy(active = false)
        
        composeTestRule.setContent {
            SatellitesTheme {
                SatelliteDetailScreenContent(
                    uiState = SatelliteDetailUiState(
                        satellite = inactiveSatellite,
                        satelliteDetail = testSatelliteDetail,
                        isLoading = false
                    )
                )
            }
        }

        composeTestRule.onNodeWithText("Inactive").assertIsDisplayed()
    }

    @Test
    fun satelliteDetailScreen_backButtonWorks() {
        var backClicked = false
        
        composeTestRule.setContent {
            SatellitesTheme {
                SatelliteDetailScreenContent(
                    uiState = SatelliteDetailUiState(
                        satellite = testSatellite,
                        isLoading = false
                    ),
                    onBackClicked = { backClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Back")
            .performClick()
        
        assert(backClicked)
    }

    @Test
    fun satelliteDetailScreen_showsNoPositionForInactiveSatellite() {
        val inactiveSatellite = testSatellite.copy(active = false)
        
        composeTestRule.setContent {
            SatellitesTheme {
                SatelliteDetailScreenContent(
                    uiState = SatelliteDetailUiState(
                        satellite = inactiveSatellite,
                        satelliteDetail = testSatelliteDetail,
                        currentPosition = null,
                        isLoading = false,
                        isLoadingDetail = false
                    )
                )
            }
        }

        composeTestRule.onNodeWithText("Position data not available")
            .assertIsDisplayed()
    }

    @Test
    fun satelliteDetailScreen_showsProperTopBarTitle() {
        composeTestRule.setContent {
            SatellitesTheme {
                SatelliteDetailScreenContent(
                    uiState = SatelliteDetailUiState(
                        satellite = testSatellite,
                        isLoading = false
                    )
                )
            }
        }

        // Check if satellite name is displayed in the top app bar
        composeTestRule.onAllNodesWithText("Starship-1")
            .assertCountEquals(2) // One in top bar, one in content
    }
}