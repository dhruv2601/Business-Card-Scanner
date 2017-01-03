package businesscard.dhruv.businesscardscanner;

/**
 * Created by dhruv on 30/12/16.
 */

public class CardObject {
    private int mDrawableImage;
    private String txtEntry;
    private String txtEntryTitle;

    CardObject(int drawableImage, String name, String position) {
        mDrawableImage = drawableImage;
        txtEntry = name;
        txtEntryTitle = position;
    }

    public int getmDrawableImage() {
        return mDrawableImage;
    }

    public void setmDrawableImage(int mDrawableImage) {
        this.mDrawableImage = mDrawableImage;
    }

    public String getTxtEntry() {
        return txtEntry;
    }

    public void setTxtEntry(String txtEntry) {
        this.txtEntry = txtEntry;
    }

    public String getTxtEntryTitle() {
        return txtEntryTitle;
    }

    public void setTxtEntryTitle(String txtEntryTitle) {
        this.txtEntryTitle = txtEntryTitle;
    }
}
