package moviles.apps.interizi.config;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import moviles.apps.interizi.R;
import moviles.apps.interizi.clases.Solicitudes;

public class ListaClientesAdapter extends RecyclerView.Adapter<ListaClientesAdapter.SolicitudViewHolder>{

    ArrayList<Solicitudes> listaSolicitudes;

    public ListaClientesAdapter(ArrayList<Solicitudes> listaSolicitudes) {
        this.listaSolicitudes = listaSolicitudes;
    }

    @NonNull
    @Override
    public SolicitudViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler2_layout, parent,false);
        SolicitudViewHolder solicitudViewHolder = new SolicitudViewHolder(view);
        return solicitudViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SolicitudViewHolder holder, int position) {
        SolicitudViewHolder usuarioViewHolder = (SolicitudViewHolder) holder;
        Solicitudes solicitud = listaSolicitudes.get(position);

        usuarioViewHolder.nombreRV.setText(solicitud.getNombre());
        usuarioViewHolder.correoRV.setText(solicitud.getEmail());
    }

    @Override
    public int getItemCount() {
        return listaSolicitudes.size();
    }

    public static class SolicitudViewHolder extends RecyclerView.ViewHolder {
        public TextView nombreRV, correoRV, telefonoRV;

        public SolicitudViewHolder(@NonNull View itemView) {
            super(itemView);

            this.nombreRV = itemView.findViewById(R.id.textViewListaNombre2);
            this.correoRV = itemView.findViewById(R.id.textViewListaCorreo2);
        }
    }
}
