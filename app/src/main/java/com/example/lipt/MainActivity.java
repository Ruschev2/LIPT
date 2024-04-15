/**
 * Luis Hernandez, Guillermo Zendejas
 * April 2, 2024
 * MainActivity.java, this describes our application's login page view
 */

package com.example.lipt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.lipt.Database.Player;
import com.example.lipt.Database.PlayerPrizeCrossRef;
import com.example.lipt.Database.PlayerPrizeCrossRefRepository;
import com.example.lipt.Database.PlayerRepository;
import com.example.lipt.Database.Pokemon;
import com.example.lipt.Database.PokemonRepository;
import com.example.lipt.Database.Prize;
import com.example.lipt.Database.PrizeRepository;
import com.example.lipt.Utils.PokemonInfo;
import com.example.lipt.databinding.ActivityMainBinding;
import com.example.lipt.Utils.InputValidator;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private PlayerRepository login_repo;
    private LiveData<List<Player>> allCurrentPlayers;
    private PokemonRepository poke_repo;
    private PrizeRepository prize_repo;
    private LiveData<List<Prize>> allPrizes;
    public static final String TAG = "LGH_DEBUG";
    private PlayerPrizeCrossRefRepository playerprizerepo;
    Executor executor = Executors.newSingleThreadExecutor();
    Executor executor_2 = Executors.newSingleThreadExecutor();
    boolean loginProcessed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //creating room databases
        initializeRooms();

        //instantiating an interface of onClickListener for login button
        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //user inputted username and password
                String temp_username = binding.loginUsernameTextEdit.getText().toString();
                String temp_password = binding.loginPasswordTextEdit.getText().toString();

                //if inputted username and password are viable inputs
                if (InputValidator.viableUsername(temp_username) && InputValidator.viablePassword(temp_password)) {

                    loginProcessed = false;
                    // Observer established to authenticate credentials
                    allCurrentPlayers.observe(MainActivity.this, new Observer<List<Player>>() {
                        @Override
                        public void onChanged(List<Player> players) {
                            if (!loginProcessed) {
                                for (Player player : players) {
                                    // Checking whether username is in the database
                                    if (player.getUsername().equalsIgnoreCase(temp_username)) {
                                        // Checking whether the inputted password matches
                                        if (player.getPassword().equals(temp_password)) {
                                            // Officially logging into game
                                            int validated_ID = player.getUserID();
                                            Toast.makeText(MainActivity.this, "LOGIN ID: " + validated_ID, Toast.LENGTH_SHORT).show();
                                            Intent intent = MenuActivity.mainMenuFactory(getApplicationContext(), validated_ID);
                                            startActivity(intent);
                                            //login completion
                                            loginProcessed = true;
                                            return;
                                        } else {
                                            Toast.makeText(MainActivity.this, "Invalid Password. Try again.", Toast.LENGTH_SHORT).show();
                                            //login completion
                                            loginProcessed = true;
                                            return;
                                        }
                                    }
                                }
                                // If the loop completes without finding a matching username
                                Toast.makeText(MainActivity.this, "Invalid Username! Try again.", Toast.LENGTH_SHORT).show();
                                // Set the flag to true to indicate login completion
                                loginProcessed = true;
                            }
                        }
                    });
                }
            }
        });


        //instantiating an interface of onClickListener for registration1 button
        binding.registrationButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = registrationActivity.registrationFactory(getApplicationContext());
                startActivity(intent);
            }
        });

        //creating notification channel
        NotificationChannel channel = new NotificationChannel(
                "POKEMON_ID",
                "POKEMON_CHANNEL",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager n_Manager = getSystemService(NotificationManager.class);
        n_Manager.createNotificationChannel(channel);

        Intent n_intent = MainActivity.mainFactory(MainActivity.this);
        n_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent p_Intent = PendingIntent.getActivity(this, 0, n_intent, PendingIntent.FLAG_IMMUTABLE);

        //notificationCompatBuilder object
        NotificationCompat.Builder b_obj = new NotificationCompat.Builder(this, "POKEMON_ID")
                .setSmallIcon(R.drawable.small_icon_pokeball)
                .setContentTitle("Hey, Pokemon Trainer!")
                .setContentText("Log in to play for prizes!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(p_Intent)
                .setAutoCancel(true);

        //Pushing notification
        NotificationManagerCompat n_ManagerCompat = NotificationManagerCompat.from(this);
        int n_ID = 42;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        n_ManagerCompat.notify(n_ID, b_obj.build());

    }

    /**
     * This method describes the main activity factory
     * @param context application context
     * @return a new intent to begin main activity
     */
    public static Intent mainFactory(Context context) {
        return new Intent(context, MainActivity.class);
    }

    /**
     * This method instantiates the room database
     */
    private void initializeRooms() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                //populating pokedex
                poke_repo = new PokemonRepository((Application) getApplicationContext());
                for (int i = 1; i < 494; i++) {
                    Pokemon pokemon = new Pokemon(i, PokemonInfo.getPokemonName(i),
                            getResources().getIdentifier("pokemon" + i, "drawable", getPackageName()),
                            getResources().getIdentifier("sound" + i, "raw", getPackageName()));
                    poke_repo.insert(pokemon);
                }

                //populating prize table
                prize_repo = new PrizeRepository((Application) getApplicationContext());
                for (int i = 1; i < 21; i++) {
                    Prize prize = new Prize(i, PokemonInfo.getPrizeName(i), getResources().getIdentifier("prize" + i, "drawable", getPackageName()));
                    prize_repo.insert(prize);
                }
                allPrizes = prize_repo.getAllPrizes();

                //establishing repo, grabbing list of players
                login_repo = new PlayerRepository((Application) getApplicationContext());
                allCurrentPlayers = login_repo.getAllPlayers();

                //adding all prizes to admin1 account
                playerprizerepo = new PlayerPrizeCrossRefRepository((Application) getApplicationContext());
                for(int i = 1; i < 21; i++) {
                    PlayerPrizeCrossRef obj = new PlayerPrizeCrossRef(1, i);
                    playerprizerepo.insert(obj);
                }


                //populating list in pokemoninfo (temp)
                for (int i = 1; i < 494; i++) {
                    Pokemon pokemon = new Pokemon(i, PokemonInfo.getPokemonName(i),
                            getResources().getIdentifier("pokemon" + i, "drawable", getPackageName()),
                            getResources().getIdentifier("sound" + i, "raw", getPackageName()));
                    PokemonInfo.full_pokemon_list.add(pokemon);
                }

                //populating list in pokemon info for prizes
                for (int i = 1; i < 21; i++) {
                    Prize prize = new Prize(i, PokemonInfo.getPrizeName(i), getResources().getIdentifier("prize" + i, "drawable", getPackageName()));
                    PokemonInfo.full_prize_list.add(prize);
                }
            }
        });
    }


}