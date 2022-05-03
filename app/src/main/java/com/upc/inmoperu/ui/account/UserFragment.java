package com.upc.inmoperu.ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.upc.inmoperu.R;
import com.upc.inmoperu.ui.Session;

public class UserFragment extends Fragment {
    private Session session;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_user, container, false);
        TextView txt_user_session = (TextView) root.findViewById(R.id.txt_user_session);
        Button btn_cerrar_sesion = (Button) root.findViewById(R.id.btn_cerrar_sesion);
        session = new Session(getContext());
        txt_user_session.setText(session.getusername());
        btn_cerrar_sesion.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                session = new Session(getContext());
                session.setuserid("");
                session.setusername("");
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_container, new LoginFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return root;
    }
}
