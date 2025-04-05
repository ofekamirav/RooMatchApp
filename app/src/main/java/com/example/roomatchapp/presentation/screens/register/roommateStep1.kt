package com.example.roomatchapp.presentation.screens.register

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.roomatchapp.R
import com.example.roomatchapp.presentation.components.CapsuleTextField
import com.example.roomatchapp.presentation.components.SurveyTopAppProgress
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.Secondary


@Composable
fun RoommateStep1(
    onContinue: () -> Unit,
    stepIndex: Int = 0,
    totalSteps: Int = 3,
    onAIButtonClick: () -> Unit
){
    var personalBio by remember { mutableStateOf("") }
    var workPlace by remember { mutableStateOf("") }
    val genders = listOf("Men", "Women", "Other")
    var selectedGender by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(top = 0.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
        contentAlignment = Alignment.Center
    ){
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Spacer(modifier = Modifier.height(50.dp))
            SurveyTopAppProgress(stepIndex = stepIndex, totalSteps = totalSteps)

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "About you..",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .align(Alignment.Start)
            )

            // Gender
            Text(
                text = "Gender:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Light,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            genders.forEach { gender ->
                val isSelected = gender == selectedGender

                Button(
                    onClick = { selectedGender = gender },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Primary else Secondary,
                        contentColor = Color.White,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .height(48.dp),
                ) {
                    Text(gender, fontWeight = FontWeight.Light, fontSize = MaterialTheme.typography.titleMedium.fontSize)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(22.dp))


            // Work
            Text("Work:", modifier = Modifier.align(Alignment.Start), fontWeight = FontWeight.Light, fontSize = MaterialTheme.typography.titleMedium.fontSize)
            Spacer(modifier = Modifier.height(8.dp))
            CapsuleTextField(
                value = workPlace,
                onValueChange = { workPlace = it },
                placeholder = "Work place"
            )

            Spacer(modifier = Modifier.height(22.dp))

            //Personal Bio
            Text("Personal Bio:", modifier = Modifier.align(Alignment.Start), fontWeight = FontWeight.Light, fontSize = MaterialTheme.typography.titleMedium.fontSize)
            Spacer(modifier = Modifier.height(8.dp))
            CapsuleTextField(
                value = personalBio,
                onValueChange = { personalBio = it },
                placeholder = "Personal Bio",
                modifier = Modifier
                    .height(170.dp)
                    .fillMaxWidth()

            )
            Spacer(modifier = Modifier.height(8.dp))

            //AI Button
            Button(
                onClick = onAIButtonClick,
                modifier = Modifier
                    .align(Alignment.End)
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black,
                ),
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, Secondary)
            ) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_gemini),
                        contentDescription = "AI Icon",
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Suggest",
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                ,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Secondary,
                    contentColor = Color.White
                ),
            ) {
                Text("Continue",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.SansSerif)
            }




        }


    }
}


@Preview(showBackground = true)
@Composable
fun RoommateStep1Preview(){
    RoommateStep1(onContinue = {}, onAIButtonClick = {})
}