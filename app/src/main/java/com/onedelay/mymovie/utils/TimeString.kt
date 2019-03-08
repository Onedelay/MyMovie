package com.onedelay.mymovie.utils

object TimeString {
    private const val SEC = 1000L
    private const val MIN = 60 * SEC
    private const val HOUR = 60 * MIN
    private const val DAY = 24 * HOUR
    private const val MONTH = 30 * DAY
    private const val YEAR = 12 * MONTH

    fun formatTimeString(regTime: Long): String {
        val curTime = System.currentTimeMillis()
        val diffTime = curTime - regTime

        return when {
            diffTime < TimeString.MIN -> "방금"
            diffTime < TimeString.HOUR -> "${diffTime / TimeString.MIN}분전"
            diffTime < TimeString.DAY -> "${diffTime / TimeString.HOUR}시간전"
            diffTime < TimeString.MONTH -> "${diffTime / TimeString.DAY}일전"
            diffTime < TimeString.YEAR -> "${diffTime / TimeString.MONTH}달전"
            else -> "${diffTime / TimeString.YEAR}년전"
        }
    }
}