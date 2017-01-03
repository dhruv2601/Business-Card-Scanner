package businesscard.dhruv.businesscardscanner;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by dhruv on 3/1/17.
 */

public class CardObjectContacts {
    private String txtName;
    private String txtNum;

    public CardObjectContacts(String txtName, String txtNum) {
        this.txtName = txtName;
        this.txtNum = txtNum;
    }

    public String getTxtName() {
        return txtName;
    }

    public void setTxtName(String txtName) {
        this.txtName = txtName;
    }

    public String getTxtNum() {
        return txtNum;
    }

    public void setTxtNum(String txtNum) {
        this.txtNum = txtNum;
    }
}
