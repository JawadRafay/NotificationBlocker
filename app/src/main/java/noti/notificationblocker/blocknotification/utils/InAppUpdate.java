package noti.notificationblocker.blocknotification.utils;

import static android.app.Activity.RESULT_CANCELED;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;

import noti.notificationblocker.blocknotification.ModelClasses.Utils;

public class InAppUpdate {

    private Activity parentActivity;
    private final AppUpdateManager appUpdateManager;
    private final int appUpdateType = AppUpdateType.FLEXIBLE;
    private final int MY_REQUEST_CODE = 500;

    public InAppUpdate(Activity parentActivity) {
        this.parentActivity = parentActivity;
        appUpdateManager = AppUpdateManagerFactory.create(parentActivity);
    }

    InstallStateUpdatedListener updatedListener = installState -> {

        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            popupSnackBarForCompleteUpdate();
        }
    };

    public void checkForUpdate() {
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            boolean isAppUpdateAvailable = appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE;
            boolean isUpdateAllowed = appUpdateInfo.isUpdateTypeAllowed(appUpdateType);

            if (isAppUpdateAvailable && isUpdateAllowed) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            appUpdateType,
                            parentActivity,
                            MY_REQUEST_CODE);

                } catch (IntentSender.SendIntentException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        appUpdateManager.registerListener(updatedListener);

    }

    public void onActivityResult(int requestCode, int resultCode) {
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode == RESULT_CANCELED) {
                Utils.showErrorDialogue(parentActivity, "Update Canceled");
            } else if (resultCode == Activity.RESULT_OK) {
                checkForUpdate();
            }
        }
    }

    private void popupSnackBarForCompleteUpdate() {
        Snackbar.make(parentActivity.findViewById(android.R.id.content),
                "Update Installed",
                Snackbar.LENGTH_INDEFINITE
        ).setAction(
                "RESTART", view -> {

                    if (appUpdateManager != null){

                        appUpdateManager.completeUpdate();

                    }
                }
        ).show();
    }

    public void onResume() {
        if (appUpdateManager != null) {
            appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    popupSnackBarForCompleteUpdate();
                }
            });
        }
    }

    public void onDestroy() {
        if (appUpdateManager != null) {
            appUpdateManager.unregisterListener(updatedListener);
        }
    }

}
