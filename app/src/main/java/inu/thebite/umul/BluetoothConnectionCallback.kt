package inu.thebite.umul

//Service에서 MainActivity의 함수를 사용하기 위한 인터페이스
interface BluetoothConnectionCallback {
    fun connecting()
    fun disconnecting()
}