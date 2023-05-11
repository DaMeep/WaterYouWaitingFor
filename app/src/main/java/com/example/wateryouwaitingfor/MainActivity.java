package com.example.wateryouwaitingfor;



import android.Manifest;
import android.app.AlertDialog;
import static android.bluetooth.BluetoothAdapter.STATE_CONNECTED;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.view.LayoutInflater;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.wateryouwaitingfor.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    ActivityMainBinding binding;
    public static final String SHARED_PREFS = "com.example.wateryouwaitingfor.shared_preferences"; // Shared Preferences Location
    private final static String TAG = MainActivity.class.getSimpleName();

    public static final int REQUEST_ENABLE_BT = 1; // Request Code for Bluetooth Permissions
    public static final int BTLE_SERVICES = 2;

    public WaterIntakeHandler waterIntakeHandler; // Handler for Bottle Data

    private SharedPreferences sharedpreferences; // Reference to Shared Preferences
    private String deviceName, deviceAddress; // Variables for the currently connected BTLE_Device

    private HashMap<String, BTLE_Device> mBTDevicesHashMap; // HashMap of all the scanned BTLE_Devices
    private ArrayList<BTLE_Device> mBTDevicesArrayList; // ArrayList of all scanned BTLE_Devices for Displaying via ListAdapter
    private ListAdapter_BTLE_Devices adapter; // ListAdapter for managing BTLE_Devices with the ListView


    public static UUID SERVICE_UUID = convertFromInteger(0xC201); // UUID of the Water Consumption Service on the BTLE_Device
    public static UUID CHAR_UUID = convertFromInteger(0x483E); // UUID of the Water Consumption Characteristic for the Water Consumption Service
    public static UUID CCCD_UUID = convertFromInteger(0x2902); // UUID of the Client Characteristic Configuration Descriptor for the Water Consumption Characteristic

    private BroadcastReceiver_BTState mBTStateUpdateReceiver; // Reads the current state of Bluetooth on the Android Device
    private Scanner_BTLE mBTLeScanner; // Scanner for BTLE Devices


    //Service Stuff
    private Intent mBTLE_Service_Intent; // Launches the BTLE_Service
    private Service_BTLE_GATT mBTLE_Service; // Instigates a connection with the BTLE_Device
    private boolean mBTLE_Service_Bound; // Holds the connection state of the BTLE_Service
    private BroadcastReceiver mGattUpdateReceiver; // Reads the current state of Connection with the BTLE_Device

    //Firebase Stuff
    private DatabaseReference mDatabaseReference; // Firebase Reference
    private DatabaseReference mUsersReference; // Firebase User List Reference
    private ValueEventListener updateListener; // Listener for Firebase Updates
    private HashMap<String, User> listOfUsers; // List of Updated User IDs and their corresponding User Objects


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        waterIntakeHandler = new WaterIntakeHandler();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        checkBluetoothPermissions(this, getApplicationContext());

        // Create References
        sharedpreferences = getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mUsersReference = mDatabaseReference.child("users");

        //Creates Listener for changes in Firebase Data
         updateListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listOfUsers = new HashMap<>();
                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                    User user = userSnapshot.getValue(User.class);
                    listOfUsers.put(userSnapshot.getKey(), user);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadUser:onCancelled", databaseError.toException());
            }
        };
        mUsersReference.addValueEventListener(updateListener);


        // Reciever for current BT Status
        mGattUpdateReceiver = new BroadcastReceiver(){

            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();

                if (Service_BTLE_GATT.ACTION_GATT_CONNECTED.equals(action)) {
                    Utils.toast(getApplicationContext(), "Connected To Device");
                }
                else if (Service_BTLE_GATT.ACTION_GATT_DISCONNECTED.equals(action)) {
                    Utils.toast(getApplicationContext(), "Disconnected From Device");
                }

            }
        };


        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Utils.toast(getApplicationContext(), "BLE not supported");
