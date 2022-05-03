package com.upc.inmoperu.ui.account;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.upc.inmoperu.LoadingScreen;
import com.upc.inmoperu.R;
import com.upc.inmoperu.ui.Session;
import com.upc.inmoperu.ui.task.LoginTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class LoginFragment extends Fragment {

    private EditText txtEmail,txtPass;
    private Session session;


    final DialogFragment loadingScreen = LoadingScreen.getInstance();
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        Button btn_return_login = (Button) root.findViewById(R.id.btn_return_login);
        txtEmail = (EditText) root.findViewById(R.id.txt_email_login);
        txtPass = (EditText)root.findViewById(R.id.txt_contrasena_login);

        btn_return_login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_container, new com.upc.inmoperu.ui.account.AccountFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        Button lnk_olvidaste_contrasena = (Button) root.findViewById(R.id.lnk_olvidaste_contrasena);
        lnk_olvidaste_contrasena.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_container, new com.upc.inmoperu.ui.account.OlvidasteContrasenaFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        Button btn_ingresar_login = (Button) root.findViewById(R.id.btn_registro2);
        btn_ingresar_login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(validateInputsLogin()) {
                    loadingScreen.show(getFragmentManager(),"loading screen");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String result;
                                LoginTask loginTask = new LoginTask(getContext(),"http://inmoperu.azurewebsites.net/inmoperu-serv/public/api/login");
                                result = loginTask.execute(txtEmail.getText().toString(),txtPass.getText().toString()).get();
                                if(result != null && !result.contains("Error")) {
                                    //Transformamos al valor Json
                                    JSONObject objeto = null;
                                    objeto = new JSONObject(result);
                                    if (objeto.getString("success").toString() == "true") {
                                        session = new Session(getContext());
                                        session.setuserid(objeto.getJSONArray("resp").getJSONObject(0).getString("id"));
                                        session.setusername(objeto.getJSONArray("resp").getJSONObject(0).getString("nombre") + " " + objeto.getJSONArray("resp").getJSONObject(0).getString("apellido"));
                                        vaciarDataLogin();
                                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                        transaction.replace(R.id.nav_container, new UserFragment());
                                        transaction.addToBackStack(null);
                                        transaction.commit();
                                    } else {
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
                                        builder1.setMessage("Error al loguearse. Verifique datos");
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



    public boolean validateInputsLogin() {
        Boolean valor = true;
        String resultado = "";

        if(txtEmail.getText().toString().matches("")){
            resultado = resultado + "Ingrese su email \n";
        }

        if(txtPass.getText().toString().matches("")){
            resultado = resultado + "Ingrese su password";
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

    public void vaciarDataLogin() {
        txtEmail.setText("");
        txtPass.setText("");
    }
}
