import net.kimleo.rec.api.*

val repo = load(".")

class Person {
    var firstName = ""
    var lastName = ""
}

repo.from("Person")
        .map(Person::class)
        .forEach { println("${it.firstName} ${it.lastName}") }