package net.perkowitz.sequence.models;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.sequence.SequencerInterface;

import java.util.List;
import java.util.Map;

import static net.perkowitz.sequence.SequencerInterface.Switch.INTERNAL_CLOCK_ENABLED;
import static net.perkowitz.sequence.SequencerInterface.Switch.MIDI_CLOCK_ENABLED;
import static net.perkowitz.sequence.SequencerInterface.Switch.TRIGGER_ENABLED;

/**
 * Created by optic on 7/9/16.
 */
public class Memory {

    @Getter @Setter private static int sessionCount = 8;
    @Getter private Session[] sessions;

    @Getter private int selectedSessionIndex;
    @Getter private int selectedPatternIndex;
    @Getter private boolean selectedPatternIsFill;
    @Getter private int selectedTrackIndex;
    @Getter private int selectedStepIndex;

    @Getter private int playingPatternIndex;// the currently playing pattern (which might not be in the chain, if a new one has been selected)
    @Getter private int patternChainMin;    // the index of the first of the playing pattern chain
    @Getter private int patternChainMax;    // the index of the last of the pattern chain
    @Getter private int patternChainIndex;  // the index of the NEXT pattern to play
    private Pattern playingFillOverride = null;

    @Getter @Setter private boolean specialSelected = false;
    @Getter @Setter private boolean copyMutesToNew = true;

    @Getter @Setter private Map<SequencerInterface.Switch, Boolean> settingsSwitches = Maps.newHashMap();
    @Getter @Setter private Map<SequencerInterface.Switch, Integer> settingsValues = Maps.newHashMap();

    public Memory() {

        this.sessions = new Session[sessionCount];
        for (int i = 0; i < sessionCount; i++) {
            sessions[i] = new Session(i);
        }

        select(getSession(0));
        select(selectedSession().getPattern(0));
        select(selectedPattern().getTrack(8));
        setPatternChain(0, 0, 0);
        playingPatternIndex = 0;

        settingsSwitches.put(TRIGGER_ENABLED, false);
        settingsSwitches.put(MIDI_CLOCK_ENABLED, false);
        settingsSwitches.put(INTERNAL_CLOCK_ENABLED, true);



    }

    public Session selectedSession() {
        return sessions[selectedSessionIndex];
    }

    public Pattern selectedPattern() {
        if (selectedPatternIsFill) {
            return selectedSession().getFill(selectedPatternIndex);
        } else {
            return selectedSession().getPattern(selectedPatternIndex);
        }
    }

    public Track selectedTrack() {
        return selectedPattern().getTrack(selectedTrackIndex);
    }

    public Step selectedStep() {
        return selectedTrack().getStep(selectedStepIndex);
    }

    public Pattern playingPattern() {
        if (playingFillOverride != null) {
            return playingFillOverride;
        }
        return selectedSession().getPattern(playingPatternIndex);
    }

    public Pattern nextPattern() {
        return selectedSession().getPattern(patternChainIndex);
    }


    public Session getSession(int index) {
        return sessions[index % sessionCount];
    }

    public void select(Session session) {
        Session selectedSession = selectedSession();
        if (selectedSession != null) {
            selectedSession.setSelected(false);
        }
        selectedSession = session;
        selectedSession.setSelected(true);
        selectedSessionIndex = session.getIndex();
        session.setNext(false);
    }

    public void select(Pattern pattern) {
        Pattern selectedPattern = selectedPattern();
        if (selectedPattern != null) {
            selectedPattern.setSelected(false);
        }
        pattern.setSelected(true);

        selectedPatternIndex = pattern.getIndex();
        if  (pattern instanceof FillPattern) {
            selectedPatternIsFill = true;
        } else {
            selectedPatternIsFill = false;
        }

        pattern.selectTrack(selectedTrackIndex);
    }

    public void select(Track track) {
        selectedTrackIndex = track.getIndex();
        for (Track t : selectedPattern().getTracks()) {
            t.setSelected(false);
        }
        track.setSelected(true);
    }

