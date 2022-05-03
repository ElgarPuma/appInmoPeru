package com.upc.inmoperu.ui.wish;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.upc.inmoperu.R;
import com.upc.inmoperu.ui.Adaptador.CardListAdapter;
import com.upc.inmoperu.ui.Entidad.Inmueble;
import com.upc.inmoperu.ui.Entidad.Oferta;
import com.upc.inmoperu.ui.Helper.RecyclerItemTouchHelper;
import com.upc.inmoperu.ui.Helper.RecyclerItemTouchHelperListener;
import com.upc.inmoperu.ui.Session;
import com.upc.inmoperu.ui.account.LoginFragment;
import com.google.android.material.snackbar.Snackbar;
import com.upc.inmoperu.ui.task.WishListDeleteTask;
import com.upc.inmoperu.ui.task.WishListUserTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class WishFragment extends Fragment implements RecyclerItemTouchHelperListener {

    private RecyclerView recyclerItems;
    private ArrayList<Inmueble> itemList;
    private TextView txtTotal;
    private CardListAdapter adapter;
    private FrameLayout rootLayout;
    private Session session;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_wish, container, false);
        session = new Session(getContext());
        if (session.getuserid() == "") {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_container, new LoginFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        }else {
            itemList = new ArrayList<>();
            recyclerItems = (RecyclerView) root.findViewById(R.id.rvListItemsWish);
            rootLayout = (FrameLayout) root.findViewById(R.id.rootLayout);
            txtTotal = (TextView) root.findViewById(R.id.text_precio);
            llenarLista();
            adapter = new CardListAdapter(getContext(), itemList);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerItems.setLayoutManager(layoutManager);
            recyclerItems.setItemAnimator(new DefaultItemAnimator());
            recyclerItems.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
            recyclerItems.setAdapter(adapter);

            Button procesar_compra = (Button) root.findViewById(R.id.procesar_compra);
            procesar_compra.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setMessage("Estas seguro de realizar la solicitud?");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            session.settotal(txtTotal.getText().toString());
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.nav_container, new CompraFragment());
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }
                    });
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });


            ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
            new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerItems);
            cargarTotal();
        }
        return root;
    }

    public void llenarLista() {
        session = new Session(getContext());
        String result;
        WishListUserTask wishListUserTask = new WishListUserTask(getContext(),"http://inmoperu.azurewebsites.net/inmoperu-serv/public/api/listarwish");
        try {
            result = wishListUserTask.execute(session.getuserid()).get();
            if(result != null && !result.contains("Error")) {
                JSONObject jparent = new JSONObject(result);
                JSONArray objeto2 = jparent.getJSONArray("resp");
                for (int i=0; i < objeto2.length(); i++) {
                    JSONArray objeto3 = objeto2.getJSONObject(i).getJSONArray("producto");
                    for (int j=0; j < objeto3.length(); j++) {
                        itemList.add(new Inmueble(objeto3.getJSONObject(j).getString("id"), objeto3.getJSONObject(j).getString("imgp1") + objeto3.getJSONObject(j).getString("imgp2") + objeto3.getJSONObject(j).getString("imgp3"), objeto3.getJSONObject(j).getString("title"), objeto3.getJSONObject(j).getString("description"), objeto3.getJSONObject(j).getString("price")));
                    }
                }
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof CardListAdapter.MyViewHolder){
            session = new Session(getContext());
            String title = itemList.get(viewHolder.getAdapterPosition()).getTitulo();
            String id = itemList.get(viewHolder.getAdapterPosition()).getInmuebleid();
            Inmueble deletedItem = itemList.get(viewHolder.getAdapterPosition());
            int deleteIndex = viewHolder.getAdapterPosition();
            adapter.removeItem(deleteIndex);
            String result;
            WishListDeleteTask wishListDeleteTask = new WishListDeleteTask(getContext(),"http://inmoperu.azurewebsites.net/inmoperu-serv/public/api/eliminarWishlist");
            wishListDeleteTask.execute(session.getuserid(),id);
            cargarTotal();
            Snackbar snackbar = Snackbar.make(rootLayout,title + " eliminado de la lista!",Snackbar.LENGTH_SHORT);
//            snackbar.setAction("RETORNAR", new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    adapter.restoreItem(deletedItem,deleteIndex);
//                    cargarTotal();
//                }
//            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    public void cargarTotal(){
        Double totalPrice = 0.00;
        for (int i = 0; i<itemList.size(); i++)
        {
            String precio = itemList.get(i).getPrecio();
            totalPrice += Double.parseDouble(precio);
        }
        txtTotal.setText(String.format("%.2f", totalPrice));
    }
}
