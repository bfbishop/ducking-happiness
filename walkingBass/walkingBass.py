#!/usr/bin/env python
import random


notes = ['A','#A','B','C','#C','D','#D','E','F','#F','G','#G']

intervalScore = [1,2,9,8,4,3,1,7,2,1]

strings = ['E','A','D','G']

chords = { 'M'  : [0,4,7], 
           'm'  : [0,3,7],
           'M7' : [0,4,7,11],
           'm7' : [0,3,7,10],
           '7'  : [0,4,7,10],
           '+'  : [0,4,8],
           '+7' : [0,4,8,10],
           'M9' : [0,2,4,7],
           'm9' : [0,2,3,7]
         }

chordList = ['CM7','#A7','#DM7','#G7',
             '#CM7','G7','CM7','#A7',
             'Em9','#G7','#CM7','G7'
            ]

def chordNotes(chord):
    root = -1;
    for i in xrange(len(notes)):
        if chord.startswith(notes[i]):
            root = i
            chord = chord[len(notes[i]):]
    return map(lambda x : (x + root) % 12, chords[chord])

chordList.append(chordList[0])

numberChords = map(chordNotes, chordList)
print numberChords
beatOneRand = map(random.choice, numberChords)
print beatOneRand
