package bibek.audiorecorder;

import java.io.Serializable;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class MediaPickerActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_picker);

        Serializable media = getIntent().getSerializableExtra("pickedMedia");
        if (media instanceof PickedMedia)
        {
            PickedMedia m = (PickedMedia) media;

            m.setStatus(PickedMedia.STATUS.CHANGED);
            m.setNewPath("This is a new Path");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_media_picker, menu);
        return true;
    }

}
