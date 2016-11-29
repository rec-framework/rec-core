package net.kimleo.rec.rule.impl

import net.kimleo.rec.repository.RecordSet
import net.kimleo.rec.rule.RecRule
import net.kimleo.rec.rule.Result

class Exist: RecRule {
    override fun verify(recs: List<RecordSet>): Pair<Boolean, List<Result>> {
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
                    override val details = "${el.text} of ${image.config.name} cannot be found in ${complete.config.name}"
                })
            }
        }

        return Pair(successful, result)
    }
}