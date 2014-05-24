package de.tum.socialcomp.android.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * This class is used to select between the three Fragments 
 * of the game: main screen, map and game statistics.
 * 
 * @author Niklas Klügel
 *
 */

public class AppSectionsPagerAdapter extends FragmentPagerAdapter {
	
	private final String[] sections = new String[]{
			"Main",
			"Map",
			"Game",
			"MyMonsters",
	};
	
    public AppSectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new MainSectionFragment();

            case 1: 
            	return new MapSectionFragment();
            case 2:
            	return new GameSectionFragment();
            default :
                Fragment fragment =  new MyMonsterFragment();
                
                return fragment;
        }
    }

    @Override
    public int getCount() {
        return this.sections.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return sections[position];
    }
}