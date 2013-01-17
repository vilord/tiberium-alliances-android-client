package com.cnc.CnCTA;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.cnc.CnCTA.adapter.ServerAdapter;
import com.cnc.CnCTA.fragment.BaseFragment;
import com.cnc.CnCTA.fragment.BasesFragment;
import com.cnc.CnCTA.fragment.ServersFragment;
import com.cnc.game.Client;
import com.cnc.game.GameServer;
import com.cnc.model.Server;

import java.util.ArrayList;


public class ClientActivity extends Activity implements ServersFragment.ServersProvider {
    private ArrayList<Server> servers;
    private GameServer gameServer;
    private Client gameClient;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.game);

        servers = (ArrayList<Server>) getIntent().getSerializableExtra("servers");
        gameServer = new GameServer();
        gameClient = new Client(gameServer);
        final SharedPreferences sharedPref = getSharedPreferences(LoginActivity.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        gameServer.setHash(sharedPref.getString(LoginActivity.HASH_KEY, ""));

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main_container, new ServersFragment());
        ft.commit();
    }

    public void enterServer(Server server) {
        gameServer.selectServer(server);
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return ClientActivity.this.gameClient.openSession();
            }

            @Override
            protected void onPostExecute(Boolean isOpen) {
                if (isOpen) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.main_container, new BasesFragment());
                    ft.commit();
                } else {
                    Toast.makeText(getApplicationContext(), "Can't open server session.", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @Override
    public ArrayList<Server> getServers() {
        return servers;
    }
}