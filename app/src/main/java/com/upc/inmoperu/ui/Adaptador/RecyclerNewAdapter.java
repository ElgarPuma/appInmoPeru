package com.upc.inmoperu.ui.Adaptador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.upc.inmoperu.R;
import com.upc.inmoperu.ui.Entidad.Oferta;

import java.util.List;

public class RecyclerNewAdapter extends RecyclerView.Adapter<RecyclerNewAdapter.MyViewHolder> {

    private List<Oferta> newsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView titulo, descripcion, fecha;
        public ImageView thumbnail2;
        public RelativeLayout viewBackground2,viewForeground2;

        public MyViewHolder(View view) {
            super(view);
            titulo = (TextView) view.findViewById(R.id.tvTitulo2);
            descripcion = (TextView) view.findViewById(R.id.tvDescripcion2);
            fecha = (TextView) view.findViewById(R.id.tvFecha2);
            viewBackground2 = itemView.findViewById(R.id.view_background2);
            viewForeground2 = itemView.findViewById(R.id.view_foreground2);
        }
    }

    public RecyclerNewAdapter(List<Oferta> newsList) {
        this.newsList = newsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_new_lista, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Oferta item = newsList.get(position);
        holder.titulo.setText(item.getTitulo());
        holder.descripcion.setText(item.getDescripcion());
        holder.fecha.setText(item.getFecha());
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public void removeItem(int position) {
        newsList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Oferta item, int position) {
        newsList.add(position, item);
        notifyItemInserted(position);
    }



}
