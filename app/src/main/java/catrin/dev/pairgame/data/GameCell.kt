package catrin.dev.pairgame.data

data class GameCell (
    var card : GameCard? = null,
    var isOpened : Boolean = false,
    var fadeStartTimeMs: Long? = null
)