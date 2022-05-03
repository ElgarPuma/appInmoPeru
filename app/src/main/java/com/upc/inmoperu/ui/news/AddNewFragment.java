package com.upc.inmoperu.ui.news;


import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.upc.inmoperu.R;
import com.upc.inmoperu.ui.DB.DBAdapter;
import com.upc.inmoperu.ui.Helper.DatePickerFragment;
import com.upc.inmoperu.ui.Session;
import com.upc.inmoperu.ui.account.LoginFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddNewFragment extends Fragment {
    EditText edtTitle,edtDescription, edtFecha;
    private static final String CHANNEL_ID = "ms";
    private static final String CHANNEL_NAME = "InmoPeru";
    private static final String CHANNEL_DESC = "InmoPeru notificaciones";
    private Session session;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_add_new, container, false);
        session = new Session(getContext());
        edtTitle = (EditText)root.findViewById(R.id.txt_title_new);
        edtDescription = (EditText)root.findViewById(R.id.txt_description_new);
        edtFecha = (EditText)root.findViewById(R.id.txt_fecha_new);

        //Importante creamos el canal de notificaciones
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getContext().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        session = new Session(getContext());
        if (session.getuserid() == "") {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_container, new LoginFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        }
        Button btn_return_new = (Button) root.findViewById(R.id.btn_return_new);
        btn_return_new.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_container, new NewsFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        Button btn_registro = (Button) root.findViewById(R.id.btn_registro_new);

        final DBAdapter dbAdapter=new DBAdapter(getContext());
        btn_registro.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(validateInputsOferta()){
                    dbAdapter.agregarOferta(session.getuserid(), edtTitle.getText().toString(),edtDescription.getText().toString(),edtFecha.getText().toString());
                    displayNotificaciones("Mensaje", "La oferta se encuentra registrada en nuestra Base de datos.");
                    vaciarDataOferta();
                }
            }
        });



        edtFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.txt_fecha_new:
                        showDatePickerDialog();
                        break;
                }
            }
        });

        return root;
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

    public boolean validateInputsOferta() {
        Boolean valor = true;
        String resultado = "";

        if(edtTitle.getText().toString().matches("")){
            resultado = resultado + "Ingrese un titulo \n";
        }

        if(edtDescription.getText().toString().matches("")){
            resultado = resultado + "Ingrese una descripcion \n";
        }

        if(edtFecha.getText().toString().matches("")){
            resultado = resultado + "Ingrese una fecha";
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

    public void vaciarDataOferta(){
        edtTitle.setText("");
        edtDescription.setText("");
        edtFecha.setText("");
    }
}
