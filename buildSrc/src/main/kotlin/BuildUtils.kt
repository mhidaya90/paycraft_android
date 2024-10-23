import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun dateTime(): String =
    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))