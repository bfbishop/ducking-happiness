#! /usr/bin/env python

allWords6 = open('allWords6','r')
simpleWords6 = open('simpleWords6','r')
hardWords6 = open('hardWords6','wr')

wordSet = set([])

for i in allWords6:
    wordSet.add(i)


for i in simpleWords6:
    if i in wordSet:
        wordSet.remove(i)


for i in sorted(wordSet):
    hardWords6.write(i)
