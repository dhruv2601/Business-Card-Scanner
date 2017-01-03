

package businesscard.dhruv.businesscardscanner;

/**
 * Created by dhruv on 30/12/16.
 */

public class CardObject1 {
    private int mDrawableImage;
    private String txtName;
    private String txtPosition;
    private String txtCompany;

    CardObject1(int drawableImage, String name, String position, String company) {
        mDrawableImage = drawableImage;
        txtName = name;
        txtPosition = position;
        txtCompany = company;
    }

    public int getmDrawableImage() {
        return mDrawableImage;
    }

    // / return 0 here if image is not set by user
    public void setmDrawableImage(int mDrawableImage) {
        this.mDrawableImage = mDrawableImage;
    }

    public void setmService(String mService) {
        this.txtName = mService;
    }

    public String getTxtName() {
        return txtName;
    }

    public void setTxtName(String txtName) {
        this.txtName = txtName;
    }

    public String getTxtPosition() {
        return txtPosition;
    }

    public void setTxtPosition(String txtPosition) {
        this.txtPosition = txtPosition;
    }

    public String getTxtCompany() {
        return txtCompany;
    }

    public void setTxtCompany(String txtCompany) {
        this.txtCompany = txtCompany;
    }
}

