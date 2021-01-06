package moviles.apps.interizi.config;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import moviles.apps.interizi.R;
import moviles.apps.interizi.clases.Solicitudes;
import moviles.apps.interizi.clases.Usuario;

public class ListaProveedoresAdapter extends RecyclerView.Adapter<ListaProveedoresAdapter.UsuarioViewHolder> {

    ArrayList<Usuario> listaProveedores;

    public ListaProveedoresAdapter(ArrayList<Usuario> listaProveedores) {
        this.listaProveedores = listaProveedores;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout, parent,false);
        UsuarioViewHolder usuarioViewHolder = new UsuarioViewHolder(view);
        return usuarioViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        UsuarioViewHolder usuarioViewHolder = (UsuarioViewHolder) holder;
        Usuario usuario = listaProveedores.get(position);

        usuarioViewHolder.nombreRV.setText(usuario.getNombre());
        usuarioViewHolder.correoRV.setText(usuario.getCorreo());
        usuarioViewHolder.telefonoRV.setText("Teléfono: " + usuario.getTelefono());
        usuarioViewHolder.buttonRV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                Log.d("infoApp", "Hizo click");

                databaseReference.child("users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            Usuario usuario1 = snapshot1.getValue(Usuario.class);
                            if (usuario.getCorreo().equals(usuario1.getCorreo())) {

                                Solicitudes solicitudes = new Solicitudes();
                                solicitudes.setNombre(currentUser.getDisplayName());
                                solicitudes.setEmail(currentUser.getEmail());

                                databaseReference.child("requests").child(usuario.getId()).child(currentUser.getUid()).setValue(solicitudes)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return listaProveedores.size();
    }


    public static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        public TextView nombreRV, correoRV, telefonoRV;
        public Button buttonRV;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);

            this.nombreRV = itemView.findViewById(R.id.textViewListaNombre);
            this.correoRV = itemView.findViewById(R.id.textViewListaCorreo);
            this.telefonoRV = itemView.findViewById(R.id.textViewListaTelefono);
            this.buttonRV = itemView.findViewById(R.id.buttonListaRV);
        }
    }
}
