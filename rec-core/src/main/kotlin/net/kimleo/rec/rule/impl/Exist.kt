package net.kimleo.rec.rule.impl

import net.kimleo.rec.loader.RecCollection
import net.kimleo.rec.rule.RecRule
import net.kimleo.rec.rule.Result

class Exist: RecRule {
    override fun verify(recs: List<RecCollection>): Pair<Boolean, List<Result>> {
        assert(recs.size == 2)

        var successful = true
        val result = arrayListOf<Result>()

        val image = recs[0]
        val complete = recs[1]

        val complete_set = complete.toSet()


        for (el in image) {
            if (!complete_set.contains(el)) {
                successful = false
                result.add(object: Result {
                    override val details = "${el.text} of ${image.type.name} cannot be found in ${complete.type.name}"
                })
            }
        }

        return Pair(successful, result)
    }
}