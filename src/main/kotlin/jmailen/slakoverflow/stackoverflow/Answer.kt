package jmailen.slakoverflow.stackoverflow

import java.util.*
import kotlin.collections.ArrayList

class Answers(items: ArrayList<Answer>, quota_max: Int, quota_remaining: Int, has_more: Boolean):
        ApiResponse<Answer>(items, quota_max, quota_remaining, has_more)

data class Answer(val answer_id: Int, val is_accepted: Boolean, val score: Int, val creation_date: Date)
