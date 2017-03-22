# Overview

MemoryApp is a tool for managing files and memory objects for Hachi modules. It can be used to copy
sessions into a new file or patterns into a new session in order to back up or organize Hachi
module data. MemoryApp runs from the command line.

# Running MemoryApp

Once the Issho package has been built (```mvn package```), just execute ```mem```
from the root directory of the repo. 

# MemoryApp commands

```
help
```
will list these commands.


```
ls
```
will list all JSON files in the current directory.


```
open <type> <filename>
```
will read a Hachi module file into memory. Type must be one of 
```mono```, ```step```, or ```rhythm``` to specify the type of file being loaded.
The ```.json``` can be left out. The contents of a memory file can't be examined or managed
until it's been opened. Shortcut: ```o```. Opening a file that doesn't exist will create
a new initialized object (but will not save it to disk).


```
print <path>
```
will display the contents of a memory object. Memory objects inside a file are named
much like files in a hierarchical file system. ```mono0-0``` is the path of a file 
containing an entire MonoModule memory. ```mono0-0/0``` indicates the first session
in that file. ```mono0-0/3/2``` is the third pattern in the fourth session (indices start with 0)
in that file. Print values depend on the object type, but generally it will display the
name and index of the object and a list of its non-empty children. If no children are shown, 
it's probably because it's an empty (newly initialized) object, or an object type that
has no children.


```
save <filename>
```
will save the memory data back to the file it was loaded from, including any changes
that have been made.


```
cp <source path> <destination path>
```
will copy the memory object specified by the source path into the place of the destination path,
overwriting any data in the destination. MemoryApp will not allow you to copy a type of memory object
to a place it doesn't belong; you can't copy a MonoModule session into a StepModule memory, or
put a session where a pattern belongs.



```
q
```

will exit MemoryApp.


# Example

List the available memory files and load a MonoModule memory.
```
> ls
m0.json
m1.json
m2.json
m3.json
r0.json
r1.json
s0.json
s1.json
 
> open mono m0
Loading MonoMemory from m0
```

Examine the contents of the memory file, then load another memory file and print its contents. 
```
> print m0
MonoMemory
  . MonoSession:00 - 02/16 children
  . MonoSession:01 - 01/16 children
 
> print m0/0
MonoSession:00 - 02/16 children
  . MonoPattern:00 - O--O--O.O.O.O---
  . MonoPattern:01 - O.OOO.........O.

> print m0/0/1
MonoPattern:01 - O.OOO.........O.
 
> open mono m1
Loading MonoMemory from m1
 
> print m1
MonoMemory
  . MonoSession:00 - 02/16 children
  . MonoSession:01 - 01/16 children
  . MonoSession:02 - 02/16 children
  . MonoSession:03 - 02/16 children
  . MonoSession:04 - 01/16 children
```

Copy one of the sessions in the second file into the first and verify that it's there.
Save the changes back into the original file.
``` 
> cp m1/04 m0/02
Copying MonoSession:04 over MonoSession:02
 
> print m0
MonoMemory
  . MonoSession:00 - 02/16 children
  . MonoSession:01 - 01/16 children
  . MonoSession:02 - 01/16 children
 
> save m0
```

Look at the contents of a StepModule file.
```
> open step s0
Loading StepMemory from s0
 
> print s0
MonoMemory
  . StepSession:00 - 08/16 children
  . StepSession:01 - 08/16 children
 
> print s0/0
StepSession:00 - 08/16 children
  . StepPattern:00 - 06/08 children
  . StepPattern:01 - 06/08 children
  . StepPattern:02 - 06/08 children
  . StepPattern:03 - 06/08 children
  . StepPattern:04 - 08/08 children
  . StepPattern:05 - 08/08 children
  . StepPattern:06 - 08/08 children
  . StepPattern:07 - 08/08 children
 
> print s0/0/5
StepPattern:05 - 08/08 children
  . Stage:00 - ...O._.^
  . Stage:01 - ...O-.^^
  . Stage:02 - .=O...^^
  . Stage:03 - .=O-...^
  . Stage:04 - ..O.....
  . Stage:05 - .._.....
  . Stage:06 - .O>>>.^^
  . Stage:07 - .......^
```

Look at the contents of a RhythmModule file.
```
> open rhythm r0
Loading RhythmMemory from r0
 
> print r0
RhythmMemory
  . RhythmSession:00 - 09/24 children
  . RhythmSession:01 - 01/24 children
  . RhythmSession:02 - 02/24 children
 
> print r0/0
RhythmSession:00 - 09/24 children
  . Pattern:00 - 15/16 children
  . Pattern:02 - 09/16 children
  . Pattern:03 - 10/16 children
  . Pattern:04 - 06/16 children
  . Pattern:05 - 05/16 children
  . Pattern:06 - 05/16 children
  . Pattern:07 - 05/16 children
  . FillPattern:02 - 03/16 children
  . FillPattern:04 - 03/16 children
 
> print r0/0/0
Pattern:00 - 15/16 children
  . RhythmTrack:01 - ...O......OO..O.
  . RhythmTrack:02 - ..OO.OO.........
  . RhythmTrack:03 - O...............
  . RhythmTrack:04 - O.O...O.OOO...O.
  . RhythmTrack:05 - O...O...O..O....
  . RhythmTrack:06 - .......O........
  . RhythmTrack:07 - ..OO.O..........
  . RhythmTrack:08 - O...O...O...O...
  . RhythmTrack:09 - ....O.......O...
  . RhythmTrack:10 - .........O......
  . RhythmTrack:11 - O...............
  . RhythmTrack:12 - ...............O
  . RhythmTrack:13 - O...O...O...O.O.
  . RhythmTrack:14 - ..O..O....O...O.
  . RhythmTrack:15 - ..........O.....
 
> print r0/0/4
Pattern:04 - 06/16 children
  . RhythmTrack:02 - ....O.......O...
  . RhythmTrack:03 - O...............
  . RhythmTrack:04 - O.OOO.OOO.OOO.OO
  . RhythmTrack:06 - ..O...O...O...O.
  . RhythmTrack:08 - O...O...O...O...
  . RhythmTrack:09 - ....O.......O...
```

Quit MemoryApp. 
```
> q
Process finished with exit code 0
```


