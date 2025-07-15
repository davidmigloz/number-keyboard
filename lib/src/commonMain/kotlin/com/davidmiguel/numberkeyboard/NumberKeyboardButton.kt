package com.davidmiguel.numberkeyboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.material3.LocalContentColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.davidmiguel.numberkeyboard.listener.NumberKeyboardClickedListener

@Composable
fun NumberKeyboardButton(
    modifier: Modifier,
    textStyle: TextStyle,
    shape: Shape = RoundedCornerShape(size = 8.dp),
    haptics: HapticFeedback = LocalHapticFeedback.current,
    number: Int,
    listener: NumberKeyboardClickedListener
) {
    OutlinedButton(
        modifier = modifier,
        shape = shape,
        border = BorderStroke(1.dp, Color.LightGray),
        onClick = {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            listener.onNumberClicked(number)
        }
    ) {
        Text(
            text = number.toString(),
            style = textStyle
        )
    }
}

/**
 * Default auxiliary button for [NumberKeyboard]. Can display either text or an icon.
 *
 * @param modifier button modifier
 * @param textStyle style applied to [value] text
 * @param shape button shape
 * @param haptics haptic feedback implementation
 * @param value optional text displayed inside the button
 * @param imageVector optional icon displayed when [value] is null or blank
 * @param clicked callback invoked when the button is clicked
 * @param colors customizable [ButtonColors] for the button
 * @param iconTint tint applied to [imageVector]
 */
@Composable
fun NumberKeyboardAuxButton(
    modifier: Modifier,
    textStyle: TextStyle,
    shape: Shape = RoundedCornerShape(size = 8.dp),
    haptics: HapticFeedback = LocalHapticFeedback.current,
    value: String? = null,
    imageVector: ImageVector? = null,
    clicked: () -> Unit,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    iconTint: Color = LocalContentColor.current
) {
    if (value.isNullOrBlank() && imageVector == null) return
    OutlinedButton(
        modifier = modifier,
        shape = shape,
        border = BorderStroke(1.dp, Color.LightGray),
        colors = colors,
        onClick = {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            clicked.invoke()
        }
    ) {
        if (!value.isNullOrBlank()) {
            Text(
                text = value,
                style = textStyle
            )
        } else if (imageVector != null) {
            Image(
                imageVector = imageVector,
                contentDescription = imageVector.name,
                colorFilter = ColorFilter.tint(iconTint)
            )
        }
    }
}

@Composable
fun RowScope.NumberKeyboardBlank() {
    Spacer(modifier = Modifier.weight(1F))
}
