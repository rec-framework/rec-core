package net.kimleo.rec

data class Field(val value: String)

data class Tuple(val fields: List<Field>)


data class MappedTuple(val header: Tuple, val tuple: Tuple) {
    val values = hashMapOf<String, String>()
    init {
        assert(header.fields.size == tuple.fields.size)
        for (i in 0 .. header.fields.size - 1) {
            values.put(header.fields[i].value, tuple.fields[i].value)
        }
    }
    fun get(name: String): String? {
        return values.get(name)
    }
}

fun <T, U> T?.bind(fn: (T) -> U?): U? =
    if (this == null) null else fn.invoke(this)

fun <T> T?.orElse(fn: () -> T) =
        if (this != null)  this else fn.invoke()