//            finish();
        }

        mBTStateUpdateReceiver = new BroadcastReceiver_BTState(getApplicationContext());
        mBTLeScanner = new Scanner_BTLE(this, 5000, -75);

        mBTDevicesHashMap = new HashMap<>();
        mBTDevicesArrayList = new ArrayList<>();

        adapter = new ListAdapter_BTLE_Devices(this, R.layout.btle_device_list_item, mBTDevicesArrayList);

        // Set current page to HomeFragment
        replaceFragment((new HomeFragment()));

        //Defaults the Navigation Bar to Select the Home Fragment
        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().findItem(R.id.home).setChecked(true);

        //Informs the Navigation Bar to replace the current Fragment with the desired when pressed
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

        //If first startup, start introduction activity
        if (sharedpreferences.getBoolean("firstStart", true)){
            Intent i = new Intent(MainActivity.this, GetStarted.class);
            startActivity(i);
            finish();
        }
    }

    /**
     * Puts the application's focus on a new Fragment
     *
     * @param fragment Replacement Fragment
     */
    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment, fragment.getClass().getSimpleName());
        fragmentTransaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();

//        createNotificationChannel();

        registerReceiver(mBTStateUpdateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        registerReceiver(mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());
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

        unregisterReceiver(mGattUpdateReceiver);
        if (mBTLE_ServiceConnection != null && mBTLE_Service_Bound){
            unbindService(mBTLE_ServiceConnection);
        }
        mBTLE_Service_Intent = null;

//        startService( new Intent( this, NotificationService. class )) ;
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

        mBTLE_Service_Intent = new Intent(this, Service_BTLE_GATT.class);
        bindService(mBTLE_Service_Intent, mBTLE_ServiceConnection, Context.BIND_AUTO_CREATE);
        startService(mBTLE_Service_Intent);
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {

            case R.id.btn_scan:
                Utils.toast(getApplicationContext(), "Scan Button Pressed");
                String[] perms = {
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.BLUETOOTH_CONNECT,
                        android.Manifest.permission.BLUETOOTH_SCAN,
                };
                if (hasPermissions(getApplicationContext(), perms)){ // Check BT Perms and scan
                    replaceFragment(new DeviceListFragment());
                    startScan();
                }
                else{ // Request BT Perms again
                    Utils.toast(getApplicationContext(), "Please enable Bluetooth and try again");
                    ActivityCompat.requestPermissions(this, perms, REQUEST_ENABLE_BT);
                }
                break;
            default:
                break;
        }

    }

    /**
     * Adds a potential device to the list of devices
     * @param device New Device
     * @param rssi Device's RSSI
     */
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

    /**
     * Prompts the Scanner_BTLE to begin its scan
     */
    public void startScan(){
//        btn_Scan.setText("Scanning...");

        mBTDevicesArrayList.clear();
        mBTDevicesHashMap.clear();

        mBTLeScanner.start();
    }

    /**
     * Prompts the Scanner_BTLE to stop its scan
     */
    public void stopScan() {
//        btn_Scan.setText("Scan Again");

        mBTLeScanner.stop();
    }
    
    //Services Start

    private ServiceConnection mBTLE_ServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {

            // We've bound to LocalService, cast the IBinder and get LocalService instance
            Service_BTLE_GATT.BTLeServiceBinder binder = (Service_BTLE_GATT.BTLeServiceBinder) service;
            mBTLE_Service = binder.getService();
            mBTLE_Service_Bound = true;

            if (!mBTLE_Service.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
            }

            mBTLE_Service.connect(deviceAddress);
            // Automatically connects to the device upon successful start-up initialization.
//            mBTLeService.connect(mBTLeDeviceAddress);

//            mBluetoothGatt = mBTLeService.getmBluetoothGatt();
//            mGattUpdateReceiver.setBluetoothGatt(mBluetoothGatt);
//            mGattUpdateReceiver.setBTLeService(mBTLeService);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBTLE_Service = null;
            mBTLE_Service_Bound = false;
//            mBluetoothGatt = null;
//            mGattUpdateReceiver.setBluetoothGatt(null);
//            mGattUpdateReceiver.setBTLeService(null);
        }
    };


    //Services Stop

    /**
     * Get the current ListAdapter for Scanned BTLE_Devices
     *
     * @return The BTLE ListAdapter
     */
    public ListAdapter_BTLE_Devices getAdapter(){
        return adapter;
    }


    /**
     * Checks the Device's allowed permissions
     * and requests more if needed
     *
     * @param activity The current activity
     * @param context The context of the prompt
     */
    public static void checkBluetoothPermissions(Activity activity, Context context){
        String[] PERMISSIONS = {
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
//                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
//                android.Manifest.permission.BLUETOOTH,
//                android.Manifest.permission.BLUETOOTH_ADMIN,
                android.Manifest.permission.BLUETOOTH_CONNECT,
//                android.Manifest.permission.BLUETOOTH_ADVERTISE,
                android.Manifest.permission.BLUETOOTH_SCAN,
        };

        if(!hasPermissions(context, PERMISSIONS)){
            ActivityCompat.requestPermissions(activity, PERMISSIONS, REQUEST_ENABLE_BT);
        }
    }

    /**
     * Checks the current state of the specified permissions
     *
     * @param context The context of the check
     * @param permissions The list of permissions
     *
     * @return Whether the permissions have been allowed
     */
    public static boolean hasPermissions(Context context, String... permissions){
        if(context != null && permissions != null){
            for (String permission : permissions){
                if (ActivityCompat.checkSelfPermission(context, permission)!=PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Converts an inputted hex value into its corresponding BLE UUID
     *
     * @param i Hex Value
     */
    public static UUID convertFromInteger(int i) { // ex "0x180D" for heart rate services
        final long MSB = 0x0000000000001000L;
        final long LSB = 0x800000805f9b34fbL;
        long value = i & 0xFFFFFFFF;
        return new UUID(MSB | (value << 32), LSB);
    }

    /**
     * Converts a received byte array to its corresponding String value
     *
     * @param bytes Byte Array
     * @return String Representation of bytes
     */
    public static String bytesToString(byte[] bytes){
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Converts a received byte array to its corresponding double value
     * @param bytes Byte Array
     * @return Double Representation of bytes
     */
    public static double bytesToDouble(byte[] bytes){
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getDouble();
    }

    /**
     * Get the name of the connected device
     *
     * @return The name of the device
     */
    public String getDeviceName(){
        return deviceName;
    }

    /**
     * Get the address of the connected device
     *
     * @return The address of the device
     */
    public String getDeviceAddress(){
        return deviceAddress;
    }

    /**
     * Get the BTLE Gatt Service with the connected device
     *
     * @return The established Service_BTLE_GATT
     */
    public Service_BTLE_GATT getService(){
        return mBTLE_Service;
    }
    
    /**
     * Get the WaterIntakeHandler for registering consumption
     * 
     * @return The WaterIntakeHandler
     */
    public WaterIntakeHandler getIntakeHandler (){
        return waterIntakeHandler;
    }

    /**
     * Get the reference to the Firebase
     *
     * @return The Firebase Reference
     */
    public DatabaseReference getUserReference() { return mUsersReference; }

    /**
     * Get the Firebase's updated list of users
     *
     * @return A Hashmap made up of User IDs and corresponding User Objects
     */
    public HashMap<String, User> getUsers(){ return listOfUsers; }

    /**
     * Get the updated information of the current user from the Firebase
     *
     * @return The Firebase information of the application's User
     */
    public User getCurrentUser(){
        String userID = sharedpreferences.getString("userID", "null");
        return listOfUsers.get(userID);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case SettingsFragment.NOTIFICATION_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    startService( new Intent( this, NotificationService. class )) ;
                }  else {
                    // Explain to the user that the feature is unavailable because
                    // the feature requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    SettingsFragment settingsFragment = (SettingsFragment) (getSupportFragmentManager().findFragmentByTag("SettingsFragment"));
                    settingsFragment.setNotificationSwitch(false);
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }

}

