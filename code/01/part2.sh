#!/bin/bash
awk '{ print $1 $1 }' $1 | sed -E -e 's/([1-9]|one|two|three|four|five|six|seven|eight|nine).*([1-9]|one|two|three|four|five|six|seven|eight|nine).*/ \1\2/;s/.* //;s/one/1/g;s/two/2/g;s/three/3/g;s/four/4/g;s/five/5/g;s/six/6/g;s/seven/7/g;s/eight/8/g;s/nine/9/g' | paste -s -d+ | bc
