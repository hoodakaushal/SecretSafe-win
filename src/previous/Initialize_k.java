//package previous;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.support.v7.app.ActionBarActivity;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.EditText;
//
//
//public class Initialize_k extends ActionBarActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_initialize_k);
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_initialize_k, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    public void onC(View view) {
//        SharedPreferences sharedPreferences = this.getSharedPreferences("UserPassPreferences", this.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        EditText editText = (EditText) findViewById(R.id.k_input);
//        int k = Integer.parseInt(editText.getText().toString());
//        //TODO Vaildate k
//        editor.putInt("k", k);
//        editor.commit();
//        Intent intent = new Intent(this, MainMenu.class);
//        startActivity(intent);
//    }
//}
