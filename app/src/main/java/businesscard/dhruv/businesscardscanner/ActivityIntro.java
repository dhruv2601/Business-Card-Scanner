package businesscard.dhruv.businesscardscanner;

import android.*;
import android.Manifest;
import android.os.Bundle;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

/**
 * Created by dhruv on 11/2/17.
 */

public class ActivityIntro extends IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFullscreen(true);
        super.onCreate(savedInstanceState);

        String[] perm = {android.Manifest.permission.CAMERA, android.Manifest.permission.ACCESS_NETWORK_STATE, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,  Manifest.permission.CALL_PHONE, Manifest.permission.CAMERA};

        addSlide(new SimpleSlide.Builder()
                .title(" Grant Permissions")
                .description("We will need a few permissions from you. Please grant them if you haven't already. Thank you and enjoy.")
                .image(R.drawable.donatefinal)
                .background(R.color.colorAccent)
                .backgroundDark(R.color.black)
                .permissions(perm)
                .build());
    }
}