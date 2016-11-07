# Rec

**Rec** is a simple framework to check and transform data record files(like CSV).
It provides several DSL to describe the relationships and transformation rules
 to check the data correctness and integrity.

## Example

Say if you have following data file:

person.txt
```csv
Kimmy | Leo | 1999/99/99 | self | 1000001 |  not married | this is just a test data
Kimbryo | Leo | 1999/99/99 | brother | 1000002 | not married | this is just a test data
Kim | Leo | 1999/99/99 | brother | 1000003 | married | this is just a test data
Lisa | Leo | 1999/99/99 | sister | 1000004 | not married | this is just a test data
Amanda | Leo | 1999/99/99 | cusion | 1000005 | married | this is just a test data
```

salary.txt
```csv
1000001 | 10k
1000002 | 1000k
1000008 | 3t
```

if you provide following rec files, it will check and tell you the details:

person.txt.rec
```
Person
delimiter=|
escape="
key=ID
format=first name, last name, {2}, ID, ..., comment
```
salary.txt.rec
```
Salary
delimiter=|
escape="
format=pid, amount
```

Rules:
```
unique: Person[first name]
unique: Person.ID
exist: Salary.pid, Person.ID
uinque: Person.comment
```

when you run the jar, you will see following outputs:
```
1000008 of Salary[pid] cannot be found in Person[ID]
duplicate record found with comment: this is just a test data
duplicate record found with comment: this is just a test data
duplicate record found with comment: this is just a test data
duplicate record found with comment: this is just a test data
duplicate record found with comment: this is just a test data
```

## TODO

* Rec transformer
* More constraints
* API to java and groovy
* Annotation binding support for Java beans
* Typing

see doc/design.md for details.