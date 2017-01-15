package businesscard.dhruv.businesscardscanner;

import android.widget.TextView;

/**
 * Created by dhruv on 11/1/17.
 */

public class DataObjectCardEntry {
    private String entryType;
    private String entryDetails;

    public DataObjectCardEntry(String entryType, String entryDetails) {
        this.entryType = entryType;
        this.entryDetails = entryDetails;
    }

    public String getEntryType() {
        return entryType;
    }

    public void setEntryType(String entryType) {
        this.entryType = entryType;
    }

    public String getEntryDetails() {
        return entryDetails;
    }

    public void setEntryDetails(String entryDetails) {
        this.entryDetails = entryDetails;
    }
}
