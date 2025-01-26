package org.turter.patrocl.domain.model.menu

data class ModifiersGroupDetailed(
    val id: String,
    val guid: String,
    val code: String,
    val name: String,
    val status: String,
    val parent: ModifiersGroupDetailed?,
    var childList: List<ModifiersGroupDetailed>,
    val modifiers: List<DishModifier>
) {
    override fun toString(): String {
        return "ModifiersGroupDetailed(" +
                "id='$id', " +
                "guid='$guid', " +
                "code='$code', " +
                "name='$name', " +
                "status='$status', " +
                "parent=${parent?.id}, " +
                "childList=$childList, " +
                "modifiers=$modifiers" +
                ")"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ModifiersGroupDetailed

        if (id != other.id) return false
        if (guid != other.guid) return false
        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + guid.hashCode()
        result = 31 * result + code.hashCode()
        return result
    }
}