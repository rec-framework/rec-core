package net.kimleo.rec

interface RowMapper<Row, T> {
    fun map(tuple: Row): T
}

class PropertyRowMapper<T>(val clazz: Class<T>): RowMapper<MappedTuple, T> {

    override fun map(tuple: MappedTuple): T {
        val instance = clazz.newInstance()
        val fields = clazz.declaredFields
        for (field in fields) {
            val accessible = field.isAccessible
            field.isAccessible = true
            field.set(instance, tuple.get(field.name))
            field.isAccessible = accessible
        }

        return instance
    }

}