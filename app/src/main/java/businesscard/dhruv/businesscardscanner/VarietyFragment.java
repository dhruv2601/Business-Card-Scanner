package businesscard.dhruv.businesscardscanner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidviewhover.BlurLayout;
import com.parse.ParseUser;

public class VarietyFragment extends Fragment {

    private View view;
    private BlurLayout mSampleLayout2, mSampleLayout3, mSampleLayout4, mSampleLayout5;
    private boolean b;
    private AppCompatButton signOut;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (view == null) {
            view = inflater.inflate(R.layout.activity_variety_fragment, container, false);
            BlurLayout.setGlobalDefaultDuration(450);
//            mSampleLayout2 = (BlurLayout) view.findViewById(R.id.blur_layout2);
//            View hover2 = LayoutInflater.from(view.getContext()).inflate(R.layout.hover_sample2, null);
//            hover2.findViewById(R.id.avatar).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(v.getContext(), "Pretty Cool, Right?", Toast.LENGTH_SHORT).show();
//                }
//            });
//            mSampleLayout2.setHoverView(hover2);
//
//            mSampleLayout2.addChildAppearAnimator(hover2, R.id.description, Techniques.FadeInUp);
//            mSampleLayout2.addChildDisappearAnimator(hover2, R.id.description, Techniques.FadeOutDown);
//            mSampleLayout2.addChildAppearAnimator(hover2, R.id.avatar, Techniques.DropOut, 1200);
//            mSampleLayout2.addChildDisappearAnimator(hover2, R.id.avatar, Techniques.FadeOutUp);
//            mSampleLayout2.setBlurDuration(1000);

            signOut = (AppCompatButton) view.findViewById(R.id.signout);
            signOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ParseUser.logOut();
                    Intent i = new Intent(getActivity(), LoginActivity.class);
                    VarietyFragment.this.getActivity().finish();
                    startActivity(i);
                }
            });

            mSampleLayout3 = (BlurLayout) view.findViewById(R.id.blur_layout3);
            View hover3 = LayoutInflater.from(view.getContext()).inflate(R.layout.hover_sample3, null);
            hover3.findViewById(R.id.avatar).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConnectivityManager connectivityManager = (ConnectivityManager) view.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

                    b = (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting());
                    if (b) {
                        Intent i = new Intent(getActivity(), BillingActLib.class);
                        startActivity(i);
                    } else {
                        startActivityForResult(new Intent(
                                Settings.ACTION_WIFI_SETTINGS), 0);
                    }
                }
            });
            mSampleLayout3.setHoverView(hover3);

            mSampleLayout3.addChildAppearAnimator(hover3, R.id.description, Techniques.FadeInUp);
            mSampleLayout3.addChildDisappearAnimator(hover3, R.id.description, Techniques.FadeOutDown);
            mSampleLayout3.addChildAppearAnimator(hover3, R.id.avatar, Techniques.DropOut, 1200);
            mSampleLayout3.addChildDisappearAnimator(hover3, R.id.avatar, Techniques.FadeOutUp);
            mSampleLayout3.setBlurDuration(1000);

            mSampleLayout4 = (BlurLayout) view.findViewById(R.id.blur_layout4);
            View hover4 = LayoutInflater.from(view.getContext()).inflate(R.layout.hover_sample4, null);
            hover4.findViewById(R.id.avatar).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), AboutActivity.class);
                    startActivity(i);
                }
            });
            mSampleLayout4.setHoverView(hover4);

            mSampleLayout4.addChildAppearAnimator(hover4, R.id.description, Techniques.FadeInUp);
            mSampleLayout4.addChildDisappearAnimator(hover4, R.id.description, Techniques.FadeOutDown);
            mSampleLayout4.addChildAppearAnimator(hover4, R.id.avatar, Techniques.DropOut, 1200);
            mSampleLayout4.addChildDisappearAnimator(hover4, R.id.avatar, Techniques.FadeOutUp);
            mSampleLayout4.setBlurDuration(1000);

            mSampleLayout5 = (BlurLayout) view.findViewById(R.id.blur_layout5);
            View hover5 = LayoutInflater.from(view.getContext()).inflate(R.layout.hover_sample5, null);
            hover5.findViewById(R.id.avatar).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "dhruvrathi15@gmail.com", null));
                    v.getContext().startActivity(Intent.createChooser(i, "Send Email..."));
                }
            });
            mSampleLayout5.setHoverView(hover5);

            mSampleLayout5.addChildAppearAnimator(hover5, R.id.description, Techniques.FadeInUp);
            mSampleLayout5.addChildDisappearAnimator(hover5, R.id.description, Techniques.FadeOutDown);
            mSampleLayout5.addChildAppearAnimator(hover5, R.id.avatar, Techniques.DropOut, 1200);
            mSampleLayout5.addChildDisappearAnimator(hover5, R.id.avatar, Techniques.FadeOutUp);
            mSampleLayout5.setBlurDuration(1000);
        }
        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
            if (!wifiManager.isWifiEnabled()) {
                Intent i = new Intent(getContext(), BillingActLib.class);
                startActivity(i);
            } else {
                Toast.makeText(getContext(), "No internet connection available.", Toast.LENGTH_SHORT).show();
                //write your code for any kind of network calling to fetch data
            }
        }
    }
}
