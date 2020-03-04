package org.domaintbn.sommd.core

enum class ExampleSongs(val description: String, val text: String) {

    BASIC_COMMENTS(
      "Basic: 0, Comments",
            """
                // text after a // will be ignored for the
                // following row (line) so that it is possible to
                // add comments in songs

                this line is not ignored

                /* multiline comments start with /* and
                ends
                with
                */

                this is not ignored by the multiline comment.
            """.trimIndent()


    ),

    BASIC_NOTESONG(
        "Basic: 1, Note command"
        , """
            // notes are made with the {nt} command
            // this plays one note on octave 4, and semitone 0 within that octave

                    nt 40

                    nt 0 0 0 0 // inserting pause.

            // this plays octave 5, semitone 4

                    nt 54

                    nt 0 0 0 0 // inserting pause.

            // and this plays octave 3, semitone 11, written with a "b"
            // .. 10 is written with an "a"

                    nt 3b

                    nt 0 0 0 0 // inserting pause.

            // the note command takes sequences as well
            // it is equivalent

                    nt 40 54 3b         // equivalent to the one below, just shorter
                    nt 40 nt 54 nt 3b   //

                        nt 0 0 0 0 // inserting pause.

            // ...and sequences with silent notes in between

                    nt 0 40 0 54 0 3b

                        nt 0 0 0 0 // inserting pause.

            // ...or "stacked" notes, played as a chord

                    nt 50-54-47     // this becomes a major chord

                        nt 0 0 0 0 // inserting pause.

            // ... of course you can combine everything

                    nt 50-60 0 50-57 0 50-55 40

                        nt 0 0 0 0 // inserting pause.

        """.trimIndent()
    ),

    BASIC_DURATIONSONG(
        "Basic: 2, Duration command"
        , """
            // durations for notes are set with the {dr} command
            // this sets the duration of all subsequent notes to 1/4

                    dr 1/4
                    nt 50 51 52 53      //output some notes with duration 1/4

            // this to 1/3

                    dr 1/3
                    nt 50 51 52 53      //...with duration 1/3

            // now we set up a CYCLE. Subsequent notes will cycle between getting a duration of
            // either 1/4 or 3/4

                    dr 1/4 3/4
                    nt 50 51 52 53      // 51 has duration 1/4, 52 -> 3/4, 52 -> 1/4, 53 -> 3/4 ...

            // can make long cycles if we like

                    dr 1/4 1/4 1/4 1/8 1/8
                    nt 50 51 52 53 54 55 56 57 58 59 5a 5b 60

            // STACKING of durations with "-" in between. Plays each subsequent note multiple times

                dr 1/16-1/16
                nt 50       // this is played twice at duration 1/16!


            // silencing a duration with an "s" in front of the fraction

                dr 1/16 s1/16 1/16-1/16
                nt 50 51 52             // 50 -> 1/16, 51 becomes silent with duration 1/16, then
                                        // ... 52 is repeated twice with duration 1/16


        """.trimIndent()
    ),

    BASIC_INSTRUMENTSONG(
        "Basic: 3, Instrument Command"
        , """
        // Command
        //	name: Instrument
        //	start label: "in"
        // 	parameters: Int, such as 0 , 1 , 4 , -1 , or 54

        // this command tells subsequent notes to be played by the
        // given instrument number (when mapped to MIDI, becomes
        // MIDI channel)

            in 0
            nt 50	// played with instrument 0

            in 1
            nt 50	// played with instrument 1

        // Can setup cycles too

            in 0 1 2
            nt 50		// played with instrument 0
            nt 51		// played with instrument 1
            nt 52		// played with instrument 2
            nt 53		// played with instrument 0
                        // etc...

        // independent cycles

            dr 1/1 1/2 1/2
            in 0 1 2

            dr 1/4			// instrument changes still defined by the
                            // previous duration command with 3 entries

            nt 50 51 52 53	// played with instrument 0
            nt 40 41		// played with instrument 1
            nt 42 43		// played with instrument 2

            dr 1/1
            nt 50			//played with instrument 0

    """.trimIndent()
    ),


