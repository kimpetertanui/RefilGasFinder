package com.libraries.consent;

import java.io.Serializable;

public class Consent implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3053478382970635522L;
	String key;
	String title;
	String category;
	String what;
	String whyNeeded;
	String moreInformation;

    int titleResId;
    int categoryResId;
    int whatResId;
    int whyNeededResId;
    int moreInformationResId;
    String infoURL;

    public final static String GET_REGION_JSON_URL = "http://ip-api.com/json";

	public Consent(String key, int resIdTitle, int resIdCategory, int resIdWhat, int resIdWhyNeeded, int resIdMoreInformation, String infoURL) {
        this.key = key;
        this.titleResId = resIdTitle;
        this.categoryResId = resIdCategory;
        this.whatResId = resIdWhat;
        this.whyNeededResId = resIdWhyNeeded;
        this.moreInformationResId = resIdMoreInformation;
        this.infoURL = infoURL;
	}

    public Consent(String key, String title, String category, String what, String whyNeeded, String moreInformation, String infoURL) {
        this.key = key;
        this.title = title;
        this.category = category;
        this.what = what;
        this.whyNeeded = whyNeeded;
        this.moreInformation = moreInformation;
        this.infoURL = infoURL;
    }

    public String getInfoURL() {
        return infoURL;
    }

    public void setInfoURL(String infoURL) {
        this.infoURL = infoURL;
    }

    public int getTitleResId() {
        return titleResId;
    }

    public void setTitleResId(int titleResId) {
        this.titleResId = titleResId;
    }

    public int getCategoryResId() {
        return categoryResId;
    }

    public void setCategoryResId(int categoryResId) {
        this.categoryResId = categoryResId;
    }

    public int getWhatResId() {
        return whatResId;
    }

    public void setWhatResId(int whatResId) {
        this.whatResId = whatResId;
    }

    public int getWhyNeededResId() {
        return whyNeededResId;
    }

    public void setWhyNeededResId(int whyNeededResId) {
        this.whyNeededResId = whyNeededResId;
    }

    public int getMoreInformationResId() {
        return moreInformationResId;
    }

    public void setMoreInformationResId(int moreInformationResId) {
        this.moreInformationResId = moreInformationResId;
    }

    public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getWhat() {
		return what;
	}

	public void setWhat(String what) {
		this.what = what;
	}

    public String getWhyNeeded() {
        return whyNeeded;
    }

    public void setWhyNeeded(String whyNeeded) {
        this.whyNeeded = whyNeeded;
    }

    public String getMoreInformation() {
        return moreInformation;
    }

    public void setMoreInformation(String moreInformation) {
        this.moreInformation = moreInformation;
    }
}
