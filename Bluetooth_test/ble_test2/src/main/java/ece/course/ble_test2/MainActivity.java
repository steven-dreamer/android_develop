package ece.course.ble_test2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 0;
    private boolean flag = false;
    private List<BluetoothDevice> mDeviceList = new ArrayList<>();
    private List<BluetoothDevice> mDeviceList2 = new ArrayList<>();
    private List<BluetoothDevice> mDeviceList3 = new ArrayList<>();
    private List<BluetoothDevice> mDeviceList4 = new ArrayList<>();
    private ListView mListView;
    private ListView mListView2;
    private DeviceAdapter mAdapter;
    private DeviceAdapter mAdapter2;

    private BlueToothController mController = new BlueToothController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

        registerBluetoothReceiver();

        mController.turnOnBlueTooth(this,REQUEST_CODE);
    }

    //初始化业务界面
    private void initUI(){
        mListView =findViewById(R.id.device_list);
        mAdapter = new DeviceAdapter(mDeviceList, this);
        mListView.setAdapter(mAdapter);

        mListView2 = findViewById(R.id.device_list2);
        mAdapter2 = new DeviceAdapter(mDeviceList2,this);
        mListView2.setAdapter(mAdapter2);
    }

    private void registerBluetoothReceiver(){
        IntentFilter filter = new IntentFilter();
        //开始查找
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        //结束查找
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //查找设备
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        //设备扫描模式改变
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);

        registerReceiver(receiver, filter);
    }

    //注册广播监听搜索结果
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                //setProgressBarIndeterminateVisibility(true);
                //初始化数据列表
                mDeviceList3.addAll(mDeviceList);
                mDeviceList.clear();
                mAdapter.notifyDataSetChanged();

            } else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                //setProgressBarIndeterminateVisibility(false);
                Log.d("process","finish found");
                mDeviceList2.clear();
                mDeviceList4.addAll(mDeviceList);
                mDeviceList4.removeAll(mDeviceList3);
                mDeviceList2.addAll(mDeviceList4);
                mDeviceList3.removeAll(mDeviceList);
                mDeviceList2.addAll(mDeviceList3);
                mAdapter2.notifyDataSetChanged();
                mDeviceList4.clear();
                mDeviceList3.clear();
                Message msg = new Message();//消息
                Bundle bundle = new Bundle();
                bundle.clear();
                msg.what = 0x01;//消息类别
                bundle.putShort("msg",(short) 0);
                msg.setData(bundle);
                myHandler.sendMessage(msg);
            }
            else if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //找到一个添加一个
                if (!mDeviceList.contains(device)) {
                    mDeviceList.add(device);
                    mAdapter.notifyDataSetChanged();
                }
                Log.d("inform", device.getName() + "  " + device.getAddress());
            }
        }
    };

    public Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            short now = bundle.getShort("msg");
            Log.d("onGet", String.valueOf(now));
            if (msg.what == 0x01) {
                try {
                    Thread.currentThread().sleep(40000);
                } catch (InterruptedException e) {
                    return;
                }
                if (!flag) {
                    Log.d("process","refound");
                    mController.findDevice();
                }
            }
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id =item.getItemId();
        if (id == R.id.find_device){
            mAdapter.refresh(mDeviceList);
            mAdapter2.refresh(mDeviceList2);
            mController.findDevice();
            flag = false;
        }
        if(id == R.id.stop_find){
            flag = true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