    BASIC_VELOCITYSONG("Basic: 4, Velocity Command","""
// velocity (loudness) of notes are set with the {ve} command
// it takes a number between 0.0 and 100, where 100 is the max loudness and 0
// is the minimum loudness.

ve 50
nt 50 52 54 0

ve 80
nt 50 52 54 0

ve 35
nt 50 52 54 0


//it is similar to the instrument command, in that you can set up cycles

ve 40.34 90.5 //decimals are allowed
nt 50 52 55 54 52 50 0 0

// independent cycles

dr 1/1 1/1 2/1
ve 90 30 60

dr 1/8
// velocity changes still defined by the
// previous duration command with 3 entries


nt 50 52 55 54 52 50 57 60 //played with velocity 90
nt 50 52 55 54 52 50 57 60 //played with velocity 30
nt 50 52 55 54 52 50 57 60 //played with velocity 60
nt 50 52 55 54 52 50 57 60 //played with velocity 60
    """.trimIndent()),

    BASIC_BRANCHSONG("Basic: 5, Branch Command","""
//Branches are made with {brs}, {brr} and {bre} commands.
brs //branch start

brr //branch reset

bre //branch end


//this plays a chord
brs
nt 40
brr // this command resets the time (and state) to what it was at the branch start
nt 47
bre //exits the branch

dr 1/1 nt 0

//this plays three sections simulatenously
brs
	//this becomes the first branch in this group!
	in 4 dr 1/1 nt 50-54-57 54-57-5a 52-56-59 50-54-57
brr
	in 1
	dr 1/8 nt 50 52 55 dr 3/8 nt 54 dr 1/8 nt 52 50 49 50 dr 2/4 nt 50
	dr 1/8 nt 57 5a 59 56 59 dr 3/8 nt 62 dr 1/4 nt 0 0
	54 52 dr 1/4 nt 50-57
brr
	in 3 dr 3/4-1/4 nt 40 44 42 dr 1/1 nt 40
bre

//after the branch exit {bre} command, the "playhead" continues with the state it had
// at the end of the FIRST branch.
//
in 4 dr 1/1 //changes nothing and can be commented out, as this is the playheads
//current state anyway

nt 0
nt 50-57 50-54 //played with instrument 4, and duration 1/1

nt 0
brs
	in 2 dr 1/4 nt 40 50
brr
	//second branch. is longer than the first one, so it will extend beyond the branch end
	in 3 dr 1/4 nt 47 47 45 45 47 47 40 40
bre


//now the state and time is equal to that as it was out of the first branch, even though
//the second branch is not finished yet. First branch always determines end state!
//sadly you need to keep track of possible overlapping yourself

in 1 nt 31 32 33 34 35 36 37 // this will overlap with output from the second branch above

nt 0 0 0 0

// branch commands can be nested:
brs
	dr 1/2 //this duration propagates down to the sub-branch unless overwritten
	brs
		in 0 nt 40 50-60 40 37-40
	brr
		in 1 nt 47 45 47 47
	bre
brr
dr 1/1 in 2 nt 30-50 30-47-60
bre
    """.trimIndent()),

