import parse.ParseResult

sealed class ParseStatus {
    object NotStarted : ParseStatus()
    data class Proceeding(
        val percent: Int
    ) : ParseStatus()
    class Completed(
        val parseResult: ParseResult,
        val logManager: LogManager
    ) : ParseStatus()
}
