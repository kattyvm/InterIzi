package moviles.apps.interizi.proveedor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

import moviles.apps.interizi.MainActivity;
import moviles.apps.interizi.R;
import moviles.apps.interizi.clases.Usuario;

public class ProveedorMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proveedor_main);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        TextView textViewProveedorNombre = findViewById(R.id.textViewProveedorNombre);
        TextView textViewProveedorRol = findViewById(R.id.textViewProveedorRol);
        EditText editTextProveedorDni = findViewById(R.id.editTextProveedorDni);
        EditText editTextProveedorTelefono = findViewById(R.id.editTextProveedorTelefono);

        drawerLayout = findViewById(R.id.drawer_layout4);
        navigationView = findViewById(R.id.nav_view4);
        toolbar = findViewById(R.id.toolbar4);

        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_profile2);

        textViewProveedorNombre.setText(currentUser.getDisplayName());

        databaseReference.child("users").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    Usuario usuario = snapshot.getValue(Usuario.class);

                    if(usuario.getRol() == 2) {
                        textViewProveedorRol.setText("Proveedor");
                    }
                    editTextProveedorDni.setText(usuario.getDni());
                    editTextProveedorTelefono.setText(usuario.getTelefono());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        View headerView = navigationView.getHeaderView(0) ;
        TextView textViewMenuNombre = headerView.findViewById(R.id.textViewMenuNombre);
        TextView textViewMenuCorreo = headerView.findViewById(R.id.textViewMenuCorreo);
        textViewMenuNombre.setText(currentUser.getDisplayName());
        textViewMenuCorreo.setText(currentUser.getEmail());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLatLong();

    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_logout2:
                AuthUI instance = AuthUI.getInstance();
                instance.signOut(this).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(ProveedorMainActivity.this, MainActivity.class));
                        finish();
                    }
                });
                break;

            case R.id.nav_cli:
                startActivity(new Intent(ProveedorMainActivity.this, ListaClientesActivity.class));
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void update (View view) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        EditText editTextProveedorDni = findViewById(R.id.editTextProveedorDni);
        EditText editTextProveedorTelefono = findViewById(R.id.editTextProveedorTelefono);

        String dni = editTextProveedorDni.getText().toString();
        boolean valid = Pattern.matches("^[0-9]*$", dni);
        if (dni.isEmpty()) {
            editTextProveedorDni.setError("No puede estar vacío");
        } else if (dni.length() <= 7 | dni.length() >= 9) {
            editTextProveedorDni.setError("Solo puede tener 8 caracteres");
        } else if (!valid) {
            editTextProveedorDni.setError("Solo se permite números");
        }

        String telefono = editTextProveedorTelefono.getText().toString();
        boolean valid2 = Pattern.matches("^[0-9]*$", telefono);
        if (telefono.isEmpty()) {
            editTextProveedorTelefono.setError("No puede estar vacío");
        } else if (telefono.length() <= 8 | telefono.length() >= 10) {
            editTextProveedorTelefono.setError("Solo puede tener 9 caracteres");
        } else if (!valid2) {
            editTextProveedorTelefono.setError("Solo se permite números");
        }


        if (!dni.isEmpty() && dni.length() == 8 && dni.matches("^[0-9]*$")
                && !telefono.isEmpty() && telefono.length() == 9 && telefono.matches("^[0-9]*$")) {
            databaseReference.child("users").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        Usuario usuario = snapshot.getValue(Usuario.class);

                        usuario.setDni(dni);

                        databaseReference.child("users").child(currentUser.getUid()).setValue(usuario)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ProveedorMainActivity.this, "Datos actualizados", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        e.printStackTrace();
                                        Log.d("logApp", "Error al guardar");
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    private void getLatLong() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        databaseReference.child("users").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    Usuario usuario = snapshot.getValue(Usuario.class);

                    if (ActivityCompat.checkSelfPermission(ProveedorMainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ProveedorMainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(ProveedorMainActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    }

                    fusedLocationProviderClient.getLastLocation()
                            .addOnSuccessListener(ProveedorMainActivity.this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        Log.d("infoApp", "Latitud: " + location.getLatitude() + " | Longitud: " + location.getLongitude());

                                        usuario.setLatitud(location.getLatitude());
                                        usuario.setLongitud(location.getLongitude());

                                        databaseReference.child("users").child(currentUser.getUid()).setValue(usuario)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d("logApp", "Coordenadas cambiadas");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        e.printStackTrace();
                                                        Log.d("logApp", "Error al guardar");
                                                    }
                                                });
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


}