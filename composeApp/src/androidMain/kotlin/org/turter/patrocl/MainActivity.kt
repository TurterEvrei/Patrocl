package org.turter.patrocl

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.koin.core.context.GlobalContext
import org.publicvalue.multiplatform.oidc.ExperimentalOpenIdConnect
import org.publicvalue.multiplatform.oidc.appsupport.AndroidCodeAuthFlowFactory
import org.publicvalue.multiplatform.oidc.tokenstore.AndroidSettingsTokenStore
import org.turter.patrocl.di.initKoin

class MainActivity : ComponentActivity() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        val codeAuthFlowFactory = AndroidCodeAuthFlowFactory(useWebView = true)
    }

    @OptIn(ExperimentalOpenIdConnect::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        codeAuthFlowFactory.registerActivity(this)
        if(GlobalContext.getKoinApplicationOrNull() == null) {
            initKoin(
                authFlowFactory = codeAuthFlowFactory,
                tokenStore = AndroidSettingsTokenStore(context = this)
            )
        }

        setContent {
            App()
        }
    }
}
