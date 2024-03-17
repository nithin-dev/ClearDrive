package com.example.cleardrive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cleardrive.databinding.ActivityChatBinding;

import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private boolean conversationEnded = false;
    private HashMap<String, String> botResponses;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initBotResponses();
        handler = new Handler();

        displayMessage("Hi, how can I help?", false);

        binding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void initBotResponses() {
        botResponses = new HashMap<>();
        botResponses.put("how to get started", "Click on GET STARTED button to view mechanics based on location");
        botResponses.put("how to do payment", "Payment can be done via online payment method after mechanics confirms the appointment");
        botResponses.put("hi", "Hi, how are you?");
        botResponses.put("hello", "Hi, how are you?");
        botResponses.put("what is your name?", "My name is ClearDrive");
        botResponses.put("bye", "Have a good day!");
        botResponses.put("thank you", "You're welcome!");
        botResponses.put("history", "View all the invoices from the history tab");
        botResponses.put("about us", "shows the application details ");
        botResponses.put("profile", "navigates to profile page ");
        botResponses.put("edit profile", "navigates to profile page and click on edit located on top of page");
        botResponses.put("how to edit profile?", "navigates to profile page and click on edit located on top of page");

    }

    private void sendMessage() {
        String userMessage = binding.userInput.getText().toString().trim();
        binding.userInput.setText("");

        if (!conversationEnded) {
            displayMessage(userMessage, true);

            // Show loading animation
            binding.loadingAnimation.setVisibility(View.VISIBLE);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (userMessage.equalsIgnoreCase("bye")) {
                        conversationEnded = true;
                        displayMessage("Have a good day!", false);
                    } else {
                        String botResponse = generateResponse(userMessage);
                        displayMessage(botResponse, false);
                    }
                    // Hide loading animation
                    binding.loadingAnimation.setVisibility(View.GONE);
                }
            }, 2000);
        }
    }

    private String generateResponse(String userInput) {
        String botResponse = "I'm sorry, I don't understand.";

        if (userInput == null) {
            return botResponse;
        }

        if (botResponses.containsKey(userInput.toLowerCase())) {
            botResponse = botResponses.get(userInput.toLowerCase());
        } else if (userInput.toLowerCase().contains("start")) {
            botResponse = botResponses.get("how to get started");
        } else if (userInput.toLowerCase().contains("how")) {
            botResponse = botResponses.get("Hi, how are you?");
        }
        else if (userInput.toLowerCase().contains("hello")) {
            botResponse = botResponses.get("Hi, how are you?");
        }
        else if (userInput.toLowerCase().startsWith("call me ")) {
            String username = userInput.substring("call me ".length());
            botResponse = "Hi, " + username;
        }


        return botResponse;
    }

    private void displayMessage(String message, boolean isUserMessage) {

        if (message == null) {
            return;
        }

        SpannableStringBuilder builder = new SpannableStringBuilder();
        Drawable iconDrawable;
        if (isUserMessage) {
            iconDrawable = getResources().getDrawable(R.drawable.profile);
        } else {
            iconDrawable = getResources().getDrawable(R.drawable.bot);
        }
        iconDrawable.setBounds(0, 0, iconDrawable.getIntrinsicWidth(), iconDrawable.getIntrinsicHeight());
        builder.append("   ");
        builder.setSpan(new ImageSpan(iconDrawable), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(" ");
        builder.append(message);

        TextView textView = new TextView(this);
        textView.setText(builder);

        if (isUserMessage) {
            textView.setBackgroundResource(R.drawable.user_message_background);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        } else {
            textView.setBackgroundResource(R.drawable.bot_message_background);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        }

        int margin = getResources().getDimensionPixelSize(R.dimen.message_margin);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(margin, margin, margin, margin);
        textView.setLayoutParams(layoutParams);

        binding.chatDisplay.addView(textView);
    }
}
