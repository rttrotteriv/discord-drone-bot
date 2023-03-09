import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.ArrayList;

/**
 * Handles events from the audio player.
 */
public class TrackScheduler extends AudioEventAdapter {

    ArrayList<AudioTrack> queue = new ArrayList<>();

    /**
     * @param player    Audio player
     * @param track     Audio track that ended
     * @param endReason The reason why the track stopped playing
     */
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (!queue.isEmpty()) {
            player.playTrack(queue.remove(0));
        }
    }


    public void queue(AudioPlayer player, AudioTrack track) {
        if (queue.isEmpty()) {
            player.playTrack(track);
        } else {
            queue.add(track);
        }
    }

    public void clearQueue() {

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
