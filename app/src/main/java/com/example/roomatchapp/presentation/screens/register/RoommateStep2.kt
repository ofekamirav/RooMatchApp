package com.example.roomatchapp.presentation.screens.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import com.example.roomatchapp.data.remote.dto.Attribute
import com.example.roomatchapp.data.remote.dto.Hobby
import com.example.roomatchapp.presentation.components.SurveyTopAppProgress
import com.example.roomatchapp.presentation.register.RegistrationViewModel
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.Secondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoommateStep2(
    onContinue: () -> Unit,
    viewModel: RegistrationViewModel,
    stepIndex: Int = 1,
    totalSteps: Int = 4
){

    val state by viewModel.roommateState.collectAsState()

    val attributes = Attribute.entries
    val hobbies = Hobby.entries

    val isStepValid by remember(state.attributes, state.hobbies) {
        derivedStateOf {
            state.attributes.size >= 3 && state.hobbies.size >= 3
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEEEEE1))
            .padding(top = 0.dp, start = 4.dp, end = 4.dp, bottom = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Text("Attributes:", fontSize = MaterialTheme.typography.titleLarge.fontSize, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(16.dp))
            Text("Select at least 3 attributes", fontSize = MaterialTheme.typography.titleSmall.fontSize, fontWeight = FontWeight.Light, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow {
                attributes.forEach { attr ->
                    val isSelected = attr in state.attributes
                    Button(
                        onClick = { viewModel.toggleAttribute(attr) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Color(0xFF01999E) else Color(0xFF94D1CA),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .padding(4.dp)
                            .height(42.dp)
                            .width(112.dp)
                    ) {
                        Text(
                            text = attr.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                            fontSize = MaterialTheme.typography.titleSmall.fontSize,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            softWrap = false,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Visible
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))
            Text("Hobbies:", fontSize = MaterialTheme.typography.titleLarge.fontSize, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(16.dp))
            Text("Select at least 3 hobbies", fontSize = MaterialTheme.typography.titleSmall.fontSize, fontWeight = FontWeight.Light, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            FlowRow {
                hobbies.forEach { hobby ->
                    val isSelected = hobby in state.hobbies
                    Button(
                        onClick = { viewModel.toggleHobby(hobby) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Primary else Secondary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .padding(4.dp)
                            .height(42.dp)
                            .width(112.dp)
                    ) {
                        Text(
                            text = hobby.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                            fontSize = MaterialTheme.typography.titleSmall.fontSize,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            softWrap = false,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Visible
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onContinue,
                enabled = isStepValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                ,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Secondary,
                    contentColor = Color.White
                ),

                ) {
                Text(
                    "Continue",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

    }

}

@Preview(showBackground = true)
@Composable
fun RoommateStep2Preview(){
    RoommateStep2(
        onContinue = {},
        viewModel = RegistrationViewModel()
    )
}