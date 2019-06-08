package com.smlnskgmail.jaman.hashchecker;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.smlnskgmail.jaman.hashchecker.components.BaseActivity;
import com.smlnskgmail.jaman.hashchecker.components.preferences.Constants;
import com.smlnskgmail.jaman.hashchecker.components.preferences.Preferences;
import com.smlnskgmail.jaman.hashchecker.fragments.functionality.MainFragment;
import com.smlnskgmail.jaman.hashchecker.fragments.functionality.history.HistoryFragment;
import com.smlnskgmail.jaman.hashchecker.fragments.info.FeedbackFragment;
import com.smlnskgmail.jaman.hashchecker.fragments.interfaces.OnAppResume;
import com.smlnskgmail.jaman.hashchecker.fragments.interfaces.OnNavigationListener;
import com.smlnskgmail.jaman.hashchecker.fragments.settings.SettingsFragment;
import com.smlnskgmail.jaman.hashchecker.utils.UIUtils;

public class MainActivity extends BaseActivity {

    @Override
    public void initialize() {
        Intent intent = getIntent();
        String scheme = null;
        ClipData clipData = null;
        if (intent != null) {
            scheme = intent.getScheme();
            clipData = intent.getClipData();
        }
        Uri externalFileUri = null;
        if (clipData != null) {
            externalFileUri = clipData.getItemAt(0).getUri();
        }

        MainFragment mainFragment = new MainFragment();
        if (scheme != null && scheme.compareTo(ContentResolver.SCHEME_CONTENT) == 0) {
            mainFragment.setArguments(getConfiguredBundleWithDataUri(intent.getData()));
            Preferences.setGenerateFromShareIntentMode(this, true);
        } else if (externalFileUri != null) {
            mainFragment.setArguments(getConfiguredBundleWithDataUri(externalFileUri));
            Preferences.setGenerateFromShareIntentMode(this, true);
        } else {
            mainFragment.setArguments(getBundleForShortcutAction(intent.getAction()));
            Preferences.setGenerateFromShareIntentMode(this, false);
        }

        UIUtils.showFragment(getSupportFragmentManager(), mainFragment);
    }

    @NonNull
    private Bundle getConfiguredBundleWithDataUri(@NonNull Uri uri) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.Data.URI_FROM_EXTERNAL_APP, uri.toString());
        return bundle;
    }

    @NonNull
    private Bundle getBundleForShortcutAction(@Nullable String action) {
        Bundle shortcutArguments = new Bundle();
        if (action != null && action.equals(Constants.ShortcutActions.ACTION_START_WITH_TEXT_SELECTION)) {
            shortcutArguments.putBoolean(Constants.ShortcutActions.ACTION_START_WITH_TEXT_SELECTION, true);
        } else if (action != null && action.equals(Constants.ShortcutActions.ACTION_START_WITH_FILE_SELECTION)) {
            shortcutArguments.putBoolean(Constants.ShortcutActions.ACTION_START_WITH_FILE_SELECTION, true);
        }
        return shortcutArguments;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        UIUtils.hideKeyboard(this, findViewById(android.R.id.content));
        switch (item.getItemId()) {
            case R.id.menu_main_section_settings:
                UIUtils.showFragment(getSupportFragmentManager(), new SettingsFragment());
                break;
            case R.id.menu_main_section_feedback:
                UIUtils.showFragment(getSupportFragmentManager(), new FeedbackFragment());
                break;
            case R.id.menu_main_section_history:
                UIUtils.showFragment(getSupportFragmentManager(), new HistoryFragment());
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager()
                .findFragmentByTag(Constants.Tags.CURRENT_FRAGMENT_TAG);
        if (fragment instanceof OnNavigationListener) {
            ((OnNavigationListener) fragment).onBack();
        }
        for (Fragment fragmentInApp: getSupportFragmentManager().getFragments()) {
            if (fragmentInApp instanceof OnAppResume) {
                ((OnAppResume) fragmentInApp).onAppResume();
            }
        }
    }

}
