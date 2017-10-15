package com.cs3270.robbie.cs3270fi;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangeTempoFragment extends Fragment
{
    EditText edtMin;
    EditText edtMax;
    Button btnSaveTempo;
    Button btnCancelTempo;

    public ChangeTempoFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_change_tempo, container, false);

        edtMin = (EditText) rootview.findViewById(R.id.edtMin);
        edtMax = (EditText) rootview.findViewById(R.id.edtMax);
        btnCancelTempo = (Button) rootview.findViewById(R.id.btnCancelTempo);
        btnSaveTempo = (Button) rootview.findViewById(R.id.btnSaveTempo);

        //button listener
        View.OnClickListener btnListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Button btnClicked = (Button) v;

                if(btnClicked.getText().toString().equals("Save"))
                {
                    MainActivity ma = (MainActivity) getActivity();
                    int min;
                    int max;

                    try
                    {
                        min = Integer.parseInt(edtMin.getText().toString());
                        max = Integer.parseInt(edtMax.getText().toString());
                    }
                    catch(NumberFormatException e)
                    {
                        return;
                    }

                    if(min < 1 || min > 256 || min > max)
                    {
                        return;
                    }
                    else if(max < 1 || max > 256 || max < min)
                    {
                        return;
                    }
                    else
                    {
                        ma.changeTempoLimits(min,max);
                        ma.reloadMetronome();
                    }
                }

                if(btnClicked.getText().toString().equals("Cancel"))
                {
                    MainActivity ma = (MainActivity) getActivity();
                    ma.reloadMetronome();
                }
            }
        };

        //add listener to buttons
        btnSaveTempo.setOnClickListener(btnListener);
        btnCancelTempo.setOnClickListener(btnListener);

        return rootview;
    }
}
