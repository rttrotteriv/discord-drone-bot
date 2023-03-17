package com.cheesetron;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/**
 * Handles events from the audio player.
 */
public class TrackScheduler extends AudioEventAdapter {

    private static final Logger audioLogger = LogManager.getLogger("com.cheesetron.dronebot.audio");

    ArrayList<AudioTrack> queue = new ArrayList<>();
    boolean notPlaying = true;
    boolean repeat = false;

    /**
     * @param player    Audio player
     * @param track     Audio track that ended
     * @param endReason The reason why the track stopped playing
     */
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        audioLogger.debug("Track ended");
        if (repeat) {
            player.playTrack(track.makeClone());
        } else if (!queue.isEmpty() && endReason.equals(AudioTrackEndReason.FINISHED)) {
            player.playTrack(queue.remove(0));
        } else {
            notPlaying = true;
        }
    }

    public void queue(AudioPlayer player, AudioTrack track) {
        if (queue.isEmpty() && notPlaying) {
            player.playTrack(track);
            audioLogger.debug(track.getInfo().uri);
            notPlaying = false;
        } else {
            queue.add(track);
        }
    }

    public void clearQueue() {
        queue.clear();
    }

    /**
     * Skips current track.
     * @param player player to skip
     * @return true on success
     */
    public boolean skip(AudioPlayer player) {
        if (!queue.isEmpty()) {
            player.playTrack(queue.remove(0));
            return true;
        }
        return false;
    }
}
