package org.turter.patrocl.data_mock.utils

import org.turter.patrocl.domain.model.menu.Category
import org.turter.patrocl.domain.model.menu.Dish
import org.turter.patrocl.domain.model.menu.DishModifier
import org.turter.patrocl.domain.model.menu.ModifiersGroup

object MenuDataSupplier {

    fun getDishList() = listOf(
        Dish(
            id = "dish-id-1",
            guid = "dish-guid-1",
            code = "dish-code-1",
            name = "Цезарь с курицей",
            status = "ok",
            mainParentIdent = "category-id-1"
        ),
        Dish(
            id = "dish-id-2",
            guid = "dish-guid-2",
            code = "dish-code-2",
            name = "Салат с куриной печенью",
            status = "ok",
            mainParentIdent = "category-id-1"
        ),
        Dish(
            id = "dish-id-3",
            guid = "dish-guid-3",
            code = "dish-code-3",
            name = "Буритто",
            status = "ok",
            mainParentIdent = "category-id-2"
        ),
        Dish(
            id = "dish-id-4",
            guid = "dish-guid-4",
            code = "dish-code-4",
            name = "Куриная отбивная",
            status = "ok",
            mainParentIdent = "category-id-2"
        ),
        Dish(
            id = "dish-id-5",
            guid = "dish-guid-5",
            code = "dish-code-5",
            name = "Греча",
            status = "ok",
            mainParentIdent = "category-id-3"
        ),
        Dish(
            id = "dish-id-6",
            guid = "dish-guid-6",
            code = "dish-code-6",
            name = "Рис",
            status = "ok",
            mainParentIdent = "category-id-3"
        ),
        Dish(
            id = "dish-id-7",
            guid = "dish-guid-7",
            code = "dish-code-7",
            name = "Картофель айдахо",
            status = "ok",
            mainParentIdent = "category-id-3"
        )
    )

    fun getCategory() = Category(
        id = "category-id-0",
        guid = "category-guid-0",
        code = "category-code-0",
        name = "Пивная кружка ROOT",
        status = "ok",
        mainParentIdent = "category-id-x",
        childList = listOf(
            Category(
                id = "category-id-1",
                guid = "category-guid-1",
                code = "category-code-1",
                name = "Салаты",
                status = "ok",
                mainParentIdent = "category-id-0",
                childList = listOf(),
                dishIdList = listOf("dish-id-1", "dish-id-2")
            ),
            Category(
                id = "category-id-01",
                guid = "category-guid-01",
                code = "category-code-01",
                name = "Основные блюда",
                status = "ok",
                mainParentIdent = "category-id-0",
                childList = listOf(
                    Category(
                        id = "category-id-2",
                        guid = "category-guid-2",
                        code = "category-code-2",
                        name = "Горячее",
                        status = "ok",
                        mainParentIdent = "category-id-01",
                        childList = listOf(),
                        dishIdList = listOf("dish-id-3", "dish-id-4")
                    ),
                    Category(
                        id = "category-id-3",
                        guid = "category-guid-3",
                        code = "category-code-3",
                        name = "Гарниры",
                        status = "ok",
                        mainParentIdent = "category-id-01",
                        childList = listOf(),
                        dishIdList = listOf("dish-id-5", "dish-id-6", "dish-id-7")
                    )
                ),
                dishIdList = listOf()
            )
        ),
        dishIdList = listOf()
    )

    fun getModifierList() = listOf(
        DishModifier(
            id = "modifier-id-1",
            guid = "modifier-guid-1",
            code = "modifier-code-1",
            name = "В ОДНУ ТАРЕЛКУ",
            status = "ok",
            mainParentIdent = "modifiers-group-id-1"
        ),
        DishModifier(
            id = "modifier-id-2",
            guid = "modifier-guid-2",
            code = "modifier-code-2",
            name = "ЗАМЕНА",
            status = "ok",
            mainParentIdent = "modifiers-group-id-1"
        ),
        DishModifier(
            id = "modifier-id-3",
            guid = "modifier-guid-3",
            code = "modifier-code-3",
            name = "НЕ ГОТОВИТЬ",
            status = "ok",
            mainParentIdent = "modifiers-group-id-2"
        ),
        DishModifier(
            id = "modifier-id-4",
            guid = "modifier-guid-4",
            code = "modifier-code-4",
            name = "БЕЗ ХОЛОПЕНЬО",
            status = "ok",
            mainParentIdent = "modifiers-group-id-2"
        ),
        DishModifier(
            id = "modifier-id-5",
            guid = "modifier-guid-5",
            code = "modifier-code-5",
            name = "Не зажаривать",
            status = "ok",
            mainParentIdent = "modifiers-group-id-2"
        )
    )

    fun getModifiersGroup() = ModifiersGroup(
        id = "modifiers-group-id-0",
        guid = "modifiers-group-id-0",
        code = "modifiers-group-id-0",
        name = "Пивная кружка ROOT",
        status = "ok",
        mainParentIdent = "modifiers-group-id-x",
        childList = listOf(
            ModifiersGroup(
                id = "modifiers-group-id-1",
                guid = "modifiers-group-id-1",
                code = "modifiers-group-id-1",
                name = "Зал",
                status = "ok",
                mainParentIdent = "modifiers-group-id-0",
                childList = listOf(),
                modifierIdList = listOf("modifier-id-1", "modifier-id-2")
            ),
            ModifiersGroup(
                id = "modifiers-group-id-2",
                guid = "modifiers-group-id-2",
                code = "modifiers-group-id-2",
                name = "ОБЩЕЕ",
                status = "ok",
                mainParentIdent = "modifiers-group-id-0",
                childList = listOf(),
                modifierIdList = listOf("modifier-id-3", "modifier-id-4", "modifier-id-5")
            )
        ),
        modifierIdList = listOf()
    )

}














