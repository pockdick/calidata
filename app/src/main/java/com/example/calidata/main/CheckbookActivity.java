package com.example.calidata.main;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calidata.OnSingleClickListener;
import com.example.calidata.R;
import com.example.calidata.main.adapters.RecyclerViewAdapterCheckbook;
import com.example.calidata.main.controllers.CheckbookController;
import com.example.calidata.models.CheckbookModel;
import com.example.calidata.models.User;
import com.example.calidata.utilities.HelpActivity;
import com.example.calidata.utilities.SettingsActivity;
import com.google.android.material.navigation.NavigationView;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class CheckbookActivity extends ParentActivity {

    @BindView(R.id.drawer)
    public DrawerLayout drawerLayout;

    @BindView(R.id.nav_view)
    public NavigationView navigationView;

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindView(R.id.floatingActionButton)
    public ImageView addCheckbookBtn;

    @BindView(R.id.progressBar)
    public ProgressBar progressBar;

    RecyclerViewAdapterCheckbook adapter;

    //private ArrayList<String> checkbooks;
    private ArrayList<CheckbookModel> checkbooksList;

    public CircleImageView imageProfile;
    private CheckbookController controller;
    private Double userId;
    private TextView textName;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        //setTheme(intent);
        setThemeByName(intent);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkbook);
        ButterKnife.bind(this);
        controller = new CheckbookController(this);
        String title = getResources().getString(R.string.checkbook_title);
        setToolbar(toolbar, title, false);
        userId = sessionManager.getUserId();

        initNavBar();
