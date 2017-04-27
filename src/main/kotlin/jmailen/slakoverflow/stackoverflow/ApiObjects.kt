package jmailen.slakoverflow.stackoverflow

import java.util.ArrayList
import java.util.Date

open class ApiResponse<T>(val items: ArrayList<T>, val quota_max: Int, val quota_remaining: Int, val has_more: Boolean)

class SiteInfos(items: ArrayList<SiteInfo>, quota_max: Int, quota_remaining: Int, has_more: Boolean):
        ApiResponse<SiteInfo>(items, quota_max, quota_remaining, has_more)

data class SiteInfo(val new_active_users: Int,
                    val total_users: Int,
                    val total_questions: Int,
                    val total_answers: Int,
                    val total_accepted: Int,
                    val total_unanswered: Int)

class SearchResults(items: ArrayList<SearchResultExcerpt>, quota_max: Int, quota_remaining: Int, has_more: Boolean):
        ApiResponse<SearchResultExcerpt>(items, quota_max, quota_remaining, has_more)

data class SearchResultExcerpt(val question_id: Int,
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

class Answers(items: ArrayList<Answer>, quota_max: Int, quota_remaining: Int, has_more: Boolean):
        ApiResponse<Answer>(items, quota_max, quota_remaining, has_more)

data class Answer(val answer_id: Int, val is_accepted: Boolean, val score: Int, val creation_date: Date = Date())
