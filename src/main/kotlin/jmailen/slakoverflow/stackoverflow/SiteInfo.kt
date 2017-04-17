package jmailen.slakoverflow.stackoverflow

data class SiteInfo(val items: ArrayList<SiteInfoItem>, val quota_max: Int, val quota_remaining: Int, val has_more: Boolean)

data class SiteInfoItem(val new_active_users: Int,
                        val total_users: Int,
                        val total_questions: Int,
                        val total_answers: Int,
                        val total_accepted: Int,
                        val total_unanswered: Int)
