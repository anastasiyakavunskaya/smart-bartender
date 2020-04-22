package com.example.user.bartender.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import com.example.user.bartender.MainActivity
import java.io.IOException
import java.util.*

private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
private const val address = "98:D3:34:90:A2:F1"

class BluetoothController(private val activity: MainActivity): Thread() {

    private var cancelled: Boolean
    private val socket: BluetoothSocket?


    init {
        val btAdapter = BluetoothAdapter.getDefaultAdapter()
        if (btAdapter != null) {
            val btDevice = btAdapter.getRemoteDevice(address)
            //создание сервер сокета uuid
            this.socket = btDevice.createInsecureRfcommSocketToServiceRecord(uuid)
            this.cancelled = false
        } else {
            this.socket = null
            this.cancelled = true
        }

    }

    override fun run() {
        while(true) {
            if (this.cancelled) {
                break
            }
            try {
                socket!!.connect()
                BluetoothClient(socket).start()
            } catch(e: IOException) {
                break
            }
        }
    }

    fun cancel() {
        this.cancelled = true
        this.socket!!.close()
    }
}