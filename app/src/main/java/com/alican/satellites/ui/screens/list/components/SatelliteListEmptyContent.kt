package com.alican.satellites.ui.screens.list.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.alican.satellites.R

@Composable
fun SatelliteListEmptyContent(
    searchQuery: String,
) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.no_satellites_found, searchQuery),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}


@Preview(showBackground = true, name = "Satellite List Empty Content")
@Composable
private fun SatelliteListEmptyContentPreview() {
    SatelliteListEmptyContent(searchQuery = "Tesla")
}