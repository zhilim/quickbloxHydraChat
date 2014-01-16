package com.zhilim.hydrachat;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import com.quickblox.core.QBCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.core.result.Result;
import com.quickblox.module.auth.QBAuth;
import com.quickblox.module.auth.result.QBSessionResult;
import com.zhilim.hydrachat.ActivityCallUser;
import com.zhilim.hydrachat.models.DataHolder;
import com.zhilim.hydrachat.R;


public class MainActivity extends Activity {
	
	private final String FIRST_USER_PASSWORD = "johnwatson";
    private final String FIRST_USER_LOGIN = "johnwatson";
    private final String SECOND_USER_PASSWORD = "sherlock";
    private final String SECOND_USER_LOGIN = "sherlock";
    private final String THIRD_USER_PASSWORD = "moriarty";
    private final String THIRD_USER_LOGIN = "moriarty";
    
    private final int thirdUserId = 788109;
    private final int firstUserId = 788114;
    private final String firstUserName = "John Watson";
    private final String secondUserName = "S Holmes";
    private final String thirdUserName = "Dr.Moriarty";
    private final int secondUserId = 782652;

    private ProgressDialog progressDialog;
    
	@Override
	protected void onCreate(Bundle savedInstanceState){
		//initialize the screen to main menu
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//initialize and prepare the please wait progress dialog for later use
		progressDialog = new ProgressDialog(this);
	    progressDialog.setMessage(getString(R.string.please_wait));
	    progressDialog.setCancelable(false);
	    
	    //initialize and rig the login buttons on the main menu.
	    findViewById(R.id.loginUser1).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                progressDialog.show();
                createSession(FIRST_USER_LOGIN, FIRST_USER_PASSWORD);
            }
        });

        findViewById(R.id.loginUser2).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                progressDialog.show();
                createSession(SECOND_USER_LOGIN, SECOND_USER_PASSWORD);
            }
        });
        
        findViewById(R.id.loginUser3).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				progressDialog.show();
				createSession(THIRD_USER_LOGIN, THIRD_USER_PASSWORD);
				
			}
		});
        
        //Authenticate with quickblox cloud to use their server. Set Quickblox credentials here.
        QBSettings.getInstance().fastConfigInit("6345", "76bJpvhtaayhSjY", "QatRAh6smETmrft");

	}
	
	@Override
	public void onResume(){
	      progressDialog.dismiss();
	      super.onResume();
	}
	
	private void createSession(String login, final String password){
		
		//authentication successful therefore create session with user.
		 QBAuth.createSession(login, password, new QBCallbackImpl(){
	            @Override
	            public void onComplete(Result result){
	                if (result.isSuccess()){
	                    // save current user
	                    DataHolder.getInstance().setCurrentQbUser(((QBSessionResult) result).getSession().getUserId(), password);

	                    // show next activity
	                    showCallUserActivity();
	                }
	            }
	        });
	}

	private void showCallUserActivity() {
        Intent intent = new Intent(this, ActivityCallUser.class);
        
        intent.putExtra("secondId", secondUserId);
        intent.putExtra("secondUser", secondUserName);
        intent.putExtra("firstId", firstUserId);
        intent.putExtra("firstUser", firstUserName);
        intent.putExtra("thirdId", thirdUserId);
        intent.putExtra("thirdUser", thirdUserName);
        
        intent.putExtra("myID", DataHolder.getInstance().getCurrentQbUser().getId());
        if(DataHolder.getInstance().getCurrentQbUser().getId() == firstUserId)
        	intent.putExtra("myName", firstUserName);
        if(DataHolder.getInstance().getCurrentQbUser().getId() == secondUserId)
        	intent.putExtra("myName", secondUserName);
        if(DataHolder.getInstance().getCurrentQbUser().getId() == thirdUserId)
        	intent.putExtra("myName", thirdUserName);
        
        startActivity(intent);
        finish();
    }

}
