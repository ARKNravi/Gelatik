package com.example.bckc.presentation.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Register : Screen("register")
    object Translate : Screen("translate")
    object Forum : Screen("forum")
    object ForumDetail : Screen("forum_detail")
    object JBISearch : Screen("jbi_search")
    object JBIDetail : Screen("jbi_detail")
    object JBIChat : Screen("jbi_chat")
    object JBI : Screen("jbi")
    object Profile : Screen("profile")
}
