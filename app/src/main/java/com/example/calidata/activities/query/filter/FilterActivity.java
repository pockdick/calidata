package com.example.calidata.activities.query.filter;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.calidata.R;
import com.example.calidata.main.ParentActivity;
import com.example.calidata.management.ManagerTheme;
import com.github.guilhe.views.SeekBarRangedView;

import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FilterActivity extends ParentActivity implements AdapterView.OnItemSelectedListener {
    private final static int MAX = 500000;
    private static final String CERO = "0";
    private static final String BARRA = "/";

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindView(R.id.button_apply)
    public Button applyBtn;


    @BindView(R.id.textView_min)
    public TextView minText;

    @BindView(R.id.textView_max)
    public TextView maxText;

    @BindView(R.id.spinner)
    public Spinner spinner;

    @BindView(R.id.textView_date_start)
    public TextView dateText;

    @BindView(R.id.textView_date_end)
    public TextView endText;


    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();

    //Variables para obtener la fecha
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);

    @OnClick(R.id.constraintLayout_date_start)
    public void getStartDate(){
        //Estos valores deben ir en ese orden, de lo contrario no mostrara la fecha actual
/**
 *También puede cargar los valores que usted desee
 */
        DatePickerDialog recogerFecha = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
            final int mesActual = month + 1;
            //Formateo el día obtenido: antepone el 0 si son menores de 10
            String diaFormateado = (dayOfMonth < 10)? CERO + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
            //Formateo el mes obtenido: antepone el 0 si son menores de 10
            String mesFormateado = (mesActual < 10)? CERO + String.valueOf(mesActual):String.valueOf(mesActual);
            //Muestro la fecha con el formato deseado
            dateText.setText(diaFormateado + BARRA + mesFormateado + BARRA + year);


        },anio, mes, dia);
        //Muestro el widget
        recogerFecha.show();
    }

    @OnClick(R.id.constraintLayout_date_end)
    public void getEndDate() {
        DatePickerDialog recogerFecha = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
            final int mesActual = month + 1;
            //Formateo el día obtenido: antepone el 0 si son menores de 10
            String diaFormateado = (dayOfMonth < 10)? CERO + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
            //Formateo el mes obtenido: antepone el 0 si son menores de 10
            String mesFormateado = (mesActual < 10)? CERO + String.valueOf(mesActual):String.valueOf(mesActual);
            //Muestro la fecha con el formato deseado
            endText.setText(diaFormateado + BARRA + mesFormateado + BARRA + year);


        },anio, mes, dia);
        //Muestro el widget
        recogerFecha.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        managerTheme = ManagerTheme.getInstance();
        setTheme(managerTheme.getThemeId());
        setContentView(R.layout.activity_filter);

        ButterKnife.bind(this);

        String title = getResources().getString(R.string.filter_title);
        setToolbar(toolbar, title, true);

        SeekBarRangedView rangebar = findViewById(R.id.rangebar1);
        initSeekBar(rangebar);
        applyBtn.setBackgroundColor(getPrimaryColorInTheme());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.status_check, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

    }

    private void initSeekBar(SeekBarRangedView rangebar) {
        String min = "min: 0";
        String max = "max: " + MAX;
        minText.setText(min);
        maxText.setText(max);

        rangebar.setRounded(true);
        rangebar.setProgressColor(getPrimaryColorInTheme());
        rangebar.setMaxValue(MAX);
        rangebar.setMinValue(0);

        rangebar.setOnSeekBarRangedChangeListener(new SeekBarRangedView.OnSeekBarRangedChangeListener() {
            @Override
            public void onChanged(SeekBarRangedView view, float minValue, float maxValue) {
                updateLayout(minValue, maxValue);
            }

            @Override
            public void onChanging(SeekBarRangedView view, float minValue, float maxValue) {
                updateLayout(minValue, maxValue);
            }

            private void updateLayout(float minValue, float maxValue) {
                String min = String.format(Locale.getDefault(), "min: %2.0f", minValue);
                String max = String.format(Locale.getDefault(), "max: %2.0f", maxValue);

                minText.setText(min);
                maxText.setText(max);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.filter_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_reset) {
            Log.i("TAG", "reset filters");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
