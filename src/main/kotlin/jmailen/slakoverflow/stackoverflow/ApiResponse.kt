package jmailen.slakoverflow.stackoverflow

import java.util.ArrayList

open class ApiResponse<T>(val items: ArrayList<T>, val quota_max: Int, val quota_remaining: Int, val has_more: Boolean)
