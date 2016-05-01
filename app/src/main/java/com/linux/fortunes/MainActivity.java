package com.linux.fortunes;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.AppEventsLogger;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.linux.fortunes.app.BuildConfig;
import com.linux.fortunes.app.FortunesApplication;
import com.linux.fortunes.app.R;
import com.linux.fortunes.bean.FortuneBean;
import com.linux.fortunes.model.Fortune;

import org.apache.http.protocol.HTTP;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mFortuneText;
    private ProgressBar mProgressBar;

    private ShareActionProvider mShareActionProvider;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadFortune();
        }
    };

    private Fortune mFortune;

    private UiLifecycleHelper uiHelper;

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(FortunesApplication.FortunesInsertionFinish);
        registerReceiver(mReceiver, filter);
        uiHelper.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uiHelper = new UiLifecycleHelper(this, null);
        uiHelper.onCreate(savedInstanceState);

        mFortuneText = (TextView) findViewById(R.id.fortune_text);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        findViewById(R.id.btn_whatsapp).setOnClickListener(this);
        findViewById(R.id.btn_facebook).setOnClickListener(this);
        findViewById(R.id.btn_facebook_messenger).setOnClickListener(this);
        findViewById(R.id.btn_google_plus).setOnClickListener(this);
        findViewById(R.id.btn_hangouts).setOnClickListener(this);
        findViewById(R.id.btn_linkedin).setOnClickListener(this);
        findViewById(R.id.btn_twitter).setOnClickListener(this);
        findViewById(R.id.btn_viber).setOnClickListener(this);
        findViewById(R.id.btn_wechat).setOnClickListener(this);


        Intent extras = getIntent();
        if (savedInstanceState != null && savedInstanceState.containsKey(FortuneBean.TABLE_NAME)) {
            mFortune = (Fortune) savedInstanceState.getSerializable(FortuneBean.TABLE_NAME);
            updateFortuneTextView();
        } else if (extras != null && extras.hasExtra((FortuneBean.TABLE_NAME))) {
            mFortune = (Fortune) extras.getSerializableExtra(FortuneBean.TABLE_NAME);
            updateFortuneTextView();
        } else {
            loadFortune();
        }

        findViewById(R.id.horizontal_scroll_view).setVisibility(BuildConfig.HAS_SOCIAL_SHARING ? View.VISIBLE : View.GONE);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && intent.getExtras() != null) {
            setIntent(intent);
            mFortune = (Fortune) intent.getSerializableExtra(FortuneBean.TABLE_NAME);
            updateFortuneTextView();
        }
    }

    private void loadFortune() {
        if (mProgressBar != null && mProgressBar.getVisibility() != View.VISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
        mFortune = FortuneBean.getRandomFortune();
        updateShareIntent();
        updateWidget();
        updateFortuneTextView();
    }

    private void updateWidget() {
        // Create an Intent to update any widgets with the new fortune
        Intent intent = new Intent();
        intent.setAction(FortunesApplication.ACTION_UPDATE_CLICK);
        intent.putExtra(FortuneBean.TABLE_NAME, mFortune);
        sendBroadcast(intent);
    }

    private void updateShareIntent() {
        if (mShareActionProvider != null && mFortune != null) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType(HTTP.PLAIN_TEXT_TYPE);
            intent.putExtra(Intent.EXTRA_TEXT, mFortune.toString());
            mShareActionProvider.setShareIntent(intent);
        }
    }

    private void updateFortuneTextView() {
        if (mFortune != null) {
            mFortuneText.setText(mFortune.toString());
            if (mProgressBar != null) {
                mProgressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(FortuneBean.TABLE_NAME, mFortune);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
        uiHelper.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);
        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        updateShareIntent();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_new:
                loadFortune();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        if (mFortune == null) {
            Toast.makeText(this, R.string.not_fortune_loaded_yet, Toast.LENGTH_SHORT).show();
            return;
        }
        final String APPLICATION_ID = "{applicationId}";
        String link = getString(R.string.app_store).replace(APPLICATION_ID, getPackageName());
        String picUrl = getString(R.string.app_store_pic_url).replace(APPLICATION_ID, getPackageName());

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType(HTTP.PLAIN_TEXT_TYPE);
        sendIntent.putExtra(Intent.EXTRA_TEXT, mFortune.toString().trim() + " " + link);
        String socialAppPackageName = null;
        switch (view.getId()) {
            case R.id.btn_facebook:
                shareOnFacebook(link, picUrl);
                break;
            case R.id.btn_facebook_messenger:
                shareOnFacebookMessenger(link, picUrl);
                break;
            case R.id.btn_whatsapp:
                socialAppPackageName = getString(R.string.whatsapp_main_package);
                break;
            case R.id.btn_google_plus:
                socialAppPackageName = getString(R.string.google_plus_main_package);
                break;
            case R.id.btn_hangouts:
                socialAppPackageName = getString(R.string.hangouts_main_package);
                break;
            case R.id.btn_linkedin:
                socialAppPackageName = getString(R.string.linkedin_main_package);
                break;
            case R.id.btn_twitter:
                socialAppPackageName = getString(R.string.twitter_main_package);
                break;
            case R.id.btn_viber:
                socialAppPackageName = getString(R.string.viber_main_package);
                break;
            case R.id.btn_wechat:
                socialAppPackageName = getString(R.string.wechat_main_package);
                break;
        }

        if (!TextUtils.isEmpty(socialAppPackageName)) {
            try {
                sendIntent.setPackage(socialAppPackageName);
                startActivity(sendIntent);
            } catch (ActivityNotFoundException e) {
                openGooglePlayIfPossible(socialAppPackageName);
            } catch (Exception e1) {
                Toast.makeText(this, getString(R.string.failed_to_share), Toast.LENGTH_SHORT).show();
                Crashlytics.logException(e1);
            }
        }
    }

    private void shareOnFacebook(String link, String picUrl) {
        // Publish the post using the Share Dialog
        FacebookDialog.ShareDialogBuilder shareDialogBuilder = new FacebookDialog.ShareDialogBuilder(this)
                .setCaption(getString(R.string.app_name))
                .setName(getString(R.string.app_name))
                .setDescription(mFortune.toString())
                .setLink(link)
                .setPicture(picUrl);

        if (shareDialogBuilder.canPresent()) {
            uiHelper.trackPendingDialogCall(shareDialogBuilder.build().present());
        } else {
            Toast.makeText(this, getString(R.string.facebook_not_installed_or_outdated), Toast.LENGTH_SHORT).show();
            openGooglePlayIfPossible(getString(R.string.facebook_main_package));
        }
    }

    private void shareOnFacebookMessenger(String link, String picUrl) {
        // Check if the Facebook app is installed and we can present the share dialog
        FacebookDialog.MessageDialogBuilder messageDialogBuilder = new FacebookDialog.MessageDialogBuilder(this)
                .setCaption(getString(R.string.app_name))
                .setName(getString(R.string.app_name))
                .setLink(link)
                .setPicture(picUrl)
                .setDescription(mFortune.toString());

        // If the Facebook app is installed and we can present the share dialog
        if (messageDialogBuilder.canPresent()) {
            uiHelper.trackPendingDialogCall(messageDialogBuilder.build().present());
        } else {
            Toast.makeText(this, getString(R.string.facebook_messenger_not_installed_or_outdated), Toast.LENGTH_SHORT).show();
            openGooglePlayIfPossible(getString(R.string.facebook_messenger_main_package));
        }
    }

    private void openGooglePlayIfPossible(String packageName) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
        } catch (ActivityNotFoundException anfe) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)));
            } catch (Exception e1) {
                Toast.makeText(this, getString(R.string.social_network_app_not_found), Toast.LENGTH_SHORT).show();
                Crashlytics.logException(e1);
            }
        } catch (Exception e1) {
            Toast.makeText(this, getString(R.string.social_network_app_not_found), Toast.LENGTH_SHORT).show();
            Crashlytics.logException(e1);
        }
    }
}
