package org.turter.patrocl

import androidx.compose.ui.window.ComposeUIViewController
import org.publicvalue.multiplatform.oidc.ExperimentalOpenIdConnect
import org.publicvalue.multiplatform.oidc.appsupport.IosCodeAuthFlowFactory
import org.publicvalue.multiplatform.oidc.tokenstore.IosKeychainTokenStore
import org.turter.patrocl.di.initKoin

@OptIn(ExperimentalOpenIdConnect::class)
fun MainViewController() = ComposeUIViewController(
        configure = {
                initKoin(
                        authFlowFactory = IosCodeAuthFlowFactory(ephemeralBrowserSession = true),
                        tokenStore = IosKeychainTokenStore()
                )
        }
) {
        App()
}