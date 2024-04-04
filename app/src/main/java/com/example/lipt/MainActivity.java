/**
 * Luis Hernandez, Guillermo Zendejas
 * April 2, 2024
 * MainActivity.java, this describes our application's login page view
 */

package com.example.lipt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.example.lipt.Database.Player;
import com.example.lipt.Database.PokeRepository;
import com.example.lipt.databinding.ActivityMainBinding;
import com.example.lipt.Utils.InputValidator;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private PokeRepository login_repo;
    private LiveData<List<Player>> allCurrentPlayers;

    public static final String TAG = "LGH_DEBUG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //establishing repo, grabbing list of players
        login_repo = new PokeRepository((Application) getApplicationContext());
        allCurrentPlayers = login_repo.getAllPlayers();

        //instantiating an interface of onClickListener for login button
        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //user inputted username and password
                String temp_username = binding.loginUsernameTextEdit.getText().toString();
                String temp_password = binding.loginPasswordTextEdit.getText().toString();

                //if inputted username and password are viable inputs
                if(InputValidator.viableUsername(temp_username) && InputValidator.viablePassword(temp_password)) {

                    //observer established to authenticate credentials
                    allCurrentPlayers.observe(MainActivity.this, new Observer<List<Player>>() {
                        @Override
                        public void onChanged(List<Player> players) {
                            for(Player player : players) {
                                //checking whether username is in the database
                                if(player.getUsername().equalsIgnoreCase(temp_username)) {
                                    //checking whether the inputted password matches
                                    if(player.getPassword().equals(temp_password)) {
                                        //officially logging into game
                                        int validated_ID = player.getUserID();
                                        Toast.makeText(MainActivity.this, "LOGIN ID: " + validated_ID, Toast.LENGTH_SHORT).show();
                                        Intent intent = MenuActivity.mainMenuFactory(getApplicationContext(), validated_ID);
                                        startActivity(intent);
                                    }
                                }
                            }
                        }
                    });
                }

                else {
                    Toast.makeText(MainActivity.this, "Invalid input!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //instantiating an interface of onClickListener for registration1 button
        binding.registrationButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = registrationActivity.registrationToMainFactory(getApplicationContext());
                startActivity(intent);
            }
        });


    }

    //Main to registration activity factory
    public static Intent mainToRegistrationFactory(Context context) {
        return new Intent(context, MainActivity.class);
    }


}