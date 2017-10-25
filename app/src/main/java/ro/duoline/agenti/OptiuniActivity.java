package ro.duoline.agenti;

import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

public class OptiuniActivity extends AppCompatActivity {
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_optiuni);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        if(SaveSharedPreference.getStyle(OptiuniActivity.this) == R.style.stilDark){
            radioGroup.check(R.id.radioDark);
        } else {
            radioGroup.check(R.id.radioLight);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                if(checkedId == R.id.radioDark) {
                    SaveSharedPreference.setStyle(OptiuniActivity.this, R.style.stilDark);
                } else if(checkedId == R.id.radioLight){
                    SaveSharedPreference.setStyle(OptiuniActivity.this, R.style.stilLight);
                }
            }
        });

    }
}
