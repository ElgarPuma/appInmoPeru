package com.upc.inmoperu.ui.contact;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.upc.inmoperu.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.upc.inmoperu.ui.task.MapTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;


public class ContactFragment extends Fragment implements OnMapReadyCallback {

    SupportMapFragment mapFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contact, container, false);


        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            ft.replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);


        Button btn_generar_reclamo = (Button) v.findViewById(R.id.btn_generar_reclamo);
        btn_generar_reclamo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.nav_container, new ReclamoFragment());
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        Button btn_atencion_cliente = (Button) v.findViewById(R.id.btn_atencion_cliente);
        btn_atencion_cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:949544027"));
                if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),Manifest.
                        permission.CALL_PHONE)!=PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]
                            {
                                    Manifest.permission.CALL_PHONE,
                            }, 1000);
                }else{

                }
                startActivity(callIntent);
            }
        });

        Button btn_whatsapp = (Button) v.findViewById(R.id.btn_whatsapp);
        btn_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackageManager pm = getActivity().getPackageManager();
                try {
                    String text = "Hola , tengo una consulta";
                    String toNumber = "+51 949544027";
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + toNumber + "&text=" + text));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Please install whatsapp app", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        String result;
        MapTask mapTask = new MapTask(getContext(), "http://inmoperu.azurewebsites.net/inmoperu-serv/public/api/listOficinas");
        try {
            result = mapTask.execute().get();
            if (result != null && !result.contains("Error")) {
                //Transformamos al valor Json
                JSONObject objeto = null;
                objeto = new JSONObject(result);
                if (objeto.getString("success").toString() == "true") {
                    JSONObject jparent = new JSONObject(result);
                    JSONArray objeto2 = jparent.getJSONArray("resp");
                    for (int i=0; i < objeto2.length(); i++) {
                        LatLng sydney = new LatLng(Double.parseDouble(objeto2.getJSONObject(i).getString("lat")), Double.parseDouble(objeto2.getJSONObject(i).getString("long")));
                        googleMap.addMarker(new MarkerOptions().position(sydney).title(objeto2.getJSONObject(i).getString("title")));
                    }

                }
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-12.077230895617333, -77.093606546715), 11.0f));
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