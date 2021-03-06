package com.eldercare.eldercare.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eldercare.eldercare.R;
import com.eldercare.eldercare.config.ConfiguracaoFirebase;
import com.eldercare.eldercare.databinding.ActivityPrincipalBinding;
import com.eldercare.eldercare.helper.Base64Custom;
import com.eldercare.eldercare.model.Utilizador;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;


public class PrincipalActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityPrincipalBinding binding;

    //variaveis para os textviews do menu lateral
    private TextView textNomeMenu, textEmailMenu;

    //variáveis do firebase
    private DatabaseReference firebaseRef;
    private FirebaseAuth autenticacao;

    private ValueEventListener utilizadorEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPrincipalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarPrincipal.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_dashboard,
                R.id.nav_pressao,
                R.id.nav_glicemia,
                R.id.nav_lembretes,
                R.id.nav_calendario,
                R.id.nav_contactos,
                R.id.nav_notas,
                R.id.nav_definicoes)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_principal);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    @Override
    protected void onStart() {
        super.onStart();

        //Buscar o id a outro layout (layout do menu lateral)
        // Obtém a referência do layout de navegação
        NavigationView navigationView1 = (NavigationView) findViewById(R.id.nav_view);

        // Obtém a referência da view de cabeçalho
        View headerView = navigationView1.getHeaderView(0);

        // Obtém a referência do nome do utilizador e altera o nome
        TextView textNomeMenu = (TextView) headerView.findViewById(R.id.textNomeMenu);
        TextView textEmailMenu = (TextView) headerView.findViewById(R.id.textEmailMenu);
        ImageView imagePfp = (ImageView) headerView.findViewById(R.id.imagePfp);

        //botão de logout
        /*navigationView1.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(
                new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        logOut();
                        return false;
                    }
                }
        );*/

        //Mudar o nome de utilizador no menu
        //Buscar e-mail
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        String emailUser = autenticacao.getCurrentUser().getEmail();

        //Buscar Nome
        firebaseRef = ConfiguracaoFirebase.getFirebaseRef();

        //codifica o email para ir buscar o identificador
        String idUser = Base64Custom.codificarBase64(emailUser);

        DatabaseReference utilizadorRef = firebaseRef.child("utilizadores").child(idUser);

        utilizadorEventListener = utilizadorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(!snapshot.exists()) {
                    autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
                    //autenticacao.signOut();
                    //finish();
                }else{
                    Utilizador utilizador = snapshot.getValue(Utilizador.class);

                    textNomeMenu.setText(utilizador.getNome());
                    textEmailMenu.setText(utilizador.getEmail());
                    if (utilizador.getTipo().equals("p")){
                        imagePfp.setImageResource(R.drawable.pacientepfp);
                    }else{
                        imagePfp.setImageResource(R.drawable.cuidadorpfp);
                    }
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //guarda o token das notificacoes na database
        recuperarToken();
    }

    public void recuperarToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseRef();
                FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

                String idUtilizador = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());

                firebaseRef.child("utilizadores")
                        .child(idUtilizador)
                        .child("token")
                        .setValue(s);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.notificacoes){
            startActivity(new Intent(PrincipalActivity.this, NotificacoesActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    /*public void logOut(){
        autenticacao.signOut();
        finish();
    }*/

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_principal);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}