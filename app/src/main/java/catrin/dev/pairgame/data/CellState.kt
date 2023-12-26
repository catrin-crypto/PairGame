package catrin.dev.pairgame.data

sealed class CellState(val cellIndex : Int,val gameCell : GameCell) {
    class OpenedCell(cellIndex: Int,gameCell: GameCell) : CellState(cellIndex,gameCell)
    class QueryingCell(cellIndex: Int,gameCell: GameCell) : CellState(cellIndex,gameCell)
}