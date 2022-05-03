package com.upc.inmoperu.ui.wish;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.upc.inmoperu.R;
import com.upc.inmoperu.ui.Helper.DatePickerFragment;
import com.upc.inmoperu.ui.Session;
import com.upc.inmoperu.ui.home.HomeFragment;
import com.upc.inmoperu.ui.home.InicioFragment;
import com.upc.inmoperu.ui.task.CompraTask;
import com.upc.inmoperu.ui.task.DetalleTask;
import com.upc.inmoperu.ui.task.WishListUserTask;
import com.upc.inmoperu.ui.task.WishlistUpdateTask;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class CompraFragment extends Fragment {
    EditText edtFecha,edtFechaVenc,edtNombre,edtDireccion,edtNroCuenta,edtCasID;
    Spinner ddlComprobante;
    private Session session;
    private static final String CHANNEL_ID = "ms";
    private static final String CHANNEL_NAME = "InmoPeru";
    private static final String CHANNEL_DESC = "InmoPeru notificaciones";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compra, container, false);
        edtFecha = (EditText)view.findViewById(R.id.txt_fecha_compra);
        edtFechaVenc = (EditText)view.findViewById(R.id.txt_fecha_vencimiento_compra);
        edtNombre = (EditText)view.findViewById(R.id.txt_nombre_compra);
        edtDireccion = (EditText)view.findViewById(R.id.txt_direccion_compra);
        ddlComprobante = (Spinner)view.findViewById(R.id.ddl_tipo_comprobante);
        edtNroCuenta = (EditText)view.findViewById(R.id.txt_nro_cuenta);
        edtCasID = (EditText)view.findViewById(R.id.txt_cas_id);

        //Importante creamos el canal de notificaciones
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getContext().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        session = new Session(getContext());
        edtNombre.setText(session.getusername());

        Spinner dropdown = view.findViewById(R.id.ddl_tipo_comprobante);
        String[] items = new String[]{"Tipo de Comprobante","Boleta","Factura"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        edtFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.txt_fecha_compra:
                        showDatePickerDialog();
                        break;
                }
            }
        });

        edtFechaVenc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.txt_fecha_vencimiento_compra:
                        showDatePickerDialog2();
                        break;
                }
            }
        });


        Button procesar_compra = (Button) view.findViewById(R.id.btn_registro_compra);
        procesar_compra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateInputsCompra()){
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Esta seguro de realizar la solicitud?");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            String result;
                            String Comprob = "0";
                            if (ddlComprobante.getSelectedItem().toString() == "Boleta") {
                                Comprob = "1";
                            } else {
                                Comprob = "2";
                            }
                            CompraTask compraTask = new CompraTask(getContext(), "http://inmoperu.azurewebsites.net/inmoperu-serv/public/api/crearCPago");
                            result = compraTask.execute(session.getuserid(), edtDireccion.getText().toString(), edtFecha.getText().toString(), Comprob,
                                    edtNroCuenta.getText().toString(), edtFechaVenc.getText().toString(), edtCasID.getText().toString(), session.gettotal()).get();

                            if (result != null && !result.contains("Error")) {
                                JSONObject objeto = null;
                                objeto = new JSONObject(result);
                                if (objeto.getString("success").toString() == "true") {
                                    //Obtenemos el valor Id mapeando todo el valor de json
                                    String cpagoid = objeto.getJSONObject("resp").getString("id");
                                    String wishlist = cargarListaWishList();
                                    String[] parts = wishlist.split("\\^");
                                    for (int i = 0; i < parts.length; i++) {
                                        String[] parts2 = parts[i].split("\\|");
                                        String result2;
                                        DetalleTask detalleTask = new DetalleTask(getContext(), "http://inmoperu.azurewebsites.net/inmoperu-serv/public/api/crearDPago");
                                        result2 = detalleTask.execute(cpagoid, parts2[1]).get();

                                        if (result2 != null && !result2.contains("Error")) {
                                            JSONObject objresult = null;
                                            objresult = new JSONObject(result2);
                                            if (objeto.getString("success").toString() == "true") {
                                                String result3;
                                                WishlistUpdateTask wishlistupdate = new WishlistUpdateTask(getContext(), "http://inmoperu.azurewebsites.net/inmoperu-serv/public/api/actualizarWishlist");
                                                result3 = wishlistupdate.execute(parts2[0]).get();
                                            } else {
                                                AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
                                                builder1.setMessage("Error al crear el usuario. Verifique datos");
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
                                    }
                                    session = new Session(getContext());
//                                    GenerarCorreo(cpagoid, wishlist, session.getuserid());
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
                                    builder1.setMessage("Se realizo correctamente la solicitud.");
                                    builder1.setCancelable(true);
                                    AlertDialog alert = builder1.create();
                                    alert.show();
                                    displayNotificaciones("Mensaje", "La noticia se encuentra registrada en nuestra Base de datos.");
                                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                    transaction.replace(R.id.nav_container, new HomeFragment());
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                } else {
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
                                    builder1.setMessage("Error al crear el usuario. Verifique datos");
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
            }
        });


        return view;
    }

    private void displayNotificaciones(String title, String msg){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getContext(),CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_note_add_black_36dp)
                        .setContentTitle(title)
                        .setContentText(msg)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getContext());
        notificationManagerCompat.notify(1,mBuilder.build());

    }

    private String cargarListaWishList() throws ExecutionException, InterruptedException, JSONException {
        session = new Session(getContext());
        String result2,solution="";
        WishListUserTask wishListUserTask = new WishListUserTask(getContext(),"http://inmoperu.azurewebsites.net/inmoperu-serv/public/api/listarwish");
        result2 = wishListUserTask.execute(session.getuserid()).get();

        if(result2 != null && !result2.contains("Error")) {
            JSONObject objeto2 = null;
            objeto2 = new JSONObject(result2);
            if (objeto2.getString("success").toString() == "true") {
                //Obtenemos el valor Id mapeando todo el valor de json
                JSONObject jparent = new JSONObject(result2);
                JSONArray objeto3 = jparent.getJSONArray("resp");
                for (int i=0; i < objeto3.length(); i++) {
                    JSONArray objeto4 = objeto3.getJSONObject(i).getJSONArray("producto");
                    for (int j=0; j < objeto4.length(); j++) {
                        solution = solution + objeto3.getJSONObject(i).getString("id") + '|';
                        solution = solution + objeto4.getJSONObject(j).getString("id") + '|';
                        solution = solution + objeto4.getJSONObject(j).getString("title") + '|';
                        solution = solution + objeto4.getJSONObject(j).getString("description") + '|';
                        solution = solution + objeto4.getJSONObject(j).getString("price") + '^';
                    }
                }

            } else {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                builder1.setMessage("Error al crear el usuario. Verifique datos");
                builder1.setCancelable(true);
                AlertDialog alert = builder1.create();
                alert.show();
            }
        }else
        {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
            builder1.setMessage("Error al realizar la conexion de la data. Consulte con el administrador");
            builder1.setCancelable(true);
            AlertDialog alert = builder1.create();
            alert.show();
        }
        solution = solution.substring(0,solution.length()-1);
        return solution;
    }



    public boolean validateInputsCompra() {
        Boolean valor = true;
        String resultado = "";


        if(edtFecha.getText().toString().matches("")){
            resultado = resultado + "Ingrese una fecha \n";
        }

        if(edtFechaVenc.getText().toString().matches("")){
            resultado = resultado + "Ingrese un fecha de vencimiento \n";
        }

        if(edtDireccion.getText().toString().matches("")){
            resultado = resultado + "Ingrese una direccion \n";
        }

        if(edtNroCuenta.getText().toString().matches("")){
            resultado = resultado + "Ingrese un numero de cuenta \n";
        }

        if(edtCasID.getText().toString().matches("")){
            resultado = resultado + "Ingrese un cas ID";
        }

        if(ddlComprobante.getSelectedItem().toString() == "Tipo de Comprobante"){
            resultado = resultado + "Seleccione el tipo de Comprobante";
        }

        if(resultado == "")
        {
            valor = true;
        }else{
            Toast toast1 =
                    Toast.makeText(getContext(),
                            resultado, Toast.LENGTH_LONG);

            toast1.show();
            valor = false;
        }
        return valor;
    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                final String selectedDate = day + " / " + (month+1) + " / " + year;
                edtFecha.setText(selectedDate);
            }
        });

        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    private void showDatePickerDialog2() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                final String selectedDate = day + " / " + (month+1) + " / " + year;
                edtFechaVenc.setText(selectedDate);
            }
        });

        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }


}
