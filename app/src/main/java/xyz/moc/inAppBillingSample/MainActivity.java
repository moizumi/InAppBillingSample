package xyz.moc.inAppBillingSample;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends ActionBarActivity {

    private String mPremiumUpgradePrice;
    private String mGasPrice;

    IInAppBillingService mService;

    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        Button button = (Button) findViewById(R.id.buy_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                buy();

//                ArrayList<String> skuList = new ArrayList<> ();
//                skuList.add("premiumUpgrade");
//                skuList.add("gas");
//                Bundle querySkus = new Bundle();
//                querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
//
//
//
//                try {
//                    Bundle skuDetails = mService.getSkuDetails(3,
//                            getPackageName(), "inapp", querySkus);
//
//
//                    int response = skuDetails.getInt("RESPONSE_CODE");
//                    if (response == 0) {
//                        ArrayList<String> responseList
//                                = skuDetails.getStringArrayList("DETAILS_LIST");
//
//                        for (String thisResponse : responseList) {
//                            JSONObject object = new JSONObject(thisResponse);
//                            String sku = object.getString("productId");
//                            String price = object.getString("price");
//                            if (sku.equals("premiumUpgrade")) {
//                                mPremiumUpgradePrice = price;
//                            } else if (sku.equals("gas")) {
//                                mGasPrice = price;
//                            }
//                        }
//                    }
//
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }


            }
        });

    }


    private void buy() {
        try {
            Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(),
                    "android.test.purchase" , "inapp", "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");

            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");

            startIntentSenderForResult(pendingIntent.getIntentSender(),
                    1001, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
                    Integer.valueOf(0));


        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            unbindService(mServiceConn);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001) {
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            if (resultCode == RESULT_OK) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = jo.getString("productId");
//                    alert("You have bought the " + sku + ". Excellent choice,
//                            adventurer!");
                }
                catch (JSONException e) {
//                    alert("Failed to parse purchase data.");
                    e.printStackTrace();
                }
            }
        }
    }
}
