package br.org.aps;

import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;

import br.org.aps.classe.Servidor;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private NavigationView navigationView;
    private NavController navController;
    private Servidor sv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sv = Servidor.getInstance(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_listar, R.id.nav_myregistro, R.id.nav_excluir,
                R.id.nav_avaliar, R.id.nav_addrem, R.id.nav_login)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        sv = Servidor.getInstance(this);
        upMenu();
    }

    @Override
    protected void onResume() {
        super.onResume();
        upMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public NavController getNav() {
        return navController;
    }

    public void upMenu(){
        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_admin).setVisible(false);
        menu.findItem(R.id.nav_myregistro).setVisible(false);
        if (sv.isUser()){
            menu.findItem(R.id.nav_login).setVisible(false);
            menu.findItem(R.id.nav_myregistro).setVisible(true);
            if (sv.getUser().isAdmin())
                menu.findItem(R.id.nav_admin).setVisible(true);
        }
    }
}
