//package previous;
//
//import android.app.DialogFragment;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.v7.app.ActionBarActivity;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import email.Email;
//import email.ReadMail;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//
//public class Decrypt extends ActionBarActivity implements SearchResultDialog.DialogListener {
//
//    List<List<Email>> emails = new ArrayList<>();
//    ArrayList<String> p = new ArrayList<>();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_decrypt);
//    }
//
//    //Handle dialog display
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (!p.isEmpty()) {
//            EditText editText = (EditText) findViewById(R.id.tag);
//            String tag = editText.getText().toString();
//            ProgressBar progressBar = (ProgressBar) findViewById(R.id.retrieveprogress);
//            progressBar.setVisibility(View.VISIBLE);
//            new rmail().execute(tag);
//        }
//    }
//
//    @Override
//    public void onSelection(String subject, int index) {
//        //Handle decryption here
//        System.out.println(emails.size());
//        ArrayList<String> parts = new ArrayList<>();
//        ArrayList<String> flags = new ArrayList<>();
//        for (int i = 0; i < emails.size(); i++) {
//            if (emails.get(i).get(index).subject.equals(subject)) {
//                parts.add(emails.get(i).get(index).body);
//                System.out.println(parts.get(parts.size() - 1));
//                flags.add("-s" + Integer.toString(i + 1));
//            } else {
//                for (int j = 0; i < emails.get(i).size(); j++) {
//                    if (emails.get(i).get(j).subject.equals(subject)) {
//                        parts.add(emails.get(i).get(index).body);
//                        flags.add("-s" + Integer.toString(i + 1));
//                    }
//                }
//            }
//        }
//        SharedPreferences sharedPreferences = Decrypt.this.getSharedPreferences("UserPassPreferences", Decrypt.this.MODE_PRIVATE);
//        int k = sharedPreferences.getInt("k", 0);
//        String text;
//        if (k == 0) {
//            text = SplitCombine.combine(parts);
//        } else {
//            text = SplitCombine.shamirCombine(parts, flags, k);
//        }
//        TextView textView = (TextView) findViewById(R.id.resultext);
//        textView.setText(text);
//        p.clear();
//        emails.clear();
//        ProgressBar progressBar = (ProgressBar) findViewById(R.id.retrieveprogress);
//        progressBar.setVisibility(View.INVISIBLE);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_decrypt, menu);
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
//    public void retrieve(View view) {
//        //TODO Validate input
//        Intent intent = new Intent(this, PassDialog.class);
//        startActivityForResult(intent, 1);
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1) {
//            p = data.getStringArrayListExtra("p");
//        }
//    }
//
//    public class rmail extends AsyncTask<String, String, String> {
//
//        @Override
//        protected String doInBackground(String... args) {
//            SharedPreferences sharedPreferences = Decrypt.this.getSharedPreferences("UserPassPreferences", Decrypt.this.MODE_PRIVATE);
//            Set<String> ids = sharedPreferences.getStringSet("ids", new HashSet<String>());
//            String[] id = ids.toArray(new String[ids.size()]);
//            System.out.println(ids.size());
//            List<Email> email = new ArrayList<>();
//            try {
//                for (int i = 0; i < ids.size(); i++) {
//
//                    System.out.println(id[i]);
//                    email = ReadMail.read(Email.getHost(2, id[i]), id[i], p.get(i), args[0]);
//                    emails.add(email);
//                    if (i == ids.size() - 1) {
//                        ArrayList<String> subjects = new ArrayList<>();
//                        for (Email e : email) {
//                            subjects.add(e.subject);
//                        }
//                        DialogFragment sr = SearchResultDialog.newInstance(subjects);
//                        sr.show(getFragmentManager(), "dialog");
//                    }
//                }
//            } catch (Exception e) {
//                Log.e("App", "Could not retrieve");
//            }
//            return null;
//        }
//    }
//}
