package `fun`.inaction.transfer.bean

abstract class TransferItem(val type:TransferType) {

    var status = TransferStatus.WAIT;



}

enum class TransferStatus {
    WAIT,TRANSFER,COMPLETE;
}

enum class TransferType {
    MSG, FILE;
}