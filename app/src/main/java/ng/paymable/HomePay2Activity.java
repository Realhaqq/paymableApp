package ng.paymable;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;

import Adapters.GridAdapter;
import Adapters.ProfilePagerAdapter_walkthrough_01;
import Models.GridModel;
import me.relex.circleindicator.CircleIndicator;
import ng.fragment.HomeFragment;
import ng.fragment.MyAccountFragment;

public class HomePay2Activity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private ViewPager viewPager;

    BottomBar mBottomBar;

    BottomNavigationView bottomNavigationView;

    //This is our viewPager


    HomeFragment homeFragment;
    MenuItem prevMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

//        mBottomBar=(BottomBar)findViewById(R.id.bottombar);
//        setupBottomBar();

        //View Pager Code

        loadFragment(new HomeFragment());

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(HomePay2Activity.this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

//        viewPager = (ViewPager) findViewById(R.id.viewPager);

//
//        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
//
//        bottomNavigationView.setOnNavigationItemSelectedListener(
//                new BottomNavigationView.OnNavigationItemSelectedListener() {
//                    @Override
//                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                        switch (item.getItemId()) {
//                            case R.id.home:
//                                viewPager.setCurrentItem(0);
//                                break;
////                            case R.id.action_chat:
////                                viewPager.setCurrentItem(1);
////                                break;
////                            case R.id.action_contact:
////                                viewPager.setCurrentItem(2);
////                                break;
//                        }
//                        return false;
//                    }
//                });

//
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                if (prevMenuItem != null) {
//                    prevMenuItem.setChecked(false);
//                }
//                else
//                {
//                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
//                }
//                Log.d("page", "onPageSelected: "+position);
//                bottomNavigationView.getMenu().getItem(position).setChecked(true);
//                prevMenuItem = bottomNavigationView.getMenu().getItem(position);
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });

//        setupViewPager(viewPager);


//    }
//
//    private void setupViewPager(ViewPager viewPager) {
//        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
//        homeFragment=new HomeFragment();
//
//        adapter.addFragment(homeFragment);
////        adapter.addFragment(chatFragment);
////        adapter.addFragment(contactsFragment);
//        viewPager.setAdapter(adapter);
    }



//    private void setupBottomBar() {
//
//
//        for (int i = 0; i < mBottomBar.getTabCount(); i++) {
//            BottomBarTab tab = mBottomBar.getTabAtPosition(i);
//            tab.setGravity(Gravity.CENTER);
//
//            View icon = tab.findViewById(com.roughike.bottombar.R.id.bb_bottom_bar_icon);
//            // the paddingTop will be modified when select/deselect,
//            // so, in order to make the icon always center in tab,
//            // we need set the paddingBottom equals paddingTop
//            icon.setPadding(0, icon.getPaddingTop(), 0, icon.getPaddingTop());
//
//            View title = tab.findViewById(com.roughike.bottombar.R.id.bb_bottom_bar_title);
//            title.setVisibility(View.VISIBLE);
//        }
//
//
//
//        mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
//            Fragment fragment;
//
//            Class clazz = null;
//
//            @Override
//            public void onTabSelected(@IdRes int tabId) {
//                if (tabId == R.id.home) {
//                    // The tab with id R.id.tab_favorites was selected,
//                    // change your content accordingly.
//                    fragment = new HomeFragment();
//                }else if (tabId == R.id.profile) {
//                    // The tab with id R.id.tab_favorites was selected,
//                    // change your content accordingly.
////                    fragment = new HomeFragment();
//                    clazz = MyAccountActivity.class;
//
//                }
//
//            }
//        });
//
//        mBottomBar.setOnTabReselectListener(new OnTabReselectListener() {
//            @Override
//            public void onTabReSelected(@IdRes int tabId) {
//                if (tabId == R.id.home) {
//                    // The tab with id R.id.tab_favorites was reselected,
//                    // change your content accordingly.
//                }
//            }
//        });
//
//
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction ft = fragmentManager.beginTransaction();
////        ft.replace(R.id.flContent, fragment, fragment.getClass().getName());
//        ft.commit();




    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;

        switch (menuItem.getItemId()) {
            case R.id.home:
                fragment = new HomeFragment();
                break;

            case R.id.profile:
                fragment = new MyAccountFragment();
                break;

//            case R.id.navigation_:
//                fragment = new NotificationsFragment();
//                break;
//
//            case R.id.navigation_profile:
//                fragment = new ProfileFragment();
//                break;
        }

        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }




    }