    public void select(Step step) {
        Step selectedStep = selectedStep();
        if (selectedStep != null) {
            selectedStep.setSelected(false);
        }
        selectedStepIndex = step.getIndex();
        step.setSelected(true);
    }

    public List<Pattern> setPatternChain(int min, int max, int index) {

        for (int i = patternChainMin; i <= patternChainMax; i++ ) {
            selectedSession().getPattern(i).setChained(false);
        }

        List<Pattern> newChain = Lists.newArrayList();
        patternChainMin = min;
        patternChainMax = max;
        patternChainIndex = index;
        for (int i = patternChainMin; i <= patternChainMax; i++ ) {
            Pattern pattern = selectedSession().getPattern(i);
            pattern.setChained(true);
            newChain.add(pattern);
        }

        return newChain;
    }

    public void resetPatternChainIndex() {
        patternChainIndex = patternChainMin;
    }

    public List<Pattern> getPatternChain() {
        List<Pattern> patterns = Lists.newArrayList();
        for (int i = patternChainMin; i <= patternChainMax; i++ ) {
            patterns.add(selectedSession().getPattern(i));
        }
        return patterns;
    }


    public Pattern advancePattern(int measureNumber) {

//        System.out.printf("advancePattern: measure=%d\n", measureNumber);
        Pattern playing = playingPattern();
        Pattern next = nextPattern();
//        System.out.printf("advancePattern: next=%s\n", next);

        // check to see if a fill should play -- when we've hit the fill's interval and there aren't applicable fills on a shorter interval
        int maxInterval = -1;
        int percentSum = 0;
        List<FillPattern> readyFills = Lists.newArrayList();
        for (FillPattern fill : selectedSession().getFills()) {
            if (fill.isChained() && measureNumber % fill.getFillInterval() == 0) {
                if (readyFills.size() == 0) {
                    readyFills.add(fill);
                    maxInterval = fill.getFillInterval();
                    percentSum = fill.getFillPercent();
                } else if (fill.getFillInterval() == maxInterval) {
                    readyFills.add(fill);
                    percentSum += fill.getFillPercent();
                } else if (fill.getFillInterval() > maxInterval) {
                    readyFills.clear();
                    readyFills.add(fill);
                    maxInterval = fill.getFillInterval();
                    percentSum = fill.getFillPercent();
                }
            }
        }
//        System.out.printf("advancePattern: readyfills=%s\n", readyFills);
        // randomly choose one of the available fills
        if (readyFills.size() > 0 && (Math.random() * 100) <= (percentSum / readyFills.size())) {
            int index = (int)(Math.random() * readyFills.size()); // TODO: use the FillPattern probs to choose between them instead of equally weightedune
            next = readyFills.get(index);
            playingFillOverride = next;
        } else {
            playingFillOverride = null;
        }
//        System.out.printf("advancePattern: next=%s\n", next);

        if (playing != next) {
            playing.setPlaying(false);
            playingPatternIndex = patternChainIndex;
            patternChainIndex++;
            if (patternChainIndex > patternChainMax) {
                patternChainIndex = patternChainMin;
            }
            next.setPlaying(true);
            if (copyMutesToNew) {
                next.copyMutes(playing);
            }

            if (!specialSelected) {
                select(next);
                next.selectTrack(selectedTrackIndex);
            }

        }

        return next;
    }

    public Boolean flipSwitch(SequencerInterface.Switch switchx) {
        Boolean isSet = isSet(switchx);
        if (isSet != null) {
            settingsSwitches.put(switchx, !isSet);
            return !isSet;
        }
        return null;
    }

    public Boolean isSet(SequencerInterface.Switch switchx) {
        Boolean isSet = settingsSwitches.get(switchx);
        if (isSet == null) {
            isSet = false;
        }
        return isSet;
    }


}
