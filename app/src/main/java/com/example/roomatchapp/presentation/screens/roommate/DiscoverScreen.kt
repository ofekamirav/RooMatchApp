package com.example.roomatchapp.presentation.screens.roommate

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.roomatchapp.R
import com.example.roomatchapp.data.remote.dto.MatchDto
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.CardBackground
import com.example.roomatchapp.presentation.theme.RooMatchAppTheme

//import com.example.roomatchapp.presentation.navigation.RoommateGraph
//import com.ramcosta.composedestinations.annotation.Destination

//@Destination<RoommateGraph>
@Composable
fun DiscoverScreen(){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        //contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 0.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_name),
                contentDescription = "Logo",
                modifier = Modifier.size(150.dp),
            )
        }
    }
}


@Composable
fun MatchCard(
    match: MatchDto,
    onMatchSlide: () -> Unit,
    onRejectSlide: () -> Unit,
){
    Card(
        modifier = Modifier
            .size(150.dp)
            .padding(16.dp)
            .background(CardBackground),
    ) {

    }

}

@Preview(showBackground = true)
@Composable
fun PreviewDiscoverScreen(){
    RooMatchAppTheme{
        DiscoverScreen()
    }
}
