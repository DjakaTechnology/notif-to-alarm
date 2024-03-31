package id.djaka.notiftoalarm.shared.ui.keyword

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import id.djaka.notiftoalarm.shared.model.KeywordItem
import id.djaka.notiftoalarm.shared.ui.theme.NotifToAlarmTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

data class KeywordScreen(
    val id: String
) : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { KeywordScreenModel(id) }
        LifecycleEffect(
            onStarted = { screenModel.onStart() },
        )
        NotifToAlarmTheme {
            Surface {
                Screen(screenModel.items, screenModel::onClickAddKeyword, screenModel::onClickDeleteKeyword)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun Screen(
    keywords: List<KeywordItem> = listOf(),
    onClickAddKeyword: (String) -> Unit = {},
    onClickDeleteKeyword: (String) -> Unit = {}
) {
    var keywordType by remember { mutableStateOf(KeywordItem.Type.CONTAINS) }
    var keywordTypeLabel by remember { mutableStateOf("Contains") }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var keyword by remember { mutableStateOf("") }

    Column(Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 12.dp).verticalScroll(rememberScrollState())) {
        Text("Add Keyword Filter", fontSize = 16.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(16.dp))
        ExposedDropdownMenuBox(
            modifier = Modifier.fillMaxWidth(),
            expanded = isDropdownExpanded,
            onExpandedChange = { isDropdownExpanded = it }
        ) {
            OutlinedTextField(
                value = keywordTypeLabel,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { isDropdownExpanded = !isDropdownExpanded }) {
                        Icon(Icons.Filled.ArrowDropDown, "add")
                    }
                },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                DropdownMenuItem(
                    text = { Text("Contains") },
                    onClick = {
                        keywordType = KeywordItem.Type.CONTAINS
                        keywordTypeLabel = "Contains"
                        isDropdownExpanded = false
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                DropdownMenuItem(
                    text = { Text("Does not contains") },
                    onClick = {
                        keywordType = KeywordItem.Type.EXCLUDE
                        keywordTypeLabel = "Does not contains"
                        isDropdownExpanded = false
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(value = keyword, onValueChange = { keyword = it }, modifier = Modifier.weight(1f))
            FilledIconButton(onClick = { onClickAddKeyword(keyword) }) {
                Icon(Icons.Filled.Add, "add")
            }
        }
        Spacer(Modifier.height(16.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            keywords.forEach { keyword ->
                KeywordItem(keyword.keyword) { onClickDeleteKeyword(keyword.id) }
            }
        }
    }
}

@Composable
private fun KeywordItem(
    keyword: String,
    onClickDelete: () -> Unit = {}
) {
    FilledTonalButton(onClick = onClickDelete, contentPadding = ButtonDefaults.TextButtonWithIconContentPadding) {
        Icon(Icons.Filled.Close, "delete", modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(2.dp))
        Text(keyword)
    }
}

@Preview
@Composable
fun DefaultPreview() {
    NotifToAlarmTheme {
        Screen()
    }
}