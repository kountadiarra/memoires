package diarra.com.housingeasy;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

public class OffresActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ProgressDialog dialog;
    @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_main, menu);

            final MenuItem searchItem = menu.findItem(R.id.searchLocal);
            final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    //votre code ici

                    return false;
                }
                @Override
                public boolean onQueryTextChange(String s) {
                    return false;
                }
            });

            return true;
        }

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offres);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Veuillez patienter SVP");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
        @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

            loadJsonObj(Utils.URL_START+"/offres.php");

    }

    private void loadJsonObj(final String url) {
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        dialog.dismiss();
                        try {

                            if(response==null){

                                Toast.makeText(OffresActivity.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                            }
                            else
                                {
                                    LatLng latlng =null;

                                    for (int i = 0; i <response.length(); i++) {

                                        JSONObject jo = response.getJSONObject(i);
                                        String latitude =jo.getString("latitude");
                                        String libelle =jo.getString("libelle");
                                        String longitude =jo.getString("longitude");
                                        String description =jo.getString("prerequis");
                                        String montant =jo.getString("montant");
                                        String lieu =jo.getString("lieu");
                                       // String login =jo.getString("login");
                                        String contact =jo.getString("contact");
                                        int Id =jo.getInt("id_offre");

                                        latlng=new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));


                                        mMap.addMarker(new MarkerOptions().position(latlng).snippet(description+" "+Id+" "+lieu+" "+montant+" "+contact).icon(BitmapDescriptorFactory.defaultMarker()).title(libelle));

                                    }

                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 12));

                            }

                        } catch (Exception e) {
                            Toast.makeText(OffresActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);

    }

}