/*
        checkbooks = new ArrayList<>();

        checkbooks.add("**** **** **** **** 1800");
        checkbooks.add("**** **** **** **** 1856");
        checkbooks.add("**** **** **** **** 7800");
        checkbooks.add("**** **** **** **** 9900");
//*/

        checkbooksList = new ArrayList<>();


        // set up the RecyclerView
        recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        //adapter = new RecyclerViewAdapterCheckbook(this, checkbooks);
        adapter = new RecyclerViewAdapterCheckbook(this, checkbooksList);

        recyclerView.setAdapter(adapter);

        addCheckbookBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                //openDialog();
                progressBar.setVisibility(View.VISIBLE);
                readQR();
            }
        });

        addCheckbookBtn.setColorFilter(getPrimaryColorInTheme(), PorterDuff.Mode.SRC_IN);

        addCheckbookBtn.bringToFront();
        progressBar.bringToFront();
        readCheckBooks();

    }

    private void readCheckBooks() {
        int checkbooksNum = 0;
        if (userId != 0) {
            /*
            controller.getCheckbooks(userId).subscribe(response -> {
                for (CheckbookModel checkbookModel : response) {
                    checkbooks.add(checkbookModel.getCheckId());
                }

            });
            //*/

            for (int i = 0; i < checkbooksNum; i++) {
                CheckbookModel checkbookModel = new CheckbookModel();
                checkbookModel.setCheckbookId("checkbookId: " + i);
                String randomId = UUID.randomUUID().toString();
                String hidden = hideCheckId(randomId);
                checkbookModel.setCheckId(hidden + "_" + i);
                checkbookModel.setTypeDoc("00");
                checkbooksList.add(checkbookModel);
            }
            //adapter.notifyDataSetChanged();
        }
    }

    private String hideCheckId(String checkId) {
        int size = checkId.length();
        String hidden = "";
        String startSubString = checkId.substring(0, size - 4);
        String endSubString = checkId.substring(size - 4, size);
        for (int i = 0; i < size - 4; i++) {
            hidden += "*";
        }
        return hidden + endSubString;
    }

    private void initNavBar() {
        View header = navigationView.getHeaderView(0);
        header.setBackgroundColor(getPrimarySoftColorInTheme());
        textName = header.findViewById(R.id.textView_username);
        textName.setTextColor(getColor(R.color.white));
        imageProfile = header.findViewById(R.id.imageView_profile);


        if (sessionManager.getKeyUsername() != null) {
            textName.setText(sessionManager.getKeyUsername());
        }
        if (sessionManager.getKeyImage64() != null) {
            putImage(sessionManager.getKeyImage64(), imageProfile);

        }
        //llamar servicio de informacion de usuario
        /*
        controller.getUserInformation(userId).subscribe(response ->{
            textName.setText(response.getUserName());
            String image64 = response.getImage64();
            putImage(image64, imageProfile);
        });
        //*/


        imageProfile.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                //pickFromGallery();
                Intent intent = new Intent(CheckbookActivity.this, ImageViewActivity.class);
                startActivity(intent);
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.openDrwawer, R.string.openDrwawer) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            drawerLayout.closeDrawers();
            Intent intent;
            switch (menuItem.getItemId()) {
                /*
                case R.id.nav_query:
                    intent = new Intent(this, CheckQueryActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.nav_emit:
                    intent = new Intent(this, CheckEmitActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.nav_cancel:
                    intent = new Intent(this, CheckCancelActivity.class);
                    startActivity(intent);
                    return true;
                    //*/
                case R.id.nav_settings:
                    intent = new Intent(this, SettingsActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.nav_help:
                    intent = new Intent(this, HelpActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.nav_close:
                    logout();
                    return true;
                default:
                    return false;
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        //getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_logout:
                logout();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        ConstraintLayout view = (ConstraintLayout) inflater.inflate(R.layout.emit_dialog, null);
        builder.setView(view);

        AlertDialog alertDialog = builder.create();

        TextView label = view.findViewById(R.id.textView_label);
        label.setText(getString(R.string.active_checkbook_label));
        Button scanBtn = view.findViewById(R.id.button_yes);
        scanBtn.setBackgroundColor(getPrimaryColorInTheme());
        scanBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                readQR();
                alertDialog.dismiss();
            }
        });

        Button searchBtn = view.findViewById(R.id.button_no);
        searchBtn.setText(getString(R.string.insert_data));
        searchBtn.setBackgroundColor(getPrimaryColorInTheme());
        searchBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent i = new Intent(v.getContext(), CheckbookAddActivity.class);
                startActivityForResult(i, 0);
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

    }


    private void readQR() {
        try {

            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes
            intent.putExtra("SCAN_MODE", "BAR_CODE_MODE"); // "PRODUCT_MODE for bar codes

            startActivityForResult(intent, 0);

        } catch (Exception e) {

            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
            startActivity(marketIntent);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");


                if (contents != null) {
                    String token = "bearer A9qzBO9lLSazlXwcoYqLamn0bLa0rI35OX2YY4RYQp3Y-B2MHAeQwSMkmj5hr1PQAQpRRaJJXtSXiv9zi-u4LB-OqWwPpLutzmeFWhk_Dv2uB83-CvFgXvTAgsQXyCFq1Han89O5aKzK4WWkCzi71O8GF8-FZ5ZUMiCU2IFpDXcFe28Y2tcNNq0U_l3E-ia4BebN174qOGYIUb0NHKfrPtaJMeCbWsG9-ZKDQBxcxv6cLvg4JZcWi-1lw2AoWzxqQ9iT4Rvj1PjNCsnJTTjozQ";

                    controller.addCheckbook(token, "aaaaaa").subscribe(response -> {
                        //if(response.getData() != null && response.getData().isEmpty()){
                        CheckbookModel checkbookModel = new CheckbookModel();
                        checkbookModel.setTypeDoc("00");
                        checkbookModel.setCheckId(shortUUID());
                        checkbookModel.setCheckbookId(shortUUID());
                        checkbooksList.add(checkbookModel);

                        adapter.notifyItemInserted(checkbooksList.size());

                        recyclerView.postDelayed(() -> {
                            recyclerView.scrollToPosition(checkbooksList.size() - 1);
                        }, 500);
                        progressBar.setVisibility(View.GONE);


                        //}

                    }, t -> {
                        Log.e("", t.getMessage());
                    });

                }

                //*/

                User user = User.getInstance();

                //*/
            }
            if (resultCode == RESULT_CANCELED) {
                //handle cancel
                Log.i("TAG-QR", "CANCELADO");
            }
        }
        if (requestCode == PICK_IMAGE) {
            Log.i("DATA", "data: " + data);
            if (data != null && data.getData() != null) {
                imageProfile.setImageURI(data.getData());
            }
        }
        progressBar.setVisibility(View.GONE);
    }

    private String hex2String(String hexString) throws DecoderException, UnsupportedEncodingException {
        byte[] bytes = Hex.decodeHex(hexString.toCharArray());
        return new String(bytes, "UTF-8");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (adapter.getItemCount() > 0) {
            System.out.println(adapter.getItemCount());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SettingsActivity.imageObs != null) {
            SettingsActivity.imageObs.subscribe(response -> {
                if (response != null) {
                    putImage(response, imageProfile);
                }
            });
        }
        if (SettingsActivity.usernameObs != null) {
            SettingsActivity.usernameObs.subscribe(response -> {
                if (response != null) {
                    textName.setText(response);
                }
            });
        }

    }
}
