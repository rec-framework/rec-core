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

if you provide following net.kimleo.rec files, it will check and tell you the details:

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

Rules(default.rule):
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

## Rec file

Rec file is contains parameters for data files, and you can
use in future to process the data files.

The Rec file format is:
```Rec
<Record Name>
delimiter=<the delimiter>
escape=<the escape char>
key=<optional; the key of record; must be one of the format field>
format=<the format of the rec file, must seperate by comma>
```

## Initializer

Rec provide a initializer to generate your *.rec file by
provide some parameters.

Just run as followings:
```shell
java -jar rec-app.jar init <data file> [<name>=<value>]
```
The names and values you provided will added to the .rec file, 
if you didn't provide enough parameter, there will be a
prompt for you to complete them.

## Format

Format is for generating the accessor for records.

By default, you should provide the field name which you want to
use in future analysis(the rules or the scripts).

It is called format because it has some useful conventions to help
you to get correct field as easy as you can.

Following introduction is based on this record file:
```Rec
Kimmy, Leo, 12, 1999/99/99, male, "Chengdu, Sichuan, China", Software Engineer
```

#### Fields

Field in a format is just a field name, which can contains space
or enclosed by double quotes, this is matching to related position(column).

So for the record above, this format can extract it's first name and last name

```
first name, last name
```

#### Paddings

Usually we wont care about too much details in the data file, like if
we only focused the person's name and his address, we can just ignore
other fields, you can using padding to **skip** these unnecessary
fields.

```
first name, last name, {3}, address
```

`{3}` here means there are 3 fields which just can be ignored.

#### Placeholder

This is another useful convention when you need lookup the field
reversely.

For example, if we only care about the person and his job title,
then we can just using this convention:
```
first name, last name, ..., job title
```
Here, the `...` means, whatever fields between last field and first
2 fields, just ignore them.

also you can add more reversed field, then it will work as expected:
```
first name, last name, ..., job title
```

and even combine everything together:
```
first name, {3}, gender, ..., dob, {2}, job title
```

It seems weird that we can see there are only 7 field in the data
record but 9 (exclude `...`) in total of format.

But currently it is OK. what Rec will do is using the Placeholder (`...`)
as a separator of ordered and reverse-ordered field accessor, so for this
format it only requires not less than 5 fields-long data record, i.e. there
can be overlaps on both side of `...`.

## Rules

Rules is for analysis the data files. Currently there are only following
rules:

  - Unique Rule, to check if a group of data fields is unique
  - Exist Rule, to check if a data field is included in another set

Rules following the format:
```$xslt
<Rule Name>: [<Query>]
```

You can also add more rule and register to rule factory.

We've planed a more powerful tools: scripts, to do more extensible and
efficient analysis stuff.

## Query

[TODO]

## Script

Currently there are bare kotlin scripting support and some groovy scripting
support.

For groovy, you can see `rec-core/src/test/GroovyScripting.groovy` for example.

And for kotlin, there is a inner module `kotlin-api` to provide more detailed api.

## TODO

* Rec transformer
* More constraints
* API to java and groovy
* Annotation binding support for Java beans
* Typing

see doc/design.md for details.