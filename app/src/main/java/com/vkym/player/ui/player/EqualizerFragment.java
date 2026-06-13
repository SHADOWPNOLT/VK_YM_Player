package com.vkym.player.ui.player;

import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.vkym.player.R;
import com.vkym.player.media.EqualizerManager;
import com.vkym.player.di.ServiceLocator;

public class EqualizerFragment extends DialogFragment {
    
    private EqualizerManager equalizerManager;
    private View rootView;
    
    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new android.app.Dialog(requireContext(), getTheme());
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_equalizer, container, false);
        
        equalizerManager = ServiceLocator.getInstance().getEqualizerManager();
        
        setupEqualizer();
        
        return rootView;
    }
    
    private void setupEqualizer() {
        if (equalizerManager == null || !equalizerManager.isEnabled()) {
            TextView statusText = rootView.findViewById(R.id.statusText);
            statusText.setText("Эквалайзер недоступен");
            return;
        }
        
        short numberOfBands = equalizerManager.getNumberOfBands();
        short[] range = equalizerManager.getBandLevelRange();
        
        LinearLayout bandsContainer = rootView.findViewById(R.id.bandsContainer);
        
        for (short i = 0; i < numberOfBands; i++) {
            View bandView = LayoutInflater.from(getContext())
                .inflate(R.layout.item_equalizer_band, bandsContainer, false);
            
            TextView freqText = bandView.findViewById(R.id.freqText);
            SeekBar seekBar = bandView.findViewById(R.id.bandSeekBar);
            
            int freq = equalizerManager.getCenterFreq(i);
            freqText.setText((freq / 1000) + " Hz");
            
            seekBar.setMax(range[1] - range[0]);
            int currentLevel = equalizerManager.getBandLevel(i) - range[0];
            seekBar.setProgress(currentLevel);
            
            final short band = i;
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        short level = (short) (range[0] + progress);
                        equalizerManager.setBandLevel(band, level);
                    }
                }
                
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}
                
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
            
            bandsContainer.addView(bandView);
        }
        
        // Кнопка сброса
        View resetButton = rootView.findViewById(R.id.resetButton);
        resetButton.setOnClickListener(v -> resetEqualizer());
    }
    
    private void resetEqualizer() {
        if (equalizerManager == null) return;
        
        short numberOfBands = equalizerManager.getNumberOfBands();
        for (short i = 0; i < numberOfBands; i++) {
            equalizerManager.setBandLevel(i, (short) 0);
        }
        
        // Обновление UI
        LinearLayout bandsContainer = rootView.findViewById(R.id.bandsContainer);
        for (int i = 0; i < bandsContainer.getChildCount(); i++) {
            View bandView = bandsContainer.getChildAt(i);
            SeekBar seekBar = bandView.findViewById(R.id.bandSeekBar);
            if (seekBar != null) {
                seekBar.setProgress(0);
            }
        }
    }
}
