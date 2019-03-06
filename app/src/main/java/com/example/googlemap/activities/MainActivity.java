package com.example.googlemap.activities;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.googlemap.R;
import com.example.googlemap.fragments.MapFragment;
import com.example.googlemap.fragments.MapFragment2;
import com.example.googlemap.fragments.WelcomeFragment;

public class MainActivity extends AppCompatActivity {

    Fragment currentFragment; //para llevar el control de que fragmento se esta cargando cada momento

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toast.makeText(this,"Called",Toast.LENGTH_SHORT).show(); //Cuando se rota se vuelve a llamar al onCreate

        //Para poder girar la pantalla y el fragment no se cambie
        if(savedInstanceState == null){//Es la primera vez que se crea ese activity
            currentFragment = new WelcomeFragment();
            changeFragment(currentFragment);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_welcome:
                currentFragment = new WelcomeFragment();
                break;
            case R.id.menu_map:
                currentFragment = new MapFragment2();
                break;
        }
        changeFragment(currentFragment);
        return super.onOptionsItemSelected(item);
    }

    private void changeFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction() //para la transición
                .replace(R.id.fragment_container, fragment).commit();
    }

}
