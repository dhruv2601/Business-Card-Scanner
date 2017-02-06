package businesscard.dhruv.businesscardscanner;

import com.parse.Parse;

/**
 * Created by dhruv on 7/2/17.
 */

public class ParseInit extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("Mh9fvVwsWq5DcPVk3lGo9sKixRUuux0d6hqyDY1Y")
                .server("https://parseapi.back4app.com/")
                .clientKey("REWqvRAhPWnuLJajk6XQq58vzQtPBdeESEhWZsO6")
                .build());

    }
}
