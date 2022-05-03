package com.upc.inmoperu.ui.Adaptador;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.upc.inmoperu.R;
import com.upc.inmoperu.ui.Entidad.Inmueble;
import com.upc.inmoperu.ui.Session;
import com.upc.inmoperu.ui.account.LoginFragment;
import com.upc.inmoperu.ui.task.WishListTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RecyclerCatAdapter extends RecyclerView.Adapter<RecyclerCatAdapter.MyViewHolder> implements Filterable {
    private List<Inmueble> itemsList;
    private List<Inmueble> itemsListFull;
    private Session session;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView titulo, descripcion, precio, inmuebleid;
        public ImageView foto;
        public Button btnDetalle;

        public MyViewHolder(View view) {
            super(view);
            titulo = (TextView) view.findViewById(R.id.tvTitulo);
            descripcion = (TextView) view.findViewById(R.id.tvDescripcion);
            precio = (TextView) view.findViewById(R.id.tvPrecio);
            foto = (ImageView) view.findViewById(R.id.ivFoto);
            inmuebleid = (TextView) view.findViewById(R.id.tvInmuebleId);
            btnDetalle = (Button) view.findViewById(R.id.btnDetalle);
        }
    }
    public RecyclerCatAdapter(List<Inmueble> itemsList) {
        this.itemsList = itemsList;
        itemsListFull = new ArrayList<>(itemsList);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_item_lista, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Inmueble item = itemsList.get(position);
        holder.titulo.setText(item.getTitulo());
        holder.descripcion.setText(item.getDescripcion());
        holder.precio.setText("Precio : s/." + item.getPrecio());
        try {
            byte[] decodedString = Base64.decode(item.getFoto().split(",")[1], Base64.DEFAULT);
            Bitmap b = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.foto.setImageBitmap(b);

        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.btnDetalle.setText("Solicitar");
        holder.btnDetalle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session = new Session(view.getContext());
                if (session.getuserid() == "") {
                    LoginFragment fragmentDetail = new LoginFragment();
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    FragmentManager fragmentManager = activity.getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.nav_container, fragmentDetail);
                    fragmentTransaction.commit();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setMessage("Desea registrar el inmueble como solicitud?");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                String result;
                                WishListTask wishListTask = new WishListTask(view.getContext(), "http://inmoperu.azurewebsites.net/inmoperu-serv/public/api/crearWishlist");
                                result = wishListTask.execute(session.getuserid(), item.getInmuebleid()).get();
                                if (result != null && !result.contains("Error")) {
                                    //Transformamos al valor Json
                                    JSONObject objeto = null;
                                    objeto = new JSONObject(result);
                                    if (objeto.getString("success").toString() == "true") {
                                        //Obtenemos el valor Id mapeando todo el valor de json
                                        String idSolicitud = objeto.getJSONObject("resp").getString("id");
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
                                        builder1.setMessage("El inmueble se encuentra registrado en solicitudes.");
                                        builder1.setCancelable(true);
                                        AlertDialog alert = builder1.create();
                                        alert.show();
                                    } else {
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
                                        builder1.setMessage("Error al crear la solicitud. Verifique datos");
                                        builder1.setCancelable(true);
                                        AlertDialog alert = builder1.create();
                                        alert.show();
                                    }
                                } else {
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
                                    builder1.setMessage("Error al realizar la conexion de la data. Consulte con el administrador");
                                    builder1.setCancelable(true);
                                    AlertDialog alert = builder1.create();
                                    alert.show();
                                }
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
//

            }
        });
    }
    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public void removeItem(int position){
        itemsList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public Filter getFilter() {
        return itemListerFilter;
    }

    private Filter itemListerFilter = new Filter(){
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Inmueble> filteredList = new ArrayList<>();
            if(charSequence == null || charSequence.length() == 0){
                filteredList.addAll(itemsListFull);
            }else{
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (Inmueble item: itemsListFull){
                    if(item.getTitulo().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            itemsList.clear();
            itemsList.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };
}
