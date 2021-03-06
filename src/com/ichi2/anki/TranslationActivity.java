package com.ichi2.anki;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ichi2.anki.glosbe.json.Meaning;
import com.ichi2.anki.glosbe.json.Phrase;
import com.ichi2.anki.glosbe.json.Response;
import com.ichi2.anki.glosbe.json.Tuc;
import com.ichi2.anki.htmlutils.Unescaper;
import com.ichi2.anki.runtimetools.TaskOperations;
import com.ichi2.anki.web.HttpFetcher;

/**
 * @author zaur
 * 
 *         Activity used now with Glosbe.com to enable translation of words.
 * 
 */
public class TranslationActivity extends FragmentActivity implements DialogInterface.OnClickListener, OnCancelListener
{

    // Something to translate
    public static final String EXTRA_SOURCE = "translation.activity.extra.source";
    // Translated result
    public static final String EXTRA_TRANSLATION = "translation.activity.extra.translation";

    String mSource;
    String mTranslation;
    private LanguagesListerGlosbe mLanguageLister;
    private Spinner mSpinnerFrom;
    private Spinner mSpinnerTo;
    private ProgressDialog progressDialog = null;
    private String mWebServiceAddress;
    private ArrayList<String> mPossibleTranslations;
    private String mLangCodeTo;
    private BackgroundPost mTranslationLoadPost = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation);

        try
        {
            mSource = getIntent().getExtras().getString(EXTRA_SOURCE).toString();
        }
        catch (Exception e)
        {
            mSource = "";
        }

        // If translation fails this is a default - source will be returned.
        mTranslation = mSource;

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.MainLayoutInTranslationActivity);

        TextView tv = new TextView(this);
        tv.setText(getText(R.string.multimedia_editor_trans_poweredglosbe));
        linearLayout.addView(tv);

        TextView tvFrom = new TextView(this);
        tvFrom.setText(getText(R.string.multimedia_editor_trans_from));
        linearLayout.addView(tvFrom);

        mLanguageLister = new LanguagesListerGlosbe(this);

        mSpinnerFrom = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                mLanguageLister.getLanguages());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerFrom.setAdapter(adapter);
        linearLayout.addView(mSpinnerFrom);

        TextView tvTo = new TextView(this);
        tvTo.setText(getText(R.string.multimedia_editor_trans_to));
        linearLayout.addView(tvTo);

        mSpinnerTo = new Spinner(this);
        ArrayAdapter<String> adapterTo = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                mLanguageLister.getLanguages());
        adapterTo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerTo.setAdapter(adapterTo);
        linearLayout.addView(mSpinnerTo);

        Button btnDone = new Button(this);
        btnDone.setText(getText(R.string.multimedia_editor_trans_translate));
        btnDone.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                translate();
            }
        });

        linearLayout.addView(btnDone);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_translation, menu);
        return true;
    }

    private class BackgroundPost extends AsyncTask<Void, Void, String>
    {

        @Override
        protected String doInBackground(Void... params)
        {
            return HttpFetcher.fetchThroughHttp(mWebServiceAddress);
        }

        @Override
        protected void onPostExecute(String result)
        {
            progressDialog.dismiss();
            mTranslation = result;
            showPickTranslationDialog();
        }

    }

    protected void translate()
    {

        progressDialog = ProgressDialog.show(this, getText(R.string.multimedia_editor_progress_wait_title),
                getText(R.string.multimedia_editor_trans_translating_online), true, false);
        
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(this);

        mWebServiceAddress = computeAddress();

        try
        {
            mTranslationLoadPost = new BackgroundPost();
            mTranslationLoadPost.execute();
        }
        catch (Exception e)
        {
            progressDialog.dismiss();
            showToast(getText(R.string.multimedia_editor_something_wrong));
        }
    }

    private String computeAddress()
    {
        String address = "http://glosbe.com/gapi/translate?from=FROMLANG&dest=TOLANG&format=json&phrase=SOURCE&pretty=true";

        String strFrom = mSpinnerFrom.getSelectedItem().toString();
        // Conversion to iso, lister created before.
        String langCodeFrom = mLanguageLister.getCodeFor(strFrom);

        String strTo = mSpinnerTo.getSelectedItem().toString();
        mLangCodeTo = mLanguageLister.getCodeFor(strTo);

        String query;

        try
        {
            query = URLEncoder.encode(mSource, "utf-8");
        }
        catch (UnsupportedEncodingException e)
        {
            query = mSource.replace(" ", "%20");
        }

        address = address.replaceAll("FROMLANG", langCodeFrom).replaceAll("TOLANG", mLangCodeTo)
                .replaceAll("SOURCE", query);

        return address;
    }

    private void showPickTranslationDialog()
    {
        if (mTranslation.startsWith("FAILED"))
        {
            returnFailure(getText(R.string.multimedia_editor_trans_getting_failure).toString());
        }

        Gson gson = new Gson();
        Response resp = gson.fromJson(mTranslation, Response.class);

        if (!resp.getResult().contentEquals("ok"))
        {
            returnFailure(getText(R.string.multimedia_editor_trans_getting_failure).toString());
        }

        mPossibleTranslations = parseJson(resp, mLangCodeTo);

        if (mPossibleTranslations.size() == 0)
        {
            returnFailure(getText(R.string.multimedia_editor_error_word_not_found).toString());
        }

        PickStringDialogFragment fragment = new PickStringDialogFragment();

        fragment.setChoices(mPossibleTranslations);
        fragment.setOnclickListener(this);
        fragment.setTitle(getText(R.string.multimedia_editor_trans_pick_translation).toString());

        fragment.show(this.getSupportFragmentManager(), "pick.translation");

    }

    private static ArrayList<String> parseJson(Response resp, String languageCodeTo)
    {
        ArrayList<String> res = new ArrayList<String>();

        /*
         * The algorithm below includes the parsing of glosbe results.
         * 
         * Glosbe.com returns a list of different phrases in source and
         * destination languages. This is done, probably, to improve the
         * reader's understanding.
         * 
         * We leave here only the translations to the destination language.
         */

        List<Tuc> tucs = resp.getTuc();

        for (Tuc tuc : tucs)
        {
            if (tuc == null)
            {
                continue;
            }
            List<Meaning> meanings = tuc.getMeanings();
            if (meanings != null)
            {
                for (Meaning meaning : meanings)
                {
                    if (meaning == null)
                    {
                        continue;
                    }
                    if (meaning.getLanguage() == null)
                    {
                        continue;
                    }
                    if (meaning.getLanguage().contentEquals(languageCodeTo))
                    {
                        String unescappedString = Unescaper.unescapeHTML(meaning.getText());
                        res.add(unescappedString);
                    }
                }
            }

            Phrase phrase = tuc.getPhrase();
            if (phrase != null)
            {
                if (phrase.getLanguageCode() == null)
                {
                    continue;
                }
                if (phrase.getLanguageCode().contentEquals(languageCodeTo))
                {
                    String unescappedString = Unescaper.unescapeHTML(phrase.getText());
                    res.add(unescappedString);
                }
            }

        }

        return res;
    }

    private void returnTheTranslation()
    {
        Intent resultData = new Intent();

        resultData.putExtra(EXTRA_TRANSLATION, mTranslation);

        setResult(RESULT_OK, resultData);

        finish();
    }

    private void returnFailure(String explanation)
    {
        showToast(explanation);
        setResult(RESULT_CANCELED);
        dismissCarefullyProgressDialog();
        finish();
    }

    private void showToast(CharSequence text)
    {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this, text, duration);
        toast.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which)
    {
        mTranslation = mPossibleTranslations.get(which);
        returnTheTranslation();
    }

    @Override
    public void onCancel(DialogInterface dialog)
    {
        stopWorking();
    }

    private void stopWorking()
    {
        TaskOperations.stopTaskGracefully(mTranslationLoadPost);
        dismissCarefullyProgressDialog();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        stopWorking();
    }

    private void dismissCarefullyProgressDialog()
    {
        try
        {
            if (progressDialog != null)
            {
                if (progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }
            }
        }
        catch (Exception e)
        {
            // nothing is done intentionally
        }
    }

}
