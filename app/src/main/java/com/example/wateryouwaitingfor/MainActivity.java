package com.example.wateryouwaitingfor;

import static android.bluetooth.BluetoothAdapter.STATE_CONNECTED;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.example.wateryouwaitingfor.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    ActivityMainBinding binding;
    public static final String SHARED_PREFS = "com.example.wateryouwaitingfor.shared_preferences";
    private final static String TAG = MainActivity.class.getSimpleName();

    public static final int REQUEST_ENABLE_BT = 1;
    public static final int BTLE_SERVICES = 2;


    private SharedPreferences sharedpreferences;
    private String deviceName, deviceAddress;

    private HashMap<String, BTLE_Device> mBTDevicesHashMap;
    private ArrayList<BTLE_Device> mBTDevicesArrayList;
    private ListAdapter_BTLE_Devices adapter;

    UUID SERVICE_UUID = convertFromInteger(0x181C);
    UUID CHAR_UUID = convertFromInteger(0x2A37);

    private BroadcastReceiver_BTState mBTStateUpdateReceiver;
    private Scanner_BTLE mBTLeScanner;
    private ActivityResultLauncher<Intent> someActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        checkPermissions(this, getApplicationContext());
        sharedpreferences = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        replaceFragment((new HomeFragment()));

        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Utils.toast(getApplicationContext(), "BLE not supported");
            finish();
        }

        mBTStateUpdateReceiver = new BroadcastReceiver_BTState(getApplicationContext());
        mBTLeScanner = new Scanner_BTLE(this, 5000, -75);

        mBTDevicesHashMap = new HashMap<>();
        mBTDevicesArrayList = new ArrayList<>();

        Log.e("DEVICES IN MAIN", mBTDevicesArrayList.toString());

        adapter = new ListAdapter_BTLE_Devices(this, R.layout.btle_device_list_item, mBTDevicesArrayList);

        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            Utils.toast(getApplicationContext(), "Thank you for turning on Bluetooth");
                        }
                        else if (result.getResultCode() == Activity.RESULT_CANCELED){
                            Utils.toast(getApplicationContext(), "Please turn on Bluetooth");
                        }
                    }
                });

        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().findItem(R.id.home).setChecked(true);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch(item.getItemId()){

                case R.id.contamination:
                    replaceFragment(new ContaminationFragment());
                    break;
                case R.id.stats:
                    replaceFragment(new StatsFragment());
                    break;
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.friends:
                    replaceFragment(new FriendsFragment());
                    break;
                case R.id.settings:
                    replaceFragment(new SettingsFragment());
                    break;

            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();

        registerReceiver(mBTStateUpdateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

        stopScan();
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(mBTStateUpdateReceiver);
        stopScan();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Context context = view.getContext();

        stopScan();

        BTLE_Device currentDevice = mBTDevicesArrayList.get(position);

        deviceName = currentDevice.getName();
        deviceAddress = currentDevice.getAddress();

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("currentDeviceName", deviceName);
        editor.putString("currentDeviceAddress", deviceAddress);
        editor.apply();

        BluetoothGatt gatt;
        BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                if (newState == STATE_CONNECTED){
                    gatt.discoverServices();
                }
            }

            @Override
            // New services discovered
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    BluetoothGattService mBluetoothGattService = gatt.getService(SERVICE_UUID);
                    if (mBluetoothGattService != null) {
                        Log.i(TAG, "Service characteristic UUID found: " + mBluetoothGattService.getUuid().toString());
                    } else {
                        Log.i(TAG, "Service characteristic not found for UUID: " + SERVICE_UUID);
                    }
                }
                else{
                    Log.e(TAG, "Gatt failed");
                }
            }
        };

        gatt = currentDevice.connect(gattCallback);

//        Log.e("Characteristic MAYBE: ", gatt.getServices().toString());
//        Log.e("Characteristic MAYBE: ", gatt.getDevice().getName());

//        Intent intent = new Intent(this, Activity_BTLE_Services.class);
//        intent.putExtra(Activity_BTLE_Services.EXTRA_NAME, name);
//        intent.putExtra(Activity_BTLE_Services.EXTRA_ADDRESS, address);
//        someActivityResultLauncher.launch(intent);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_scan:
                Utils.toast(getApplicationContext(), "Scan Button Pressed");
                replaceFragment(new DeviceListFragment());

                if (!mBTLeScanner.isScanning()) {
                    startScan();
                }
                else {
                    stopScan();
                }
                break;
            default:
                break;
        }

    }

    public void addDevice(BluetoothDevice device, int rssi) {

        String address = device.getAddress();
        if (!mBTDevicesHashMap.containsKey(address)) {
            BTLE_Device btleDevice = new BTLE_Device(device);
            btleDevice.setRSSI(rssi);

            mBTDevicesHashMap.put(address, btleDevice);
            mBTDevicesArrayList.add(btleDevice);
//            Log.e("Device Stored", btleDevice.getAddress());
        }
        else {
            mBTDevicesHashMap.get(address).setRSSI(rssi);
        }

        adapter.notifyDataSetChanged();
    }

    public void startScan(){
//        btn_Scan.setText("Scanning...");

        mBTDevicesArrayList.clear();
        mBTDevicesHashMap.clear();

        mBTLeScanner.start();
    }

    public void stopScan() {
//        btn_Scan.setText("Scan Again");

        mBTLeScanner.stop();
    }

    public ListAdapter_BTLE_Devices getAdapter(){
        return adapter;
    }

    public static void checkPermissions(Activity activity, Context context){
        int PERMISSION_ALL = 1;
        Log.e("BLUETOOTH PERMS", "HI");
        String[] PERMISSIONS = {
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                android.Manifest.permission.BLUETOOTH,
                android.Manifest.permission.BLUETOOTH_ADMIN,
                android.Manifest.permission.BLUETOOTH_CONNECT,
                android.Manifest.permission.BLUETOOTH_ADVERTISE,
                android.Manifest.permission.BLUETOOTH_SCAN

        };

        if(!hasPermissions(context, PERMISSIONS)){
            ActivityCompat.requestPermissions(activity, PERMISSIONS, PERMISSION_ALL);
            Log.e("BLUETOOTH PERMS", "NO PERMS");
        }
    }

    public static boolean hasPermissions(Context context, String... permissions){
        Log.e("BLUETOOTH PERMS", "RUNNING HASPERMS");
        if(context != null && permissions != null){
            for (String permission : permissions){
                if (ActivityCompat.checkSelfPermission(context, permission)!=PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
        }
        return true;
    }

    public static UUID convertFromInteger(int i) { // ex "0x180D" for heart rate services
        final long MSB = 0x0000000000001000L;
        final long LSB = 0x800000805f9b34fbL;
        long value = i & 0xFFFFFFFF;
        return new UUID(MSB | (value << 32), LSB);
    }

    public String getDeviceName(){
        return deviceName;
    }

    public String getDeviceAddress(){
        return deviceAddress;
    }
}
