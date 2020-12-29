package com.example.fyptest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    boolean fabOpenedFlag = false;
    FloatingActionButton fabCamera;
    FloatingActionButton fabShare;
    ArrayList<String> mPermissions = new ArrayList();
    private static final int ALL_PERMISSIONS_RESULT = 1011;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Floating Button Setting
        FloatingActionButton fab = findViewById(R.id.fab);
        fabCamera = findViewById(R.id.fab_camera);
        fabShare = findViewById(R.id.fab_share);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(!fabOpenedFlag){
                   OpenFab();
               }else{
                   CloseFab();
               }
            }
        });
        fabCamera.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                    Intent intent = new Intent(getApplication(), ActivityCamera.class);
                    startActivity(intent);
              }
        }
        );
        //Navigation View Setting
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_camera)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        //HoverMenu Initiation
        Button hoverLaunch = findViewById(R.id.b_launch);
        hoverLaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FYPHoverMenuService.showFloatingMenu(getApplicationContext());
            }
        });

        //Start requesting permission
        mPermissions.add(Manifest.permission.CAMERA);
        mPermissions.add(Manifest.permission.SYSTEM_ALERT_WINDOW);

        ArrayList<String> mPermissionsToRequest = permissionsToRequest(mPermissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mPermissionsToRequest.size() > 0) {
                requestPermissions(mPermissionsToRequest.toArray(
                        new String[mPermissionsToRequest.size()]),
                        ALL_PERMISSIONS_RESULT);
            }
        }
        //End requesting permission
    }

    private ArrayList<String> permissionsToRequest(ArrayList<String> permissions) {
        ArrayList<String> result = new ArrayList<>();
        for (String permission : permissions)
            if (!hasPermission(permission))
                result.add(permission);
        return result;
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return Objects.requireNonNull(this)
                    .checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        return true;
    }

    private void OpenFab() {
        fabCamera.animate().translationY(-getResources().getDimension(R.dimen.fab_camera_margin));
        fabShare.animate().translationY(-getResources().getDimension(R.dimen.fab_share_margin));
        fabOpenedFlag = true;
    }

    private void CloseFab(){
        fabCamera.animate().translationY(0);
        fabShare.animate().translationY(0);
        fabOpenedFlag = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}