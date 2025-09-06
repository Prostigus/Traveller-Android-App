package com.divine.traveller.ui.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.divine.traveller.data.entity.ItineraryCategory
import com.divine.traveller.data.entity.ItineraryItemStatus
import com.divine.traveller.data.viewmodel.ItineraryViewModel
import com.divine.traveller.model.ItineraryItemModel
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItineraryItemBottomSheet(
    visible: Boolean,
    selectedDateTime: LocalDateTime?,
    timeZone: ZoneId,
    tripId: Long,
    viewModel: ItineraryViewModel,
    onDismiss: () -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    if (visible && selectedDateTime != null) {
        ModalBottomSheet(
            onDismissRequest = onDismiss
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Add Itinerary Item", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    text = "Time: ${selectedDateTime.hour.toString().padStart(2, '0')}:${selectedDateTime.minute.toString().padStart(2, '0')}",
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        val startInstant = selectedDateTime.atZone(timeZone).toInstant()
                        val endInstant = selectedDateTime.plusHours(1).atZone(timeZone).toInstant()

                        val newItem = ItineraryItemModel(
                            tripId = tripId,
                            title = title,
                            description = description,
                            placeId = "",
                            viewType = "view",
                            startDateTime = Date.from(startInstant),
                            endDateTime = Date.from(endInstant),
                            category = ItineraryCategory.OTHER,
                            status = ItineraryItemStatus.PENDING
                        )
                        viewModel.insert(newItem)
                        title = ""
                        description = ""
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save")
                }
            }
        }
    }
}