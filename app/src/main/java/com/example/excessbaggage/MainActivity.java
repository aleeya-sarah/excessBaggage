package com.example.excessbaggage;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final double ECONOMY_LIMIT_KG = 20.0;
    private static final double BUSINESS_LIMIT_KG = 40.0;
    private static final double SERVICE_TAX = 0.08;

    private TextInputLayout tilWeight, tilRate;
    private TextInputEditText etWeight, etRate;
    private RadioGroup rgClass;
    private View cardResult;
    private TextView tvExcess, tvCharge, tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tilWeight = findViewById(R.id.tilWeight);
        tilRate = findViewById(R.id.tilRate);
        etWeight = findViewById(R.id.etWeight);
        etRate = findViewById(R.id.etRate);
        rgClass = findViewById(R.id.rgClass);
        cardResult = findViewById(R.id.cardResult);
        tvExcess = findViewById(R.id.tvExcess);
        tvCharge = findViewById(R.id.tvCharge);
        tvTotal = findViewById(R.id.tvTotal);

        findViewById(R.id.btnCalculate).setOnClickListener(v -> calculate());
        findViewById(R.id.btnReset).setOnClickListener(v -> reset());
    }

    private void calculate() {
        tilWeight.setError(null);
        tilRate.setError(null);

        Double weight = parse(etWeight);
        Double rate = parse(etRate);
        boolean ok = true;

        if (weight == null) {
            tilWeight.setError(getString(R.string.error_weight_empty));
            ok = false;
        } else if (weight < 0) {
            tilWeight.setError(getString(R.string.error_negative));
            ok = false;
        }
        if (rate == null) {
            tilRate.setError(getString(R.string.error_rate_empty));
            ok = false;
        } else if (rate < 0) {
            tilRate.setError(getString(R.string.error_negative));
            ok = false;
        }
        if (!ok) {
            cardResult.setVisibility(View.GONE);
            return;
        }

        boolean business = rgClass.getCheckedRadioButtonId() == R.id.rbBusiness;
        double limit = business ? BUSINESS_LIMIT_KG : ECONOMY_LIMIT_KG;

        double excessWeight = Math.max(0, weight - limit);   // never negative
        double charge = excessWeight * rate;
        double total = charge + (SERVICE_TAX * charge);

        tvExcess.setText(String.format(Locale.US, "%.2f kg", excessWeight));
        tvCharge.setText(String.format(Locale.US, "RM %.2f", charge));
        tvTotal.setText(String.format(Locale.US, "RM %.2f", total));
        cardResult.setVisibility(View.VISIBLE);
    }

    private Double parse(TextInputEditText field) {
        String s = field.getText() == null ? "" : field.getText().toString().trim();
        if (s.isEmpty()) return null;
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void reset() {
        etWeight.setText("");
        etRate.setText("");
        tilWeight.setError(null);
        tilRate.setError(null);
        rgClass.check(R.id.rbEconomy);
        cardResult.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share) {
            Intent send = new Intent(Intent.ACTION_SEND);
            send.setType("text/plain");
            send.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            send.putExtra(Intent.EXTRA_TEXT,
                    getString(R.string.share_message, getString(R.string.app_url)));
            startActivity(Intent.createChooser(send, getString(R.string.share_via)));
            return true;
        } else if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
