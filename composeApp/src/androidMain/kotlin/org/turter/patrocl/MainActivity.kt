package org.turter.patrocl

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.koin.compose.KoinApplication
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.publicvalue.multiplatform.oidc.ExperimentalOpenIdConnect
import org.publicvalue.multiplatform.oidc.appsupport.AndroidCodeAuthFlowFactory
import org.publicvalue.multiplatform.oidc.appsupport.CodeAuthFlowFactory
import org.publicvalue.multiplatform.oidc.tokenstore.AndroidSettingsTokenStore
import org.publicvalue.multiplatform.oidc.tokenstore.TokenStore
import org.turter.patrocl.di.appModule
import org.turter.patrocl.di.dataModule
import org.turter.patrocl.di.initKoin
import org.turter.patrocl.di.viewModelModule

class MainActivity : ComponentActivity() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        val codeAuthFlowFactory = AndroidCodeAuthFlowFactory(useWebView = true)
    }

    @OptIn(ExperimentalOpenIdConnect::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        codeAuthFlowFactory.registerActivity(this)
        codeAuthFlowFactory

//        initKoin(
//            authFlowFactory = codeAuthFlowFactory,
//            tokenStore = AndroidSettingsTokenStore(context = this)
//        )

        setContent {
            org.turter.patrocl.di.KoinApplication(
                authFlowFactory = codeAuthFlowFactory,
                tokenStore = AndroidSettingsTokenStore(context = this@MainActivity)
            ) {

                App(
//                authFlowFactory = codeAuthFlowFactory,
//                tokenStore = AndroidSettingsTokenStore(context = this)
                )
            }
        }
    }
}
