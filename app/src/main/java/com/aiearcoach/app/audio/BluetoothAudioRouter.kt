package com.aiearcoach.app.audio

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.media.AudioManager
import android.util.Log

class BluetoothAudioRouter(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var bluetoothHeadset: BluetoothHeadset? = null

    private val profileListener = object : BluetoothProfile.ServiceListener {
        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile?) {
            if (profile == BluetoothProfile.HEADSET) {
                bluetoothHeadset = proxy as BluetoothHeadset
                Log.d("BluetoothAudioRouter", "Bluetooth Headset Connected")
            }
        }

        override fun onServiceDisconnected(profile: Int) {
            if (profile == BluetoothProfile.HEADSET) {
                bluetoothHeadset = null
                Log.d("BluetoothAudioRouter", "Bluetooth Headset Disconnected")
            }
        }
    }

    init {
        BluetoothAdapter.getDefaultAdapter()?.getProfileProxy(context, profileListener, BluetoothProfile.HEADSET)
    }

    fun startBluetoothSco() {
        if (audioManager.isBluetoothScoAvailableOffCall) {
            audioManager.startBluetoothSco()
            audioManager.isBluetoothScoOn = true
            Log.d("BluetoothAudioRouter", "SCO Started")
        }
    }

    fun stopBluetoothSco() {
        audioManager.stopBluetoothSco()
        audioManager.isBluetoothScoOn = false
        Log.d("BluetoothAudioRouter", "SCO Stopped")
    }

    fun isBluetoothConnected(): Boolean {
        return bluetoothHeadset?.connectedDevices?.isNotEmpty() ?: false
    }

    fun dispose() {
        BluetoothAdapter.getDefaultAdapter()?.closeProfileProxy(BluetoothProfile.HEADSET, bluetoothHeadset)
    }
}
