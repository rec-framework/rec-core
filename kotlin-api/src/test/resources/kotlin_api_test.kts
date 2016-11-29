import net.kimleo.rec.api.*

val repo = load(".")

class Person {
    var firstName = ""
    var lastName = ""

    override fun toString(): String {
        return "Person[$firstName $lastName]"
    }
}

repo.from("Person")
        .map(Person::class)
        .forEach { println("${it.firstName} ${it.lastName}") }

repo.map(Person::class)
        .forEach { println("${it.firstName} ${it.lastName}") }

repo.rule(name = "minimum length", on = Person::class) {
    it.firstName.length > 3
}