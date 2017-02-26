package net.perkowitz.issho.hachi.modules.minibeat;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by optic on 10/24/16.
 */
public class MinibeatMemory {

    @Getter private int currentSessionIndex = 0;
    @Getter private int nextSessionIndex = 0;
    @Getter private int playingPatternIndex = 0;
    @Getter private int chainStartIndex = 0;
    @Getter private int chainEndIndex = 0;
    @Getter private int selectedPatternIndex = 0;
    @Getter private int selectedTrackIndex = 0;

    @Getter @Setter private int midiChannel = 0;

    private List<MinibeatSession> sessions = Lists.newArrayList();


    public MinibeatMemory() {

        for (int i = 0; i < MinibeatUtil.SESSION_COUNT; i++) {
            sessions.add(new MinibeatSession(i));
        }
    }


    /***** get current *************************************/

    public List<Integer> getChainedPatternIndices() {
        return Lists.newArrayList();
    }

    public MinibeatSession getCurrentSession() {
        return sessions.get(currentSessionIndex);
    }

    public MinibeatPattern getPlayingPattern() {
        return getCurrentSession().getPattern(playingPatternIndex);
    }

    public MinibeatPattern getSelectedPattern() {
        return getCurrentSession().getPattern(selectedPatternIndex);
    }

    public MinibeatTrack getSelectedTrack() {
        return getSelectedPattern().getTrack(selectedTrackIndex);
    }


    /***** make selections *************************************/

    public void selectSession(int index) {
        currentSessionIndex = index;
    }

    public void selectPattern(int index) {
        selectedPatternIndex = index;
    }

    public void selectChain(int startIndex, int endIndex) {
        chainStartIndex = startIndex;
        chainEndIndex = endIndex;
        playingPatternIndex = chainStartIndex;
    }

    public void selectTrack(int index) {
        selectedTrackIndex = index;
    }


    /***** play logic *************************************/

    public void advancePattern() {
        playingPatternIndex++;
        if (playingPatternIndex > chainEndIndex) {
            playingPatternIndex = chainStartIndex;
        }
    }



}
