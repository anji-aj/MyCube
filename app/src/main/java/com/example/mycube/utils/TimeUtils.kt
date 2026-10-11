package com.example.mycube.utils

fun formatTime(millis: Long): String {
    val hundredths = (millis % 1000) / 10
    val seconds = (millis / 1000) % 60
    val minutes = (millis / 60000)
    return if (minutes > 0) {
        "%d:%02d.%02d".format(minutes, seconds, hundredths)
    } else {
        "%02d.%02d".format(seconds, hundredths)
    }
}
