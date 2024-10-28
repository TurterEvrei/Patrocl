package org.turter.patrocl

import androidx.compose.ui.window.ComposeUIViewController
import org.publicvalue.multiplatform.oidc.ExperimentalOpenIdConnect
import org.publicvalue.multiplatform.oidc.appsupport.IosCodeAuthFlowFactory
import org.publicvalue.multiplatform.oidc.tokenstore.IosKeychainTokenStore
import org.turter.patrocl.di.KoinApplication

@OptIn(ExperimentalOpenIdConnect::class)
fun MainViewController() = ComposeUIViewController {

    val factory = IosCodeAuthFlowFactory()

//    initKoin(
//        authFlowFactory = factory,
//        tokenStore = IosKeychainTokenStore()
//    )

    KoinApplication(
        authFlowFactory = factory,
        tokenStore = IosKeychainTokenStore()
    ) {
        App()
    }

}