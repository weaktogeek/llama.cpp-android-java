package com.weaktogeek.offlineai.chat;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.weaktogeek.offlineai.Llama_Wrapper;
import com.weaktogeek.offlineai.R;
import com.weaktogeek.offlineai.databinding.ActivityChatBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private Llama_Wrapper llmw = new Llama_Wrapper();
    private ChatAdapter adapter;
    private final List<ChatMessage> messagesBackingList = new ArrayList<>();
    private ActivityChatBinding binding; // Make binding a class member

    private String strAbsolutePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize binding
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get the intent and bundle
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            strAbsolutePath = bundle.getString("path");
        }

        // Load the Llama model
        try {
           // strAbsolutePath = "/data/user/0/com.weaktogeek.offlineai/cache/Llama-3.2-1B-Instruct-Q6_K.gguf";
            llmw.load(strAbsolutePath);
            Toast.makeText(this, "Model loaded successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Toast.makeText(this, "Load error: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }

        // Setup RecyclerView
        adapter = new ChatAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // So new messages appear at the bottom
        binding.rvMessages.setLayoutManager(layoutManager);
        binding.rvMessages.setAdapter(adapter);

        // Auto-scroll when new items are added
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                binding.rvMessages.post(() -> {
                    int count = adapter.getItemCount();
                    if (count > 0) {
                        binding.rvMessages.smoothScrollToPosition(count - 1);
                    }
                });
            }
        });

        loadInitialMessages();

        // Setup Send button and keyboard action
        binding.btnSend.setOnClickListener(v -> sendMessageFromInput());
        binding.etMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessageFromInput();
                return true;
            }
            return false;
        });


    }


    private String preparePath() throws IOException {
        // This function correctly copies the model from raw resources to cache
        InputStream inputStream = getResources().openRawResource(R.raw.qwen2515b);
        File tempFile = File.createTempFile("model", ".gguf", getCacheDir());
        try (OutputStream out = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        }
        inputStream.close();
        return tempFile.getAbsolutePath();
    }


    private void loadInitialMessages() {
        messagesBackingList.add(new ChatMessage("Welcome to Offline.AI", false));
        updateList();
    }

    private void sendMessageFromInput() {
        String userText = binding.etMessage.getText().toString().trim();
        if (userText.isEmpty()) return;

        hideKeyboard();
        // 1. Add user message to the UI
        messagesBackingList.add(new ChatMessage(userText, true));
        updateList();
        binding.etMessage.setText("");

        // 2. Add a temporary "typing..." message for the bot
        final int botMessageIndex = messagesBackingList.size();
        messagesBackingList.add(new ChatMessage("...", false));
        updateList();

        // 3. Send the message to Llama and get the response
        llmw.send(userText, new Llama_Wrapper.MessageHandler() {
            private final StringBuilder fullResponse = new StringBuilder();

            @Override
            public void h(@NonNull String msgChunk) {
                // This is called for each piece of the response
                fullResponse.append(msgChunk);
                runOnUiThread(() -> {
                    // Update the "typing..." message with the new content
                    ChatMessage updatedBotMessage = new ChatMessage(fullResponse.toString(), false);
                    messagesBackingList.set(botMessageIndex, updatedBotMessage); // Replace the item at the index
                    adapter.notifyItemChanged(botMessageIndex);
                    updateList();


                });
            }
        });
    }

    private void updateList() {
        // Submit an immutable copy for the ListAdapter to calculate diffs
        adapter.submitList(new ArrayList<>(messagesBackingList));
    }

    private void hideKeyboard() {
        ViewCompat.getWindowInsetsController(binding.getRoot())
                .hide(WindowInsetsCompat.Type.ime());
    }
}
