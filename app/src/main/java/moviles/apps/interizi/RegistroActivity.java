package moviles.apps.interizi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

import moviles.apps.interizi.clases.Usuario;
import moviles.apps.interizi.cliente.ClienteMainActivity;
import moviles.apps.interizi.proveedor.ProveedorMainActivity;

public class RegistroActivity extends AppCompatActivity {

    private int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        //getSupportActionBar().hide();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        TextView textViewRegistroTitulo = findViewById(R.id.textViewRegistroTitulo);

        if (currentUser != null) {
            String uid = currentUser.getUid();
            String displayName = currentUser.getDisplayName();
            String email = currentUser.getEmail();

            textViewRegistroTitulo.setText("Bienvenido, " + displayName);
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLatLong();

    }

    private void getLatLong() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(RegistroActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    public void registro(View view) {
        EditText editTextRegistroDni = findViewById(R.id.editTextRegistroDni);
        EditText editTextRegistroTelefono = findViewById(R.id.editTextRegistroTelefono);
        TextView textViewOpcionRol = findViewById(R.id.textViewOpcionRol);
        RadioButton radioButtonCliente = findViewById(R.id.radioButtonCliente);
        RadioButton radioButtonProvedor = findViewById(R.id.radioButtonProvedor);

        String dni = editTextRegistroDni.getText().toString();
        boolean valid = Pattern.matches("^[0-9]*$", dni);
        if (dni.isEmpty()) {
            editTextRegistroDni.setError("No puede estar vacío");
        } else if (dni.length() <= 7 | dni.length() >= 9) {
            editTextRegistroDni.setError("Solo puede tener 8 caracteres");
        } else if (!valid) {
            editTextRegistroDni.setError("Solo se permite números");
        }

        String telefono = editTextRegistroTelefono.getText().toString();
        boolean valid2 = Pattern.matches("^[0-9]*$", telefono);
        if (telefono.isEmpty()) {
            editTextRegistroTelefono.setError("No puede estar vacío");
        } else if (telefono.length() <= 8 | telefono.length() >= 10) {
            editTextRegistroTelefono.setError("Solo puede tener 9 caracteres");
        } else if (!valid2) {
            editTextRegistroTelefono.setError("Solo se permite números");
        }

        if (radioButtonCliente.isChecked()) {
            textViewOpcionRol.setText("Cliente");
        } else if (radioButtonProvedor.isChecked()) {
            textViewOpcionRol.setText("Proveedor");
        }
        String rol = textViewOpcionRol.getText().toString();


        if (!dni.isEmpty() && dni.length() == 8 && dni.matches("^[0-9]*$")
            && !telefono.isEmpty() && telefono.length() == 9 && telefono.matches("^[0-9]*$")) {
            saveUser(rol);
        }
    }

    public void saveUser(String rol) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        EditText editTextRegistroDni = findViewById(R.id.editTextRegistroDni);
        EditText editTextRegistroTelefono = findViewById(R.id.editTextRegistroTelefono);

        Usuario usuario = new Usuario();
        String id = currentUser.getUid();
        String nombre = currentUser.getDisplayName();
        String correo = currentUser.getEmail();
        String dni = editTextRegistroDni.getText().toString();
        String telefono = editTextRegistroTelefono.getText().toString();

        if (rol.equals("Cliente")) {
            usuario.setRol(1);
        } else if (rol.equals("Proveedor")) {
            usuario.setRol(2);
        }

        usuario.setId(id);
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        usuario.setDni(dni);
        usuario.setTelefono(telefono);


        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(RegistroActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
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
                                            Toast.makeText(RegistroActivity.this, "Datos guardados exitosamente", Toast.LENGTH_SHORT).show();
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


        databaseReference.child("users").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    Usuario usuario = snapshot.getValue(Usuario.class);
                    Log.d("infoApp", usuario.getDni());

                    if (usuario.getRol() == 1) {
                        startActivity(new Intent(RegistroActivity.this, ClienteMainActivity.class));
                        finish();
                    } else if (usuario.getRol() == 2) {
                        startActivity(new Intent(RegistroActivity.this, ProveedorMainActivity.class));
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void logout(View view) {
        AuthUI instance = AuthUI.getInstance();
        instance.signOut(this).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                startActivity(new Intent(RegistroActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}