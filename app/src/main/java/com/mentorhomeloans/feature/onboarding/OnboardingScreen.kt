package com.mentorhomeloans.feature.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.res.stringResource
import com.mentorhomeloans.core.navigation.Screen
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.mentorhomeloans.R
import com.mentorhomeloans.ui.theme.MentorBlue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    navController: NavController,
    viewModel: OnboardingViewModel,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    data class OnboardingItem(
        val title: String,
        val description: String,
        val imageRes: Int,
        val icon: androidx.compose.ui.graphics.vector.ImageVector
    )

    val onboardingItems = listOf(
        OnboardingItem(stringResource(R.string.onboarding_title_1), stringResource(R.string.onboarding_desc_1), R.drawable.onboard_1, Icons.Default.Home),
        OnboardingItem(stringResource(R.string.onboarding_title_2), stringResource(R.string.onboarding_desc_2), R.drawable.onboard_2, Icons.Default.CalendarMonth),
        OnboardingItem(stringResource(R.string.onboarding_title_3), stringResource(R.string.onboarding_desc_3), R.drawable.onboard_3, Icons.Default.Analytics)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2A52BE), 
                        Color(0xFF5C6BC0)
                    )
                )
            )
            .systemBarsPadding()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val item = onboardingItems[page]

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Top Graphic / Image area
                Image(
                    painter = painterResource(id = item.imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.5f),
                    contentScale = ContentScale.Crop
                )

                // Bottom White Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .fillMaxHeight(0.5f),
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 48.dp, start = 24.dp, end = 24.dp, bottom = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = item.title,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MentorBlue,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = item.description,
                                fontSize = 14.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                lineHeight = 22.sp
                            )
                        }

                        // Pager Indicator
                        Row(
                            modifier = Modifier.padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            repeat(3) { index ->
                                val isActive = pagerState.currentPage == index
                                val width by animateDpAsState(
                                    targetValue = if (isActive) 24.dp else 8.dp,
                                    animationSpec = tween(300)
                                )
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp)
                                        .height(8.dp)
                                        .width(width)
                                        .clip(CircleShape)
                                        .background(if (isActive) Color(0xFF3F51B5) else Color(0xFFC5CAE9))
                                )
                            }
                        }

                        // Next Button
                        Button(
                            onClick = {
                                if (pagerState.currentPage == 2) {
                                    viewModel.completeOnboarding()
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                                    }
                                } else {
                                    scope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF283593))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (pagerState.currentPage == 2) stringResource(R.string.action_get_started) else stringResource(R.string.action_next),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // Floating Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = (-4).dp)
                        .size(72.dp)
                        .background(Color(0xFFE8EAF6), CircleShape)
                        .padding(8.dp)
                        .background(Color(0xFF3F51B5), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
        
        // Skip Button
        TextButton(
            onClick = {
                viewModel.completeOnboarding()
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text(text = stringResource(R.string.action_skip), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}
