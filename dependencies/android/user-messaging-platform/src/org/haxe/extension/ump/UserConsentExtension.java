package org.haxe.extension.ump;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.FormError;
import com.google.android.ump.UserMessagingPlatform;

import org.haxe.extension.Extension;

public class UserConsentExtension extends Extension {

    private ConsentInformation consentInformation;
    private ConsentForm consentForm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity.setContentView(mainView);

        ConsentDebugSettings debugSettings = new ConsentDebugSettings.Builder(mainContext)
                .setDebugGeography(ConsentDebugSettings
                        .DebugGeography
                        .DEBUG_GEOGRAPHY_EEA)
                .addTestDeviceHashedId("E7E865F465F186D04BE65F022F83C134")
                .build();

        // ConsentRequestParameters params = new ConsentRequestParameters
        //         .Builder()
        //         .setConsentDebugSettings(debugSettings)
        //         .build();


        // Set tag for underage of consent. false means users are not underage.
        ConsentRequestParameters params = new ConsentRequestParameters
                .Builder()
                .setTagForUnderAgeOfConsent(false)
                .build();

        consentInformation = UserMessagingPlatform.getConsentInformation(mainActivity);
        consentInformation.requestConsentInfoUpdate(
                mainActivity,
                params,
                new ConsentInformation.OnConsentInfoUpdateSuccessListener() {
                    @Override
                    public void onConsentInfoUpdateSuccess() {
                        // The consent information state was updated.
                        // You are now ready to check if a form is available.
                        if (consentInformation.isConsentFormAvailable()) {
                            loadForm();
                        }
                    }
                },
                new ConsentInformation.OnConsentInfoUpdateFailureListener() {
                    @Override
                    public void onConsentInfoUpdateFailure(FormError formError) {
                        // Handle the error.
                        Log.d("UserConsentExtension","DANS onConsentInfoUpdateFailure : " + formError.getMessage());
                    }
                });
    }

    public void loadForm() {
        UserMessagingPlatform.loadConsentForm(
                mainContext,
                new UserMessagingPlatform.OnConsentFormLoadSuccessListener() {
                    @Override
                    public void onConsentFormLoadSuccess(ConsentForm consentForm) {
                        UserConsentExtension.this.consentForm = consentForm;
                        if (consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.REQUIRED) {
                            consentForm.show(
                                    UserConsentExtension.this.mainActivity,
                                    new ConsentForm.OnConsentFormDismissedListener() {
                                        @Override
                                        public void onConsentFormDismissed(@Nullable FormError formError) {
                                            // Handle dismissal by reloading form.
                                            loadForm();
                                        }
                                    });
                        }
                    }
                },
                new UserMessagingPlatform.OnConsentFormLoadFailureListener() {
                    @Override
                    public void onConsentFormLoadFailure(FormError formError) {
                        // Handle the error
                        Log.d("UserConsentExtension","DANS onConsentFormLoadFailure : " + formError.getMessage());
                    }
                }
        );
    }


}
