package com.cs3270.robbie.cs3270fi;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class Metronome extends Fragment
{
    TextView txvTempo;
    TextView txvBeat;
    TextView txvStyle;
    TextView txvMemory;

    SeekBar skbTempo;
    SeekBar skbAccent;
    SeekBar skbBeat;
    SeekBar skbEighth;
    SeekBar skbSixteenth;
    SeekBar skbTriplet;
    SeekBar skbMVolume;

    Button btnMinus;
    Button btnPlus;
    Button btnStartStop;
    Button btnMute;
    Button btnTap;
    Button btnMemPrev;
    Button btnMemNext;
    Button btnMemSave;

    //making this as global as possible
    public int tempo;
    public int offset;                  //for setting a low limit with the progress bar
    public boolean externalChange;      //help with logic involving progress changing and buttons
    private static int[] memory = {1,2,3,4,5,6,7,8,9,10};
    private int memoryIndex;


    public Metronome()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_metronome, container, false);

        //wire up
        //textViews
        txvTempo = (TextView) rootView.findViewById(R.id.txvTempo);
        txvStyle = (TextView) rootView.findViewById(R.id.txvStyle);
        txvBeat = (TextView) rootView.findViewById(R.id.txvBeat);
        txvMemory = (TextView) rootView.findViewById(R.id.txvMemory);

        //seekbars
        skbTempo = (SeekBar) rootView.findViewById(R.id.skbTempo);
        skbAccent = (SeekBar) rootView.findViewById(R.id.skbAccent);
        skbBeat = (SeekBar) rootView.findViewById(R.id.skbBeat);
        skbEighth = (SeekBar) rootView.findViewById(R.id.skbEighth);
        skbSixteenth = (SeekBar) rootView.findViewById(R.id.skbSixteenth);
        skbTriplet = (SeekBar) rootView.findViewById(R.id.skbTriplet);
        skbMVolume = (SeekBar) rootView.findViewById(R.id.skbMVolume);

        //buttons
        btnMinus = (Button) rootView.findViewById(R.id.btnMinus);
        btnPlus = (Button) rootView.findViewById(R.id.btnPlus);
        btnStartStop = (Button) rootView.findViewById(R.id.btnStartStop);
        btnMute = (Button) rootView.findViewById(R.id.btnMute);
        btnTap = (Button) rootView.findViewById(R.id.btnTap);
        btnMemPrev = (Button) rootView.findViewById(R.id.btnMemPrev);
        btnMemNext = (Button) rootView.findViewById(R.id.btnMemNext);
        btnMemSave = (Button) rootView.findViewById(R.id.btnMemSave);

        View.OnClickListener beatListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                TextView txvClick = (TextView) v;
                MainActivity ma = (MainActivity) getActivity();

                if(txvClick.getId() == R.id.txvStyle)
                {
                    ma.loadBeatFragment();
                }

                if(txvClick.getId() == R.id.txvTempo)
                {
                    //load a change tempo fragment
                }
            }
        };

        txvBeat.setOnClickListener(beatListener);
        txvTempo.setOnClickListener((beatListener));
        //button listener
        View.OnClickListener btnListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Button btnClicked = (Button) v;

                if(btnClicked.getText().toString().equals("+"))
                {
                    incrementTempo();
                }

                if(btnClicked.getText().toString().equals("-"))
                {
                    decrementTempo();
                }

                if(btnClicked.getText().toString().equals("Start/Stop"))
                {
                    //play/stop metronome
                    MainActivity ma = (MainActivity) getActivity();
                    ma.computeBeatLength(tempo);
                    ma.beatNum = 1;
                    ma.playBeat();
                }

                if(btnClicked.getText().toString().equals("Mute"))
                {
                    //turn volume off
                    //ma.muteSounds();
                }

                if(btnClicked.getText().toString().equals("Tap"))
                {
                    //determines a new tempo based on how frequent the button is tapped.
                    //hidden timer starts after button is pressed.
                }

                if(btnClicked.getText().toString().equals("Prev"))
                {
                    previousMemory();
                }

                if(btnClicked.getText().toString().equals("Next"))
                {
                    nextMemory();
                }
            }
        };

        //add listener to buttons
        btnMinus.setOnClickListener(btnListener);
        btnPlus.setOnClickListener(btnListener);
        btnStartStop.setOnClickListener(btnListener);
        btnMute.setOnClickListener(btnListener);
        btnTap.setOnClickListener(btnListener);
        btnMemPrev.setOnClickListener(btnListener);
        btnMemNext.setOnClickListener(btnListener);
        btnMemSave.setOnClickListener(btnListener);

        //tempo seekbar listener
        skbTempo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if(externalChange)
                {
                    externalChange = false;
                    return;
                }

                tempo = progress + offset;
                txvTempo.setText(tempo + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

        //beat listener
        skbBeat.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            MainActivity ma = (MainActivity) getActivity();
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                double volume = progress;
                ma.setBeatVolume(volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

        //acccent listener
        skbAccent.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            MainActivity ma = (MainActivity) getActivity();
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                double volume = progress;
                ma.setAccentVolume(volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

        //eighth note listener
        skbEighth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            MainActivity ma = (MainActivity) getActivity();
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                double volume = progress;
                ma.setEighthVolume(volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

        //sixteenth note listener
        skbSixteenth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            MainActivity ma = (MainActivity) getActivity();
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                double volume = progress;
                ma.setSixteenthVolume(volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

        //triplet listener
        skbTriplet.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            MainActivity ma = (MainActivity) getActivity();
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                double volume = progress;
                ma.setTripletVolume(volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

        //master volume listener
        skbMVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            MainActivity ma = (MainActivity) getActivity();
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                double volume = progress;
                ma.setMasterVolume(volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

        MainActivity ma = (MainActivity) getActivity();
        //default values on load
        offset = 1; //so the app can never have a tempo of zero
        tempo = 119; //adds to 120: kind of considered the default march tempo.
        externalChange = false;
        txvTempo.setText(tempo + "");
        memoryIndex = 0;
        txvMemory.setText(memory[0] + "");
        skbTempo.setProgress(tempo);
        skbBeat.setProgress(100);
        skbAccent.setProgress(75);
        skbTriplet.setProgress(0);
        skbSixteenth.setProgress(0);
        skbEighth.setProgress(0);
        skbMVolume.setProgress(100);
        setLimits(ma.getMin(), ma.getMax());

        computeBeatLength(tempo);
        return rootView;
    }

    //sets new tempo limits
    public void setLimits(int low, int high)
    {
        externalChange = true;
        offset = low;
        tempo = high;
        skbTempo.setMax(high - offset);
        txvTempo.setText(tempo + "");
        skbTempo.setProgress(tempo);
    }

    //increments the current tempo by one
    public void incrementTempo()
    {
        externalChange = true;
        //if tempo is at the max already
        if(tempo >= (skbTempo.getMax() + offset))
        {
            //do nothing
            return;
        }
        else
        {
            tempo++;
            txvTempo.setText(tempo + "");
            skbTempo.setProgress(skbTempo.getProgress() + 1);
        }
    }

    //decreases the current tempo by one
    public void decrementTempo()
    {
        externalChange = true;
        //if tempo is at the min already
        if(tempo == offset)
        {
            //do nothing
            return;
        }
        else
        {
            tempo--;
            String temp = tempo + "";
            txvTempo.setText(temp);
            skbTempo.setProgress(skbTempo.getProgress() - 1);
        }
    }

    public void previousMemory()
    {
        if(memoryIndex == 0)
        {
            return;
        }
        memoryIndex--;
        String temp = memory[memoryIndex] + "";
        txvMemory.setText(temp);
    }

    public void nextMemory()
    {
        if(memoryIndex == 9)
        {
            return;
        }
        memoryIndex++;
        txvMemory.setText(memory[memoryIndex] + "");
    }

    public long getTempo()
    {
        return Long.parseLong(tempo + "");
    }

    public void computeBeatLength(long tempo)
    {
        MainActivity ma = (MainActivity) getActivity();
        ma.computeBeatLength(tempo);
    }
}
