package router.com.ssevening.www.androidrouter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import router.com.ssevening.www.mouproduct.ProductActivity;

public class MainActivity extends AppCompatActivity {

    private Button btn_to_product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_to_product = (Button) findViewById(R.id.btn_to_product);

        btn_to_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ProductActivity.class);
                startActivity(i);
            }
        });
    }
}
