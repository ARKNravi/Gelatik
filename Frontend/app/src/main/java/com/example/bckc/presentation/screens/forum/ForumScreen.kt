package com.example.bckc.presentation.screens.forum

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bckc.R
import com.example.bckc.data.model.response.ForumResponse
import com.example.bckc.presentation.screens.forum.viewmodel.ForumUiState
import com.example.bckc.presentation.screens.forum.viewmodel.ForumViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.example.bckc.presentation.components.NavigationBar
import com.example.bckc.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumScreen(
    navController: NavController,
    viewModel: ForumViewModel = hiltViewModel()
) {
    val forumState by viewModel.forumState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Implement create post */ },
                containerColor = Color(0xFF2171CF),
                contentColor = Color.White
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = "Create post"
                )
            }
        },
        bottomBar = {
            NavigationBar(
                navController = navController,
                currentRoute = Screen.Forum.route
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
        ) {
            // Header
            Text(
                text = "Forum",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B1D28),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                placeholder = {
                    Text(
                        "Cari di sini...",
                        color = Color(0xFF8C8C8C)
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = null,
                        tint = Color(0xFF8C8C8C),
                        modifier = Modifier.size(24.dp)
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedBorderColor = Color(0xFF2171CF)
                ),
                singleLine = true
            )

            // Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                TabItem(
                    text = "Beranda",
                    isSelected = selectedTab == 0,
                    onClick = { viewModel.setSelectedTab(0) },
                    modifier = Modifier.weight(1f)
                )
                TabItem(
                    text = "Jelajah",
                    isSelected = selectedTab == 1,
                    onClick = { viewModel.setSelectedTab(1) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Content
            when (forumState) {
                is ForumUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF2171CF))
                    }
                }
                is ForumUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (forumState as ForumUiState.Error).message,
                            color = Color.Red
                        )
                    }
                }
                is ForumUiState.Success -> {
                    val forums = (forumState as ForumUiState.Success).forums
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(forums) { forum ->
                            ForumPostCard(forum)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TabItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color(0xFF2171CF) else Color(0xFF8C8C8C),
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(
                    if (isSelected) Color(0xFF2171CF)
                    else Color(0xFFE2E8F0)
                )
        )
    }
}

@Composable
private fun ForumPostCard(
    forum: ForumResponse,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Community Label
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_group),
                    contentDescription = null,
                    tint = Color(0xFF8C8C8C),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = forum.topic.capitalize(),
                    color = Color(0xFF8C8C8C),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            // Author Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE2E8F0))
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_profile),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp),
                            tint = Color.Unspecified
                        )
                    }
                    Column(
                        modifier = Modifier.padding(start = 12.dp)
                    ) {
                        Text(
                            text = forum.userName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = formatDate(forum.createdAt),
                            fontSize = 14.sp,
                            color = Color(0xFF8C8C8C)
                        )
                    }
                }

                IconButton(
                    onClick = { /* Show menu */ },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_more),
                        contentDescription = "More options",
                        tint = Color(0xFF8C8C8C),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Post Content
            Text(
                text = forum.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = forum.subtitle,
                fontSize = 14.sp,
                color = Color(0xFF8C8C8C),
                modifier = Modifier.padding(top = 4.dp)
            )

            // Optional Image
            if (forum.imageUrl != null) {
                AsyncImage(
                    model = forum.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(top = 16.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            // Interaction Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { /* Like action */ }
                    ) {
                        Text(
                            text = "${forum.likeCount}",
                            fontSize = 14.sp,
                            color = Color(0xFF8C8C8C),
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Icon(
                            painter = if (forum.hasLiked) {
                                painterResource(id = R.drawable.ic_heart_filled)
                            } else {
                                painterResource(id = R.drawable.ic_heart_outline)
                            },
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = Color.Unspecified
                        )
                        Text(
                            text = "Suka",
                            fontSize = 14.sp,
                            color = Color(0xFF8C8C8C),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { /* Comment action */ }
                    ) {
                        Text(
                            text = "${forum.commentCount}",
                            fontSize = 14.sp,
                            color = Color(0xFF8C8C8C),
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_comment),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = Color.Unspecified
                        )
                        Text(
                            text = "Komentar",
                            fontSize = 14.sp,
                            color = Color(0xFF8C8C8C),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                IconButton(
                    onClick = { /* Bookmark */ },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        painter = if (forum.hasBookmarked) {
                            painterResource(id = R.drawable.ic_bookmark_filled)
                        } else {
                            painterResource(id = R.drawable.ic_bookmark_outline)
                        },
                        contentDescription = "Bookmark",
                        modifier = Modifier.size(20.dp),
                        tint = Color.Unspecified
                    )
                }
            }
        }
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val instant = Instant.parse(dateString)
        val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        localDate.format(formatter)
    } catch (e: Exception) {
        dateString
    }
}
