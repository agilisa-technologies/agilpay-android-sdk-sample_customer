package com.far.paynetsdkexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.agilisa.devices.DeviceFactory;
import com.agilisa.devices.IDevice;
import com.agilisa.devices.IDeviceListener;
import com.agilisa.devices.global.Functions;
import com.agilisa.devices.global.Global;
import com.agilisa.devices.models.CloseBatchRequest;
import com.agilisa.devices.models.CloseBatchResponse;
import com.agilisa.devices.models.Configuration;
import com.agilisa.devices.models.ConnectionStatus;
import com.agilisa.devices.models.EntryMode;
import com.agilisa.devices.models.ErrorResponse;
import com.agilisa.devices.models.FiscalControlDetails;
import com.agilisa.devices.models.FiscalControlTokenRequest;
import com.agilisa.devices.models.FiscalControlTokenResponse;
import com.agilisa.devices.models.SalesReportResponse;
import com.agilisa.devices.models.SearchTransactionsResponse;
import com.agilisa.devices.models.TransactionRequest;
import com.agilisa.devices.models.TransactionResponse;
import com.agilisa.devices.models.VoucherData;
import com.far.paynetsdkexample.Global.Globals;
import com.far.paynetsdkexample.model.KV;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements IDeviceListener {


    Spinner spnTransactionType;
    ViewGroup configLayout, transactionLayout, responseLayout;
    EditText etCurrency,etServiceKey,etTerminalCode,etMerchantCode,  etTransactionId, etInvoice, etAmount, etCashbackAmount, etStateTax,etCityTax,etReducedStateTax, etTipAmount,etKeySlot, etMinPinLength, etMaxPinLength,etTimeout,etExternalData, etCardNumber, etVoucherNumber, etAuthNumber, etTransactionIdResponse;
    CheckBox cbManualEntry, cbContactEntry, cbSwipeEntry, cbFallbackEntry, cbContactlessEntry;
    Button btnBack, btnNext;
    TextView tvResponse;

    EditText etClientId,etClientSecret,etCustomerId;

    Global.TRANSACTION_TYPE selectedTransactionType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        etCurrency = findViewById(R.id.etCurrency);
        etTerminalCode = findViewById(R.id.etTerminalCode);
        etMerchantCode = findViewById(R.id.etMerchantCode);


        etClientId= findViewById(R.id.etClientId);
        etClientSecret= findViewById(R.id.etClientSecret);
        etCustomerId= findViewById(R.id.etCustomerId);



        spnTransactionType = findViewById(R.id.spnTransactionType);
        configLayout = findViewById(R.id.configLayout);
        transactionLayout = findViewById(R.id.transactionLayout);
        responseLayout = findViewById(R.id.responseLayout);
        etTransactionId = findViewById(R.id.etTransactionId);
        etInvoice = findViewById(R.id.etInvoice);
        etServiceKey = findViewById(R.id.etServiceKey);
        etAmount = findViewById(R.id.etAmount);
        etCashbackAmount= findViewById(R.id.etCashbackAmount);
        etStateTax=findViewById(R.id.etStateTax);
        etCityTax=findViewById(R.id.etCityTax);
        etReducedStateTax=findViewById(R.id.etReducedStateTax);
        etTipAmount= findViewById(R.id.etTipAmount);
        etKeySlot= findViewById(R.id.etKeySlot);
        etMinPinLength= findViewById(R.id.etMinPinLength);
        etMaxPinLength= findViewById(R.id.etMaxPinLength);
        etTimeout= findViewById(R.id.etTimeout);
        etExternalData= findViewById(R.id.etExternalData);
        etCardNumber = findViewById(R.id.etCardNumber);
        etVoucherNumber = findViewById(R.id.etVoucherNumber);
        etAuthNumber = findViewById(R.id.etAuthNumber);

        cbManualEntry = findViewById(R.id.cbManualEntry);
        cbContactEntry= findViewById(R.id.cbContactEntry);
        cbSwipeEntry= findViewById(R.id.cbSwipeEntry);
        cbFallbackEntry= findViewById(R.id.cbFallbackEntry);
        cbContactlessEntry= findViewById(R.id.cbContactlessEntry);

        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);
        etTransactionIdResponse = findViewById(R.id.etTransactionIdResponse);
        tvResponse = findViewById(R.id.tvResponse);

        spnTransactionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                selectedTransactionType =(Global.TRANSACTION_TYPE)((KV)adapterView.getAdapter().getItem(i)).getObject();
                prepare();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(configLayout.getVisibility() == View.VISIBLE){
                    gotTransactionScreen();
                }else if(transactionLayout.getVisibility() == View.VISIBLE){
                    if(validate()){
                        startTransaction();
                    }
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(transactionLayout.getVisibility() == View.VISIBLE){
                    gotConfigurationScreen();
                }else if(responseLayout.getVisibility() == View.VISIBLE){
                    gotTransactionScreen();
                }

            }
        });


        fillTransactionSpinner();
    }




    private void fillTransactionSpinner(){
        ArrayList<KV> arrayList = new ArrayList<>();
        arrayList.add(new KV(Global.TRANSACTION_TYPE.SALE,"SALE"));
        arrayList.add(new KV(Global.TRANSACTION_TYPE.AUTHORIZATION,"AUTHORIZATION"));
        arrayList.add(new KV(Global.TRANSACTION_TYPE.POSTAUTHORIZATION,"POSTAUTHORIZATION"));
        arrayList.add(new KV(Global.TRANSACTION_TYPE.VOID,"VOID"));
        arrayList.add(new KV(Global.TRANSACTION_TYPE.REFUND,"REFUND"));
        arrayList.add(new KV(Global.TRANSACTION_TYPE.CLOSEBATCH,"CLOSEBATCH"));
        /*arrayList.add(new KV(Global.TRANSACTION_TYPE.EBT_FOODPURCHASE,"EBT_FOODPURCHASE"));
        arrayList.add(new KV(Global.TRANSACTION_TYPE.EBT_CASHPURCHASE,"EBT_CASHPURCHASE"));
        arrayList.add(new KV(Global.TRANSACTION_TYPE.EBT_FOOD_RETURN,"EBT_FOOD_RETURN"));
        arrayList.add(new KV(Global.TRANSACTION_TYPE.EBT_BALANCE_INQUIRY,"EBT_BALANCE_INQUIRY"));
        arrayList.add(new KV(Global.TRANSACTION_TYPE.EBT_CASHPURCHASE_CASHBACK,"EBT_CASHPURCHASE_CASHBACK"));
        arrayList.add(new KV(Global.TRANSACTION_TYPE.EBT_CASHADVANCE,"EBT_CASHADVANCE"));
        arrayList.add(new KV(Global.TRANSACTION_TYPE.EBT_FOODVOUCHER,"EBT_FOODVOUCHER"));
        arrayList.add(new KV(Global.TRANSACTION_TYPE.EBT_CASHVOUCHER,"EBT_CASHVOUCHER"));*/

        ArrayAdapter<KV> adapter = new ArrayAdapter<KV>(MainActivity.this, android.R.layout.simple_list_item_1,arrayList);
        spnTransactionType.setAdapter(adapter);


    }

    /*void enableControlFiscal(boolean isEnabled){

        FiscalControlTokenResponse f = com.far.paynetsdkexample.Global.Functions.getFiscalControlToken(MainActivity.this);

        trTaxId.setVisibility(isEnabled && f==null?View.VISIBLE:View.GONE);
        trTerminalNumber.setVisibility(isEnabled && f==null?View.VISIBLE:View.GONE);
        trPassword.setVisibility(isEnabled && f==null?View.VISIBLE:View.GONE);
        trDeviceName.setVisibility(isEnabled && f==null?View.VISIBLE:View.GONE);
        trToken.setVisibility(isEnabled && f!=null?View.VISIBLE:View.GONE);
        trSearchToken.setVisibility(isEnabled && f==null?View.VISIBLE:View.GONE);
        trDeleteToken.setVisibility(isEnabled && f!=null?View.VISIBLE:View.GONE);

        if(f != null){
            etToken.setText(f.getToken());
        }else{
            etToken.setText("");
            etTaxId.setText("");
            etTerminalNumber.setText("");
            etPassword.setText("");
            etDeviceName.setText("");
            etToken.setText("");
        }
    }*/

   /* boolean validateGetControlNumber(){
        if(etTaxId.getText().toString().trim().isEmpty()){
            Snackbar.make(configLayout,"TaxId cannot be empty or white space", BaseTransientBottomBar.LENGTH_LONG).show();
            return false;
        }else if(etTerminalNumber.getText().toString().trim().isEmpty()){
            Snackbar.make(configLayout,"TerminalNumber cannot be empty or white space", BaseTransientBottomBar.LENGTH_LONG).show();
            return false;
        }else if(etPassword.getText().toString().trim().isEmpty()){
            Snackbar.make(configLayout,"Password cannot be empty or white space", BaseTransientBottomBar.LENGTH_LONG).show();
            return false;
        }else if(etDeviceName.getText().toString().trim().isEmpty()){
            Snackbar.make(configLayout,"DeviceName cannot be empty or white space", BaseTransientBottomBar.LENGTH_LONG).show();
            return false;
        }
        return true;
    }*/
    private void prepare(){

        findViewById(R.id.trTransactionId).setVisibility(hasTransactionId()?View.VISIBLE:View.GONE);
        findViewById(R.id.trInvoice).setVisibility(hasInvoice()?View.VISIBLE:View.GONE);
        findViewById(R.id.trServiceKey).setVisibility(hasServiceKey()?View.VISIBLE: View.GONE);
        findViewById(R.id.trAmount).setVisibility(hasAmount()? View.VISIBLE: View.GONE);
        findViewById(R.id.trCashback).setVisibility(hasCashback()? View.VISIBLE:View.GONE);
        findViewById(R.id.trStateTax).setVisibility(hasTaxes()? View.VISIBLE:View.GONE);
        findViewById(R.id.trCityTax).setVisibility(hasTaxes()? View.VISIBLE:View.GONE);
        findViewById(R.id.trReducedStateTax).setVisibility(hasTaxes()? View.VISIBLE:View.GONE);
        findViewById(R.id.trTips).setVisibility(hasTips()? View.VISIBLE:View.GONE);
        findViewById(R.id.trKeySlot).setVisibility(hasKeySlot()? View.VISIBLE:View.GONE);
        findViewById(R.id.trMinPinLength).setVisibility(hasKeySlot()? View.VISIBLE:View.GONE);
        findViewById(R.id.trMaxPinLength).setVisibility(hasKeySlot()? View.VISIBLE: View.GONE);
        findViewById(R.id.trTimeout).setVisibility(View.VISIBLE);
        findViewById(R.id.trExternalData).setVisibility(View.VISIBLE);

        findViewById(R.id.trCardNumber).setVisibility(isEBTVoucher()?View.VISIBLE:View.GONE);
        findViewById(R.id.trVoucherNumber).setVisibility(isEBTVoucher()?View.VISIBLE:View.GONE);
        findViewById(R.id.trAuthNumber).setVisibility(isEBTVoucher()?View.VISIBLE:View.GONE);


        findViewById(R.id.trEntry1).setVisibility(hasEntryMode()? View.VISIBLE:View.GONE);
        findViewById(R.id.trEntry2).setVisibility(hasEntryMode()? View.VISIBLE:View.GONE);
        findViewById(R.id.trEntry3).setVisibility(hasEntryMode()? View.VISIBLE:View.GONE);

        if (isEBTTransaction()){
            etServiceKey.setText("EBT-01");
            cbContactEntry.setChecked(false);
            cbContactlessEntry.setChecked(false);
            cbFallbackEntry.setChecked(false);
            cbManualEntry.setChecked(false);
            cbSwipeEntry.setChecked(true);

            cbContactEntry.setEnabled(false);
            cbContactlessEntry.setEnabled(false);
            cbFallbackEntry.setEnabled(false);
            cbManualEntry.setEnabled(false);
            cbSwipeEntry.setEnabled(true);
        }else{
            etServiceKey.setText("TEST-001");
            cbContactEntry.setEnabled(true);
            cbContactlessEntry.setEnabled(true);
            cbFallbackEntry.setEnabled(true);
            cbManualEntry.setEnabled(true);
            cbSwipeEntry.setEnabled(true);
        }


    }

    private boolean hasTransactionId(){
        boolean tId = (selectedTransactionType == Global.TRANSACTION_TYPE.POSTAUTHORIZATION || selectedTransactionType == Global.TRANSACTION_TYPE.VOID);
        return tId;
    }
    private boolean hasInvoice(){
        boolean invoice = (selectedTransactionType == Global.TRANSACTION_TYPE.SALE || selectedTransactionType == Global.TRANSACTION_TYPE.AUTHORIZATION || selectedTransactionType == Global.TRANSACTION_TYPE.REFUND
                || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_FOODPURCHASE || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_CASHPURCHASE || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_FOOD_RETURN || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_CASHPURCHASE_CASHBACK || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_CASHADVANCE);
        return invoice;
    }

    private boolean hasServiceKey(){
        boolean amount = (selectedTransactionType == Global.TRANSACTION_TYPE.SALE || selectedTransactionType == Global.TRANSACTION_TYPE.AUTHORIZATION || selectedTransactionType == Global.TRANSACTION_TYPE.POSTAUTHORIZATION  || selectedTransactionType == Global.TRANSACTION_TYPE.VOID|| selectedTransactionType == Global.TRANSACTION_TYPE.REFUND
                || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_FOODPURCHASE || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_CASHPURCHASE || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_FOOD_RETURN || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_BALANCE_INQUIRY || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_CASHPURCHASE_CASHBACK || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_CASHADVANCE || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_FOODVOUCHER || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_CASHVOUCHER);
        return amount;
    }

    private boolean hasAmount(){
        boolean amount = (selectedTransactionType == Global.TRANSACTION_TYPE.SALE || selectedTransactionType == Global.TRANSACTION_TYPE.AUTHORIZATION || selectedTransactionType == Global.TRANSACTION_TYPE.POSTAUTHORIZATION || selectedTransactionType == Global.TRANSACTION_TYPE.REFUND
                || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_FOODPURCHASE || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_CASHPURCHASE || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_FOOD_RETURN || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_CASHPURCHASE_CASHBACK || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_CASHADVANCE || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_FOODVOUCHER || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_CASHVOUCHER);
        return amount;
    }
    private boolean hasCashback(){
        boolean cashback = (selectedTransactionType == Global.TRANSACTION_TYPE.SALE || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_CASHPURCHASE_CASHBACK);
        return cashback;
    }

    private boolean hasTaxes(){
        boolean taxes = (selectedTransactionType == Global.TRANSACTION_TYPE.SALE || selectedTransactionType == Global.TRANSACTION_TYPE.AUTHORIZATION || selectedTransactionType == Global.TRANSACTION_TYPE.POSTAUTHORIZATION || selectedTransactionType == Global.TRANSACTION_TYPE.REFUND ||
        selectedTransactionType == Global.TRANSACTION_TYPE.EBT_CASHPURCHASE || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_CASHPURCHASE_CASHBACK || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_CASHADVANCE ||   selectedTransactionType == Global.TRANSACTION_TYPE.EBT_CASHVOUCHER);
        return taxes;
    }
    private boolean hasTips(){
        boolean tips = (selectedTransactionType == Global.TRANSACTION_TYPE.SALE || selectedTransactionType == Global.TRANSACTION_TYPE.POSTAUTHORIZATION );
        return tips;
    }
    private boolean hasKeySlot(){
        boolean keyslot = (selectedTransactionType == Global.TRANSACTION_TYPE.SALE || selectedTransactionType == Global.TRANSACTION_TYPE.AUTHORIZATION || selectedTransactionType == Global.TRANSACTION_TYPE.POSTAUTHORIZATION || selectedTransactionType == Global.TRANSACTION_TYPE.REFUND
                || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_FOODPURCHASE || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_CASHPURCHASE || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_FOOD_RETURN || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_BALANCE_INQUIRY || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_CASHPURCHASE_CASHBACK || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_CASHADVANCE);
        return keyslot;
    }
    private boolean hasEntryMode(){
        boolean entryMode = (selectedTransactionType == Global.TRANSACTION_TYPE.SALE || selectedTransactionType == Global.TRANSACTION_TYPE.AUTHORIZATION || selectedTransactionType == Global.TRANSACTION_TYPE.REFUND
                || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_FOODPURCHASE || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_CASHPURCHASE || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_FOOD_RETURN || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_CASHPURCHASE_CASHBACK || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_CASHADVANCE || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_BALANCE_INQUIRY);
        return entryMode;
    }

    private boolean isEBTTransaction(){
        boolean isEbt = ( selectedTransactionType == Global.TRANSACTION_TYPE.EBT_FOODPURCHASE || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_CASHPURCHASE || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_FOOD_RETURN || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_BALANCE_INQUIRY || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_CASHPURCHASE_CASHBACK || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_CASHADVANCE || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_FOODVOUCHER || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_CASHVOUCHER);
        return isEbt;
    }

    private boolean isEBTVoucher(){
        boolean isEbtVoucher = ( selectedTransactionType == Global.TRANSACTION_TYPE.EBT_FOODVOUCHER || selectedTransactionType == Global.TRANSACTION_TYPE.EBT_CASHVOUCHER);
        return isEbtVoucher;
    }

    private double getAmount(){
        double d = 0;
        try{
            d = Double.parseDouble(etAmount.getText().toString().trim());
        }catch (Exception e){}
        return d;
    }

    private double getCashbackAmount(){
        double d = 0;
        try{
            d = Double.parseDouble(etCashbackAmount.getText().toString().trim());
        }catch (Exception e){}
        return d;
    }

    private double getStateTaxAmount(){
        double d = 0;
        try{
            d = Double.parseDouble(etStateTax.getText().toString().trim());
        }catch (Exception e){}
        return d;
    }

    private double getCityTaxAmount(){
        double d = 0;
        try{
            d = Double.parseDouble(etCityTax.getText().toString().trim());
        }catch (Exception e){}
        return d;
    }

    private double getReducedStateTaxAmount(){
        double d = 0;
        try{
            d = Double.parseDouble(etReducedStateTax.getText().toString().trim());
        }catch (Exception e){}
        return d;
    }

    private double getTotalTaxesAmount(){
        double d = getStateTaxAmount() + getCityTaxAmount() + getReducedStateTaxAmount();
        return d;
    }

    private double getTipAmount(){
        double d = 0;
        try{
            d = Double.parseDouble(etTipAmount.getText().toString().trim());
        }catch (Exception e){}
        return d;
    }

    private String getInvoice(){
        return etInvoice.getText().toString().trim();
    }

    private String getKeySlot(){
        return etKeySlot.getText().toString().trim();
    }
    private int getMinPinLength(){
        int i = -1;
        try{
            i = Integer.parseInt(etMinPinLength.getText().toString().trim());
        }catch (Exception e){}
        return i;
    }

    private int getMaxPinLength(){
        int i = -1;
        try{
            i = Integer.parseInt(etMaxPinLength.getText().toString().trim());
        }catch (Exception e){}
        return i;
    }

    private boolean validate(){
        if(etCurrency.getText().toString().isEmpty() || etMerchantCode.getText().toString().isEmpty() || etTerminalCode.getText().toString().isEmpty()){
            gotConfigurationScreen();
            Snackbar.make(configLayout,"Values cannot be empty or white space", BaseTransientBottomBar.LENGTH_LONG).show();
            return false;
        }else if(etClientId.getText().toString().isEmpty() || etClientSecret.getText().toString().isEmpty() || etCustomerId.getText().toString().isEmpty()){
            gotConfigurationScreen();
            Snackbar.make(configLayout,"Values cannot be empty or white space", BaseTransientBottomBar.LENGTH_LONG).show();
            return false;
        }else if(etServiceKey.getText().toString().isEmpty()){
            Snackbar.make(transactionLayout,"ServiceKey cannot be empty or white space", BaseTransientBottomBar.LENGTH_LONG).show();
            etServiceKey.requestFocus();
        }else if(hasAmount() && getAmount() <= 0){
            Snackbar.make(transactionLayout,"Amount must be greater than 0", BaseTransientBottomBar.LENGTH_LONG).show();
            etAmount.requestFocus();
            return false;
        } else if(hasEntryMode() && (!cbManualEntry.isChecked() && !cbContactEntry.isChecked() && !cbContactlessEntry.isChecked() && !cbFallbackEntry.isChecked() && !cbSwipeEntry.isChecked())){
            Snackbar.make(configLayout,"Select at leat 1 entry mode", BaseTransientBottomBar.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

   /* private boolean getManualEntry(){
        return cbManualEntry.isChecked();
    }
    private boolean getContactEntry(){
        return cbContactEntry.isChecked();
    }
    private boolean getSwipeEntry(){
        return cbSwipeEntry.isChecked();
    }
    private boolean getFallbackEntry(){
        return cbFallbackEntry.isChecked();
    }
    private boolean getContactlessEntry(){
        return cbContactlessEntry.isChecked();
    }*/

    private String getCardNumber(){
        return etCardNumber.getText().toString().trim();
    }

    private String getVoucherNumber(){
        return etVoucherNumber.getText().toString().trim();
    }

    private String getAuthNumber(){
        return etAuthNumber.getText().toString().trim();
    }

    private int getTimeout(){
        int i = 0;
        try{
            i = Integer.parseInt(etTimeout.getText().toString().trim());
        }catch (Exception e){}
        return i;
    }

    private String getTransactionId(){
        return etTransactionId.getText().toString().trim();
    }

    private String getExternalData(){
        return etExternalData.getText().toString().trim();
    }

    private void startTransaction(){
        try {
            Configuration configuration = new Configuration(Globals.HOST,etCurrency.getText().toString().trim(),etServiceKey.getText().toString().trim(),etTerminalCode.getText().toString().trim(),etMerchantCode.getText().toString().trim(), etClientId.getText().toString().trim(), etClientSecret.getText().toString().trim(), etCustomerId.getText().toString());
            DeviceFactory factory = DeviceFactory.getInstance(MainActivity.this, configuration);
            IDevice device = factory.init(DeviceFactory.DEVICE_TYPE.ANDROID, MainActivity.this);
            Object request = getRequest();
            if (request != null) {
                if (request instanceof CloseBatchRequest) {
                    device.closeBatch((CloseBatchRequest) request);
                } else if (request instanceof TransactionRequest) {
                    device.startTransaction((TransactionRequest) request);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private Object getRequest(){


        if(selectedTransactionType == Global.TRANSACTION_TYPE.SALE){
            return TransactionRequest.newSaleInstance(Global.ENDPOINT_TYPE.API,getInvoice(),getAmount(),getCashbackAmount(),getTotalTaxesAmount(),getTipAmount(),getKeySlot(),getMinPinLength(),getMaxPinLength(),new EntryMode(false,false,false,false,false,true),getTimeout(),getExternalData(),null);
        }else if(selectedTransactionType == Global.TRANSACTION_TYPE.AUTHORIZATION){
            return TransactionRequest.newAuthInstance(Global.ENDPOINT_TYPE.API,getInvoice(),getAmount(),getTotalTaxesAmount(),getKeySlot(),getMinPinLength(),getMaxPinLength(),new EntryMode(false,false,false,false,false,true),getTimeout(),getExternalData());
        }else if(selectedTransactionType == Global.TRANSACTION_TYPE.POSTAUTHORIZATION){
            return TransactionRequest.newPostAuthInstance(getTransactionId(),getAmount(),getTotalTaxesAmount(),getTipAmount(),getTimeout(),getExternalData(),null);
        }else if(selectedTransactionType == Global.TRANSACTION_TYPE.VOID){
            return TransactionRequest.newVoidInstance(getTransactionId(),getTimeout(),getExternalData(),null);
        }else if(selectedTransactionType == Global.TRANSACTION_TYPE.REFUND){
            return TransactionRequest.newReturnInstance(Global.ENDPOINT_TYPE.API,getInvoice(),getAmount(),getTotalTaxesAmount(),getKeySlot(),getMinPinLength(),getMaxPinLength(),new EntryMode(false,false,false,false,false,true),getTimeout(),getExternalData(),null);
        }else if(selectedTransactionType == Global.TRANSACTION_TYPE.CLOSEBATCH){
            return CloseBatchRequest.newCloseBatchIstance(Global.ENDPOINT_TYPE.API);
        }else if(selectedTransactionType == Global.TRANSACTION_TYPE.EBT_FOODPURCHASE){
            return TransactionRequest.newEbtFoodPurchaseInstance(Global.ENDPOINT_TYPE.API,getInvoice(),getAmount(),getKeySlot(),getMinPinLength(),getMaxPinLength(),new EntryMode(false,false,false,false,false,true),getTimeout(),getExternalData(),null);
        }else if(selectedTransactionType == Global.TRANSACTION_TYPE.EBT_CASHPURCHASE){
            return TransactionRequest.newEbtEbtCashPurchaseInstance(Global.ENDPOINT_TYPE.API,getInvoice(),getAmount(),getTotalTaxesAmount(),getKeySlot(),getMinPinLength(),getMinPinLength(),new EntryMode(false,false,false,false,false,true),getTimeout(),getExternalData(),null);
        }else if(selectedTransactionType == Global.TRANSACTION_TYPE.EBT_FOOD_RETURN){
            return TransactionRequest.newEbtFoodReturnInstance(Global.ENDPOINT_TYPE.API,getInvoice(),getAmount(),getKeySlot(),getMinPinLength(),getMaxPinLength(),new EntryMode(false,false,false,false,false,true),getTimeout(),getExternalData(),null);
        }else if(selectedTransactionType == Global.TRANSACTION_TYPE.EBT_BALANCE_INQUIRY){
            return TransactionRequest.newEbtBalanceInquiryInstance(Global.ENDPOINT_TYPE.API,getKeySlot(),getMinPinLength(),getMaxPinLength(),new EntryMode(false,false,false,false,false,true),getTimeout(),getExternalData());
        }else if(selectedTransactionType == Global.TRANSACTION_TYPE.EBT_CASHPURCHASE_CASHBACK){
            return TransactionRequest.newEbtCashPurchaseWithCashbackInstance(Global.ENDPOINT_TYPE.API,getInvoice(),getAmount(),getCashbackAmount(),getTotalTaxesAmount(),getKeySlot(),getMinPinLength(),getMaxPinLength(),new EntryMode(false,false,false,false,false,true),getTimeout(),getExternalData(),null);
        }else if(selectedTransactionType == Global.TRANSACTION_TYPE.EBT_CASHADVANCE){
            return TransactionRequest.newEbtCashAdvanceInstance(Global.ENDPOINT_TYPE.API,getInvoice(),getAmount(),getKeySlot(),getMinPinLength(),getMaxPinLength(),new EntryMode(false,false,false,false,false,true),getTimeout(),getExternalData(),null);
        }else if(selectedTransactionType == Global.TRANSACTION_TYPE.EBT_FOODVOUCHER){
            return TransactionRequest.newEbtFoodVoucherInstance(Global.ENDPOINT_TYPE.API,new VoucherData(getCardNumber(),getVoucherNumber(),getAuthNumber()),getAmount(),getTimeout(),getExternalData(),null);
        }else if(selectedTransactionType == Global.TRANSACTION_TYPE.EBT_CASHVOUCHER){
            return TransactionRequest.newEbtCashVoucherInstance(Global.ENDPOINT_TYPE.API,new VoucherData(getCardNumber(),getVoucherNumber(),getAuthNumber()),getAmount(),getTotalTaxesAmount(),getTimeout(),getExternalData(),null);
        }

        return null;
    }


    private void gotTransactionScreen(){
        configLayout.setVisibility(View.GONE);
        transactionLayout.setVisibility(View.VISIBLE);
        responseLayout.setVisibility(View.GONE);
        btnBack.setVisibility(View.VISIBLE);
        btnNext.setVisibility(View.VISIBLE);
    }

    private void gotConfigurationScreen(){
        configLayout.setVisibility(View.VISIBLE);
        transactionLayout.setVisibility(View.GONE);
        responseLayout.setVisibility(View.GONE);
        btnBack.setVisibility(View.GONE);
        btnNext.setVisibility(View.VISIBLE);
    }

    private void goResponseScreen(){
        configLayout.setVisibility(View.GONE);
        transactionLayout.setVisibility(View.GONE);
        responseLayout.setVisibility(View.VISIBLE);
        btnBack.setVisibility(View.VISIBLE);
        btnNext.setVisibility(View.GONE);

    }

    @Override
    public void onConnectionStatus(ConnectionStatus connectionStatus) {

    }

    @Override
    public void onError(ErrorResponse errorResponse) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvResponse.setText(Functions.parseToJson(errorResponse));
                etTransactionIdResponse.setText("");
                goResponseScreen();
            }
        });

    }

    @Override
    public void onPaymentResponse(TransactionResponse transactionResponse) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvResponse.setText(Functions.parseToJson(transactionResponse));
                etTransactionIdResponse.setText(transactionResponse.getTransactionId());
                goResponseScreen();
            }
        });

    }

    @Override
    public void onVoidResponse(TransactionResponse transactionResponse) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvResponse.setText(Functions.parseToJson(transactionResponse));
                etTransactionIdResponse.setText("");
                goResponseScreen();
            }
        });

    }

    @Override
    public void onCloseBatchResponse(CloseBatchResponse closeBatchResponse) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvResponse.setText(Functions.parseToJson(closeBatchResponse));
                etTransactionIdResponse.setText("");
                goResponseScreen();
            }
        });

    }

    @Override
    public void onTransactionSearchResponse(SearchTransactionsResponse searchTransactionsResponse) {

    }

    @Override
    public void onSalesReportResponse(SalesReportResponse salesReportResponse) {

    }

    @Override
    public void onGetFiscalTokenResponse(FiscalControlTokenResponse fiscalControlTokenResponse) {

    }
}