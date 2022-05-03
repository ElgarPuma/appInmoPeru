package com.upc.inmoperu.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.upc.inmoperu.R;
import com.upc.inmoperu.ui.Adaptador.RecyclerViewAdapter;
import com.upc.inmoperu.ui.Entidad.Inmueble;
import com.upc.inmoperu.ui.task.InicioTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class InicioFragment extends Fragment{

    RecyclerView recyclerItems;
    ArrayList<Inmueble> itemList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_inicio, container, false);
        itemList = new ArrayList<>();
        recyclerItems= (RecyclerView) root.findViewById(R.id.rvListItemsInicio);
        int numberOfColumns = 1;
        recyclerItems.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));
        llenarLista();

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(itemList);
        recyclerItems.setAdapter(adapter);
        return root;
    }

    public void llenarLista() {
        String result;
        InicioTask inicioTask = new InicioTask(getContext(),"http://inmoperu.azurewebsites.net/inmoperu-serv/public/api/listarOferta");
        try {
            result = inicioTask.execute().get();
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
}
