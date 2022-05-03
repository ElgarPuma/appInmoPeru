package com.upc.inmoperu.ui.account;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.upc.inmoperu.LoadingScreen;
import com.upc.inmoperu.R;
import com.upc.inmoperu.ui.task.OlvidasteTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;


public class OlvidasteContrasenaFragment  extends Fragment {
    private TextView txt_email_olvidaste;
    final DialogFragment loadingScreen = LoadingScreen.getInstance();
    private static final String CHANNEL_ID = "ms";
    private static final String CHANNEL_NAME = "InmoPeru";
    private static final String CHANNEL_DESC = "InmoPeru notificaciones";
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_olvidaste_contrasena, container, false);
        txt_email_olvidaste = (TextView) root.findViewById(R.id.txt_email_olvidaste);
        Button btn_return_olvidaste_contrasena = (Button) root.findViewById(R.id.btn_return_olvidaste_contrasena);
        btn_return_olvidaste_contrasena.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_container, new LoginFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        //Importante creamos el canal de notificaciones
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getContext().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        //Permiso para que te deje enviar sms , de igual manera se pone en el manifest
        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),Manifest.
                permission.SEND_SMS)!=PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]
                    {
                            Manifest.permission.SEND_SMS,
                    }, 1000);
        }else{

        }
        Button btn_recuperar_contrasena = (Button) root.findViewById(R.id.btn_recuperar_contrasena);
        btn_recuperar_contrasena.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(validateOlvidasteContraseña()){
                    loadingScreen.show(getFragmentManager(),"loading screen");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String result;
                                OlvidasteTask olvidasteTask = new OlvidasteTask(getContext(),"http://inmoperu.azurewebsites.net/inmoperu-serv/public/api/getUserEmail");
                                result = olvidasteTask.execute(txt_email_olvidaste.getText().toString()).get();
                                if(result != null && !result.contains("Error")) {
                                    //Transformamos al valor Json
                                    JSONObject objeto = null;
                                    objeto = new JSONObject(result);
                                    if (objeto.getString("success").toString() == "true") {
                                        //Obtenemos el valor Id mapeando todo el valor de json
                                        String username = objeto.getJSONArray("resp").getJSONObject(0).getString("nombre") + " " + objeto.getJSONArray("resp").getJSONObject(0).getString("apellido");
                                        displayNotificaciones("Mensaje", "Estimado usuario " + username+ " se le envio un mensaje con su usuario.");
                                        enviarMensaje(objeto.getJSONArray("resp").getJSONObject(0).getString("number"), "Se le reenvia los datos de acceso para el app MovilStore  \n Datos : \n User: " + objeto.getJSONArray("resp").getJSONObject(0).getString("email") + "\n Pass: " + objeto.getJSONArray("resp").getJSONObject(0).getString("pass"));
                                        vaciarDataOlvidaste();
                                    } else {
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
                                        builder1.setMessage("Error al recuperar la contraseña. Verifique datos");
                                        builder1.setCancelable(true);
                                        AlertDialog alert = builder1.create();
                                        alert.show();
                                    }
                                }else{
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
                                    builder1.setMessage("Error al realizar la conexion de la data. Consulte con el administrador");
                                    builder1.setCancelable(true);
                                    AlertDialog alert = builder1.create();
                                    alert.show();
                                }
                                loadingScreen.dismiss();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },3000);
                }
            }
        });
        return root;
    }

    private void displayNotificaciones(String title, String msg){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getContext(),CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_wish_black_24dp)
                        .setContentTitle(title)
                        .setContentText(msg)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getContext());
        notificationManagerCompat.notify(1,mBuilder.build());

    }


    public boolean validateOlvidasteContraseña() {
        Boolean valor = true;
        String resultado = "";

        if(txt_email_olvidaste.getText().toString().matches("")){
            resultado = resultado + "Ingrese el email correspondiente";
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

    public void vaciarDataOlvidaste(){
        txt_email_olvidaste.setText("");
    }

    private void enviarMensaje(String numero, String mensaje){
        try{
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(numero,null,mensaje,null,null);
            Toast.makeText(getContext(),"Mensaje Enviado",Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(getContext(),"Mensaje no enviado, Datos Incorrectos", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }
}
