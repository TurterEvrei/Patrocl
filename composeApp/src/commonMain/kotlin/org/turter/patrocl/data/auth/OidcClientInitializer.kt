package org.turter.patrocl.data.auth

import org.publicvalue.multiplatform.oidc.OpenIdConnectClient
import org.publicvalue.multiplatform.oidc.types.CodeChallengeMethod

class OidcClientInitializer {
    private val BASE_AUTH_URL = "http://92.255.107.65:8080/realms/TurterApp"
    
    val client = OpenIdConnectClient {
        endpoints {
            tokenEndpoint = "$BASE_AUTH_URL/protocol/openid-connect/token"
            authorizationEndpoint = "$BASE_AUTH_URL/protocol/openid-connect/auth"
            userInfoEndpoint = "$BASE_AUTH_URL/protocol/openid-connect/userinfo"
            endSessionEndpoint = "$BASE_AUTH_URL/protocol/openid-connect/logout"
        }
        clientId = "mobile-waiter"
        clientSecret = "Bw2HZbLYeSChGzJ3SsVR7MpErqHtuYD7"
        scope = "openid profile"
        codeChallengeMethod = CodeChallengeMethod.S256
        redirectUri = "turter.app.waiter.mobile://callback"
    }
}