    ADVANCED_SCALESONG("Advanced: 1, Scale Commands","""
/*
Scales are set with the {sc} command, and it
takes stacked pitches as parameters.

then the second digit in note commands will point
to the corresponding pitches in the stack/group

With multiple entries of stacked pitches, the scale command
sets up a cycle using the preceding duration
*/

//play a bit faster than default 1/4
	dr 1/8

//first we play a sequence of notes with the default scale, which is the chromatic one
	nt 50 51 52 53 54 55 56 57 58 59 5a 5b

	dr 1/1 nt 0 dr 1/8 //insert silence

//now define the chromatic scale ourselves. notice that scale command
//only accepts stacked pitches ({-} connected pitches)
	sc 50-51-52-53-54-55-56-57-58-59-5a-5b

//the scale we set and the default scale are equal, so a note command with the same
//data as earlier will play the same notes

	nt 50 51 52 53 54 55 56 57 58 59 5a 5b

	dr 1/1 nt 0 dr 1/8 //insert silence

//here we have a minor scale
	sc 50-52-53-55-57-58-5a

//the following note command is the same as the earlier ones, but it is played
//using a different scale!
	nt 50 51 52 53 54 55 56 57 58 59 5a 5b

	dr 1/1 nt 0 dr 1/8 //insert silence

//define the major scale
	sc 50-52-54-55-57-59-5b

//play the major scale
	nt 50 51 52 53 54 55 56


	dr 1/1 nt 0 dr 1/8 //insert silence

//define a backwards scale and play it
	sc 5b-59-57-55-54-52-50
	nt 50 51 52 53 54 55 56

//Now we need to understand a bit more how pitches are defined in the note command
// nt 50, really means "note with octave offset 0 (because 5 - 5 = 0), and scale index 0"
// 	(5 is defined as the "zero" or reference octave
//nt 43 becomes "note with octave offset  (4 - 5 = ..) -1, and scale index 3

	dr 1/1 nt 0 dr 1/8 //insert silence

//define short scale with only three elements indexed by 0, 1 or 2
	sc 50-54-57

// plays the scale with no octave offset: 5 - 5 = 0
	nt 50 51 52 0

// plays the scale with -1 octave offset: 4 - 5 = -1
	nt 40 41 42 0

// plays the scale with +1 octave offset: 6 - 5 = 1
	nt 60 61 62 0


// indexing the scale but in a different order
	nt 52 50 51 0

// What happens when a note command has an index larger
// than the amount of entries in the scale?
// It wraps around, and adds an octave offset

// another scale with 3 entries, indexed by 0, 1 and 2
	sc 50-53-57

//these are equivalent.
	nt 50 51 52 53 54 55 0 0 // wraps 3 to 0 and adds +1 to octave, 4 to 1, etc...
	nt 50 51 52 60 61 62 0 0 // doesn't wrap, "manually inserted" octave offset

//these too are equivalent, the octave offset in the note command works as expected
	nt 40 41 42 43 44 45 0 0
	nt 40 41 42 50 51 52 0 0

//pitches in the scale are not limited to the default (=5) octave
	sc 40-53-67
	nt 50 51 52 0
	nt 50 54 42 0
// note command with octave=5 and index less than amount of pitches in the scale
// will thus play back the given scale pitch exactly.

// one could argue that pitches in the scale command are different
// from pitches in the note command.
// the scale command pitches are interpreted not as octave+offset,
// but directly as octave and semitone


// setting up cycles for a scale is possible
//here every 2nd bar the scale switches between a 3-note minor and 3-note major scale
	dr 2/1
	sc 50-53-57    50-54-57

	dr 1/8
//played with the first scale in the cycle
	nt 50 51 52 53   52 61 55 50

	dr 1/1 nt 0 dr 1/8 //pause

//played with the second scale in the cycle
	nt 50 51 52 53   52 61 55 50



    """.trimIndent()),

    ADVANCED_VARIABLESSONG("Advanced: 2, Variables","""
//variables are made and used by writing a command, with a period and a variable
// name behind it

//this defines a note command but it is not activated or "played yet"
nt.var1= 40 50 60 0

//rewriting the command with the same variable name, but WITHOUT the {=}
//means we activate the variable
nt.var1

//and we can do so multiple times
nt 0 0 0 0 //insert pause

nt.var1 nt.var1

// define two duration commands
dr.rhythm1= 1/8 3/8
dr.rhythm2= 1/8-1/8 s2/8

nt.var2= 40 42 43 45 47 43 47 0

nt 0 0 0 0 //insert pause

//putting some variables together
dr.rhythm1 nt.var2
dr.rhythm2 nt.var2

// and now two different instrument commands
in.inst1= 0 1
in.inst2= 2 3

//putting some variables together
dr.rhythm1 in.inst1 nt.var2
dr.rhythm1 in.inst2 nt.var2






    """.trimIndent()),

