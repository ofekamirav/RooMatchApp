import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.roomatchapp.presentation.theme.Primary

@Composable
fun CustomAlertDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    dialogBackgroundColor: Color = Color(0xFFF5F5F5)
) {
    var isDismissClicked by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        shape = RoundedCornerShape(20.dp),
        containerColor = dialogBackgroundColor,
        title = { Text(text = title, style = MaterialTheme.typography.titleMedium,color = Primary) },
        text = { Text(text = message, style = MaterialTheme.typography.bodyLarge,) },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { onConfirm() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary,
                        contentColor = Color.White,
                        disabledContainerColor = Primary.copy(alpha = 0.5f),
                        disabledContentColor = Color.White.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text(
                        text = "Confirm",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }

                OutlinedButton(
                    onClick = {
                        isDismissClicked = true
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = if(isDismissClicked) Color.White else Primary,
                            disabledContainerColor = Primary.copy(alpha = 0.5f),
                            disabledContentColor = Color.White.copy(alpha = 0.5f)
                        ),
                    border = BorderStroke(
                        width = 2.dp,
                        brush = SolidColor(Primary)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text(
                        text = "Dismiss",
                        style = MaterialTheme.typography.bodyLarge,
                        )
                }
            }
        },
        dismissButton = {}
    )
}

@Preview(showBackground = true)
@Composable
fun CustomAlertDialogPreview() {
    CustomAlertDialog(
        title = "Log out",
        message = "Are you sure you want to log out?",
        onDismiss = {},
        onConfirm = {}
    )
}
