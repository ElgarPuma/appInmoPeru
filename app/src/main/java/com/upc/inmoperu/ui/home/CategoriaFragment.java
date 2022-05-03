package com.upc.inmoperu.ui.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.upc.inmoperu.LoadingScreen;
import com.upc.inmoperu.R;
import com.upc.inmoperu.ui.Adaptador.RecyclerCatAdapter;
import com.upc.inmoperu.ui.Entidad.Inmueble;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.upc.inmoperu.ui.task.CategoriaTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class CategoriaFragment extends Fragment {
    RecyclerView recyclerItems;
    ArrayList<Inmueble> itemList;
    public AlertDialog dialog;
    final DialogFragment loadingScreen = LoadingScreen.getInstance();


    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_categoria, container, false);

        itemList = new ArrayList<>();
        recyclerItems= (RecyclerView)root.findViewById(R.id.rvListItemsInmueble);
        SearchView sv = (SearchView)root.findViewById(R.id.search_view);
        FloatingActionButton fb = (FloatingActionButton)root.findViewById(R.id.floatingActionButton);
        int numberOfColumns = 1;
        recyclerItems.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));

        //ocultamos x primera ve
        recyclerItems.setVisibility(View.INVISIBLE);
        sv.setVisibility(View.INVISIBLE);
        fb.setVisibility(View.INVISIBLE);

        Button btn_address1 = (Button) root.findViewById(R.id.btn_address1);
        btn_address1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                setProgressDialog();
                ChangeVisibility(root,true);
                itemList.clear();
                llenarLista2("1");
                RecyclerCatAdapter adapter = new RecyclerCatAdapter(itemList);
                sv.setImeOptions(EditorInfo.IME_ACTION_DONE);
                sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        adapter.getFilter().filter(s);
                        return false;
                    }
                });
                recyclerItems.setAdapter(adapter);
                new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        dialog.dismiss();
                    }
                },
                3000);
            }
        });

        Button btn_address2 = (Button) root.findViewById(R.id.btn_address2);
        btn_address2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                setProgressDialog();
                ChangeVisibility(root,true);
                itemList.clear();
                llenarLista2("2");
                RecyclerCatAdapter adapter = new RecyclerCatAdapter(itemList);
                sv.setImeOptions(EditorInfo.IME_ACTION_DONE);
                sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        adapter.getFilter().filter(s);
                        return false;
                    }
                });
                recyclerItems.setAdapter(adapter);
                new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        dialog.dismiss();
                    }
                },
                3000);
            }
        });

        Button btn_address3 = (Button) root.findViewById(R.id.btn_address3);
        btn_address3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                setProgressDialog();
                ChangeVisibility(root,true);
                itemList.clear();
                llenarLista2("3");
                RecyclerCatAdapter adapter = new RecyclerCatAdapter(itemList);
                sv.setImeOptions(EditorInfo.IME_ACTION_DONE);
                sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        adapter.getFilter().filter(s);
                        return false;
                    }
                });
                recyclerItems.setAdapter(adapter);
                new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        dialog.dismiss();
                    }
                },
                3000);
            }
        });

        Button btn_address4 = (Button) root.findViewById(R.id.btn_address4);
        btn_address4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                setProgressDialog();
                ChangeVisibility(root,true);
                itemList.clear();
                llenarLista2("4");
                RecyclerCatAdapter adapter = new RecyclerCatAdapter(itemList);
                sv.setImeOptions(EditorInfo.IME_ACTION_DONE);
                sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        adapter.getFilter().filter(s);
                        return false;
                    }
                });
                recyclerItems.setAdapter(adapter);
                new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        dialog.dismiss();
                    }
                },
                3000);
            }
        });

        Button btn_address5 = (Button) root.findViewById(R.id.btn_address5);
        btn_address5.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                setProgressDialog();
                ChangeVisibility(root,true);
                itemList.clear();
                llenarLista2("5");
                RecyclerCatAdapter adapter = new RecyclerCatAdapter(itemList);
                sv.setImeOptions(EditorInfo.IME_ACTION_DONE);
                sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        adapter.getFilter().filter(s);
                        return false;
                    }
                });
                recyclerItems.setAdapter(adapter);
                new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        dialog.dismiss();
                    }
                },
                3000);
            }
        });

        Button btn_address6 = (Button) root.findViewById(R.id.btn_address6);
        btn_address6.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                setProgressDialog();
                ChangeVisibility(root,true);
                itemList.clear();
                llenarLista2("6");
                RecyclerCatAdapter adapter = new RecyclerCatAdapter(itemList);
                sv.setImeOptions(EditorInfo.IME_ACTION_DONE);
                sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        adapter.getFilter().filter(s);
                        return false;
                    }
                });
                recyclerItems.setAdapter(adapter);
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                dialog.dismiss();
                            }
                        },
                        3000);
            }
        });


        //boton de regreso
        fb.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) { ChangeVisibility(root,false); }
        });



        return root;
    }

    @SuppressLint("RestrictedApi")
    public void ChangeVisibility(View root, Boolean val){
        recyclerItems= (RecyclerView)root.findViewById(R.id.rvListItemsInmueble);
        FloatingActionButton fb = (FloatingActionButton)root.findViewById(R.id.floatingActionButton);
        SearchView sv = (SearchView)root.findViewById(R.id.search_view);
        Button btn_address1 = (Button) root.findViewById(R.id.btn_address1);
        Button btn_address2 = (Button) root.findViewById(R.id.btn_address2);
        Button btn_address3 = (Button) root.findViewById(R.id.btn_address3);
        Button btn_address4 = (Button) root.findViewById(R.id.btn_address4);
        Button btn_address5 = (Button) root.findViewById(R.id.btn_address5);
        Button btn_address6 = (Button) root.findViewById(R.id.btn_address6);

        if(val){
            recyclerItems.setVisibility(View.VISIBLE);
            sv.setVisibility(View.VISIBLE);
            fb.setVisibility(View.VISIBLE);
            btn_address1.setVisibility(View.INVISIBLE);
            btn_address2.setVisibility(View.INVISIBLE);
            btn_address3.setVisibility(View.INVISIBLE);
            btn_address4.setVisibility(View.INVISIBLE);
            btn_address5.setVisibility(View.INVISIBLE);
            btn_address6.setVisibility(View.INVISIBLE);
        }else{
            recyclerItems.setVisibility(View.INVISIBLE);
            fb.setVisibility(View.INVISIBLE);
            sv.setVisibility(View.INVISIBLE);
            btn_address1.setVisibility(View.VISIBLE);
            btn_address2.setVisibility(View.VISIBLE);
            btn_address3.setVisibility(View.VISIBLE);
            btn_address4.setVisibility(View.VISIBLE);
            btn_address5.setVisibility(View.VISIBLE);
            btn_address6.setVisibility(View.VISIBLE);
        }
    }

    public void llenarLista2(String value) {
        String result;
        CategoriaTask categoriaTask = new CategoriaTask(getContext(),"http://inmoperu.azurewebsites.net/inmoperu-serv/public/api/listarFiltro");
        try {
            result = categoriaTask.execute(value).get();
            if(result != null && !result.contains("Error")) {
                JSONObject jparent = new JSONObject(result);
                JSONArray objeto2 = jparent.getJSONArray("resp");
                for (int i=0; i < objeto2.length(); i++) {
                    itemList.add(new Inmueble(objeto2.getJSONObject(i).getString("id"),objeto2.getJSONObject(i).getString("imgp1")+objeto2.getJSONObject(i).getString("imgp2")+objeto2.getJSONObject(i).getString("imgp3"), objeto2.getJSONObject(i).getString("title"), objeto2.getJSONObject(i).getString("description"),objeto2.getJSONObject(i).getString("price")));
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

    public void setProgressDialog() {

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(getContext());
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(getContext());
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(getContext());
        tvText.setText("Loading ...");
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setView(ll);

        dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(layoutParams);
        }
    }


}