    ADVANCED_VARIABLEBRANCHES(
      "Advanced: 3, Branch Variables",
            """
// branch variables are used/made by writing {brs.variablename} or
// {brs.variablename= ... brr .... bre}
// {brr} and {bre} doesn't contain any data themselves,
//  so e.g{bre.varname} is not supported


//defining a branch variable
brs.chord1= nt 40
brr nt 47
brr nt 50
bre

//play chord1 three times and then pause
brs.chord1 brs.chord1 brs.chord1 nt 0

dr 1/2
brs.chord2= nt 40
brr dr 1/8-1/8-1/8-1/8 nt 47 //second branch
brr nt 50
bre

brs.chord2 brs.chord2

dr 1/1 //the outer duration is different this time, but it is overwritten for the second branch
brs.chord2 brs.chord2


//nesting is possible

dr 2/1
brs
	brs.chord1
brr
	in 1
	ve 50
	brs.chord2
brr
	in 2
	nt 30
bre



            """.trimIndent()


    ),

    ADVANCED_NOTE_COMMAND(
      "Advanced: 4, Note command (more)",
            """
                // Note command {nt}, advanced concepts:

//NEGATIVE INDEX
//recall that nt 53 means (standard octave (5-5=0), and scale index 3

//adding an exclamation mark in front of the pitch data makes the index, "negative"

// different notation but plays the same two notes in a row each time
nt !50 50 0 0    !51 4b 0 0   !52 4a 0 0   !53 49 0 0

// mostly meant to be used with scales, so that one can go "backwards" without having
// to worry about the octave
sc 50-52-53-55-57-58-5a

nt 50 51 50 !51 50 51 52 53 50 !51 !52 !53


sc 50-51-52-53-54-55-56-57-58-59-5a-5b //resetting to chromatic scale

nt 0 0 0 0 //pause

//REPEATS AND EXTENDS
// repeat or extend notes with {^} or {*}
nt 40^4 // equivalent to {nt 40 40 40 40}

nt 0 0 0 0 //pause

dr 1/2 nt 40*4 // equivalent to {dr 2/1 nt 40}
dr 1/4 1/4 1/2 nt 40*3 //equivalent to dr 1/1 nt 40

// extended pitches ignore stacked durations and are sustained
dr 1/8-1/8 nt 50*2 //equivalent to dr 1/2 nt 50

dr 1/4 //reset duration
nt 0 0 0 0 //pause


//BRACKET GROUPS
// with {[} and {]}
// - be able to repeat a sequence and not just a single pitch

//equivalent commands below
nt [50 52 53 0]^2
nt 50 52 53 0 50 52 53 0


// another way to stack pitches (chords) when a group starts with {-[}
nt -[50 54 57] //equivalent to nt 50-54-57

// parallel sequence with {,} inside bracket group
// similar to branches, where {[} behaves like {brs}, {,} behaves like {brr}
// and {]} behaves like {bre}
dr 1/2 nt [50*4,0 57*3, 0^2 60*2]
            """.trimIndent()

    ),

    DEMO1("Demo: Demo 1",
            """
sc 50-52-53-55-57-5b	// harmonic minor scale

// exclamation mark = negative scale index

brs
dr 1/8 1/16 1/16
nt 50 	52 50	!52   50 !52  !54	!52 !54 !56
dr 1/8 nt 0	//0 means silent note
brr
in 1 dr 1/1 nt 50-52-54
bre

brs
dr 1/16
nt !42 40 42 40 41 40 41 40
nt !41 41 43 41
dr 1/8
nt 42 40
brr
in 1 dr 1/4 ve 90 70 nt !52-50-52 !52-50-52 !52-50-52 !52-50-52
bre


brs.seg1=
in 0
dr 1/16 1/16 1/8
nt 50 51 52  51 52 53
dr 1/32-s1/32		//repeat notes, but drop/silence the second
			// creates staccato effect
nt 54 54 53 51 52 51
dr 1/8
nt 50
bre

dr 1/1 ss 0 0 -2 1	//setting up a scale shift cycle
brs.seg2=
	in 1 dr 1/4 ve 80 75
	dr 1/4-1/4-1/4-1/4
 	nt 50-52-54
brr
	brs.seg1
bre

brs.seg2 brs.seg2 brs.seg2 brs.seg2
        """.trimIndent()
    ),

