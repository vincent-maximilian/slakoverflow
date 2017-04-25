package jmailen.slakoverflow.stackoverflow

import java.util.*

class SearchExcerpts(items: ArrayList<SearchExcerpt>, quota_max: Int, quota_remaining: Int, has_more: Boolean):
        ApiResponse<SearchExcerpt>(items, quota_max, quota_remaining, has_more)

data class SearchExcerpt(val question_id: Int,
                         val item_type: SearchResultType,
                         val question_score: Int,
                         val score: Int,
                         val is_answered: Boolean,
                         val is_accepted: Boolean,
                         val title: String,
                         val body: String,
                         val excerpt: String,
                         val last_activity_date: Date = Date(),
                         val creation_date: Date = Date())

enum class SearchResultType {
    answer,
    question
}
