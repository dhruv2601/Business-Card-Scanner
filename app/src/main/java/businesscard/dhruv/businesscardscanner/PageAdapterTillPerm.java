package businesscard.dhruv.businesscardscanner;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

/**
 * Created by dhruv on 27/12/16.
 */

public class PageAdapterTillPerm extends FragmentStatePagerAdapter {
    private static final String TAG = "pagerAdapter";
    public static int mNumOfTabs;

    public PageAdapterTillPerm(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.mNumOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        Log.d(TAG, "pos: " + position);
        switch (position) {
            case 0:
                return new AllCardsFragment();

            case 1:
                return new AllCardsFragment();

            case 2:
                return new AllCardsFragment();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
