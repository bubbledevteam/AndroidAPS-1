package info.nightscout.androidaps.plugins.pump.common.dialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import javax.inject.Inject;

import info.nightscout.androidaps.activities.NoSplashAppCompatActivity;
import info.nightscout.androidaps.interfaces.ActivePlugin;
import info.nightscout.androidaps.logging.AAPSLogger;
import info.nightscout.androidaps.plugins.bus.RxBus;
import info.nightscout.androidaps.plugins.pump.common.ble.BlePreCheck;
import info.nightscout.androidaps.plugins.pump.common.hw.rileylink.R;
import info.nightscout.androidaps.plugins.pump.common.hw.rileylink.RileyLinkUtil;
import info.nightscout.androidaps.plugins.pump.common.hw.rileylink.ble.RFSpy;
import info.nightscout.androidaps.utils.resources.ResourceHelper;
import info.nightscout.androidaps.utils.rx.AapsSchedulers;
import info.nightscout.androidaps.utils.sharedPreferences.SP;

// IMPORTANT: This activity needs to be called from RileyLinkSelectPreference (see pref_medtronic.xml as example)
public class OrangeLinkConfigActivity extends NoSplashAppCompatActivity {
    public static final String ACTION_ORANGE_CONFIGURE = "ACTION_ORANGE_CONFIGURE";
    @Inject AAPSLogger aapsLogger;
    @Inject SP sp;
    @Inject ResourceHelper rh;
    @Inject BlePreCheck blePrecheck;
    @Inject RileyLinkUtil rileyLinkUtil;
    @Inject ActivePlugin activePlugin;
    @Inject RFSpy rfSpy;
    @Inject RxBus rxBus;
    @Inject AapsSchedulers aapsSchedulers;
    private CheckBox orangeTestVibrator;
    private CheckBox orangeTestRedLed;
    private CheckBox orangeTestYellowLed;
    private CheckBox orangeEnableLed;
    private CheckBox orangeEnableVibrator;
    private Handler handler;
    private Button findDev;
    private static final int delayTime = 50;
    private static final int delayTime2 = 150;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orange_link_config_activity);
        handler = new Handler(Looper.getMainLooper());
        orangeTestVibrator = (CheckBox) findViewById(R.id.orangeTestVibrator);
        orangeTestRedLed = (CheckBox) findViewById(R.id.orangeTestRedLed);
        orangeTestYellowLed = (CheckBox) findViewById(R.id.orangeTestYellowLed);
        orangeEnableLed = (CheckBox) findViewById(R.id.orangeEnableLed);
        orangeEnableVibrator = (CheckBox) findViewById(R.id.orangeEnableVibrator);
        findDev = (Button) findViewById(R.id.findDev);
        initClick();
        handler.postDelayed(new Runnable() {
            @Override public void run() {
                rfSpy.writeToOrange(new byte[]{(byte) 0xAA});
                getConfigure();
            }
        }, delayTime);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_ORANGE_CONFIGURE);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            Log.e("xxxx", "OrangeLinkImpl==" + intent.getAction());
            String action = intent.getAction();
            if (action.equals(ACTION_ORANGE_CONFIGURE)) {
                int led = intent.getIntExtra("led", 0);
                int vibrator = intent.getIntExtra("vibrator", 0);
                if (led == 1) {
                    orangeEnableLed.setChecked(true);
                } else {
                    orangeEnableLed.setChecked(false);
                }
                if (vibrator == 1) {
                    orangeEnableVibrator.setChecked(true);
                } else {
                    orangeEnableVibrator.setChecked(false);
                }
            }
        }
    };

    private void getConfigure() {
        handler.postDelayed(new Runnable() {
            @Override public void run() {
                rfSpy.writeToOrange(new byte[]{(byte) 0xdd, 0x01});
            }
        }, delayTime);
    }

    private void checkStatus(CompoundButton current, boolean flag) {
        checkStatus(current);
    }

    private void checkStatus(CompoundButton current) {
        CheckBox checkBoxs[] = new CheckBox[]{orangeTestVibrator, orangeTestRedLed,
                orangeTestYellowLed};
        Log.e("xxxx", "checkStatus======");
        for (CheckBox c : checkBoxs) {
            if (c != current) {
                if (current.isChecked()) {
                    c.setChecked(false);
                }
            }
        }
    }

    CompoundButton preCompoundButton;

    private void closeCmd(CompoundButton current) {
//        if (preCompoundButton != null) {
//            if (preCompoundButton == orangeTestVibrator) {
//                handler.postDelayed(new Runnable() {
//                    @Override public void run() {
//                        byte[] values = new byte[]{(byte) 0xbb, 0x05};
//                        rfSpy.writeToOrange(values);
//                    }
//                }, delayTime);
//            } else {
//                handler.postDelayed(new Runnable() {
//                    @Override public void run() {
//                        byte[] values = new byte[]{(byte) 0xbb, 0x03};
//                        rfSpy.writeToOrange(values);
//                    }
//                }, delayTime);
//            }
//        }
//        if (current.isChecked()) {
//            preCompoundButton = current;
//        } else {
//            preCompoundButton = null;
//        }

    }

    private void initClick() {
        orangeTestVibrator.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Log.e("xxxx", "orangeTestVibrator=== onClick===" + orangeTestVibrator.isChecked());
                boolean b = orangeTestVibrator.isChecked();
                closeCmd(orangeTestVibrator);
                handler.postDelayed(new Runnable() {
                    @Override public void run() {
                        byte[] values = new byte[]{(byte) 0xbb, 0x05};
                        if (b) {
                            values = new byte[]{(byte) 0xbb, 0x04};
                        }
                        rfSpy.writeToOrange(values);
                    }
                }, delayTime2);

            }
        });
        orangeTestRedLed.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Log.e("xxxx", "orangeTestRedLed=== onClick===" + orangeTestRedLed.isChecked());
                boolean b = orangeTestRedLed.isChecked();
                closeCmd(orangeTestRedLed);
                handler.postDelayed(new Runnable() {
                    @Override public void run() {
                        byte[] values = new byte[]{(byte) 0xbb, 0x03};
                        if (b) {
                            values = new byte[]{(byte) 0xbb, 0x02};
                        }
                        rfSpy.writeToOrange(values);
                    }
                }, delayTime2);
            }
        });


        orangeTestYellowLed.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Log.e("xxxx", "orangeTestYellowLed=== onClick===" + orangeTestYellowLed.isChecked());
                boolean b = orangeTestYellowLed.isChecked();
                closeCmd(orangeTestRedLed);
                handler.postDelayed(new Runnable() {
                    @Override public void run() {
                        byte[] values = new byte[]{(byte) 0xbb, 0x03};
                        if (b) {
                            values = new byte[]{(byte) 0xbb, 0x01};
                        }
                        rfSpy.writeToOrange(values);
                    }
                }, delayTime2);
            }
        });

        orangeTestVibrator.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checkStatus(compoundButton, b);

            }
        });
        orangeTestRedLed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checkStatus(compoundButton, b);

            }
        });
        orangeTestYellowLed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checkStatus(compoundButton, b);
            }
        });

        orangeEnableLed.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                led = 0x00;
                if (orangeEnableLed.isChecked()) {
                    led = 0x01;
                }
                byte[] values = new byte[]{(byte) 0xdd, 0x02, 0x00, led};
                sendconfigure(values);

            }
        });
        orangeEnableVibrator.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                vibrator = 0x00;
                if (orangeEnableVibrator.isChecked()) {
                    vibrator = 0x01;
                }
                byte[] values = new byte[]{(byte) 0xdd, 0x02, 0x01, vibrator};
                sendconfigure(values);
            }
        });
        findDev.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                handler.postDelayed(new Runnable() {
                    @Override public void run() {
                        byte[] values = new byte[]{(byte) 0xdd, 0x04};
                        rfSpy.writeToOrange(values);
                    }
                }, delayTime2);
            }
        });
    }

    byte led, vibrator;

    private void sendconfigure(final byte[] value) {

        handler.postDelayed(new Runnable() {
            @Override public void run() {
                rfSpy.writeToOrange(value);
            }
        }, delayTime2);
    }

    @Override protected void onResume() {
        super.onResume();
        prepareForScanning();

    }

    @Override protected void onDestroy() {
        super.onDestroy();
        handler.postDelayed(new Runnable() {
            @Override public void run() {
                rfSpy.writeToOrange(new byte[]{(byte) 0xCC});
            }
        }, delayTime);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    private void prepareForScanning() {
    }

}
