package com.vkym.player.ui.player;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.vkym.player.R;
import com.vkym.player.media.EqualizerManager;

public class EqualizerFragment extends DialogFragment {
    
    private EqualizerManager equalizerManager;
    private LinearLayout bandsContainer;
    private TextView statusText;
    private Button resetButton;
    
    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new android.app.Dialog(requireContext(), getTheme());
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_equalizer, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        bandsContainer = view.findViewById(R.id.bandsContainer);
        statusText = view.findViewById(R.id.statusText);
        resetButton = view.findViewById(R.id.resetButton);
        
        equalizerManager = new EqualizerManager();
        // Для демонстрации создаём тестовый эквалайзер без аудиосессии
        setupEqualizerUI();
        
        resetButton.setOnClickListener(v -> resetEqualizer());
    }
    
    private void setupEqualizerUI() {
        // Для демонстрации создаём фиктивные полосы
        String[] frequencies = {"60 Hz", "230 Hz", "910 Hz", "3.6 kHz", "14 kHz"};
        
        for (int i = 0; i < frequencies.length; i++) {
            View bandView = LayoutInflater.from(getContext())
                .inflate(R.layout.item_equalizer_band, bandsContainer, false);
            
            TextView freqText = bandView.findViewById(R.id.freqText);
            SeekBar seekBar = bandView.findViewById(R.id.bandSeekBar);
            
            freqText.setText(frequencies[i]);
            seekBar.setMax(100);
            seekBar.setProgress(50);
            
            final int bandIndex = i;
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        statusText.setText(frequencies[bandIndex] + ": " + (progress - 50));
                    }
                }
                
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}
                
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
            
            bandsContainer.addView(bandView);
        }
        
        statusText.setText("Эквалайзер готов");
    }
    
    private void resetEqualizer() {
        for (int i = 0; i < bandsContainer.getChildCount(); i++) {
            View bandView = bandsContainer.getChildAt(i);
            SeekBar seekBar = bandView.findViewById(R.id.bandSeekBar);
            if (seekBar != null) {
                seekBar.setProgress(50);
            }
        }
        statusText.setText("Эквалайзер сброшен");
    }
}
