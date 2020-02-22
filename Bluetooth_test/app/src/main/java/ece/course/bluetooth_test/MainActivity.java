package ece.course.bluetooth_test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.Manifest;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter bleadapter = BluetoothAdapter.getDefaultAdapter();
    private ArrayList<String> bledata_compare = null; //用于比较
    private ArrayList<String> bledata = null;    //用于存储
    ArrayAdapter<String> adapter = null;
    private Button Search;
    private Button Stop;
    private ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取权限
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION},1);
        //创建容器，用于数据传递
        this.bledata_compare = new ArrayList<>();
        this.bledata = new ArrayList<>();
        //控件初始化
        initView();
        //数据传递
        this.adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, this.bledata);
        //显示
        lv.setAdapter(adapter);

    }
    private void initView(){

        //找到控件对象
        Search = findViewById(R.id.btn1);
        Stop = findViewById(R.id.btn2);
        lv = findViewById(R.id.lv1);

        //创建单击事件
        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //开启蓝牙
                enableBle();
                //开始扫描
                bleadapter.startLeScan(leScanCallback);
                adapter.notifyDataSetChanged();
            }
        });
        Stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //扫描停止
                bleadapter.stopLeScan(leScanCallback);
                bledata_compare.clear();
                bledata.clear();
            }
        });
    }
    //开启蓝牙
    private void enableBle(){
        if(!bleadapter.isEnabled()){
            bleadapter.enable();
        }
    }
    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {

            final ble_device ble_device = new ble_device();  //此处为新建的一个类,为ble_device
            ble_device.ble_address = bluetoothDevice.getAddress();

            if(bledata_compare.contains(ble_device.ble_address)) {
                int n = bledata_compare.indexOf(ble_device.ble_address);
                bledata.set(n,"address:"+bluetoothDevice.getAddress()+"\nname:"+bluetoothDevice.getName()+"\nrssi:"+i);
                adapter.notifyDataSetChanged();
            }   //若列表中已经有了相应设备信息，则不添加进去
            else {
                bledata_compare.add(ble_device.ble_address);
                bledata.add("address:"+bluetoothDevice.getAddress()+"\nname:"+bluetoothDevice.getName()+"\nrssi:"+i);
                adapter.notifyDataSetChanged();
            }
        }
    };
}
