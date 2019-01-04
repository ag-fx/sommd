# SoMMD: Somewhat Mathematical Music Description

SoMMD is meant to be an educational alternative to classic music notation. Classic music notation is just mathematics. Fractions, pitches,
base 12 numbers, subsets of the chromatic scale, ...mathematical patterns all hidden behind symbols you might not have learnt
to interpret (yet).

In SoMMD, all these numbers are out there in the plain.

It is meant to be simple and efficient to write in. To express, "four notes at the 5th octave, with increasing semitone,
with a duration that alternates between 1/4th and 3/4ths"
, write 
"dr 1/4 3/4 nt 50 55 54 50"

The following is supported: Branching, chords, scales, scale shifts, instrument change, velocities, variable definitions, 
MIDI export, FL Studio score export, chiptune-esque audio export and Java General-MIDI playback.

The program is split into the following modules:
* Core: Handles all parsing of text into music formats like MIDI
* Gui: TornadoFX (JavaFX) based GUI

The core has no java dependencies at all, it is written in pure Kotlin! Thus it can be used in web or native applications as
Kotlin can be compiled against those targets.