    DEMO2("Demo: Demo 2",
            """sc 50-52-53-55-57-58-5a


brs.var1=
dr 3/64 3/64 1/32
in 2 dr 3/64-3/64-s1/32 nt 50 51 53 52 dr 1/4 nt 50
dr 1/8 ve 80
nt 4a 51 50 4b
dr 1/4
nt 50

dr 1/4 nt 0 50

brr in 1 dr 3/4-5/4
nt 40-42-44

brr
in 3
dr 1/16 ve 80 50 70 75
dr 1/16-1/16-1/16-1/16 nt 60 61 62 63	60 62 64 66
bre

brs.var1
ss 4
brs.var1
ss 3
brs.var1
ss 4
brs.var1

dr 15/8 1/8 nt 0 50"""

    ),

    DEMO3("Demo: Demo 3",
            """brs
in 2   dr 2/1   nt 40 35 37 33   nt 40-47 35-40 37-40 33-37
brr
in 1   dr 1/8
nt 50 57 60 57   nt 50 57 63 57   nt 50 57 60 57   nt 50 57 67 57
nt 50 57 60 57   nt 50 57 63 57   nt 50 57 60 57   nt 50 57 67 57
nt 50 57 60 57   nt 50 57 63 57   nt 50 57 60 57   nt 50 57 67 57
nt 50 57 60 57   nt 50 57 63 57   nt 50 57 60 57   nt 50 57 67 57

dr 3/16 1/16
nt 50 57 60 57   nt 50 57 63 57   nt 50 57 60 57   nt 50 57 67 57
nt 50 57 60 57   nt 50 57 63 57   nt 50 57 60 57   nt 50 57 67 57
nt 50 57 60 57   nt 50 57 63 57   nt 50 57 60 57   nt 50 57 67-60 57
nt 50 57 60 57   nt 50 57 63 57   nt 50 57 60 57   nt 50 57 67 57

brr
in 0   dr 1/4 1/4 1/2   nt 60 50 67   nt 67 65 63   nt 63 62 60   dr 1/8   nt 50 52 53 55 57 58 5a 60

dr 1/4 1/4 1/2   nt 60 50 67   nt 67 65 63   nt 63 62 60   dr 1/8   nt 50 52 53 55 57 58 5a 60
dr 1/4 1/4 1/2   nt 60 50 67   nt 67 65 63   nt 63 62 60   dr 1/8   nt 50 52 53 55 57 58 5a 60
dr 1/4 1/4 1/2   nt 60 50 67   nt 67 65 63   nt 63 62 60   dr 1/8   nt 50 52 53 55 57 58 5a 60

  bre

brs
in 2
dr 6/8 2/8   nt 40 3a 40 47   nt 45 3a 45 4a
nt 40-57 3a-53 40 47    nt 45 3a 45 4a
brr
in 1
dr 5/32 3/32
nt 50 50 40 4a   nt 50 50-47 40 4a   nt 50 50 40 4a   nt 50 50-45 40 4a
nt 50 50 40 4a   nt 50 50-47 40 4a   nt 50 50 40 4a   nt 50 50-45 40 4a
nt 50 50 40 4a   nt 50 50-47 40 4a   nt 50 50 40 4a   nt 50 50-45 40 4a
nt 50 50 40 4a   nt 50 50-47 40 4a   nt 50 50 40 4a   nt 50 50-45 40 4a
bre

brs
in 0
dr 1/4 1/2 1/4   nt 60 5a 55 60   dr 1/16   nt 40 00 50 00 52 00 53 00
dr 1/1   nt 30   dr 1/4 3/4   nt 4a 40
dr 1/4 1/2 1/4   nt 60 5a 55 60   dr 1/16   nt 40 00 50 00 52 00 53 00
dr 1/1   nt 30   dr 1/4 3/4   nt 4a 40
brr
in 1
dr 1/8   nt 60 50 57 50   nt 60 50 60-55 50   nt 60 50 60 50   nt 60 50 60 50
nt 60 50 57 50   nt 60 50 60-55 50   nt 60 50 60 50   nt 60 50 60 50
nt 60 50 57 50   nt 60 50 60-55 50   nt 60 50 60 50   nt 60 50 60 50
nt 60 50 57 50   nt 60 50 60-55 50   nt 60 50 60 50   nt 60 50 60 50
brr
in 2  dr 2/1
nt 40 00 40-57 00
bre

brs
in 3
dr 1/4 1/4 1/2 1/8 1/8 1/4 2/8
nt 50 57 55 53 52 50 60
dr 1/16   nt 62 63 62 60   dr 1/3   nt 50 4a 50
in 3
dr 1/4 1/4 1/2 1/8 1/8 1/4 2/8
nt 50 57 55 53 52 50 60
dr 1/16   nt 62 63 62 60   dr 1/3   nt 50 4a 50
bre

"""),
    DEMO4(
            "Demo: Demo 4",
    """brs.mel1=
in 1 dr 1/6 5/6 ve 80 60
dr 1/12-s1/12 1/6 nt 50 50 50 50 40*2
nt 53 55 53 51 50*2
nt 4a 4a 4a 4a 47*2
nt 4a 50 51 50 dr 1/12 nt 3a^4
bre

brs.mel1_2=
dr 1/6 3/6 2/6 in 1 1 3 ve 80 60 70
dr 1/6 s1/12-1/12 nt 50 50 50 50 60*2
nt 53 55 53 51 60*2
nt 4a 4a 4a 4a 57*2
nt 4a 50 51 50 dr 1/12 in 3 nt 3a 3a 4a 5a

bre


brs.backing1=
dr 1/3 2/3 ve 10 60  dr -[1/3 2/3 1/3 2/3 ]in 2 nt 50-57 4a-55

bre

brs.arp1=
dr 1/24-s3/24 nt [50 58 57 51 57 58]^2
dr 1/24-s3/24 nt [4a 55 51 4a 55 51]^2
bre

brs brs.mel1 brs.mel1_2 brr brs.backing1^2 brr brs.arp1^2 bre
"""
    ),

