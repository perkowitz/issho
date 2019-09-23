package net.perkowitz.issho.hachi.modules.deprecated.minibeat;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by optic on 2/25/17.
 */
public class MinibeatSession {

    @Getter private int index;
    @Getter private List<MinibeatPattern> patterns = Lists.newArrayList();

    // sessions remember their state so you can load them to specific patterns and mutes
    @Getter private List<Boolean> tracksEnabled = Lists.newArrayList();
    @Getter private int chainStartIndex = 0;
    @Getter private int chainEndIndex = 0;
    @Getter @Setter private int selectedTrackIndex = 0;


    public MinibeatSession() {}

    public MinibeatSession(int index) {
        this.index = index;
        for (int i = 0; i < MinibeatUtil.PATTERN_COUNT; i++) {
            patterns.add(new MinibeatPattern(i));
        }
        for (int i = 0; i < MinibeatUtil.TRACK_COUNT; i++) {
            tracksEnabled.add(true);
        }
    }

    public MinibeatPattern getPattern(int index) {
        return patterns.get(index);
    }

    public Boolean trackIsEnabled(int index) { return tracksEnabled.get(index); }

    public void toggleTrackEnabled(int index) {
        tracksEnabled.set(index, !tracksEnabled.get(index));
    }

    public void selectChain(int chainStartIndex, int chainEndIndex) {
        this.chainStartIndex = chainStartIndex;
        this.chainEndIndex = chainEndIndex;
    }

}
