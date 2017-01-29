package me.amald.youtubedownloader.Fragments;

import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.amald.youtubedownloader.Player.PlayerAdapter;
import me.amald.youtubedownloader.Player.SOng;
import me.amald.youtubedownloader.R;
import me.amald.youtubedownloader.Util.MLogger;

/**
 * Created by amald on 26/1/17.
 */

public class FragmentList extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerView;
    private PlayerAdapter adapter;
    private List<SOng> songList;


    private static final int UPDATE_FREQUENCY = 500;
    private static MediaPlayer player = new MediaPlayer();
    private static boolean isStarted = false;
    private static boolean is_back = false;


    private static TextView title_c;
    private static ImageView play_c;
    private static String cutent_track;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        title_c = (TextView) v.findViewById(R.id.play_c_titile);
        play_c = (ImageView) v.findViewById(R.id.play_c);
        play_c.setOnClickListener(this);

        songList = new ArrayList<>();


        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

        preparesong();
        updateControll();

        return v;
    }

    private void preparesong() {

        //  player = new MediaPlayer();

        player.setOnCompletionListener(onCompletion);
        player.setOnErrorListener(onError);


        Cursor cursor = getActivity().getContentResolver().query(MediaStore.Files.getContentUri("external"),
                null,
                MediaStore.Images.Media.DATA + " like ? ",
                new String[]{"%music/YVD2M%"},
                null);

        //Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);

        if (null != cursor) {


            while (cursor.moveToNext()) {


                String name = cursor.getString(
                        cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));

                String title = cursor.getString(
                        cursor.getColumnIndex(MediaStore.MediaColumns.TITLE));


                String data = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));

                if (name != null) {

                    SOng a = new SOng(name.replaceAll(".mp3",""), title, data);
                    songList.add(a);

                    adapter = new PlayerAdapter(getActivity(), songList);

                    recyclerView.setAdapter(adapter);

                    adapter.notifyDataSetChanged();


                }
            }


        }
    }


    private void updateControll() {


        if(player.isPlaying()){

            play_c.setImageResource(R.drawable.pause);
            title_c.setText(cutent_track);


        }

        if(FragmentList.isplay()){

            play_c.setImageResource(R.drawable.pause);
            title_c.setText(cutent_track);

        }else{

            play_c.setImageResource(R.drawable.play);
            if(cutent_track!=null){
                title_c.setText(cutent_track);

            }

        }

    }


    public static void updateBottomControll(String title) {

        cutent_track = title;

        title_c.setText(title);
        play_c.setImageResource(R.drawable.pause);


    }


    public static void startPlay(String file) {

        Log.i("Selected: ", file);





        play_c.setImageResource(R.drawable.pause);



//        selelctedFile.setText(file);
//        seekbar.setProgress(0);


        player.stop();
        player.reset();


        try {
            player.setDataSource(file);
            player.prepare();
            player.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        FragmentPlayer.seekBarUpdate(player.getDuration(),player);

        //seekbar.setMax(player.getDuration());
        // playButton.setImageResource(android.R.drawable.ic_media_pause);

        //updatePosition();

        isStarted = true;
    }

    public static boolean isplay() {

        Boolean state = false;

        if (player.isPlaying()) {

            state = true;

        }

        return state;

    }

    public static void isplaytwo() {

        if (player.isPlaying()) {

            player.stop();
            player.reset();

            MLogger.debug("playinggg", "yesss");

        }

    }


    public static void releasePlayer() {


        player.release();

        player = new MediaPlayer();


    }

    public static void stopPlay() {


        play_c.setImageResource(R.drawable.play);

        player.stop();
        player.reset();
//        playButton.setImageResource(android.R.drawable.ic_media_play);
//        handler.removeCallbacks(updatePositionRunnable);
//        seekbar.setProgress(0);

        isStarted = false;
    }


    private MediaPlayer.OnCompletionListener onCompletion = new MediaPlayer.OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mp) {
            stopPlay();
        }
    };

    private MediaPlayer.OnErrorListener onError = new MediaPlayer.OnErrorListener() {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {

            return false;
        }
    };


    @Override
    public void onResume() {
        super.onResume();

        //player.start();


        MLogger.debug("resume", "yess");


    }

    @Override
    public void onPause() {
        super.onPause();

        // player.pause();

        MLogger.debug("resumexx", "yess");
        is_back = true;


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.play_c:

                if (isStarted) {

                    stopPlay();
                    FragmentPlayer.updateControll();

                } else {

                    startPlay(cutent_track);
                    FragmentPlayer.updateControll();
                }


                break;

        }

    }
}
