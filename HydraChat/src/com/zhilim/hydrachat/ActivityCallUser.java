package com.zhilim.hydrachat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.quickblox.module.videochat.model.utils.Debugger;
import com.zhilim.hydrachat.R;
import com.zhilim.hydrachat.models.DataHolder;
import com.zhilim.hydrachat.model.listener.OnCallDialogListener;
import com.zhilim.hydrachat.model.utils.DialogHelper;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.module.videochat.core.service.QBVideoChatService;
import com.quickblox.module.videochat.model.listeners.OnQBVideoChatListener;
import com.quickblox.module.videochat.model.objects.CallState;
import com.quickblox.module.videochat.model.objects.CallType;
import com.quickblox.module.videochat.model.objects.VideoChatConfig;
import com.zhilim.hydrachat.ActivityVideoChat;


public class ActivityCallUser extends Activity{
	
	 private static final String TAG = ActivityCallUser.class.getSimpleName();
	 private ProgressDialog progressDialog;
	 private Toast callError, callRejected, connecting, stopCall;
	 private Button videoCallBtn;
	 private QBUser qbUser;
	 private boolean isCanceledVideoCall;
	 private VideoChatConfig videoChatConfig1, videoChatConfig2, receivedConfig;
	 private TextView txtName;
	 private int oppId1, oppId2;
	 private String oppName1, oppName2, myName;
	 private QBUser opp1, opp2;
	 
	 
	 @Override
	 protected void onCreate(Bundle savedInstanceState){
	     super.onCreate(savedInstanceState);
	     setContentView(R.layout.call_user_layout);
	     initViews();
	 }
	 
	 private void initViews(){
		 //retrieve user data from the database to set up the layout/buttons
	     
	     myName = getIntent().getStringExtra("myName");
	     
	     if(myName.equals(getIntent().getStringExtra("firstUser"))){
	    	 oppId1 = getIntent().getIntExtra("secondId", 0);
	    	 oppName1 = getIntent().getStringExtra("secondUser");
	    	 opp1 = new QBUser(oppId1);
	    	 
	    	 oppId2 = getIntent().getIntExtra("thirdId", 0);
	    	 oppName2 = getIntent().getStringExtra("thirdUser");
	    	 opp2 = new QBUser(oppId2);
	     }
	     
	     if(myName.equals(getIntent().getStringExtra("secondUser"))){
	    	 oppId1 = getIntent().getIntExtra("firstId", 0);
	    	 oppName1 = getIntent().getStringExtra("firstUser");
	    	 opp1 = new QBUser(oppId1);
	    	 
	    	 oppId2 = getIntent().getIntExtra("thirdId", 0);
	    	 oppName2 = getIntent().getStringExtra("thirdUser");
	    	 opp2 = new QBUser(oppId2);
	     }
	     
	     if(myName.equals(getIntent().getStringExtra("thirdUser"))){
	    	 oppId1 = getIntent().getIntExtra("firstId", 0);
	    	 oppName1 = getIntent().getStringExtra("firstUser");
	    	 opp1 = new QBUser(oppId1);
	    	 
	    	 oppId2 = getIntent().getIntExtra("secondId", 0);
	    	 oppName2 = getIntent().getStringExtra("secondUser");
	    	 opp2 = new QBUser(oppId2);
	     }
	     
	  
	     isCanceledVideoCall = true;
	     videoChatConfig1 = new VideoChatConfig();
	     
	     txtName = (TextView) findViewById(R.id.textView1);
	     videoCallBtn = (Button) findViewById(R.id.callButton);
	     progressDialog = new ProgressDialog(this);
	     progressDialog.setMessage(getString(R.string.calling_dialog_title));
	     
	     CharSequence callErrorText = "Couldn't Make the Call.";
	     CharSequence callRejectText = "Your Call has been Rejected";
	     CharSequence connectingText = "Connecting your call!";
	     CharSequence stopText = "You stopped the Call!";
	     final CharSequence callEnded = "Call Ended";
	     callError = Toast.makeText(this, callErrorText, Toast.LENGTH_LONG);
	     callRejected = Toast.makeText(this, callRejectText, Toast.LENGTH_LONG);
	     connecting = Toast.makeText(this, connectingText, Toast.LENGTH_LONG);
	     stopCall = Toast.makeText(this, stopText, Toast.LENGTH_LONG);
	     
	     txtName.setText("You logged in as " + myName);
	     videoCallBtn.setText("Call " + oppName1 + " and " + oppName2);
	     
	     progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener(){
	     @Override
	     public void onDismiss(DialogInterface dialogInterface){
	    	 if (isCanceledVideoCall){
	                System.out.println("Video Call has been stopped for some reason.");
	                try{
	                	QBVideoChatService.getService().stopCalling(videoChatConfig1);
	                	QBVideoChatService.getService().stopCalling(videoChatConfig2);
	                }catch(Exception e){
	                	//ignore
	                }
	                Toast.makeText(getBaseContext(), callEnded, Toast.LENGTH_LONG).show();
	             }
	         }
	     });
	     