    DEMO5(
            "Demo: Demo 5",
            """
sc [50 52 53 55 57 58 5a]

dr 3/12 1/12 1/12 1/12
brs.theme1=
ve 90 in 0
dr 3/12 1/12 1/12 1/12
brs nt 50 53 52 50 brr in 2 nt 40*4 bre
brs nt  0 0 53 50  brr in 2 nt 36*4 bre
brs nt  54 52 54 50 brr in 2 nt 35*4 bre
brs nt  54 0 52 0 brr in 2 nt 35*4 bre

brs nt 50 53 52 50 brr in 2 nt 34*4 bre
brs nt  0 0 53 50  brr in 2 nt 34*4 bre
brs nt  54 52 54 50 brr in 2 nt 30*4 bre
brs nt  54 0 !52 !51 brr in 2 nt 30*4 bre
bre

brs.theme2=
dr 1/12 ve 80 70 60 80 70 80 in 1 1 3 1 3 1
dr -[1/12^3] 1/24-s1/24 1/12 s1/24-1/24

brs nt 50 53 52 50 bre
brs nt  0 0 53 50   bre
brs nt  54 52 54 50  bre
brs nt  54 0 52 0  bre

brs nt 50 53 52 50  bre
brs nt  0 0 53 50   bre
brs nt  54 52 54 50  bre
brs nt  54 0 !52 !51 bre
bre


brs.backing1=
in 2 dr 1/2 ve 75
nt 40 !41 !42 !42
nt !43 !43 !44 !44
brr
in 2 dr 2/6-s1/6 ve 55
nt 44 42 40 40
nt 42 42 43 41
bre


brs.chopped1=
dr 6/12 0 ve -[30 50]
in 5 dr -[1/12^6]
//in 5 dr 1/12-1/12-1/12-1/12-1/12
nt 60 !61 !62 !62
nt !63 !63 !64 !64

bre


dr 4/1
tp 0 0 3 -2


brs.chopped1
brs.wrapit=
brs.theme1 brs.theme2
brr
brs.backing1^2
brr
brs.chopped1^2
bre

brs.wrapit^4
"""
    ),


