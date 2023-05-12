package com.example.wateryouwaitingfor;


import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class Scanner_BTLE {

    private final MainActivity ma;

    private final BluetoothAdapter mBluetoothAdapter;
    private final ScanCallback mLeScanCallback = new ScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            final int rssi = result.getRssi();
            if (rssi > signalStrength) {
                mHandler.post(() -> ma.addDevice(result.getDevice(), rssi));
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("SCAN ERROR", String.valueOf(errorCode));
            super.onScanFailed(errorCode);
        }
    };
    private boolean mScanning;
    private final Handler mHandler;

    private final long scanPeriod;
    private final int signalStrength;



    public Scanner_BTLE(MainActivity mainActivity, long scanPeriod, int signalStrength) {
        ma = mainActivity;

        mHandler = new Handler(Looper.myLooper());

        this.scanPeriod = scanPeriod;
        this.signalStrength = signalStrength;

        final BluetoothManager bluetoothManager =
                (BluetoothManager) ma.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

    }

    public boolean isScanning() {
        return mScanning;
    }


    /**
     * Initiates a scan for new BTLE_Devices
     */
    public void start() {
        if (!Utils.checkBluetooth(mBluetoothAdapter)) {
            Utils.requestUserBluetooth(ma);
            ma.stopScan();
        } else {
            scanLeDevice(true);
        }
    }

    /**
     * Stops the current scan
     */
    public void stop() {
        scanLeDevice(false);
    }

    // If you want to scan for only specific types of peripherals,
    // you can instead call startLeScan(UUID[], BluetoothAdapter.LeScanCallback),
    // providing an array of UUID objects that specify the GATT services your app supports.


    @SuppressLint("MissingPermission")
    private void scanLeDevice(final boolean enable) {
        final BluetoothLeScanner mBluetoothScanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (enable && !mScanning) {
            Utils.toast(ma.getApplicationContext(), "Starting BLE scan...");

            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(() -> {
                Utils.toast(ma.getApplicationContext(), "Stopping BLE scan...");

                mScanning = false;
                mBluetoothScanner.stopScan(mLeScanCallback);

                ma.stopScan();
            }, scanPeriod);

            mScanning = true;
            mBluetoothScanner.startScan(mLeScanCallback);

            Log.e("PERMS: FINE LOCATION", String.valueOf(ActivityCompat.checkSelfPermission(ma.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED));
            Log.e("PERMS: COARSE LOCATION", String.valueOf(ActivityCompat.checkSelfPermission(ma.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED));
            Log.e("PERMS: BLUETOOTH", String.valueOf(ActivityCompat.checkSelfPermission(ma.getApplicationContext(), Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED));
            Log.e("PERMS: BLUETOOTH SCAN", String.valueOf(ActivityCompat.checkSelfPermission(ma.getApplicationContext(), Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED));
            Log.e("PERMS: BLUETOOTH ADMIN", String.valueOf(ActivityCompat.checkSelfPermission(ma.getApplicationContext(), Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED));
            Log.e("PERMS: BACKGROUND LOCATION", String.valueOf(ActivityCompat.checkSelfPermission(ma.getApplicationContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED));
        }
        else {
            mScanning = false;
            mBluetoothScanner.stopScan(mLeScanCallback);
        }
    }
}