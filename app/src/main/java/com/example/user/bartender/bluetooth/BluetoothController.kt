package com.example.user.bartender.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import java.io.IOException
import java.util.*

private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
private const val address = "98:D3:34:90:A2:F1"

class BluetoothController(adapter: BluetoothAdapter, private val outputString: String): Thread() {

    private var cancelled: Boolean = false
    var socket: BluetoothSocket? = null


    init {
        try {
            val btDevice = adapter.getRemoteDevice(address)
            //создание сервер сокета uuid
            this.socket = btDevice.createInsecureRfcommSocketToServiceRecord(uuid)
            this.cancelled = false
        }catch (e: IllegalArgumentException){}

    }

    override fun run() {
        while(true) {
            if (this.cancelled) {
                break
            }
            try {
                socket!!.connect()
                BluetoothClient(socket!!, outputString).start()


            } catch(e: IOException) {
                break
            }
        }
    }
}