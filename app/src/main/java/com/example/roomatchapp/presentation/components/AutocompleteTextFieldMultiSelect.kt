import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.roomatchapp.presentation.components.CapsuleTextField
 import androidx.compose.foundation.lazy.items
import androidx.compose.ui.tooling.preview.Preview
import com.example.roomatchapp.data.model.Roommate
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.Secondary
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AutocompleteTextFieldMultiSelect(
    suggestionsList: List<Roommate>,
    placeHolder: String,
    selectedUsers: List<Roommate>,
    onUserSelected: (Roommate) -> Unit,
    onUserRemoved: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var filteredSuggestions by remember { mutableStateOf<List<Roommate>>(emptyList()) }
    var isDropdownVisible by remember { mutableStateOf(false) }

    Column {
        CapsuleTextField(
            value = text,
            onValueChange = { newText ->
                text = newText
                if (newText.length >= 2) {
                    filteredSuggestions = suggestionsList
                        .filter { it.fullName.contains(newText, ignoreCase = true) && !selectedUsers.map { s -> s.id }.contains(it.id) }
                    isDropdownVisible = filteredSuggestions.isNotEmpty()
                } else {
                    filteredSuggestions = emptyList()
                    isDropdownVisible = false
                }
            },
            placeholder = placeHolder,
            modifier = Modifier.fillMaxWidth(),
            isError = false,
            supportingText = null,
            enabled = true,
        )

        if (isDropdownVisible) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                LazyColumn(modifier = Modifier.heightIn(max = 150.dp)) {
                    items(filteredSuggestions) { roommate ->
                        Text(
                            text = roommate.fullName,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable {
                                    onUserSelected(roommate)
                                    text = ""
                                    filteredSuggestions = emptyList()
                                    isDropdownVisible = false
                                }
                        )
                    }
                }
            }
        }

        // Display selected users
        if (selectedUsers.isNotEmpty()) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                selectedUsers.forEach { user ->
                    AssistChip(
                        onClick = { onUserRemoved(user.id) },
                        label = { Text(user.fullName) },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove ${user.fullName}",
                                tint = Color.White,
                            )
                        },
                        shape = RoundedCornerShape(50),
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = Secondary,
                            labelColor = Color.White),
                        border = BorderStroke(
                            width = 1.dp,
                            color = Secondary
                        )
                    )
                }
            }
        }
    }
}




