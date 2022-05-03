package com.upc.inmoperu.ui.contact;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.upc.inmoperu.LoadingScreen;
import com.upc.inmoperu.R;
import com.upc.inmoperu.ui.Session;
import com.upc.inmoperu.ui.account.LoginFragment;
import com.upc.inmoperu.ui.task.ReclamoTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutionException;


public class ReclamoFragment extends Fragment {
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888;
    private Button btnTakePhoto;
    private ImageView imgPhoto;
    private TextView hid_img;
    private EditText txtTitle,txtAsunto,txtDescription;
    String userid;
    private static final String CHANNEL_ID = "ms";
    private static final String CHANNEL_NAME = "InmoPeru";
    private static final String CHANNEL_DESC = "InmoPeru notificaciones";
    private Session session;
    final DialogFragment loadingScreen = LoadingScreen.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_reclamo, container, false);
        session = new Session(getContext());
        if (session.getuserid() == "") {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_container, new LoginFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        }
        userid = session.getuserid();
        hid_img = (TextView) root.findViewById(R.id.hid_img_reclamo);
        hid_img.setText("False");
        txtTitle = (EditText) root.findViewById(R.id.txt_title_reclamo);
        txtAsunto = (EditText)root.findViewById(R.id.txt_asunto_reclamo);
        txtDescription = (EditText)root.findViewById(R.id.txt_descripcion_reclamo);

        Button btn_return_reclamo = (Button) root.findViewById(R.id.btn_return_reclamo);
        btn_return_reclamo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_container, new ContactFragment());
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

        Button btn_registro_reclamo = (Button) root.findViewById(R.id.btn_registro_reclamo);
        btn_registro_reclamo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(validateInputsReclamo()){
                    loadingScreen.show(getFragmentManager(),"loading screen");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // obtenemos la imagen
                                BitmapDrawable drawable = (BitmapDrawable) imgPhoto.getDrawable();
                                Bitmap bitmap = drawable.getBitmap();
                                ByteArrayOutputStream stream=new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
                                byte[] image=stream.toByteArray();

                                String img_str = Base64.encodeToString(image, 0);
                                String img1 = "";
                                String img2 = "";
                                String img3 = "";
                                if(img_str.length()>80000){
                                    img1= "data:image/jpeg;base64," + img_str.substring(0,80000);
                                    if(img_str.length()>160000){
                                        img2 = img_str.substring(80000,160000);
                                        if(img_str.length()>240000){
                                            img3 = img_str.substring(160000,240000);
                                        }else{
                                            img3 = img_str.substring(160000,img_str.length()-160000);
                                        }
                                    }else{
                                        img2 = img_str.substring(80000,img_str.length() - 80000);
                                    }
                                }else{
                                    img1= "data:image/jpeg;base64," + img_str;
                                }

                                String result;
                                ReclamoTask reclamoTask = new ReclamoTask(getContext(),"http://inmoperu.azurewebsites.net/inmoperu-serv/public/api/createReclamo");
                                result = reclamoTask.execute(userid,txtTitle.getText().toString(),txtAsunto.getText().toString(),txtDescription.getText().toString(),img1.toString(),img2.toString(),img3.toString()).get();
                                if(result != null && !result.contains("Error")) {
                                    //Transformamos al valor Json
                                    JSONObject objeto = null;
                                    objeto = new JSONObject(result);
                                    if (objeto.getString("success").toString() == "true") {
                                        //Obtenemos el valor Id mapeando todo el valor de json
                                        String idSolicitud = objeto.getJSONObject("resp").getString("id");
                                        displayNotificaciones("Mensaje", "El reclamo con codigo : " + idSolicitud + " se encuentra en estado pendiente de revisar.");
                                        vaciarDataReclamo();
                                    }else{
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
                                        builder1.setMessage("Error al crear el reclamo. Verifique datos");
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


        initActivity(root);
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

    private void initActivity(View view) {
        btnTakePhoto = (Button)view.findViewById(R.id.btn_take_photo2);
        imgPhoto = (ImageView)view.findViewById(R.id.imgPhoto2);
        CreateOnClickBtnTakePhoto();
    }

    private void CreateOnClickBtnTakePhoto() {
        btnTakePhoto.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                tomaFoto();
            }
        });
    }

    private void tomaFoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                Bitmap bmp = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                // convert byte array to Bitmap

                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
                        byteArray.length);

                imgPhoto.setImageBitmap(bitmap);
                hid_img.setText("True");
            }
        }
    }

    public boolean validateInputsReclamo() {
        Boolean valor = true;
        String resultado = "";

        if(hid_img.getText().toString() == "False"){
            resultado = "Ingrese una imagen \n";
        }

        if(txtTitle.getText().toString().matches("")){
            resultado = resultado + "Ingrese un titulo \n";
        }

        if(txtAsunto.getText().toString().matches("")){
            resultado = resultado + "Ingrese un asunto \n";
        }

        if(txtDescription.getText().toString().matches("")){
            resultado = resultado + "Ingrese una descripcion";
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

    public void vaciarDataReclamo(){
        txtTitle.setText("");
        txtAsunto.setText("");
        txtDescription.setText("");
        imgPhoto.setImageResource(R.drawable.ic_image_black_24dp);
        hid_img.setText("False");
    }


}
