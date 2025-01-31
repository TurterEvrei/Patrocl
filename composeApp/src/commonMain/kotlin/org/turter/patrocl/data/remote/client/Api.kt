package org.turter.patrocl.data.remote.client

import io.ktor.http.encodeURLPath
import org.turter.patrocl.data.remote.client.ApiServiceName.ORDER_SERVICE
import org.turter.patrocl.data.remote.client.ApiServiceName.ORGANIZATION_SERVICE
import org.turter.patrocl.data.remote.client.ApiServiceName.SOURCE_SERVICE
import org.turter.patrocl.data.remote.client.ApiServiceName.STOP_LIST_SERVICE

object ApiServiceName {
//    val STATION_CONNECTOR = "station-connector"
    val SOURCE_SERVICE = "source-service/api/v1"
    val ORDER_SERVICE = "order-service/api/v1"
    val ORGANIZATION_SERVICE = "organization-service"
    val NOTIFICATION_SERVICE = "notification-service"
    val STOP_LIST_SERVICE = "stoplist-service/api/v1"
}

//TODO актуализировать эндпоинты
object ApiEndpoint {
    private val API_BASE_URL_HTTP = "http://92.255.107.65:8765"
//    private val API_BASE_URL_WS = "ws://192.168.0.105:18765"

    object Menu {
        fun getCategoryTree() = "$API_BASE_URL_HTTP/$SOURCE_SERVICE/menu/category-list/tree"
        fun getModifiersGroupTree() = "$API_BASE_URL_HTTP/$SOURCE_SERVICE/menu/modifiers-groups/tree"
        fun getDishes() = "$API_BASE_URL_HTTP/$SOURCE_SERVICE/dish/list"
        fun getModifiers() = "$API_BASE_URL_HTTP/$SOURCE_SERVICE/modifier/list"
    }

    object StopList {
        fun getStopList() = "$API_BASE_URL_HTTP/$STOP_LIST_SERVICE/stop-list/list"
        fun getStopListFlow() = "$API_BASE_URL_HTTP/$STOP_LIST_SERVICE/stop-list/flow"
        fun createStopListItem() = "$API_BASE_URL_HTTP/$STOP_LIST_SERVICE/stop-list"
        fun editStopListItem() = "$API_BASE_URL_HTTP/$STOP_LIST_SERVICE/stop-list"
        fun removeStopListItem(id: String) =
            "$API_BASE_URL_HTTP/$STOP_LIST_SERVICE/stop-list/$id".encodeURLPath()
        fun removeStopListItems() = "$API_BASE_URL_HTTP/$STOP_LIST_SERVICE/stop-list/items/remove"
    }

    object Hall {
        fun getTables() = "$API_BASE_URL_HTTP/$SOURCE_SERVICE/table/list"
    }

    object Order {
        fun getOrder(guid: String) = "$API_BASE_URL_HTTP/$ORDER_SERVICE/order/$guid".encodeURLPath()
        fun getOpenedOrdersList() = "$API_BASE_URL_HTTP/$ORDER_SERVICE/order/list/opened"
        fun getOpenedOrdersListFlow() = "$API_BASE_URL_HTTP/$ORDER_SERVICE/order/flow/opened"
        fun createOrder() = "$API_BASE_URL_HTTP/$ORDER_SERVICE/order/create"
        fun addItemsToOrder() = "$API_BASE_URL_HTTP/$ORDER_SERVICE/order/session/items/add"
        fun removeItemsFromOrder() = "$API_BASE_URL_HTTP/$ORDER_SERVICE/order/session/items/remove"
//        fun updateOrder(guid: String) = "$API_BASE_URL_HTTP/$ORDER_SERVICE/order/$guid".encodeURLPath()
//        fun removeItem(guid: String) = "$API_BASE_URL_HTTP/$ORDER_SERVICE/order/$guid/remove-item".encodeURLPath()
//        fun getWebSocketOrderList() = "$API_BASE_URL_WS/$STATION_CONNECTOR/order/list/ws"
    }

    object Employee {
        fun getOwnEmployee() = "$API_BASE_URL_HTTP/$ORGANIZATION_SERVICE/employee/own"
        fun editOwnEmployee() = "$API_BASE_URL_HTTP/$ORGANIZATION_SERVICE/employee/own"
    }

    object Waiter {
        fun getOwnWaiter() = "$API_BASE_URL_HTTP/$ORDER_SERVICE/waiter/own"
//        fun editOwnEmployee() = "$API_BASE_URL_HTTP/$STATION_CONNECTOR/waiter/own"
    }
}