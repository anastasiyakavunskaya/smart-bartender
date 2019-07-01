package com.example.user.SmartBartender;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.user.SmartBartender.SettingsFragments.AdminFragment;
import com.example.user.SmartBartender.SettingsFragments.CoefficientFragment;
import com.example.user.SmartBartender.SettingsFragments.InfoFragment;
import com.example.user.SmartBartender.SettingsFragments.IngredientsFragment;
import com.example.user.SmartBartender.SettingsFragments.MotorsSettingsFragment;

public class SettingsActivity extends AppCompatActivity
        implements  NavigationView.OnNavigationItemSelectedListener, AdminFragment.OnFragmentInteractionListener, IngredientsFragment.OnFragmentInteractionListener, InfoFragment.OnFragmentInteractionListener, CoefficientFragment.OnFragmentInteractionListener {

    Fragment fragment = null;
    Class fragmentClass = InfoFragment.class;
    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        setTitle(getResources().getString(R.string.item_info));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            fragmentClass = InfoFragment.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        }
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.nav_info:{
                fragmentClass = InfoFragment.class;
                break;
            }
            case R.id.nav_ingredients:{
               fragmentClass = IngredientsFragment.class;
                break;
            }
            case R.id.nav_settings:{
                fragmentClass = MotorsSettingsFragment.class;
                break;
            }
            case R.id.nav_coefficient:{
                fragmentClass = CoefficientFragment.class;
                break;
            }
            case R.id.nav_contact:{
                //fragmentClass = ContactFragment.class;
                break;
            }
            case R.id.nav_admin:{
                fragmentClass = AdminFragment.class;
                break;
            }
            default:
                fragmentClass = InfoFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        item.setChecked(true);
        setTitle(item.getTitle());
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }


}
