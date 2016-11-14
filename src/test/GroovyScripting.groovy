import net.kimleo.rec.API

def record = API.rec("Kimmy, Leo, 1999/99/99, hello world")
def accessor = API.accessor("first name", "last name", "dob", "comment")

def object = accessor.of(record)

assert object.dob == "1999/99/99"
assert object."first name" == "Kimmy"

println(object.comment)

def recs = API.collect([record], API.type("Friend", "first name, last name, dob, comment"))

println(recs."first name"[0])

def repo = API.repo([recs])

println(repo."Friend[first name, dob, comment]"[0]."first name".first())