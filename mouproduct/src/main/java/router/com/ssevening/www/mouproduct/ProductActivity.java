package router.com.ssevening.www.mouproduct;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import router.com.ssevening.www.moubase.ReflectNav;
import router.com.ssevening.www.moubase.SchemaNav;

/**
 * Created by Pan on 2017/6/12.
 */

public class ProductActivity extends AppCompatActivity {

    private Button btn_to_shopcart_reflect;
    private Button btn_to_shopcart_scheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        btn_to_shopcart_reflect = (Button) findViewById(R.id.btn_to_shopcart_reflect);
        btn_to_shopcart_reflect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("key", "Reflect value");
                ReflectNav.from(ProductActivity.this).setClass("router.com.ssevening.www.moushopcart.ShopcartActivity").withExtras(bundle).fire();
            }
        });

        btn_to_shopcart_scheme = (Button) findViewById(R.id.btn_to_shopcart_scheme);
        btn_to_shopcart_scheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("key", "Schema value");
                SchemaNav.from(ProductActivity.this).withExtras(bundle).fire("https://www.ssevening.com/shopcart.html");
            }
        });
    }


}
