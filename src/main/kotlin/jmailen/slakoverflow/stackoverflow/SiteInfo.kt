package jmailen.slakoverflow.stackoverflow

data class SiteInfos(val items: ArrayList<SiteInfo>, val quota_max: Int, val quota_remaining: Int, val has_more: Boolean)

data class SiteInfo(val new_active_users: Int,
                    val total_users: Int,
                    val total_questions: Int,
                    val total_answers: Int,
                    val total_accepted: Int,
                    val total_unanswered: Int)
