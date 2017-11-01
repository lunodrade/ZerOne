package com.lunodrade.zerone.bookcards;


public class CardItem {

    private int mLevelResource;
    private int mPointsResource;
    private int mTextResource;
    private int mTitleResource;
    private String mCodeResource;

    public CardItem(String code, int level, int points, int title, int text) {
        mCodeResource = code;
        mLevelResource = level;
        mPointsResource = points;
        mTitleResource = title;
        mTextResource = text;
    }

    public String getCode() { return mCodeResource; }

    public int getLevel() {
        return mLevelResource;
    }

    public int getPoints() {
        return mPointsResource;
    }

    public int getText() {
        return mTextResource;
    }

    public int getTitle() {
        return mTitleResource;
    }
}
