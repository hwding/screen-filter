package com.amastigote.darker.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.amastigote.darker.R;

public class AboutActivity extends AppCompatActivity {
    TextView versionCodeTextView;
    Button licenseButton;
    Button feedbackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        versionCodeTextView = (TextView) findViewById(R.id.aa_versionCode);
        licenseButton = (Button) findViewById(R.id.aa_licenseButton);
        feedbackButton = (Button) findViewById(R.id.aa_feedBackButton);

        versionCodeTextView.setText(getVersionCode());

        licenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(AboutActivity.this)
                        .setTitle(R.string.license)
                        .setView(R.layout.activity_license)
                        .setPositiveButton(R.string.positive, null)
                        .create()
                        .show();
            }
        });

        feedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:m@amastigote.com"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "feedback from ScreenFilter " + getVersionCode());
                startActivity(intent);
            }
        });
    }

    private String getVersionCode() {
        try {
            PackageManager packageManager = this.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(this.getPackageName(), PackageManager.GET_GIDS);
            return packageInfo.versionName;
        } catch (Exception ignored) {}
        return null;
    }
}
