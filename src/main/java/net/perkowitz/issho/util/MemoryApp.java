package net.perkowitz.issho.util;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import net.perkowitz.issho.hachi.MemoryObject;
import net.perkowitz.issho.hachi.modules.mono.MonoMemory;
import net.perkowitz.issho.hachi.modules.mono.MonoPattern;
import net.perkowitz.issho.hachi.modules.mono.MonoSession;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by optic on 3/13/17.
 */
public class MemoryApp {

    private ObjectMapper objectMapper = new ObjectMapper();

    private MemoryObject memory;


    public static void main(String args[]) throws Exception {

        if (args.length < 0) {
            System.out.println("Usage: MemoryApp");
            System.exit(0);
        }

        MemoryApp memoryApp = new MemoryApp();
        memoryApp.run();
    }


    public MemoryApp() {
//        this.type = type.toLowerCase();
//        this.filename = filename;
    }

    public void run() {

//        load(filename);
//        print(memory, "");
        getInput();

    }

    private void getInput() {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input = "";
        while (true) {
            try {
                System.out.print("> ");
                input = br.readLine();
                List<String> args = Lists.newArrayList(input.split(" "));
                String command = args.get(0).toLowerCase();
                args.remove(0);
                processCommand(command, args);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processCommand(String command, List<String> args) {

        if (command.equals("end") || command.equals("q")) {
            System.exit(0);

        } else if (command.equals("ls")) {
            File[] files = new File(".").listFiles();
            for (File file : files) {
                if (file.getName().endsWith(".json")) {
                    System.out.println(file.getName());
                }
            }

        } else if (command.equals("open")) {
            if (args.size() < 2) {
                System.out.println("Usage: open <type> <filename>");
            } else {
                String type = args.get(0);
                String filename = args.get(1);
                load(type, filename);
            }

        } else if (command.equals("save")) {
            if (args.size() < 1) {
                System.out.println("Usage: save <filename>");
            } else {
                String filename = args.get(0);
                save(filename);
            }
            
        } else if (command.equals("print")) {
            print(memory, "");

        } else if (command.equals("cp")) {
            if (args.size() < 2) {
                System.out.println("Usage: cp <source> <destination>");
            } else {
                String sourcePath = args.get(0);
                String destinationPath = args.get(1);
                MemoryObject source = get(memory, sourcePath);
                MemoryObject clone = source.clone();
                MemoryObject destination = get(memory, destinationPath);
                clone.setIndex(destination.getIndex());
                System.out.printf("Copying %s over %s\n", source, destination);
                put(memory, destinationPath, clone);
            }

        } else {
            System.out.printf("Unrecognized command: %s\n", command);

        }

    }

    private void print(MemoryObject object, String indent) {
        for (MemoryObject child : object.list()) {
            if (child.nonEmpty()) {
                System.out.println(indent + child);
                print(child, indent + "  ");
            }
        }
    }

    private MemoryObject get(MemoryObject memoryObject, String path) {

        List<String> pathElements = Lists.newArrayList(path.split("/"));

        MemoryObject current = memoryObject;
        while (pathElements.size() > 0) {
            Integer index = new Integer(pathElements.get(0));
            pathElements.remove(0);
            current = current.list().get(index);
        }

        return current;
    }

    private void put(MemoryObject memoryObject, String path, MemoryObject putObject) {

        List<String> pathElements = Lists.newArrayList(path.split("/"));

        Integer lastIndex = null;
        MemoryObject previous = null;
        MemoryObject current = memoryObject;
        while (pathElements.size() > 0) {
            Integer index = new Integer(pathElements.get(0));
            pathElements.remove(0);
            lastIndex = index;
            previous = current;
            current = current.list().get(index);
        }

        if (previous != null) {
            previous.put(lastIndex, putObject);
        }

    }


    private void save(String filename) {

        try {

            File file = new File(filename);
            if (file.exists()) {
                Files.copy(file, new File(filename + ".backup"));
            }

            objectMapper.writeValue(file, memory);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void load(String type, String filename) {

        File file = new File(filename);
        if (!file.exists()) {
            file = new File(filename + ".json");
        }

        try {
            if (type.equals("mono")) {
                if (file.exists()) {
                    memory = objectMapper.readValue(file, MonoMemory.class);
                } else {
                    System.out.println("File not found. Initializing new MemoryObject.");
                    memory = new MonoMemory();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
