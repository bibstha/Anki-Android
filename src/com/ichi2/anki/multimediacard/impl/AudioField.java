package com.ichi2.anki.multimediacard.impl;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ichi2.anki.AnkiDroidApp;
import com.ichi2.anki.multimediacard.EFieldType;
import com.ichi2.anki.multimediacard.IField;
import com.ichi2.libanki.Collection;

/**
 * @author zaur
 * 
 *         Implementation of Audio field type
 * 
 */
public class AudioField extends FieldBase implements IField
{
	private static final long serialVersionUID = 5033819217738174719L;
	private String mAudioPath;
	private String mName;
	private boolean mHasTemporaryMedia = false;

	private static final String PATH_REGEX = "\\[sound:(.*)\\]";

	@Override
	public EFieldType getType()
	{
		return EFieldType.AUDIO;
	}

	@Override
	public boolean setType(EFieldType type)
	{
		return false;
	}

	@Override
	public boolean isModified()
	{
		return getThisModified();
	}

	@Override
	public String getHtml()
	{
		return null;
	}

	@Override
	public boolean setHtml(String html)
	{
		return false;
	}

	@Override
	public boolean setImagePath(String pathToImage)
	{
		return false;
	}

	@Override
	public String getImagePath()
	{
		return null;
	}

	@Override
	public boolean setAudioPath(String pathToAudio)
	{
		mAudioPath = pathToAudio;
		setThisModified();
		return true;
	}

	@Override
	public String getAudioPath()
	{
		return mAudioPath;
	}

	@Override
	public String getText()
	{
		return null;
	}

	@Override
	public boolean setText(String text)
	{
		return false;
	}

	@Override
	public void setHasTemporaryMedia(boolean hasTemporaryMedia)
	{
		mHasTemporaryMedia = hasTemporaryMedia;
	}

	@Override
	public boolean hasTemporaryMedia()
	{
		return mHasTemporaryMedia;
	}

	@Override
	public String getName()
	{
		return mName;
	}

	@Override
	public void setName(String name)
	{
		mName = name;
	}

	@Override
	public String getFormattedValue()
	{
		File file = new File(getAudioPath());
		if (file.exists())
		{
			return String.format("[sound:%s]", file.getName());
		}
		else
		{
			return "";
		}
	}

	@Override
	public void setFormattedString(String value)
	{
		Pattern p = Pattern.compile(PATH_REGEX);
		Matcher m = p.matcher(value);
		String res = "";
		if (m.find())
		{
			res = m.group(1);
		}
		Collection col = AnkiDroidApp.getCol();
		String mediaDir = col.getMedia().getDir() + "/";
		setAudioPath(mediaDir + res);
	}
}