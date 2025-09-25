package com.divine.traveller.ui.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import dev.darkokoa.datetimewheelpicker.WheelDateTimePicker
import dev.darkokoa.datetimewheelpicker.core.WheelPickerDefaults
import dev.darkokoa.datetimewheelpicker.core.format.CjkSuffixConfig
import dev.darkokoa.datetimewheelpicker.core.format.MonthDisplayStyle
import dev.darkokoa.datetimewheelpicker.core.format.TimeFormat
import dev.darkokoa.datetimewheelpicker.core.format.dateFormatter
import dev.darkokoa.datetimewheelpicker.core.format.timeFormatter
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun WheelDateTimePickerComposable(
    onCancel: () -> Unit = {},
    onConfirm: ((java.time.LocalDateTime) -> Unit)? = null,
) {

    var selectedDateTime by remember {
        mutableStateOf(
            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                .toJavaLocalDateTime()
        )
    }

    WheelDateTimePicker(
        startDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
        minDateTime = LocalDateTime(
            year = 2025,
            month = 1,
            day = 1,
            hour = 1,
            minute = 0
        ),
        maxDateTime = LocalDateTime(
            year = 2150,
            month = 10,
            day = 20,
            hour = 5,
            minute = 30
        ),
        dateFormatter = dateFormatter(
            locale = Locale.current,
            monthDisplayStyle = MonthDisplayStyle.SHORT,
            cjkSuffixConfig = CjkSuffixConfig.HideAll
        ),
        timeFormatter = timeFormatter(
            timeFormat = TimeFormat.HOUR_24
        ),
        size = DpSize(400.dp, 100.dp),
        rowCount = 5,
        textStyle = MaterialTheme.typography.titleSmall,
        textColor = MaterialTheme.colorScheme.primary,
        selectorProperties = WheelPickerDefaults.selectorProperties(
            enabled = true,
            shape = RoundedCornerShape(0.dp),
            color = Color(0xFFf1faee).copy(alpha = 0.2f),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.surface)
        )
    ) { snappedDateTime ->

        selectedDateTime = snappedDateTime.toJavaLocalDateTime()
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, end = 16.dp)
    ) {
        Button(
            onClick = { onConfirm?.invoke(selectedDateTime) },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Text("Confirm")
        }
    }
}