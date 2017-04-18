package jmailen.slakoverflow.stackoverflow

import java.util.*

class SearchExcerpts(items: ArrayList<SearchExcerpt>, quota_max: Int, quota_remaining: Int, has_more: Boolean):
        ApiResponse<SearchExcerpt>(items, quota_max, quota_remaining, has_more)

data class SearchExcerpt(val question_score: Int,
                         val is_accepted: Boolean,
                         val is_answered: Boolean,
                         val question_id: Int,
                         val item_type: SearchResultType,
                         val score: Int,
                         val last_activity_date: Date,
                         val creation_date: Date,
                         val body: String,
                         val excerpt: String,
                         val title: String)

enum class SearchResultType {
    answer,
    question
}
