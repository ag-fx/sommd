# SoMMD: Somewhat Mathematical Music Description

SoMMD is meant to be an educational alternative to classic music notation. Classic music notation is just mathematics. Fractions, pitches,
base 12 numbers, subsets of the chromatic scale, ...mathematical patterns all hidden behind symbols you might not have learnt
to interpret (yet).

In SoMMD, all these numbers are out there in the plain.

It is meant to be simple and efficient to write in. To express, "four notes at the 5th octave, with increasing semitone,
with a duration that alternates between 1/4th and 3/4ths"
, write 
"dr 1/4 3/4 nt 50 55 54 50"

The following is supported: 
* Language: Branching, chords, scales, scale shifts, instrument change, velocities, and variable definitions

* Export/Playback: MIDI, FL Studio score, chiptune-esque audio, and Java General-MIDI playback.

* UI: Syntax highlighting, error messages, dark and light theme, inbuilt documentation/examples

The program is split into the following modules:
* Core: Handles all parsing of text into music formats like MIDI
* Gui: TornadoFX (JavaFX) based GUI

The core has no java dependencies at all, it is written in pure Kotlin! Thus it can be used in web or native applications as
Kotlin can be compiled against those targets.

#Build/run
On command line:
mvn package;
java -jar ./target/sommdjfx-0.1.jar;

Using an IDE, main function is in:
org.domaintbn.sommd.gui.Main

#Screenshot

![](https://github.com/user00e00/sommd/blob/master/Screenshot_SoMMD.png?raw=true)
