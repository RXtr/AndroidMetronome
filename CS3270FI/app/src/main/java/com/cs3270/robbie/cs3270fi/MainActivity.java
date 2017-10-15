package com.cs3270.robbie.cs3270fi;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
{
    MediaPlayer beat;
    MediaPlayer accent;
    MediaPlayer eighth;
    MediaPlayer sixteenth;
    MediaPlayer triplet;
    String beatStyle;
    int beatNum;
    public int min;
    public int max;
    double masterVolume;
    Thread thread;
    boolean on;
    long beatLength;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null)
        {
            getFragmentManager().beginTransaction()
                    .add(R.id.mainContainer, new Metronome(), "MetronomeF")
                    .commit();
        }

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        //load sound files
        accent = MediaPlayer.create(this, R.raw.accent);
        beat = MediaPlayer.create(this, R.raw.beat);
        eighth = MediaPlayer.create(this, R.raw.eighth);
        sixteenth = MediaPlayer.create(this, R.raw.sixteenth);
        triplet = MediaPlayer.create(this, R.raw.triplet);

        Metronome met = (Metronome) getFragmentManager()
                .findFragmentByTag("MetronomeF");

        //defaults: remove for loading the first memory setting
        beatNum = 1;
        min = 1;
        max = 256;
        beatStyle = "1";
        masterVolume = 100;
        setAccentVolume(0.0);
        setBeatVolume(0.0);
        setEighthVolume(0.0);
        setSixteenthVolume(0.0);
        setTripletVolume(0.0);
        on = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //change tempo limits button
        if (id == R.id.changeTempoLimits)
        {
            on = false;

            getFragmentManager().beginTransaction()
                    .replace(R.id.mainContainer, new ChangeTempoFragment(), "TempoChangeF")
                    .commit();
            return true;
        }

        //change beat style.
        if (id == R.id.changeStyle)
        {
            on = false;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void changeTempoLimits(int min, int max)
    {
        Metronome met = (Metronome) getFragmentManager()
                .findFragmentByTag("MetronomeF");

        this.min = min;
        this.max = max;
    }

    public void reloadMetronome()
    {
        getFragmentManager().beginTransaction()
                .replace(R.id.mainContainer, new Metronome(), "MetronomeF")
                .commit();
    }

    public void loadBeatFragment()
    {
        getFragmentManager().beginTransaction()
                .replace(R.id.mainContainer, new ChangeBeatFragment(), "BeatF")
                .commit();
    }

    //play the metronome
    public void playBeat()
    {
        //is the met running?
        if(on)
        {
            //turn it off
            on = false;
            try
            {
                thread.join();
            }
            catch (Exception e)
            {

            }
        }
        //not on
        else
        {
            //turn the met on
            on = true;
            thread = new Thread()
            {
                @Override
                public void run()
                {
                    try
                    {
                        Metronome met = (Metronome) getFragmentManager()
                                .findFragmentByTag("MetronomeF");

                        while(on)
                        {
                            //put everything in the mutex lock just to be safe.
                            synchronized(this)
                            {
                                //broke the beat down into twelve parts (4 * 3 [lowest conflicting subdivisions])
                                //all 12 beatLength pieces represent one entire beat.
                                if(beatNum == 1)
                                {
                                    accent.start();         //an accent at the beginning if it's count 1
                                }
                                beat.start();           //a beat every count
                                triplet.start();        //a triplet partial every count
                                wait(beatLength);
                                wait(beatLength);
                                wait(beatLength);
                                sixteenth.start();      //a sixteenth note every quarter beat
                                wait(beatLength);
                                triplet.start();        //a triplet partial every third-beat
                                wait(beatLength);
                                wait(beatLength);
                                eighth.start();         //an eighth note every half-beat
                                wait(beatLength);
                                wait(beatLength);
                                triplet.start();        //triplet
                                wait(beatLength);
                                sixteenth.start();      //sixteenth
                                wait(beatLength);
                                wait(beatLength);
                                wait(beatLength);
                                computeBeatLength(met.getTempo());      //get the new tempo after
                                nextBeatNum();                          //find the next beat
                            }
                        }
                    }

                    catch(InterruptedException ex)
                    {

                    }
                }
            };
            thread.setPriority(Thread.MAX_PRIORITY);
            thread.start();
        }
    }

    //sets beat volume
    public void setBeatVolume(double volume)
    {
        volume = volume * masterVolume / 10000;
        float vol;
        vol = Float.parseFloat(volume + "");
        beat.setVolume(vol, vol);
    }

    //sets accent volume
    public void setAccentVolume(double volume)
    {
        volume = volume * masterVolume / 10000;
        float vol;
        vol = Float.parseFloat(volume + "");
        accent.setVolume(vol, vol);
    }

    //sets eighth note partial volume
    public void setEighthVolume(double volume)
    {
        volume = volume * masterVolume / 10000;
        float vol;
        vol = Float.parseFloat(volume + "");
        eighth.setVolume(vol, vol);
    }

    //sets sixteenth note partial volume
    public void setSixteenthVolume(double volume)
    {
        volume = volume * masterVolume / 10000;
        float vol;
        vol = Float.parseFloat(volume + "");
        sixteenth.setVolume(vol, vol);
    }

    //sets triplet partial volume
    public void setTripletVolume(double volume)
    {
        volume = volume * masterVolume / 10000;
        float vol;
        vol = Float.parseFloat(volume + "");
        triplet.setVolume(vol, vol);
    }

    //sets master volume
    public void setMasterVolume(double volume)
    {
        masterVolume = volume;
        Metronome met = (Metronome)getFragmentManager().findFragmentByTag("MetronomeF");
        setAccentVolume(met.skbAccent.getProgress());
        setBeatVolume(met.skbBeat.getProgress());
        setEighthVolume(met.skbEighth.getProgress());
        setSixteenthVolume(met.skbSixteenth.getProgress());
        setTripletVolume(met.skbTriplet.getProgress());
    }

    //takes the current beat number and determines the next beat
    public void nextBeatNum()
    {
        //need performance. Switch statements are faster than if-else.
        switch(beatStyle)
        {
            case"1":
                break;
            case"2":
                if(beatNum < 2)
                {
                    beatNum++;
                }
                else
                {
                    beatNum = 1;
                }
                break;
            case"3":
                if(beatNum < 3)
                {
                    beatNum++;
                }
                else
                {
                    beatNum = 1;
                }
                break;
            case"4":
                if(beatNum < 4)
                {
                    beatNum++;
                }
                else
                {
                    beatNum = 1;
                }
                break;
            case"5":
                if(beatNum < 5)
                {
                    beatNum++;
                }
                else
                {
                    beatNum = 1;
                }
                break;
            case"6":
                if(beatNum < 6)
                {
                    beatNum++;
                }
                else
                {
                    beatNum = 1;
                }
                break;
            case"7":
                if(beatNum < 7)
                {
                    beatNum++;
                }
                else
                {
                    beatNum = 1;
                }
                break;
            case"8":
                if(beatNum < 9)
                {
                    beatNum++;
                }
                else
                {
                    beatNum = 1;
                }
                break;
            case"9":
                if(beatNum < 9)
                {
                    beatNum++;
                }
                else
                {
                    beatNum = 1;
                }
                break;
        }
    }

    //get the beat and divide it into twelve pieces
    public void computeBeatLength(long bpm)
    {
        beatLength = (60000 / bpm) / 12;
    }

    //returns the tempo min
    public int getMin()
    {
        return min;
    }

    //returns the tempo max
    public int getMax()
    {
        return max;
    }
}
