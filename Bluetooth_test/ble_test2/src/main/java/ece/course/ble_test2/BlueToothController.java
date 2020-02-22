package ece.course.ble_test2;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

public class BlueToothController {
    private BluetoothAdapter mAdapter;

    public BlueToothController() {
        this.mAdapter = BluetoothAdapter.getDefaultAdapter();
    }
    public BluetoothAdapter getmAdapter(){
        return mAdapter;
    }
    /**
     * 打开蓝牙
     */
    public void turnOnBlueTooth(Activity activity, int requestCode) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 查找设备
     */
    public void findDevice() {
        assert (mAdapter != null);
        mAdapter.startDiscovery();
    }

}
