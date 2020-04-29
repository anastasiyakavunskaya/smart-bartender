package com.example.user.bartender.bluetooth

import android.bluetooth.BluetoothSocket
import android.util.Log


class BluetoothClient(private val socket: BluetoothSocket, private val outputString: String): Thread() {

    override fun run() {
        Log.i("bluetooth", "Connecting")
        this.socket.connect()

        Log.i("bluetooth", "Sending")
        val outputStream = this.socket.outputStream
        val inputStream = this.socket.inputStream
        try {
            outputStream.write(outputString.toByteArray())
            outputStream.flush()
            Log.i("bluetooth", "Sent")
        } catch(e: Exception) {
            Log.e("bluetooth", "Cannot send", e)
        } finally {
            outputStream.close()
            inputStream.close()
            this.socket.close()
        }
    }
}