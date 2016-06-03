package com.tugasp4json;

import com.tugasp4json.R;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MainActivity extends ListActivity {

    private ProgressDialog pDialog;

    // URL untuk mengambil data kontak JSON
    private static String url = "http://jsonplaceholder.typicode.com/posts";

    //Nama-nama Node pada JSON
    private static final String TAG_KONTAK = "kontak";
    private static final String TAG_ID = "id";
    private static final String TAG_NAMA = "nama";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_HP = "hp";

    // kontak JSONArray
    JSONArray kontak = null;

    // Hashmap for ListView
    ArrayList<HashMap<String, String>> contactList;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      contactList = new ArrayList<HashMap<String, String>>();
      ListView lv = getListView();
            
      // Action klik pada Listview
      lv.setOnItemClickListener(new OnItemClickListener() {
      
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                	
           // Mengambil data dari ListView yang dipilih
           String nama = ((TextView) view.findViewById(R.id.nama)).getText().toString();
           String email = ((TextView) view.findViewById(R.id.email)).getText().toString();
           String hp = ((TextView) view.findViewById(R.id.hp)).getText().toString();
                    
           // Memulai memanggil ke class KontakActivity dengan beberapa data
           Intent in = new Intent(getApplicationContext(),AkunActivity.class);
           in.putExtra(TAG_NAMA, nama);
           in.putExtra(TAG_EMAIL, email);
           in.putExtra(TAG_HP, hp);
           startActivity(in);
        }
      });
    
      // Menggunakan async task untuk "ngeload" data JSON
      new GetContacts().execute();
    }
    
    private class GetContacts extends AsyncTask<Void, Void, Void> {
      @Override
      protected void onPreExecute() {
    	super.onPreExecute();
    	pDialog = new ProgressDialog(MainActivity.this);
    	pDialog.setMessage("Tolong Tunggu...");
    	pDialog.setCancelable(false);
    	pDialog.show();
      }

      @Override
      protected Void doInBackground(Void... arg0) {
    	// Creating service handler class instance
    	ServiceHandler sh = new ServiceHandler();
    	   
    	// Making a request to url and getting response
    	String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
    	Log.d("Response: ", "> " + jsonStr);
    	if (jsonStr != null) {
    	  try {
    	    JSONObject jsonObj = new JSONObject(jsonStr);
    	    
    	    // Getting JSON Array node
    	    kontak = jsonObj.getJSONArray(TAG_KONTAK);
    	    
    	    // looping through All Contacts
    	    for (int i = 0; i < kontak.length(); i++) {
    	      JSONObject c = kontak.getJSONObject(i);
    	      String id = c.getString(TAG_ID);
    	      String name = c.getString(TAG_NAMA);
    	      String email = c.getString(TAG_EMAIL);
    	      String hp = c.getString(TAG_HP);
    	      
    	      // tmp hashmap for single contact
    	      HashMap<String, String> contact = new HashMap<String, String>();
    	      
    	      // adding each child node to HashMap key => value
    	      contact.put(TAG_ID, id);
    	      contact.put(TAG_NAMA, name);
    	      contact.put(TAG_EMAIL, email);
    	      contact.put(TAG_HP, hp);
    	      
    	      // adding contact to contact list
    	      contactList.add(contact);
    	    }
    	   } 
    	  catch (JSONException e) {
    	     e.printStackTrace();
    	  }
    	} 
    	else {
    	  Log.e("ServiceHandler", "Couldn't get any data from the url");
    	}
    	return null;
      }
    	
      
      @Override
      protected void onPostExecute(Void result) {
    	 super.onPostExecute(result);
    	
    	 // Dismiss the progress dialog
    	 if (pDialog.isShowing())
    	 pDialog.dismiss();
    	 
    	 /**
    	  * Updating parsed JSON data into ListView
    	 * */
    	 ListAdapter adapter = new SimpleAdapter(MainActivity.this, contactList, R.layout.activity_list_akun, new String[] {TAG_NAMA, TAG_EMAIL,TAG_HP }, new int[] { R.id.nama,R.id.email, R.id.hp });
    	 
    	 setListAdapter(adapter);
      }
    }
}