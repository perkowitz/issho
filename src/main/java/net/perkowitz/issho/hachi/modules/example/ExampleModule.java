package net.perkowitz.issho.hachi.modules.example;

import com.google.common.io.Files;
import net.perkowitz.issho.devices.*;
import net.perkowitz.issho.hachi.Chordable;
import net.perkowitz.issho.hachi.Clockable;
import net.perkowitz.issho.hachi.Saveable;
import net.perkowitz.issho.hachi.Sessionizeable;
import net.perkowitz.issho.hachi.modules.*;
import net.perkowitz.issho.hachi.modules.step.*;
import org.codehaus.jackson.map.ObjectMapper;

import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import java.io.File;
import java.util.List;

import static net.perkowitz.issho.hachi.modules.example.ExampleUtil.*;


/**
 * Created by optic on 10/24/16.
 */
public class ExampleModule extends MidiModule implements Module, Clockable, GridListener, Sessionizeable, Chordable, Saveable, Muteable {

    ObjectMapper objectMapper = new ObjectMapper();

    private ExampleMemory memory = new ExampleMemory();
    private ExampleDisplay exampleDisplay;
    private SettingsSubmodule settingsModule;
    private boolean settingsView = false;

    private String filePrefix = "example";
    private int currentFileIndex = 0;

    private boolean someModeIsSet = false;
    private int someIndexOrOther = 0;


    /***** Constructor ****************************************/

    public ExampleModule(Transmitter inputTransmitter, Receiver outputReceiver, String filePrefix) {
        super(inputTransmitter, outputReceiver);
        this.exampleDisplay = new ExampleDisplay(this.display);
        exampleDisplay.setSomeModeIsSet(someModeIsSet);
        this.filePrefix = filePrefix;
        load(0);
        this.settingsModule = new SettingsSubmodule();
    }


    /***** private implementation ****************************************/

    /**
     * whatever happens every time there's a clock tick
     * 
     * @param andReset
     */
    private void advance(boolean andReset) {
        
    }


    /***** Module implementation **********************************
     *
     * all the basic methods a Module must implement 
     */

    /**
     * redraw all the device controls according to current state
     */
    public void redraw() {
        exampleDisplay.initialize();
        if (settingsView) {
            settingsModule.redraw();
            exampleDisplay.drawLeftControls();
        } else {
            exampleDisplay.redraw(memory);
        }
    }

    /**
     * when using a settings module and a dedicated display class, need to make sure 
     * the display object is passed on to them, since Hachi may set the module's display
     * 
     * @param display: a GridDisplay where this module should display its state
     */
    public void setDisplay(GridDisplay display) {
        this.display = display;
        this.exampleDisplay.setDisplay(display);
        this.settingsModule.setDisplay(display);
    }

    /**
     * anything the module should do when Hachi is turned off (e.g. turn off any lingering midi notes)
     */
    public void shutdown() {
        
    }


    /***** Muteable implementation **********************************
     * 
     * a Muteable is anything that may be muted (e.g. by a ShihaiModule 
     */

    /**
     * toggle the muted state
     * 
     * @param muted
     */
    public void mute(boolean muted) {
        this.isMuted = muted;
    }

    /**
     * report the current muted state
     * 
     * @return boolean
     */
    public boolean isMuted() {
        return isMuted;
    }


    /***** Chordable implementation **********************************
     * 
     * a Chordable may want to adjust its output based on notes sent in (e.g. from a ShihaiModule)
     */

    /**
     * receive a set of notes for adjusting/filtering the module's output 
     * 
     * @param notes
     */
    public void setChordNotes(List<Integer> notes) {

    }


    /***** Sessionizeable implementation ************************************
     * 
     * a Sessionizeable implements sessions and/or patterns and can be told
     * which sessions or patterns to load (e.g. by a ShihaiModule)
     * sessions/patterns may be defined differently for every module; they are just referred to by index
     */

    /**
     * select a new Session. 
     * module may do as it likes, but most modules load new sessions on the next measure start
     * 
     * @param index
     */
    public void selectSession(int index) {
        memory.setNextSessionIndex(index);
        redraw();
    }

    /**
     * select a new pattern or chain of patterns.
     * module may do as it likes, but most modules load new patterns on the next measure start
     * 
     * @param firstIndex
     * @param lastIndex
     */
    public void selectPatterns(int firstIndex, int lastIndex) {
        memory.setCurrentPatternIndex(firstIndex);
        exampleDisplay.drawPads(memory);
    }


    /***** GridListener interface ***************************************
     * 
     * a GridListener receives events from user input: press and release of pads and buttons 
     */

    public void onPadPressed(GridPad pad, int velocity) {
        onControlPressed(new GridControl(pad, null), velocity);
    }

