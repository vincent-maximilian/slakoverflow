package jmailen.slakoverflow.stackoverflow

class SiteInfos(items: ArrayList<SiteInfo>, quota_max: Int, quota_remaining: Int, has_more: Boolean):
        ApiResponse<SiteInfo>(items, quota_max, quota_remaining, has_more)

data class SiteInfo(val new_active_users: Int,
                    val total_users: Int,
                    val total_questions: Int,
                    val total_answers: Int,
                    val total_accepted: Int,
                    val total_unanswered: Int)
