package ir.kazemcodes.infinity.extension_feature.presentation.extension_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zhuinden.simplestackcomposeintegration.core.LocalBackstack
import ir.kazemcodes.infinity.api_feature.network.apis
import ir.kazemcodes.infinity.base_feature.navigation.BrowserScreenKey

@Composable
fun ExtensionScreen(modifier: Modifier= Modifier) {
    val backStack = LocalBackstack.current
    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.background,elevation = 0.dp
            ) {
                Text("Extension", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.h5)
            }
        }
    ) {
        LazyColumn {
            items(apis.size) {index ->
                Text(apis[index].name, modifier = modifier.padding(16.dp).clickable {
                    backStack.goTo(BrowserScreenKey(apis[index]))
                })
            }
        }
        apis.forEach {api->

        }

    }
}