    DEMO6("Demo: Demo 6",
            """   //minor scale
sc 50-52-53-55-57-58-5a

   /*starting a branch and
   saving it to a variable*/
brs.part1=
       //instrument channel 0
   in 0

      // dr sets duration, nt creates a note
      // first digit = octave, second digit = scale index
   dr 1/4 nt 50 51 52 51 dr 1/1 nt 50
   dr 1/8-1/8 nt 50 51 53 54 dr 1/1 nt 50

brr      //branch reset (start on next branch)
   in 2

      //dashes lets you stack notes into a chord
   dr 4/1 nt 40-42-44
brr
   in 1
      // setting up a velocity cycle. each 1/16 time
      // division will have a different velocity
   dr 1/16 ve 40 60 70 30

      // dashes lets you make a note repeat
      // here, one note is played four times
   dr 1/16-1/16-1/16-1/16

      // all commands can be saved to a variable
      // when a command is saved, it is not inserted
      // into the song, just defined
   nt.pat= 60 61 62 63  64 65 66 65

      // and here the variable is loaded
      // and inserted into the song
   nt.pat
   nt.pat
bre      // end of branch here. will now continue
      // with the state of the FIRST branch as it
      // finished

brs.section=   //defining a new branch, referencing the other one

      // scale shift. shifts all notes up or down the scale
   ss 0
   brs.part1
   ss 1
   brs.part1
   ss -2
   brs.part1
   ss 0
   brs.part1
bre

      // now we play the section
brs.section
      // change scale to major
sc 50-52-54-55-57-59-5b
      // play section again in major
brs.section"""



    ),

    DEMO7("Demo: Demo 7","""
sc.minor= [50 52 53 55 57 58 5a]
sc.major= [50 52 54 55 57 59 5b]



sc.minor
brs.sec1=
dr 1/16-1/16
in 3
nt [50 52 51 50 !53 !51 50*2]^2
dr 1/8
nt [50 52 51 50 !51 !53 !55*2]^2
brr
dr 1/1 in 2 ve 50 nt -[40 44 50]^3 -[44 50 54]
brr
in 0 dr 1/16 ve 0 40 90 85 dr 1/16 nt [30 30 30 30 30 30 40 30]^8
bre


brs.intro= ss 0 brs.sec1 ss 3 brs.sec1 bre

brs.sec1= dr 1/1 ss 0 0 0 2 0 0 0 3 brs.sec1^2 bre

brs.intro
brs.sec1
sc.major
brs.sec1

    """.trimIndent()),


