package moviles.apps.interizi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import moviles.apps.interizi.clases.Usuario;
import moviles.apps.interizi.cliente.ClienteMainActivity;
import moviles.apps.interizi.proveedor.ProveedorMainActivity;

public class SplashActivity extends AppCompatActivity {

    private int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        //getSupportActionBar().hide();

        Animation topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        Animation bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        ImageView imageViewSplash = findViewById(R.id.imageViewSplash);
        TextView textViewTituloSplash = findViewById(R.id.textViewTituloSplash);
        TextView textViewDescripSplash = findViewById(R.id.textViewDescripSplash);

        imageViewSplash.setAnimation(topAnim);
        textViewTituloSplash.setAnimation(bottomAnim);
        textViewDescripSplash.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if(currentUser == null) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                } else {
                    openUserAct(currentUser);
                }
            }
        }, 3000);


        //permissions();

    }

    public void permissions() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(SplashActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            return;
        }
    }

    public void openUserAct(FirebaseUser currentUser) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("users").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    Usuario usuario = snapshot.getValue(Usuario.class);
                    Log.d("infoApp", usuario.getDni());
                    if (usuario.getDni().equals(null)) {
                        startActivity(new Intent(SplashActivity.this, RegistroActivity.class));
                        finish();
                    } else {
                        if (usuario.getRol() == 1) {
                            startActivity(new Intent(SplashActivity.this, ClienteMainActivity.class));
                            finish();
                        } else if (usuario.getRol() == 2) {
                            startActivity(new Intent(SplashActivity.this, ProveedorMainActivity.class));
                            finish();
                        } else {
                            startActivity(new Intent(SplashActivity.this, RegistroActivity.class));
                            finish();
                        }
                    }
                } else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}