	     videoCallBtn.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                if (progressDialog != null && !progressDialog.isShowing()){
	                    progressDialog.show();
	                }
	                System.out.println("Trying to start video call. fingers crossed!");
	                try{
	                videoChatConfig1 = QBVideoChatService.getService().callUser(opp1, CallType.VIDEO_AUDIO, null);
	                videoChatConfig2 = QBVideoChatService.getService().callUser(opp2, CallType.VIDEO_AUDIO, null);
	                }catch(Exception e){
	                	progressDialog.dismiss();
	                	callError.show();
	                }
	            }
	        });
	     
	     QBUser currentQbUser = DataHolder.getInstance().getCurrentQbUser();
	        Debugger.logConnection("setQBVideoChatListener: " + (currentQbUser == null));
	        try {
	        	System.out.println("Setting the VClistener");
	            QBVideoChatService.getService().setQBVideoChatListener(currentQbUser, qbVideoChatListener);
	        } catch (Exception e){
	            /*IGNORE*/
	        }
	             
	 }
	 
	 //set the chat listener to receive calls, sense if call has been received, etc
	 private OnQBVideoChatListener qbVideoChatListener = new OnQBVideoChatListener() {

	        @Override
	        public void onVideoChatStateChange(CallState state, VideoChatConfig receivedVideoChatConfig) {
	            receivedConfig = receivedVideoChatConfig;
	            isCanceledVideoCall = false;
	            switch (state) {
	                case ON_CALLING:
	                    showCallDialog();
	                    break;
	                case ON_ACCEPT_BY_USER:
	                    progressDialog.dismiss();
	                    connecting.show();
	                    startVideoChatActivity();
	                    break;
	                case ON_REJECTED_BY_USER:
	                    progressDialog.dismiss();
	                    callRejected.show();
	                    break;
	                case ON_DID_NOT_ANSWERED:
	                    progressDialog.dismiss();
	                    callError.show();
	                    break;
	                case ON_CANCELED_CALL:
	                	stopCall.show();
	                    isCanceledVideoCall = true;
	                    if(receivedConfig == videoChatConfig1){
	                    	receivedConfig = null;
	                    	videoChatConfig1 = null;
	                    }
	                    else if(receivedConfig == videoChatConfig2){
	                    	receivedConfig = null;
	                    	videoChatConfig2 = null;
	                    }
	                    
	                    break;
	                case ON_START_CONNECTING:
	                    progressDialog.dismiss();
	                    connecting.show();
	                    startVideoChatActivity();
	                    break;
	            }
	        }
	    };
	    
	    private void showCallDialog() {
	        DialogHelper.showCallDialog(this, new OnCallDialogListener() {
	            @Override
	            public void onAcceptCallClick() {
	                if (receivedConfig == null) {
	                    Toast.makeText(getBaseContext(), getString(R.string.call_start_txt), Toast.LENGTH_SHORT).show();
	                    return;
	                }
	                QBVideoChatService.getService().acceptCall(receivedConfig);
	            }

	            @Override
	            public void onRejectCallClick() {
	                if (receivedConfig == null) {
	                    Toast.makeText(getBaseContext(), getString(R.string.call_canceled_txt), Toast.LENGTH_SHORT).show();
	                    return;
	                }
	                QBVideoChatService.getService().rejectCall(receivedConfig);
	            }
	        });
	    }

	    @Override
	    public void onResume() {
	        try {
	            QBVideoChatService.getService().setQbVideoChatListener(qbVideoChatListener);
	        } catch (NullPointerException ex) {
	            ex.printStackTrace();
	        }
	        super.onResume();
	    }


	    private void startVideoChatActivity() {
	        Intent intent = new Intent(getBaseContext(), ActivityVideoChat.class);
	        intent.putExtra(VideoChatConfig.class.getCanonicalName(), receivedConfig);
	        startActivity(intent);
	    }


	    @Override
	    public void onDestroy() {
	        Log.v(TAG, "onDestroy");
	        stopService(new Intent(getApplicationContext(), QBVideoChatService.class));
	        super.onDestroy();
	    }

}