    public void onPadReleased(GridPad pad) {
        onControlReleased(new GridControl(pad, null));
    }

    public void onButtonPressed(GridButton button, int velocity) {
        onControlPressed(new GridControl(button, null), velocity);
    }

    public void onButtonReleased(GridButton button) {
        onControlReleased(new GridControl(button, null));
    }

    /**
     * it is generally easier to use the GridControl abstraction, which generalizes both pads and buttons.
     * many modules pass the listener functions directly here, and then define their controls in their Util class
     * 
     * @param control
     * @param velocity
     */
    private void onControlPressed(GridControl control, int velocity) {

        if (control.equals(StepUtil.settingsControl)) {
            settingsView = !settingsView;
            exampleDisplay.setSettingsView(settingsView);
            this.redraw();

        } else if (control.equals(buttonControl)) {
            // check this control first because it's in the main and settings view
            someModeIsSet = !someModeIsSet;
            exampleDisplay.setSomeModeIsSet(someModeIsSet);
            exampleDisplay.drawLeftControls();

        } else if (settingsView) {
            // now check if we're in settings view and then process the input accordingly
           onControlPressedSettings(control, velocity);

        } else if (buttonControls.contains(control)) {
            // now see if it's one of the button controls, and then get the index to figure out which one
            int index = buttonControls.getIndex(control);
            someIndexOrOther = index;
            exampleDisplay.drawControl(control, true);

        } else if (onePadRowControls.contains(control)) {
            // now see if it's one of the pad controls
            int index = onePadRowControls.getIndex(control);
            exampleDisplay.drawPads(memory);

        } else if (twoPadRowControls.contains(control)) {
            // and so on
            int index = twoPadRowControls.getIndex(control);
            exampleDisplay.drawPads(memory);

        } else if (partialPadRowControls.contains(control)) {
            // and so forth
            int index = onePadRowControls.getIndex(control);
            exampleDisplay.drawPads(memory);

        } else if (control.equals(padControl)) {
            // or maybe it was a single pad
            memory.setSomeSettingOn(!memory.isSomeSettingOn());
            exampleDisplay.drawPads(memory);
        }

    }

    /**
     * when settings view is active, the user input should be passed to the SettingsSubmodule
     * but then the module must check with the SettingsSubmodule to see what was changed and
     * follow up accordingly. settings being similar for many modules, this still saves
     * some implementation effort
     * 
     * @param control
     * @param velocity
     */
    private void onControlPressedSettings(GridControl control, int velocity) {

        SettingsUtil.SettingsChanged settingsChanged = settingsModule.controlPressed(control, velocity);
        switch (settingsChanged) {
            case SELECT_SESSION:
                selectSession(settingsModule.getNextSessionIndex());
                break;
            case LOAD_FILE:
                load(settingsModule.getCurrentFileIndex());
                break;
            case SAVE_FILE:
                save(settingsModule.getCurrentFileIndex());
                break;
            case SET_MIDI_CHANNEL:
                memory.setMidiChannel(settingsModule.getMidiChannel());
                break;
        }
    }

    /**
     * any momentary controls may need to be lit on press and unlit on release
     * 
     * @param control
     */
    private void onControlReleased(GridControl control) {
        if (settingsView) return;

        if (buttonControls.contains(control)) {
            exampleDisplay.drawControl(control, false);
        }


    }

    /***** Clockable implementation ***************************************
     * 
     * a Clockable can receive clock ticks and start/stop messages from Hachi's master clock. 
     */

    public void start(boolean restart) {

    }

    public void stop() {

    }

    public void tick(boolean andReset) {
        advance(andReset);
    }


    /***** Saveable implementation ***************************************
     * 
     * a Saveable can write its memory to a file and load memory from a file.
     * Saveables can also be asked (e.g. by a ShihaiModule) to save/load by index.
     * many modules store a filePrefix and then just select files by appending an index number to the prefix
     */

    public void setFilePrefix(String filePrefix) {
        this.filePrefix = filePrefix;
    }

    public String getFilePrefix() {
        return filePrefix;
    }

    public void save(int index) {
        try {
            String filename = filename(index);
            File file = new File(filename);
            if (file.exists()) {
                // make a backup, but will overwrite any previous backups
                Files.copy(file, new File(filename + ".backup"));
            }
            objectMapper.writeValue(file, memory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load(int index) {
        try {
            String filename = filename(index);
            File file = new File(filename);
            if (file.exists()) {
                memory = objectMapper.readValue(file, ExampleMemory.class);
            } else {
                memory = new ExampleMemory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String filename(int index) {
        return filePrefix + "-" + index + ".json";
    }



}
