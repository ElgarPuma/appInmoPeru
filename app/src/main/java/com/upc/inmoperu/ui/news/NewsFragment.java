package com.upc.inmoperu.ui.news;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.upc.inmoperu.R;
import com.upc.inmoperu.ui.Adaptador.RecyclerNewAdapter;
import com.upc.inmoperu.ui.DB.DBAdapter;
import com.upc.inmoperu.ui.Entidad.Oferta;
import com.upc.inmoperu.ui.Helper.RecyclerNewTouchHelper;
import com.upc.inmoperu.ui.Helper.RecyclerNewTouchHelperListener;
import com.upc.inmoperu.ui.Session;
import com.upc.inmoperu.ui.account.LoginFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class NewsFragment extends Fragment implements RecyclerNewTouchHelperListener {

    RecyclerView recyclerItems;
    private RecyclerNewAdapter adapter;
    private FrameLayout rootLayout;
    ArrayList<Oferta> itemList;
    private Session session;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_news, container, false);
        FloatingActionButton fb = (FloatingActionButton)root.findViewById(R.id.floatAddNew);

        fb.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_container, new AddNewFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        itemList = new ArrayList<>();

        recyclerItems= (RecyclerView) root.findViewById(R.id.rvListNews);
        rootLayout = (FrameLayout)root.findViewById(R.id.rootLayout2);
        recyclerItems.setLayoutManager(new LinearLayoutManager(getContext()));
        DBAdapter dbAdapter = new DBAdapter(getContext());
        itemList = (ArrayList<Oferta>) dbAdapter.mostrarOfertas();
        adapter = new RecyclerNewAdapter(dbAdapter.mostrarOfertas());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerItems.setLayoutManager(layoutManager);
        recyclerItems.setItemAnimator(new DefaultItemAnimator());
        recyclerItems.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        recyclerItems.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerNewTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerItems);

        return root;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof RecyclerNewAdapter.MyViewHolder){
            session = new Session(getContext());
            if (session.getuserid() == "") {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_container, new LoginFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }else {
                String title = itemList.get(viewHolder.getAdapterPosition()).getTitulo();
                int id = itemList.get(viewHolder.getAdapterPosition()).getId();
                DBAdapter dbAdapter = new DBAdapter(getContext());
                int deleteIndex = viewHolder.getAdapterPosition();
                adapter.removeItem(deleteIndex);
                dbAdapter.eliminarOfertas(id);
                Snackbar snackbar = Snackbar.make(rootLayout, title + " eliminado de la lista!", Snackbar.LENGTH_SHORT);
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        }
    }

}
