package router.com.ssevening.www.moushopcart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import router.com.ssevening.www.moubase.ReflectNav;
import router.com.ssevening.www.moubase.SchemaNav;

/**
 * Created by Pan on 2017/6/12.
 */

public class ShopcartActivity extends AppCompatActivity {


    private Button btn_back_to_main_reflect;
    private Button btn_back_to_main_scheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopcart);
        btn_back_to_main_reflect = (Button) findViewById(R.id.btn_back_to_main_reflect);
        btn_back_to_main_reflect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReflectNav.from(ShopcartActivity.this).setClass("router.com.ssevening.www.androidrouter.MainActivity").fire();
            }
        });
        btn_back_to_main_scheme = (Button) findViewById(R.id.btn_back_to_main_scheme);
        btn_back_to_main_scheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SchemaNav.from(ShopcartActivity.this).fire("https://www.ssevening.com/Index.html");
            }
        });
        String key = getIntent().getStringExtra("key");
        Toast.makeText(this, "获取到值:" + key, Toast.LENGTH_SHORT).show();
    }
}