    TUTORIAL_1_NOTE(
        "Tutorial 1 : Note Command", """/*
    The note command "nt ... "
    takes a two digit base12 number to specify a pitch.
    whitespace separated numbers are played in sequence
     a note command continues off from where the previous one ended

    */


    nt 40 40 40 40 // Play a note at octave 4, semitone 0, four times.
    nt 0 0 0 0  // Notes with a "0" instead of numbers will be a silent note

    nt 40 50 40 50  //Play 4 notes at alternate octave
    nt 0 0 0 0

    // chromatic scale. Note that the number input is base12, not simply base10
    nt 40 41 42 43 44 45 46 47 48 49 4a 4b
    nt 50 50 50 50

    // note parameters may be stacked, by connecting them with a dash: -
    // this causes the pitches to be played simultaneously, and allows for chords

    nt	40-47-50
        40-45-50
        50-57-60
        50-57-60


    """
    ),
    TUTORIAL_2_DURATION(
        "Tutorial 2 : Duration Command", """/*
    Duration command, "dr ......."
    takes fractions as input, in base10
    determine how long subsequent note commands will play
     a fraction of 1/1 represents one measure
    only the last seen duration command are valid.
    all those that comes before are forgotten/replaced
    */

    dr 2/4 nt 50 55 57 55 	// play notes with duration of 1/2 measure
    dr 1/1 nt 0			// insert a pause lasting one measure
    dr 1/4 nt 50 55 57 55 	// replace previous duration. play twice as fast
    dr 1/1 nt 0

    /*
    duration commands are cyclical. for each next note, it cycles
    through the white space separated fractions in the previous duration command
    */

    dr 1/4 3/4 nt 50 50 50 50 nt 50 60 50 50 //plays each alternating note with a different duration
    dr 1/1 nt 0

    dr 1/8 1/3 1/2 nt 50 55 60 50 55 5a 50 6a 40 40 40 //duration does not care about separation into note commands
    dr 1/1 nt 0

    // duration commands support stacked fractions
    // it will then take a single note and play it multiple times,
    // each time using the next fractions within the group to define the note's duration
    dr 1/4-3/4-1/1 nt 50 60 //only two notes for input, but will play 6 notes

    """
    ),
    TUTORIAL_3_BRANCH(
        "Tutorial 3 : Branch Commands", """/*
    Branching command(s), "brs ... brr ... bre"
    The branching command(s) wraps around sequences of other commands.
    It forces "brr" separated sequences to start at the same point in time
    thus playing the sequences in parallel
    */

    dr 1/1			//set duration
    brs
        nt 40
    brr
        nt 47
    bre				//plays two notes simultaneously
    nt 0

    brs 							//play three note sections in parallel
            dr 3/4 1/4		nt 47 40
        brr	dr 1/4		nt 50 50 57 50
        brr	dr 1/1		nt 70
    bre

    nt 0

    /*
    Only the first command sequence within the branching command
    determines when notes after the branching section is finished,
    */
    brs dr 4/1 nt 40 brr dr 1/4 nt 50 57 60 5a dr 2/4 nt 50 bre //continues after 4 measures


    /*
    parallel commands create a scope for all commands being entered
    within them, that is, the original duration etc that was
    set prior to the parallel commmand, are put back in place
    once the parallel command is finished (reached ")" )
    */


    // Parallel commands can be nested
    brs
        brs dr 1/4 nt 40 50 4b 49 brr dr 2/4 nt 30 nt 30 bre
        brr dr 1/8-1/8 nt 60 70 nt 60 60
    bre
    nt 0
    nt 30


    """
    ),
    TUTORIAL_4_VELOCITY(
        "Tutorial 4 : Velocity Command", """/*
    Velocity command, "ve  ... "
    sets the loudness of subsequent notes
    takes a number between 0 and 100. (33.34, 75.550 is also allowed)

    Velocity command is cyclic, and its cycle is determined by the preceding duration command
    */

    dr 3/4 1/4 //make the next silent notes create a staccato effect
    ve 100	nt 50 0
    ve 50	nt 50 0
    ve 10	nt 50 0


    //cycle
    dr 1/4 ve 10 40 70 100 // define rising velocity throughout one measure
    nt 40 40 40 40 30 30 30 30

    /* still affected by the same velocity command, independently
    of the new duration/rhythm */
    dr 1/8-1/8 nt 40 45 4a 4b nt 40 45 dr 2/4 nt 50
    dr 1/8-1/8 nt 40 45 4a 4b nt 40 45 dr 1/4 nt 50 50
    dr 1/8-1/8 nt 40 45 4a 4b nt 40 45 dr 2/4 nt 50
    dr 1/8-1/8 nt 40 45 4a 4b nt 40 45 dr 1/4 nt 50-57 57-60
    ve 100 //back to velocity command with no cycling (constantly equal to 100)
    dr 2/1 nt 50-57-60

    """
    ),
    TUTORIAL_5_INSTRUMENT(
        "Tutorial 5 : Instrument Command", """/*
    Instrument command, "in ... "
    takes integers like 0, 4, 2, 15
    this command selects which instrument will be used to play the next note.
    Its parameter are one/two digit decimal numbers separated by whitespace
    When exporting to MIDI, the instrument number corresponds to channel.

    the instrument command is cyclic, if it was set with multiple values
    (like the velocity command)
    */

    dr 1/1 //define duration

    in 0	nt 50 // i(0) is the default instrument
    in 1	nt 50
    in 2 	nt 50
    in 3 	nt 50

    nt 0 //pause

    /*
    There is a limited number of instruments defined for the audio export
    so large numbers inside the instrument command will be wrapped around
    and one of the defined instruments are used instead (modulo)
    Likewise, numbers over 15 are wrapped around when exporting to MIDI.
    (only 16 different channels)
    */

    in 42 nt 50 //will use one of the instruments already heard if exporting audio
    in 43 nt 50
    in 44 nt 50
    in 45 nt 50


    /*
    Instrument is a cyclical command.
    below, plays with a different instrument for the 3 last 1/8 notes,
    and a little louder. Same concept for both instrument choice and velocity
    */

    dr 5/8 3/8 ve 60 100 in 0 3
    dr 1/8-1/8 nt 40 40 45 50 nt 51 4a 47 42

    """
    )

}
