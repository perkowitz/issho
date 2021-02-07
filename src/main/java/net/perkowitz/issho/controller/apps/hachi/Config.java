package net.perkowitz.issho.controller.apps.hachi;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.perkowitz.issho.controller.Log;
import net.perkowitz.issho.controller.apps.hachi.modules.MockModule;
import net.perkowitz.issho.controller.apps.hachi.modules.Module;
import net.perkowitz.issho.controller.apps.hachi.modules.step.StepModule;
import net.perkowitz.issho.controller.midi.ClockListener;
import net.perkowitz.issho.controller.midi.MidiIn;
import net.perkowitz.issho.controller.midi.MidiOut;
import net.perkowitz.issho.controller.midi.MidiSetup;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.type.TypeReference;

import javax.sound.midi.MidiUnavailableException;
import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Config {

    private static final String MODULES = "modules";

    private static ObjectMapper objectMapper = new ObjectMapper();

    private JsonNode mainConfig;
    private JsonNode registryConfig;
    @Getter private List<Module> modules;
    @Getter private List<ModuleTranslator> moduleTranslators;

    public Config(String configFile, String registryFile) throws IOException {
        this.mainConfig = readJsonFile(configFile);
        this.registryConfig = readJsonFile(registryFile);
    }

    public Config(JsonNode mainConfig) {
        this.mainConfig = mainConfig;
    }

    public Map<String, List<List<String>>> getDeviceNameStrings() {
        Map<String, List<List<String>>> result = objectMapper.convertValue(registryConfig.get("deviceNameStrings"),
                new TypeReference<Map<String, List<List<String>>>>(){});
        return result;
    }

    public List<MidiIn> getMidiIns(MidiSetup midiSetup, ClockListener listener) throws MidiUnavailableException {
        List<String> names = objectMapper.convertValue(registryConfig.get("midiInNames"),
                new TypeReference<List<String>>(){});

        List<MidiIn> midiIns = Lists.newArrayList();
        for (String name : names) {
            MidiIn input = midiSetup.getMidiIn(name);
            if (input != null) {
                Log.log(this, Log.ALWAYS, "Found MIDI input for '%s': %s", name, input);
                input.addClockListener(listener);
                midiIns.add(input);
            }
        }

        return midiIns;
    }

    public List<MidiOut> getMidiOuts(MidiSetup midiSetup) throws MidiUnavailableException {
        List<String> names = objectMapper.convertValue(registryConfig.get("midiOutNames"),
                new TypeReference<List<String>>(){});

        List<MidiOut> midiOuts = Lists.newArrayList();
        for (String name : names) {
            MidiOut output = midiSetup.getMidiOut(name);
            if (output != null) {
                Log.log(this, Log.ALWAYS, "Found MIDI output for '%s': %s", name, output);
                midiOuts.add(output);
            }
        }

        return midiOuts;
    }

    public void loadModules(HachiController controller, MidiOut midiOut) {

        modules = Lists.newArrayList();
        moduleTranslators = Lists.newArrayList();

        Palette []ps = new Palette[]{
                Palette.BLUE, Palette.MAGENTA, Palette.CYAN, Palette.PURPLE,
                Palette.ORANGE, Palette.YELLOW, Palette.PINK, Palette.RED
        };

        ArrayNode moduleConfigs = (ArrayNode) mainConfig.get(MODULES);
        Iterator<JsonNode> iter = moduleConfigs.getElements();
        while (iter.hasNext()) {
            JsonNode node = iter.next();
            String className = node.get("class").getTextValue();
            String paletteName = "";
            if (node.get("palette") != null) {
                paletteName = node.get("palette").getTextValue();
            }
            String filePrefix = node.get("filePrefix").getTextValue();
            if (filePrefix == null) {
                filePrefix = className.toLowerCase() + (modules.size() + 1);
            }

            Palette palette = Palette.fromName(paletteName);
            if (palette == null) {
                palette = ps[modules.size() % ps.length];
            }

            if (className.equals("StepModule")) {
                ModuleTranslator moduleTranslator = new ModuleTranslator(controller);
                moduleTranslator.setEnabled(false);
                Module module = new StepModule(moduleTranslator, midiOut, palette, filePrefix);
                module.setPalette(palette);
                modules.add(module);
                moduleTranslators.add(moduleTranslator);

            } else if (className.equals("MockModule")) {
                ModuleTranslator moduleTranslator = new ModuleTranslator(controller);
                moduleTranslator.setEnabled(false);
                Module module = new MockModule(moduleTranslator, palette, false);
                modules.add(module);
                moduleTranslators.add(moduleTranslator);

            }
        }

    }


    /***** static methods *****/

    public static JsonNode readJsonFile(String filename) throws IOException {

        InputStream inputStream = null;
        try {

            File file = new File(filename);
            if (file.exists()) {
                inputStream = new FileInputStream(file);
            } else {
                inputStream = Config.class.getClassLoader().getResourceAsStream(filename);
            }

            if (inputStream != null) {
                return objectMapper.readTree(inputStream);
            }
            throw new FileNotFoundException("Config file '" + filename + "' not found in the classpath or path");

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return null;
    }
}
