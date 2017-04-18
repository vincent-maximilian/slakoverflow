package jmailen.slakoverflow.stackoverflow

import java.util.*

data class SearchExcerpts(val items: ArrayList<SearchExcerpt>, val quota_max: Int, val quota_remaining: Int, val has_more: Boolean)

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
