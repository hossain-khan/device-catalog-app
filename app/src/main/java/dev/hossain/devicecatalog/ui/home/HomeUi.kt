package dev.hossain.devicecatalog.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.devicecatalog.di.AppScope

@CircuitInject(screen = HomeScreen::class, scope = AppScope::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceCatalogHome(
    state: HomeScreen.State,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier, topBar = { TopAppBar(title = { Text("Home") }) }) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(state.items) { item ->
                DeviceItemComposable(
                    item = item,
                    onClick = {
                        // TODO fix click event later
                        state.eventSink(HomeScreen.Event.ItemClicked(item.modelName))
                    },
                )
            }
        }
    }
}

@Composable
fun DeviceItemComposable(
    item: AndroidDevice,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .padding(16.dp)
                .clickable { onClick() },
    ) {
        Text(text = item.modelName)
        Text(text = item.device)
    }
}
