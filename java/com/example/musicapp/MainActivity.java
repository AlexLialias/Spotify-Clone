
package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private LinearLayout linearLayout;
    private ArtistAdapter adp;
    private  ConsumerImpl p = new ConsumerImpl();
    private Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        linearLayout = findViewById(R.id.list);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);





        btn = findViewById(R.id.offline_list_b);







        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, Song_OfflineList.class);
                startActivity(myIntent);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.my_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.search_icon);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search artist!");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                p.ResetArtists();
               //r.request=query;
               p.tosend=query;

               linearLayout = findViewById(R.id.list);
               p.init();

                ArrayList<String> dummy = new ArrayList();
                if(((LinearLayout) linearLayout).getChildCount() > 0){
                    ((LinearLayout) linearLayout).removeAllViews();

                    linearLayout.addView(btn);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent myIntent = new Intent(MainActivity.this, Song_OfflineList.class);
                            startActivity(myIntent);
                        }
                    });
                }

                dummy.clear();
                dummy=p.Artist;
                adp = new ArtistAdapter(getApplicationContext(), dummy);


                for(int i = 0;i<dummy.size();i++){
                    View item = adp.getView(i,null,null);
                    linearLayout.addView(item);
                    p.ResetSongs();
                    item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent activityS = new Intent(MainActivity.this,SongList.class);
                            TextView tx = v.findViewById(R.id.Name);
                            p.tosend= (String) tx.getText();
                            startActivity(activityS);
                        }
                    });
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }







}
