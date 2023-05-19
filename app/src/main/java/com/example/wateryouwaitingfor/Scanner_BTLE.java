package com.example.wateryouwaitingfor;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.ArrayList;

/**
 * A class for handling the
 * scanning of new BTLE_Devices
 */
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

    /**
     * Initiates or Stops BTLE scans
     * @param enable enables the scanner
     */
    @SuppressLint("MissingPermission")
    private void scanLeDevice(final boolean enable) {
        final BluetoothLeScanner mBluetoothScanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (enable && !mScanning) {
            Log.d("Scanner_BTLE", "Starting BLE scan...");

            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(() -> {
                Log.d("Scanner_BTLE", "Stopping BLE scan...");

                mScanning = false;
                mBluetoothScanner.stopScan(mLeScanCallback);

                ma.stopScan();
            }, scanPeriod);

            mScanning = true;

            ScanFilter.Builder filterBuilder = new ScanFilter.Builder();
            filterBuilder.setServiceUuid(new ParcelUuid(MainActivity.SERVICE_UUID));

            ScanSettings.Builder settingsBuilder = new ScanSettings.Builder();
            settingsBuilder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);

            ArrayList<ScanFilter> scanFilters = new ArrayList<>();

            scanFilters.add(filterBuilder.build());
            mBluetoothScanner.startScan(scanFilters, settingsBuilder.build(), mLeScanCallback);
        }
        else {
            mScanning = false;
            mBluetoothScanner.stopScan(mLeScanCallback);
        }
    }
}