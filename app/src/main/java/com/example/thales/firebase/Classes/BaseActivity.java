package com.example.thales.firebase.Classes;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import android.support.v7.widget.Toolbar;

import com.example.thales.firebase.Activity.PrincipalActivityCliente;
import com.example.thales.firebase.R;


public class BaseActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    PrincipalActivityCliente principalActivityCliente;

    protected void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if(toolbar!=null){
            setSupportActionBar(toolbar);
        }
    }

    //Configura o NavDrawer
    protected void setUpNavDrawer() {
        final ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.navigation_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                item.setChecked(true);

                drawerLayout.closeDrawers();

                onNavigationItemSelected(item);
                return true;
            }
        });

    }

    private void onNavDrawerItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_ver_profissionais:
                principalActivityCliente.verProfissionais();
                break;
        }
    }


}
