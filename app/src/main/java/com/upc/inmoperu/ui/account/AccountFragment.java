package com.upc.inmoperu.ui.account;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.upc.inmoperu.LoadingScreen;
import com.upc.inmoperu.R;
import com.upc.inmoperu.ui.Session;
import com.upc.inmoperu.ui.task.AccountTask;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class AccountFragment extends Fragment{

    private EditText txtNombre,txtApellido,txtEmail,txtPass,txtNumber;
    final DialogFragment loadingScreen = LoadingScreen.getInstance();
    private Session session;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_account, container, false);
        session = new Session(getContext());
        if (session.getuserid() != "") {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_container, new com.upc.inmoperu.ui.account.UserFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        }

        //obtenemos los valores para la validacion
        txtNombre = (EditText) root.findViewById(R.id.txt_nombre);
        txtApellido = (EditText)root.findViewById(R.id.txt_apellido);
        txtEmail = (EditText)root.findViewById(R.id.txt_email);
        txtPass = (EditText)root.findViewById(R.id.txt_password);
        txtNumber = (EditText)root.findViewById(R.id.txt_number);
        //Permiso para que te deje enviar sms , de igual manera se pone en el manifest
        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),Manifest.
                permission.SEND_SMS)!=PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]
                    {
                            Manifest.permission.SEND_SMS,
                    }, 1000);
        }else{

        }
        Button btn_ingresar = (Button) root.findViewById(R.id.btn_ingresar);
        btn_ingresar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_container, new LoginFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        Button btnRegistrar = (Button) root.findViewById(R.id.btn_registro);
        btnRegistrar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(validateInputsAccount()) {
                    loadingScreen.show(getFragmentManager(),"loading screen");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String result;
                                AccountTask accountTask = new AccountTask(getContext(),"http://inmoperu.azurewebsites.net/inmoperu-serv/public/api/createUser");
                                result = accountTask.execute(txtNombre.getText().toString(),txtApellido.getText().toString(),txtEmail.getText().toString(),txtPass.getText().toString(),txtNumber.getText().toString()).get();
                                if(result != null && !result.contains("Error")) {
                                    //Transformamos al valor Json
                                    JSONObject objeto = null;
                                    objeto = new JSONObject(result);
                                    if (objeto.getString("success").toString() == "true") {
                                        //Obtenemos el valor Id mapeando todo el valor de json
                                        String nombre = objeto.getJSONObject("resp").getString("nombre");
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
                                        builder1.setMessage("El usuario " + nombre + " se encuentra registrado correctamente.");
                                        builder1.setCancelable(true);
                                        AlertDialog alert = builder1.create();
                                        alert.show();
                                        enviarMensaje(txtNumber.getText().toString(), "Se creo la cuenta para el app Movil Store  \n Datos : \n User: " + objeto.getJSONObject("resp").getString("email") + "\n Pass: " + objeto.getJSONObject("resp").getString("pass"));
                                        vaciarDataAccount();
                                    } else {
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
                                        builder1.setMessage("Error al crear el usuario. Verifique datos");
                                        builder1.setCancelable(true);
                                        AlertDialog alert = builder1.create();
                                        alert.show();
                                    }
                                }else
                                {
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


    public boolean validateInputsAccount() {
        Boolean valor = true;
        String resultado = "";

        if(txtNombre.getText().toString().matches("")){
            resultado = resultado + "Ingrese un Nombre \n";
        }

        if(txtApellido.getText().toString().matches("")){
            resultado = resultado + "Ingrese un apellido \n";
        }

        if(txtEmail.getText().toString().matches("")){
            resultado = resultado + "Ingrese un email \n";
        }

        if(txtPass.getText().toString().matches("")){
            resultado = resultado + "Ingrese una contraseña \n";
        }

        if(txtNumber.getText().toString().matches("")){
            resultado = resultado + "Ingrese un numero telefonico";
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

    public void vaciarDataAccount(){
        txtNombre.setText("");
        txtApellido.setText("");
        txtEmail.setText("");
        txtPass.setText("");
        txtNumber.setText("");
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

