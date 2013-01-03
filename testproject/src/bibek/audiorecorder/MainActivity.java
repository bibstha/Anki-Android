package bibek.audiorecorder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;

public class MainActivity extends Activity implements OnMenuItemClickListener
{
    private static final String TAG = "MainActivity";
    private static final int RECORD_AUDIO_REQUEST = 1;

    @Override
    public void onCreate(Bundle savedState)
    {
        super.onCreate(savedState);
        // Hide status bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        LinearLayout ll = new LinearLayout(this);
        DummyMenuSelectorButton b = new DummyMenuSelectorButton(this);
        ll.addView(b, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0));
        setContentView(ll);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.field_type_audio:
                Intent audioRecorder = new Intent(this, AudioRecorderActivity.class);
                startActivityForResult(audioRecorder, RECORD_AUDIO_REQUEST);
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED)
        {
            Log.d("MainActivity", "Cancelled");
        }
        else if (requestCode == RECORD_AUDIO_REQUEST)
        {
            Log.d("MainActivity", "" + RECORD_AUDIO_REQUEST);
            FieldValue value = (FieldValue) data.getExtras().get("fieldValue");
            Log.d(TAG, value.getTmpPath());
        }
    }

    public class DummyMenuSelectorButton extends Button
    {
        public DummyMenuSelectorButton(Context context)
        {
            super(context);
            setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    PopupMenu fieldTypeSelectorMenu = new PopupMenu(MainActivity.this, v);
                    MenuInflater inflater = fieldTypeSelectorMenu.getMenuInflater();
                    inflater.inflate(R.menu.edit_note_field_types, fieldTypeSelectorMenu.getMenu());
                    fieldTypeSelectorMenu.setOnMenuItemClickListener(MainActivity.this);
                    fieldTypeSelectorMenu.show();
                }
            });
        }
    }
}
