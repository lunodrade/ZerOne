package com.lunodrade.zerone.achievement;

import android.graphics.drawable.Drawable;

public class AchieViewerEntry {

    String achievName;
    String achievDescription;
    Integer achievXp;
    Integer achievCount;
    Drawable achievResourceIcon;

    public AchieViewerEntry(String achievName, String achievDescription, Integer achievXp,
                            Integer achievCount, Drawable achievResourceIcon) {
        this.achievName = achievName;
        this.achievDescription = achievDescription;
        this.achievXp = achievXp;
        this.achievCount = achievCount;
        this.achievResourceIcon = achievResourceIcon;
    }

    public String getName() {
        return achievName;
    }

    public String getDescription() {
        return achievDescription;
    }

    public Integer getXp() {
        return achievXp;
    }

    public Integer getCount() {
        return achievCount;
    }

    public Drawable getIcon() {
        return achievResourceIcon;
    }
}
