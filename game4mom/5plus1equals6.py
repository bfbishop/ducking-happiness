#!/usr/bin/env python

def minusOneLetter(word):
    for i in xrange(len(word)):
        yield word[:i] + word[i+1:], word[i]

sixLetters = open('hardWords6','r')
fiveLetters = open('allWords5','r')

fiveLettersMap = {}

for line in fiveLetters:
    wordSorted = ''.join(sorted(line[:-1]))
    if wordSorted in fiveLettersMap:
        fiveLettersMap[wordSorted].append(line[:-1])
    else:
        fiveLettersMap[wordSorted] = [line[:-1]]

for line in sixLetters:
    for word, letter in minusOneLetter(line[:-1]):
        wordSorted = ''.join(sorted(word))
        if wordSorted in fiveLettersMap:
            for i in fiveLettersMap[wordSorted]:
                if not i in line[:-1]:
                    print line[:-1] + " = " + letter + " + " + i
