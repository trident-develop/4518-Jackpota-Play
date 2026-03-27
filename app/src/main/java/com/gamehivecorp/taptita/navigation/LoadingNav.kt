package com.gamehivecorp.taptita.navigation

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gamehivecorp.taptita.LoadingActivity
import com.gamehivecorp.taptita.MainActivity
import com.gamehivecorp.taptita.gray4.Gray4
import com.gamehivecorp.taptita.gray4.STUB_STORAGE_KEY
import com.gamehivecorp.taptita.gray4.STUB_STORAGE_VALUE_TRUE
import com.gamehivecorp.taptita.gray4.datastore.StorageImpl
import com.gamehivecorp.taptita.ui.screens.ConnectScreen
import com.gamehivecorp.taptita.ui.screens.LoadingScreen
import com.gamehivecorp.taptita.ui.screens.isFlowersConnected
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("ContextCastToActivity")
@Composable
fun LoadingGraph() {

    val navController = rememberNavController()
    val context = LocalContext.current as LoadingActivity

    NavHost(
        navController = navController,
        startDestination = if (context.isFlowersConnected()) Routes.LOADING else Routes.CONNECT
    ) {
        composable(Routes.LOADING) {

//            LaunchedEffect(Unit) {
//                delay(2000)
//                context.startActivity(Intent(context, MainActivity::class.java))
//                context.finish()
//            }

            //-----------------
            val toNoNet = {
                navController.navigate(Routes.CONNECT) {
                    popUpTo(Routes.LOADING) { inclusive = true }
                }
            }

            val toStub = {
                context.startActivity(Intent(context, MainActivity::class.java))
                context.finish()
            }

            val internetCheck = {
                context.isFlowersConnected()
            }


            Gray4(toStub = {
                CoroutineScope(Dispatchers.IO).launch {
                    Log.d("TAGG", "Save Stub TRUE")
                    val storage = StorageImpl.getInstance(context)
                    storage.putString(STUB_STORAGE_KEY, STUB_STORAGE_VALUE_TRUE)
                }
                toStub()
            }, toNoNet = toNoNet,
                internetCheck)

            //-----------------

            LoadingScreen({})
        }

        composable(Routes.CONNECT) {
            ConnectScreen(navController)
        }
    }
}