package org.turter.patrocl.data.client

import io.ktor.http.encodeURLPath
import org.turter.patrocl.data.client.ApiServiceName.ORGANIZATION_SERVICE
import org.turter.patrocl.data.client.ApiServiceName.STATION_CONNECTOR

object ApiServiceName {
    val STATION_CONNECTOR = "station-connector"
    val ORGANIZATION_SERVICE = "organization-service"
    val NOTIFICATION_SERVICE = "notification-service"
}

object ApiEndpoint {
    private val API_BASE_URL_HTTP = "http://192.168.0.105:18765"
    private val API_BASE_URL_WS = "ws://192.168.0.105:18765"

    object Menu {
        fun getCategoryTree() = "$API_BASE_URL_HTTP/$STATION_CONNECTOR/menu/category-list/tree"
        fun getDishes() = "$API_BASE_URL_HTTP/$STATION_CONNECTOR/menu/dish/list"
        fun getModifiers() = "$API_BASE_URL_HTTP/$STATION_CONNECTOR/menu/modifier/list"
    }

    object Hall {
        fun getTables() = "$API_BASE_URL_HTTP/$STATION_CONNECTOR/table/list"
    }

    object Order {
        fun getOrder(guid: String) = "$API_BASE_URL_HTTP/$STATION_CONNECTOR/order/$guid".encodeURLPath()
        fun getOrderList() = "$API_BASE_URL_HTTP/$STATION_CONNECTOR/order/list"
        fun createOrder() = "$API_BASE_URL_HTTP/$STATION_CONNECTOR/order/create"
        fun updateOrder(guid: String) = "$API_BASE_URL_HTTP/$STATION_CONNECTOR/order/$guid".encodeURLPath()
        fun removeItem(guid: String) = "$API_BASE_URL_HTTP/$STATION_CONNECTOR/order/$guid/remove-item".encodeURLPath()
        fun getWebSocketOrderList() = "$API_BASE_URL_WS/$STATION_CONNECTOR/order/list/ws"
    }

    object Employee {
        fun getOwnEmployee() = "$API_BASE_URL_HTTP/$ORGANIZATION_SERVICE/employee/own"
    }

    object Waiter {
        fun getOwnWaiter() = "$API_BASE_URL_HTTP/$STATION_CONNECTOR/waiter/own"
        fun editOwnEmployee() = "$API_BASE_URL_HTTP/$STATION_CONNECTOR/waiter/own"
    }
}