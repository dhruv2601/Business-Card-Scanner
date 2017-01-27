package businesscard.dhruv.businesscardscanner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import uk.co.senab.photoview.PhotoViewAttacher;

public class FullImage extends AppCompatActivity {

    private ImageView fullImage;
    PhotoViewAttacher photoViewAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        fullImage  = (ImageView) findViewById(R.id.full_img);
        fullImage.setImageBitmap(ShowCardDetails.bitmap);

        photoViewAttacher = new PhotoViewAttacher(fullImage);
        photoViewAttacher.update();
    }
}
